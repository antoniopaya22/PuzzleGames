package com.puzzlesmentales.logic.sudoku.command;

import android.os.Bundle;

import com.puzzlesmentales.logic.sudoku.Celda;
import com.puzzlesmentales.logic.sudoku.GrupoCeldas;
import com.puzzlesmentales.logic.sudoku.NotaCelda;
import com.puzzlesmentales.logic.sudoku.Tablero;

import java.util.ArrayList;
import java.util.List;

public class FillNotasCommand extends AbstractCeldaCommand{

	private List<NotaEntry> notasViejas = new ArrayList<NotaEntry>();
	public FillNotasCommand() {}

	@Override
	void guardarEstado(Bundle outState) {
		super.guardarEstado(outState);

		int[] rows = new int[notasViejas.size()];
		int[] cols = new int[notasViejas.size()];
		String[] notas = new String[notasViejas.size()];
		int i = 0;
		for (NotaEntry ne : notasViejas) {
			rows[i] = ne.row;
			cols[i] = ne.col;
			notas[i] = ne.nota.serialize();
			i++;
		}

		outState.putIntArray("rows", rows);
		outState.putIntArray("cols", cols);
		outState.putStringArray("notas", notas);
	}

	@Override
	void recuperarEstado(Bundle inState) {
		super.recuperarEstado(inState);
		int[] rows = inState.getIntArray("rows");
		int[] cols = inState.getIntArray("cols");
		String[] notas = inState.getStringArray("notas");

		for (int i = 0; i < rows.length; i++) {
			notasViejas.add(new NotaEntry(rows[i], cols[i], NotaCelda
					.deserialize(notas[i])));
		}
	}

	@Override
	void execute() {
		Tablero tablero = getCeldas();
		notasViejas.clear();

		for (int r = 0; r < Tablero.SUDOKU_SIZE; r++) {
			for (int c = 0; c < Tablero.SUDOKU_SIZE; c++) {
				Celda celda = tablero.getCelda(r, c);
				notasViejas.add(new NotaEntry(r, c, celda.getNota()));
				celda.setNota(new NotaCelda());

				GrupoCeldas row = celda.getRow();
				GrupoCeldas column = celda.getColumn();
				GrupoCeldas sector = celda.getSector();
				for (int i = 1; i <= Tablero.SUDOKU_SIZE; i++) {
					if (!row.contains(i) && !column.contains(i) && !sector.contains(i)) {
						celda.setNota(celda.getNota().addNumber(i));
					}
				}
			}
		}
	}

	@Override
	void undo() {
		Tablero celdas = getCeldas();

		for (NotaEntry ne : notasViejas) {
			celdas.getCelda(ne.row, ne.col).setNota(ne.nota);
		}
	}

	private static class NotaEntry {
		public int row;
		public int col;
		public NotaCelda nota;

		public NotaEntry(int row, int col, NotaCelda nota) {
			this.row = row;
			this.col = col;
			this.nota = nota;
		}

	}
}
