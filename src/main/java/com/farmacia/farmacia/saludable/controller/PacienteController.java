package com.farmacia.farmacia.saludable.controller;

import com.farmacia.farmacia.saludable.entities.Paciente;
import com.farmacia.farmacia.saludable.repository.PacienteRepository;
import com.farmacia.farmacia.saludable.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Paciente>> listarTodos() {
        return new ResponseEntity<>(pacienteRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obtenerPacientePorId(@PathVariable Long id) {
        Optional<Paciente> pacienteOptional = pacienteRepository.findById(id);
        return pacienteOptional
                .map(paciente -> new ResponseEntity<>(paciente, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Eliminar paciente y su usuario relacionado
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPaciente(@PathVariable Long id) {
        boolean eliminado = usuarioService.eliminarPaciente(id);
        if (eliminado) {
            return ResponseEntity.ok("Paciente y usuario eliminado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente no encontrado.");
        }
    }
}
