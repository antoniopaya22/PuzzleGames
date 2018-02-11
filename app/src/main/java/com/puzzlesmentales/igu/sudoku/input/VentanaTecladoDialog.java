package com.puzzlesmentales.igu.sudoku.input;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.ToggleButton;

import com.puzzlesmentales.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VentanaTecladoDialog extends Dialog {
	//Atributos
	private Context context;
	private LayoutInflater inflater;
	private TabHost tabHost;
	private Map<Integer, Button> botonesNumeros = new HashMap<Integer, Button>();
	private Map<Integer, ToggleButton> botonesNotas = new HashMap<Integer, ToggleButton>();
	private int numeroSeleccionado;
	private Set<Integer> notasNumerosSeleccionados = new HashSet<Integer>();
	private OnNumeroEditadoListener onNumeroEditadoListener;
	private OnNotaEditadaListener onNotaEditadaListener;

	//Metodos

	public VentanaTecladoDialog(Context context) {
		super(context, AlertDialog.THEME_HOLO_DARK);
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		tabHost = createTabView();
		setContentView(tabHost);
	}
	public void setOnNumeroEditadoListener(OnNumeroEditadoListener l) {
		onNumeroEditadoListener = l;
	}
	public void setOnNotaEditadaListener(OnNotaEditadaListener l) {
		onNotaEditadaListener = l;
	}
	public void resetButtons() {
		for (Map.Entry<Integer, ToggleButton> entry : botonesNotas.entrySet()) {
			entry.getValue().setText("" + entry.getKey());
		}
	}
	public void updateNumber(Integer num) {
		numeroSeleccionado = num;
		for (Map.Entry<Integer, Button> entry : botonesNumeros.entrySet()) {
			Button b = entry.getValue();
			if (entry.getKey().equals(numeroSeleccionado)) {
                b.setTextColor(Color.WHITE);
				b.setTextAppearance(context, android.R.style.TextAppearance_Inverse);
				b.getBackground().setColorFilter(0x44FFFFFF, PorterDuff.Mode.MULTIPLY);
			} else {
				b.setTextAppearance(context, android.R.style.TextAppearance_Widget_Button);
                b.setTextColor(Color.WHITE);
				b.getBackground().setColorFilter(null);
			}
		}
	}

	public void updateNota(Collection<Integer> nums) {
		notasNumerosSeleccionados = new HashSet<Integer>();
		if (nums != null) {
			for (int numero : nums) {
				notasNumerosSeleccionados.add(numero);
			}
		}
		for (Integer numero : botonesNotas.keySet()) {
			botonesNotas.get(numero).setChecked(notasNumerosSeleccionados.contains(numero));
		}
	}

	public void colorearNumero(int number) {
		if (number == numeroSeleccionado) {
			botonesNumeros.get(number).getBackground().setColorFilter(0xFF2E7D32, PorterDuff.Mode.MULTIPLY);
		} else {
			botonesNumeros.get(number).getBackground().setColorFilter(0xFF1B5E20, PorterDuff.Mode.MULTIPLY);
		}
        botonesNotas.get(number).getBackground().setColorFilter(0xFF1B5E20, PorterDuff.Mode.MULTIPLY);
	}

	public void setContadorValores(int num, int cont) {
		botonesNumeros.get(num).setText(num + " (" + cont + ")");
	}

	/**
	 * Crea un view con dos Tabs una para numeros y otra para notas
	 *
	 * @return
	 */
	private TabHost createTabView() {
		TabHost tabHost = new TabHost(context, null);
		tabHost.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		linearLayout.setOrientation(LinearLayout.VERTICAL);

		TabWidget tabWidget = new TabWidget(context);
        tabWidget.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		tabWidget.setId(android.R.id.tabs);

		FrameLayout frameLayout = new FrameLayout(context);
		frameLayout.setId(android.R.id.tabcontent);

		linearLayout.addView(tabWidget);
		linearLayout.addView(frameLayout);
		tabHost.addView(linearLayout);

		tabHost.setup();

		final View numerosView = crearNumerosView();
		final View notasView = crearNotasView();

		tabHost.addTab(tabHost.newTabSpec("numero")
				.setIndicator(context.getString(R.string.numeroSeleccionado))
				.setContent(new TabHost.TabContentFactory() {

					@Override
					public View createTabContent(String tag) {
						return numerosView;
					}

				}));
		tabHost.addTab(tabHost.newTabSpec("nota")
				.setIndicator(context.getString(R.string.editarNota))
				.setContent(new TabHost.TabContentFactory() {

					@Override
					public View createTabContent(String tag) {
						return notasView;
					}

				}));

		return tabHost;
	}

	private View crearNumerosView() {
		View v = inflater.inflate(R.layout.popup_editar_valor, null);

		botonesNumeros.put(1, (Button) v.findViewById(R.id.button_1));
		botonesNumeros.put(2, (Button) v.findViewById(R.id.button_2));
		botonesNumeros.put(3, (Button) v.findViewById(R.id.button_3));
		botonesNumeros.put(4, (Button) v.findViewById(R.id.button_4));
		botonesNumeros.put(5, (Button) v.findViewById(R.id.button_5));
		botonesNumeros.put(6, (Button) v.findViewById(R.id.button_6));
		botonesNumeros.put(7, (Button) v.findViewById(R.id.button_7));
		botonesNumeros.put(8, (Button) v.findViewById(R.id.button_8));
		botonesNumeros.put(9, (Button) v.findViewById(R.id.button_9));

		for (Integer num : botonesNumeros.keySet()) {
			Button b = botonesNumeros.get(num);
			b.setTag(num);
			b.setOnClickListener(botonNumeroClickListener);
		}

		Button closeBt = (Button) v.findViewById(R.id.button_close);
		closeBt.setOnClickListener(closeButtonListener);
		Button clearBt = (Button) v.findViewById(R.id.button_clear);
		clearBt.setOnClickListener(clearButtonListener);

		return v;
	}

	private View crearNotasView() {
		View v = inflater.inflate(R.layout.popup_editar_nota, null);

		botonesNotas.put(1, (ToggleButton) v.findViewById(R.id.button_1));
		botonesNotas.put(2, (ToggleButton) v.findViewById(R.id.button_2));
		botonesNotas.put(3, (ToggleButton) v.findViewById(R.id.button_3));
		botonesNotas.put(4, (ToggleButton) v.findViewById(R.id.button_4));
		botonesNotas.put(5, (ToggleButton) v.findViewById(R.id.button_5));
		botonesNotas.put(6, (ToggleButton) v.findViewById(R.id.button_6));
		botonesNotas.put(7, (ToggleButton) v.findViewById(R.id.button_7));
		botonesNotas.put(8, (ToggleButton) v.findViewById(R.id.button_8));
		botonesNotas.put(9, (ToggleButton) v.findViewById(R.id.button_9));

		for (Integer num : botonesNotas.keySet()) {
			ToggleButton b = botonesNotas.get(num);
			b.setTag(num);
			b.setOnCheckedChangeListener(botonNotasClickListener);
		}

		Button closeBt = (Button) v.findViewById(R.id.button_close);
		closeBt.setOnClickListener(closeButtonListener);
		Button clearBt = (Button) v.findViewById(R.id.button_clear);
		clearBt.setOnClickListener(clearButtonListener);

		return v;
	}

	private View.OnClickListener botonNumeroClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Integer numero = (Integer) v.getTag();

			if (onNumeroEditadoListener != null) {
				onNumeroEditadoListener.onNumeroEditado(numero);
			}
			dismiss();
		}
	};

	private OnCheckedChangeListener botonNotasClickListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
									 boolean isChecked) {
			Integer numero = (Integer) buttonView.getTag();
			if (isChecked) {
				notasNumerosSeleccionados.add(numero);
			} else {
				notasNumerosSeleccionados.remove(numero);
			}
		}

	};

	private View.OnClickListener clearButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String tabActual = tabHost.getCurrentTabTag();

			if (tabActual.equals("numero")) {
				if (onNumeroEditadoListener != null) {
					onNumeroEditadoListener.onNumeroEditado(0);
				}
				dismiss();
			} else {
				for (ToggleButton b : botonesNotas.values()) {
					b.setChecked(false);
					notasNumerosSeleccionados.remove(b.getTag());
				}
			}
		}
	};

	private View.OnClickListener closeButtonListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (onNotaEditadaListener != null) {
				Integer[] nums = new Integer[notasNumerosSeleccionados.size()];
				onNotaEditadaListener.onNoteEdit(notasNumerosSeleccionados.toArray(nums));
			}
			dismiss();
		}
	};

	public interface OnNumeroEditadoListener {
		boolean onNumeroEditado(int numero);
	}

	public interface OnNotaEditadaListener {
		boolean onNoteEdit(Integer[] numeros);
	}

}
