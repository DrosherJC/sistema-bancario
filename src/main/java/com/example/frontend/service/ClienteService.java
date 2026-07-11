package com.example.frontend.service;

import com.example.frontend.dao.ClienteDAO;
import com.example.frontend.model.Cliente;

import java.sql.SQLException;
import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public List<Cliente> listarTodos() throws SQLException {
        return clienteDAO.listarTodos();
    }

    public Cliente buscarPorUsuarioId(Long usuarioId) throws SQLException {
        return clienteDAO.buscarPorUsuarioId(usuarioId);
    }

    public void crear(Cliente cliente) throws SQLException {
        validar(cliente);
        if (cliente.getUsuarioId() == null) {
            throw new IllegalArgumentException(
                    "Todo cliente necesita un usuario. Los clientes se crean desde la pantalla de Registro.");
        }
        if (clienteDAO.buscarPorCedula(cliente.getCedula()) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con esa cedula");
        }
        clienteDAO.guardar(cliente);
    }

    public void actualizar(Cliente cliente) throws SQLException {
        validar(cliente);
        Cliente existente = clienteDAO.buscarPorCedula(cliente.getCedula());
        if (existente != null && !existente.getId().equals(cliente.getId())) {
            throw new IllegalArgumentException("Ya existe otro cliente con esa cedula");
        }
        clienteDAO.actualizar(cliente);
    }

    public void eliminar(Long id) throws SQLException {
        clienteDAO.eliminar(id);
    }

    private void validar(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (cliente.getApellido() == null || cliente.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
        if (cliente.getCedula() == null || !cliente.getCedula().matches("\\d{10}")) {
            throw new IllegalArgumentException("La cedula debe tener 10 digitos numericos");
        }
        if (cliente.getEmail() == null || !cliente.getEmail().contains("@")) {
            throw new IllegalArgumentException("El email debe contener una arroba (@)");
        }
        if (cliente.getTelefono() == null || !cliente.getTelefono().matches("\\d{10}")) {
            throw new IllegalArgumentException("El telefono debe tener exactamente 10 digitos");
        }
    }
}