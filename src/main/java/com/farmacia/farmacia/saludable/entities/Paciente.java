package com.farmacia.farmacia.saludable.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pacientes")

public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPaciente;

    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "idUsuario")
    //@JsonIgnore
    //@JsonBackReference
    private Usuario usuario;

    private String historialMedico;
    private String alergias;
    private String medicamentosRecetados;
    private String otraInformacionRelevante;


    // getters y setters


    public Integer getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Integer idPaciente) {
        this.idPaciente = idPaciente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getHistorialMedico() {
        return historialMedico;
    }

    public void setHistorialMedico(String historialMedico) {
        this.historialMedico = historialMedico;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getMedicamentosRecetados() {
        return medicamentosRecetados;
    }

    public void setMedicamentosRecetados(String medicamentosRecetados) {
        this.medicamentosRecetados = medicamentosRecetados;
    }

    public String getOtraInformacionRelevante() {
        return otraInformacionRelevante;
    }

    public void setOtraInformacionRelevante(String otraInformacionRelevante) {
        this.otraInformacionRelevante = otraInformacionRelevante;
    }
}
