package com.example.frontend.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hashear(String passwordPlano) {
        return BCrypt.hashpw(passwordPlano, BCrypt.gensalt());
    }

    public static boolean verificar(String passwordPlano, String hashGuardado) {
        return BCrypt.checkpw(passwordPlano, hashGuardado);
    }
}
