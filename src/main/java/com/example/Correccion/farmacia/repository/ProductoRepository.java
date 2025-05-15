package com.example.Correccion.farmacia.repository;



import com.example.Correccion.farmacia.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository <Producto, Long> {

}
