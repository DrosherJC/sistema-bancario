package com.example.frontend.dao;

import com.example.frontend.model.Rol;
import com.example.frontend.model.Usuario;
import com.example.frontend.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//
//  IMP RECCORDAR - DAO = Data Access Object. Es la UNICA clase que sabe escribir SQL
//  para la tabla "usuarios". El resto del programa nunca escribe SQL
//  directamente, siempre pasa por aqui.

public class UsuarioDAO {

    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, rol FROM usuarios WHERE username = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
                return null; // no existe ese usuario
            }
        }
    }

    public boolean existeUsername(String username) throws SQLException {
        return buscarPorUsername(username) != null;
    }

    public void guardar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (username, password_hash, rol) VALUES (?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPasswordHash());
            ps.setString(3, usuario.getRol().name());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setId(keys.getLong(1));
                }
            }
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT id, username, password_hash, rol FROM usuarios ORDER BY username";
        List<Usuario> resultado = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.add(mapearUsuario(rs));
            }
        }
        return resultado;
    }

    public void eliminar(Long id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public void actualizarRol(Long id, Rol nuevoRol) throws SQLException {
        String sql = "UPDATE usuarios SET rol = ? WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoRol.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                Rol.valueOf(rs.getString("rol"))
        );
    }
}