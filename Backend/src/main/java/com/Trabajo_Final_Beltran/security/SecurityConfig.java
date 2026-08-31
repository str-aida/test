
package com.Trabajo_Final_Beltran.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final RateLimitFilter rateLimitFilter;
    
    @Value("${app.security.require-https:false}")
    private boolean requireHttps;

    @Value("${app.security.hsts-enabled:false}")
    private boolean hstsEnabled;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider =
        new DaoAuthenticationProvider(customUserDetailsService);

    authProvider.setPasswordEncoder(passwordEncoder());

    return authProvider;
  }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();

    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
      // TODO: Implementar requireHttps al configurar el entorno de producción.
      // La propiedad app.security.require-https ya existe, pero aún no se utiliza
      // para forzar conexiones HTTPS mediante requiresChannel().
    http
            .headers(headers -> headers
                    .httpStrictTransportSecurity(hsts -> {
                        if (hstsEnabled) {
                            hsts.includeSubDomains(true)
                                .preload(true)
                                .maxAgeInSeconds(31536000);
                        } else {
                            hsts.disable();
                        }
                    })
                    .contentTypeOptions(contentTypeOptions -> {})
                    .frameOptions(frameOptions -> frameOptions.deny())
                    .contentSecurityPolicy(csp -> csp
                            .policyDirectives(
                                "default-src 'self'; " +
                                "img-src 'self' data: https:; " +
                                "script-src 'self'; " +
                                "style-src 'self' 'unsafe-inline'; " +
                                "connect-src 'self'"
                            )
                    )
            )
                .cors(cors -> {})
             // CSRF se deshabilita porque Gestia utiliza autenticación JWT stateless.
             // No se utilizan sesiones HTTP ni cookies de autenticación.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                            "/auth/crear-personal"
                        ).hasRole("ADMIN")
                    .requestMatchers(
                        "/auth/login",
                        "/auth/registro-admin",
                        "/auth/registro-cliente",
                        "/auth/solicitar-recuperacion",
                        "/auth/restablecer-password",
                        "/setup/**",
                        "/api/pagos/webhook",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/test",
                        "/uploads/**"
                    ).permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        rateLimitFilter,                            
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        jwtAuthFilter,                               
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }
}