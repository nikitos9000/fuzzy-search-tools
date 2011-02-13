package ru.fuzzysearch;

import java.util.HashSet;
import java.util.Set;

public class NGramSearcher extends WordSearcher {

	public NGramSearcher(NGramIndex index, Metric metric, int maxDistance, boolean prefix) {
		super(index);
		this.metric = metric;
		this.maxDistance = maxDistance;
		this.prefix = prefix;

		dictionary = index.getDictionary();
		alphabet = index.getAlphabet();
		ngramMap = index.getNGramMap();
		n = index.getN();
	}

	public Set<Integer> search(String string) {
		Set<Integer> set = new HashSet<Integer>();

		for (int i = 0; i < string.length() - n + 1; ++i) {
			int ngram = NGramIndexer.getNGram(alphabet, string, i, n);

			int[] dictIndexes = ngramMap[ngram];

			if (dictIndexes != null) for (int k : dictIndexes) {
				int distance = metric.getDistance(dictionary[k], string, maxDistance, prefix);
				if (distance <= maxDistance) set.add(k);
			}
		}
		return set;
	}

	private final Metric metric;
	private final int maxDistance;
	private final boolean prefix;
	private final String[] dictionary;
	private final Alphabet alphabet;
	private final int[][] ngramMap;
	private final int n;
}
