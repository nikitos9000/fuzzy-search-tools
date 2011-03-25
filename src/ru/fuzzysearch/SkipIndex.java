package ru.fuzzysearch;

public class SkipIndex extends WordIndex {

	private static final long serialVersionUID = 1L;

	public SkipIndex(String[] dictionary, Alphabet alphabet, int[][] wordArray, int maxLength, int oneErrorLength,
		int twoErrorsLength, int searchLength) {
		super(dictionary);
		this.alphabet = alphabet;
		this.wordArray = wordArray;
		this.maxLength = maxLength;
		this.oneErrorLength = oneErrorLength;
		this.twoErrorsLength = twoErrorsLength;
		this.searchLength = searchLength;
	}

	public Alphabet getAlphabet() {
		return alphabet;
	}

	public int[][] getWordArray() {
		return wordArray;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public int getOneErrorLength() {
		return oneErrorLength;
	}

	public int getTwoErrorsLength() {
		return twoErrorsLength;
	}

	public int getSearchLength() {
		return searchLength;
	}

	private final Alphabet alphabet;
	private final int[][] wordArray;
	private final int maxLength;
	private final int oneErrorLength;
	private final int twoErrorsLength;
	private final int searchLength;
}
