package com.example.Correccion.farmacia.dto;

import java.util.List;

public class CompraDTO {
    private double total;
    private List<ProductoCarritoDTO> productos;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<ProductoCarritoDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoCarritoDTO> productos) {
        this.productos = productos;
    }
}
