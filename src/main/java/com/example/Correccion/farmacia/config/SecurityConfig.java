package com.example.Correccion.farmacia.config;

import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    //Encriptacion de la contraseña
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //para el login de las dos credenciales este metodo busca al usuario por el correo y saca el nombre
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepository.findByCorreo(username)
                .map(usuario -> org.springframework.security.core.userdetails.User
                        .withUsername(usuario.getCorreo())
                        .password(usuario.getContraseña())
                        .roles(usuario.getRol().toUpperCase())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con correo: " + username));
    }
    //para el login de las dos credenciales este metodo busca al usuario por el correo y saca
    // el nombre( aun no funciona ya que no saca el nombre por el correo en google)
    private OAuth2User oauth2UserService(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");

        Usuario usuario = usuarioRepository.findByCorreo(email).orElseGet(() -> {
            Usuario nuevo = new Usuario();
            nuevo.setCorreo(email);
            nuevo.setNombre((String) attributes.get("given_name"));
            nuevo.setApellido((String) attributes.get("family_name"));
            nuevo.setRol("cliente"); // solo si es nuevo
            return usuarioRepository.save(nuevo);
        });

        // Aquí imprimes el rol y autoridad
        System.out.println("ROL del usuario desde DB: " + usuario.getRol());
        String rol = "ROLE_" + usuario.getRol().toUpperCase();
        System.out.println("Autoridad usada: " + rol);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(rol));

        return new DefaultOAuth2User(authorities, attributes, "email");
    }




    //Dependiendo del rol del correo que tenga lo manda a una u otra ruta( HTML )
    private AuthenticationSuccessHandler successHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            boolean esFarmaceutico = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_FARMACEUTICO"));
            System.out.println("El valor de successHandler "+esFarmaceutico);
            if (esFarmaceutico) {
                response.sendRedirect("/farmaceutico/home");
            } else {
                response.sendRedirect("/");
            }
        };
    }

    /*Dar autorizacion de las rutas que no van a usar registro oauth
    Con el simple hecho de poner (ejemplo http://localhost:8080/api/** si se pone la /api/** esto hace
    que toda las rutas que vayan despues de api esten autorizadas con solo colocar los dos asteriscos*/
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/", "/home", "/index/**", "/productos",
                                "/api/**","/register","/api/pacientes/**").permitAll()
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService()) // 👈 REGISTRA TU SERVICIO AQUÍ
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler())
                        .permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(info -> info.userService(this::oauth2UserService))
                        .successHandler(successHandler())
                )
                .logout(logout -> logout.permitAll())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

}
