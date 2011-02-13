package ru.fuzzysearch;

public class NGramIndexerM1 implements Indexer {

	public NGramIndexerM1(Alphabet alphabet) {
		this(alphabet, DEFAULT_N);
	}

	public NGramIndexerM1(Alphabet alphabet, int n) {
		this.alphabet = alphabet;
		this.n = n;
	}

	public Index createIndex(String[] dictionary) {
		int maxLength = 0;

		for (String word : dictionary)
			if (word.length() > maxLength) maxLength = word.length();

		maxLength = Math.min(maxLength, n * LENGTH_FACTOR);

		int mapLength = 1;
		for (int i = 0; i < n; ++i)
			mapLength *= alphabet.size();

		int[][] ngramCountMap = new int[mapLength][];

		for (String word : dictionary) {
			int wordLength = Math.min(word.length(), maxLength);
			for (int k = 0; k < wordLength - n + 1; ++k) {
				int ngram = getNGram(alphabet, word, k, n);
				if (ngramCountMap[ngram] == null) ngramCountMap[ngram] = new int[maxLength - n + 1];
				++ngramCountMap[ngram][k];
			}
		}

		int[][][] ngramMap = new int[mapLength][][];

		for (int i = 0; i < dictionary.length; ++i) {
			String word = dictionary[i];
			int wordLength = Math.min(word.length(), maxLength);
			for (int k = 0; k < wordLength - n + 1; ++k) {
				int ngram = getNGram(alphabet, word, k, n);
				if (ngramMap[ngram] == null) ngramMap[ngram] = new int[maxLength - n + 1][];
				if (ngramMap[ngram][k] == null) ngramMap[ngram][k] = new int[ngramCountMap[ngram][k]];
				ngramMap[ngram][k][--ngramCountMap[ngram][k]] = i;
			}
		}
		return new NGramIndexM1(dictionary, alphabet, ngramMap, n, maxLength);
	}

	public static int getNGram(Alphabet alphabet, CharSequence string, int start, int n) {
		int ngram = 0;
		for (int i = start; i < start + n; ++i)
			ngram = ngram * alphabet.size() + alphabet.mapChar(string.charAt(i));
		return ngram;
	}

	private static final int LENGTH_FACTOR = 4;
	private static final int DEFAULT_N = 3;
	private final Alphabet alphabet;
	private final int n;
}
