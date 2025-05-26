package com.example.Correccion.farmacia.services;

import com.example.Correccion.farmacia.entities.Compra;
import com.example.Correccion.farmacia.repository.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CompraService {

    private final CompraRepository compraRepository;

    @Autowired
    public CompraService(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public Compra guardarCompra(Compra compra) {
        // Coloca la fecha actual si no está definida
        if (compra.getFechaCompra() == null) {
            compra.setFechaCompra(LocalDate.now());
        }

        return compraRepository.save(compra);
    }

    // Puedes agregar más métodos para buscar o listar compras si quieres
}
