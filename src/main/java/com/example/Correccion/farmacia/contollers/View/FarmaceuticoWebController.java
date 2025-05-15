package com.example.Correccion.farmacia.contollers.View;

import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/home")
    public String homeFarmaceutico(Model model, Principal principal) {
        // Obtener el email del usuario logueado
        String email = principal.getName();

        // Buscar el usuario por email
        Usuario usuario = usuarioService.encontrarPorCorreo(email);

        // Pasar el nombre del usuario al modelo
        if (usuario != null) {
            model.addAttribute("nombreUsuario", usuario.getNombre());
        } else {
            model.addAttribute("nombreUsuario", "Desconocido");
        }

        return "farmaceutico/home"; // Asegúrate de tener esta vista
    }
}
