package org.kuleuven.lineagetree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.kuleuven.collections.SortedList;
import org.kuleuven.math.MathEMC;

public class TrackedNucleus implements HasDistance {
	SortedList<Nucleus> nuclei;
	int index = 0;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public SortedList<Nucleus> getNuclei() {
		return nuclei;
	}

	public void setNuclei(SortedList<Nucleus> nuclei) {
		this.nuclei = nuclei;
	}
	public TrackedNucleus getCopy(){
		TrackedNucleus tn = new TrackedNucleus();
		for(Nucleus n: getNuclei()){
			tn.addNucleus(new Nucleus(n));
		}
		tn.setIndex(getIndex());
		return tn;
	}
	@Override
	public String toString() {
		if (nuclei.size() > 0) {
			String out = nuclei.get(0).toString();
			/*
			 * if (nuclei.size() > 1) { out += "--" + nuclei.get(nuclei.size() -
			 * 1).toString(); }
			 */
			return out;
		} else
			return "root";
	}

	public String getName() {
		return nuclei.get(nuclei.size() - 1).getName();
	}

	public void setName(String newName) {
		for (Nucleus nucleus : nuclei) {
			nucleus.setIdentity(newName);
		}
	}

	public int getLastTimePoint() {
		if (nuclei.size() == 0) {
			return 1;
		}
		return nuclei.get(nuclei.size() - 1).timePoint;
	}

	public int getFirstTimePoint() {
		if (nuclei.size() > 0) {
			return nuclei.get(0).timePoint;
		} else
			return 1;
	}

	public Nucleus getNucleusForTimePoint(int time) {
		if (time >= getFirstTimePoint() && time <= getLastTimePoint()) {
			int index = time - getFirstTimePoint();
			return nuclei.get(index);
		}
		return null;
	}

	public TrackedNucleus() {
		nuclei = new SortedList<Nucleus>(new Comparator<Nucleus>() {

			@Override
			public int compare(Nucleus o1, Nucleus o2) {
				if (o1.timePoint < o2.timePoint)
					return -1;
				else if (o1.timePoint > o2.timePoint)
					return 1;
				else
					return 0;
			}
		});
	}

	public Nucleus removeLastNucleus() {
		if (nuclei.size() > 0)
			return nuclei.remove(nuclei.size() - 1);
		return null;
	}
	public Nucleus removeFirstNucleus() {
		if (nuclei.size() > 0)
			return nuclei.remove(0);
		return null;
	}

	public boolean isValid() {
		boolean result = true;
		for (Nucleus n : nuclei) {
			if (n.status == 0) {
				result = false;
			}
		}
		return result;
	}

	public float getMaximumMovenment() {
		return (float) MathEMC.max(getMovements());
	}

	public float getTotalMovement(int relativeStartTime) {
		float result = 0;
		for (int i = relativeStartTime; i < (nuclei.size() - 1); i++) {
			result += (nuclei.get(i).distanceTo(nuclei.get(i + 1)));
		}
		return result;
	}

	public float getNetMovement(int relativeStartTime) {
		return (nuclei.get(relativeStartTime).distanceTo(nuclei.get(nuclei.size() - 1)));
	}

	public float getTotalMovement() {
		return getTotalMovement(0);
	}

	public List<Float> getMovements() {
		List<Float> result = new ArrayList<Float>();
		for (int i = 0; i < (nuclei.size() - 1); i++) {
			result.add(nuclei.get(i).distanceTo(nuclei.get(i + 1)));
		}
		return result;
	}

	public void addNucleus(Nucleus n) {
		nuclei.add(n);
	}

	public void mergeTrackedNucleus(TrackedNucleus tn) {
		for (Nucleus n : tn.nuclei) {
			if (!nuclei.contains(n))
				nuclei.add(n);
		}

	}

	@Override
	public double getDistance() {

		return getLastTimePoint();
	}

}
