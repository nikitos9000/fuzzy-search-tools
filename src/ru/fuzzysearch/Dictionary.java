package ru.fuzzysearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Dictionary {

	public static String[] createDictionary(Reader reader, Normalizer normalizer) throws IOException {
		HashSet<String> dictionarySet = new HashSet<String>();
		StringBuilder stringBuilder = new StringBuilder();

		int sym;
		while ((sym = reader.read()) >= 0) {
			char ch = (char) sym;
			if (Character.isLetter(ch))
				stringBuilder.append(ch);
			else if (stringBuilder.length() > 0) {
				String word = normalizer.normalize(stringBuilder.toString());
				if (word.length() > 0) dictionarySet.add(word);
				stringBuilder.setLength(0);
			}
		}

		if (stringBuilder.length() > 0) {
			String word = normalizer.normalize(stringBuilder.toString());
			if (word.length() > 0) dictionarySet.add(word);
		}
		String[] dictionary = dictionarySet.toArray(new String[dictionarySet.size()]);
		Arrays.sort(dictionary);
		return dictionary;
	}

	public static void saveDictionary(String[] dictionary, String filename) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		for (String line : dictionary) {
			writer.write(line);
			writer.newLine();
		}
		writer.close();
	}

	public static String[] loadDictionary(String filename) throws IOException {
		List<String> lines = new ArrayList<String>();

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = reader.readLine()) != null)
			lines.add(line);

		reader.close();
		return lines.toArray(new String[lines.size()]);
	}
}
