package com.puzzlesmentales.igu.sudoku.input;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.puzzlesmentales.R;
import com.puzzlesmentales.igu.sudoku.HintsQueue;
import com.puzzlesmentales.igu.sudoku.TableroSudokuView;
import com.puzzlesmentales.logic.sudoku.Celda;
import com.puzzlesmentales.logic.sudoku.NotaCelda;
import com.puzzlesmentales.logic.sudoku.SudokuGame;
import com.puzzlesmentales.logic.sudoku.Tablero;

import java.util.HashMap;
import java.util.Map;

/**
 * Input Manager de modo Pad Numerico
 */
public class PadNumerico extends InputMethod {

	//Atributos

	private boolean moveCeldaSeleccionadaOnPress = true;
	private boolean colorearValoresCompletados = true;
	private boolean mostrarTotalNumeros = false;
	private Celda celdaSeleccionada;
	private ImageButton cambiarDeNumerosANotasButton;
	private Map<Integer, Button> botonesNumeros;
	private int modoEditar = MODE_EDIT_VALUE;

	private static final int MODE_EDIT_VALUE = 0;
	private static final int MODE_EDIT_NOTE = 1;

	//Getters
	public boolean isMoveCeldaSeleccionadaOnPress() {
		return moveCeldaSeleccionadaOnPress;
	}
	public boolean getColorearValoresCompletados() {
		return colorearValoresCompletados;
	}
	public boolean getMostrarTotalNumeros() {
		return mostrarTotalNumeros;
	}
	//Setters
	public void setMoveCeldaSeleccionadaOnPress(boolean moveCeldaSeleccionadaOnPress) {
		this.moveCeldaSeleccionadaOnPress = moveCeldaSeleccionadaOnPress;
	}
	public void setColorearValoresCompletados(boolean colorear) {
		colorearValoresCompletados = colorear;
	}
	public void setMostrarTotalNumeros(boolean numeros) {
		mostrarTotalNumeros = numeros;
	}

	//Metodos

	@Override
	protected void inicializar(Context context, PanelControl controlPanel,
							   SudokuGame game, TableroSudokuView tablero, HintsQueue hintsQueue) {
		super.inicializar(context, controlPanel, game, tablero, hintsQueue);
		game.getCeldas().addOnChangeListener(onCeldasChangeListener);
	}

	@Override
	protected View createControlPanelView() {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View controlPanel = inflater.inflate(R.layout.pad_numerico, null);
		botonesNumeros = new HashMap<Integer, Button>();
		botonesNumeros.put(1, (Button) controlPanel.findViewById(R.id.button_1));
		botonesNumeros.put(2, (Button) controlPanel.findViewById(R.id.button_2));
		botonesNumeros.put(3, (Button) controlPanel.findViewById(R.id.button_3));
		botonesNumeros.put(4, (Button) controlPanel.findViewById(R.id.button_4));
		botonesNumeros.put(5, (Button) controlPanel.findViewById(R.id.button_5));
		botonesNumeros.put(6, (Button) controlPanel.findViewById(R.id.button_6));
		botonesNumeros.put(7, (Button) controlPanel.findViewById(R.id.button_7));
		botonesNumeros.put(8, (Button) controlPanel.findViewById(R.id.button_8));
		botonesNumeros.put(9, (Button) controlPanel.findViewById(R.id.button_9));
		botonesNumeros.put(0, (Button) controlPanel.findViewById(R.id.button_clear));

		for (Integer num : botonesNumeros.keySet()) {
			Button b = botonesNumeros.get(num);
			b.setTag(num);
			b.setOnClickListener(botonNumeroClick);
		}

		cambiarDeNumerosANotasButton = (ImageButton) controlPanel.findViewById(R.id.cambiar_num_nota);
		cambiarDeNumerosANotasButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				modoEditar = modoEditar == MODE_EDIT_VALUE ? MODE_EDIT_NOTE : MODE_EDIT_VALUE;
				update();
			}

		});
		return controlPanel;
	}

	@Override
	public int getNameResID() {
		return R.string.numpad;
	}

	@Override
	public int getHelpResID() {
		return R.string.im_numpad_hint;
	}

	@Override
	public String getAbbrName() {
		return context.getString(R.string.numpad_abbr);
	}

	@Override
	protected void onActivated() {
		update();
		celdaSeleccionada = tableroView.getCeldaSeleccionada();
	}

	@Override
	protected void onCellSelected(Celda cell) {
		celdaSeleccionada = cell;
	}

	private OnClickListener botonNumeroClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int numeroSeleccionado = (Integer) v.getTag();
			Celda celSel = celdaSeleccionada;

			if (celSel != null) {
				switch (modoEditar) {
					case MODE_EDIT_NOTE:
						if (numeroSeleccionado == 0) {
							game.setNotaCelda(celSel, NotaCelda.VACIA);
						} else if (numeroSeleccionado > 0 && numeroSeleccionado <= 9) {
							game.setNotaCelda(celSel, celSel.getNota().anotarNumero(numeroSeleccionado));
						}
						break;
					case MODE_EDIT_VALUE:
						if (numeroSeleccionado >= 0 && numeroSeleccionado <= 9) {
							game.setValorCelda(celSel, numeroSeleccionado);
							if (isMoveCeldaSeleccionadaOnPress()) {
								tableroView.moverCeldaSeleccionadaADerecha();
							}
						}
						break;
				}
			}
		}

	};

	private Tablero.OnChangeListener onCeldasChangeListener = new Tablero.OnChangeListener() {

		@Override
		public void onChange() {
			if (activo) {
				update();
			}
		}
	};


	private void update() {
		switch (modoEditar) {
			case MODE_EDIT_NOTE:
				cambiarDeNumerosANotasButton.setImageResource(R.drawable.ic_edit_white);
				break;
			case MODE_EDIT_VALUE:
				cambiarDeNumerosANotasButton.setImageResource(R.drawable.ic_edit_grey);
				break;
		}

		Map<Integer, Integer> valoresUsados = null;
		if (colorearValoresCompletados || mostrarTotalNumeros)
			valoresUsados = game.getCeldas().getValoresUsados();

		if (colorearValoresCompletados) {
			for (Map.Entry<Integer, Integer> entry : valoresUsados.entrySet()) {
				boolean colorearValor = entry.getValue() >= Tablero.SUDOKU_SIZE;
				Button b = botonesNumeros.get(entry.getKey());
				if (colorearValor) {
                    b.getBackground().setColorFilter(0xFF1B5E20, PorterDuff.Mode.MULTIPLY);
				} else {
                    b.getBackground().setColorFilter(null);
				}
			}
		}

		if (mostrarTotalNumeros) {
			for (Map.Entry<Integer, Integer> entry : valoresUsados.entrySet()) {
				Button b = botonesNumeros.get(entry.getKey());
				b.setText(entry.getKey() + " (" + entry.getValue() + ")");
			}
		}
	}

}
