package com.puzzlesmentales.igu.sudoku.input;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.puzzlesmentales.R;
import com.puzzlesmentales.igu.sudoku.HintsQueue;
import com.puzzlesmentales.igu.sudoku.TableroSudokuView;
import com.puzzlesmentales.logic.sudoku.Celda;
import com.puzzlesmentales.logic.sudoku.SudokuGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Input Manager Control Panel
 * Panel de control para los inputs numericos
 * @author Antonio Paya Gonzalez
 */
public class PanelControl extends LinearLayout {

	public static final int INPUT_METHOD_POPUP = 0;
	public static final int INPUT_METHOD_NUMPAD = 1;

	private Context context;
	private TableroSudokuView tablero;
	private SudokuGame game;
	private HintsQueue queue;

	private List<InputMethod> inputMethods = new ArrayList<InputMethod>();
	private int methodActivo = -1;

	public PanelControl(Context context) {
		super(context);
		this.context = context;
	}

	public PanelControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public void initialize(TableroSudokuView tablero, SudokuGame game, HintsQueue cola) {
		this.tablero = tablero;
		this.tablero.setOnTocarCeldaListener(mOnCellTapListener);
		this.tablero.setOnCeldaSeleccionadaListener(mOnCellSelected);

		this.game = game;
		this.queue = cola;

		crearInputMethods();
	}

	/**
	 * Activa el primer método de entrada habilitado
	 */
	public void activarPrimerInputMethod() {
		asegurarInputMethods();
		if (methodActivo == -1 || !inputMethods.get(methodActivo).isEnabled()) {
			activarInputMethod(0);
		}

	}

	/**
	 * Activa el inputMethod dado.
	 * Si el método dado no está activado, activa el primer método
	 * disponible después de este método.
	 *
	 * @param idMetodo ID of method input to activate.
	 * @return
	 */
	public void activarInputMethod(int idMetodo) {
		if (idMetodo < -1 || idMetodo >= inputMethods.size())
			throw new IllegalArgumentException(String.format("Id de metodo no valido: %s.", idMetodo));
		asegurarInputMethods();

		if (methodActivo != -1)
			inputMethods.get(methodActivo).deactivate();

		boolean encontrado = false;
		int id = idMetodo;
		int temp = 0;

		if (id != -1) {
			while (!encontrado && temp <= inputMethods.size()) {
				if (inputMethods.get(id).isEnabled()) {
					asegurarControlPanel(id);
					encontrado = true;
					break;
				}
				id++;
				if (id == inputMethods.size())
					id = 0;
				temp++;
			}
		}
		if (!encontrado)
			id = -1;

		for (int i = 0; i < inputMethods.size(); i++) {
			InputMethod im = inputMethods.get(i);
			if (im.isInputMethodViewCreated())
				im.getInputMethodView().setVisibility(i == id ? View.VISIBLE : View.GONE);
		}
		methodActivo = id;
		if (methodActivo != -1) {
			InputMethod activeMethod = inputMethods.get(methodActivo);
			activeMethod.activate();
			if (queue != null)
				queue.showOneTimeHint(activeMethod.getNombreInputMethod(), activeMethod.getNameResID(), activeMethod.getHelpResID());
		}
	}

	public void activateNextInputMethod() {
		asegurarInputMethods();
		int id = methodActivo + 1;
		if (id >= inputMethods.size()) {
			if (queue != null) {
				queue.showOneTimeHint("thatIsAll", R.string.esoEsTodo, R.string.recordarModosEntrada);
			}
			id = 0;
		}
		activarInputMethod(id);
	}

	public void pause() {
		for (InputMethod im : inputMethods) {
			im.pause();
		}
	}

	/**
	 * Devuelve el inputMethod con id pasado por parametro
	 *
	 * @param id
	 * @return
	 */
	public <T extends InputMethod> T getInputMethod(int id) {
		asegurarInputMethods();
		return (T) inputMethods.get(id);
	}

	public List<InputMethod> getInputMethods() {
		return Collections.unmodifiableList(inputMethods);
	}

	public int getIndiceMethodActivo() {
		return methodActivo;
	}

	/**
	 * Ensures that all input method objects are created.
	 */
	private void asegurarInputMethods() {
		if (inputMethods.size() == 0) {
			throw new IllegalStateException("Input methods are not created yet. Call inicializar() first.");
		}

	}

	private void crearInputMethods() {
		if (inputMethods.size() == 0) {
			addInputMethod(INPUT_METHOD_POPUP, new VentanaTeclado());
			addInputMethod(INPUT_METHOD_NUMPAD, new PadNumerico());
		}
	}

	private void addInputMethod(int methodIndex, InputMethod im) {
		im.inicializar(context, this, game, tablero, queue);
		inputMethods.add(methodIndex, im);
	}

	/**
	 * Garantiza la creación del panel de control para el método de entrada dado.
	 *
	 * @param id
	 */
	private void asegurarControlPanel(int id) {
		InputMethod im = inputMethods.get(id);
		if (!im.isInputMethodViewCreated()) {
			View controlPanel = im.getInputMethodView();
			Button cambiarModo = (Button) controlPanel.findViewById(R.id.cambiarModo);
			cambiarModo.setOnClickListener(cambiarModoListener);
			this.addView(controlPanel, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		}
	}

	private TableroSudokuView.OnTocarCeldaListener mOnCellTapListener = new TableroSudokuView.OnTocarCeldaListener() {
		@Override
		public void onCellTapped(Celda celda) {
			if (methodActivo != -1 && inputMethods != null) {
				inputMethods.get(methodActivo).onCellTapped(celda);
			}
		}
	};

	private TableroSudokuView.OnCeldaSeleccionadaListener mOnCellSelected = new TableroSudokuView.OnCeldaSeleccionadaListener() {
		@Override
		public void onCellSelected(Celda celda) {
			if (methodActivo != -1 && inputMethods != null) {
				inputMethods.get(methodActivo).onCellSelected(celda);
			}
		}
	};

	private OnClickListener cambiarModoListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			activateNextInputMethod();
		}
	};


}
