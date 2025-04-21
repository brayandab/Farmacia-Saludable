package com.farmacia.farmacia.saludable.dto;

import com.farmacia.farmacia.saludable.entities.Paciente;
import com.farmacia.farmacia.saludable.entities.Usuario;

public class UsuarioPacienteDTO {
    private Usuario usuario;
    private Paciente paciente;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }


}

