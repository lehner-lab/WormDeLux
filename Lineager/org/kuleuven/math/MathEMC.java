package org.kuleuven.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kuleuven.math.space.Space;
import org.kuleuven.math.vector.ArrayVector;
import org.kuleuven.math.vector.Vector;

/**
 * 
 * @author Peter-Jan Roes ,Rob Jelier
 * @version 1.0
 */

public class MathEMC {
	public static double getVariance(List<? extends Number> m) {
		double n = 0;
		double mean = 0;
		double M2 = 0;

		for (Number x : m) {
			n = n + 1;
			double delta = x.doubleValue() - mean;
			mean = mean + delta / n;
			M2 = M2 + delta * (x.doubleValue() - mean); // This expression uses the new value
											// of mean
		}
		
		double variance = M2 / (n );//or n-1? whatever....
		return variance;

	}
	public static double max(List<? extends Number> m) {
		m = new ArrayList<Number>(m);
		if (m.size() > 0) {
			Collections.sort(m,new Comparator<Number>() {

				@Override
				public int compare(Number o1, Number o2) {
					
					return Double.compare(o1.doubleValue(),o2.doubleValue());
				}
			});
			return m.get(m.size() - 1).doubleValue();
		}
		return Double.NaN;
	}
	public static float max(float[] m) {
		float max = -Float.MAX_VALUE;
		for(float val:m){
			max=Math.max(max, val);
		}
		return max;
	}

	public static double min(List<? extends Number> m) {
		m = new ArrayList<Number>(m);
		
		if (m.size() > 0) {
			Collections.sort(m,new Comparator<Number>() {

				@Override
				public int compare(Number o1, Number o2) {
					
					return Double.compare(o1.doubleValue(),o2.doubleValue());
				}
			});
			return m.get(0).doubleValue();
		}
		return Double.NaN;
	}

	public static double median(List<? extends Number> m) {
		m = new ArrayList<Number>(m);
		
		if (m.size() > 0) {
			Collections.sort(m,new Comparator<Number>() {

				@Override
				public int compare(Number o1, Number o2) {
					
					return Double.compare(o1.doubleValue(),o2.doubleValue());
				}
			});
			int middle = m.size() / 2; // subscript of middle element
			if (m.size() % 2 == 1) {
				// Odd number of elements -- return the middle one.
				return m.get(middle).doubleValue();
			} else {
				return (m.get(middle - 1).doubleValue() + m.get(middle).doubleValue()) / 2.0;
			}
		}
		return Double.NaN;
	
	}

	public static double integerListMedian(List<Integer> m) {
		Collections.sort(m);
		int middle = m.size() / 2; // subscript of middle element
		if (m.size() % 2 == 1) {
			// Odd number of elements -- return the middle one.
			return m.get(middle);
		} else {
			return (m.get(middle - 1) + m.get(middle)) / 2.0;
		}
	}


	public static double mean(List<? extends Number> m) {
		double result = 0;
		double factor = 1d / m.size();
		for (Number entry : m) {
			result += factor * entry.doubleValue();
		}
		return result;
	}


	public static double mean(double[] m) {
		double result = 0;
		double factor = 1d / m.length;
		for (double entry : m) {
			result += factor * entry;
		}
		return result;
	}

	public static float mean(float[] m) {
		float result = 0;
		double factor = 1d / m.length;
		for (double entry : m) {
			result += factor * entry;
		}
		return result;
	}
	public static float mean(int[] m) {
		float result = 0;
		double factor = 1d / m.length;
		for (double entry : m) {
			result += factor * entry;
		}
		return result;
	}
	public static double median(double[] m) {
		Arrays.sort(m);
		int middle = m.length / 2; // subscript of middle element
		if (m.length % 2 == 1) {
			// Odd number of elements -- return the middle one.
			return m[middle];
		} else {
			return (m[middle - 1] + m[middle]) / 2.0;
		}
	}

	public static double distancePointToLineSegment(Vector point, Vector lineStart, Vector lineEnd) {
		double lineMagnitude = lineStart.squaredDistanceTo(lineEnd);

		if (lineMagnitude == 0.0d) {
			return point.distanceTo(lineStart);
		} else {
			double u = ((point.get(0) - lineStart.get(0)) * (lineEnd.get(0) - lineStart.get(0)) + (point
					.get(1) - lineStart.get(1))
					* (lineEnd.get(1) - lineStart.get(1)))
					/ lineMagnitude;

			if (u >= 0.0d && u <= 1.0d) {
				Vector intersection = new ArrayVector(Space.twoD);

				intersection.set(0, lineStart.get(0) + u * (lineEnd.get(0) - lineStart.get(0)));
				intersection.set(1, lineStart.get(1) + u * (lineEnd.get(1) - lineStart.get(1)));

				return point.distanceTo(intersection);
			} else {
				return Double.POSITIVE_INFINITY;
			}
		}
	}

	public static double getSD(List<? extends Number> times) {
		return Math.sqrt(getVariance(times));
		
	}
}