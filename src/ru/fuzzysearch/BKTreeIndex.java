package ru.fuzzysearch;

public class BKTreeIndex extends WordIndex {

	private static final long serialVersionUID = 1L;

	public BKTreeIndex(String[] dictionary, Class<? extends Metric> metricClass, int[][] nodeMap, int rootNode) {
		super(dictionary);
		this.metricClass = metricClass;
		this.nodeMap = nodeMap;
		this.rootNode = rootNode;
	}

	public Class<? extends Metric> getMetricClass() {
		return metricClass;
	}

	public int[][] getNodeMap() {
		return nodeMap;
	}

	public int getRootNode() {
		return rootNode;
	}

	private final Class<? extends Metric> metricClass;
	private final int[][] nodeMap;
	private final int rootNode;
}
