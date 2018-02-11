package com.puzzlesmentales.logic.sudoku;

import android.os.Bundle;
import android.os.SystemClock;

import com.puzzlesmentales.logic.sudoku.command.AbstractCommand;
import com.puzzlesmentales.logic.sudoku.command.CommandStack;
import com.puzzlesmentales.logic.sudoku.command.EditNotaCeldaCommand;
import com.puzzlesmentales.logic.sudoku.command.FillNotasCommand;
import com.puzzlesmentales.logic.sudoku.command.LimpiarNotasCommand;
import com.puzzlesmentales.logic.sudoku.command.SetValorCeldaCommand;
import com.puzzlesmentales.logic.sudoku.Celda;

import java.io.Serializable;

/**
 * Clase que representa una partida de sudoku
 */
public class SudokuGame{

	public static final int GAME_STATE_PLAYING = 0;
	public static final int GAME_STATE_NOT_STARTED = 1;
	public static final int GAME_STATE_COMPLETED = 2;

	private long id;
	private long creado;
	private int estado;
	private long tiempo;
	private long lastPlayed;
	private String nota;
	private Tablero celdas;
	private int puntos;

	private OnSudokuResueltoListener onSudokuResueltoListener;
	private CommandStack commandStack;
	// Tiempo cuando la activitie se activa
	private long acParaTiempo = -1;

	/**
	 * Constructor principal de la clase
	 */
	public SudokuGame() {
		this.tiempo = 0;
		this.lastPlayed = 0;
		this.creado = 0;
		estado = GAME_STATE_NOT_STARTED;
		this.puntos = 18;
	}

	public static SudokuGame crearSudokuVacio() {
		SudokuGame game = new SudokuGame();
		game.setCeldas(Tablero.createEmpty());
		game.setCreado(System.currentTimeMillis());
		return game;
	}

	public static SudokuGame crearSudokuConTablero(Tablero tablero){
		SudokuGame game = new SudokuGame();
		game.setCeldas(tablero);
		game.setCreado(System.currentTimeMillis());
		return game;
	}

	public void guardarEstado(Bundle outState) {
		outState.putLong("id", id);
		outState.putString("nota", nota);
		outState.putLong("creado", creado);
		outState.putInt("estado", estado);
		outState.putLong("tiempo", tiempo);
		outState.putLong("lastPlayed", lastPlayed);
		outState.putString("celdas", celdas.serialize());

		commandStack.guardarEstado(outState);
	}

	public void recuperarEstado(Bundle inState) {
		id = inState.getLong("id");
		nota = inState.getString("nota");
		creado = inState.getLong("creado");
		estado = inState.getInt("estado");
		tiempo = inState.getLong("tiempo");
		lastPlayed = inState.getLong("lastPlayed");
		celdas = Tablero.deserialize(inState.getString("celdas"));
		commandStack = new CommandStack(celdas);
		commandStack.recuperarEstado(inState);
		validar();
	}

	//Getters
	public String getNota() {
		return this.nota;
	}
	public long getCredo() {
		return this.creado;
	}
	public int getEstado() {
		return this.estado;
	}
	public int getPuntos() {
		return this.puntos;
	}
	public long getTiempo() {
		if (acParaTiempo != -1) {
			return tiempo + SystemClock.uptimeMillis() - acParaTiempo;
		} else {
			return tiempo;
		}
	}
	public long getLastPlayed() {
		return this.lastPlayed;
	}
	public Tablero getCeldas() {
		return this.celdas;
	}
	public long getId() {
		return this.id;
	}
	public boolean isCompleted() {
		return celdas.isCompleted();
	}
	//Setters
	public void setOnSudokuResueltoListener(OnSudokuResueltoListener l) {
		this.onSudokuResueltoListener = l;
	}
	public void setNota(String note) {
		this.nota = note;
	}
	public void setCreado(long created) {
		this.creado = created;
	}
	public void setEstado(int state) {
		this.estado = state;
	}
	public void setTiempo(long time) {
		this.tiempo = time;
	}
	public void setLastPlayed(long lastPlayed) {
		this.lastPlayed = lastPlayed;
	}
	public void setCeldas(Tablero tablero) {
		celdas = tablero;
		validar();
		commandStack = new CommandStack(celdas);
	}
	public void setPuntos(int puntos) {
		this.puntos = puntos;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setValorCelda(Celda celda, int valor) {
		if (celda == null) {
			throw new IllegalArgumentException("Celda no puede ser null");
		}
		if (valor < 0 || valor > 9) {
			throw new IllegalArgumentException("El valor tiene que estar entre 1 y 9");
		}
		if (celda.isEditable()) {
			executeCommand(new SetValorCeldaCommand(celda, valor));
			puntos = validar() ? puntos : puntos-1;
			if (isCompleted()) {
				finish();
				if (onSudokuResueltoListener != null) {
					onSudokuResueltoListener.onSudokuResuelto();
				}
			}
		}
	}
	public void setNotaCelda(Celda celda, NotaCelda nota) {
		if (celda == null) {
			throw new IllegalArgumentException("Celda no puede ser null");
		}
		if (nota == null) {
			throw new IllegalArgumentException("Nota no puede ser null");
		}

		if (celda.isEditable()) {
			executeCommand(new EditNotaCeldaCommand(celda, nota));
		}
	}

	//Metodos
	public boolean validar() {
		return this.celdas.validar();
	}

	private void executeCommand(AbstractCommand c) {
		commandStack.execute(c);
	}
	public void undo() {
		commandStack.undo();
	}
	public boolean hasSomethingToUndo() {
		return commandStack.hasSomethingToUndo();
	}
	public void setUndoCheckpoint() {
		commandStack.setCheckpoint();
	}
	public void undoToCheckpoint() {
		commandStack.undoToCheckpoint();
	}
	public boolean hasUndoCheckpoint() {
		return commandStack.hasCheckpoint();
	}

	public void limpiarNotas() {
		executeCommand(new LimpiarNotasCommand());
	}
	public void fillNotas() {
		executeCommand(new FillNotasCommand());
	}

	/**
	 * Empezar juego
	 */
	public void start() {
		estado = GAME_STATE_PLAYING;
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
		setLastPlayed(System.currentTimeMillis());
	}

	/**
	 * Finaliza el juego. Se llama cuando el sudoku se resuelve
	 */
	private void finish() {
		pause();
		estado = GAME_STATE_COMPLETED;
	}

	/**
	 * Resetea el juego
	 */
	public void reset() {
		for (int r = 0; r < Tablero.SUDOKU_SIZE; r++)
			for (int c = 0; c < Tablero.SUDOKU_SIZE; c++) {
				Celda cell = celdas.getCelda(r, c);
				if (cell.isEditable()) {
					cell.setValor(0);
					cell.setNota(new NotaCelda());
				}
			}
		validar();
		setTiempo(0);
		setLastPlayed(0);
		estado = GAME_STATE_NOT_STARTED;
	}


	public interface OnSudokuResueltoListener{
		void onSudokuResuelto();
	}

}
