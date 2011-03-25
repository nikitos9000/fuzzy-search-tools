package ru.fuzzysearch;

import java.util.HashSet;
import java.util.Set;

public class SkipSearcher extends WordSearcher {

	public SkipSearcher(SkipIndex index, Metric metric, boolean prefix) {
		super(index);
		this.metric = metric;
		this.prefix = prefix;

		dictionary = index.getDictionary();
		alphabet = index.getAlphabet();
		wordArray = index.getWordArray();
		oneErrorLength = index.getOneErrorLength();
		twoErrorsLength = index.getTwoErrorsLength();
		searchLength = index.getSearchLength();
	}

	public Set<Integer> search(String string) {
		Set<Integer> set = new HashSet<Integer>();

		CharSequence query = string.substring(0, Math.min(string.length(), searchLength));

		int maxDistance = getMaxDistance(string.length());

		if (string.length() >= oneErrorLength)
			for (int i = 0; i < query.length(); ++i) {
				if (string.length() >= twoErrorsLength)
					for (int j = i + 1; j < query.length(); ++j)
						populate(query, i, j, string, set, maxDistance);
				else populate(query, i, -1, string, set, maxDistance);
			}
		else populate(query, -1, -1, string, set, maxDistance);

		return set;
	}

	private void populate(final CharSequence query, final int queryFirstIndex, final int querySecondIndex,
		CharSequence string, Set<Integer> set, int maxDistance) {

		int hash = SkipIndexer.makePrefixHash(alphabet, query, queryFirstIndex, querySecondIndex);
		if (hash < 0) return;
		final int[] wordList = wordArray[hash];

		BinarySearch.Comparator comparator = new BinarySearch.Comparator() {

			public int compare(int index) {
				int value = wordList[index];
				int wordIndex = SkipIndexer.getDictionaryIndex(value);
				int wordFirstIndex = SkipIndexer.getFirstCharIndex(value);
				int wordSecondIndex = SkipIndexer.getSecondCharIndex(value);

				return SkipIndexer.stringCompare(dictionary[wordIndex], wordFirstIndex, wordSecondIndex, false, query,
					queryFirstIndex, querySecondIndex, true);
			}
		};

		int lower = BinarySearch.lowerBound(0, wordList.length, comparator);
		int upper = BinarySearch.upperBound(lower, wordList.length, comparator);

		for (int i = lower; i < upper; ++i) {
			int value = wordList[i];
			int dictIndex = SkipIndexer.getDictionaryIndex(value);

			CharSequence dictWord = dictionary[dictIndex];
			CharSequence stringWord = string;

			int distance = metric.getDistance(dictWord, stringWord, maxDistance, prefix);
			if (distance <= maxDistance) set.add(new Integer(dictIndex));
		}
	}

	private int getMaxDistance(int length) {
		if (length < oneErrorLength) return 0;
		if (length < twoErrorsLength) return 1;
		return 2;
	}

	private final Metric metric;
	private final boolean prefix;
	private final String[] dictionary;
	private final Alphabet alphabet;
	private final int[][] wordArray;
	private final int oneErrorLength;
	private final int twoErrorsLength;
	private final int searchLength;

}
