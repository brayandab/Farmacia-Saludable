package com.example.Correccion.farmacia.contollers.View;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagoWebController {

    @GetMapping("/pago")
    public String mostrarFormularioPago() {
        return "pago"; // Asegúrate de que 'pago.html' esté en src/main/resources/templates
    }
}
