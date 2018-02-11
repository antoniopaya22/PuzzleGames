
package com.puzzlesmentales.logic.sudoku;

import java.util.Random;

/**
 * Clase que genera un Sudoku 9x9
 * 
 * @author Antonio Paya Gonzalez
 *
 */
public class GeneradorSudoku {

	// Constantes: Ancho y alto del sudoku
	public static final int ANCHO_TABLERO = 9;
	public static final int ALTO_TABLERO = 9;
	// Atributos
	private int[][] tablero;
	
	/**
	 * Constructor de la clase.
	 * Inicializa la matriz tablero con el ancho y el alto.
	 */
	public GeneradorSudoku() {
		tablero = new int[ANCHO_TABLERO][ALTO_TABLERO];
	}

	/**
	 * Metodo que genera un sudoku nuevo
	 * 
	 * @param huecos, numero de espacios en blanco del tablero
	 * @return devuelve una matriz int[][] con el tablero del sudoku
	 */
	public int[][] generar(int huecos) {
		tablero = new int[ANCHO_TABLERO][ALTO_TABLERO];
		boolean realizado = resolverCasillas(0, 0);
		while(!realizado) {
			realizado = resolverCasillas(0, 0);
		}
		hacerAgujeros(huecos);
		
		return tablero;

	}

	/**
	 * Metodo que va colocando numeros en celdas
	 * recursivamente.
	 *
	 * @param x, posicion x de la celda
	 * @param y, posicion y de la celda
	 * @return Devuelve true si se puede realizar sin problema, false en caso contrario.
	 */
	public boolean resolverCasillas(int x, int y) {
		int[] valores = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		int nextX = x, nextY = y;
		int temp = 0,actual = 0,top = valores.length;
		Random r = new Random();
		
		for (int i = top - 1; i > 0; i--) {
			actual = r.nextInt(i);
			temp = valores[actual];
			valores[actual] = valores[i];
			valores[i] = temp;
		}

		for (int i = 0; i < valores.length; i++) {
			if (insertarValorEnCasilla(x, y, valores[i])) {
				tablero[x][y] = valores[i];
				if (x == 8) {
					if (y == 8)
						return true;
					else {
						nextX = 0;
						nextY = y + 1;
					}
				} else {
					nextX = x + 1;
				}
				if (resolverCasillas(nextX, nextY))
					return true;
			}
		}
		tablero[x][y] = 0;
		return false;
	}

	/**
	 * 
	 * Segun las coordenadas dadas por parametro y un valor 
	 * calcula si dicho valor se puede insertar en la celda
	 *
	 * @param x,posicion x de la casilla
	 * @param y,posicion y de la casilla
	 * @param valor, valor con el que se quiere probar
	 * @return Devuelve True si el valor se puede insertar ah�.
	 */
	private boolean insertarValorEnCasilla(int x, int y, int valor) {
		for (int i = 0; i < 9; i++) {
			if (valor == tablero[x][i])
				return false;
		}
		for (int i = 0; i < 9; i++) {
			if (valor == tablero[i][y])
				return false;
		}
		int cornerX = 0;
		int cornerY = 0;
		if (x > 2)
			if (x > 5)
				cornerX = 6;
			else
				cornerX = 3;
		if (y > 2)
			if (y > 5)
				cornerY = 6;
			else
				cornerY = 3;
		for (int i = cornerX; i < 10 && i < cornerX + 3; i++)
			for (int j = cornerY; j < 10 && j < cornerY + 3; j++)
				if (valor == tablero[i][j])
					return false;
		return true;
	}

	/**
	 * Rellena de ceros(huecos) el tablero de forma aleatoria
	 * y con el numero dado por parametro
	 * 
	 * @param huecos,huecos a poner en el tablero
	 */
	public void hacerAgujeros(int huecos) {
		
		double casillasRestantes = ANCHO_TABLERO*ALTO_TABLERO;
		double huecosRestantes = (double) huecos;

		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++) {
				double temp = huecosRestantes / casillasRestantes;
				if (Math.random() <= temp) {
					tablero[i][j] = 0;
					huecosRestantes--;
				}
				casillasRestantes--;
			}
	}
	
	
	/**
	 * Metodo que devuelve un String con el tablero
	 */
	@Override
	public String toString() {
		String cadena = "";
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++)
				cadena+=tablero[i][j] + "  ";
			cadena+="\n";
		}
		cadena+="\n";
		return cadena;
	}
	

}
