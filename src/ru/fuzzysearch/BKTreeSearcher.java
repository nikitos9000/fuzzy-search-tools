package ru.fuzzysearch;

import java.util.HashSet;
import java.util.Set;

public class BKTreeSearcher extends WordSearcher {

	public BKTreeSearcher(BKTreeIndex index, Metric metric, int maxDistance) {
		super(index);
		Class<? extends Metric> metricClass = metric.getClass();
		Class<? extends Metric> indexMetricClass = index.getMetricClass();
		if (!metricClass.equals(indexMetricClass))
			throw new UnsupportedOperationException(
				"Metric type should be same as which has been used for creating index.");

		this.maxDistance = maxDistance;
		this.metric = metric;
		dictionary = index.getDictionary();
		nodeMap = index.getNodeMap();
		rootNode = index.getRootNode();
	}

	public Set<Integer> search(String word) {
		Set<Integer> result = new HashSet<Integer>();
		search(rootNode, word, result);
		return result;
	}

	private void search(int nodeIndex, String searchWord, Set<Integer> set) {
		int[] nodeChildren = nodeMap[nodeIndex];

		int limitDistance = maxDistance;
		if (nodeChildren != null) limitDistance += nodeChildren.length - 1;

		int distance = metric.getDistance(dictionary[nodeIndex], searchWord, limitDistance);

		if (distance <= maxDistance) {
			set.add(new Integer(nodeIndex));
			if (distance == maxDistance) return;
		}

		if (nodeChildren != null) {
			int startScore = Math.max(distance - maxDistance, 1);
			int endScore = Math.min(distance + maxDistance, nodeChildren.length - 1);
			for (int score = startScore; score <= endScore; ++score) {
				int childIndex = nodeChildren[score];
				if (childIndex >= 0) search(childIndex, searchWord, set);
			}
		}
	}

	private final int maxDistance;
	private final String[] dictionary;
	private final Metric metric;
	private final int[][] nodeMap;
	private final int rootNode;
}
