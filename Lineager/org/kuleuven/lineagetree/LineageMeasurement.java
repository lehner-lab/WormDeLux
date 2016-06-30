package org.kuleuven.lineagetree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.kuleuven.collections.ComparatorFactory;
import org.kuleuven.collections.Node;
import org.kuleuven.collections.Pair;
import org.kuleuven.collections.SortedListMap;
import org.kuleuven.math.vector.Vector;

public class LineageMeasurement {
	private Map<Integer, Set<Nucleus>> nucleiOverTime;
	public String name="anonymous";
	public LineageTree lineage;
	public float xyResolution;

	public float zResolution;
	public float timeresolution;
	public Map<Nucleus, TrackedNucleus> nucleusMap;
	public float movementReportThreshold = 0.35f;
	public float diceNucleiOverlapReportThreshold = 0.1f;

	public LineageMeasurement() {
		nucleusMap = new HashMap<Nucleus, TrackedNucleus>();
		nucleiOverTime = new SortedListMap<Integer, Set<Nucleus>>(ComparatorFactory
				.getAscendingIntegerComparator());
		lineage = new LineageTree();
		xyResolution = 0.1f;
		zResolution = 1f;
		timeresolution = 1f;
	}

	public TrackedNucleus retrieveTrackedNucleusByName(String name) {
		TrackedNucleus result = null;
		Iterator<Entry<Nucleus, TrackedNucleus>> it = nucleusMap.entrySet().iterator();
		while (it.hasNext() && result == null) {
			Entry<Nucleus, TrackedNucleus> e = it.next();
			if (e.getKey().getName().equalsIgnoreCase(name)) {
				result = e.getValue();
			}
		}
		return result;
	}
	public List<TrackedNucleus> retrieveTrackedNucleusByName(Pattern name) {
		List<TrackedNucleus> result = new ArrayList<TrackedNucleus>();
		
		Iterator<Entry<Nucleus, TrackedNucleus>> it = nucleusMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Nucleus, TrackedNucleus> e = it.next();
			if (name.matcher(e.getKey().getName()).matches()) {
				result.add( e.getValue());
			}
		}
		return result;
	}
	public Set<TrackedNucleus> retrieveTrackedNucleiByName(String name) {
		Set<TrackedNucleus> result = new HashSet<TrackedNucleus>();
		Iterator<Entry<Nucleus, TrackedNucleus>> it = nucleusMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Nucleus, TrackedNucleus> e = it.next();
			if (e.getKey().getName().equalsIgnoreCase(name)) {
				result.add(e.getValue());
			}
		}
		return result;
	}

	public boolean changeParentChildRelation(TrackedNucleus parent, TrackedNucleus child) {
		if (parent.equals(lineage.getID())
				|| parent.getLastTimePoint() == (-1 + child.getFirstTimePoint())) {
			Set<TrackedNucleus> parents = lineage.getParents(child);
			TrackedNucleus parentArrival = parents.iterator().next();
			lineage.removeParentChildRelation(parentArrival, child);
			lineage.addParentChildRelation(parent, child);
			lineage.removeReduncancy();
			lineage.initializeIndices();
			return true;
		}
		return false;
	}

	public Map<Integer, Set<Nucleus>> getNucleiOverTime() {
		return nucleiOverTime;
	}

	public List<Nucleus> getNucleiForATimepoint(int time) {
		Set<Nucleus> l = nucleiOverTime.get(time);
		List<Nucleus> result = new ArrayList<Nucleus>();
		if (l != null) {
			result.addAll(l);
		}
		return result;
	}

	public Set<TrackedNucleus> getAllTrackedNuclei() {
		Set<TrackedNucleus> result = new HashSet<TrackedNucleus>(nucleusMap.values());
		return result;
	}
	public Integer getFirstTimePoint() {
		List<Integer> l = new ArrayList<Integer>(nucleiOverTime.keySet().size());
		for (Entry<Integer, Set<Nucleus>> e : nucleiOverTime.entrySet()) {
			if (e.getValue() != null && e.getValue().size() > 0) {
				l.add(e.getKey());
			}
		}
		Collections.sort(l);
		Integer min = l.get(0);
		return min;
	}
	public Integer getLastTimePoint() {
		List<Integer> l = new ArrayList<Integer>(nucleiOverTime.keySet().size());
		for (Entry<Integer, Set<Nucleus>> e : nucleiOverTime.entrySet()) {
			if (e.getValue() != null && e.getValue().size() > 0) {
				l.add(e.getKey());
			}
		}
		Collections.sort(l);
		Integer max = l.get(l.size() - 1);

		for (int i = l.size() - 2; i >= 0; i--) {
			if (l.get(i) != l.get(i + 1) - 1) {
				max = l.get(i);
			}
		}
		return max;
	}

	public Map<Integer, List<Pair<Nucleus, Nucleus>>> getOverlappingNucleiPerTimePoint() {
		Map<Integer, List<Pair<Nucleus, Nucleus>>> result = new TreeMap<Integer, List<Pair<Nucleus, Nucleus>>>();
		for (Entry<Integer, Set<Nucleus>> entry : nucleiOverTime.entrySet()) {
			List<Nucleus> nList = new ArrayList<Nucleus>(entry.getValue());
			for (int i = 0; i < nList.size(); i++) {
				Nucleus ni = nList.get(i);
				for (int j = i + 1; j < nList.size(); j++) {
					Nucleus nj = nList.get(j);
					if (ni.doesTouch(nj)) {
						Pair<Nucleus, Nucleus> pair = new Pair<Nucleus, Nucleus>(ni, nj);
						List<Pair<Nucleus, Nucleus>> resultList = result.get(entry.getKey());
						if (resultList == null) {
							resultList = new ArrayList<Pair<Nucleus, Nucleus>>();
							result.put(entry.getKey(), resultList);
						}
						resultList.add(pair);
					}
				}
			}
		}
		return result;
	}

	public boolean addNucleusToTrackedNucleus(TrackedNucleus tn, Nucleus n) {
		if (tn.nuclei.size()==0||tn.getLastTimePoint() + 1 == n.getTimePoint()
				|| tn.getFirstTimePoint() - 1 == n.getTimePoint()) {
			if (!nucleusMap.containsKey(n)) {
				tn.addNucleus(n);
				Set<Nucleus> map = nucleiOverTime.get(n.getTimePoint());
				if (map == null) {
					map = new HashSet<Nucleus>();
					nucleiOverTime.put(n.getTimePoint(), map);
				}

				n.setIndex(-1);
				nucleiOverTime.get(n.getTimePoint()).add(n);
				nucleusMap.put(n, tn);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public Integer numberOfReportedNuclei() {
		Integer count = 0;
		for (Entry<Integer, Set<Nucleus>> entry : nucleiOverTime.entrySet()) {
			count += entry.getValue().size();
		}
		return count;
	}

	/*
	 * public Nucleus getNucleus(int time, int index) { Set<Nucleus> list =
	 * nucleiOverTime.get(time); if (list != null) { return list.get(index); }
	 * return null; }
	 */// temporarily out of action

	public TrackedNucleus getTrackedNucleusForNucleus(Nucleus n) {
		if (n != null) {
			return nucleusMap.get(n);
		}
		return null;
	}

	/*
	 * public TrackedNucleus getTrackedNucleusForNucleus(int time, int index) {
	 * Nucleus n = getNucleus(time, index); if (n != null) { return
	 * nucleusMap.get(n); } return null; }
	 */// temporarily out of action

	/*
	 * public boolean isUnconnected(int time, int index) {
	 * SortedListMap<Integer, Nucleus> list = nucleiOverTime.get(time); if (list
	 * != null) { return isUnconnected(list.get(index)); } return true; }
	 */// temporarily out of action

	public boolean isUnconnected(Nucleus n) {
		TrackedNucleus tn = nucleusMap.get(n);
		if (tn != null) {
			return lineage.isUnconnected(tn);
		}
		return true;
	}

	public String report() {
		StringBuffer sb = new StringBuffer();
		sb.append("In total " + numberOfReportedNuclei().toString() + " nuclei were reported over "
				+ nucleiOverTime.size() + " timepoints.\n");
		Integer totalNumberOfTrackedNuclei = lineage.getAllChildrenBelowNode().size();
		Integer unconnectedNuclei = lineage.getChildlessNuclei().size();
		sb.append("This amounted to " + totalNumberOfTrackedNuclei.toString()
				+ " nuclei tracked over time of which " + unconnectedNuclei
				+ " were unconnected.\n");
		sb.append("There were " + lineage.getDivisions().size() + " observed divisions, of which "
				+ lineage.numberOfIncorrectDivisions() + " appeared to be incorrect.\n");
		Set<TrackedNucleus> premDeaths = lineage.getPrematureDeaths();
		int deathcount = 0;
		for (TrackedNucleus tn : premDeaths) {
			if (lineage.isUnconnected(tn)) {
				deathcount++;
			}
		}
		sb.append("There were " + premDeaths.size() + " premature cellDeaths; " + deathcount
				+ " were unconnected. They are listed below with their end time:\n");
		for (TrackedNucleus tn : premDeaths) {
			sb.append(tn.toString() + "\t" + tn.getLastTimePoint() + "\n");
		}
		sb.append("the following trackednuclei moved more than " + movementReportThreshold
				+ " um\n");
		for (TrackedNucleus tn : lineage.asList()) {
			if (tn.getMaximumMovenment() >= 3.5) {
				List<Float> mov = tn.getMovements();
				int first = tn.getFirstTimePoint();
				for (int i = 0; i < mov.size(); i++) {
					if (mov.get(i) > 3.5) {
						int time = first + i;
						sb.append(tn.nuclei.get(i) + "\tstarting at timepoint " + time + " moved: "
								+ tn.nuclei.get(i).distanceTo(tn.nuclei.get(i + 1)) + "\n");
					}
				}
			}

		}
		sb.append("The following nuclei touched with a dice >" + diceNucleiOverlapReportThreshold
				+ "\n");
		Map<Integer, List<Pair<Nucleus, Nucleus>>> overlapMap = getOverlappingNucleiPerTimePoint();
		for (Entry<Integer, List<Pair<Nucleus, Nucleus>>> entry : overlapMap.entrySet()) {
			for (Pair<Nucleus, Nucleus> pair : entry.getValue()) {
				float dice = NucleusUtilities.getDiceForOverlapNuclei(pair.object1, pair.object2,
						xyResolution, zResolution);
				if (dice > diceNucleiOverlapReportThreshold)
					sb.append(entry.getKey() + "\t" + pair + "\t" + dice + "\n");
			}
		}
		sb.append("Time\tNucleiCount\tUnconnected\n");
		List<Integer> keys = new ArrayList<Integer>(nucleiOverTime.keySet());
		Collections.sort(keys);
		for (Integer key : keys) {
			Integer count = 0;
			Set<Nucleus> nuclei = nucleiOverTime.get(key);
			for (Nucleus n : nuclei) {
				if (isUnconnected(n))
					count++;
			}
			sb.append(key + "\t" + nuclei.size() + "\t" + count.toString() + "\n");
		}
		return sb.toString();
	}

	public void setNucleiOverTime(Map<Integer, Set<Nucleus>> nucleiOverTime) {
		this.nucleiOverTime = nucleiOverTime;
	}

	public void transformNuclei(Vector<Integer> transform) {
		for (Nucleus n : nucleusMap.keySet()) {
			Vector<Integer> coordinates = n.getCoordinates();
			coordinates.subtract(transform);
			n.setCoordinates(coordinates);
		}
	}

	public void scaleNucleiSize(float factor) {
		for (Nucleus n : nucleusMap.keySet()) {
			n.radius = factor * n.radius;
		}
	}

	public void rotateNuclei(float angle) {
		for (Nucleus n : nucleusMap.keySet()) {
			Vector<Integer> coordinates = n.getCoordinates();
			double newx = Math.cos(angle) * coordinates.get(0) + Math.sin(angle)
					* coordinates.get(1);
			double newy = -Math.sin(angle) * coordinates.get(0) + Math.cos(angle)
					* coordinates.get(1);
			;
			coordinates.set(0, newx);
			coordinates.set(1, newy);
			n.setCoordinates(coordinates);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Integer, SortedListMap<Integer, Nucleus>> getIndexedNuclei() {
		Map<Integer, SortedListMap<Integer, Nucleus>> result = new SortedListMap<Integer, SortedListMap<Integer, Nucleus>>(
				ComparatorFactory.getAscendingIntegerComparator());
		for (Entry<Integer, Set<Nucleus>> v : nucleiOverTime.entrySet()) {
			int i = 0;
			SortedListMap<Integer, Nucleus> newMap = new SortedListMap<Integer, Nucleus>(
					ComparatorFactory.getAscendingIntegerComparator());
			int max = -1;
			for (Nucleus me : v.getValue()) {
				max = Math.max(me.getIndex(), max);
			}
			if (max >= 0)
				i = max + 100;
			for (Nucleus n : v.getValue()) {
				if (n.getIndex() <= 0) {
					n.setIndex(i);
					i++;
				}
				newMap.put(n.getIndex(), n);
			}
			result.put(v.getKey(), newMap);
		}
		return result;
	}

	public void removeTrackedNucleus(TrackedNucleus selectedTrackedNucleus) {
		Node<TrackedNucleus> n = lineage.getNode(selectedTrackedNucleus);
		Set<TrackedNucleus> children = n.getChildren();
		if (children != null) {
			for (TrackedNucleus tn : children) {
				removeTrackedNucleus(tn);
			}
		}
		Set<TrackedNucleus> parents = lineage.getParents(selectedTrackedNucleus);
		if (parents != null) {
			for (TrackedNucleus tn : parents) {
				Node<TrackedNucleus> nP = lineage.getNode(tn);
				nP.removeChild(selectedTrackedNucleus);
			}
		} else if (lineage.getChildren().contains(selectedTrackedNucleus)) {
			lineage.removeChild(selectedTrackedNucleus);
		}
		for (Nucleus nucleus : selectedTrackedNucleus.nuclei) {
			nucleiOverTime.get(nucleus.timePoint).remove(nucleus);
			nucleusMap.remove(nucleus);
		}
	}

	public TrackedNucleus newNucleus(String name, int time, double plane, double x,double y,
			float radius) {
		Set<Nucleus> map = nucleiOverTime.get(time);
		if (map == null) {
			map = new HashSet<Nucleus>();
			nucleiOverTime.put(time, map);
		}

		Nucleus newNucleus = new Nucleus(name, -1, 1, x, y, plane, radius, time);
		map.add(newNucleus);
		TrackedNucleus tn = new TrackedNucleus();
		tn.addNucleus(newNucleus);
		nucleusMap.put(newNucleus, tn);
		lineage.addParentChildRelation(this.lineage.getID(), tn);
		lineage.initializeIndices();
		return tn;
	}

	public boolean mergeConsecutiveNuclei(TrackedNucleus parent, TrackedNucleus child) {
		if (parent.getLastTimePoint() == (-1 + child.getFirstTimePoint())) {
			String name = parent.getName();
			for (Nucleus n : child.nuclei) {
				parent.addNucleus(n);
				nucleusMap.put(n, parent);
			}
			Node<TrackedNucleus> tn = lineage.getNode(parent);
			Set<TrackedNucleus> children = new HashSet<TrackedNucleus>(tn.getChildren());
			for (TrackedNucleus tnChild : children) {
				lineage.removeParentChildRelation(parent, tnChild);
			}
			Node<TrackedNucleus> tn2 = lineage.getNode(child);
			Set<TrackedNucleus> children2 = new HashSet<TrackedNucleus>(tn2.getChildren());
			for (TrackedNucleus tnChild : children2) {
				changeParentChildRelation(parent, tnChild);
			}
			Set<TrackedNucleus> parents = lineage.getParents(child);
			if (parents != null) {
				for (TrackedNucleus tnP : parents) {
					Node<TrackedNucleus> nP = lineage.getNode(tnP);
					nP.removeChild(child);
				}
			} else if (lineage.getChildren().contains(child)) {
				lineage.removeChild(child);
			}
			parent.setName(name);
			lineage.removeReduncancy();
			lineage.initializeIndices();
			return true;
		}
		return false;
	}

	public List<TrackedNucleus> changeLifeTimeOfNucleus(TrackedNucleus selectedTrackedNucleus,
			Integer firstTimePoint, Integer lastTimePoint) {
		int originalFirst = selectedTrackedNucleus.getFirstTimePoint();
		int originalLast = selectedTrackedNucleus.getLastTimePoint();
		List<TrackedNucleus> result = new ArrayList<TrackedNucleus>();
		if (originalFirst != firstTimePoint) {
			changeParentChildRelation(lineage.getID(), selectedTrackedNucleus);
			if (originalFirst > firstTimePoint) {
				Nucleus firstNucleus = selectedTrackedNucleus.getNucleusForTimePoint(originalFirst);
				for (Integer i = originalFirst - 1; i >= firstTimePoint; i--) {
					Nucleus newNucleus = new Nucleus(selectedTrackedNucleus.getName(), -1, 1,
							firstNucleus.getX(), firstNucleus.getY(), firstNucleus.getZ(),
							firstNucleus.getRadius(), i);
					addNucleusToTrackedNucleus(selectedTrackedNucleus, newNucleus);
				}

			} else {
				TrackedNucleus tn = new TrackedNucleus();
				for (Integer i = originalFirst; i < firstTimePoint; i++) {
					Nucleus n = selectedTrackedNucleus.removeFirstNucleus();
					tn.addNucleus(n);
					// nucleiOverTime.get(i).remove(n.getIndex());
					nucleusMap.put(n, tn);
				}
				tn.setName("r" + selectedTrackedNucleus.getName());
				lineage.addParentChildRelation(lineage.getID(), tn);
				lineage.removeReduncancy();
				lineage.initializeIndices();
				result.add(tn);
			}

		}
		if (originalLast != lastTimePoint) {
			if (lastTimePoint < originalLast) {
				TrackedNucleus tn = new TrackedNucleus();
				for (Integer i = originalLast; i > lastTimePoint; i--) {
					Nucleus n = selectedTrackedNucleus.removeLastNucleus();
					tn.addNucleus(n);
					// nucleiOverTime.get(i).remove(n.getIndex());
					nucleusMap.put(n, tn);
				}
				tn.setName("r" + tn.getName());
				lineage.addParentChildRelation(lineage.getID(), tn);
				Set<TrackedNucleus> set = lineage.getNode(selectedTrackedNucleus).getChildren();
				for (TrackedNucleus tnc : set) {
					changeParentChildRelation(tn, tnc);
				}
				lineage.removeReduncancy();
				lineage.initializeIndices();
				result.add(tn);
			} else {
				for (TrackedNucleus tn : lineage.getNode(selectedTrackedNucleus).getChildren()) {
					changeParentChildRelation(lineage.getID(), tn);
				}

				Nucleus lastNucleus = selectedTrackedNucleus.getNucleusForTimePoint(originalLast);
				for (Integer i = originalLast + 1; i <= lastTimePoint; i++) {
					Nucleus newNucleus = new Nucleus(selectedTrackedNucleus.getName(), -1, 1,
							lastNucleus.getX(), lastNucleus.getY(), lastNucleus.getZ(), lastNucleus
									.getRadius(), i);
					addNucleusToTrackedNucleus(selectedTrackedNucleus, newNucleus);
				}
			}
		}
		return result;
	}

	public float getXyResolution() {
		return xyResolution;
	}

	public void setXyResolution(float xyResolution) {
		if(xyResolution!=this.xyResolution){
			double correction = xyResolution/this.xyResolution;
			for(Entry<Integer, Set<Nucleus>> e: nucleiOverTime.entrySet()){
				for(Nucleus n : e.getValue()){
					n.radius*=correction;
					n.coordinates.set(0, n.coordinates.get(0)*correction);
					n.coordinates.set(1, n.coordinates.get(1)*correction);
				}
			}
			this.xyResolution = xyResolution;
		}
	
	}

	public float getzResolution() {
		return zResolution;
	}

	public void setzResolution(float zResolution) {
		if(zResolution!=this.zResolution){
			double correction = zResolution/this.zResolution;
			for(Entry<Integer, Set<Nucleus>> e: nucleiOverTime.entrySet()){
				for(Nucleus n : e.getValue()){
					n.coordinates.set(2, n.coordinates.get(2)*correction);
				}
			}
			this.zResolution = zResolution;
		}
	}

	public float getTimeresolution() {
		return timeresolution;
	}

	public void setTimeresolution(float timeresolution) {
		this.timeresolution = timeresolution;
	}

	public Nucleus removeLastNucleus(TrackedNucleus selectedTrackedNucleus) {
		Nucleus last = selectedTrackedNucleus.removeLastNucleus();
		nucleusMap.remove(last);
		nucleiOverTime.get(last.timePoint).remove(last);
		return last;
	}

}
