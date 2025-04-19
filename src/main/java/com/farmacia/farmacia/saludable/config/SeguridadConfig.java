package com.farmacia.farmacia.saludable.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/", "/registro", "/basico", "/api/usuarios","/api/usuarios/**","/api/productos/**","/api/pacientes/**","/registroCompleto/**")  // Permite acceso sin autenticación a estas rutas
                                .permitAll()
                                .anyRequest()
                                .authenticated()  // El resto requiere autenticación
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .defaultSuccessUrl("/home", true)  // Página de éxito después de iniciar sesión con Google
                                .failureUrl("/login?error=true")  // Página de error
                );

        return http.build();
    }
}
