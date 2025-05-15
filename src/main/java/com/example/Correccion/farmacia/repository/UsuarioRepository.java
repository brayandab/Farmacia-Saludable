package com.example.Correccion.farmacia.repository;



import com.example.Correccion.farmacia.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombre(String nombre);

    Optional<Usuario> findByCorreo(String correo);  // Añadir este método para encontrar un usuario por correo
}
