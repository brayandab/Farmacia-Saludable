package com.example.Correccion.farmacia.repository;
import com.example.Correccion.farmacia.entities.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
    public interface PacienteRepository extends JpaRepository<Paciente, Long> {
// Repositorio que permite acceder y gestionar los datos de pacientes desde la base de datos.
        Paciente findByUsuario_IdUsuario(Long idUsuario);

    Optional<Paciente> findByUsuario_Correo(String correo);


    //Optional<Paciente> findByCorreo(String correo);
    }


