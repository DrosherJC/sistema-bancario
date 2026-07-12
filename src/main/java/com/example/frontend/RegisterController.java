package com.example.frontend;

import com.example.frontend.model.Cliente;
import com.example.frontend.service.AuthService;
import com.example.frontend.util.Navegador;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField txfUsuario;
    @FXML private PasswordField txfPassword;
    @FXML private PasswordField txfPasswordConfirm;
    @FXML private TextField txfNombre;
    @FXML private TextField txfApellido;
    @FXML private TextField txfCedula;
    @FXML private TextField txfEmail;
    @FXML private TextField txfTelefono;
    @FXML private TextField txfDireccion;
    @FXML private Label lblMensaje;

    private final AuthService authService = new AuthService();

    @FXML
    private void onRegistrar() {
        String username = txfUsuario.getText();
        String password = txfPassword.getText();
        String confirm = txfPasswordConfirm.getText();

        if (!password.equals(confirm)) {
            mostrarError("Las contraseñas no coinciden");
            return;
        }

        Cliente datosCliente = new Cliente();
        datosCliente.setNombre(txfNombre.getText());
        datosCliente.setApellido(txfApellido.getText());
        datosCliente.setCedula(txfCedula.getText());
        datosCliente.setEmail(txfEmail.getText());
        datosCliente.setTelefono(txfTelefono.getText());
        datosCliente.setDireccion(txfDireccion.getText());

        try {
            AuthService.ResultadoRegistro resultado = authService.registrar(username, password, datosCliente);
            lblMensaje.setTextFill(javafx.scene.paint.Color.GREEN);
            lblMensaje.setText("Cuenta creada. Tu numero de cuenta es " + resultado.getNumeroCuenta() +
                    ". Ya puedes iniciar sesion.");
            limpiarCampos();
        } catch (IllegalArgumentException | AuthService.UsuarioExistenteException ex) {
            mostrarError(ex.getMessage());
        } catch (SQLException ex) {
            mostrarError("Error de conexion a la base de datos");
            ex.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txfUsuario.clear();
        txfPassword.clear();
        txfPasswordConfirm.clear();
        txfNombre.clear();
        txfApellido.clear();
        txfCedula.clear();
        txfEmail.clear();
        txfTelefono.clear();
        txfDireccion.clear();
    }

    @FXML
    private void onVolverALogin() {
        Navegador.cambiarEscena(txfUsuario, "LoginView.fxml", "Sistema Bancario - Login");
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setTextFill(javafx.scene.paint.Color.RED);
        lblMensaje.setText(mensaje);
    }
}

