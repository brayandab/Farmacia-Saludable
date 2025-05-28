package com.example.Correccion.farmacia.contollers.View;

import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.services.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CrearUsuarioController {

    private final UsuarioService usuarioService;

    public CrearUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/farmaceutico/crear")
    public String mostrarFormularioCrear(Authentication authentication, Model model) {
        String correo = obtenerCorreoDeAuth(authentication);
        if (correo == null) {
            return "redirect:/login";
        }

        Usuario usuarioLogueado = usuarioService.encontrarPorCorreo(correo);
        model.addAttribute("usuario", usuarioLogueado);

        model.addAttribute("nuevoUsuario", new Usuario());
        return "farmaceutico/crear-usuario";
    }

    @PostMapping("/farmaceutico/crear")
    public String crearUsuario(@ModelAttribute("nuevoUsuario") Usuario nuevoUsuario,
                               Authentication authentication, Model model) {
        String correo = obtenerCorreoDeAuth(authentication);
        if (correo == null) {
            return "redirect:/login";
        }

        Usuario usuarioLogueado = usuarioService.encontrarPorCorreo(correo);
        model.addAttribute("usuario", usuarioLogueado);

        if (nuevoUsuario.getContraseña() == null || nuevoUsuario.getContraseña().isEmpty()) {
            model.addAttribute("error", "La contraseña es obligatoria");
            return "farmaceutico/crear-usuario";
        }

        nuevoUsuario.setRol("farmaceutico"); // definir rol aquí
        usuarioService.crearUsuario(nuevoUsuario, null);

        model.addAttribute("mensaje", "Usuario creado correctamente");
        model.addAttribute("nuevoUsuario", new Usuario()); // limpiar el formulario después de crear
        return "farmaceutico/crear-usuario";
    }


    private String obtenerCorreoDeAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User user = oauthToken.getPrincipal();
            return user.getAttribute("email");
        } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return null;
    }
}
