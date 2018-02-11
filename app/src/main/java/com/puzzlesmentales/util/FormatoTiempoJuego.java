package com.puzzlesmentales.util;

import java.util.Formatter;

public class FormatoTiempoJuego {

    private static final int TIME_99_99 = 99 * 99 * 1000;

    private StringBuilder textoTiempo = new StringBuilder();
    private Formatter formatter = new Formatter(textoTiempo);

    public String format(long tiempo) {
        textoTiempo.setLength(0);
        if (tiempo > TIME_99_99) {
            formatter.format("%d:%02d", tiempo / 60000, tiempo / 1000 % 60);
        } else {
            formatter.format("%02d:%02d", tiempo / 60000, tiempo / 1000 % 60);
        }
        return textoTiempo.toString();
    }
}
