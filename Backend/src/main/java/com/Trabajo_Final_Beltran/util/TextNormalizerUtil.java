package com.Trabajo_Final_Beltran.util;

public final class TextNormalizerUtil {

    private TextNormalizerUtil() {
    }

    public static String normalizarTexto(String texto) {
        if (texto == null) {
            return null;
        }

        String limpio = texto.trim().replaceAll("\\s+", " ");

        if (limpio.isEmpty()) {
            return limpio;
        }

        String[] palabras = limpio.split(" ");
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < palabras.length; i++) {
            String palabra = palabras[i];
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)));
                if (palabra.length() > 1) {
                    resultado.append(palabra.substring(1).toLowerCase());
                }
            }
            if (i < palabras.length - 1) {
                resultado.append(" ");
            }
        }

        return resultado.toString();
    }

    public static String normalizarNumero(String numero) {
        if (numero == null) {
            return null;
        }

        String limpio = numero.trim().replaceAll("\\s+", " ");

        return limpio.toUpperCase();
    }
}