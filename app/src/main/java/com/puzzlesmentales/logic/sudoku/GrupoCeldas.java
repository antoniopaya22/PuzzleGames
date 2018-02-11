package com.puzzlesmentales.logic.sudoku;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa un grupo de celdas del sudoku como filas,
 * columnas o sectores
 *
 * @author Antonio Paya Gonzalez
 */
public class GrupoCeldas{
	//Atributos
	private Celda[] celdas = new Celda[Tablero.SUDOKU_SIZE];
	private int pos = 0;

	//Metodos

	/**
	 * Devuelve true si el grupo contiene un numero
	 * @param value el numero
	 * @return true en caso afirmativo
	 */
	public boolean contains(int value) {
		for (Celda celda : celdas) {
			if (celda.getValor() == value) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Añade una celda al grupo
	 * @param celda Celda a añadir
	 */
	public void addCelda(Celda celda) {
		this.celdas[pos] = celda;
		this.pos++;
	}
	/**
	 * Valida los numeros de todas las casillas del grupo
	 * y marca aquellas que no sean válidas (ver {@link Celda#isValido})
	 *
	 * @return Devuelve True si es correcta la validacion
	 */
	protected boolean validar() {
		boolean valido = true;
		Map<Integer, Celda> celdasValor = new HashMap<Integer, Celda>();

		for (Celda celda : celdas) {
			int value = celda.getValor();
			if (celdasValor.get(value) != null) {
				celda.setValido(false);
				celdasValor.get(value).setValido(false);
				valido = false;
			} else {
				celdasValor.put(value, celda);
			}
		}
		return valido;
	}


}
