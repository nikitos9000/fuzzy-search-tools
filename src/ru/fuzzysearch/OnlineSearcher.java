package ru.fuzzysearch;

import java.io.Reader;
import java.util.Set;

/**
 * Онлайн поиск (без предварительного индексирования).
 */
public interface OnlineSearcher {

	/**
	 * Метод выполняет поиск заданного слова string в данных, предоставляемых reader'ом.
	 * 
	 * @param reader
	 *            поток чтения символов
	 * @param string
	 *            искомое слово
	 * @param maxDistance
	 *            максимальное количество ошибок
	 * @return множество индексов слов в исходном потоке
	 */
	public Set<Integer> search(Reader reader, String word, int maxDistance);
}
