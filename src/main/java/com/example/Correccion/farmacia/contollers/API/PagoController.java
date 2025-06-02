package com.example.Correccion.farmacia.contollers.API;

import com.example.Correccion.farmacia.dto.ProductoCarritoDTO;
import com.example.Correccion.farmacia.dto.ProductosRequest;
import com.example.Correccion.farmacia.entities.*;
import com.example.Correccion.farmacia.repository.CompraRepository;
import com.example.Correccion.farmacia.repository.PacienteRepository;
import com.example.Correccion.farmacia.repository.ProductoRepository;
import com.example.Correccion.farmacia.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/pago")
public class PagoController {

    @Value("${stripe.secret.key}")
    private String secretKey;

    private final CompraRepository compraRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public PagoController(CompraRepository compraRepository,
                          PacienteRepository pacienteRepository,
                          UsuarioRepository usuarioRepository,
                          ProductoRepository productoRepository) {
        this.compraRepository = compraRepository;
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    // Crear sesión de pago en Stripe
    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody ProductosRequest request,
                                                                     HttpServletRequest httpRequest) {
        try {
            List<ProductoCarritoDTO> productos = request.getProductos();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String correo;

            if (auth.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
                correo = oauth2User.getAttribute("email");
            } else {
                correo = auth.getName();
            }

            Optional<Usuario> optionalUsuario = usuarioRepository.findByCorreo(correo);
            if (optionalUsuario.isEmpty()) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Usuario usuario = optionalUsuario.get();

            if (productos == null || productos.isEmpty()) {
                throw new RuntimeException("Carrito vacío");
            }

            // Guardar productos como JSON en usuario (opcional)
            ObjectMapper mapper = new ObjectMapper();
            String productosJson = mapper.writeValueAsString(productos);
            usuario.setCarritolista(productosJson);
            usuarioRepository.save(usuario);

            // Construir URL base dinámica tomando en cuenta headers de proxy (Cloud Run)
            String scheme = Optional.ofNullable(httpRequest.getHeader("X-Forwarded-Proto")).orElse(httpRequest.getScheme());

            String forwardedHost = httpRequest.getHeader("X-Forwarded-Host");
            if (forwardedHost != null && forwardedHost.endsWith(":80")) {
                forwardedHost = forwardedHost.substring(0, forwardedHost.length() - 3); // Elimina ":80"
            }

            String host = Optional.ofNullable(forwardedHost).orElse(httpRequest.getServerName());
            int port = httpRequest.getServerPort();

            boolean isCloudRun = forwardedHost != null;
            String url="";
            if (!isCloudRun) {
                // Solo agrega puerto si no es estándar HTTP/HTTPS
                if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
                    url= host;
                }
            }

            String baseUrl = scheme + "://" + url;
            System.out.println("LA URL QUE TIENE EN EL MOMENTO: " + baseUrl);

            // Crear items para Stripe
            List<SessionCreateParams.LineItem> lineItems = productos.stream().map(prod ->
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) prod.getCantidad())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("cop")
                                            .setUnitAmount((long) (prod.getPrecio() * 100)) // centavos
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(prod.getNombre())
                                                            .build())
                                            .build())
                            .build()
            ).toList();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(baseUrl + "/success")
                    .setCancelUrl(baseUrl + "/cancel")
                    .addAllLineItem(lineItems)
                    .build();

            Session session = Session.create(params);

            return ResponseEntity.ok(Collections.singletonMap("id", session.getId()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Error al iniciar el pago: " + e.getMessage()));
        }
    }

    // Guardar la compra y detalles luego de la confirmación del pago
    @PostMapping("/guardar-compra")
    public ResponseEntity<String> guardarCompraConDetalles(@RequestBody List<ProductoCarritoDTO> productos) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String correo;

            if (auth.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
                correo = oauth2User.getAttribute("email");
            } else {
                correo = auth.getName();
            }

            Optional<Paciente> pacienteOpt = pacienteRepository.findByUsuario_Correo(correo);
            if (pacienteOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Paciente no encontrado");
            }

            Paciente paciente = pacienteOpt.get();

            Compra compra = new Compra();
            compra.setPaciente(paciente);
            compra.setFechaCompra(LocalDate.now());

            double total = productos.stream()
                    .mapToDouble(p -> p.getPrecio() * p.getCantidad())
                    .sum();
            compra.setTotal(total);

            for (ProductoCarritoDTO prodDTO : productos) {
                Optional<Producto> productoOpt = productoRepository.findById(prodDTO.getId());
                Producto producto = productoOpt.get();
                producto.setStock(producto.getStock() - prodDTO.getCantidad());
                productoRepository.save(producto);

                DetalleCompra detalle = new DetalleCompra();
                detalle.setNombreProducto(producto.getNombre());
                detalle.setCantidad(prodDTO.getCantidad());
                detalle.setPrecioUnitario(prodDTO.getPrecio());

                compra.agregarDetalle(detalle);
            }

            compraRepository.save(compra);
            return ResponseEntity.ok("Compra guardada con éxito");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al guardar la compra: " + e.getMessage());
        }
    }
}
