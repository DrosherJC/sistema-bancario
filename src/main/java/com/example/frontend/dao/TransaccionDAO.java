package com.example.frontend.dao;

import com.example.frontend.model.Transaccion;
import com.example.frontend.model.TipoTransaccion;
import com.example.frontend.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransaccionDAO {

    private static final String SELECT_BASE =
            "SELECT t.id, t.tipo, t.monto, t.cuenta_origen_id, t.cuenta_destino_id, t.fecha, t.usuario_id, " +
                    "       co.numero_cuenta AS origen_numero, cd.numero_cuenta AS destino_numero, " +
                    "       clo.nombre AS origen_cliente_nombre, clo.apellido AS origen_cliente_apellido, " +
                    "       cld.nombre AS destino_cliente_nombre, cld.apellido AS destino_cliente_apellido " +
                    "FROM transacciones t " +
                    "LEFT JOIN cuentas co ON co.id = t.cuenta_origen_id " +
                    "LEFT JOIN cuentas cd ON cd.id = t.cuenta_destino_id " +
                    "LEFT JOIN clientes clo ON clo.id = co.cliente_id " +
                    "LEFT JOIN clientes cld ON cld.id = cd.cliente_id ";

    /** Se usa dentro de una transaccion JDBC ya abierta (autoCommit=false). */
    public void guardar(Transaccion transaccion, Connection con) throws SQLException {
        String sql = "INSERT INTO transacciones (tipo, monto, cuenta_origen_id, cuenta_destino_id, fecha, usuario_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, transaccion.getTipo().name());
            ps.setBigDecimal(2, transaccion.getMonto());
            if (transaccion.getCuentaOrigenId() != null) {
                ps.setLong(3, transaccion.getCuentaOrigenId());
            } else {
                ps.setNull(3, java.sql.Types.BIGINT);
            }
            if (transaccion.getCuentaDestinoId() != null) {
                ps.setLong(4, transaccion.getCuentaDestinoId());
            } else {
                ps.setNull(4, java.sql.Types.BIGINT);
            }
            ps.setTimestamp(5, Timestamp.valueOf(transaccion.getFecha()));
            ps.setLong(6, transaccion.getUsuarioId());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    transaccion.setId(keys.getLong(1));
                }
            }
        }
    }

    public List<Transaccion> listarTodos() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY t.fecha DESC";
        List<Transaccion> resultado = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        }
        return resultado;
    }

    /**
     * Historial de un cliente puntual: cualquier transaccion donde alguna de
     * sus cuentas participo, sea como origen o como destino. Es lo que ve el
     * usuario estandar en "sus transacciones".
     */
    public List<Transaccion> listarPorCliente(Long clienteId) throws SQLException {
        String sql = SELECT_BASE +
                "WHERE co.cliente_id = ? OR cd.cliente_id = ? " +
                "ORDER BY t.fecha DESC";
        List<Transaccion> resultado = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, clienteId);
            ps.setLong(2, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        }
        return resultado;
    }

    public List<Transaccion> listarPorCuenta(Long cuentaId) throws SQLException {
        String sql = SELECT_BASE + "WHERE t.cuenta_origen_id = ? OR t.cuenta_destino_id = ? " +
                "ORDER BY t.fecha DESC";
        List<Transaccion> resultado = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, cuentaId);
            ps.setLong(2, cuentaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapear(rs));
                }
            }
        }
        return resultado;
    }

    private Transaccion mapear(ResultSet rs) throws SQLException {
        Transaccion t = new Transaccion();
        t.setId(rs.getLong("id"));
        t.setTipo(TipoTransaccion.valueOf(rs.getString("tipo")));
        t.setMonto(rs.getBigDecimal("monto"));
        long origenId = rs.getLong("cuenta_origen_id");
        t.setCuentaOrigenId(rs.wasNull() ? null : origenId);
        long destinoId = rs.getLong("cuenta_destino_id");
        t.setCuentaDestinoId(rs.wasNull() ? null : destinoId);
        t.setCuentaOrigenNumero(rs.getString("origen_numero"));
        t.setCuentaDestinoNumero(rs.getString("destino_numero"));
        t.setClienteOrigenNombre(nombreCompleto(rs.getString("origen_cliente_nombre"), rs.getString("origen_cliente_apellido")));
        t.setClienteDestinoNombre(nombreCompleto(rs.getString("destino_cliente_nombre"), rs.getString("destino_cliente_apellido")));
        t.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        t.setUsuarioId(rs.getLong("usuario_id"));
        return t;
    }

    private String nombreCompleto(String nombre, String apellido) {
        return nombre == null ? null : nombre + " " + apellido;
    }
}
