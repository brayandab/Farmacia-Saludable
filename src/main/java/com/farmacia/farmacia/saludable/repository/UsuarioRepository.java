package com.farmacia.farmacia.saludable.repository;

import com.farmacia.farmacia.saludable.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional findByCorreo(String correo);  // Añadir este método para encontrar un usuario por correo
}
