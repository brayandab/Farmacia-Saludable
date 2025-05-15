package com.example.Correccion.farmacia.contollers.API;

import com.example.Correccion.farmacia.dto.RegistroDTO;
import com.example.Correccion.farmacia.entities.Paciente;
import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.repository.PacienteRepository;
import com.example.Correccion.farmacia.repository.UsuarioRepository;
import com.example.Correccion.farmacia.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository; // Inyectar PacienteRepository

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Constructor con inyección de dependencias
    @Autowired
    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository, PacienteRepository pacienteRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.pacienteRepository = pacienteRepository; // Inicializar el repositorio de Paciente
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    // Método para crear un usuario cuyo rol es cliente
    @PostMapping("/crear-cliente")
    public ResponseEntity<?> crearUsuario_cliente(@RequestBody RegistroDTO request) {
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setCorreo(request.getCorreo());
        usuario.setContraseña(passwordEncoder.encode(request.getContraseña())); // Cifrado de contraseña
        usuario.setRol("cliente");
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setGenero(request.getGenero());
        usuario.setTelefono(request.getTelefono());

        // Guardar usuario primero para generar ID
        Usuario guardado = usuarioRepository.save(usuario);

        // Crear paciente solo si el rol es cliente
        if ("cliente".equalsIgnoreCase(guardado.getRol())) {
            Paciente paciente = new Paciente();
            paciente.setUsuario(guardado);
            paciente.setDireccion(request.getDireccion());
            paciente.setFechaNacimiento(request.getFechaNacimiento());
            paciente.setAlergias(request.getAlergias());
            paciente.setHistorialMedico(request.getHistorialMedico());
            paciente.setMedicamentosRecetados(request.getMedicamentosRecetados());
            paciente.setOtraInformacionRelevante(request.getOtraInformacionRelevante());
            paciente.setTipoSangre(request.getTipoSangre());

            // Guardar paciente
            pacienteRepository.save(paciente);
        }

        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    //Este metodo aun no funciona
    @PostMapping("/crear-farmaceutico")
    public ResponseEntity<?> crearUsuario(@RequestBody RegistroDTO request) {
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setCorreo(request.getCorreo());
        usuario.setContraseña(passwordEncoder.encode(request.getContraseña())); // Cifrado de contraseña
        usuario.setRol(request.getRol()); // Debe venir como "farmaceutico"
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setGenero(request.getGenero());
        usuario.setTelefono(request.getTelefono());

        // Guardar usuario
        Usuario guardado = usuarioRepository.save(usuario);

        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }



    // Obtener un usuario por el ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario != null) {
            return new ResponseEntity<>(usuario, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Eliminar un usuario por el ID
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
