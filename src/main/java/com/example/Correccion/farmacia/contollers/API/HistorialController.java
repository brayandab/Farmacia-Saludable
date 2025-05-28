package com.example.Correccion.farmacia.contollers.API;

import com.example.Correccion.farmacia.dto.CompraDTO;
import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.services.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HistorialController {

    private final UsuarioService usuarioService;

    public HistorialController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/cliente/historial")
    public String mostrarHistorialCompras(Authentication authentication, Model model) {
        // Verifica si el usuario está autenticado
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String correo = null;

        // Soporte para login con OAuth2
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User user = oauthToken.getPrincipal();
            correo = user.getAttribute("email");
        }
        // Soporte para login tradicional (formulario)
        else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            correo = userDetails.getUsername(); // Asumiendo que el username es el correo
        }

        if (correo == null) {
            return "redirect:/login";
        }

        // Buscar usuario
        Usuario usuario = usuarioService.encontrarPorCorreo(correo);
        model.addAttribute("usuario", usuario);

        // Preparar historial de compras
        List<CompraDTO> historial = new ArrayList<>();

        if (usuario != null && usuario.getPaciente() != null) {
            usuario.getPaciente().getCompras().forEach(compra -> {
                compra.getDetalles().forEach(detalle -> {
                    CompraDTO dto = new CompraDTO();
                    dto.setNombreProducto(detalle.getNombreProducto());
                    dto.setCantidad(detalle.getCantidad());
                    dto.setPrecioUnitario(detalle.getPrecioUnitario());
                    dto.setFechaCompra(compra.getFechaCompra());
                    historial.add(dto);
                });
            });
        }

        model.addAttribute("historial", historial);
        return "historial-compras";
    }
}
