package ru.fuzzysearch;

/**
 * Индексатор поискового алгоритма.
 */
public interface Indexer {

	/**
	 * Создает индекс по заданному словарю.
	 * 
	 * @param dictionary
	 *            словарь
	 * @return индекс {@link Index}
	 */
	public Index createIndex(String[] dictionary);
}
