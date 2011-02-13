package ru.fuzzysearch.phonetic;

/**
 * Encodes a string into a metaphone value.
 * <p>
 * Initial Java implementation by <CITE>William B. Brogden. December, 1997</CITE>. Permission given by
 * <CITE>wbrogden</CITE> for code to be used anywhere.
 * </p>
 * <p>
 * <CITE>Hanging on the Metaphone</CITE> by <CITE>Lawrence Philips</CITE> in <CITE>Computer Language of Dec. 1990, p
 * 39.</CITE>
 * </p>
 * <p>
 * Note, that this does not match the algorithm that ships with PHP, or the algorithm found in the Perl <a
 * href="http://search.cpan.org/~mschwern/Text-Metaphone-1.96/Metaphone.pm">Text:Metaphone-1.96</a>. They have had
 * undocumented changes from the originally published algorithm. For more information, see <a
 * href="https://issues.apache.org/jira/browse/CODEC-57">CODEC-57</a>.
 * </p>
 * 
 * @author Apache Software Foundation
 * @version $Id: Metaphone.java 797690 2009-07-24 23:28:35Z ggregory $
 */
public class MetaphoneEnglish {

	/**
	 * Find the metaphone value of a String. This is similar to the soundex algorithm, but better at finding similar
	 * sounding words. All input is converted to upper case. Limitations: Input format is expected to be a single ASCII
	 * word with only characters in the A - Z range, no punctuation or numbers.
	 * 
	 * @param string
	 *            String to find the metaphone code for
	 * @return A metaphone code corresponding to the String supplied
	 */
	public String encode(String string) {
		boolean hard = false;
		if ((string == null) || (string.length() == 0))
			return "";
		// single character is itself
		if (string.length() == 1)
			return string.toUpperCase(java.util.Locale.ENGLISH);

		char[] inwd = string.toUpperCase(java.util.Locale.ENGLISH).toCharArray();

		StringBuffer local = new StringBuffer(40); // manipulate
		StringBuffer code = new StringBuffer(10); // output
		// handle initial 2 characters exceptions
		switch (inwd[0]) {
			case 'K':
			case 'G':
			case 'P': /* looking for KN, etc */
				if (inwd[1] == 'N')
					local.append(inwd, 1, inwd.length - 1);
				else local.append(inwd);
				break;
			case 'A': /* looking for AE */
				if (inwd[1] == 'E')
					local.append(inwd, 1, inwd.length - 1);
				else local.append(inwd);
				break;
			case 'W': /* looking for WR or WH */
				if (inwd[1] == 'R') { // WR -> R
					local.append(inwd, 1, inwd.length - 1);
					break;
				}
				if (inwd[1] == 'H') {
					local.append(inwd, 1, inwd.length - 1);
					local.setCharAt(0, 'W'); // WH -> W
				}
				else local.append(inwd);
				break;
			case 'X': /* initial X becomes S */
				inwd[0] = 'S';
				local.append(inwd);
				break;
			default:
				local.append(inwd);
		} // now local has working string with initials fixed

		int wdsz = local.length();
		int n = 0;

		while ((n < wdsz)) { // max code size of 4 works well
			char symb = local.charAt(n);
			// remove duplicate letters except C
			if ((symb != 'C') && (isPreviousChar(local, n, symb)))
				n++;
			else { // not dup
				switch (symb) {
					case 'A':
					case 'E':
					case 'I':
					case 'O':
					case 'U':
						if (n == 0)
							code.append(symb);
						break; // only use vowel if leading char
					case 'B':
						if (isPreviousChar(local, n, 'M') && isLastChar(wdsz, n))
							break;
						code.append(symb);
						break;
					case 'C': // lots of C special cases
						/* discard if SCI, SCE or SCY */
						if (isPreviousChar(local, n, 'S') && !isLastChar(wdsz, n)
							&& (FRONTV.indexOf(local.charAt(n + 1)) >= 0))
							break;
						if (regionMatch(local, n, "CIA")) { // "CIA" -> X
							code.append('X');
							break;
						}
						if (!isLastChar(wdsz, n) && (FRONTV.indexOf(local.charAt(n + 1)) >= 0)) {
							code.append('S');
							break; // CI,CE,CY -> S
						}
						if (isPreviousChar(local, n, 'S') && isNextChar(local, n, 'H')) { // SCH->sk
							code.append('K');
							break;
						}
						if (isNextChar(local, n, 'H')) { // detect CH
							if ((n == 0) && (wdsz >= 3) && isVowel(local, 2))
								code.append('K');
							else code.append('X'); // CHvowel -> X
						}
						else code.append('K');
						break;
					case 'D':
						if (!isLastChar(wdsz, n + 1) && isNextChar(local, n, 'G')
							&& (FRONTV.indexOf(local.charAt(n + 2)) >= 0)) { // DGE DGI DGY -> J
							code.append('J');
							n += 2;
						}
						else code.append('T');
						break;
					case 'G': // GH silent at end or before consonant
						if (isLastChar(wdsz, n + 1) && isNextChar(local, n, 'H'))
							break;
						if (!isLastChar(wdsz, n + 1) && isNextChar(local, n, 'H') && !isVowel(local, n + 2))
							break;
						if ((n > 0) && (regionMatch(local, n, "GN") || regionMatch(local, n, "GNED")))
							break; // silent
						// G
						if (isPreviousChar(local, n, 'G')) // NOTE: Given that duplicated chars are removed, I don't see
							// how this can ever be true
							hard = true;
						else hard = false;
						if (!isLastChar(wdsz, n) && (FRONTV.indexOf(local.charAt(n + 1)) >= 0) && (!hard))
							code.append('J');
						else code.append('K');
						break;
					case 'H':
						if (isLastChar(wdsz, n))
							break; // terminal H
						if ((n > 0) && (VARSON.indexOf(local.charAt(n - 1)) >= 0))
							break;
						if (isVowel(local, n + 1))
							code.append('H'); // Hvowel
						break;
					case 'F':
					case 'J':
					case 'L':
					case 'M':
					case 'N':
					case 'R':
						code.append(symb);
						break;
					case 'K':
						if (n > 0) { // not initial
							if (!isPreviousChar(local, n, 'C'))
								code.append(symb);
						}
						else code.append(symb); // initial K
						break;
					case 'P':
						if (isNextChar(local, n, 'H')) // PH -> F
							code.append('F');
						else code.append(symb);
						break;
					case 'Q':
						code.append('K');
						break;
					case 'S':
						if (regionMatch(local, n, "SH") || regionMatch(local, n, "SIO") || regionMatch(local, n, "SIA"))
							code.append('X');
						else code.append('S');
						break;
					case 'T':
						if (regionMatch(local, n, "TIA") || regionMatch(local, n, "TIO")) {
							code.append('X');
							break;
						}
						if (regionMatch(local, n, "TCH")) // Silent if in "TCH"
							break;
						// substitute numeral 0 for TH (resembles theta after all)
						if (regionMatch(local, n, "TH"))
							code.append('0');
						else code.append('T');
						break;
					case 'V':
						code.append('F');
						break;
					case 'W':
					case 'Y': // silent if not followed by vowel
						if (!isLastChar(wdsz, n) && isVowel(local, n + 1))
							code.append(symb);
						break;
					case 'X':
						code.append('K');
						code.append('S');
						break;
					case 'Z':
						code.append('S');
						break;
				} // end switch
				n++;
			} // end else from symb != 'C'
		}
		return code.toString();
	}

	private boolean isVowel(StringBuffer string, int index) {
		return VOWELS.indexOf(string.charAt(index)) >= 0;
	}

	private boolean isPreviousChar(StringBuffer string, int index, char c) {
		boolean matches = false;
		if (index > 0 && index < string.length())
			matches = string.charAt(index - 1) == c;
		return matches;
	}

	private boolean isNextChar(StringBuffer string, int index, char c) {
		boolean matches = false;
		if (index >= 0 && index < string.length() - 1)
			matches = string.charAt(index + 1) == c;
		return matches;
	}

	private boolean regionMatch(StringBuffer string, int index, String test) {
		boolean matches = false;
		if (index >= 0 && (index + test.length() - 1) < string.length()) {
			String substring = string.substring(index, index + test.length());
			matches = substring.equals(test);
		}
		return matches;
	}

	private boolean isLastChar(int wdsz, int n) {
		return n + 1 == wdsz;
	}

	private static final String VOWELS = "AEIOU";
	private static final String FRONTV = "EIY";
	private static final String VARSON = "CSPTG";
}
