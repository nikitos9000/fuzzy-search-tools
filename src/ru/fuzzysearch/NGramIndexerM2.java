package ru.fuzzysearch;

public class NGramIndexerM2 implements Indexer {

	public NGramIndexerM2(Alphabet alphabet) {
		this(alphabet, DEFAULT_N);
	}

	public NGramIndexerM2(Alphabet alphabet, int n) {
		this.alphabet = alphabet;
		this.n = n;
	}

	public static final int ENTRY_LENGTH_OFFSET = 3;

	public Index createIndex(String[] dictionary) {
		int maxLength = 0;

		for (String word : dictionary)
			if (word.length() > maxLength) maxLength = word.length();

		maxLength = Math.min(maxLength, n * LENGTH_FACTOR);

		int mapLength = 1;
		for (int i = 0; i < n; ++i)
			mapLength *= alphabet.size();

		int[][][] ngramCountMaps = new int[maxLength - ENTRY_LENGTH_OFFSET + 1][mapLength][];

		for (String word : dictionary) {
			if (word.length() < ENTRY_LENGTH_OFFSET) continue;
			int wordLength = Math.min(word.length(), maxLength);
			int[][] ngramCountMap = ngramCountMaps[wordLength - ENTRY_LENGTH_OFFSET];
			for (int k = 0; k < wordLength - n + 1; ++k) {
				int ngram = getNGram(alphabet, word, k, n);
				if (ngramCountMap[ngram] == null) ngramCountMap[ngram] = new int[maxLength - n + 1];
				++ngramCountMap[ngram][k];
			}
		}

		int[][][][] ngramMaps = new int[maxLength - ENTRY_LENGTH_OFFSET + 1][mapLength][][];

		for (int i = 0; i < dictionary.length; ++i) {
			String word = dictionary[i];
			if (word.length() < ENTRY_LENGTH_OFFSET) continue;
			int wordLength = Math.min(word.length(), maxLength);
			int[][][] ngramMap = ngramMaps[wordLength - ENTRY_LENGTH_OFFSET];
			int[][] ngramCountMap = ngramCountMaps[wordLength - ENTRY_LENGTH_OFFSET];
			for (int k = 0; k < wordLength - n + 1; ++k) {
				int ngram = getNGram(alphabet, word, k, n);
				if (ngramMap[ngram] == null) ngramMap[ngram] = new int[maxLength - n + 1][];
				if (ngramMap[ngram][k] == null) ngramMap[ngram][k] = new int[ngramCountMap[ngram][k]];
				ngramMap[ngram][k][--ngramCountMap[ngram][k]] = i;
			}
		}
		return new NGramIndexM2(dictionary, alphabet, ngramMaps, n, maxLength);
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
