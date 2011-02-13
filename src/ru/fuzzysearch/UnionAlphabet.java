package ru.fuzzysearch;

/**
 * Алфавит, являющийся объединением нескольких алфавитов.
 */
public class UnionAlphabet implements Alphabet {

	private static final long serialVersionUID = 1L;

	public UnionAlphabet(Alphabet... alphabets) {
		this.alphabets = alphabets;

		int charsLength = 0;
		for (Alphabet alphabet : alphabets)
			charsLength += alphabet.size();

		chars = new char[charsLength];

		int index = 0;
		for (Alphabet alphabet : alphabets)
			for (char ch : alphabet.chars())
				chars[index++] = ch;
	}

	public int mapChar(char ch) {
		int index;
		for (Alphabet alphabet : alphabets)
			if ((index = alphabet.mapChar(ch)) >= 0) return index;
		return -1;
	}

	public char[] chars() {
		return chars;
	}

	public int size() {
		return chars.length;
	}

	private final Alphabet[] alphabets;
	private final char[] chars;
}
