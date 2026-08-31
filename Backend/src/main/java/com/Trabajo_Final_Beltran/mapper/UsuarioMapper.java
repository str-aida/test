package com.Trabajo_Final_Beltran.mapper;

import com.Trabajo_Final_Beltran.dto.request.RegisterRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdatePerfilRequest;
import com.Trabajo_Final_Beltran.dto.request.UpdateUsuarioRequest;
import com.Trabajo_Final_Beltran.dto.response.DireccionResponse;
import com.Trabajo_Final_Beltran.dto.response.UsuarioPerfilResponse;
import com.Trabajo_Final_Beltran.entity.Direccion;
import com.Trabajo_Final_Beltran.entity.Usuario;
import lombok.Builder;
import com.Trabajo_Final_Beltran.enums.Estado;

@Builder
public class UsuarioMapper {

    public static Usuario toEntity(RegisterRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setDni(request.getDni());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        return usuario;
    }

    public static UsuarioPerfilResponse toPerfilResponse(
            Usuario usuario,
            Direccion direccion
    ) {
        return UsuarioPerfilResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .dni(usuario.getDni())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .rol(usuario.getRol())
                .estado(usuario.getEstado())
                .direccion(toDireccionResponse(direccion))
                .build();
    }

    public static void updateUsuarioFromRequest(
            Usuario usuario,
            UpdatePerfilRequest request
    ) {
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
    }

    public static void updateUsuarioAdminFromRequest(
            Usuario usuario,
            UpdateUsuarioRequest request
    ) {
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setEmail(request.getEmail());
        usuario.setEstado(request.getEstado());
    }

    private static DireccionResponse toDireccionResponse(Direccion direccion) {
        if (direccion == null) {
            return null;
        }
        return DireccionResponse.builder()
                .id(direccion.getId())
                .nombre(direccion.getNombre())
                .calle(direccion.getCalle())
                .numero(direccion.getNumero())
                .localidad(direccion.getLocalidad())
                .piso(direccion.getPiso())
                .departamento(direccion.getDepartamento())
                .codigoPostal(direccion.getCodigoPostal())
                .referencia(direccion.getReferencia())
                .esPrincipal(direccion.getEsPrincipal())
                .build();
    }
}