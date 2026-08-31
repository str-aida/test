
package com.Trabajo_Final_Beltran.security;

import com.Trabajo_Final_Beltran.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.Trabajo_Final_Beltran.entity.Usuario;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException {

      log.debug("Buscando usuario: {}", email);

    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() ->
                    new UsernameNotFoundException(
                            "Usuario no encontrado"
                    )
            );

      log.debug("Usuario encontrado: {}", usuario.getEmail());

    return usuario;
}
    
}
