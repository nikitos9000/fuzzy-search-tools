package ru.fuzzysearch;

public class SignHashIndex extends WordIndex {

	private static final long serialVersionUID = 1L;

	public SignHashIndex(String[] dictionary, Alphabet alphabet, int[][] hashTable, int hashSize, int[] alphabetMap,
		int maxLength) {
		super(dictionary);
		this.alphabet = alphabet;
		this.hashTable = hashTable;
		this.hashSize = hashSize;
		this.alphabetMap = alphabetMap;
		this.maxLength = maxLength;
	}

	public Alphabet getAlphabet() {
		return alphabet;
	}

	public int[][] getHashTable() {
		return hashTable;
	}

	public int getHashSize() {
		return hashSize;
	}

	public int[] getAlphabetMap() {
		return alphabetMap;
	}

	public int getMaxLength() {
		return maxLength;
	}

	private final Alphabet alphabet;
	private final int[][] hashTable;
	private final int hashSize;
	private final int[] alphabetMap;
	private final int maxLength;
}
