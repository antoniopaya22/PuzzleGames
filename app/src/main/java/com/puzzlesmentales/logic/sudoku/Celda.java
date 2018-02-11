package com.puzzlesmentales.logic.sudoku;
import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Celda del Sudoku.
 * Cada celda tiene un valor, algunas notas adjuntas y algunas notas básicas
 * (si es editable y válido).
 *
 * @author Antonio Paya Gonzalez
 */
public class Celda{

	//Atributos

	private Tablero tablero;
	private final Object tableroLock = new Object();
	private int rowIndex = -1;
	private int colIndex = -1;
	//Grupos
	private GrupoCeldas row; // Fila que contiena esta celda
	private GrupoCeldas col; // Columna que contiene a esta celda
	private GrupoCeldas sector; // Sector que contiene a esta celda

	private int valor;
	private NotaCelda nota;
	private boolean editable;
	private boolean isValido;

	/**
	 * Constructor que crea una celda editable vacia.
	 */
	public Celda() {
		this(0, new NotaCelda(), true, true);
	}

	/**
	 * Crea una celda vacia editable con un valor concreto
	 * @param value Valor de la celda
	 */
	public Celda(int value) {
		this(value, new NotaCelda(), true, true);
	}

	/**
	 * Constructor privado base de la clase celda
	 * @param value Valor de la celda
	 * @param note Nota de la celda
	 * @param editable True si es editable
	 * @param valid True si es valido
	 */
	private Celda(int value, NotaCelda note, boolean editable, boolean valid) {
		//Comprobar que el valor es correcto
		if (value < 0 || value > 9) {
			throw new IllegalArgumentException("El valor tiene que estar entre 1 y 9");
		}
		this.valor = value;
		this.nota = note;
		this.editable = editable;
		this.isValido = valid;
	}

	//Getters
	public int getRowIndex() {
		return this.rowIndex;
	}
	public int getColumnIndex() {
		return this.colIndex;
	}
	public GrupoCeldas getSector() {
		return this.sector;
	}
	public GrupoCeldas getRow() {
		return this.row;
	}
	public GrupoCeldas getColumn() {
		return this.col;
	}
	public int getValor() {
		return this.valor;
	}
	public NotaCelda getNota() {
		return this.nota;
	}
	public boolean isValido() {
		return this.isValido;
	}
	public boolean isEditable() {
		return this.editable;
	}

	//Setters
	public void setValor(int value) {
		if (value < 0 || value > 9) {
			throw new IllegalArgumentException("El valor tiene que estar entre 1 y 9");
		}
		this.valor = value;
		onChange();
	}
	public void setNota(NotaCelda note) {
		this.nota = note;
		onChange();
	}
	public void setEditable(Boolean editable) {
		this.editable = editable;
		onChange();
	}
	public void setValido(Boolean valido) {
		this.isValido = valido;
		onChange();
	}

	//METODOS

	/**
	 * Se llama cuando se añade <code>Celda</code> a {@link Tablero}.
	 *
	 * @param rowIndex indice de la fila de la celda
	 * @param colIndex indice de la columna de la celda
	 * @param sector   sector de la celda
	 * @param row      fila
	 * @param column   columna
	 */
	protected void iniciarTablero(Tablero tablero, int rowIndex, int colIndex, GrupoCeldas sector,
								  GrupoCeldas row, GrupoCeldas column) {
		synchronized (tableroLock) {
			this.tablero = tablero;
		}
		this.rowIndex = rowIndex;
		this.colIndex = colIndex;
		this.sector = sector;
		this.row = row;
		this.col = column;
		sector.addCelda(this);
		row.addCelda(this);
		column.addCelda(this);
	}

	/**
	 * Notifica al tablero que algo ha cambiado
	 */
	private void onChange() {
		synchronized (tableroLock) {
			if (tablero != null) {
				tablero.onChange();
			}

		}
	}


	//SERIALIZACION

	public static Celda deserialize(StringTokenizer data) {
		Celda celdaAuxiliar = new Celda();
		celdaAuxiliar.setValor(Integer.parseInt(data.nextToken()));
		celdaAuxiliar.setNota(NotaCelda.deserialize(data.nextToken()));
		celdaAuxiliar.setEditable(data.nextToken().equals("1"));
		return celdaAuxiliar;
	}

	public static Celda deserialize(String datosCelda) {
		StringTokenizer data = new StringTokenizer(datosCelda, "|");
		return deserialize(data);
	}

	public void serialize(StringBuilder data) {
		data.append(valor).append("|");
		if (nota == null || nota.isEmpty()) {
			data.append("-").append("|");
		} else {
			nota.serialize(data);
			data.append("|");
		}
		data.append(editable ? "1" : "0").append("|");
	}

	public String serialize() {
		StringBuilder sb = new StringBuilder();
		serialize(sb);
		return sb.toString();
	}

}
