package org.kuleuven.lineager;

import ij.ImagePlus;
import ij.process.ColorProcessor;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.kuleuven.collections.ComparatorFactory;
import org.kuleuven.collections.Pair;
import org.kuleuven.collections.SortedListMap;
import org.kuleuven.lineagetree.Lineage2StatisticsScript;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.lineagetree.StarryNightFormatIO;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.math.MathEMC;
import org.kuleuven.math.matrix.Matrix;
import org.kuleuven.math.space.IntegerSpace;
import org.kuleuven.math.space.Space;
import org.kuleuven.math.vector.ArrayVector;
import org.kuleuven.math.vector.Vector;
import org.kuleuven.utilities.ImageUtilities;
import org.kuleuven.utilities.RandomUtilities;
import org.kuleuven.utilities.SetUtilities;
import org.kuleuven.utilities.StringUtilities;
import org.kuleuven.utilities.TextFileUtilities;

public class GetProjection {
	// String lineage =
	// "/Users/rob/Projects/CE_lineaging/Lineages/wt/wt-100809.zip";
	static String lineageDir = "/Users/robjelier/Projects/ChromatinRNAiScreen/newComparison/";
	static String lineage = "wt-100809.zip";
	public static Color[] colors = { new Color(228, 26, 28),
			new Color(55, 126, 184), new Color(77, 175, 74),
			new Color(152, 78, 163), new Color(255, 127, 0),
			new Color(238, 203, 173), new Color(255, 255, 51),
			new Color(166, 86, 40), new Color(247, 129, 191),
			new Color(153, 153, 153) };
	public double tolerance = 1.2;
	int xPunten = 250;
	int yPunten = 250;
	int maxTime = 200;
	private LineageMeasurement lm;
	private SortedListMap<Integer, List<Nucleus>> nucleiOverTime;
	private HashMap<Nucleus, TrackedNucleus> nucleusMap;
	private float maxAP;
	private float minAP;
	private float maxLR;
	private float minLR;
	private float maxDV;
	private float minDV;
	private double varyColorAmplitude = 0;
	private Map<String, Color> colorMap = new HashMap<String, Color>();
	private String outDir = "/Users/robjelier/Projects/ChromatinRNAiScreen/newComparison//";
	private int windowSize = 3;
	private float minDiameter;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		new GetProjection().surfaceNuclei();
		new GetProjection().summarize();
	}

	public void summarize() {
		File f = new File(outDir);
		String[] files = f.list();
		HashMap<String, Pair<List<String>, List<Integer>>> ingressions = new HashMap<String, Pair<List<String>, List<Integer>>>();
		HashMap<String, Pair<List<String>, List<Integer>>> egressions = new HashMap<String, Pair<List<String>, List<Integer>>>();
		HashMap<String, Pair<List<String>, List<Integer>>> confused = new HashMap<String, Pair<List<String>, List<Integer>>>();
		for (String file : files) {
			if (file.endsWith("_InAndEgressions.txt")) {
				String name = file.substring(0, file.indexOf("_"));
				List<String> lines = TextFileUtilities.loadFromFile(outDir
						+ file);
				for (String line : lines) {
					String[] cells = line.split("\t");
					String[] gressions = cells[2].split(";");
					if (gressions.length == 2 || gressions.length == 6) {
						if (gressions[1].equals("-1")) {
							Pair<List<String>, List<Integer>> support = ingressions
									.get(cells[0]);

							if (support == null) {
								List<String> ids = new ArrayList<String>();
								List<Integer> times = new ArrayList<Integer>();
								support = new Pair<List<String>, List<Integer>>(
										ids, times);
								ingressions.put(cells[0], support);
							}
							String out = new String(name);
							if (gressions.length == 6) {
								out = out + "*";
							}
							support.object1.add(out);
							support.object2.add(Integer.parseInt(cells[1]));

						} else if (gressions[1].equals("1")) {
							Pair<List<String>, List<Integer>> support = egressions
									.get(cells[0]);
							if (support == null) {
								List<String> ids = new ArrayList<String>();
								List<Integer> times = new ArrayList<Integer>();
								support = new Pair<List<String>, List<Integer>>(
										ids, times);
								egressions.put(cells[0], support);
							}
							String out = new String(name);
							if (gressions.length == 6) {
								out = out + "*";
							}
							support.object1.add(out);
							support.object2.add(Integer.parseInt(cells[1]));
						}
					} else {
						Pair<List<String>, List<Integer>> support = confused
								.get(cells[0]);

						if (support == null) {
							List<String> ids = new ArrayList<String>();
							List<Integer> times = new ArrayList<Integer>();
							support = new Pair<List<String>, List<Integer>>(
									ids, times);
							confused.put(cells[0], support);
						}
						support.object1.add(name);
						support.object2.add(Integer.parseInt(cells[1]));

					}
				}
			}
		}
		Iterator<Entry<String, Pair<List<String>, List<Integer>>>> it = ingressions.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, Pair<List<String>, List<Integer>>> e = it.next();
			boolean stop = false;
			if (egressions.containsKey(e.getKey())) {
				Pair<List<String>, List<Integer>> egres = egressions.get(e.getKey());
				if (e.getValue().object1.size() > 2 && egres.object1.size() <= 2) {
					egressions.remove(e.getKey());
					System.out
							.println("Removed egression to favor an ingression for "
									+ e.getKey());
				} else if (egres.object1.size() > 2 && e.getValue().object1.size() <= 2) {
					it.remove();
					stop = true;
					System.out
							.println("Removed ingression to favor an egression for "
									+ e.getKey());
				} else {
					Pair<List<String>, List<Integer>> con = confused.get(e.getKey());
					if (con == null) {
						List<String> ids = new ArrayList<String>();
						List<Integer> times = new ArrayList<Integer>();
						con = new Pair<List<String>, List<Integer>>(
								ids, times);
						confused.put(e.getKey(), con);
					}
					con.object1.addAll(e.getValue().object1);
					con.object1.addAll(egres.object1);
					con.object2.addAll(e.getValue().object2);
					con.object2.addAll(egres.object2);

					it.remove();
					stop = true;
					System.out
					.println("Ingression- egression confusion "
							+ e.getKey());
				}
			}
			if (confused.containsKey(e.getKey()) && !stop) {
				Pair<List<String>, List<Integer>> confuse = confused.get(e.getKey());
				if (e.getValue().object1.size() > 2 && confuse.object1.size() <= 2) {
					Pair<List<String>, List<Integer>> removed = confused.remove(e.getKey());
					System.out.println(e.getKey() + " " + removed.object1.size());
				} else {
					confuse.object1.addAll(e.getValue().object1);
					confuse.object2.addAll(e.getValue().object2);
					it.remove();
				}
			}
		}
		it = egressions.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Pair<List<String>, List<Integer>>> e = it.next();

			if (confused.containsKey(e.getKey())) {
				Pair<List<String>, List<Integer>> confuse = confused.get(e.getKey());
				if (e.getValue().object1.size() > 2 && confuse.object1.size() <= 2) {
					confused.remove(e.getKey());
				} else {
					confuse.object1.addAll(e.getValue().object1);
					confuse.object2.addAll(e.getValue().object2);
					it.remove();
				}
			}
		}

		Set<String> ineg = SetUtilities.Intersection(ingressions.keySet(),
				egressions.keySet());
		Set<String> inconf = SetUtilities.Intersection(ingressions.keySet(),
				confused.keySet());
		Set<String> egconf = SetUtilities.Intersection(confused.keySet(),
				egressions.keySet());
		System.out.println("intersection in / egressions:\t"
				+ StringUtilities.join(ineg, "\t"));
		System.out.println("intersection ingressions - confused:\t"
				+ StringUtilities.join(inconf, "\t"));
		System.out.println("intersection egressions - confused:\t"
				+ StringUtilities.join(egconf, "\t"));
		List<String> lines = new ArrayList<String>();
		lines.add("\ningressions");
		lines.add("");
		
		for (Entry<String,Pair<List<String>, List<Integer>>> in : ingressions.entrySet()) {
			lines.add(in.getKey() + "\t" + in.getValue().object1.size() + "\t"+MathEMC.median(in.getValue().object2)+"\t"
					+ StringUtilities.join(in.getValue().object1, ";")+"\t"+ StringUtilities.join(in.getValue().object2, ";"));
		}
		lines.add("\negressions");
		lines.add("");
		for (Entry<String, Pair<List<String>, List<Integer>>> in : egressions.entrySet()) {
			lines.add(in.getKey() + "\t" + in.getValue().object1.size() + "\t"+MathEMC.median(in.getValue().object2)+"\t"
					+ StringUtilities.join(in.getValue().object1, ";")+"\t"+ StringUtilities.join(in.getValue().object2, ";"));
		}
		lines.add("\nconfused");
		lines.add("");
		for (Entry<String, Pair<List<String>, List<Integer>>> in : confused.entrySet()) {
			lines.add(in.getKey() + "\t" + in.getValue().object1.size() + "\t"+MathEMC.median(in.getValue().object2)+"\t"
					+ StringUtilities.join(in.getValue().object1, ";")+"\t"+ StringUtilities.join(in.getValue().object2, ";"));
		}
		TextFileUtilities.saveToFile(lines, outDir
				+ "inAndEgressionSummary-cleaned.txt");

	}

	private void surfaceNuclei() {
		try {
			Map<String, LineageMeasurement> m = Lineage2StatisticsScript
					.getLineagesDir(lineageDir, "analyses");
			for (Entry<String, LineageMeasurement> entry : m.entrySet()) {

				LineageMeasurement lm = entry.getValue();
				int c=Lineage2StatisticsScript.getCorrection(lm);
				String name = lm.getName();
				Map<Integer, Set<Nucleus>> outcome = getSurfaceNucleiOverTime(
						lm, maxTime);

				Map<TrackedNucleus, List<Integer>> statesPerNucleus = new HashMap<TrackedNucleus, List<Integer>>();

				Iterator<SortedListMap<Integer, List<Nucleus>>.MapEntry<Integer, List<Nucleus>>> it = nucleiOverTime
						.entryIterator();
				int time = 0;
				while (it.hasNext()
						&& (time + 1) < Math
								.min(maxTime, lm.getLastTimePoint())) {
					SortedListMap<Integer, List<Nucleus>>.MapEntry<Integer, List<Nucleus>> e = it
							.next();
					time = e.getKey();
					Set<Nucleus> surfaceNuclei = outcome.get(time);
					for (Nucleus n : e.getValue()) {
						TrackedNucleus tn = nucleusMap.get(n);
						List<Integer> states = statesPerNucleus.get(tn);
						if (states == null) {
							states = new ArrayList<Integer>(20);
							statesPerNucleus.put(tn, states);
						}
						if (surfaceNuclei.contains(n)) {
							states.add(1);
						} else {
							states.add(0);
						}
					}
				}
				List<String> lines = new ArrayList<String>();
				for (Entry<TrackedNucleus, List<Integer>> e : statesPerNucleus
						.entrySet()) {
					List<Integer> state = stateAnalysis(e.getValue());
					List<Integer> detect = detectStateChange(state);
					 
					if (detect.size() > 0){
						int first = e.getKey().getFirstTimePoint()+detect.get(0)-c;
						lines.add(e.getKey() + "\t"
								+ first + "\t"
								+ StringUtilities.join(detect, ";"));
					}
				}
				TextFileUtilities.saveToFile(lines, outDir + name
						+ "_InAndEgressions.txt");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// do you see clean states change within the window size?
	private List<Integer> detectStateChange(List<Integer> stateList) {
		int state = -1;
		List<Integer> stateChanges = new ArrayList<Integer>();
		for (int i = 0; i < stateList.size(); i++) {
			int current = stateList.get(i);
			if (current >= 0) {
				if (state >= 0) {
					if (state != current) {
						stateChanges.add(i);
						stateChanges.add(current - state);
						state = current;
					}
				} else {
					state = current;
				}
			}
		}
		return stateChanges;
	}

	private List<Integer> stateAnalysis(List<Integer> value) {
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i <= value.size() - windowSize - 1; i++) {
			int state = value.get(i);
			for (int j = i + 1; j < i + windowSize + 1; j++) {
				if (state != value.get(j)) {
					state = -1;
				}
			}
			result.add(state);
		}
		return result;
	}

	public Map<Integer, Set<Nucleus>> getSurfaceNucleiOverTime(
			LineageMeasurement lm, int maxTime) throws Exception {
		// reorient lineage
		this.lm = lm;
		Matrix<Integer, Integer> transformationMatrix = GetAxes
				.getOrientLineageMatrix(lm, GetAxes.ORIENTMS);
		parseLineage(lm, transformationMatrix);
		ArrayVector<Integer> center = getCenter();
		Map<Integer, Set<Nucleus>> result = new SortedListMap<Integer, Set<Nucleus>>(
				ComparatorFactory.getDescendingIntegerComparator());
		// move over time
		for (int time = 1; time < Math.min(maxTime, lm.getLastTimePoint()); time++) {
			// algorithm: .
			if (nucleiOverTime.containsKey(time)) {
				System.out.println(time);
				List<Nucleus> nucs = nucleiOverTime.get(time);
				Set<Nucleus> resultT = new HashSet<Nucleus>();
				result.put(time, resultT);
				for (int i = 0; i < nucs.size(); i++) {
					Nucleus nuci = nucs.get(i);

					int j = 0;
					boolean exterior = true;
					while (j < nucs.size() && exterior) {
						if (i != j) {
							Nucleus nucj = nucs.get(j);
							if (nuci.distanceTo(nucj) < minDiameter * 2f / 3f) {
								ArrayVector<Integer> POL = GetAxes
										.closestPointOnLine(center,
												nuci.getCoordinates(),
												nucj.getCoordinates());
								double POL2CENTER = POL.distanceTo(center);
								double NUCI2CENTER = nuci.getCoordinates()
										.distanceTo(center);
								double POL2NUCI = POL.distanceTo(nuci
										.getCoordinates());
								double POL2NUCJ = POL.distanceTo(nucj
										.getCoordinates());
								if (POL2CENTER > NUCI2CENTER
										&& POL2NUCI > POL2NUCJ) {
									exterior = false;
								}
							}
						}
						j++;
					}
					if (exterior)
						resultT.add(nuci);
				}
			}
		}

		return result;
	}

	public Map<Integer, Set<Nucleus>> getSurfaceNucleiOverTimeOLD(
			LineageMeasurement lm) throws Exception {
		// reorient lineage
		Matrix<Integer, Integer> transformationMatrix = GetAxes
				.getOrientLineageMatrix(lm, GetAxes.ORIENTMS);
		parseLineage(lm, transformationMatrix);

		float radius = 0.52f * (Math.max(maxLR - minLR, maxDV - minDV));

		float step = (maxAP - minAP) / xPunten;
		float middleLR = (maxLR + minLR) / 2;
		float middleDV = (maxDV + minDV) / 2;
		Map<Integer, Set<Nucleus>> result = new SortedListMap<Integer, Set<Nucleus>>(
				ComparatorFactory.getDescendingIntegerComparator());
		// move over time
		for (int time = 1; time < Math.min(maxTime, lm.getLastTimePoint()); time++) {
			// algorithm: scan the surface as a column, at every point finding
			// the closest nucleus.
			if (nucleiOverTime.containsKey(time)) {
				System.out.println(time);
				Set<Nucleus> set = new HashSet<Nucleus>();
				for (int i = 0; i < xPunten; i++) {
					double AP = minAP + step * (float) i;
					for (int j = 0; j < yPunten; j++) {
						double angle = 2 * Math.PI * j / yPunten;

						double LR1 = middleLR + Math.sin(angle) * radius;
						double DV1 = middleDV - Math.cos(angle) * radius;
						double[] v = { AP, LR1, DV1 };
						ArrayVector<Integer> av1 = new ArrayVector<Integer>(
								IntegerSpace.threeD, v);
						Nucleus nuc = findNearest(nucleiOverTime.get(time), av1);
						if (nuc != null)
							set.add(nuc);

					}
				}
				// patch extremities with scanning a plate
				double APstart = minAP - 0 * radius;
				int points = (int) Math.round(xPunten / 2.5);
				double LRstep = (maxLR - minLR) / points;
				double DVstep = (maxDV - minDV) / points;
				for (int i = 0; i < points; i++) {
					double LR = minLR + i * LRstep;
					for (int j = 0; j < points; j++) {
						double DV = minDV + i * DVstep;
						double[] v = { APstart, LR, DV };
						ArrayVector<Integer> av1 = new ArrayVector<Integer>(
								IntegerSpace.threeD, v);
						Nucleus nuc = findNearest(nucleiOverTime.get(time), av1);
						if (nuc != null) {
							set.add(nuc);
						}

					}
				}
				double APend = maxAP + 0 * radius;
				for (int i = 0; i < points; i++) {
					double LR = minLR + i * LRstep;
					for (int j = 0; j < points; j++) {
						double DV = minDV + i * DVstep;
						double[] v = { APend, LR, DV };
						ArrayVector<Integer> av1 = new ArrayVector<Integer>(
								IntegerSpace.threeD, v);
						Nucleus nuc = findNearest(nucleiOverTime.get(time), av1);
						if (nuc != null)
							set.add(nuc);

					}
				}
				result.put(time, set);
			}
		}
		return result;
	}

	private void run() {
		try {
			colorMap.put("ABp", colors[1]);
			colorMap.put("ABa", colors[0]);

			colorMap.put("ABpl", colors[1]);
			colorMap.put("ABpla", colors[1]);
			colorMap.put("ABplp", colors[2]);
			colorMap.put("ABpr", colors[7]);
			colorMap.put("ABal", colors[0]);
			colorMap.put("ABar", colors[4]);
			colorMap.put("ABarp", colors[4]);
			colorMap.put("ABarpapp", Color.white);
			colorMap.put("ABara", colors[8]);
			colorMap.put("MS", colors[3]);
			colorMap.put("E", colors[6]);
			colorMap.put("C", colors[9]);
			colorMap.put("D", colors[5]);
			colorMap.put("P", Color.black);
			colorMap.put("Z", Color.black);
			LineageMeasurement lm = StarryNightFormatIO.readNuclei(lineage,
					0.1f, 1f, 1f);
			Matrix<Integer, Integer> mat = GetAxes.getOrientLineageMatrix(lm,
					GetAxes.ORIENTMS);
			parseLineage(lm, mat);
			float radius = 0.65f * (Math.max(maxLR - minLR, maxDV - minDV));
			int maxTime = 150;
			float step = (maxAP - minAP) / xPunten;
			float middleLR = (maxLR + minLR) / 2;
			float middleDV = (maxDV + minDV) / 2;
			for (int time = 1; time < maxTime; time++) {
				Map<Nucleus, Color> colorMap = getColorMap(time);
				ColorProcessor cp = new ColorProcessor(xPunten, yPunten);
				for (int i = 0; i < xPunten; i++) {
					double AP = minAP + step * (float) i;
					for (int j = 0; j < yPunten; j++) {
						double angle = 2 * Math.PI * j / yPunten;

						double LR1 = middleLR + Math.sin(angle) * radius;
						double DV1 = middleDV - Math.cos(angle) * radius;
						double[] first = { AP, LR1, DV1 };
						cp.set(i, j, getColor(first, time, colorMap));

					}
				}
				Formatter form1 = new Formatter();
				String file = form1.format("%1$2s%2$2s-t%3$03d%4$2s%5$02d.tif",
						outDir, "surface_", time, "-p", 1).toString();
				ImageUtilities.saveAsTif(new ImagePlus("", cp), file);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private int varyColor(int original) {
		int val = (int) (original + varyColorAmplitude
				* RandomUtilities.random.nextGaussian());
		if (val < 0) {
			val = -val;
		}
		if (val > 255) {
			val = 255 - val % 255;
		}
		return val;
	}

	private Color varyColor(Color actualColor) {
		return new Color(varyColor(actualColor.getRed()),
				varyColor(actualColor.getGreen()),
				varyColor(actualColor.getBlue()));
	}

	private Map<Nucleus, Color> getColorMap(int time) {
		Map<Nucleus, Color> result = new HashMap<Nucleus, Color>();
		List<Nucleus> nucs = nucleiOverTime.get(time);
		for (Nucleus n : nucs) {
			String name = nucleusMap.get(n).getName();
			String last = "";
			Color c = Color.white;
			Iterator<String> it = colorMap.keySet().iterator();
			while (it.hasNext()) {
				String e = it.next();
				if (name.startsWith(e) & last.length() < e.length()) {
					c = varyColor(colorMap.get(e));
					last = e;
				}
			}
			result.put(n, c);
		}
		return result;
	}

	private int getColor(double[] v, int time, Map<Nucleus, Color> colorMap) {
		ArrayVector<Integer> av1 = new ArrayVector<Integer>(
				IntegerSpace.threeD, v);
		Nucleus nuc = findNearest(nucleiOverTime.get(time), av1);
		if (nuc == null) {
			return Color.black.getRGB();
		}
		Color col = colorMap.get(nuc);
		return col.getRGB();
	}

	private Nucleus findNearest(List<Nucleus> list, ArrayVector<Integer> av1) {
		double min = Double.MAX_VALUE;
		Nucleus n = null;
		for (Nucleus nuc : list) {
			double dis = nuc.getCoordinates().squaredDistanceTo(av1);
			if (Math.abs(dis - min) < tolerance) {
				n = null;
				min = Math.min(dis, min);
			}
			if (dis < min - tolerance) {
				min = dis;
				n = nuc;
			}
		}
		return n;
	}

	private ArrayVector<Integer> getCenter() {
		List<Vector<Integer>> positions = new ArrayList<Vector<Integer>>();

		int correction = Lineage2StatisticsScript.getCorrection(lm);
		for (int i = correction; i < Math.min(correction + 100,
				lm.getLastTimePoint()); i++) {
			List<Nucleus> n = nucleiOverTime.get(i);
			for (Nucleus nuc : n)
				positions.add(nuc.getCoordinates());
		}
		ArrayVector<Integer> center = new ArrayVector<Integer>(Space.threeD);
		Vector.meanVector(center, positions);
		return center;
	}

	private void parseLineage(LineageMeasurement lm,
			Matrix<Integer, Integer> transformationMatrix) {
		Map<Integer, Set<Nucleus>> not = lm.getNucleiOverTime();
		nucleiOverTime = new SortedListMap<Integer, List<Nucleus>>(
				ComparatorFactory.getAscendingIntegerComparator());
		nucleusMap = new HashMap<Nucleus, TrackedNucleus>(
				2 * lm.nucleusMap.size());
		this.maxAP = -Float.MAX_VALUE;
		this.minAP = Float.MAX_VALUE;
		this.maxLR = -Float.MAX_VALUE;
		this.minLR = Float.MAX_VALUE;
		this.maxDV = -Float.MAX_VALUE;
		this.minDV = Float.MAX_VALUE;

		if (transformationMatrix != null) {

			for (Entry<Integer, Set<Nucleus>> eNot : not.entrySet()) {
				List<Nucleus> newNucs = new ArrayList<Nucleus>(eNot.getValue()
						.size());

				for (Nucleus n : eNot.getValue()) {
					if (lm.nucleusMap.containsKey(n)) {
						Nucleus copy = new Nucleus(n);
						Vector<Integer> adapted;
						adapted = GetAxes.transform(transformationMatrix,
								copy.getCoordinates(), GetAxes.ORIENTMS);
						maxAP = (float) Math.max(adapted.get(0), maxAP);
						minAP = (float) Math.min(adapted.get(0), minAP);
						maxLR = (float) Math.max(adapted.get(1), maxLR);
						minLR = (float) Math.min(adapted.get(1), minLR);
						maxDV = (float) Math.max(adapted.get(2), maxDV);
						minDV = (float) Math.min(adapted.get(2), minDV);

						copy.setCoordinates(adapted);
						newNucs.add(copy);
						nucleusMap.put(copy, lm.nucleusMap.get(n));
					}

				}
				nucleiOverTime.put(eNot.getKey(), newNucs);
			}
		}
		this.minDiameter = Math.min(maxLR - minLR, maxDV - minDV);

	}

}
