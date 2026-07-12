package com.example.frontend.service;

import com.example.frontend.dao.UsuarioDAO;
import com.example.frontend.model.Cliente;
import com.example.frontend.model.Cuenta;
import com.example.frontend.model.Rol;
import com.example.frontend.model.Usuario;
import com.example.frontend.util.PasswordUtil;

import java.sql.SQLException;

public class AuthService {

    private final UsuarioDAO usuarioDAO;
    private final ClienteService clienteService;
    private final CuentaService cuentaService;

    public AuthService() {
        this.usuarioDAO = new UsuarioDAO();
        this.clienteService = new ClienteService();
        this.cuentaService = new CuentaService();
    }


    public Usuario autenticar(String username, String password) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorUsername(username);

        if (usuario == null) {
            throw new CredencialesInvalidasException();
        }

        boolean passwordCorrecta = PasswordUtil.verificar(password, usuario.getPasswordHash());
        if (!passwordCorrecta) {
            throw new CredencialesInvalidasException();
        }

        return usuario;
    }


    public ResultadoRegistro registrar(String username, String password, Cliente datosCliente) throws SQLException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        if (usuarioDAO.existeUsername(username)) {
            throw new UsuarioExistenteException();
        }

        String hash = PasswordUtil.hashear(password);
        Usuario nuevoUsuario = new Usuario(null, username, hash, Rol.ESTANDAR);
        usuarioDAO.guardar(nuevoUsuario);

        Cliente clienteCreado = null;
        try {
            datosCliente.setUsuarioId(nuevoUsuario.getId());
            clienteService.crear(datosCliente);

            clienteCreado = clienteService.buscarPorUsuarioId(nuevoUsuario.getId());

            Cuenta cuentaCreada = cuentaService.abrirCuentaInicial(clienteCreado.getId());

            return new ResultadoRegistro(nuevoUsuario, cuentaCreada.getNumeroCuenta());
        } catch (RuntimeException | SQLException e) {

            if (clienteCreado != null) {
                clienteService.eliminar(clienteCreado.getId());
            }
            usuarioDAO.eliminar(nuevoUsuario.getId());
            throw e;
        }
    }

    public static class CredencialesInvalidasException extends RuntimeException {
        public CredencialesInvalidasException() {
            super("Usuario o contraseña incorrectos");
        }
    }

    public static class UsuarioExistenteException extends RuntimeException {
        public UsuarioExistenteException() {
            super("Ese nombre de usuario no esta disponible, intenta con otro");
        }
    }

    public static class ResultadoRegistro {
        private final Usuario usuario;
        private final String numeroCuenta;

        public ResultadoRegistro(Usuario usuario, String numeroCuenta) {
            this.usuario = usuario;
            this.numeroCuenta = numeroCuenta;
        }

        public Usuario getUsuario() {
            return usuario;
        }

        public String getNumeroCuenta() {
            return numeroCuenta;
        }
    }
}

