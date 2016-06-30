package org.kuleuven.lineagetree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.kuleuven.collections.Pair;
import org.kuleuven.math.vector.ArrayVector;
import org.kuleuven.math.vector.Vector;

public class Division {

	public TrackedNucleus tnMum;
	public Set<TrackedNucleus> daughters;
	Integer timePoint1 = null;
	Integer timePoint2 = -1;
	Float small = 10e-9f;

	public Division(TrackedNucleus mum) {
		this.tnMum = mum;
		timePoint1 = mum.getLastTimePoint();
		daughters = new HashSet<TrackedNucleus>();
	}

	public void addDaughter(TrackedNucleus daughter) {
		daughters.add(daughter);
		int daughterTime = daughter.getFirstTimePoint();
		if (timePoint2 < 0) {
			timePoint2 = daughterTime;
		}
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer("Division: ");
		result.append(tnMum.toString());
		result.append(" to ");
		for (TrackedNucleus d : daughters) {
			result.append("|");
			result.append(d.getName());
		}
		result.append(" at ");
		result.append(timePoint2);
		return result.toString();
	}

	public int isValid() {
		if (daughters.size() != 2) {
			return daughters.size();
		}
		Object[] o = daughters.toArray();
		if (Math.abs(((TrackedNucleus) o[0]).nuclei.get(0).timePoint
				- ((TrackedNucleus) o[0]).nuclei.get(0).timePoint) > small) {
			return 2;
		} else {
			return 0;
		}
	}

	public Pair<String, Vector<Integer>> getDivisionAxis() {
		Iterator<TrackedNucleus> tns = daughters.iterator();
		TrackedNucleus tn1 = tns.next();
		TrackedNucleus tn2 = tns.next();
		char c1 = tn1.getName().charAt(tn1.getName().length() - 1);
		char c2 = tn2.getName().charAt(tn2.getName().length() - 1);
		if (c1 > c2) {
			TrackedNucleus tntemp = tn2;
			tn2 = tn1;
			tn1 = tntemp;
		}
		if (tn1 != null && tn2 != null) {
			Nucleus n1 = tn1.getNucleusForTimePoint(tn1.getFirstTimePoint());
			Nucleus n2 = tn2.getNucleusForTimePoint(tn2.getFirstTimePoint());
			Vector<Integer> result = new ArrayVector<Integer>(n1.getCoordinates());
			result.subtract(n2.getCoordinates());
			return new Pair<String, Vector<Integer>>(tnMum.getName(),result);
		}
		return null;
	}
}
