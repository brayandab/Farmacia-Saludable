package com.farmacia.farmacia.saludable.repository;

import com.farmacia.farmacia.saludable.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByCorreo(String correo);  // Añadir este método para encontrar un usuario por correo
}
