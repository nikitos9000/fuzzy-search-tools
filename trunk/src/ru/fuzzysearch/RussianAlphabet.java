package ru.fuzzysearch;

/**
 * Русский алфавит, чего ж непонятного то
 */
public class RussianAlphabet extends SimpleAlphabet {

	private static final long serialVersionUID = 1L;

	public RussianAlphabet() {
		super('А', 'Я');
	}

	@Override
	public int mapChar(char ch) {
		if (ch == 'Ё') ch = 'Е';
		return super.mapChar(ch);
	}
}
