package com.farmacia.farmacia.saludable.service;

import com.farmacia.farmacia.saludable.entities.Paciente;
import com.farmacia.farmacia.saludable.entities.Usuario;
import com.farmacia.farmacia.saludable.repository.PacienteRepository;
import com.farmacia.farmacia.saludable.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        Usuario nuevo = usuarioRepo.save(usuario);

        if ("cliente".equalsIgnoreCase(nuevo.getRol()) && datosPaciente != null) {

            datosPaciente.setUsuario(nuevo);
            pacienteRepo.save(datosPaciente);
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



}
