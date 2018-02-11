package com.puzzlesmentales.logic.sudoku.command;

import android.os.Bundle;

import com.puzzlesmentales.logic.sudoku.Celda;


public class SetValorCeldaCommand extends AbstractCeldaCommand{

	private int row;
	private int col;
	private int value;
	private int valueOld;

	public SetValorCeldaCommand(Celda celda, int value) {
		row = celda.getRowIndex();
		col = celda.getColumnIndex();
		this.value = value;
	}

	SetValorCeldaCommand() {}

	@Override
	void guardarEstado(Bundle outState) {
		super.guardarEstado(outState);

		outState.putInt("row", row);
		outState.putInt("col", col);
		outState.putInt("value", value);
		outState.putInt("oldValue", valueOld);
	}

	@Override
	void recuperarEstado(Bundle inState) {
		super.recuperarEstado(inState);

		row = inState.getInt("row");
		col = inState.getInt("col");
		value = inState.getInt("value");
		valueOld = inState.getInt("oldValue");
	}

	@Override
	void execute() {
		Celda cell = getCeldas().getCelda(row, col);
		valueOld = cell.getValor();
		cell.setValor(value);
	}

	@Override
	void undo() {
		Celda cell = getCeldas().getCelda(row, col);
		cell.setValor(valueOld);
	}

}
