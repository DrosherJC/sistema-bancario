package com.example.frontend.util;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class LimitadorCampos {

    private LimitadorCampos() {
    }

    // Limita el campo a un máximo de caracteres
    public static void limitarTexto(TextField campo, int maxCaracteres) {
        UnaryOperator<TextFormatter.Change> filtro = change ->
                change.getControlNewText().length() > maxCaracteres ? null : change;
        campo.setTextFormatter(new TextFormatter<>(filtro));
    }

    // Solo dígitos, hasta maxDigitos
    public static void limitarSoloNumeros(TextField campo, int maxDigitos) {
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String textoNuevo = change.getControlNewText();
            if (!textoNuevo.matches("\\d{0," + maxDigitos + "}")) {
                return null;
            }
            return change;
        };
        campo.setTextFormatter(new TextFormatter<>(filtro));
    }

    // Números decimales para montos,saldos
    public static void limitarDecimal(TextField campo, int maxEnteros, int maxDecimales) {
        String regex = "\\d{0," + maxEnteros + "}(\\.\\d{0," + maxDecimales + "})?";
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String textoNuevo = change.getControlNewText();
            if (textoNuevo.isEmpty() || textoNuevo.matches(regex)) {
                return change;
            }
            return null;
        };
        campo.setTextFormatter(new TextFormatter<>(filtro));
    }

    // Solo letras y espacios, hasta maxCaracteres
    public static void limitarSoloLetras(TextField campo, int maxCaracteres) {
        String regex = "[\\p{L} ]{0," + maxCaracteres + "}";
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String textoNuevo = change.getControlNewText();
            if (textoNuevo.isEmpty() || textoNuevo.matches(regex)) {
                return change;
            }
            return null;
        };
        campo.setTextFormatter(new TextFormatter<>(filtro));
    }
}