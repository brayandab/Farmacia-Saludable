package com.example.Correccion.farmacia.contollers.API;



import com.example.Correccion.farmacia.entities.Producto;
import com.example.Correccion.farmacia.repository.ProductoRepository;
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
    //Trae todos los productos
    @GetMapping
    public List<Producto> obtenerTodos() {
        return repository.findAll();
    }

    @ApiOperation(value = "Crear un nuevo producto")
    //Crea nuevos productos
    @PostMapping("/crear")
    public Producto guardar(@RequestBody Producto producto) {
        return repository.save(producto);
    }

    //Busca el producto asociado
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return repository.findById(id).<ResponseEntity<?>>map(producto ->
                ResponseEntity.ok(producto)
        ).orElseGet(() ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.")
        );
    }




    //Actualiza el producto por id
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

    //Elimina el producto por id
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