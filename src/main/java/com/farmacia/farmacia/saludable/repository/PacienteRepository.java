package com.farmacia.farmacia.saludable.repository;

import com.farmacia.farmacia.saludable.entities.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


    @Repository
    public interface PacienteRepository extends JpaRepository<Paciente, Long> {
        Paciente findByUsuario_IdUsuario(Long idUsuario);
    }

