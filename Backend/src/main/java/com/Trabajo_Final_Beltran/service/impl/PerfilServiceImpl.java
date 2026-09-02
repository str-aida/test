package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.request.SolicitarRecuperacionRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdatePasswordRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdatePerfilRequest;
import com.Trabajo_Final_Beltran.dto.response.UsuarioPerfilResponse;
import com.Trabajo_Final_Beltran.entity.Direccion;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.mapper.UsuarioMapper;
import com.Trabajo_Final_Beltran.repository.DireccionRepository;
import com.Trabajo_Final_Beltran.repository.UsuarioRepository;
import com.Trabajo_Final_Beltran.security.SecurityUtils;
import com.Trabajo_Final_Beltran.service.AuthService;
import com.Trabajo_Final_Beltran.service.PerfilService;
import com.Trabajo_Final_Beltran.util.NumeroUtils;
import com.Trabajo_Final_Beltran.util.TextNormalizerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class PerfilServiceImpl implements PerfilService {

    private final UsuarioRepository usuarioRepository;
    private final DireccionRepository direccionRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void solicitarCambioPassword() {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
        SolicitarRecuperacionRequest request = SolicitarRecuperacionRequest.builder()
                .email(usuario.getEmail())
                .build();
        authService.solicitarRecuperacionPassword(request);
    }

    @Override
    public UsuarioPerfilResponse obtenerPerfil() {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();
        Direccion direccion = obtenerDireccionPrincipal(usuario.getId());
        return UsuarioMapper.toPerfilResponse(usuario, direccion);
    }

    @Override
    @Transactional
    public UsuarioPerfilResponse actualizarPerfil(UpdatePerfilRequest request) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();

        request.setTelefono(NumeroUtils.limpiarNumero(request.getTelefono())); 
        request.setNombre(TextNormalizerUtil.normalizarTexto(request.getNombre()));
        request.setApellido(TextNormalizerUtil.normalizarTexto(request.getApellido()));

        
        UsuarioMapper.updateUsuarioFromRequest(usuario, request);
        usuarioRepository.save(usuario);

        Direccion direccion = actualizarDireccionSiViene(usuario, request);

        return UsuarioMapper.toPerfilResponse(usuario, direccion);
    }

    private Direccion obtenerDireccionPrincipal(Long usuarioId) {
        return direccionRepository
                .findByUsuarioIdAndEsPrincipalTrue(usuarioId)
                .orElse(null);
    }

    private Direccion actualizarDireccionSiViene(
            Usuario usuario,
            UpdatePerfilRequest request
    ) {
        if (request.getDireccion() == null) {
            return obtenerDireccionPrincipal(usuario.getId());
        }

        Direccion direccion = direccionRepository
                .findByUsuarioIdAndEsPrincipalTrue(usuario.getId())
                .orElseGet(() -> Direccion.builder()
                        .usuario(usuario)
                        .esPrincipal(true)
                        .fechaCreacion(LocalDateTime.now())
                        .build()
                );

        direccion.setNombre(request.getDireccion().getNombre());
        direccion.setCalle(request.getDireccion().getCalle());
        direccion.setNumero(request.getDireccion().getNumero());
        direccion.setLocalidad(request.getDireccion().getLocalidad());
        direccion.setPiso(request.getDireccion().getPiso());
        direccion.setDepartamento(request.getDireccion().getDepartamento());
        direccion.setCodigoPostal(request.getDireccion().getCodigoPostal());
        direccion.setReferencia(request.getDireccion().getReferencia());

        return direccionRepository.save(direccion);
    }
    
    @Override
    @Transactional
    public void cambiarPassword(UpdatePasswordRequest request) {
        Usuario usuario = SecurityUtils.obtenerUsuarioAutenticado();

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }
    
    
}