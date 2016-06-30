package org.kuleuven.lineagetree;

import java.util.Comparator;

public class NucleiComparator implements Comparator<Nucleus> {

	
	@Override
	public int compare(Nucleus o1, Nucleus o2) {
		return o1.getIndex()-o2.getIndex();
	}

}
