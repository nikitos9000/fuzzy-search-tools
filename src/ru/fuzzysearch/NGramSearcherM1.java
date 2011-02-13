package ru.fuzzysearch;

import java.util.HashSet;
import java.util.Set;

public class NGramSearcherM1 extends WordSearcher {

	public NGramSearcherM1(NGramIndexM1 index, Metric metric, int maxDistance, boolean prefix) {
		super(index);
		this.metric = metric;
		this.maxDistance = maxDistance;
		this.prefix = prefix;

		dictionary = index.getDictionary();
		alphabet = index.getAlphabet();
		ngramMap = index.getNGramMap();
		n = index.getN();
		maxLength = index.getMaxLength();
	}

	public Set<Integer> search(String string) {
		HashSet<Integer> set = new HashSet<Integer>();

		int stringLength = Math.min(string.length(), maxLength);

		for (int i = 0; i < stringLength - n + 1; ++i) {
			int ngram = NGramIndexerM1.getNGram(alphabet, string, i, n);

			for (int j = Math.max(0, i - maxDistance); j <= Math.min(stringLength - n, i + maxDistance); ++j) {
				int[] dictIndexes = ngramMap[ngram][j];

				if (dictIndexes != null) for (int k : dictIndexes) {
					int distance = metric.getDistance(dictionary[k], string, maxDistance, prefix);
					if (distance <= maxDistance) set.add(k);
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
	private final int[][][] ngramMap;
	private final int n;
	private final int maxLength;
}
