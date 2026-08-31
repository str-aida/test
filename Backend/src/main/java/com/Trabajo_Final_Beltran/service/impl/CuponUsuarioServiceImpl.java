package com.Trabajo_Final_Beltran.service.impl;

import com.Trabajo_Final_Beltran.dto.response.CuponUsuarioResponse;
import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.CuponUsuario;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.event.CuponAsignadoEvent;
import com.Trabajo_Final_Beltran.mapper.CuponUsuarioMapper;
import com.Trabajo_Final_Beltran.repository.CuponUsuarioRepository;
import com.Trabajo_Final_Beltran.service.CuponUsuarioService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CuponUsuarioServiceImpl implements CuponUsuarioService {

    private final CuponUsuarioRepository cuponUsuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void asignarCupon(Usuario usuario, Cupon cupon) {
       
    if (cupon == null || cupon.getId() == null) {
        return; // cupón inválido/no persistido, se ignora
    }
        
        boolean yaAsignado = cuponUsuarioRepository
                .existsByUsuarioIdAndCuponId(usuario.getId(), cupon.getId());
        if (yaAsignado) {
            return;
        }

        CuponUsuario cuponUsuario = CuponUsuario.builder()
                .usuario(usuario)
                .cupon(cupon)
                .usado(false)
                .build();
        cuponUsuarioRepository.save(cuponUsuario);

        usuario.getEmail();
        eventPublisher.publishEvent(new CuponAsignadoEvent(this, usuario, cupon));
    }
    
    @Override
    public List<CuponUsuarioResponse> obtenerCuponesActivos(Long usuarioId) {
        return cuponUsuarioRepository.findAllByUsuarioIdAndUsado(usuarioId, false)
                .stream()
                .map(CuponUsuarioMapper::toResponse)
                .toList();
    }
}