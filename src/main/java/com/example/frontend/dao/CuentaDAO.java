package com.example.frontend.dao;

import com.example.frontend.model.Cuenta;
import com.example.frontend.model.EstadoCuenta;
import com.example.frontend.model.TipoCuenta;
import com.example.frontend.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CuentaDAO {

    private static final String SELECT_BASE =
            "SELECT c.id, c.numero_cuenta, c.cliente_id, c.tipo, c.saldo, c.estado, " +
            "       cl.nombre AS cliente_nombre, cl.apellido AS cliente_apellido " +
            "FROM cuentas c JOIN clientes cl ON cl.id = c.cliente_id ";

    public List<Cuenta> listarTodos() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY c.numero_cuenta";
        List<Cuenta> resultado = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        }
        return resultado;
    }

    private Cuenta mapear(ResultSet rs) throws SQLException {
        Cuenta cuenta = new Cuenta();
        cuenta.setId(rs.getLong("id"));
        cuenta.setNumeroCuenta(rs.getString("numero_cuenta"));
        cuenta.setClienteId(rs.getLong("cliente_id"));
        cuenta.setTipo(TipoCuenta.valueOf(rs.getString("tipo")));
        cuenta.setSaldo(rs.getBigDecimal("saldo"));
        cuenta.setEstado(EstadoCuenta.valueOf(rs.getString("estado")));
        cuenta.setClienteNombre(rs.getString("cliente_nombre") + " " + rs.getString("cliente_apellido"));
        return cuenta;
    }
}
