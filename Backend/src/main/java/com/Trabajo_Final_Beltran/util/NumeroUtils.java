package com.Trabajo_Final_Beltran.util;

public class NumeroUtils {

    private NumeroUtils() {
    }

    // Elimina todo lo que no sea dígito: puntos, guiones, espacios, etc.
    public static String limpiarNumero(String valor) {
        if (valor == null) {
            return null;
        }
        return valor.replaceAll("\\D", "");
    }
}
