package ru.fuzzysearch;

/**
 * Утилиты для бинарного поиска.
 */
public class BinarySearch {

	/**
	 * Интерфейс компаратора
	 */
	public static interface Comparator {

		public int compare(int index);
	}

	/**
	 * Находит нижнюю границу в диапазоне, равную минимальному значению, для которого компаратор вернул < 0
	 * 
	 * @param start
	 *            начало диапазона (включительно)
	 * @param end
	 *            конец диапазона (не включительно)
	 * @param comparator
	 *            компаратор
	 * @return нижнаяя граница
	 */
	public static int lowerBound(int start, int end, Comparator comparator) {
		int count = end - start;

		while (count > 0) {
			int current = start;
			int step = count / 2;
			current += step;
			if (comparator.compare(current) < 0) {
				start = ++current;
				count -= step + 1;
			}
			else count = step;
		}
		return start;
	}

	/**
	 * Находит верхнюю границу в диапазоне, следующую за максимальным значением, для которого компаратор вернул <= 0
	 * 
	 * @param start
	 *            начало диапазона (включительно)
	 * @param end
	 *            конец диапазона (не включительно)
	 * @param comparator
	 *            компаратор
	 * @return верхняя граница
	 */
	public static int upperBound(int start, int end, Comparator comparator) {
		int count = end - start;

		while (count > 0) {
			int current = start;
			int step = count / 2;
			current += step;
			if (comparator.compare(current) <= 0) {
				start = ++current;
				count -= step + 1;
			}
			else count = step;
		}
		return start;
	}
}
