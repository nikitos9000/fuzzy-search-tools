package ru.fuzzysearch;

import java.io.Serializable;

/**
 * Индекс поискового алгоритма. Может быть сериализован.
 */
public interface Index extends Serializable {

	/**
	 * Словарь, по которому построен индекс.
	 * 
	 * @return словарь - массив слов.
	 */
	public String[] getDictionary();
}
