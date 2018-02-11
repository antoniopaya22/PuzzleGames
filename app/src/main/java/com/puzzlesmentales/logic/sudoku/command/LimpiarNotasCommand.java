package com.puzzlesmentales.logic.sudoku.command;

import android.os.Bundle;

import com.puzzlesmentales.logic.sudoku.Celda;
import com.puzzlesmentales.logic.sudoku.NotaCelda;
import com.puzzlesmentales.logic.sudoku.Tablero;

import java.util.ArrayList;
import java.util.List;

public class LimpiarNotasCommand extends AbstractCeldaCommand{

	private List<NoteEntry> mOldNotes = new ArrayList<NoteEntry>();


	public LimpiarNotasCommand() {}


	@Override
	void guardarEstado(Bundle outState) {
		super.guardarEstado(outState);

		int[] rows = new int[mOldNotes.size()];
		int[] cols = new int[mOldNotes.size()];
		String[] notas = new String[mOldNotes.size()];

		int i = 0;
		for (NoteEntry ne : mOldNotes) {
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
			mOldNotes.add(new NoteEntry(rows[i], cols[i], NotaCelda
					.deserialize(notas[i])));
		}
	}

	@Override
	void execute() {
		Tablero tablero = getCeldas();

		mOldNotes.clear();
		for (int r = 0; r < Tablero.SUDOKU_SIZE; r++) {
			for (int c = 0; c < Tablero.SUDOKU_SIZE; c++) {
				Celda celda = tablero.getCelda(r, c);
				NotaCelda nota = celda.getNota();
				if (!nota.isEmpty()) {
					mOldNotes.add(new NoteEntry(r, c, nota));
					celda.setNota(new NotaCelda());
				}
			}
		}
	}

	@Override
	void undo() {
		Tablero tablero = getCeldas();

		for (NoteEntry ne : mOldNotes) {
			tablero.getCelda(ne.row, ne.col).setNota(ne.nota);
		}

	}

	private static class NoteEntry {
		public int row;
		public int col;
		public NotaCelda nota;

		public NoteEntry(int row, int col, NotaCelda nota) {
			this.row = row;
			this.col = col;
			this.nota = nota;
		}

	}


}
