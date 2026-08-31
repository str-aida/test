package com.Trabajo_Final_Beltran.service.strategy.cupon;

import com.Trabajo_Final_Beltran.entity.Cupon;
import com.Trabajo_Final_Beltran.entity.Usuario;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ManualStrategy implements AsignacionCuponStrategy {

    @Override
    public List<Cupon> obtenerCupones(Usuario usuario) {
        // Esta estrategia NUNCA asigna nada automáticamente.
        // La asignación manual la hace el admin directamente
        // por endpoint (AsignarCuponRequest), no por este flujo
        // automático de "revisar todas las estrategias".
        return Collections.emptyList();
    }
}