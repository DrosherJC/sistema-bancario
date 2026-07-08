package com.example.frontend.dao;

import com.example.frontend.model.Cliente;
import com.example.frontend.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private static final String COLUMNAS =
            "id, nombre, apellido, cedula, email, telefono, direccion, usuario_id ";

    public List<Cliente> listarTodos() throws SQLException {
        String sql = "SELECT " + COLUMNAS + "FROM clientes ORDER BY apellido, nombre";
        List<Cliente> resultado = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        }
        return resultado;
    }

    public Cliente buscarPorId(Long id) throws SQLException {
        String sql = "SELECT " + COLUMNAS + "FROM clientes WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public Cliente buscarPorCedula(String cedula) throws SQLException {
        String sql = "SELECT " + COLUMNAS + "FROM clientes WHERE cedula = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }


    public Cliente buscarPorUsuarioId(Long usuarioId) throws SQLException {
        String sql = "SELECT " + COLUMNAS + "FROM clientes WHERE usuario_id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("cedula"),
                rs.getString("email"),
                rs.getString("telefono"),
                rs.getString("direccion")
        );
        long usuarioId = rs.getLong("usuario_id");
        cliente.setUsuarioId(rs.wasNull() ? null : usuarioId);
        return cliente;
    }
}
