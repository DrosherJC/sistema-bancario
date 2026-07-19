package com.example.frontend;

import com.example.frontend.util.Navegador;
import com.example.frontend.util.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label lblUser;
    @FXML private Button btnClientes;
    @FXML private Button btnCuentas;
    @FXML private Button btnTransacciones;
    @FXML private Button btnUsuarios;
    @FXML private Button btnLogout;

    @FXML
    private void initialize() {
        var usuario = Sesion.getUsuarioActual();
        lblUser.setText("Bienvenido " + usuario.getUsername() + "! Rol: " + usuario.getRol());

        boolean esAdmin = Sesion.esAdministrador();

        btnClientes.setVisible(esAdmin);
        btnClientes.setManaged(esAdmin);
        btnCuentas.setVisible(esAdmin);
        btnCuentas.setManaged(esAdmin);
        btnUsuarios.setVisible(esAdmin);
        btnUsuarios.setManaged(esAdmin);

    }

    @FXML
    private void onClientes() {
        Navegador.cambiarEscena(btnClientes, "ClientesView.fxml", "Sistema Bancario - Clientes");
    }

    @FXML
    private void onCuentas() {
        Navegador.cambiarEscena(btnCuentas, "CuentasView.fxml", "Sistema Bancario - Cuentas");
    }

    @FXML
    private void onTransacciones() {
        Navegador.cambiarEscena(btnTransacciones, "TransaccionesView.fxml", "Sistema Bancario - Transacciones");
    }

    @FXML
    private void onUsuarios() {
        Navegador.cambiarEscena(btnUsuarios, "UsuariosView.fxml", "Sistema Bancario - Usuarios");
    }

    @FXML
    private void onLogout() {
        Sesion.cerrar();
        Navegador.cambiarEscena(btnLogout, "LoginView.fxml", "Sistema Bancario - Login");
    }
}
