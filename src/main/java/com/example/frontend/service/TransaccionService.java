package com.example.frontend.service;

import com.example.frontend.dao.CuentaDAO;
import com.example.frontend.dao.TransaccionDAO;
import com.example.frontend.model.Cuenta;
import com.example.frontend.model.EstadoCuenta;
import com.example.frontend.model.Transaccion;
import com.example.frontend.model.TipoTransaccion;
import com.example.frontend.util.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;


public class TransaccionService {

    private final CuentaDAO cuentaDAO = new CuentaDAO();
    private final TransaccionDAO transaccionDAO = new TransaccionDAO();

    public List<Transaccion> listarTodos() throws SQLException {
        return transaccionDAO.listarTodos();
    }

    public List<Transaccion> listarPorCuenta(Long cuentaId) throws SQLException {
        return transaccionDAO.listarPorCuenta(cuentaId);
    }

    public List<Transaccion> listarPorCliente(Long clienteId) throws SQLException {
        return transaccionDAO.listarPorCliente(clienteId);
    }

    public void depositar(Long cuentaId, BigDecimal monto, Long usuarioId) throws SQLException {
        validarMonto(monto);

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                Cuenta cuenta = obtenerCuentaActiva(cuentaId, con);
                BigDecimal nuevoSaldo = cuenta.getSaldo().add(monto);
                cuentaDAO.actualizarSaldo(cuentaId, nuevoSaldo, con);

                Transaccion t = new Transaccion(null, TipoTransaccion.DEPOSITO, monto,
                        null, cuentaId, LocalDateTime.now(), usuarioId);
                transaccionDAO.guardar(t, con);

                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void retirar(Long cuentaId, BigDecimal monto, Long usuarioId) throws SQLException {
        validarMonto(monto);

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                Cuenta cuenta = obtenerCuentaActiva(cuentaId, con);
                if (cuenta.getSaldo().compareTo(monto) < 0) {
                    throw new IllegalArgumentException("Saldo insuficiente. Disponible: " + cuenta.getSaldo());
                }
                BigDecimal nuevoSaldo = cuenta.getSaldo().subtract(monto);
                cuentaDAO.actualizarSaldo(cuentaId, nuevoSaldo, con);

                Transaccion t = new Transaccion(null, TipoTransaccion.RETIRO, monto,
                        cuentaId, null, LocalDateTime.now(), usuarioId);
                transaccionDAO.guardar(t, con);

                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void transferir(Long cuentaOrigenId, Long cuentaDestinoId, BigDecimal monto, Long usuarioId)
            throws SQLException {
        validarMonto(monto);
        if (cuentaOrigenId.equals(cuentaDestinoId)) {
            throw new IllegalArgumentException("La cuenta origen y destino no pueden ser la misma");
        }

        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                Cuenta origen = obtenerCuentaActiva(cuentaOrigenId, con);
                Cuenta destino = obtenerCuentaActiva(cuentaDestinoId, con);

                if (origen.getSaldo().compareTo(monto) < 0) {
                    throw new IllegalArgumentException("Saldo insuficiente. Disponible: " + origen.getSaldo());
                }

                cuentaDAO.actualizarSaldo(cuentaOrigenId, origen.getSaldo().subtract(monto), con);
                cuentaDAO.actualizarSaldo(cuentaDestinoId, destino.getSaldo().add(monto), con);

                Transaccion t = new Transaccion(null, TipoTransaccion.TRANSFERENCIA, monto,
                        cuentaOrigenId, cuentaDestinoId, LocalDateTime.now(), usuarioId);
                transaccionDAO.guardar(t, con);

                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private Cuenta obtenerCuentaActiva(Long cuentaId, Connection con) throws SQLException {
        Cuenta cuenta = cuentaDAO.buscarPorIdParaActualizar(cuentaId, con);
        if (cuenta == null) {
            throw new IllegalArgumentException("La cuenta no existe");
        }
        if (cuenta.getEstado() != EstadoCuenta.ACTIVA) {
            throw new IllegalArgumentException("La cuenta " + cuenta.getNumeroCuenta() + " esta inactiva");
        }
        return cuenta;
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
    }
}
