package com.example.Correccion.farmacia.services;

import com.example.Correccion.farmacia.entities.Compra;
import com.example.Correccion.farmacia.repository.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CompraService {

    private final CompraRepository compraRepository;

    @Autowired
    public CompraService(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public Compra guardarCompra(Compra compra) {
        if (compra.getFechaCompra() == null) {
            compra.setFechaCompra(LocalDate.now());
        }
        return compraRepository.save(compra);
    }

    // Nuevo método para obtener todas las compras
    public List<Compra> obtenerTodasLasCompras() {
        return compraRepository.findAll();
    }
}