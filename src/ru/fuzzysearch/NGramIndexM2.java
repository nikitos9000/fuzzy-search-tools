package ru.fuzzysearch;

public class NGramIndexM2 extends WordIndex {

	private static final long serialVersionUID = 1L;

	public NGramIndexM2(String[] dictionary, Alphabet alphabet, int[][][][] ngramMap, int n, int maxLength) {
		super(dictionary);
		this.alphabet = alphabet;
		this.ngramMap = ngramMap;
		this.n = n;
		this.maxLength = maxLength;
	}

	public Alphabet getAlphabet() {
		return alphabet;
	}

	public int[][][][] getNGramMap() {
		return ngramMap;
	}

	public int getN() {
		return n;
	}

	public int getMaxLength() {
		return maxLength;
	}

	private final Alphabet alphabet;
	private final int[][][][] ngramMap;
	private final int n;
	private final int maxLength;
}
