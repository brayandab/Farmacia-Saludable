package com.example.Correccion.farmacia.dto;

import java.util.List;

public class ProductosRequest {
    private List<ProductoCarritoDTO> productos;

    public List<ProductoCarritoDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoCarritoDTO> productos) {
        this.productos = productos;
    }
}
