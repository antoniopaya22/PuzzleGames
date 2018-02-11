package com.puzzlesmentales.logic.sudoku.command;

import android.os.Bundle;

import com.puzzlesmentales.logic.sudoku.Celda;
import com.puzzlesmentales.logic.sudoku.NotaCelda;


public class EditNotaCeldaCommand extends AbstractCeldaCommand{

	private int row;
	private int col;
	private NotaCelda nota;
	private NotaCelda notaOld;

	public EditNotaCeldaCommand(){}

	public EditNotaCeldaCommand(Celda celda, NotaCelda nota) {
		this.row = celda.getRowIndex();
		this.col = celda.getColumnIndex();
		this.nota = nota;
	}

	@Override
	void guardarEstado(Bundle outState) {
		super.guardarEstado(outState);
		outState.putInt("row", row);
		outState.putInt("col", col);
		outState.putString("nota", nota.serialize());
		outState.putString("notaOld", notaOld.serialize());
	}

	@Override
	void recuperarEstado(Bundle inState) {
		super.recuperarEstado(inState);
		row = inState.getInt("row");
		col = inState.getInt("col");
		nota = NotaCelda.deserialize(inState.getString("nota"));
		notaOld = NotaCelda.deserialize(inState.getString("notaOld"));
	}

	@Override
	void execute() {
		Celda cell = getCeldas().getCelda(row, col);
		notaOld = cell.getNota();
		cell.setNota(nota);
	}

	@Override
	void undo() {
		Celda cell = getCeldas().getCelda(row, col);
		cell.setNota(notaOld);
	}

}
