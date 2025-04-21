package com.farmacia.farmacia.saludable.controller;

import com.farmacia.farmacia.saludable.dto.UsuarioPacienteDTO;
import com.farmacia.farmacia.saludable.entities.Paciente;
import com.farmacia.farmacia.saludable.entities.Usuario;
import com.farmacia.farmacia.saludable.repository.PacienteRepository;
import com.farmacia.farmacia.saludable.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", principal);
        return "home"; // Página de inicio
    }


    @PostMapping("/registroCompleto")
    public String registrarDesdeFormulario(@ModelAttribute UsuarioPacienteDTO dto, OAuth2AuthenticationToken token) {
        String correo = token.getPrincipal().getAttribute("email");

        Optional<Usuario> optionalUsuario = usuarioRepository.findByCorreo(correo);
        if (optionalUsuario.isEmpty()) return "redirect:/error";

        Usuario usuario = optionalUsuario.get();

        // Datos del usuario
        usuario.setFechaNacimiento(dto.getUsuario().getFechaNacimiento());
        usuario.setGenero(dto.getUsuario().getGenero());
        usuario.setTelefono(dto.getUsuario().getTelefono());
        usuario.setDireccion(dto.getUsuario().getDireccion());
        usuario.setTipoSangre(dto.getUsuario().getTipoSangre());
        usuarioRepository.save(usuario);

        // Solo si es cliente se guarda paciente
        if ("cliente".equalsIgnoreCase(usuario.getRol())) {
            Paciente paciente = dto.getPaciente();
            paciente.setUsuario(usuario);
            pacienteRepository.save(paciente);
        }

        return "redirect:/registro-exitoso";
    }

    @GetMapping("/registro-exitoso")
    public String mostrarConfirmacion() {
        return "registro-exitoso";
    }
}
