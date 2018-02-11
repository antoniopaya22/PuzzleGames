package com.puzzlesmentales.logic.sudoku;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Nota de una celda
 *
 * @author Antonio Paya Gonzalez
 */
public class NotaCelda{

	//Atributos
	private final Set<Integer> numeros;
	public static final NotaCelda VACIA = new NotaCelda();

	/**
	 * Constructor base de la clase
	 */
	public NotaCelda() {
		this.numeros = Collections.unmodifiableSet(new HashSet<Integer>());
	}

	private NotaCelda(Set<Integer> numeros) {
		this.numeros = Collections.unmodifiableSet(numeros);
	}


	//Getters
	public Set<Integer> getNumeros() {
		return this.numeros;
	}
	public boolean isEmpty() {
		return this.numeros.size() == 0;
	}

	//Metodos

	public NotaCelda addNumber(int numero) {
		if (numero < 1 || numero > 9)
			throw new IllegalArgumentException("El numero debe estar entre 1 y 9");
		Set<Integer> aux = new HashSet<Integer>(getNumeros());
		aux.add(numero);
		return new NotaCelda(aux);
	}
	public NotaCelda clear() {
		return new NotaCelda();
	}

	/**
	 * Cambia el número de nota: si el número ya está anotado, se eliminará, de lo contrario se añadirá.
	 *
	 * @param numero numero a anotar
	 * @return new NotaCelda
	 */
	public NotaCelda anotarNumero(int numero) {
		if (numero < 1 || numero > 9)
			throw new IllegalArgumentException("El numero debe estar entre 1 y 9");
		Set<Integer> numerosAnotados = new HashSet<Integer>(getNumeros());
		if (numerosAnotados.contains(numero)) {
			numerosAnotados.remove(numero);
		} else {
			numerosAnotados.add(numero);
		}
		return new NotaCelda(numerosAnotados);
	}

	public static NotaCelda fromIntArray(Integer[] nums) {
		Set<Integer> numerosAnotados = new HashSet<Integer>();

		for (Integer n : nums) {
			numerosAnotados.add(n);
		}

		return new NotaCelda(numerosAnotados);
	}


	//SERIALIZACION
	public static NotaCelda deserialize(String note) {
		Set<Integer> numeros = new HashSet<Integer>();
		if (note != null && !note.equals("")) {
			StringTokenizer tokenizer = new StringTokenizer(note, ",");
			while (tokenizer.hasMoreTokens()) {
				String value = tokenizer.nextToken();
				if (!value.equals("-")) {
					numeros.add(Integer.parseInt(value));
				}
			}
		}
		return new NotaCelda(numeros);
	}
	public void serialize(StringBuilder data) {
		if (numeros.size() == 0) {
			data.append("-");
		} else {
			for (Integer num : numeros) {
				data.append(num).append(",");
			}
		}
	}
	public String serialize() {
		StringBuilder sb = new StringBuilder();
		serialize(sb);
		return sb.toString();
	}
}
