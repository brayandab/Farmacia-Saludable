package com.example.Correccion.farmacia.dto;


import com.example.Correccion.farmacia.entities.Paciente;
import com.example.Correccion.farmacia.entities.Usuario;

public class UsuarioPacienteDTO {
    // Clase que sirve para enviar y recibir datos de un usuario que esta relacionado con el paciente
    // entre el backend y el frontend.

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

