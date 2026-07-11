package com.example.frontend.service;

import com.example.frontend.dao.UsuarioDAO;
import com.example.frontend.model.Rol;
import com.example.frontend.model.Usuario;

import java.sql.SQLException;
import java.util.List;


public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public List<Usuario> listarTodos() throws SQLException {
        return usuarioDAO.listarTodos();
    }

    public void cambiarRol(Usuario actor, Long usuarioObjetivoId, Rol nuevoRol) throws SQLException {
        if (actor == null || actor.getRol() != Rol.ADMINISTRADOR) {
            throw new SecurityException("Solo un administrador puede cambiar roles");
        }
        if (actor.getId().equals(usuarioObjetivoId) && nuevoRol != Rol.ADMINISTRADOR) {
            throw new IllegalArgumentException("No puedes quitarte tu propio rol de administrador");
        }
        usuarioDAO.actualizarRol(usuarioObjetivoId, nuevoRol);
    }
}