package com.example.Correccion.farmacia.contollers.View;

import com.example.Correccion.farmacia.entities.Paciente;
import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class PerfilWebController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (auth instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) auth;
            OAuth2User oauthUser = oauthToken.getPrincipal();
            username = (String) oauthUser.getAttributes().get("email");
        } else {
            username = auth.getName();
        }

        Usuario usuario = usuarioService.encontrarPorCorreo(username);
        if (usuario == null) {
            return "redirect:/login?error";
        }

        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@ModelAttribute("usuario") Usuario usuarioActualizado, Model model) {
        Usuario usuarioOriginal = usuarioService.obtenerUsuarioActual();

        // Actualizar campos
        usuarioOriginal.setNombre(usuarioActualizado.getNombre());
        usuarioOriginal.setApellido(usuarioActualizado.getApellido());
        usuarioOriginal.setTelefono(usuarioActualizado.getTelefono());
        usuarioOriginal.setGenero(usuarioActualizado.getGenero());

        if (usuarioOriginal.getPaciente() != null && usuarioActualizado.getPaciente() != null) {
            Paciente pacienteOriginal = usuarioOriginal.getPaciente();
            Paciente pacienteActualizado = usuarioActualizado.getPaciente();

            pacienteOriginal.setDireccion(pacienteActualizado.getDireccion());
            pacienteOriginal.setFechaNacimiento(pacienteActualizado.getFechaNacimiento());
        }

        usuarioService.guardar(usuarioOriginal);

        // Recargar usuario actualizado para asegurar que el modelo esté correcto
        Usuario usuarioConPaciente = usuarioService.obtenerUsuarioActual();

        model.addAttribute("usuario", usuarioConPaciente);  // <-- aquí está lo clave
        model.addAttribute("mensaje", "Perfil actualizado correctamente");

        return "perfil";  // Vista perfil donde usas 'usuario' y 'paciente'
    }



}
