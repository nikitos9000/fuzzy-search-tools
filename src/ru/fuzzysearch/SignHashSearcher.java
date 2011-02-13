package ru.fuzzysearch;

import java.util.HashSet;
import java.util.Set;

/**
 * Поисковый модуль метода хеширования по сигнатуре.
 * 
 * @see SignHashIndexer
 */
public class SignHashSearcher extends WordSearcher {

	public SignHashSearcher(SignHashIndex index, Metric metric, int maxDistance) {
		super(index);
		this.metric = metric;
		this.maxDistance = maxDistance;
		dictionary = index.getDictionary();
		alphabet = index.getAlphabet();
		hashTable = index.getHashTable();
		hashSize = index.getHashSize();
		alphabetMap = index.getAlphabetMap();
	}

	/**
	 * Производит поиск по всем словам из словаря, сигнатурный хеш которых отличается от исходного не более чем в
	 * maxDistance битах.
	 */
	public Set<Integer> search(String string) {
		Set<Integer> result = new HashSet<Integer>();

		int stringHash = SignHashIndexer.makeHash(alphabet, alphabetMap, string);
		populate(string, stringHash, result);
		if (maxDistance > 0) hashPopulate(string, stringHash, 0, hashSize, result, maxDistance - 1);
		return result;
	}

	/**
	 * Вносит перебирает все хеши, отличающиеся от исходного на 1 бит на какой-либо позиции. При добавлении или удалении
	 * символа из слова в сигнатурном хеше изменяются 0 или 1 бит, при замене символа - от 0 до 2 бит.
	 */
	private void hashPopulate(String query, int hash, int start, int hashSize, Set<Integer> set, int depth) {
		for (int i = start; i < hashSize; ++i) {
			int queryHash = hash ^ (1 << i);
			populate(query, queryHash, set);
			if (depth > 0) hashPopulate(query, queryHash, i + 1, hashSize, set, depth - 1);
		}
	}

	/**
	 * Перебирает все слова в словаре с заданным хешем, записывая в результат только слова, удоволетворяющие ограничению
	 * при данной метрике.
	 */
	private void populate(String query, int queryHash, Set<Integer> set) {
		int[] hashBucket = hashTable[queryHash];
		if (hashBucket != null) for (int dictionaryIndex : hashBucket) {
			String word = dictionary[dictionaryIndex];
			if (metric.getDistance(query, word, maxDistance) <= maxDistance) set.add(new Integer(dictionaryIndex));
		}
	}

	private final Metric metric;
	private final int maxDistance;
	private final String[] dictionary;
	private final Alphabet alphabet;
	private final int[][] hashTable;
	private final int hashSize;
	private final int[] alphabetMap;
}
