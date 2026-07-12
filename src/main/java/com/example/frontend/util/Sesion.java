package com.example.frontend.util;

import com.example.frontend.model.Rol;
import com.example.frontend.model.Usuario;


public class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {
    }

    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public static boolean esAdministrador() {
        return usuarioActual != null && usuarioActual.getRol() == Rol.ADMINISTRADOR;
    }

    public static void cerrar() {
        usuarioActual = null;
    }
}
