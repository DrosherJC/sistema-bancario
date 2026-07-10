package com.example.frontend.dao;

import com.example.frontend.model.Cuenta;
import com.example.frontend.model.EstadoCuenta;
import com.example.frontend.model.TipoCuenta;
import com.example.frontend.util.ConexionBD;

import java.math.BigDecimal;
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

    public List<Cuenta> listarPorCliente(Long clienteId) throws SQLException {
        String sql = SELECT_BASE + "WHERE c.cliente_id = ? ORDER BY c.numero_cuenta";
        List<Cuenta> resultado = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        }
        return resultado;
    }

    public Cuenta buscarPorId(Long id) throws SQLException {
        String sql = SELECT_BASE + "WHERE c.id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public Cuenta buscarPorNumero(String numeroCuenta) throws SQLException {
        String sql = SELECT_BASE + "WHERE c.numero_cuenta = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numeroCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public Cuenta buscarPorIdParaActualizar(Long id, Connection con) throws SQLException {
        String sql = "SELECT id, numero_cuenta, cliente_id, tipo, saldo, estado " +
                "FROM cuentas WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Cuenta cuenta = new Cuenta();
                cuenta.setId(rs.getLong("id"));
                cuenta.setNumeroCuenta(rs.getString("numero_cuenta"));
                cuenta.setClienteId(rs.getLong("cliente_id"));
                cuenta.setTipo(TipoCuenta.valueOf(rs.getString("tipo")));
                cuenta.setSaldo(rs.getBigDecimal("saldo"));
                cuenta.setEstado(EstadoCuenta.valueOf(rs.getString("estado")));
                return cuenta;
            }
        }
    }
    public void guardar(Cuenta cuenta) throws SQLException {
        String sql = "INSERT INTO cuentas (numero_cuenta, cliente_id, tipo, saldo, estado) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cuenta.getNumeroCuenta());
            ps.setLong(2, cuenta.getClienteId());
            ps.setString(3, cuenta.getTipo().name());
            ps.setBigDecimal(4, cuenta.getSaldo());
            ps.setString(5, cuenta.getEstado().name());
            ps.executeUpdate();
        }
    }

    public void actualizar(Cuenta cuenta) throws SQLException {
        String sql = "UPDATE cuentas SET numero_cuenta = ?, cliente_id = ?, tipo = ?, estado = ? " +
                "WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cuenta.getNumeroCuenta());
            ps.setLong(2, cuenta.getClienteId());
            ps.setString(3, cuenta.getTipo().name());
            ps.setString(4, cuenta.getEstado().name());
            ps.setLong(5, cuenta.getId());
            ps.executeUpdate();
        }
    }

    public void actualizarSaldo(Long cuentaId, BigDecimal nuevoSaldo, Connection con) throws SQLException {
        String sql = "UPDATE cuentas SET saldo = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, nuevoSaldo);
            ps.setLong(2, cuentaId);
            ps.executeUpdate();
        }
    }

    public void eliminar(Long id) throws SQLException {
        String sql = "DELETE FROM cuentas WHERE id = ?";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
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
