package ru.fuzzysearch;

public class SkipIndexer implements Indexer {

	public SkipIndexer(Alphabet alphabet) {
		this(alphabet, DEFAULT_GROUP_FACTOR);
	}

	public SkipIndexer(Alphabet alphabet, int groupFactor) {
		this.alphabet = alphabet;
		this.groupFactor = groupFactor;
	}

	public Index createIndex(final String[] dictionary) {
		int dictionaryLength = Math.min(dictionary.length, MAX_DICTIONARY_LENGTH);

		int groupsLength = 1;
		for (int i = 0; i < groupFactor; ++i)
			groupsLength *= alphabet.size();

		int wordMaxLength = 0;

		int[] wordCountArray = new int[groupsLength];
		for (int i = 0; i < dictionaryLength; ++i) {
			String word = dictionary[i];
			if (word.length() > wordMaxLength) wordMaxLength = word.length();
			int wordLength = Math.min(word.length(), SEARCH_LENGTH);

			countDictionaryWord(alphabet, wordCountArray, word, -1, -1);

			if (word.length() >= ONE_ERROR_LENGTH) for (int j = 0; j < wordLength; ++j) {
				countDictionaryWord(alphabet, wordCountArray, word, j, -1);

				if (word.length() >= TWO_ERRORS_LENGTH) for (int k = j + 1; k < wordLength; ++k)
					countDictionaryWord(alphabet, wordCountArray, word, j, k);
			}
		}

		int[][] wordArray = new int[wordCountArray.length][];

		for (int i = 0; i < dictionaryLength; ++i) {
			String word = dictionary[i];
			int wordLength = Math.min(word.length(), SEARCH_LENGTH);

			putDictionaryWord(alphabet, wordArray, wordCountArray, dictionary, i, -1, -1);

			if (word.length() >= ONE_ERROR_LENGTH) for (int j = 0; j < wordLength; ++j) {
				putDictionaryWord(alphabet, wordArray, wordCountArray, dictionary, i, j, -1);

				if (word.length() >= TWO_ERRORS_LENGTH) for (int k = j + 1; k < wordLength; ++k)
					putDictionaryWord(alphabet, wordArray, wordCountArray, dictionary, i, j, k);
			}
		}

		IntComparator comparator = new IntComparator() {

			public int compare(int first, int second) {
				int firstDictIndex = getDictionaryIndex(first);
				int firstCharIndexOne = getFirstCharIndex(first);
				int firstCharIndexTwo = getSecondCharIndex(first);
				int secondDictIndex = getDictionaryIndex(second);
				int secondCharIndexOne = getFirstCharIndex(second);
				int secondCharIndexTwo = getSecondCharIndex(second);

				return stringCompare(dictionary[firstDictIndex], firstCharIndexOne, firstCharIndexTwo, false,
					dictionary[secondDictIndex], secondCharIndexOne, secondCharIndexTwo, false);
			}
		};

		for (int i = 0; i < wordArray.length; ++i)
			if (wordArray[i] != null) IntArrays.sort(wordArray[i], comparator);

		return new SkipIndex(dictionary, alphabet, wordArray, wordMaxLength, ONE_ERROR_LENGTH, TWO_ERRORS_LENGTH,
			SEARCH_LENGTH);
	}

