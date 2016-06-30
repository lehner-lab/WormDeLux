package org.kuleuven.utilities;

import java.util.ArrayList;
import java.util.List;

public class ArrayConverter {
	public static double[] convertTodouble(List<Double> original) {
		double[] result = new double[original.size()];
		for (int i = 0; i < original.size(); i++) {
			result[i] = original.get(i).doubleValue();
		}
		return result;
	}

	public static double[] convertTodouble(Double[] original) {
		double[] result = new double[original.length];
		for (int i = 0; i < original.length; i++) {
			result[i] = original[i].doubleValue();
		}
		return result;
	}

	public static <K> List<K> convert(K[] original) {
		ArrayList<K> result = new ArrayList<K>(original.length);
		for (int i = 0; i < original.length; i++) {
			result.add(original[i]);
		}
		return result;
	}
}
