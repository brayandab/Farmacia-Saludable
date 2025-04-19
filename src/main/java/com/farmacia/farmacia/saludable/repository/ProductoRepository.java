package com.farmacia.farmacia.saludable.repository;

import com.farmacia.farmacia.saludable.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository <Producto, Long> {

}
