package ru.fuzzysearch;

public class NGramIndexer implements Indexer {

	public NGramIndexer(Alphabet alphabet) {
		this(alphabet, DEFAULT_N);
	}

	public NGramIndexer(Alphabet alphabet, int n) {
		this.alphabet = alphabet;
		this.n = n;
	}

	public Index createIndex(String[] dictionary) {
		int mapLength = 1;
		for (int i = 0; i < n; ++i)
			mapLength *= alphabet.size();

		int[] ngramCountMap = new int[mapLength];

		int maxLength = 0;

		for (String word : dictionary) {
			if (word.length() > maxLength) maxLength = word.length();

			for (int k = 0; k < word.length() - n + 1; ++k) {
				int ngram = getNGram(alphabet, word, k, n);
				++ngramCountMap[ngram];
			}
		}

		int[][] ngramMap = new int[mapLength][];

		for (int i = 0; i < dictionary.length; ++i) {
			String word = dictionary[i];
			for (int k = 0; k < word.length() - n + 1; ++k) {
				int ngram = getNGram(alphabet, word, k, n);
				if (ngramMap[ngram] == null) ngramMap[ngram] = new int[ngramCountMap[ngram]];
				ngramMap[ngram][--ngramCountMap[ngram]] = i;
			}
		}

		return new NGramIndex(dictionary, alphabet, ngramMap, n, maxLength);
	}

	public static int getNGram(Alphabet alphabet, CharSequence string, int start, int n) {
		int ngram = 0;
		for (int i = start; i < start + n; ++i)
			ngram = ngram * alphabet.size() + alphabet.mapChar(string.charAt(i));
		return ngram;
	}

	private static final int DEFAULT_N = 3;
	private final Alphabet alphabet;
	private final int n;
}
