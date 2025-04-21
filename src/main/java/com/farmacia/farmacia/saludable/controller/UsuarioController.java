package com.farmacia.farmacia.saludable.controller;

import com.farmacia.farmacia.saludable.dto.RegistroDTO;
import com.farmacia.farmacia.saludable.entities.Usuario;
import com.farmacia.farmacia.saludable.repository.UsuarioRepository;
import com.farmacia.farmacia.saludable.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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
    /*@PostMapping("/registroCompleto")
    public String completarRegistro(@RequestParam String fechaNacimiento,
                                    @RequestParam String genero,
                                    @RequestParam String telefono,
                                    @RequestParam String direccion,
                                    OAuth2AuthenticationToken authentication,
                                    RedirectAttributes redirectAttributes) {
        String nombre = (String) authentication.getPrincipal().getAttributes().get("given_name");
        String apellido = (String) authentication.getPrincipal().getAttributes().get("family_name");
        String correo = (String) authentication.getPrincipal().getAttributes().get("email");

        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario != null) {
            usuario.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            usuario.setGenero(genero);
            usuario.setTelefono(telefono);
            usuario.setDireccion(direccion);
            usuarioRepository.save(usuario);
        }

        // Enviamos los datos como flash attributes para la siguiente vista
        redirectAttributes.addFlashAttribute("nombre", nombre + " " + apellido);
        redirectAttributes.addFlashAttribute("correo", correo);

        return "redirect:/registro-exitoso";
    }*/

    @GetMapping("/registro-exitoso")
    public String registroExitoso() {
        return "registro-exitoso"; // nombre del archivo HTML (sin extensión)
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
