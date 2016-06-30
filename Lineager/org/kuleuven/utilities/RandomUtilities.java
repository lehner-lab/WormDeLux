package org.kuleuven.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.kuleuven.collections.SortedIntListSet;
import org.kuleuven.math.vector.SparseVectorInt2Float;

public class RandomUtilities {
	public static Random random = new Random();

		

	public static SparseVectorInt2Float getNormalDistributionVector(Integer size,
			Double standardDeviation, Double mean) {
		SparseVectorInt2Float result = new SparseVectorInt2Float();
		for (int i = 1; i <= size; i++) {
			Double value = random.nextGaussian() * standardDeviation + mean;
			result.values.addEntry(i, value.floatValue());
		}
		return result;

	}

	public static SparseVectorInt2Float getNormalDistributionVector(Set<Integer> entries,
			Double standardDeviation, Double mean) {
		SparseVectorInt2Float result = new SparseVectorInt2Float();
		Iterator<Integer> it = entries.iterator();
		while (it.hasNext()) {
			double value = random.nextGaussian() * standardDeviation + mean;
			result.set(it.next(), value);
		}
		return result;
	}

	public static List randomSelectorWithReplacement(Collection input, Integer numberOfSelected) {
		// zonder teruglegging!

		List result = new ArrayList();

		try {

			if (input.size() <= 1) {

				throw new Exception("Illegal number of Selected requested in randomIdSelector");
			} else {

				Object[] objects = input.toArray();
				List<Integer> temp = new ArrayList<Integer>(numberOfSelected);
				while (temp.size() < numberOfSelected) {
					temp.add(random.nextInt(input.size()));
				}
				for (Integer id : temp) {
					result.add(objects[id]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static List randomSelector(Collection input, Integer numberOfSelected) {
		// zonder teruglegging!

		List result = new ArrayList();

		try {

			if (numberOfSelected > input.size()) {

				throw new Exception("Illegal number of Selected requested in randomIdSelector");
			} else {

				Object[] objects = input.toArray();
				Set<Integer> temp = new SortedIntListSet(numberOfSelected);
				while (temp.size() < numberOfSelected) {
					temp.add(random.nextInt(input.size()));
				}
				for (Integer id : temp) {
					result.add(objects[id]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
