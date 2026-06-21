package com.example.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader =
                new FXMLLoader(MainApp.class.getResource("LoginView.fxml"));
        // LoginView.fxml se puede reemplazar por otro de los .fxml para pruebas
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Sistema Bancario");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}