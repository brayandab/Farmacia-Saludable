package com.example.Correccion.farmacia.dto;

import com.example.Correccion.farmacia.entities.Paciente;
import com.example.Correccion.farmacia.entities.Usuario;
import com.example.Correccion.farmacia.entities.Compra;

import java.util.List;

public class UsuarioPacienteCompraDTO {

    private Usuario usuario;


    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


}
