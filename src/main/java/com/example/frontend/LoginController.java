package com.example.frontend;

import com.example.frontend.model.Usuario;
import com.example.frontend.service.AuthService;
import com.example.frontend.util.LimitadorCampos;
import com.example.frontend.util.Navegador;
import com.example.frontend.util.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class LoginController {

    @FXML private TextField txfUsuario;
    @FXML private PasswordField txfPassword;
    @FXML private Label lblError;

    private final AuthService authService = new AuthService();

    @FXML
    private void initialize() {
        LimitadorCampos.limitarTexto(txfUsuario, 30);
        LimitadorCampos.limitarTexto(txfPassword, 30);
    }

    @FXML
    private void onLogin() {
        String username = txfUsuario.getText();
        String password = txfPassword.getText();

        if (username.isBlank() || password.isBlank()) {
            lblError.setText("Usuario y contraseña son obligatorios");
            return;
        }

        try {
            Usuario usuario = authService.autenticar(username, password);
            lblError.setText("");
            Sesion.iniciar(usuario);
            Navegador.cambiarEscena(txfUsuario, "DashboardView.fxml", "Sistema Bancario - Dashboard");
        } catch (AuthService.CredencialesInvalidasException ex) {
            lblError.setText(ex.getMessage());
        } catch (SQLException ex) {
            lblError.setText("Error de conexion a la base de datos");
            ex.printStackTrace();
        }
    }

    @FXML
    private void onIrARegistro() {
        Navegador.cambiarEscena(txfUsuario, "RegisterView.fxml", "Sistema Bancario - Registro");
    }
}