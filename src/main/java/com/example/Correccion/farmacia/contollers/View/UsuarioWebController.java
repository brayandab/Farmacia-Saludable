package com.example.Correccion.farmacia.contollers.View;

import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioWebController {

    @Autowired
    private UsuarioService usuarioService;

    // Mostrar todos los usuarios en una vista HTML
    @GetMapping
    public String mostrarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuarios";  // Nombre de la plantilla Thymeleaf (usuarios.html)
    }

    // Mostrar formulario para crear un usuario
    @GetMapping("/crear")
    public String mostrarFormularioCrearUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "crearUsuario";  // Nombre de la plantilla Thymeleaf (crearUsuario.html)
    }



    // Mostrar detalles de un usuario
    @GetMapping("/{id}")
    public String mostrarDetallesUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            return "detalleUsuario";  // Nombre de la plantilla Thymeleaf (detalleUsuario.html)
        } else {
            return "error";  // Mostrar error si no se encuentra el usuario
        }
    }
}
