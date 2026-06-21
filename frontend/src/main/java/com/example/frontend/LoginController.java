package com.example.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField txfUsuario;
    @FXML private TextField txfPassword;
    @FXML private Label lblError;

    private String username = "admin"; // un
    private String userpassword = "admin"; // up

    public void prueba() {
        String unRead = txfUsuario.getText();
        String upRead = txfPassword.getText();

        if (unRead.equals(username) && upRead.equals(userpassword)) {
            lblError.setText("CORRECTO!");
        } else {
            lblError.setText("ERROR!");
        }
    }
}
