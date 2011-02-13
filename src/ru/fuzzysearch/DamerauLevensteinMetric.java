package ru.fuzzysearch;

/**
 * Метрика Дамерау-Левенштейна.
 */
public class DamerauLevensteinMetric extends Metric {

	public DamerauLevensteinMetric() {
		this(DEFAULT_LENGTH);
	}

	public DamerauLevensteinMetric(int maxLength) {
		currentRow = new int[maxLength + 1];
		previousRow = new int[maxLength + 1];
		transpositionRow = new int[maxLength + 1];
	}

	/**
	 * {@inheritDoc} Расстояние Дамерау-Левенштейна вычисляется за асимптотическое время O((max + 1) *
	 * min(first.length(), second.length()))
	 */
	@Override
	public int getDistance(CharSequence first, CharSequence second, int max) {
		int firstLength = first.length();
		int secondLength = second.length();

		if (firstLength == 0)
			return secondLength;
		else if (secondLength == 0) return firstLength;

		if (firstLength > secondLength) {
			CharSequence tmp = first;
			first = second;
			second = tmp;
			firstLength = secondLength;
			secondLength = second.length();
		}

		if (max < 0) max = secondLength;
		if (secondLength - firstLength > max) return max + 1;

		if (firstLength > currentRow.length) {
			currentRow = new int[firstLength + 1];
			previousRow = new int[firstLength + 1];
			transpositionRow = new int[firstLength + 1];
		}

		for (int i = 0; i <= firstLength; i++)
			previousRow[i] = i;

		char lastSecondCh = 0;
		for (int i = 1; i <= secondLength; i++) {
			char secondCh = second.charAt(i - 1);
			currentRow[0] = i;

			// Вычисляем только диагональную полосу шириной 2 * (max + 1)
			int from = Math.max(i - max - 1, 1);
			int to = Math.min(i + max + 1, firstLength);

			char lastFirstCh = 0;
			for (int j = from; j <= to; j++) {
				char firstCh = first.charAt(j - 1);

				// Вычисляем минимальную цену перехода в текущее состояние из предыдущих среди удаления, вставки и
				// замены соответственно.
				int cost = firstCh == secondCh ? 0 : 1;
				int value = Math.min(Math.min(currentRow[j - 1] + 1, previousRow[j] + 1), previousRow[j - 1] + cost);

				// Если вдруг была транспозиция, надо также учесть и её стоимость.
				if (firstCh == lastSecondCh && secondCh == lastFirstCh)
					value = Math.min(value, transpositionRow[j - 2] + cost);

				currentRow[j] = value;
				lastFirstCh = firstCh;
			}
			lastSecondCh = secondCh;

			int tempRow[] = transpositionRow;
			transpositionRow = previousRow;
			previousRow = currentRow;
			currentRow = tempRow;
		}
		return previousRow[firstLength];

	}

	/**
	 * {@inheritDoc} Префиксное расстояние Дамерау-Левенштейна вычисляется за асимптотическое время O((max + 1) *
	 * min(prefix.length(), string.length()))
	 */
	@Override
	public int getPrefixDistance(CharSequence string, CharSequence prefix, int max) {
		int prefixLength = prefix.length();
		if (max < 0) max = prefixLength;
		int stringLength = Math.min(string.length(), prefix.length() + max);

		if (prefixLength == 0)
			return 0;
		else if (stringLength == 0) return prefixLength;

		if (stringLength < prefixLength - max) return max + 1;

		if (prefixLength > currentRow.length) {
			currentRow = new int[prefixLength + 1];
			previousRow = new int[prefixLength + 1];
			transpositionRow = new int[prefixLength + 1];
		}

		for (int i = 0; i <= prefixLength; i++)
			previousRow[i] = i;

		int distance = Integer.MAX_VALUE;

		char lastStringCh = 0;
		for (int i = 1; i <= stringLength; i++) {
			char stringCh = string.charAt(i - 1);
			currentRow[0] = i;

			// Вычисляем только диагональную полосу шириной 2 * (max + 1)
			int from = Math.max(i - max - 1, 1);
			int to = Math.min(i + max + 1, prefixLength);

			char lastPrefixCh = 0;
			for (int j = from; j <= to; j++) {
				char prefixCh = prefix.charAt(j - 1);

				// Вычисляем минимальную цену перехода в текущее состояние из предыдущих среди удаления, вставки и
				// замены соответственно.
				int cost = prefixCh == stringCh ? 0 : 1;
				int value = Math.min(Math.min(currentRow[j - 1] + 1, previousRow[j] + 1), previousRow[j - 1] + cost);

				// Если вдруг была транспозиция, надо также учесть и её стоимость.
				if (prefixCh == lastStringCh && stringCh == lastPrefixCh)
					value = Math.min(value, transpositionRow[j - 2] + cost);

				currentRow[j] = value;
				lastPrefixCh = prefixCh;
			}
			lastStringCh = stringCh;

			// Вычисляем минимальное расстояние от заданного префикса ко всем префиксам строки, отличающимся от
			// заданного не более чем на max
			if (i >= prefixLength - max && i <= prefixLength + max && currentRow[prefixLength] < distance)
				distance = currentRow[prefixLength];

			int tempRow[] = transpositionRow;
			transpositionRow = previousRow;
			previousRow = currentRow;
			currentRow = tempRow;
		}
		return distance;
	}

	private static final int DEFAULT_LENGTH = 255;
	private int[] currentRow;
	private int[] previousRow;
	private int[] transpositionRow;
}
