package ru.fuzzysearch;

/**
 * Метрика
 */
public abstract class Metric {

	/**
	 * Находит расстояние между двумя последовательностями символов.
	 * 
	 * @param first
	 *            первая последовательность
	 * @param second
	 *            вторая последовательность
	 * @return расстояние от 0 до наибольшей из длин
	 */
	public int getDistance(CharSequence first, CharSequence second) {
		return getDistance(first, second, -1);
	}

	/**
	 * Находит расстояние между двумя последовательностями символов.
	 * 
	 * @param first
	 *            первая последовательность
	 * @param second
	 *            вторая последовательность
	 * @param max
	 *            максимальное расстояние
	 * @return расстояние от 0 до max включительно. Если расстояние больше max, то возвращает некоторое неопределенное
	 *         значение строго больше max.
	 */
	public abstract int getDistance(CharSequence first, CharSequence second, int max);

	/**
	 * Находит префиксное расстояние между префиксом и строкой, т.е. расстояние между заданным префиксом и
	 * соответствующим префиксом строки.
	 * 
	 * @param prefix
	 *            префикс
	 * @param string
	 *            строка
	 * @return расстояние от 0 до наибольшей из длин.
	 */
	public int getPrefixDistance(CharSequence string, CharSequence prefix) {
		return getPrefixDistance(string, prefix, -1);
	}

	/**
	 * Находит префиксное расстояние между префиксом и строкой, т.е. расстояние между заданным префиксом и
	 * соответствующим префиксом строки.
	 * 
	 * @param prefix
	 *            префикс
	 * @param string
	 *            строка
	 * @param max
	 *            максимальное расстояние
	 * @return расстояние от 0 до max включительно. Если расстояние больше max, то возвращает некоторое неопределенное
	 *         значение строго больше max.
	 */
	public abstract int getPrefixDistance(CharSequence string, CharSequence prefix, int max);

	/**
	 * В зависимости от значения параметра prefix возвращает или префиксное расстояние, или обычное.
	 * 
	 * @see #getDistance(CharSequence, CharSequence)
	 * @see #getPrefixDistance(CharSequence, CharSequence)
	 */
	public int getDistance(CharSequence first, CharSequence second, boolean prefix) {
		return prefix ? getPrefixDistance(first, second) : getDistance(first, second);
	}

	/**
	 * В зависимости от значения параметра prefix возвращает или префиксное расстояние, или обычное.
	 * 
	 * @see #getDistance(CharSequence, CharSequence, int)
	 * @see #getPrefixDistance(CharSequence, CharSequence, int)
	 */
	public int getDistance(CharSequence first, CharSequence second, int max, boolean prefix) {
		return prefix ? getPrefixDistance(first, second, max) : getDistance(first, second, max);
	}
}
