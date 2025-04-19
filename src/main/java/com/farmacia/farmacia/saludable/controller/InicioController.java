package com.farmacia.farmacia.saludable.controller;

import com.farmacia.farmacia.saludable.dto.RegistroDTO;
import com.farmacia.farmacia.saludable.entities.Usuario;
import com.farmacia.farmacia.saludable.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Controller
public class InicioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String mostrarRegistro() {
        return "registro"; // Esta es la vista HTML de registro
    }

    // Paso 1: Registrar los datos básicos
    @PostMapping("/basico")
    public String registrarPasoBasico(@RequestBody RegistroDTO datos) {
        Usuario usuario = new Usuario();
        usuario.setNombre(datos.getNombre());
        usuario.setApellido(datos.getApellido());
        usuario.setCorreo(datos.getCorreo());
        usuario.setContraseña(datos.getContraseña());
        usuario.setRol("cliente"); // siempre cliente
        usuario.setFechaRegistro(LocalDateTime.now());

        // Guardar al usuario en la base de datos
        usuarioRepository.save(usuario);

        // Redirigir a la segunda parte del formulario
        return "redirect:/registroCompleto"; // Aquí se redirige a la página para completar el resto de los datos
    }

}
