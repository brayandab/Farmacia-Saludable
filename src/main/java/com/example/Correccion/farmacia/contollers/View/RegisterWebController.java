package com.example.Correccion.farmacia.contollers.View;

import com.example.Correccion.farmacia.entities.Paciente;
import com.example.Correccion.farmacia.entities.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegisterWebController {

    @GetMapping("/register")
    public String mostrarFormularioRegistro(Model model) {
        Usuario usuario = new Usuario();
        usuario.setPaciente(new Paciente()); // inicializas para que no sea null
        model.addAttribute("usuario", usuario);
        return "register";
    }


}
