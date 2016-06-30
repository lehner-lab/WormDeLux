package org.kuleuven.lineagetree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuleuven.collections.Node;
import org.kuleuven.lineager.RootTrackedNucleus;
import org.kuleuven.math.MathEMC;

public class LineageTree extends DistancedTree<TrackedNucleus>{
	float small = 10e-9f;
	private TrackedNucleusComparatorByName comparatorByName;

	public LineageTree() {
		super(new RootTrackedNucleus());
		((RootTrackedNucleus)getID()).setLineageTree(this);
		this.comparator = new TrackedNucleusComparator();
		this.comparatorByName = new TrackedNucleusComparatorByName();
	}

	public LineageTree(TrackedNucleus root) {
		super(root);
		this.comparator = new TrackedNucleusComparator();
		this.comparatorByName = new TrackedNucleusComparatorByName();
	}

	public LineageTree(LineageTree copyLT) {
		super(copyLT.getRootNode().getID().getCopy());
		this.comparator = new TrackedNucleusComparator();
		this.comparatorByName = new TrackedNucleusComparatorByName();
	
	}

	public void removeInvalidTrackedNuclei() {

		for (TrackedNucleus tn : getAllChildrenBelowNode()) {
			if (!tn.isValid()) {
				if (children.containsKey(tn) && getNode(tn).getChildren().size() == 0) {
					children.remove(tn);

				}
			}
		}
	}

	public void initializeIndices() {
		new IndexInitializer(this, 1);
	}

	@Override
	public List<TrackedNucleus> getChildrenAsSortedList() {
		return super.getChildrenAsSortedList(new TrackedNucleusComparator());
	}

	public Set<TrackedNucleus> getPrematureDeaths(boolean connectedOnly) {
		Set<TrackedNucleus> result = getPrematureDeaths();
		if (connectedOnly) {
			result.removeAll(getChildlessNuclei());
		}
		return result;
	}

	public Set<TrackedNucleus> getPrematureDeaths() {
		Set<Node<TrackedNucleus>> ln = getAllLeafNodes();
		Set<TrackedNucleus> result = new HashSet<TrackedNucleus>();
		float max = 0f;
		for (Node<TrackedNucleus> node : ln) {
			float endtime = node.getID().getLastTimePoint();
			if (endtime > max) {
				max = endtime;
			}
		}
		for (Node<TrackedNucleus> node : ln) {
			float endtime = node.getID().getLastTimePoint();
			if (Math.abs(endtime - max) > small) {
				result.add(node.getID());
			}
		}
		return result;
	}

	public double getMeanLineageDuration() {
		return MathEMC.mean(getLineageDurations());
	}

	public double getMedianLineageDuration() {
		return MathEMC.integerListMedian(getLineageDurations());
	}

	public List<Integer> getLineageDurations() {
		List<List<TrackedNucleus>> lineageLines = getLineageLines();
		List<Integer> result = new ArrayList<Integer>();
		for (List<TrackedNucleus> line : lineageLines) {
			int first = (line.get(line.size() - 1)).getFirstTimePoint();
			int last = (line.get(0)).getLastTimePoint();
			result.add(last - first + 1);
		}
		return result;
	}

	public Integer getLastTimePoint() {
		Set<Node<TrackedNucleus>> leafs = getAllLeafNodes();
		if(leafs.size()==0)
			return 1;
		int last = 0;
		for (Node<TrackedNucleus> tn : leafs) {
			last = Math.max(last, tn.getID().getLastTimePoint());
		}
		return last;

	}

	public List<List<TrackedNucleus>> getLineageLines() {
		Set<Node<TrackedNucleus>> leafNodes = getAllLeafNodes();
		List<List<TrackedNucleus>> result = new ArrayList<List<TrackedNucleus>>();
		for (Node<TrackedNucleus> leaf : leafNodes) {
			TrackedNucleus tn = leaf.getID();
			List<TrackedNucleus> tnList = new ArrayList<TrackedNucleus>();
			result.add(tnList);
			tnList.add(tn);
			while (tn != null) {
				Set<TrackedNucleus> tnP = getParents(tn);
				if (tnP == null || tnP.size() != 1) {
					tn = null;
				} else {
					tn = new ArrayList<TrackedNucleus>(tnP).get(0);
					if (tn == this.getID())
						tn = null;
					else {
						tnList.add(tn);
					}
				}
			}
		}
		return result;
	}

