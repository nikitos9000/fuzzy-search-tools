package ru.fuzzysearch;

/**
 * Стандартный Arrays.sort(...) не предоставляет возможности сортировать примитивные типы с собственным компаратором,
 * так что вот.
 */
public class IntArrays {

	public static void sort(int[] array, IntComparator comparator) {
		int[] source = array.clone();

		mergeSort(source, array, 0, array.length, 0, comparator);
	}

	private static void mergeSort(int[] src, int[] dest, int low, int high, int off, IntComparator c) {
		int length = high - low;

		if (length < INSERTIONSORT_THRESHOLD) {
			for (int i = low; i < high; i++)
				for (int j = i; j > low && c.compare(dest[j - 1], dest[j]) > 0; j--)
					swap(dest, j, j - 1);
			return;
		}

		int destLow = low;
		int destHigh = high;
		low += off;
		high += off;
		int mid = (low + high) >> 1;
		mergeSort(dest, src, low, mid, -off, c);
		mergeSort(dest, src, mid, high, -off, c);

		if (c.compare(src[mid - 1], src[mid]) <= 0) {
			System.arraycopy(src, low, dest, destLow, length);
			return;
		}

		for (int i = destLow, p = low, q = mid; i < destHigh; i++)
			if (q >= high || p < mid && c.compare(src[p], src[q]) <= 0)
				dest[i] = src[p++];
			else dest[i] = src[q++];
	}

	private static void swap(int[] x, int a, int b) {
		int t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static final int INSERTIONSORT_THRESHOLD = 7;
}
