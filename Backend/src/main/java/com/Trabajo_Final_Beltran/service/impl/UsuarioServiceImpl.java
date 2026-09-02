package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.request.UpdateUsuarioRequest;
import com.Trabajo_Final_Beltran.dto.response.UsuarioPerfilResponse;
import com.Trabajo_Final_Beltran.entity.Direccion;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.Estado;
import com.Trabajo_Final_Beltran.enums.Rol;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.UsuarioMapper;
import com.Trabajo_Final_Beltran.repository.DireccionRepository;
import com.Trabajo_Final_Beltran.repository.UsuarioRepository;
import com.Trabajo_Final_Beltran.security.SecurityUtils;
import com.Trabajo_Final_Beltran.service.LogSistemaService;
import com.Trabajo_Final_Beltran.service.UsuarioService;
import com.Trabajo_Final_Beltran.specification.UsuarioSpecification;
import com.Trabajo_Final_Beltran.util.TextNormalizerUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final DireccionRepository direccionRepository;

    private final LogSistemaService logSistemaService;

    @Override
    @Transactional
    public UsuarioPerfilResponse editarUsuario(
            Long id,
            UpdateUsuarioRequest request
    ){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

            request.setNombre(TextNormalizerUtil.normalizarTexto(request.getNombre()));
            request.setApellido(TextNormalizerUtil.normalizarTexto(request.getApellido()));
            request.setEmail(request.getEmail().trim().toLowerCase());

        
        if (usuario.getId().equals(SecurityUtils.obtenerUsuarioAutenticado().getId())
                && request.getEstado() == Estado.INACTIVO) {
            throw new BusinessException("No podés desactivar tu propia cuenta");
        }

      String nombreAnterior = usuario.getNombre();
      String apellidoAnterior = usuario.getApellido();
      String telefonoAnterior = usuario.getTelefono();
      String emailAnterior = usuario.getEmail();
      String estadoAnterior = usuario.getEstado().name();

        UsuarioMapper.updateUsuarioAdminFromRequest(usuario, request);
        usuarioRepository.save(usuario);

      logSistemaService.registrarAuditoria(
          "USUARIO",
          usuario.getId(),
          usuario.getNombre() + " " + usuario.getApellido(),
          "nombre",
          nombreAnterior,
          usuario.getNombre(),
          "Se modificó el nombre del usuario"
      );

      logSistemaService.registrarAuditoria(
          "USUARIO",
          usuario.getId(),
          usuario.getNombre() + " " + usuario.getApellido(),
          "apellido",
          apellidoAnterior,
          usuario.getApellido(),
          "Se modificó el apellido del usuario"
      );

      logSistemaService.registrarAuditoria(
          "USUARIO",
          usuario.getId(),
          usuario.getNombre() + " " + usuario.getApellido(),
          "telefono",
          telefonoAnterior,
          usuario.getTelefono(),
          "Se modificó el teléfono del usuario"
      );

      logSistemaService.registrarAuditoria(
          "USUARIO",
          usuario.getId(),
          usuario.getNombre() + " " + usuario.getApellido(),
          "email",
          emailAnterior,
          usuario.getEmail(),
          "Se modificó el email del usuario"
      );

      logSistemaService.registrarAuditoria(
          "USUARIO",
          usuario.getId(),
          usuario.getNombre() + " " + usuario.getApellido(),
          "estado",
          estadoAnterior,
          usuario.getEstado().name(),
          "Se modificó el estado del usuario"
      );

        Direccion direccion = direccionRepository
                .findByUsuarioIdAndEsPrincipalTrue(usuario.getId())
                .orElse(null);

        return UsuarioMapper.toPerfilResponse(usuario, direccion);
    }

    @Override
    public List<UsuarioPerfilResponse> listarUsuarios(Rol rol, String texto) {

        Specification<Usuario> spec = (rol != null)
                ? UsuarioSpecification.conRol(rol)
                : UsuarioSpecification.conRolAdminOEmpleado();

        if (texto != null && !texto.isBlank()) {
            spec = spec.and(UsuarioSpecification.conTexto(texto));
        }

        return usuarioRepository.findAll(spec)
                .stream()
                .map(usuario -> {
                    Direccion direccion = direccionRepository
                            .findByUsuarioIdAndEsPrincipalTrue(usuario.getId())
                            .orElse(null);
                    return UsuarioMapper.toPerfilResponse(usuario, direccion);
                })
                .toList();
    }
    
    @Override
    public UsuarioPerfilResponse obtenerUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        Direccion direccion = direccionRepository
                .findByUsuarioIdAndEsPrincipalTrue(usuario.getId())
                .orElse(null);

        return UsuarioMapper.toPerfilResponse(usuario, direccion);
    }
    
    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException("Usuario no encontrado")
                );

        if (usuario.getRol() != Rol.EMPLEADO) {
            throw new BusinessException(
                "Solo se pueden eliminar empleados"
            );
        }

        if (usuario.getEstado() == Estado.INACTIVO) {
            throw new BusinessException(
                "El usuario ya está inactivo"
            );
        }

        usuario.setEstado(Estado.INACTIVO);
        usuarioRepository.save(usuario);

      logSistemaService.registrarAuditoria(
          "USUARIO",
          usuario.getId(),
          usuario.getNombre() + " " + usuario.getApellido(),
          "estado",
          Estado.ACTIVO.name(),
          usuario.getEstado().name(),
          "Usuario desactivado"
      );
    }
}