	public static int stringCompare(CharSequence first, int firstDeleteOne, int firstDeleteTwo, boolean firstBreak,
		CharSequence second, int secondDeleteOne, int secondDeleteTwo, boolean secondBreak) {
		int firstLength = first.length();
		int secondLength = second.length();

		if (firstDeleteOne >= 0) --firstLength;
		if (firstDeleteTwo >= 0) --firstLength;
		if (secondDeleteOne >= 0) --secondLength;
		if (secondDeleteTwo >= 0) --secondLength;

		int firstIndex = 0;
		int secondIndex = 0;
		for (int i = 0; i < Math.min(firstLength, secondLength); ++i) {
			if (firstIndex == firstDeleteOne) ++firstIndex;
			if (firstIndex == firstDeleteTwo) ++firstIndex;
			if (secondIndex == secondDeleteOne) ++secondIndex;
			if (secondIndex == secondDeleteTwo) ++secondIndex;

			char firstCh = first.charAt(firstIndex);
			char secondCh = second.charAt(secondIndex);

			if (firstCh < secondCh) return -1;
			if (firstCh > secondCh) return 1;

			++firstIndex;
			++secondIndex;
		}
		if (!firstBreak && firstLength < secondLength) return -1;
		if (!secondBreak && firstLength > secondLength) return 1;

		return 0;
	}

	public static int getDictionaryIndex(int value) {
		return value >>> POWER * 2;
	}

	public static int getFirstCharIndex(int value) {
		int result = value & MAX_SEARCH_LENGTH;
		return result != MAX_SEARCH_LENGTH ? result : -1;
	}

	public static int getSecondCharIndex(int value) {
		int result = (value >>> POWER) & MAX_SEARCH_LENGTH;
		return result != MAX_SEARCH_LENGTH ? result : -1;
	}

	public static int makePrefixHash(Alphabet alphabet, CharSequence string, int firstIndex, int secondIndex) {
		// TODO: переделать в обобщенную версию
		int firstOffset = (firstIndex == 0 ? 1 : 0) + (secondIndex == 1 ? 1 : 0);
		int secondOffset = 1 + firstOffset + (firstIndex == 1 ? 1 : 0) + (secondIndex == 2 ? 1 : 0);
		int thirdOffset = 1 + secondOffset + (firstIndex == 2 ? 1 : 0) + (secondIndex == 3 ? 1 : 0);
		if (thirdOffset >= string.length()) return -1;
		return (alphabet.mapChar(string.charAt(firstOffset)) * alphabet.size() + alphabet.mapChar(string.charAt(secondOffset)))
			* alphabet.size() + alphabet.mapChar(string.charAt(thirdOffset));
	}

	private static int makeValue(int dictionaryIndex, int firstCharIndex, int secondCharIndex) {
		if (firstCharIndex < 0) firstCharIndex = MAX_SEARCH_LENGTH;
		if (secondCharIndex < 0) secondCharIndex = MAX_SEARCH_LENGTH;
		return (((dictionaryIndex << POWER) + secondCharIndex) << POWER) + firstCharIndex;
	}

	private static void countDictionaryWord(Alphabet alphabet, int[] wordCountArray, String word, int firstIndex,
		int secondIndex) {
		int prefixHash = makePrefixHash(alphabet, word, firstIndex, secondIndex);
		if (prefixHash >= 0) ++wordCountArray[prefixHash];
	}

	private static void putDictionaryWord(Alphabet alphabet, int[][] wordArray, int[] wordCountArray,
		String[] dictionary, int dictionaryIndex, int firstIndex, int secondIndex) {
		int prefixHash = makePrefixHash(alphabet, dictionary[dictionaryIndex], firstIndex, secondIndex);
		if (prefixHash < 0) return;
		if (wordArray[prefixHash] == null) wordArray[prefixHash] = new int[wordCountArray[prefixHash]];
		int index = --wordCountArray[prefixHash];
		wordArray[prefixHash][index] = makeValue(dictionaryIndex, firstIndex, secondIndex);
	}

	private static final int DEFAULT_GROUP_FACTOR = 3;
	private static final int ONE_ERROR_LENGTH = 4;
	private static final int TWO_ERRORS_LENGTH = 6;
	private static final int SEARCH_LENGTH = 9;
	private static final int POWER = 4;
	private static final int MAX_SEARCH_LENGTH = (1 << POWER) - 1;
	private static final int MAX_DICTIONARY_LENGTH = (1 << (Integer.SIZE - POWER * 2)) - 1;
	private final Alphabet alphabet;
	private final int groupFactor;
}
