package com.puzzlesmentales.logic.sudoku;

/**
 * Clase que resuelve un Sudoku de 9x9
 * @author Antonio
 *
 */
public class ResuelveSudoku {
	
	//Constantes
	public static final int ANCHO_TABLERO = 9;
    public static final int ALTO_TABLERO = 9;
    public int[][] tablero;
    
    /**
     * Constructor de la clas.
     * Resuelve el tablero dado por parametro
     * @param tablero, sudoku a resolver
     */
    public ResuelveSudoku(int[][] tablero) {
    	this.tablero = tablero;
    	resolverSudoku();
    }

    /**
     * Metodo que usa un algoritmo de backtraking para resolver el sudoku
     * @return Devuelve True si ha resuelto el sudoku
     */
    public boolean resolverSudoku() {
        if (backtraking(tablero) == false) {
            return false;
        }
        return true;
    }

    /**
     * Metodo que resuelve el sudoku usando backtraking
     * @param sudoku, tablero del sudoku
     * @return Devuelve true si consigue resolverlo
     */
    private boolean backtraking(int[][] sudoku) {
        boolean temp = false;
        int i = 0;
        int j = 0;
        for (i = 0; i < ANCHO_TABLERO; i++) {
            for (j = 0; j < ALTO_TABLERO; j++) {
                if (sudoku[i][j] == 0) {
                    temp = true;
                    break;
                }
            }
            if (temp)
                break;
        }

        if(i==ANCHO_TABLERO || j == ALTO_TABLERO){
            return true;
        }

        for (int num = 1; num <= 9; num++) {
            if (comprobarNumero(sudoku, i, j, num)) {
                sudoku[i][j] = num;
                if (backtraking(sudoku))
                    return true;
            }
            sudoku[i][j] = 0;
        }
        return false;
    }

    /**
     * Metodo que comprueba si se puede poner el numero pasado por parametro
     * en una casilla
     * @param sudoku, tablero del sudoku
     * @param fila, fila de la casilla
     * @param columna, columna de la casilla
     * @param num, numero a colocar
     * @return Devuelve true si se puede colocar ahi
     */
    private boolean comprobarNumero(int[][] sudoku, int fila, int columna, int num) {
        return (comprobarColumna(sudoku, columna, num) && comprobarFila(sudoku, fila, num) 
        		&& comprobarCuadrado(sudoku, fila, columna, num));
    }

    /**
     * Metodo que comprueba si se puede colocar un numero en un cuadrado
     * @param sudoku, tablero del sudoku
     * @param fila, fila de la casilla
     * @param columna, columna de la casilla
     * @param num, numero a colocar
     * @return Devuelve true si se puede colocar ahi
     */
    private boolean comprobarCuadrado(int[][] sudoku, int fila, int columna, int num) {
        int newRow = fila - fila % 3;
        int newCol = columna - columna % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (sudoku[i + newRow][j + newCol] == num)
                    return false;
            }
        }
        return true;
    }
    
    /**
     * Metodo que comprueba si el numero esta bien colocado verticalmente
     * @param sudoku, tablero del sudoku
     * @param columna, columna a comprobar
     * @param num, numero a comprobar
     * @return Devuelve True si esta tdo correcto
     */
    private boolean comprobarColumna(int[][] sudoku, int columna, int num) {
        for (int i = 0; i < ANCHO_TABLERO; i++) {
            if (sudoku[i][columna] == num)
                return false;
        }
        return true;
    }

    /**
     * Metodo que comprueba si el numero esta bien colocado horizontalmente
     * @param sudoku, tablero del sudoku
     * @param fila, fila a comprobar
     * @param num, numero a comprobar
     * @return Devuelve True si esta todo correcto
     */
    private boolean comprobarFila(int[][] sudoku, int fila, int num) {
        for (int i = 0; i < ALTO_TABLERO; i++) {
            if (sudoku[fila][i] == num)
                return false;
        }
        return true;
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
