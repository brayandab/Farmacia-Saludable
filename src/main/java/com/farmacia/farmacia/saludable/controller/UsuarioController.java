package com.farmacia.farmacia.saludable.controller;

import com.farmacia.farmacia.saludable.dto.RegistroDTO;
import com.farmacia.farmacia.saludable.entities.Usuario;
import com.farmacia.farmacia.saludable.repository.UsuarioRepository;
import com.farmacia.farmacia.saludable.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;  // Inyectar el repositorio

    @Autowired
    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;  // Inicializar el repositorio
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @PostMapping("/crear")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody RegistroDTO request) {
        // Aquí se puede tener la lógica para crear un nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setCorreo(request.getCorreo());
        usuario.setContraseña(request.getContraseña());
        usuario.setRol("cliente"); // Siempre cliente
        usuario.setFechaRegistro(LocalDateTime.now());

        Usuario guardado = usuarioRepository.save(usuario);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    // Registro completo (con datos adicionales)
    @PostMapping("/registroCompleto")
    public ResponseEntity<Usuario> registrarCompleto(@RequestBody RegistroDTO datosCompletos) {
        Usuario usuario = usuarioRepository.findByCorreo(datosCompletos.getCorreo());
        if (usuario != null) {
            // Aquí puedes actualizar los datos faltantes
            usuario.setFechaNacimiento(datosCompletos.getFechaNacimiento());
            usuario.setGenero(datosCompletos.getGenero());
            usuario.setTelefono(datosCompletos.getTelefono());
            usuario.setDireccion(datosCompletos.getDireccion());

            // Guardar el usuario con los datos actualizados
            usuarioRepository.save(usuario);

            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Usuario no encontrado
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario != null) {
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        boolean eliminado = usuarioService.eliminarUsuario(id);
        if (eliminado) {
            return ResponseEntity.ok("Usuario eliminado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }
    }
}
