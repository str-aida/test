package com.Trabajo_Final_Beltran.security;

import com.Trabajo_Final_Beltran.entity.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

  public static Usuario obtenerUsuarioAutenticado() {

    Authentication authentication =
        SecurityContextHolder
            .getContext()
            .getAuthentication();

    return (Usuario)
        authentication.getPrincipal();
  }

}
