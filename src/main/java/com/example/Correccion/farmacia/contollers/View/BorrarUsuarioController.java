package com.example.Correccion.farmacia.contollers.View;

import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.services.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class BorrarUsuarioController {

    private final UsuarioService usuarioService;

    public BorrarUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Solo usuarios con rol FARMACEUTICO pueden acceder
    @PreAuthorize("hasRole('FARMACEUTICO')")
    @GetMapping("/farmaceutico/borrar-usuario")
    public String mostrarUsuarios(Authentication authentication, Model model) {
        String correo = obtenerCorreoDeAuth(authentication);
        if (correo == null) {
            return "redirect:/login";
        }

        Usuario usuarioLogueado = usuarioService.encontrarPorCorreo(correo);
        model.addAttribute("usuario", usuarioLogueado);

        List<Usuario> usuarios = usuarioService.obtenerTodos();
        model.addAttribute("usuarios", usuarios);

        return "farmaceutico/borrar-usuario";
    }

    @PreAuthorize("hasRole('FARMACEUTICO')")
    @PostMapping("/farmaceutico/borrar-usuario")
    public String eliminarUsuario(@RequestParam("id") Long id, Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        String correo = obtenerCorreoDeAuth(authentication);
        if (correo == null) {
            return "redirect:/login";
        }

        Usuario usuarioLogueado = usuarioService.encontrarPorCorreo(correo);

        // Evitar que el usuario se elimine a sí mismo
        if (usuarioLogueado.getIdUsuario().equals(id)) {
            redirectAttributes.addFlashAttribute("error", "No puedes eliminarte a ti mismo.");
            return "redirect:/farmaceutico/borrar-usuario";
        }

        try {
            usuarioService.eliminarUsuario(id);  // Asegúrate que este método exista
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }

        return "redirect:/farmaceutico/borrar-usuario";
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
