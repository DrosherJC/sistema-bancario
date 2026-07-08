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
