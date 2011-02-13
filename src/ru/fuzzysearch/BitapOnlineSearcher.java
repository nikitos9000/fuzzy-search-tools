package ru.fuzzysearch;

import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public class BitapOnlineSearcher extends WordOnlineSearcher {

	public BitapOnlineSearcher(Alphabet alphabet) {
		this.alphabet = alphabet;
	}

	/**
	 * {@inheritDoc} Модификация алгоритма Bitap от S. Wu и U. Manber. Ищет во входном потоке все слова, для которых
	 * word является префиксом с учетом maxDistance ошибок.
	 */
	public Set<Integer> search(Reader reader, final String word, final int maxDistance) {
		final int wordLength = word.length();
		final int endMask = 1 << wordLength;

		final Set<Integer> result = new HashSet<Integer>();

		final int[] tableRow = new int[maxDistance + 1];
		final int[] wordMask = new int[alphabet.size() + 1];

		for (int i = 0; i < wordLength; ++i)
			wordMask[normalizeChar(word.charAt(i))] |= 1 << i;

		Visitor visitor = new Visitor() {

			public void read(CharSequence string, int index) {
				for (int i = 0; i <= maxDistance; ++i)
					tableRow[i] = 1;

				for (int i = 0; i < string.length(); ++i) {
					char ch = string.charAt(i);

					int charMask = wordMask[normalizeChar(ch)];

					int oldTableCell = 0;
					int nextOldTableCell = 0;
					for (int d = 0; d <= maxDistance; ++d) {
						// Замена символа
						int newSubstitutionTableCell = (oldTableCell | (tableRow[d] & charMask)) << 1;
						// Вставка символа
						int newInsertionTableCell = oldTableCell | ((tableRow[d] & charMask) << 1);
						// Удаление символа
						int newDeletionTableCell = (nextOldTableCell | (tableRow[d] & charMask)) << 1;
						// Объединение всех операций
						int newTableCell = newSubstitutionTableCell | newInsertionTableCell | newDeletionTableCell | 1;

						oldTableCell = tableRow[d];
						tableRow[d] = newTableCell;
						nextOldTableCell = newTableCell;
					}

					if ((tableRow[maxDistance] & endMask) > 0) {
						result.add(new Integer(index));
						break;
					}
				}
			}
		};

		readText(reader, visitor);
		return result;
	}

	/**
	 * Отображает символы из алфавита на диапазон от 0 до размера алфавита - 1. Для символов, не входящих в алфавит,
	 * возвращает размер алфавита.
	 */
	private int normalizeChar(char ch) {
		int value = alphabet.mapChar(ch);
		if (value < 0) value = alphabet.size();
		return value;
	}

	private final Alphabet alphabet;
}