	@Override
	public Node<TrackedNucleus> add(TrackedNucleus newChild) {
		needsToSort = true;
		if (id.equals(newChild)) {
			return this;
		} else if (children.containsKey(newChild)) {
			return null;
		} else {
			LineageNode childNode = new LineageNode(newChild);
			children.put(newChild, childNode);
			return childNode;
		}
	}

	public List<Division> getDivisions() {
		List<Division> result = new ArrayList<Division>();
		for (Node<TrackedNucleus> tn : children.values()) {
			if (!isUnconnected(tn.getID())) {
				LineageNode ln = (LineageNode) tn;
				ln.getDivisions(result);
			}
		}
		return result;
	}

	public int numberOfIncorrectDivisions() {
		List<Division> divs = getDivisions();
		int count = 0;
		for (Division div : divs) {
			if (div.isValid() != 0) {
				count++;
			}
		}
		return count;
	}

	public Set<TrackedNucleus> getOrphanNuclei() {
		Set<TrackedNucleus> result = new HashSet<TrackedNucleus>();
		Double start =getStart();
		for (TrackedNucleus node : children.keySet()) {
			Set<TrackedNucleus> p = getParents(node);
			if (p == null || p.contains(getID()) && node.getFirstTimePoint() > start) {
				result.add(node);
			}
		}
		return result;
	}

	public Set<TrackedNucleus> getChildlessNuclei() {
		Set<TrackedNucleus> result = new HashSet<TrackedNucleus>();
		int last = getLastTimePoint();
		for (Node<TrackedNucleus> node : children.values()) {
			if (node.countChildren() == 0 && node.getID().getLastTimePoint() < last) {
				result.add(node.getID());
			}
		}
		return result;
	}

	public boolean isUnconnected(TrackedNucleus tn) {
		if (children.containsKey(tn)) {
			if (children.get(tn).countChildren() == 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public double getDistance() {

		return getLastTimePoint();
	}

	protected class IndexInitializer {
		int start = 0;

		public IndexInitializer(Node<TrackedNucleus> root, int start) {
			this.start = start;

			List<TrackedNucleus> s = new ArrayList<TrackedNucleus>(root.getChildren());
			Collections.sort(s, comparatorByName);
			if (s.size() == 0) {
				root.getID().setIndex(start);
			} else {
				int runningsum = start;
				int locationOfRoot = (int) Math.round((double) s.size() / 2);
				int i = 0;
				for (TrackedNucleus tn : s) {
					if (i == locationOfRoot) {
						root.getID().setIndex(runningsum);
						runningsum++;
					}
					i++;
					Node<TrackedNucleus> tnNode = root.getNode(tn);
					new IndexInitializer(tnNode, runningsum);
					runningsum += 1 + tnNode.getAllNodesBelowNode().size();

				}
			}
		}
	}

	public static class TrackedNucleusComparator implements Comparator<TrackedNucleus> {

		@Override
		public int compare(TrackedNucleus o1, TrackedNucleus o2) {
			return o1.getIndex() - o2.getIndex();
		}

	}

	public static class TrackedNucleusComparatorByName implements Comparator<TrackedNucleus> {

		@Override
		public int compare(TrackedNucleus o1, TrackedNucleus o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}

	}

	@Override
	public double getStart() {
		Set<TrackedNucleus> tns =getChildren();
		if(tns.size()==0)
			return 1;
		double start = Double.MAX_VALUE;
		for (TrackedNucleus child : tns) {
			start = Math.min(start, child.getFirstTimePoint());
		}
		return start;
	}

	

}
