package com.example.Correccion.farmacia.contollers.View;

import com.example.Correccion.farmacia.entities.Producto;
import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.repository.ProductoRepository;
import com.example.Correccion.farmacia.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeWebController {

    private final ProductoRepository productoRepository;
    private final UsuarioService usuarioService;

    @Autowired
    public HomeWebController(ProductoRepository productoRepository, UsuarioService usuarioService) {
        this.productoRepository = productoRepository;
        this.usuarioService = usuarioService;
    }

    //Este metodo manda al usuario a diferentes vistas dependiendo del rol (cliente o farmaceutico)
    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        List<Producto> productos = productoRepository.findAll();
        model.addAttribute("productos", productos);

        if (authentication == null) {
            return "index-publico"; // No logueado
        }

        String correo = null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof User userDetails) {
            correo = userDetails.getUsername();
        } else if (principal instanceof OAuth2User oauthUser) {
            correo = (String) oauthUser.getAttributes().get("email");
        }

        if (correo != null) {
            Usuario usuario = usuarioService.encontrarPorCorreo(correo);
            if (usuario != null) {
                model.addAttribute("usuario", usuario);

                String rol = usuario.getRol().toLowerCase();
                if ("cliente".equals(rol)) {
                    return "index-privado";
                } else if ("farmaceutico".equals(rol)) {
                    return "farmaceutico/home";
                }
            }
        }

        return "index-publico"; // Si no se detecta bien el usuario
    }

    //Este es el metodo que lo dirige al html de login
    @GetMapping("/login")
    public String login(Model model, Authentication authentication) {
        agregarUsuarioAlModelo(model, authentication);
        return "login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, Authentication authentication) {
        agregarUsuarioAlModelo(model, authentication);
        return "admin/dashboard";
    }
//Este método obtiene el correo del usuario autenticado (ya sea por login tradicional o con Google), busca su información
// en la base de datos y la agrega al modelo para que esté disponible en las vistas.
    private void agregarUsuarioAlModelo(Model model, Authentication authentication) {
        if (authentication == null) return;

        String correo = null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof User userDetails) {
            correo = userDetails.getUsername();
        } else if (principal instanceof OAuth2User oauthUser) {
            correo = (String) oauthUser.getAttributes().get("email");
        }

        if (correo != null) {
            Usuario usuario = usuarioService.encontrarPorCorreo(correo);
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
            }
        }
    }
}
