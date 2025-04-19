package com.farmacia.farmacia.saludable.controller;

import com.farmacia.farmacia.saludable.entities.Producto;
import com.farmacia.farmacia.saludable.repository.ProductoRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Api(value = "Productos", description = "Operaciones relacionadas con los productos")
public class ProductoController {

    private final ProductoRepository repository;

    public ProductoController(ProductoRepository repository) {
        this.repository = repository;
    }

    @ApiOperation(value = "Obtener todos los productos")
    @GetMapping
    public List<Producto> obtenerTodos() {
        return repository.findAll();
    }

    @ApiOperation(value = "Crear un nuevo producto")
    @PostMapping("/crear")
    public Producto guardar(@RequestBody Producto producto) {
        return repository.save(producto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return repository.findById(id).<ResponseEntity<?>>map(producto ->
                ResponseEntity.ok(producto)
        ).orElseGet(() ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.")
        );
    }





    @ApiOperation(value = "Actualizar un producto existente")
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        return repository.findById(id)
                .map(existingProducto -> {
                    existingProducto.setNombre(producto.getNombre());
                    existingProducto.setDescripcion(producto.getDescripcion());
                    existingProducto.setProveedor(producto.getProveedor());
                    existingProducto.setLote(producto.getLote());
                    existingProducto.setFechaVencimiento(producto.getFechaVencimiento());
                    existingProducto.setStock(producto.getStock());
                    existingProducto.setPrecioUnitario(producto.getPrecioUnitario());
                    return repository.save(existingProducto);
                })
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @ApiOperation(value = "Eliminar un producto por su ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok("Producto eliminado correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        }
    }

}