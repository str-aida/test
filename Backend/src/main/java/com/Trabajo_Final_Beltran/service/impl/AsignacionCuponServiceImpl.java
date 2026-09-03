package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.request.AsignarCuponRequest;
import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.exception.BusinessException;
import com.Trabajo_Final_Beltran.repository.CuponRepository;
import com.Trabajo_Final_Beltran.repository.UsuarioRepository;
import com.Trabajo_Final_Beltran.service.AsignacionCuponService;
import com.Trabajo_Final_Beltran.service.CuponUsuarioService;
import com.Trabajo_Final_Beltran.service.strategy.cupon.AsignacionCuponStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsignacionCuponServiceImpl implements AsignacionCuponService {

    private final List<AsignacionCuponStrategy> strategies;
    private final CuponUsuarioService cuponUsuarioService;
    private final UsuarioRepository usuarioRepository;
    private final CuponRepository cuponRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void asignarCupones(Usuario usuario) {
        for (AsignacionCuponStrategy strategy : strategies) {
            for (Cupon cupon : strategy.obtenerCupones(usuario)) {
                try {
                    cuponUsuarioService.asignarCupon(usuario, cupon);
                } catch (BusinessException e) {
                    // Si ya lo tiene disponible o el cupón no es asignable, se omite silenciosamente en procesos automáticos
                }
            }
        }
    }

    @Override
    @Transactional
    public void asignarCuponManual(AsignarCuponRequest request) {
        Usuario usuario = resolverUsuario(request);

        Cupon cupon = cuponRepository.findById(request.getCuponId())
                .orElseThrow(() -> new BusinessException("Cupón no encontrado"));

        cuponUsuarioService.asignarCupon(usuario, cupon);
    }

    private Usuario resolverUsuario(AsignarCuponRequest request) {
        boolean porId = request.getUsuarioId() != null;
        boolean porEmail = request.getEmail() != null && !request.getEmail().isBlank();
        boolean porNombre = request.getNombreCompleto() != null && !request.getNombreCompleto().isBlank();

        int cantidad = (porId ? 1 : 0) + (porEmail ? 1 : 0) + (porNombre ? 1 : 0);
        if (cantidad != 1) {
            throw new BusinessException(
                    "Debés indicar exactamente uno: usuarioId, email o nombreCompleto"
            );
        }

        if (porId) {
            return usuarioRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        }

        if (porEmail) {
            return usuarioRepository.findByEmailIgnoreCase(request.getEmail().trim())
                    .orElseThrow(() -> new BusinessException("No existe un usuario con ese email"));
        }

        List<Usuario> usuarios = usuarioRepository
                .findByNombreCompletoIgnoreCase(normalizarNombre(request.getNombreCompleto()));

        if (usuarios.isEmpty()) {
            throw new BusinessException("No existe un usuario con ese nombre y apellido");
        }
        if (usuarios.size() > 1) {
            throw new BusinessException(
                    "Hay varios usuarios con ese nombre y apellido. Identificalo con el email"
            );
        }
        return usuarios.get(0);
    }

    private String normalizarNombre(String texto) {
        return texto.trim().replaceAll("\\s+", " ");
    }
}