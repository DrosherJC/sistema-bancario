package com.example.frontend.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConexionBD {

    private static final Properties propiedades = new Properties();

    static {
        try (InputStream input = ConexionBD.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontraron las credenciales");
            }
            propiedades.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo las credenciales", e);
        }
    }

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        String url = propiedades.getProperty("db.url");
        String user = propiedades.getProperty("db.user");
        String password = propiedades.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }
}
