package ru.fuzzysearch;

import java.util.HashSet;
import java.util.Set;

public class NGramSearcherM2 extends WordSearcher {

	public NGramSearcherM2(NGramIndexM2 index, Metric metric, int maxDistance, boolean prefix) {
		super(index);
		this.metric = metric;
		this.prefix = prefix;
		this.maxDistance = maxDistance;

		dictionary = index.getDictionary();
		alphabet = index.getAlphabet();
		ngramMaps = index.getNGramMap();
		n = index.getN();
		maxLength = index.getMaxLength();
	}

	public Set<Integer> search(String string) {
		HashSet<Integer> set = new HashSet<Integer>();

		int entryLengthOffset = NGramIndexerM2.ENTRY_LENGTH_OFFSET;
		int entryLengthStart = Math.max(entryLengthOffset, string.length() - maxDistance);
		int entryLengthEnd = prefix ? maxLength : Math.min(maxLength, string.length() + maxDistance);

		for (int entryLength = entryLengthStart; entryLength <= entryLengthEnd; ++entryLength) {
			int[][][] ngramMap = ngramMaps[entryLength - entryLengthOffset];

			int stringLength = Math.min(string.length(), maxLength);
			for (int i = 0; i < stringLength - n + 1; ++i) {
				int ngram = NGramIndexerM2.getNGram(alphabet, string, i, n);

				int[][] ngramList = ngramMap[ngram];
				if (ngramList == null) continue;

				for (int j = Math.max(0, i - maxDistance); j <= Math.min(stringLength - n, i + maxDistance); ++j) {
					int[] dictIndexes = ngramList[j];
					if (dictIndexes == null) continue;

					for (int k : dictIndexes) {
						int distance = metric.getDistance(dictionary[k], string, maxDistance, prefix);
						if (distance <= maxDistance) set.add(k);
					}
				}
			}
		}
		return set;
	}

	private final Metric metric;
	private final int maxDistance;
	private final boolean prefix;
	private final String[] dictionary;
	private final Alphabet alphabet;
	private final int[][][][] ngramMaps;
	private final int n;
	private final int maxLength;
}
