package org.kuleuven.lineager;

import org.kuleuven.collections.SortedList;
import org.kuleuven.lineagetree.LineageTree;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.lineagetree.TrackedNucleus;

public class RootTrackedNucleus extends TrackedNucleus {

	private LineageTree lt;

	public void setLineageTree(LineageTree lt)  {
		this.lt=lt;
	}
	
	@Override
	public String getName() {
		return "root";
	}
	@Override
	public void setNuclei(SortedList<Nucleus> nuclei) {
	
	}
	
	@Override
	public int getLastTimePoint() {
		
		return (int) lt.getStart();
	}

	@Override
	public int getFirstTimePoint() {
		return (int) lt.getStart();
		}

	@Override
	public void setName(String newName) {
		}
}
