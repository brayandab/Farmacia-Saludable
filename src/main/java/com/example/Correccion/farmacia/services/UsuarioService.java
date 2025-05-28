package com.example.Correccion.farmacia.services;



import com.example.Correccion.farmacia.entities.Paciente;
import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.repository.PacienteRepository;
import com.example.Correccion.farmacia.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PacienteRepository pacienteRepo;

    public Usuario crearUsuario(Usuario usuario, Paciente datosPaciente) {
        usuario.setFechaRegistro(LocalDateTime.now());
        Usuario nuevo = usuarioRepo.save(usuario); // Guardamos primero el usuario

        if ("cliente".equalsIgnoreCase(nuevo.getRol()) && datosPaciente != null) {
            // Verificar que los campos de datosPaciente estén correctamente asignados
            if (datosPaciente.getAlergias() != null) {
                System.out.println("Alergias: " + datosPaciente.getAlergias());
            }
            if (datosPaciente.getHistorialMedico() != null) {
                System.out.println("Historial Medico: " + datosPaciente.getHistorialMedico());
            }

            // Relacionar el usuario con el paciente
            datosPaciente.setUsuario(nuevo);
            pacienteRepo.save(datosPaciente); // Guardamos el paciente con los datos asignados
        }

        return nuevo;
    }



    public List<Usuario> obtenerTodos() {
        return usuarioRepo.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepo.findById(id).orElse(null);
    }

    public boolean eliminarUsuario(Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepo.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            if ("cliente".equalsIgnoreCase(usuario.getRol())) {
                Paciente paciente = pacienteRepo.findByUsuario_IdUsuario(id);
                if (paciente != null) {
                    pacienteRepo.delete(paciente);
                }
            }

            usuarioRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean eliminarPaciente(Long idPaciente) {
        Optional<Paciente> pacienteOpt = pacienteRepo.findById(idPaciente);
        if (pacienteOpt.isPresent()) {
            Paciente paciente = pacienteOpt.get();
            Usuario usuario = paciente.getUsuario();

            pacienteRepo.delete(paciente);

            if (usuario != null) {
                usuarioRepo.delete(usuario);
            }

            return true;
        }
        return false;
    }

    // Método para encontrar un usuario por su correo
    public Usuario encontrarPorCorreo(String correo) {
        return usuarioRepo.findByCorreo(correo).orElse(null);
    }

    public Usuario findByNombre(String nombre) {
        return usuarioRepo.findByNombre(nombre).orElse(null);
    }

    public Usuario obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            System.out.println("Authentication nulo o no autenticado");
            return null;
        }

        Object principal = auth.getPrincipal();

        System.out.println("Tipo de principal: " + principal.getClass().getName());

        if (principal instanceof DefaultOidcUser) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
            String email = (String) oidcUser.getAttributes().get("email");
            System.out.println("Email extraído de OIDC: " + email);

            return usuarioRepo.findByCorreo(email).orElse(null);

        } else if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            System.out.println("Email extraído de UserDetails: " + email);

            return usuarioRepo.findByCorreo(email).orElse(null);
        } else {
            System.out.println("Principal NO es instancia esperada");
            return null;
        }
    }



    @Transactional
    public Usuario guardar(Usuario usuario) {
        if (usuario.getPaciente() != null) {
            pacienteRepo.save(usuario.getPaciente());
        }
        return usuarioRepo.save(usuario);
    }



}
