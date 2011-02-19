package ru.fuzzysearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;

public class FuzzySearch {

	public static Object loadObject(String filename) throws Exception {
		FileInputStream fileInputStream = new FileInputStream(filename);
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

		Object object = objectInputStream.readObject();

		objectInputStream.close();
		fileInputStream.close();
		return object;
	}

	public static void saveObject(Object object, String filename) throws Exception {
		FileOutputStream fileOutputStream = new FileOutputStream(filename);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

		objectOutputStream.writeObject(object);

		objectOutputStream.close();
		fileOutputStream.close();
	}

	public static interface SearchFactory {

		public String getName();

		public String getFilename();

		public Index newIndex(String[] dictionary);

		public Searcher newSearcher(Index index);
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("file.encoding", "UTF-8");

		SearchFactory extensionFactory = new SearchFactory() {

			public String getFilename() {
				return "extension_index.txt";
			}

			public String getName() {
				return "Extension Method";
			}

			public Index newIndex(String[] dictionary) {
				return new ExtensionIndexer().createIndex(dictionary);
			}

			public Searcher newSearcher(Index index) {
				return new ExtensionSearcher((ExtensionIndex) index, new RussianAlphabet(), false, 2);
			}
		};

		SearchFactory ngramFactory = new SearchFactory() {

			public String getFilename() {
				return "ngram_index.txt";
			}

			public String getName() {
				return "N-Gram Method";
			}

			public Index newIndex(String[] dictionary) {
				return new NGramIndexer(new RussianAlphabet()).createIndex(dictionary);
			}

			public Searcher newSearcher(Index index) {
				return new NGramSearcher((NGramIndex) index, new DamerauLevensteinMetric(), 2, false);
			}
		};

		SearchFactory ngram1Factory = new SearchFactory() {

			public String getFilename() {
				return "ngram1_index.txt";
			}

			public String getName() {
				return "N-Gram M1 Method";
			}

			public Index newIndex(String[] dictionary) {
				return new NGramIndexerM1(new RussianAlphabet()).createIndex(dictionary);
			}

			public Searcher newSearcher(Index index) {
				return new NGramSearcherM1((NGramIndexM1) index, new DamerauLevensteinMetric(), 2, false);
			}
		};

		SearchFactory ngram2Factory = new SearchFactory() {

			public String getFilename() {
				return "ngram2_index.txt";
			}

			public String getName() {
				return "N-Gram M2 Method";
			}

			public Index newIndex(String[] dictionary) {
				return new NGramIndexerM2(new RussianAlphabet()).createIndex(dictionary);
			}

			public Searcher newSearcher(Index index) {
				return new NGramSearcherM2((NGramIndexM2) index, new DamerauLevensteinMetric(), 2, false);
			}
		};

		SearchFactory signHashFactory = new SearchFactory() {

			public String getFilename() {
				return "signhash_index.txt";
			}

			public String getName() {
				return "SignHash Method";
			}

			public Index newIndex(String[] dictionary) {
				return new SignHashIndexer(new RussianAlphabet()).createIndex(dictionary);
			}

			public Searcher newSearcher(Index index) {
				return new SignHashSearcher((SignHashIndex) index, new DamerauLevensteinMetric(), 2);
			}
		};

		SearchFactory bktreeFactory = new SearchFactory() {

			public String getFilename() {
				return "bktree_index.txt";
			}

			public String getName() {
				return "BK Tree Method";
			}

			public Index newIndex(String[] dictionary) {
				return new BKTreeIndexer(new DamerauLevensteinMetric()).createIndex(dictionary);
			}

			public Searcher newSearcher(Index index) {
				return new BKTreeSearcher((BKTreeIndex) index, new DamerauLevensteinMetric(), 2);
			}
		};

		SearchFactory[] factories = new SearchFactory[] { extensionFactory, ngramFactory, ngram1Factory, ngram2Factory,
			signHashFactory, bktreeFactory };

		String dictFile = PATH + "dict.txt";

		String[] dictionary = Dictionary.loadDictionary(dictFile);
		long dictFileSize = new File(dictFile).length();

		System.out.println("Dictionary file size: " + (dictFileSize / (1024 * 1024)) + " MB");

		for (SearchFactory factory : factories) {
			long startTime = System.currentTimeMillis();
			Index index = factory.newIndex(dictionary);
			long endTime = System.currentTimeMillis();

			String indexFile = PATH + factory.getFilename();
			saveObject(index, indexFile);

			long indexFileSize = new File(indexFile).length();

			System.out.println(factory.getName() + " index file size: " + (indexFileSize / (1024 * 1024)) + " MB");
			System.out.println(factory.getName() + " index creation time: " + (double) (endTime - startTime) / 1000
				+ " с");
			System.out.println();
		}

		System.out.println();

		for (SearchFactory factory : factories) {
			String indexFile = PATH + factory.getFilename();
			Index index = (Index) loadObject(indexFile);

			Searcher searcher = factory.newSearcher(index);

			int step = dictionary.length / TEST_COUNT;
			long startTime = System.currentTimeMillis();

			int count = 0;

			for (int i = 0; i < dictionary.length; i += step) {
				String word = dictionary[i];
				if (word.length() >= MIN_LENGTH) {
					searcher.search(word);
					++count;
				}
			}

			long endTime = System.currentTimeMillis();

			System.out.println(factory.getName() + " search time: " + (double) (endTime - startTime) / count + " мс");
		}

		System.out.println();

		OnlineSearcher[] onlineSearchers = new OnlineSearcher[] { new BitapOnlineSearcher(new RussianAlphabet()),
			new MetricOnlineSearcher(new LevensteinMetric(), false) };

		for (OnlineSearcher onlineSearcher : onlineSearchers) {
			int step = dictionary.length / ONLINE_TEST_COUNT;
			long startTime = System.currentTimeMillis();

			int count = 0;
			for (int i = 0; i < dictionary.length; i += step) {
				String word = dictionary[i];
				if (word.length() >= MIN_LENGTH) {
					Reader reader = new BufferedReader(new FileReader(dictFile));
					onlineSearcher.search(reader, word, 2);
					++count;
				}
			}
			long endTime = System.currentTimeMillis();

			System.out.println(onlineSearcher.getClass().getSimpleName() + " search time: "
				+ (double) (endTime - startTime) / count + " мс");
		}
	}

	private static final String PATH = System.getProperty("user.home") + "/";
	private static final int TEST_COUNT = 1000;
	private static final int ONLINE_TEST_COUNT = 10;
	private static final int MIN_LENGTH = 3;
}
