package ru.fuzzysearch;

import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public class MetricOnlineSearcher extends WordOnlineSearcher {

	public MetricOnlineSearcher(Metric metric, boolean prefix) {
		this.metric = metric;
		this.prefix = prefix;
	}

	public Set<Integer> search(Reader reader, final String word, final int maxDistance) {
		final Set<Integer> result = new HashSet<Integer>();

		Visitor visitor = new Visitor() {

			public void read(CharSequence string, int index) {
				if (metric.getDistance(string, word, maxDistance, prefix) <= maxDistance)
					result.add(new Integer(index));
			}
		};

		readText(reader, visitor);
		return result;
	}

	private final Metric metric;
	private final boolean prefix;
}
