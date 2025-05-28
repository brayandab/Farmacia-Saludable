package com.example.Correccion.farmacia.contollers.View;

import com.example.Correccion.farmacia.dto.CompraDTO;
import com.example.Correccion.farmacia.entities.Compra;
import com.example.Correccion.farmacia.services.CompraService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PedidosWebController {

    private final CompraService compraService;

    public PedidosWebController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping("/farmaceutico/pedidos")
    public String mostrarPedidos(Model model) {
        List<Compra> compras = compraService.obtenerTodasLasCompras();
        List<CompraDTO> pedidosDTO = new ArrayList<>();

        for (Compra compra : compras) {
            compra.getDetalles().forEach(detalle -> {
                CompraDTO dto = new CompraDTO();
                dto.setNombreProducto(detalle.getNombreProducto());
                dto.setCantidad(detalle.getCantidad());
                dto.setPrecioUnitario(detalle.getPrecioUnitario());
                dto.setFechaCompra(compra.getFechaCompra());

                if (compra.getPaciente() != null && compra.getPaciente().getUsuario() != null) {
                    dto.setNombreCliente(compra.getPaciente().getUsuario().getNombre());
                    dto.setApellidoCliente(compra.getPaciente().getUsuario().getApellido());
                    dto.setCorreoCliente(compra.getPaciente().getUsuario().getCorreo());
                }

                pedidosDTO.add(dto);
            });
        }

        model.addAttribute("pedidos", pedidosDTO);
        return "farmaceutico/pedidos";
    }
}
