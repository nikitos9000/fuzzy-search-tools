package ru.fuzzysearch;

/**
 * Алфавит для непрерывных диапазонов символов (напр. А-Я)
 */
public class SimpleAlphabet implements Alphabet {

	private static final long serialVersionUID = 1L;

	public SimpleAlphabet(char min, char max) {
		this.min = min;
		this.max = max;

		chars = new char[max - min + 1];

		int index = 0;
		for (char ch = min; ch <= max; ++ch)
			chars[index++] = ch;
	}

	public int mapChar(char ch) {
		if (ch < min || ch > max) return -1;
		return ch - min;
	}

	public char[] chars() {
		return chars;
	}

	public int size() {
		return chars.length;
	}

	private final char min;
	private final char max;
	private final char[] chars;
}
