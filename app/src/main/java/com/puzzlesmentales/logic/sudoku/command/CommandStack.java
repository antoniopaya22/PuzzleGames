package com.puzzlesmentales.logic.sudoku.command;

import android.os.Bundle;

import com.puzzlesmentales.logic.sudoku.Tablero;

import java.util.Stack;

public class CommandStack{
	private Stack<AbstractCommand> commandStack = new Stack<AbstractCommand>();
	private Tablero tablero;

	public CommandStack(Tablero tablero) {
		this.tablero = tablero;
	}

	public void guardarEstado(Bundle outState) {
		outState.putInt("cmdStack.size", commandStack.size());
		for (int i = 0; i < commandStack.size(); i++) {
			AbstractCommand command = commandStack.get(i);
			Bundle commandState = new Bundle();
			commandState.putString("commandClass", command.getCommandClass());
			command.guardarEstado(commandState);
			outState.putBundle("cmdStack." + i, commandState);
		}
	}

	public void recuperarEstado(Bundle inState) {
		int stackSize = inState.getInt("cmdStack.size");
		for (int i = 0; i < stackSize; i++) {
			Bundle commandState = inState.getBundle("cmdStack." + i);
			AbstractCommand command = AbstractCommand.nuevaInstancia(commandState.getString("commandClass"));
			command.recuperarEstado(commandState);
			push(command);
		}
	}

	public boolean empty() {
		return this.commandStack.empty();
	}

	public void execute(AbstractCommand command) {
		push(command);
		command.execute();
	}

	public void undo() {
		if (!commandStack.empty()) {
			AbstractCommand c = pop();
			c.undo();
			validarCeldas();
		}
	}

	public void setCheckpoint() {
		if (!commandStack.empty()) {
			AbstractCommand c = commandStack.peek();
			c.setCheckpoint(true);
		}
	}

	public boolean hasCheckpoint() {
		for (AbstractCommand c : commandStack) {
			if (c.isCheckpoint())
				return true;
		}
		return false;
	}

	public void undoToCheckpoint() {
		AbstractCommand c;
		while (!commandStack.empty()) {
			c = commandStack.pop();
			c.undo();
			if (commandStack.empty() || commandStack.peek().isCheckpoint()) {
				break;
			}
		}
		validarCeldas();
	}


	public boolean hasSomethingToUndo() {
		return commandStack.size() != 0;
	}

	private void push(AbstractCommand command) {
		if (command instanceof AbstractCeldaCommand) {
			((AbstractCeldaCommand) command).setCells(tablero);
		}
		commandStack.push(command);
	}

	private AbstractCommand pop() {
		return commandStack.pop();
	}

	private void validarCeldas() {
		tablero.validar();
	}


}
