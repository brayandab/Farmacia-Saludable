package com.example.Correccion.farmacia.contollers.API;

import com.example.Correccion.farmacia.dto.ProductoCarritoDTO;
import com.example.Correccion.farmacia.dto.RegistroDTO;
import com.example.Correccion.farmacia.entities.Compra;
import com.example.Correccion.farmacia.entities.Paciente;
import com.example.Correccion.farmacia.entities.Producto;
import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.repository.PacienteRepository;
import com.example.Correccion.farmacia.repository.ProductoRepository;
import com.example.Correccion.farmacia.repository.UsuarioRepository;
import com.example.Correccion.farmacia.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository; // Inyectar PacienteRepository


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProductoRepository productoRepository;

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
            Compra compra = new Compra();
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

    @GetMapping("/catalogo")
    public String mostrarCatalogo(Model model, Principal principal) {
        String username = principal.getName();

        // Obtener usuario
        Usuario usuario = usuarioRepository.findByCorreo(username).orElse(null); // O el método que tengas para buscar por correo

        // Obtener productos del catálogo
        List<Producto> productos = productoRepository.findAll();


        // Obtener carrito lista desde el usuario y convertir a lista de DTO
        List<ProductoCarritoDTO> carritoItems = productoCarritoDtoFromUsuario(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("productos", productos);
        model.addAttribute("carritoItems", carritoItems);

        return "catalogo"; // tu vista HTML
    }

    // Método auxiliar para convertir la lista de carrito del usuario a List<ProductoCarritoDTO>
    private List<ProductoCarritoDTO> productoCarritoDtoFromUsuario(Usuario usuario) {
        // Supongamos que carritolista es un JSON almacenado como String
        String carritoJson = usuario.getCarritolista();

        if (carritoJson == null || carritoJson.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            // Convertir JSON a lista de ProductoCarritoDTO
            List<ProductoCarritoDTO> lista = mapper.readValue(carritoJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, ProductoCarritoDTO.class));
            return lista;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @GetMapping("/ver-carrito/{idUsuario}")
    public ResponseEntity<List<ProductoCarritoDTO>> verCarritoConInfo(@PathVariable Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Convertir el JSON a lista de ProductoCarritoDTO
        List<ProductoCarritoDTO> carritoDTOs = productoCarritoDtoFromUsuario(usuario);

        if (carritoDTOs.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        // Traer datos reales de productos y rellenar los DTOs
        List<Long> ids = carritoDTOs.stream().map(ProductoCarritoDTO::getId).toList();
        List<Producto> productos = productoRepository.findAllById(ids);

        // Mapear info del producto al DTO (nombre, precio, etc.)
        for (ProductoCarritoDTO dto : carritoDTOs) {
            productos.stream()
                    .filter(p -> p.getIdMedicamento().equals(dto.getId()))
                    .findFirst()
                    .ifPresent(p -> {
                        dto.setNombre(p.getNombre());
                        dto.setPrecio(p.getPrecioUnitario());
                        // Puedes asignar más campos si quieres
                    });
        }

        return ResponseEntity.ok(carritoDTOs);
    }




}
