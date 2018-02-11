package com.puzzlesmentales.logic.sopa;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Fer on 30/12/2017.
 */

public class SopaLetras {

    private List<String> palabras;

    private int alto, ancho, colocar;
    private Character[][] sopa;
    private List<String> colocadas;
    private List<String> adivinadas;
    private long tiempo;
    private long acParaTiempo = -1;

    /* Cuando la sopa ya ha sido creada mediante Tesseract... */
    private boolean flagSePasaSopaCreada = false; // si se pasa por constructor una sopa ya creada
    private static List<String> palabras_jugador;

    public long getTiempo() {
        if (acParaTiempo != -1) {
            return tiempo + SystemClock.uptimeMillis() - acParaTiempo;
        } else {
            return tiempo;
        }
    }
    public boolean getFlag()
    {
        return flagSePasaSopaCreada;
    }


    public SopaLetras( char[][] sopaCreada, List<String> palabras )
    {
        this.sopa = new Character[ sopaCreada.length ][ sopaCreada[0].length ];
        pasaCharACharacter(sopaCreada);
        this.alto = sopaCreada.length;
        this.ancho = sopaCreada[0].length;
        palabras_jugador = palabras;

        flagSePasaSopaCreada = true;
    }

    public SopaLetras(int alto, int ancho, int colocarAprox , List<String> palabras) {
        this.palabras = palabras;
        this.alto = alto;
        this.ancho = ancho;
        if(colocarAprox < palabras.size()){
            colocar = colocarAprox;
        }else{
           colocar = palabras.size() - 1;
        }
    }

    private void pasaCharACharacter(char[][] sopaCreada)
    {
        for (int fila=0; fila< sopa.length; fila++) // recorro todas las columnas
        {
            for (int columna=0; columna< sopa[0].length; columna++)
            {
                sopa[fila][columna] = sopaCreada[fila][columna];
            }
        }
    }

    public void start() {
        resume();
    }

    public void resume() {
        acParaTiempo = SystemClock.uptimeMillis();
    }

    /**
     * Para el juego
     */
    public void pause() {
        tiempo += SystemClock.uptimeMillis() - acParaTiempo;
        acParaTiempo = -1;
    }


    /**
     * Finaliza el juego. Se llama cuando el sudoku se resuelve
     */
    private void finish() {
        pause();
    }


    public Character[][] getSopa() {
        return sopa;
    }

    public boolean addAdivinar(String s){
        if(adivinadas.contains(s) || !colocadas.contains(s)){
           return false;
        }
        adivinadas.add(s);
        return true;
    }

    public void init() {
        adivinadas = new ArrayList<>();
        colocadas = new ArrayList<>();

        if (!flagSePasaSopaCreada)
        {
            sopa = new Character[alto][ancho];
            Random r = new Random();
            for (int i = 0; i < colocar; i++) {

                int columna = r.nextInt(ancho);
                int fila = r.nextInt(alto);
                //System.out.println(0);

                List<String> lpal = new ArrayList<>();
                for (String s : palabras) {
                    if (!colocadas.contains(s)) {
                        lpal.add(s);
                    }
                }

                String palabra = lpal.get(r.nextInt(lpal.size()));
                //System.out.println(palabra + " " + fila + " " + columna);
                if (palabra == null) {
                    break;
                }
                //System.out.println(1);
                List<Direction> dl = disponible(fila, columna, palabra);
                if (dl == null || dl.isEmpty()) {
                    continue;
                }

                Direction d = dl.get(r.nextInt(dl.size()));
                //System.out.println(2);
                colocadas.add(palabra);

                colocar(fila, columna, d, palabra);


            }
            print();
            for (int i = 0; i < sopa.length; i++) {
                for (int j = 0; j < sopa[i].length; j++) {
                    if (sopa[i][j] == null) {
                        sopa[i][j] = (char) (r.nextInt(25) + 'a');
                    }
                }
            }
            System.out.println(colocadas);
            print();
        }

        else
        {
            colocadas.addAll( palabras_jugador );
        }
    }

    public boolean acabado(){
        return colocadas.size() <= adivinadas.size();
    }

    private void colocar(int fila, int columna, Direction d, String palabra) {
        for (int i = 0; i < palabra.length(); i++) {
            sopa[fila + (d.fila * i)][columna + (d.columna * i)] = palabra.charAt(i);
        }
    }

    public List<Direction> disponible(int fila, int columna, String palabra) {
        List<Direction> res = new ArrayList<>();
        if (sopa[fila][columna] != null && !palabra.isEmpty() && sopa[fila][columna] != palabra.charAt(0)) {
            return null;
        }
        for (Direction d : Direction.values()) {
            //System.out.println(palabra + " " + d.name());
            for (int i = 0; i < palabra.length(); i++) {
                if ((fila + (d.fila * i)) < 0 || (columna + (d.columna * i)) < 0 || (fila + (d.fila * i)) >= alto || (columna + (d.columna * i)) >= ancho) {
                    //System.out.println("Motivo " + String.valueOf((fila + (d.fila*i)) <  0)+" "+ String.valueOf((columna + (d.columna*i)) < 0)+" "+ String.valueOf((fila +  (d.fila*i)) >=  alto)+" "+ String.valueOf((columna + (d.columna*i)) >=ancho));
                    break;
                }
                //System.out.println("Pri");
                Character caracter = sopa[fila + (d.fila * i)][columna + (d.columna * i)];
                if (caracter != null && caracter != palabra.charAt(i)) {
                    break;
                }
                //System.out.println("Sec");
                if (i == palabra.length() - 1) {
                    res.add(d);
                }
            }
        }
        return res;
    }

    private void print() {
        for (Character[] row : sopa) {
            for (Character i : row) {
                System.out.print(i);
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    public enum Direction {
        TOP(-1, 0), BOTTOM(1, 0), LEFT(0, -1), RIGHT(0, 1);

        private int fila, columna;

        Direction(int fila, int columna) {
            this.fila = fila;
            this.columna = columna;
        }
    }

    public List<String> getColocadas(){
        return new ArrayList<>(colocadas);
    }
    public List<String> getAdivinadas(){
        return new ArrayList<>(adivinadas);
    }

    public void setColocadas(List<String> colocadas) {
        this.colocadas = colocadas;
    }

    public void setAdivinadas(List<String> adivinadas) {
        this.adivinadas = adivinadas;
    }

    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }
}
