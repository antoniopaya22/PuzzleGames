package com.puzzlesmentales.logic.sudoku.command;

import android.os.Bundle;

import java.io.Serializable;

public abstract class AbstractCommand {

	/**
	 * Ejecuta el commando
	 */
	abstract void execute();

	/**
	 * Deshace el comando
	 */
	abstract void undo();


	public static AbstractCommand nuevaInstancia(String commandClass) {
		if (commandClass.equals(LimpiarNotasCommand.class.getSimpleName())) {
			return new LimpiarNotasCommand();
		} else if (commandClass.equals(EditNotaCeldaCommand.class.getSimpleName())) {
			return new EditNotaCeldaCommand();
		} else if (commandClass.equals(FillNotasCommand.class.getSimpleName())) {
			return new FillNotasCommand();
		} else if (commandClass.equals(SetValorCeldaCommand.class.getSimpleName())) {
			return new SetValorCeldaCommand();
		} else {
			throw new IllegalArgumentException(String.format("Command class desconocido '%s'.", commandClass));
		}
	}

	private boolean mIsCheckpoint;

	void guardarEstado(Bundle outState) {
		outState.putBoolean("isCheckpoint", mIsCheckpoint);
	}

	void recuperarEstado(Bundle inState) {
		mIsCheckpoint = inState.getBoolean("isCheckpoint");
	}

	public boolean isCheckpoint() {
		return mIsCheckpoint;
	}
	public void setCheckpoint(boolean isCheckpoint) {
		mIsCheckpoint = isCheckpoint;
	}
	public String getCommandClass() {
		return getClass().getSimpleName();
	}


}
