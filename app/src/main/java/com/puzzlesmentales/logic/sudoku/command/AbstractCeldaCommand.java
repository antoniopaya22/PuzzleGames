package com.puzzlesmentales.logic.sudoku.command;

import com.puzzlesmentales.logic.sudoku.Tablero;

/**
 * Command abstracto que actua en una o mas celdas
 * @author Antonio Paya Gonzalez
 */
public abstract class AbstractCeldaCommand extends AbstractCommand{

	protected Tablero getCeldas() {
		return tablero;
	}
	protected void setCells(Tablero t) {
		this.tablero = t;
	}
	private Tablero tablero;

}
