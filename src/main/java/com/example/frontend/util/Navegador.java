package com.example.frontend.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Navegador {

    private Navegador() {
    }

    public static void cambiarEscena(Node origen, String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(Navegador.class.getResource(
                    "/com/example/frontend/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) origen.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la vista " + fxml, e);
        }
    }
}
