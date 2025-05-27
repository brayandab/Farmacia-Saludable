package com.example.Correccion.farmacia.contollers.API;

import com.example.Correccion.farmacia.entities.Producto;
import com.example.Correccion.farmacia.repository.ProductoRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@Api(value = "Productos", description = "Operaciones relacionadas con los productos")
public class ProductoController {

    private final ProductoRepository repository;

    public ProductoController(ProductoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public java.util.List<Producto> obtenerTodos() {
        return repository.findAll();
    }

    @PostMapping("/crear")
    public Producto guardar(@RequestBody Producto producto) {
        return repository.save(producto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado."));
    }

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

    @PutMapping("/actualizar-stock-fecha/{id}")
    public ResponseEntity<?> actualizarStockYFecha(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Producto> optionalProducto = repository.findById(id);
        if (optionalProducto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        }

        Producto producto = optionalProducto.get();

        try {
            if (updates.containsKey("stock")) {
                Object stockObj = updates.get("stock");
                int stock = (stockObj instanceof Number)
                        ? ((Number) stockObj).intValue()
                        : Integer.parseInt(stockObj.toString());
                producto.setStock(stock);
            }

            if (updates.containsKey("fechaVencimiento")) {
                String fechaStr = updates.get("fechaVencimiento").toString();
                producto.setFechaVencimiento(LocalDate.parse(fechaStr));
            }

            Producto actualizado = repository.save(producto);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Datos inválidos: " + e.getMessage());
        }
    }

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
