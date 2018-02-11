package com.puzzlesmentales.logic.sudoku;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Representa un tablero del sudoku 9x9
 *
 * @author Antonio Paya Gonzalez
 */
public class Tablero{

	//Atributos
	private Celda[][] celdas;
	private GrupoCeldas[] sectores;
	private GrupoCeldas[] filas;
	private GrupoCeldas[] columnas;

	private boolean onChangeEnabled = true;
	private final List<OnChangeListener> changeListeners = new ArrayList<OnChangeListener>();

	public static final int SUDOKU_SIZE = 9;
	public static int DATA_VERSION_PLAIN = 0;
	public static int DATA_VERSION_1 = 1;


	/**
	 * Constructor principal de la clase que
	 * crea el tablero
	 *
	 * @param celdas celdas del tablero
	 */
	private Tablero(Celda[][] celdas) {
		this.celdas = celdas;
		iniciarTablero();
	}

	//Getters
	public Celda[][] getCeldas() {
		return this.celdas;
	}
	public Celda getCelda(int row, int col) {
		return this.celdas[row][col];
	}
	public boolean isCompleted() {
		for (int i = 0; i < SUDOKU_SIZE; i++) {
			for (int j = 0; j < SUDOKU_SIZE; j++) {
				Celda cell = celdas[i][j];
				if (cell.getValor() == 0 || !cell.isValido()) {
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * Devuelve cuantas veces se ha usado cada valor en <code>Tablero</code>.
	 * @return devuelve un map de integer e integer
	 */
	public Map<Integer, Integer> getValoresUsados() {
		Map<Integer, Integer> valuesUseCount = new HashMap<Integer, Integer>();
		for (int value = 1; value <= Tablero.SUDOKU_SIZE; value++) {
			valuesUseCount.put(value, 0);
		}
		for (int r = 0; r < Tablero.SUDOKU_SIZE; r++) {
			for (int c = 0; c < Tablero.SUDOKU_SIZE; c++) {
				int value = getCelda(r, c).getValor();
				if (value != 0) {
					valuesUseCount.put(value, valuesUseCount.get(value) + 1);
				}
			}
		}
		return valuesUseCount;
	}
	/**
	 * Devuelve true si no se ha introducido ningun valor
	 * en el tablero (tablero vacio)
	 * @return True si tablero vacio
	 */
	public boolean isEmpty() {
		for (int i = 0; i < SUDOKU_SIZE; i++) {
			for (int j = 0; j < SUDOKU_SIZE; j++) {
				Celda celda = celdas[i][j];
				if (celda.getValor() != 0)
					return false;
			}
		}
		return true;
	}

	//Metodos

	/**
	 * Metodo que crea un sudoku vacio
	 * @return new Tablero
	 */
	public static Tablero createEmpty() {
		Celda[][] celdas = new Celda[SUDOKU_SIZE][SUDOKU_SIZE];
		for (int r = 0; r < SUDOKU_SIZE; r++)
			for (int c = 0; c < SUDOKU_SIZE; c++)
				celdas[r][c] = new Celda();
		return new Tablero(celdas);
	}
	/**
	 * Genera un juego para Debugear
	 */
	public static Tablero createDebugGame() {
		Tablero debugGame = new Tablero(new Celda[][]{
				{new Celda(), new Celda(), new Celda(), new Celda(4), new Celda(5), new Celda(6), new Celda(7), new Celda(8), new Celda(9),},
				{new Celda(), new Celda(), new Celda(), new Celda(7), new Celda(8), new Celda(9), new Celda(1), new Celda(2), new Celda(3),},
				{new Celda(), new Celda(), new Celda(), new Celda(1), new Celda(2), new Celda(3), new Celda(4), new Celda(5), new Celda(6),},
				{new Celda(2), new Celda(3), new Celda(4), new Celda(), new Celda(), new Celda(), new Celda(8), new Celda(9), new Celda(1),},
				{new Celda(5), new Celda(6), new Celda(7), new Celda(), new Celda(), new Celda(), new Celda(2), new Celda(3), new Celda(4),},
				{new Celda(8), new Celda(9), new Celda(1), new Celda(), new Celda(), new Celda(), new Celda(5), new Celda(6), new Celda(7),},
				{new Celda(3), new Celda(4), new Celda(5), new Celda(6), new Celda(7), new Celda(8), new Celda(9), new Celda(1), new Celda(2),},
				{new Celda(6), new Celda(7), new Celda(8), new Celda(9), new Celda(1), new Celda(2), new Celda(3), new Celda(4), new Celda(5),},
				{new Celda(9), new Celda(1), new Celda(2), new Celda(3), new Celda(4), new Celda(5), new Celda(6), new Celda(7), new Celda(8),},
		});
		debugGame.marcarCeldasRellenasComoNoEditables();
		return debugGame;
	}

	public static Tablero createGameFromArray(int[][] tablero){
		Celda[][] nuevo = new Celda[SUDOKU_SIZE][SUDOKU_SIZE];
		for (int r = 0; r < SUDOKU_SIZE; r++)
			for (int c = 0; c < SUDOKU_SIZE; c++)
				nuevo[r][c] = new Celda(tablero[r][c]);
		Tablero game = new Tablero(nuevo);
		game.marcarCeldasRellenasComoNoEditables();
		return game;
	}

	public void marcarTodasCeldasInvalid() {
		onChangeEnabled = false;
		for (int r = 0; r < SUDOKU_SIZE; r++) {
			for (int c = 0; c < SUDOKU_SIZE; c++) {
				celdas[r][c].setValido(true);
			}
		}
		onChangeEnabled = true;
		onChange();
	}

	/**
	 * Valida los numeros del tablero segun las reglas del juego.
	 * @return True si es correcto
	 */
	public boolean validar() {
		boolean valido = true;
		marcarTodasCeldasInvalid();
		onChangeEnabled = false;

		// Validar por grupos
		for (GrupoCeldas row : filas) {
			if (!row.validar()) {
				valido = false;
			}
		}
		for (GrupoCeldas column : columnas) {
			if (!column.validar()) {
				valido = false;
			}
		}
		for (GrupoCeldas sector : sectores) {
			if (!sector.validar()) {
				valido = false;
			}
		}

		onChangeEnabled = true;
		onChange();
		return valido;
	}

	/**
	 * Marca todas las celdas como editables
	 */
	public void markAllCellsAsEditable() {
		for (int i = 0; i < SUDOKU_SIZE; i++) {
			for (int j = 0; j < SUDOKU_SIZE; j++) {
				Celda celda = celdas[i][j];
				celda.setEditable(true);
			}
		}
	}

	/**
	 * Marca todas las celdas rellenas (celdas con valor distinto de 0) como no editables.
	 */
	public void marcarCeldasRellenasComoNoEditables() {
		for (int r = 0; r < SUDOKU_SIZE; r++) {
			for (int c = 0; c < SUDOKU_SIZE; c++) {
				Celda celda = celdas[r][c];
				celda.setEditable(celda.getValor() == 0);
			}
		}
	}

	/**
	 * Inicializa el tablero. Tiene dos pasos:
	 * 1.- Se crean grupos de celdas que deben contener números únicos.
	 * 2.- Se fija el indice de filas y columnas para cada celda.
	 */
	private void iniciarTablero() {
		filas = new GrupoCeldas[SUDOKU_SIZE];
		columnas = new GrupoCeldas[SUDOKU_SIZE];
		sectores = new GrupoCeldas[SUDOKU_SIZE];
		//1
		for (int i = 0; i < SUDOKU_SIZE; i++) {
			filas[i] = new GrupoCeldas();
			columnas[i] = new GrupoCeldas();
			sectores[i] = new GrupoCeldas();
		}
		//2
		for (int i = 0; i < SUDOKU_SIZE; i++) {
			for (int j = 0; j < SUDOKU_SIZE; j++) {
				Celda celda = celdas[i][j];
				celda.iniciarTablero(
						this, i, j,
						sectores[((j / 3) * 3) + (i / 3)],
						filas[j],
						columnas[i]
				);
			}
		}
	}

	//LISTENERS
	public void addOnChangeListener(OnChangeListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("The listener is null.");
		}
		synchronized (changeListeners) {
			if (changeListeners.contains(listener)) {
				throw new IllegalStateException("Listener " + listener + "is already registered.");
			}
			changeListeners.add(listener);
		}
	}

	public void removeOnChangeListener(OnChangeListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("The listener is null.");
		}
		synchronized (changeListeners) {
			if (!changeListeners.contains(listener)) {
				throw new IllegalStateException("Listener " + listener + " was not registered.");
			}
			changeListeners.remove(listener);
		}
	}
	/**
	 * Notifica a los listeners que se a cambiado
	 */
	protected void onChange() {
		if (onChangeEnabled) {
			synchronized (changeListeners) {
				for (OnChangeListener l : changeListeners) {
					l.onChange();
				}
			}
		}
	}

	public interface OnChangeListener {
		/**
		 * Called when anything in the collection changes (cell's value, nota, etc.)
		 */
		void onChange();
	}

	//SERIALIZACION

	public static Tablero deserialize(StringTokenizer data) {
		Celda[][] cells = new Celda[SUDOKU_SIZE][SUDOKU_SIZE];
		int r = 0, c = 0;
		while (data.hasMoreTokens() && r < 9) {
			cells[r][c] = Celda.deserialize(data);
			c++;
			if (c == 9) {
				r++;
				c = 0;
			}
		}
		return new Tablero(cells);
	}
	public static Tablero deserialize(String data) {
		String[] lines = data.split("\n");
		if (lines.length == 0) {
			throw new IllegalArgumentException("Cannot deserialize Sudoku, data corrupted.");
		}

		if (lines[0].equals("version: 1")) {
			StringTokenizer st = new StringTokenizer(lines[1], "|");
			return deserialize(st);
		} else {
			return fromString(data);
		}
	}
	public static Tablero fromString(String data) {
		Celda[][] cells = new Celda[SUDOKU_SIZE][SUDOKU_SIZE];
		int pos = 0;
		for (int r = 0; r < Tablero.SUDOKU_SIZE; r++) {
			for (int c = 0; c < Tablero.SUDOKU_SIZE; c++) {
				int value = 0;
				while (pos < data.length()) {
					pos++;
					if (data.charAt(pos - 1) >= '0'
							&& data.charAt(pos - 1) <= '9') {
						// value=Integer.parseInt(data.substring(pos-1, pos));
						value = data.charAt(pos - 1) - '0';
						break;
					}
				}
				Celda cell = new Celda();
				cell.setValor(value);
				cell.setEditable(value == 0);
				cells[r][c] = cell;
			}
		}

		return new Tablero(cells);
	}

	public String serialize() {
		StringBuilder sb = new StringBuilder();
		serialize(sb);
		return sb.toString();
	}

	public void serialize(StringBuilder data) {
		data.append("version: 1\n");

		for (int r = 0; r < SUDOKU_SIZE; r++) {
			for (int c = 0; c < SUDOKU_SIZE; c++) {
				Celda cell = celdas[r][c];
				cell.serialize(data);
			}
		}
	}

	private static Pattern DATA_PATTERN_VERSION_PLAIN = Pattern.compile("^\\d{81}$");
}
