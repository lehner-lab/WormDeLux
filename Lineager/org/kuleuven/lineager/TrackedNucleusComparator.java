package org.kuleuven.lineager;

import java.util.Comparator;

import org.kuleuven.lineagetree.TrackedNucleus;

public class TrackedNucleusComparator implements Comparator<TrackedNucleus> {

	@Override
	public int compare(TrackedNucleus o1, TrackedNucleus o2) {
		return o1.getName().compareTo(o2.getName());
	}

}
