package com.example.Correccion.farmacia.contollers.API;

import com.example.Correccion.farmacia.dto.ProductoCarritoDTO;
import com.example.Correccion.farmacia.dto.ProductosRequest;
import com.example.Correccion.farmacia.entities.Compra;
import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.repository.CompraRepository;
import com.example.Correccion.farmacia.repository.PacienteRepository;
import com.example.Correccion.farmacia.repository.UsuarioRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pago")
public class PagoController {

    @Value("${stripe.secret.key}")
    private String secretKey;

    private final CompraRepository compraRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    public PagoController(CompraRepository compraRepository,
                          PacienteRepository pacienteRepository,
                          UsuarioRepository usuarioRepository) {
        this.compraRepository = compraRepository;
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody ProductosRequest request) {
        try {
            List<ProductoCarritoDTO> productos = request.getProductos();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String correoUsuario = auth.getName();

            Optional<Usuario> optionalUsuario = usuarioRepository.findByCorreo(correoUsuario);

            if (optionalUsuario.isEmpty()) {
                throw new RuntimeException("Usuario no encontrado");
            }

            Usuario usuario = optionalUsuario.get();

            if (productos == null || productos.isEmpty()) {
                throw new RuntimeException("Carrito vacío");
            }

            // Calcular total antes de crear lineItems
            long totalCentavos = productos.stream()
                    .mapToLong(prod -> (long)(prod.getPrecio() * 100) * prod.getCantidad())
                    .sum();

            System.out.println("Total que se enviará a Stripe (en centavos): " + totalCentavos);
            System.out.println("Total que se enviará a Stripe (en pesos): " + (totalCentavos / 100.0));

// Luego crear lineItems como antes
            List<SessionCreateParams.LineItem> lineItems = productos.stream().map(prod ->
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) prod.getCantidad())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("mxn")
                                            .setUnitAmount((long)(prod.getPrecio() * 100))  // convertir a centavos
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(prod.getNombre())
                                                            .build())
                                            .build())
                            .build()
            ).toList();



            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:8080/success")
                    .setCancelUrl("http://localhost:8080/cancel")
                    .addAllLineItem(lineItems)
                    .build();

            Session session = Session.create(params);

            return ResponseEntity.ok(Collections.singletonMap("id", session.getId()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Error al iniciar el pago: " + e.getMessage()));
        }
    }
}
