package ru.fuzzysearch;

import java.io.Serializable;

/**
 * Алфавит
 */
public interface Alphabet extends Serializable {

	/**
	 * Отображает символ ch из алфавита на целое число из диапазона от 0 до size() - 1
	 */
	public int mapChar(char ch);

	/**
	 * Возвращает массив всех символов алфавита
	 */
	public char[] chars();

	/**
	 * Возвращает размер алфавита.
	 */
	public int size();
}
