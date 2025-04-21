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
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;


@Controller
public class InicioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String mostrarRegistro(@RequestParam(required = false) String error,
                                  org.springframework.ui.Model model) {
        if ("correo_existente".equals(error)) {
            model.addAttribute("correoExistente", true);
        }
        return "registro";
    }


    @PostMapping("/basico")
    public String registrarPasoBasico(@RequestParam String nombre,
                                      @RequestParam String apellido,
                                      @RequestParam String correo,
                                      @RequestParam String contraseña) {

        System.out.println("Nombre recibido: " + nombre);
        System.out.println("Apellido recibido: " + apellido);
        System.out.println("Correo recibido: " + correo);
        System.out.println("Contraseña recibida: " + contraseña);

        Optional<Usuario> existente = usuarioRepository.findByCorreo(correo);

        if (existente.isPresent()) {
            System.out.println("El correo ya está registrado: " + correo);
            return "redirect:/?error=correo_existente";
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCorreo(correo);
        usuario.setContraseña(contraseña);
        usuario.setRol("cliente");
        usuario.setFechaRegistro(LocalDateTime.now());

        usuarioRepository.save(usuario);

        System.out.println("Usuario registrado exitosamente: " + usuario.getCorreo());

        return "redirect:/login/oauth2/authorization/google";
    }


}
