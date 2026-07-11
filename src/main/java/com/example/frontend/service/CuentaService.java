package com.example.frontend.service;

import com.example.frontend.dao.ClienteDAO;
import com.example.frontend.dao.CuentaDAO;
import com.example.frontend.model.Cuenta;
import com.example.frontend.model.EstadoCuenta;
import com.example.frontend.model.TipoCuenta;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CuentaService {

    private final CuentaDAO cuentaDAO = new CuentaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    public List<Cuenta> listarTodos() throws SQLException {
        return cuentaDAO.listarTodos();
    }

    public List<Cuenta> listarPorCliente(Long clienteId) throws SQLException {
        return cuentaDAO.listarPorCliente(clienteId);
    }

    public void crear(Cuenta cuenta) throws SQLException {
        validar(cuenta, true);
        if (cuentaDAO.buscarPorNumero(cuenta.getNumeroCuenta()) != null) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese numero");
        }
        if (clienteDAO.buscarPorId(cuenta.getClienteId()) == null) {
            throw new IllegalArgumentException("El cliente seleccionado no existe");
        }
        if (cuenta.getEstado() == null) {
            cuenta.setEstado(EstadoCuenta.ACTIVA);
        }
        cuentaDAO.guardar(cuenta);
    }

    public void actualizar(Cuenta cuenta) throws SQLException {
        validar(cuenta, false);
        Cuenta existente = cuentaDAO.buscarPorNumero(cuenta.getNumeroCuenta());
        if (existente != null && !existente.getId().equals(cuenta.getId())) {
            throw new IllegalArgumentException("Ya existe otra cuenta con ese numero");
        }
        cuentaDAO.actualizar(cuenta);
    }

    public void eliminar(Long id) throws SQLException {
        cuentaDAO.eliminar(id);
    }


    public Cuenta abrirCuentaInicial(Long clienteId) throws SQLException {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(generarNumeroCuentaUnico());
        cuenta.setClienteId(clienteId);
        cuenta.setTipo(TipoCuenta.AHORROS);
        cuenta.setSaldo(BigDecimal.ZERO);
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuentaDAO.guardar(cuenta);
        return cuenta;
    }

    private String generarNumeroCuentaUnico() throws SQLException {
        String numero;
        do {
            numero = generarNumeroAleatorioDe10Digitos();
        } while (cuentaDAO.buscarPorNumero(numero) != null);
        return numero;
    }

    private String generarNumeroAleatorioDe10Digitos() {
        long min = 1_000_000_000L;
        long max = 9_999_999_999L;
        long numero = min + (long) (Math.random() * (max - min));
        return String.valueOf(numero);
    }

    private void validar(Cuenta cuenta, boolean esNueva) {
        if (cuenta.getNumeroCuenta() == null || !cuenta.getNumeroCuenta().matches("\\d{10}")) {
            throw new IllegalArgumentException("El numero de cuenta debe tener exactamente 10 digitos");
        }
        if (cuenta.getClienteId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un cliente");
        }
        if (cuenta.getTipo() == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de cuenta");
        }
        if (esNueva) {
            if (cuenta.getSaldo() == null || cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
            }
        }
    }
}
