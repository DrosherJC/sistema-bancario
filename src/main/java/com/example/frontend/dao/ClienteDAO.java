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

    public void guardar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, apellido, cedula, email, telefono, direccion, usuario_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getCedula());
            ps.setString(4, cliente.getEmail());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getDireccion());
            ps.setLong(7, cliente.getUsuarioId());
            ps.executeUpdate();
        }
    }

    public void actualizar(Cliente cliente) throws SQLException {

        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, cedula = ?, email = ?, " +
                "telefono = ?, direccion = ? WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getCedula());
            ps.setString(4, cliente.getEmail());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getDireccion());
            ps.setLong(7, cliente.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(Long id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
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
