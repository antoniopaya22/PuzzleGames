package com.puzzlesmentales.igu.sudoku.input;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;

import com.puzzlesmentales.R;
import com.puzzlesmentales.logic.sudoku.Celda;
import com.puzzlesmentales.logic.sudoku.NotaCelda;
import com.puzzlesmentales.logic.sudoku.Tablero;

import java.util.Map;

/**
 * Input Manager en modo Popup
 */
public class VentanaTeclado extends InputMethod {

	//Atributos
	private boolean colorearValoresCompletados = true;
	private boolean mostrarTotalNumeros = false;
	private VentanaTecladoDialog editarCeldaPopup;
	private Celda celdaSeleccionada;

	//Getters
	public boolean getColorearValoresCompletados() {
		return colorearValoresCompletados;
	}
	public boolean getMostrarTotalNumeros() {
		return mostrarTotalNumeros;
	}

	//Setters
	public void setColorearValoresCompletados(boolean colorear) {
		colorearValoresCompletados = colorear;
	}
	public void setMostrarTotalNumeros(boolean mostrar) {
		mostrarTotalNumeros = mostrar;
	}

	//Metodos
	private void asegurarDialogoCelda() {
		if (editarCeldaPopup == null) {
			editarCeldaPopup = new VentanaTecladoDialog(context);
			editarCeldaPopup.setOnNumeroEditadoListener(onNumeroEditadoListener);
			editarCeldaPopup.setOnNotaEditadaListener(onNotaEditadaListener);
			editarCeldaPopup.setOnDismissListener(onPopupDismissedListener);
		}

	}

	@Override
	protected void onActivated() {
		tableroView.setAutoHideTouchedCellHint(false);
	}

	@Override
	protected void onDeactivated() {
		tableroView.setAutoHideTouchedCellHint(true);
	}

	@Override
	protected void onCellTapped(Celda cell) {
		celdaSeleccionada = cell;
		if (cell.isEditable()) {
			asegurarDialogoCelda();
			editarCeldaPopup.resetButtons();
			editarCeldaPopup.updateNumber(cell.getValor());
			editarCeldaPopup.updateNota(cell.getNota().getNumeros());

			Map<Integer, Integer> contadorValoresUsados = null;
			if (colorearValoresCompletados || mostrarTotalNumeros)
				contadorValoresUsados = game.getCeldas().getValoresUsados();

			if (colorearValoresCompletados) {
				for (Map.Entry<Integer, Integer> entry : contadorValoresUsados.entrySet()) {
					if (entry.getValue() >= Tablero.SUDOKU_SIZE) {
						editarCeldaPopup.colorearNumero(entry.getKey());
					}
				}
			}

			if (mostrarTotalNumeros) {
				for (Map.Entry<Integer, Integer> entry : contadorValoresUsados.entrySet()) {
					editarCeldaPopup.setContadorValores(entry.getKey(), entry.getValue());
				}
			}
			editarCeldaPopup.show();
		} else {
			tableroView.ocultarCeldaTocada();
		}
	}

	@Override
	protected void onPause() {
		if (editarCeldaPopup != null) {
			editarCeldaPopup.cancel();
		}
	}

	@Override
	public int getNameResID() {
		return R.string.popup;
	}

	@Override
	public int getHelpResID() {
		return R.string.im_popup_hint;
	}

	@Override
	public String getAbbrName() {
		return context.getString(R.string.popup_abbr);
	}

	@Override
	protected View createControlPanelView() {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.popup_num, null);
	}

	private VentanaTecladoDialog.OnNumeroEditadoListener onNumeroEditadoListener = new VentanaTecladoDialog.OnNumeroEditadoListener() {
		@Override
		public boolean onNumeroEditado(int numero) {
			if (numero != -1 && celdaSeleccionada != null) {
				game.setValorCelda(celdaSeleccionada, numero);
			}
			return true;
		}
	};

	private VentanaTecladoDialog.OnNotaEditadaListener onNotaEditadaListener = new VentanaTecladoDialog.OnNotaEditadaListener() {
		@Override
		public boolean onNoteEdit(Integer[] numeros) {
			if (celdaSeleccionada != null) {
				game.setNotaCelda(celdaSeleccionada, NotaCelda.fromIntArray(numeros));
			}
			return true;
		}
	};

	private OnDismissListener onPopupDismissedListener = new OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			tableroView.ocultarCeldaTocada();
		}
	};

}
