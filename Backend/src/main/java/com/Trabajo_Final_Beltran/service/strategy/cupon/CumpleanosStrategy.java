package com.Trabajo_Final_Beltran.service.strategy.cupon;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Usuario;
import com.Trabajo_Final_Beltran.enums.EstadoCupon;
import com.Trabajo_Final_Beltran.enums.TipoAsignacionCupon;
import com.Trabajo_Final_Beltran.repository.CuponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CumpleanosStrategy implements AsignacionCuponStrategy {

    private final CuponRepository cuponRepository;

    @Override
    public List<Cupon> obtenerCupones(Usuario usuario) {

        if (usuario.getFechaNacimiento() == null) {
            return Collections.emptyList();
        }

        LocalDate hoy = LocalDate.now();

        if (
                usuario.getFechaNacimiento().getDayOfMonth() == hoy.getDayOfMonth()
                &&
                usuario.getFechaNacimiento().getMonth() == hoy.getMonth()
        ) {

           return cuponRepository.findByTipoAsignacionAndEstado(
            TipoAsignacionCupon.CUMPLEANOS,
            EstadoCupon.ACTIVO
);
        }

        return Collections.emptyList();
    }
}