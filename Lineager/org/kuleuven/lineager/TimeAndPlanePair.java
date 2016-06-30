package org.kuleuven.lineager;

public class TimeAndPlanePair {
public 	Integer time;
public	Integer plane;

	public TimeAndPlanePair(Integer time, Integer plane) {
		this.time = time;
		this.plane = plane;
	}
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof TimeAndPlanePair) {
			TimeAndPlanePair pair = (TimeAndPlanePair) arg0;
			if (pair.time.equals(time) && pair.plane.equals(plane)){
				return true;
			}

		}
		return false;
	}
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return "[[" + time.toString() + "],[" + plane.toString() + "]]";
	}
}
