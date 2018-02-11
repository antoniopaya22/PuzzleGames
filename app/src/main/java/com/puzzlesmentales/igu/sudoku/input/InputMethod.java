package com.puzzlesmentales.igu.sudoku.input;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.view.View;
import android.widget.Button;

import com.puzzlesmentales.R;
import com.puzzlesmentales.igu.sudoku.HintsQueue;
import com.puzzlesmentales.igu.sudoku.TableroSudokuView;
import com.puzzlesmentales.logic.sudoku.Celda;
import com.puzzlesmentales.logic.sudoku.SudokuGame;

/**
 * Clase base para los input methods usados
 *
 * @author Antonio Paya Gonzalez
 */
public abstract class InputMethod {
	//Atributos
	protected Context context;
	protected PanelControl panel;
	protected SudokuGame game;
	protected TableroSudokuView tableroView;
	protected HintsQueue mHintsQueue;
	private String imName;
	protected View imView;
	protected boolean activo = false;
	private boolean enabled = true;

	public InputMethod() {

	}

	protected void inicializar(Context context, PanelControl controlPanel, SudokuGame game, TableroSudokuView tablero, HintsQueue hintsQueue) {
		this.context = context;
		this.panel = controlPanel;
		this.game = game;
		this.tableroView = tablero;
		this.mHintsQueue = hintsQueue;
		this.imName = this.getClass().getSimpleName();
	}

	public boolean isInputMethodViewCreated() {
		return imView != null;
	}

	public View getInputMethodView() {
		if (imView == null) {
			imView = createControlPanelView();
			View cambiarModoView = imView.findViewById(R.id.cambiarModo);
			Button cambiarModoBoton = (Button) cambiarModoView;
			cambiarModoBoton.setText(getAbbrName());
			cambiarModoBoton.getBackground().setColorFilter(new LightingColorFilter(Color.parseColor("#00695c"), 0));
			onControlPanelCreated(imView);
		}

		return imView;
	}

	public void pause() {
		onPause();
	}

	protected void onPause() {}
	protected String getNombreInputMethod() {
		return imName;
	}
	public abstract int getNameResID();
	public abstract int getHelpResID();
	public abstract String getAbbrName();

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (!enabled) {
			panel.activateNextInputMethod();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void activate() {
		activo = true;
		onActivated();
	}

	public void deactivate() {
		activo = false;
		onDeactivated();
	}

	protected abstract View createControlPanelView();

	protected void onControlPanelCreated(View controlPanel) {

	}

	protected void onActivated() {
	}

	protected void onDeactivated() {
	}

	protected void onCellSelected(Celda cell) {

	}

	protected void onCellTapped(Celda cell) {

	}

}
