package org.kuleuven.lineager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.kuleuven.lineagetree.Lineage2StatisticsScript;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.lineagetree.StarryNightFormatIO;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.math.matrix.Matrix;
import org.kuleuven.math.vector.ArrayVector;
import org.kuleuven.math.vector.Vector;
import org.kuleuven.utilities.ProcessUtilities;

public class NamingAttempt {
	String dir = "/Users/rjelier/Projects/CE_lineaging/Lineages/wtFixed/";
	private double d = 0.2;
	private HashMap<String, LineageMeasurement> map;
	private HashMap<String, Vector<Integer>> EMS_map;
	private HashMap<String, Vector<Integer>> ABp_map;
	
	private int mode = GetAxes.ORIENT4CELL;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new NamingAttempt(null).run();

	}

	public NamingAttempt(File f) {
		if (f == null)
			f = new File(dir);
		String[] files = f.list();
		map = new HashMap<String, LineageMeasurement>();
		EMS_map = new HashMap<String, Vector<Integer>>();
		ABp_map = new HashMap<String, Vector<Integer>>();
		for (String file : files) {
			if (file.endsWith(".zip")) {
				LineageMeasurement lm = null;
				try {
					lm = StarryNightFormatIO.readNuclei(
							f.getAbsolutePath() + ProcessUtilities.getDirectorySeparator() + file,
							0.1f, 1f, 1f, 0, 200);
					Matrix<Integer,Integer> m=GetAxes.getOrientLineageMatrix(lm, GetAxes.ORIENT4CELL);
					TrackedNucleus ABplT=lm.retrieveTrackedNucleusByName("ABpl");
					Vector<Integer> ABpl=new ArrayVector<Integer>(ABplT.getNucleusForTimePoint(ABplT.getFirstTimePoint()).getCoordinates());
					TrackedNucleus ABprT=lm.retrieveTrackedNucleusByName("ABpr");
					ABpl.subtract(ABprT.getNucleusForTimePoint(ABprT.getFirstTimePoint()).getCoordinates());
					ABpl=GetAxes.transform(m, ABpl,GetAxes.ORIENT4CELL);
					TrackedNucleus ET=lm.retrieveTrackedNucleusByName("E");
					Vector<Integer> E=new ArrayVector<Integer>(ET.getNucleusForTimePoint(ET.getFirstTimePoint()).getCoordinates());
					TrackedNucleus MST=lm.retrieveTrackedNucleusByName("MS");
					E.subtract(MST.getNucleusForTimePoint(MST.getFirstTimePoint()).getCoordinates());
					E=GetAxes.transform(m, E,GetAxes.ORIENT4CELL);
					reOrientLineage(lm,GetAxes.ORIENTMS);
					lm.setName(file);
					EMS_map.put(file, E);
					ABp_map.put(file, ABpl);
					map.put(file, lm);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(file);

			}
		}
	}
	private void reOrientLineage(LineageMeasurement lm,int mode) throws Exception {
		Matrix<Integer, Integer> transformationMatrix = GetAxes
				.getOrientLineageMatrix(lm, mode);
		for (Nucleus n : lm.nucleusMap.keySet()) {
			Vector<Integer> adapted = GetAxes.transform(transformationMatrix, n.getCoordinates(),
					mode);
			n.setCoordinates(adapted);
		}
	}

	public void run() {
		java.util.List<LineageMeasurement> lmList = new ArrayList<LineageMeasurement>(map.values());
		for (LineageMeasurement lm : lmList) {
			System.out.println(lm.name);
			map.remove(lm.getName());
			int countCorrect = 0;
			int countAll = 0;
			for (TrackedNucleus tn : lm.getAllTrackedNuclei()) {
				Set<TrackedNucleus> children = lm.lineage.getNode(tn).getChildren();
				if (children != null && children.size() == 2) {
					Iterator<TrackedNucleus> it = children.iterator();
					TrackedNucleus tn1 = it.next();
					TrackedNucleus tn2 = it.next();
					if (naming(tn1, tn2, tn.getName())) {
						countCorrect++;
					}
					countAll++;

				}
			}
			System.out.println(countAll + "\t" + countCorrect + "\t" + (double) countCorrect
					/ countAll);
			map.put(lm.getName(), lm);
		}
	}

	public void initialNaming(LineageMeasurement lm) {
		Map<Integer, Set<Nucleus>> nOT = lm.getNucleiOverTime();
		boolean done = false;
		int running = 0;
		List<Integer> keys = new ArrayList<Integer>(nOT.keySet());
		while (!done && running < keys.size()) {
			Set<Nucleus> ib = nOT.get(keys.get(running));
			if (ib.size() == 4) {
				done = true;
			} else {
				running++;
			}
		}

		if (done) {
			List<Nucleus> ib = new ArrayList<Nucleus>(nOT.get(keys.get(running)));
			int nMax1 = 0;
			int nMax2 = 0;
			double distance = 0;
			for (int j = 0; j < 4; j++) {
				Nucleus n1 = ib.get(j);
				for (int k = j + 1; k < 4; k++) {
					Nucleus n2 = ib.get(k);
					double d = n1.distanceTo(n2);
					if (d > distance) {
						nMax1 = j;
						nMax2 = k;
						distance = d;
					}
				}
			}
			TrackedNucleus tn1 = lm.getTrackedNucleusForNucleus(ib.get(nMax1));
			TrackedNucleus tn2 = lm.getTrackedNucleusForNucleus(ib.get(nMax2));
			if (tn1.getLastTimePoint() < tn2.getLastTimePoint()) {
				tn1.setName("ABa");
				tn2.setName("P2");
			} else {
				tn2.setName("ABa");
				tn1.setName("P2");
			}
			int ABp = 0;
			int EMS = 0;
			int t = 0;
			for (int j = 0; j < 4; j++) {
				if (j != nMax1 & j != nMax2) {
					if (lm.getTrackedNucleusForNucleus(ib.get(j)).getLastTimePoint() > t) {
						ABp = EMS;
						EMS = j;
						t = lm.getTrackedNucleusForNucleus(ib.get(j)).getLastTimePoint();
					} else {
						ABp = j;
					}
				}
			}

			lm.getTrackedNucleusForNucleus(ib.get(ABp)).setName("ABp");
			lm.getTrackedNucleusForNucleus(ib.get(EMS)).setName("EMS");
		}
	}

	public void name(LineageMeasurement lm, int mode) throws Exception {

		initialNaming(lm);
		
		TrackedNucleus ABa = lm.retrieveTrackedNucleusByName("ABa");
		TrackedNucleus ABp = lm.retrieveTrackedNucleusByName("ABp");
		TrackedNucleus EMS = lm.retrieveTrackedNucleusByName("EMS");
		TrackedNucleus P2 = lm.retrieveTrackedNucleusByName("P2");
		if (ABa != null && ABp != null && EMS != null && P2 != null) {
			Matrix<Integer, Integer> transformationMatrix = GetAxes
					.getOrientLineageMatrix(lm,GetAxes.ORIENT4CELL);
			if (mode == GetAxes.ORIENT4CELL) {
				nameTrackedNucleus(lm, ABa, transformationMatrix,mode);
				nameTrackedNucleus(lm, ABp, transformationMatrix,mode);
				nameTrackedNucleus(lm, EMS, transformationMatrix,mode);
				nameTrackedNucleus(lm, P2, transformationMatrix,mode);
			}else if (mode==GetAxes.ORIENTMS){
				Set<TrackedNucleus> children = lm.lineage.getNode(ABp).getChildren();
				Iterator<TrackedNucleus> it = children.iterator();
				TrackedNucleus tn1ABp = it.next();
				TrackedNucleus tn2ABp = it.next();
				namePre(tn1ABp,tn2ABp,transformationMatrix,ABp_map,"ABpl","ABpr");
				Set<TrackedNucleus> childrenEMS = lm.lineage.getNode(EMS).getChildren();
				it = childrenEMS.iterator();
				TrackedNucleus tn1EMS = it.next();
				TrackedNucleus tn2EMS = it.next();
				namePre(tn1EMS,tn2EMS,transformationMatrix,EMS_map,"E","MS");
				Matrix<Integer, Integer> transformationMatrixMS = GetAxes
						.getOrientLineageMatrix(lm,GetAxes.ORIENTMS);
				nameTrackedNucleus(lm, ABa, transformationMatrixMS,mode);
				nameTrackedNucleus(lm, tn1ABp, transformationMatrixMS,mode);
				nameTrackedNucleus(lm, tn2ABp, transformationMatrixMS,mode);
				nameTrackedNucleus(lm, tn1EMS, transformationMatrixMS,mode);
				nameTrackedNucleus(lm, tn2EMS, transformationMatrixMS,mode);
				nameTrackedNucleus(lm, P2, transformationMatrixMS,mode);

			}
		}
	}

	private void nameTrackedNucleus(LineageMeasurement lm, TrackedNucleus tn,
			Matrix transformationMatrix,int mode) {
		Set<TrackedNucleus> children = lm.lineage.getNode(tn).getChildren();
		if (children != null && children.size() == 2) {
			Iterator<TrackedNucleus> it = children.iterator();
			TrackedNucleus tn1 = it.next();
			TrackedNucleus tn2 = it.next();
			name(tn1, tn2, tn.getName(), transformationMatrix, mode);
			nameTrackedNucleus(lm, tn1, transformationMatrix,mode);
			nameTrackedNucleus(lm, tn2, transformationMatrix,mode);
		}

	}
private boolean namePre(TrackedNucleus tn1, TrackedNucleus tn2,
		Matrix<Integer,Integer> transformationMatrix, Map<String,Vector<Integer>> map,String name1,String name2){
	Nucleus n1 = tn1.getNucleusForTimePoint(tn1.getFirstTimePoint());
	Nucleus n2 = tn2.getNucleusForTimePoint(tn2.getFirstTimePoint());
	Nucleus copy1 = new Nucleus(n1);
	Nucleus copy2 = new Nucleus(n2);
	Vector<Integer> divAxis = new ArrayVector<Integer>(copy1.getCoordinates());
	divAxis.subtract(copy2.getCoordinates());
	divAxis=GetAxes.transform(transformationMatrix, divAxis, GetAxes.ORIENT4CELL);
	Map<String, Integer> tn1AssignedNames = new HashMap<String, Integer>();

	for (Entry<String, Vector<Integer>> e : map.entrySet()) {
		double score = e.getValue().cosine(divAxis);
		String out;
		if (score > 0) {
			out = name1 + "\t" + name2;
		} else {
			out = name2+ "\t" + name1;
		}
		Integer i = tn1AssignedNames.get(out);
		if (i == null) {
			i = 0;
		}
		tn1AssignedNames.put(out, ++i);
	}
	String assignedName1 = null;
	String assignedName2 = null;

	if (tn1AssignedNames.size() == 1) {
		String[] c = tn1AssignedNames.keySet().iterator().next().split("\t");
		assignedName1 = c[0];
		assignedName2 = c[1];

	} else if (tn1AssignedNames.size() > 1) {
		Integer max = Collections.max(tn1AssignedNames.values());
		for (Entry<String, Integer> e : tn1AssignedNames.entrySet()) {
			if (e.getValue().equals(max)) {
				String[] c = tn1AssignedNames.keySet().iterator().next().split("\t");
				assignedName1 = c[0];
				assignedName2 = c[1];
			}
		}
	}
	if (assignedName1 != null && assignedName2 != null) {
		tn1.setName(assignedName1);
		tn2.setName(assignedName2);
	}
	return true;

	
}
	public boolean name(TrackedNucleus tn1, TrackedNucleus tn2, String nameOfMother,
			Matrix transformationMatrix,int mode) {
		Nucleus n1 = tn1.getNucleusForTimePoint(tn1.getFirstTimePoint());
		Nucleus n2 = tn2.getNucleusForTimePoint(tn2.getFirstTimePoint());
		Nucleus copy1 = new Nucleus(n1);
		Vector<Integer> adapted = GetAxes.transform(transformationMatrix, copy1.getCoordinates(),
				mode);
		copy1.setCoordinates(adapted);
		Nucleus copy2 = new Nucleus(n2);
		Vector<Integer> adapted2 = GetAxes.transform(transformationMatrix, copy2.getCoordinates(),
				mode);
		copy2.setCoordinates(adapted2);

		Vector<Integer> divAxis = new ArrayVector<Integer>(copy1.getCoordinates());
		divAxis.subtract(copy2.getCoordinates());

		Map<String, Integer> tn1AssignedNames = new HashMap<String, Integer>();

		for (Entry<String, LineageMeasurement> e : map.entrySet()) {
			TrackedNucleus mother = e.getValue().retrieveTrackedNucleusByName(nameOfMother);
			if (mother != null) {
				Set<TrackedNucleus> set = e.getValue().lineage.getNode(mother).getChildren();
				if (set.size() == 2) {
					Iterator<TrackedNucleus> it = set.iterator();
					TrackedNucleus daughter1 = it.next();
					Nucleus nd1 = daughter1.getNucleusForTimePoint(daughter1.getFirstTimePoint());
					TrackedNucleus daughter2 = it.next();
					Nucleus nd2 = daughter2.getNucleusForTimePoint(daughter2.getFirstTimePoint());
					Vector<Integer> result = new ArrayVector<Integer>(nd1.getCoordinates());

					result.subtract(nd2.getCoordinates());
					double score = result.cosine(divAxis);
					String name1;
					if (score > 0) {
						name1 = nd1.getName() + "\t" + nd2.getName();
					} else {
						name1 = nd2.getName() + "\t" + nd1.getName();
					}
					Integer i = tn1AssignedNames.get(name1);
					if (i == null) {
						i = 0;
					}
					tn1AssignedNames.put(name1, ++i);
				} else {
				}
			} else {
			}
		}
		String assignedName1 = null;
		String assignedName2 = null;

		if (tn1AssignedNames.size() == 1) {
			String[] c = tn1AssignedNames.keySet().iterator().next().split("\t");
			assignedName1 = c[0];
			assignedName2 = c[1];

		} else if (tn1AssignedNames.size() > 1) {
			Integer max = Collections.max(tn1AssignedNames.values());
			for (Entry<String, Integer> e : tn1AssignedNames.entrySet()) {
				if (e.getValue().equals(max)) {
					String[] c = tn1AssignedNames.keySet().iterator().next().split("\t");
					assignedName1 = c[0];
					assignedName2 = c[1];
				}
			}
		}
		if (assignedName1 != null && assignedName2 != null) {
			tn1.setName(assignedName1);
			tn2.setName(assignedName2);
		}
		return true;
	}

	public boolean naming(TrackedNucleus tn1, TrackedNucleus tn2, String nameOfMother) {
		Nucleus n1 = tn1.getNucleusForTimePoint(tn1.getFirstTimePoint());
		Nucleus n2 = tn2.getNucleusForTimePoint(tn2.getFirstTimePoint());
		Vector<Integer> divAxis = new ArrayVector<Integer>(n1.getCoordinates());
		divAxis.subtract(n2.getCoordinates());

		Map<String, Integer> tn1AssignedNames = new HashMap<String, Integer>();
		// Map<String, Integer> tn2AssignedNames = new HashMap<String,
		// Integer>();

		for (Entry<String, LineageMeasurement> e : map.entrySet()) {
			TrackedNucleus mother = e.getValue().retrieveTrackedNucleusByName(nameOfMother);
			if (mother != null) {
				Set<TrackedNucleus> set = e.getValue().lineage.getNode(mother).getChildren();
				if (set.size() == 2) {
					Iterator<TrackedNucleus> it = set.iterator();
					TrackedNucleus daughter1 = it.next();
					Nucleus nd1 = daughter1.getNucleusForTimePoint(daughter1.getFirstTimePoint());
					TrackedNucleus daughter2 = it.next();
					Nucleus nd2 = daughter2.getNucleusForTimePoint(daughter2.getFirstTimePoint());
					Vector<Integer> result = new ArrayVector<Integer>(nd1.getCoordinates());
					result.subtract(nd2.getCoordinates());
					double score = result.cosine(divAxis);
					String name1;
					if (score >= 0) {
						name1 = nd1.getName() + "\t" + nd2.getName();
					} else {
						name1 = nd2.getName() + "\t" + nd1.getName();
					}
					Integer i = tn1AssignedNames.get(name1);
					if (i == null) {
						i = 0;
					}
					tn1AssignedNames.put(name1, ++i);
				} else {
				}
			} else {
			}
		}
		String assignedName1 = "";
		String assignedName2 = "";

		if (tn1AssignedNames.size() == 1) {
			String[] c = tn1AssignedNames.keySet().iterator().next().split("\t");
			assignedName1 = c[0];
			assignedName2 = c[1];
		} else if (tn1AssignedNames.size() > 1) {
			Integer max = Collections.max(tn1AssignedNames.values());
			for (Entry<String, Integer> e : tn1AssignedNames.entrySet()) {
				if (e.getValue().equals(max)) {
					String[] c = tn1AssignedNames.keySet().iterator().next().split("\t");
					assignedName1 = c[0];
					assignedName2 = c[1];

				}
			}
		}

		/*
		 * if (!(assignedName1.equals(n1.getIdentity()) &&
		 * assignedName2.equals(n2.getIdentity()))) {
		 * System.out.println(n1.getIdentity() + "\t" + assignedName1 + "\t" +
		 * tn1AssignedNames.get(assignedName1) + "\t" + n2.getIdentity() + "\t"
		 * + assignedName2 + "\t" + tn2AssignedNames.get(assignedName2)); return
		 * false; }
		 */
		return true;
	}
}
/*
 * for (TrackedNucleus tn : set) { if
 * (lm1.lineage.getNode(tn).getChildren().size() > 0) { for (Entry<String,
 * LineageMeasurement> e : map.entrySet()) { LineageMeasurement lm =
 * e.getValue(); TrackedNucleus ABa =
 * e.getValue().retrieveTrackedNucleusByName(tn.getName()); if (ABa != null) {
 * Set<TrackedNucleus> c = lm.lineage.getNode(ABa).getChildren(); if (c.size()
 * == 2) { Iterator<TrackedNucleus> it = c.iterator(); TrackedNucleus tn1 =
 * it.next(); TrackedNucleus tn2 = it.next();
 * 
 * Nucleus n1 = tn1.getNucleusForTimePoint(tn1.getFirstTimePoint()); Nucleus n2
 * = tn2.getNucleusForTimePoint(tn2.getFirstTimePoint()); Vector<Integer> result
 * = new ArrayVector(n1.getCoordinates()); result.subtract(n2.getCoordinates());
 * Vector<Integer> APax = new ArrayVector<Integer>(ListSpace.threeD);
 * APax.set(0, 1); Vector<Integer> Vax = new
 * ArrayVector<Integer>(ListSpace.threeD); Vax.set(2, 1); double APcos =
 * APax.cosine(result); double Vcos = Vax.cosine(result); if (Math.abs(APcos) >
 * d) { if (APcos < 0) { Nucleus inbetween = n1; n1 = n2; n2 = inbetween; } }
 * else { if (Vcos > 0) { Nucleus inbetween = n1; n1 = n2; n2 = inbetween; } }
 * result = new ArrayVector(n1.getCoordinates());
 * result.subtract(n2.getCoordinates()); System.out.println(e.getKey() + "\t" +
 * n1.getIdentity() + "\t" + n2.getIdentity());
 * System.out.println(APax.cosine(result));
 * System.out.println(Vax.cosine(result)); } }
 * 
 * } } }
 */