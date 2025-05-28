package com.example.Correccion.farmacia.contollers.API;

import com.example.Correccion.farmacia.entities.Compra;
import com.example.Correccion.farmacia.entities.Paciente;
import com.example.Correccion.farmacia.repository.CompraRepository;
import com.example.Correccion.farmacia.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    @Autowired
    private CompraRepository compraRepository;



    @Autowired
    private PacienteRepository pacienteRepository;

    @PostMapping("/guardar")
    public String guardarCompra(@RequestParam double total, Principal principal) {
        // Buscar el paciente por el correo
        String correo = principal.getName();
        Optional<Paciente> pacienteOpt = pacienteRepository.findByUsuario_Correo(correo);

        if (pacienteOpt.isEmpty()) {
            return "Paciente no encontrado";
        }

        Compra compra = new Compra();
        compra.setPaciente(pacienteOpt.get());
        compra.setFechaCompra(LocalDate.now());
        compra.setTotal(total);

        compraRepository.save(compra);
        return "Compra guardada exitosamente";
    }
}
