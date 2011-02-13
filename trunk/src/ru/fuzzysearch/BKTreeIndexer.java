package ru.fuzzysearch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BKTreeIndexer implements Indexer {

	public BKTreeIndexer(Metric metric) {
		this.metric = metric;
	}

	public Index createIndex(String[] dictionary) {
		long averageLength = 0;
		for (String word : dictionary)
			averageLength += word.length();
		averageLength = Math.round((double) averageLength / dictionary.length);

		int rootWord = -1;

		for (int i = 0; i < dictionary.length && rootWord < 0; ++i)
			if (dictionary[i].length() == averageLength) rootWord = i;

		Node rootNode = new Node(rootWord);

		for (int i = 0; i < dictionary.length; ++i)
			if (i != rootNode.getIndex()) rootNode.add(dictionary, metric, i);

		int[][] nodeMap = new int[dictionary.length][];
		populate(nodeMap, rootNode);

		return new BKTreeIndex(dictionary, metric.getClass(), nodeMap, rootNode.getIndex());
	}

	private void populate(int[][] nodeMap, Node node) {
		int dictionaryIndex = node.getIndex();
		Map<Integer, Node> nodeChildren = node.getChildren();

		if (nodeMap[dictionaryIndex] == null) {
			int maximumDistance = 0;
			for (Integer distance : nodeChildren.keySet())
				if (distance > maximumDistance) maximumDistance = distance;

			nodeMap[dictionaryIndex] = new int[maximumDistance + 1];
			Arrays.fill(nodeMap[dictionaryIndex], -1);
		}

		for (Map.Entry<Integer, Node> child : nodeChildren.entrySet()) {
			Node childNode = child.getValue();
			nodeMap[dictionaryIndex][child.getKey()] = childNode.getIndex();
			populate(nodeMap, childNode);
		}
	}

	private static class Node {

		public Node(int index) {
			this.index = index;
			children = new HashMap<Integer, Node>();
		}

		public int getIndex() {
			return index;
		}

		public Map<Integer, Node> getChildren() {
			return children;
		}

		public void add(String[] dictionary, Metric metric, int childWord) {
			int distance = metric.getDistance(dictionary[index], dictionary[childWord]);

			Node child = children.get(distance);
			if (child != null)
				child.add(dictionary, metric, childWord);
			else children.put(distance, new Node(childWord));
		}

		private final int index;
		private final Map<Integer, Node> children;
	}

	private final Metric metric;
}
