package com.Trabajo_Final_Beltran.scheduler;

import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.Estado;
import com.Trabajo_Final_Beltran.repository.UsuarioRepository;
import com.Trabajo_Final_Beltran.service.AsignacionCuponService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CumpleanosScheduler {

    private final UsuarioRepository usuarioRepository;
    private final AsignacionCuponService asignacionCuponService;

    // Corre todos los días a las 6:00 AM
    @Scheduled(cron = "0 0 6 * * *")
    public void asignarCuponesCumpleanos() {

        List<Usuario> usuarios = usuarioRepository
                .findByEstado(Estado.ACTIVO);

        for (Usuario usuario : usuarios) {
            asignacionCuponService.asignarCupones(usuario);
        }

        System.out.println(
            "CumpleanosScheduler ejecutado - usuarios revisados: "
            + usuarios.size()
        );
    }
}