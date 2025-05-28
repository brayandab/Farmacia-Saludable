package com.example.Correccion.farmacia.contollers.View;

import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.repository.ProductoRepository;
import com.example.Correccion.farmacia.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/farmaceutico")
public class FarmaceuticoWebController {


    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ProductoRepository productoRepository; // O ProductoService si usas uno


    @GetMapping("/home")
    public String homeFarmaceutico(Model model, Authentication authentication) {
        String email;

        if (authentication.getPrincipal() instanceof OAuth2User oauthUser) {
            email = oauthUser.getAttribute("email");
        } else {
            email = authentication.getName();
        }

        Usuario usuario = usuarioService.encontrarPorCorreo(email);

        if (usuario == null) {
            usuario = new Usuario();
            usuario.setNombre("Desconocido");
        }

        model.addAttribute("usuario", usuario);

        return "farmaceutico/home";
    }



    @GetMapping("/inventario")
    public String mostrarInventario(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "farmaceutico/inventario";
    }

}
