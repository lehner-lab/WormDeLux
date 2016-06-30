package org.kuleuven.lineagetree;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kuleuven.collections.Pair;
import org.kuleuven.lineager.GetAxes;
import org.kuleuven.math.MathEMC;
import org.kuleuven.math.matrix.Matrix;
import org.kuleuven.math.space.Space;
import org.kuleuven.math.vector.ArrayVector;
import org.kuleuven.math.vector.Vector;
import org.kuleuven.utilities.StringUtilities;
import org.kuleuven.utilities.TextFileUtilities;

public class Lineage2StatisticsScript {

	String baseDirectory = "/Users/robjelier/Projects/ChromatinRNAiScreen/newComparison/";
	String out = "analyses";
	
	String wildTypes = "compressed";
	String phenoTypes = "let-526";//
	static String s = System.getProperty("file.separator");
	int startT = 0;//2
	private boolean doMovements = false;
	private boolean doCellDistances = false;
	private boolean doDistancesPerTime = true;
	private boolean doAngles = true;
	private boolean doPositions = true;
	private int orientationMode = GetAxes.ORIENTMS;
	Map<LineageMeasurement, float[]> embryoSizes = new HashMap<LineageMeasurement, float[]>();
	private HashMap<String, Matrix<Integer, Integer>> transformationMatrices;
	private static Pattern p = Pattern.compile("^.*XY([0-9]+[.,][0-9]+).*zip");
	private String[] baseCells = { "ABal", "ABpl", "ABpr", "ABar" };
	private float[] refMeasure;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Lineage2StatisticsScript().run();

	}

	
	

	private void run() {
		Map<String, LineageMeasurement> lms = getLineagesDir(baseDirectory,out);
		Set<String> wt = new HashSet<String>();
		Set<String> wtNucleiNames = new HashSet<String>();
		for (String s : lms.keySet()) {
			if (s.startsWith(wildTypes)) {
				wt.add(s);
				LineageMeasurement lm = lms.get(s);
				Set<TrackedNucleus> tns = lm.getAllTrackedNuclei();
				for (TrackedNucleus tn : tns) {
					wtNucleiNames.add(tn.getName());
				}
			}
		}
		initializeEmbryoSize(lms);
		Map<LineageMeasurement, Integer> corrections = getCorrections(lms
				.values());
		Map<String, Map<String, List<Integer>>> timings = getTimings(lms,corrections);
		Map<String, Map<String, List<Float>>> netmov = getCellMovements(lms,
				true, corrections);
		Map<String, Map<String, List<Float>>> totalmov = getCellMovements(lms,
				false, corrections);
		Map<String, Map<String, List<Float>>> angles = getAngles(lms, wt);
		Map<String, Map<String, Nucleus>> pos = getPositions(lms);

		List<String> lmNames = new ArrayList<String>(lms.keySet());
		Collections.sort(lmNames);
		List<String> nNames = new ArrayList<String>(wtNucleiNames);
		Collections.sort(nNames);

		// Collections.reverse(lmNames);
		StringBuffer headers = new StringBuffer("name");
		for (String lmname : lmNames) {
			headers.append("\t");
			headers.append(lmname);
			headers.append("_Tbirth");
			headers.append("\t");
			headers.append(lmname);
			headers.append("_Tdeath");
			headers.append("\t");
			headers.append(lmname);
			headers.append("_netdis");
			headers.append("\t");
			headers.append(lmname);
			headers.append("_totdis");
			headers.append("\t");
			headers.append(lmname);
			headers.append("_aMean");
			headers.append("\t");
			headers.append(lmname);
			headers.append("_aAP");
			headers.append("\t");
			headers.append(lmname);
			headers.append("_aDV");
			headers.append("\t");
			headers.append(lmname);
			headers.append("_aLR");
			headers.append("\t");
			headers.append(lmname);
			headers.append("_pAP");
			headers.append("\t");
			headers.append(lmname);
			headers.append("_pDV");
			headers.append("\t");
			headers.append(lmname);
			headers.append("_pLR");
			headers.append("\t");
			headers.append(lmname);
			headers.append("_pOV");
		}
		List<String> lines = new ArrayList<String>();
		lines.add(headers.toString());
		for (String name : nNames) {
			System.out.println(name);
			StringBuffer sb = new StringBuffer(name);
			Map<String, List<Integer>> timingN = timings.get(name);
			Map<String, List<Float>> netmovN = netmov.get(name);
			Map<String, List<Float>> totmovN = totalmov.get(name);
			Map<String, List<Float>> anglesN = angles.get(name);
			Map<String, Nucleus> posN = pos.get(name);
			if (netmovN != null) {
				float[] refPos=new float[3];
				int count = 0;
				for(String wtL:wt){
					Nucleus nuc= posN.get(wtL);
					if(nuc!=null){
						refPos[0]+=(float)nuc.getX();
						refPos[1]+=(float)nuc.getY();
						refPos[2]+=(float)nuc.getZ();
						count++;
					}
					
				}
				refPos[0]/=count;
				refPos[1]/=count;
				refPos[2]/=count;
				for (String lmname : lmNames) {

					sb.append("\t");
					List<Integer> timingD = timingN.get(lmname);

					if (timingD != null) {

						sb.append(StringUtilities.join(timingD, "\t"));
					} else {
						sb.append("NA\tNA");
					}
					sb.append("\t");
					List<Float> netmovD = netmovN.get(lmname);

					if (netmovD != null) {
						sb.append(netmovD.get(2));
					} else {
						sb.append("NA");
					}
					sb.append("\t");
					List<Float> totmovD = totmovN.get(lmname);
					if (totmovD != null) {
						sb.append(totmovD.get(2));
					} else {
						sb.append("NA");
					}
					sb.append("\t");

					if (anglesN != null) {
						List<Float> anglesD = anglesN.get(lmname);
						if (anglesD != null) {
							sb.append(StringUtilities.join(anglesD, "\t"));
						} else {
							sb.append("NA\tNA\tNA\tNA");
						}
					} else {
						sb.append("NA\tNA\tNA\tNA");
						// System.out.println("no angles for " + name);
					}
					sb.append("\t");
					Nucleus posD = posN.get(lmname);
					if (posD != null) {
						sb.append(posD.getX());
						sb.append("\t");
						sb.append(posD.getY());
						sb.append("\t");
						sb.append(posD.getZ());
						sb.append("\t");
						double dX=refMeasure[0]*(refPos[0]-posD.getX());
						double dY=refMeasure[1]*(refPos[1]-posD.getY());
						double dZ=refMeasure[2]*(refPos[2]-posD.getZ());
						double base=dX*dX+dY*dY+dZ*dZ;
						sb.append(Math.sqrt(base));
						
					} else {
						sb.append("NA\tNA\tNA\tNA");
					}

				}
				lines.add(sb.toString());
			} else {
				// System.out.println(name);
			}
		}
		TextFileUtilities.saveToFile(lines, baseDirectory + out
				+ "/bigTableN.txt");
		List<String> perLM = new ArrayList<String>();
		for (Entry<String, LineageMeasurement> e : lms.entrySet()) {
			String line = e.getKey() + "\t" + e.getValue().getName();
			perLM.add(line);
		}
		TextFileUtilities.saveToFile(perLM, baseDirectory + out
				+ "/bigTableNamesN.txt");

	}

	private void initializeEmbryoSize(Map<String, LineageMeasurement> lms) {
		transformationMatrices = new HashMap<String, Matrix<Integer, Integer>>();
		float APlength = 0;
		float DVlength = 0;
		float LRlength = 0;
		float total = 0;
		for (Entry<String, LineageMeasurement> e : lms.entrySet()) {
			float minAP = Float.MAX_VALUE;
			float minDV = Float.MAX_VALUE;
			float minLR = Float.MAX_VALUE;
			float maxAP = Float.NEGATIVE_INFINITY;
			float maxDV = Float.NEGATIVE_INFINITY;
			float maxLR = Float.NEGATIVE_INFINITY;
			Matrix<Integer, Integer> transformationMatrix = null;
			try {
				transformationMatrix = GetAxes.getOrientLineageMatrix(
						e.getValue(), orientationMode);
			} catch (Exception e1) {

				e1.printStackTrace();
			}
			if (transformationMatrix == null)
				System.out.println(e.getKey());
			transformationMatrices.put(e.getKey(), transformationMatrix);

			Map<Integer, Set<Nucleus>> not = e.getValue().getNucleiOverTime();
			Map<String, Nucleus> nucleusMap = new HashMap<String, Nucleus>(
					2 * e.getValue().nucleusMap.size());

			if (transformationMatrix != null) {
				float[] measures = new float[6];
				embryoSizes.put(e.getValue(), measures);
				for (Entry<Integer, Set<Nucleus>> eNot : not.entrySet()) {
					for (Nucleus n : eNot.getValue()) {
						Nucleus copy = new Nucleus(n);
						Vector<Integer> adapted = GetAxes.transform(
								transformationMatrix, copy.getCoordinates(),
								orientationMode);

						minAP = (float) Math.min(minAP, adapted.get(0)
								- copy.radius);
						minDV = (float) Math.min(minDV, adapted.get(1)
								- copy.radius);
						minLR = (float) Math.min(minLR, adapted.get(2)
								- copy.radius);
						maxAP = (float) Math.max(maxAP, adapted.get(0)
								+ copy.radius);
						maxDV = (float) Math.max(maxDV, adapted.get(1)
								+ copy.radius);
						maxLR = (float) Math.max(maxLR, adapted.get(2)
								+ copy.radius);
						TrackedNucleus tn = e.getValue()
								.getTrackedNucleusForNucleus(n);
						if (tn.getLastTimePoint() == n.timePoint) {
							copy.setCoordinates(adapted);
							nucleusMap.put(e.getValue().nucleusMap.get(n)
									.getName(), copy);
						}
					}
				}
				measures[0] = minAP;
				measures[1] = maxAP;
				measures[2] = minDV;
				measures[3] = maxDV;
				measures[4] = minLR;
				measures[5] = maxLR;
				APlength += maxAP - minAP;
				DVlength += maxDV - minDV;
				LRlength += maxLR - minLR;
				total++;
				System.out.println(e.getValue().getName() + "\t"
						+ (maxAP - minAP));
			}
		}
		refMeasure = new float[3];
		refMeasure[0] = APlength / total;
		refMeasure[1] = DVlength / total;
		refMeasure[2] = LRlength / total;
	}


	
	public Map<String, Map<String, List<Float>>> getAverageDistances(
			Map<String, LineageMeasurement> all) {

		Map<String, Map<String, List<Float>>> result = new HashMap<String, Map<String, List<Float>>>();
		for (Entry<String, LineageMeasurement> lm : all.entrySet()) {
			List<TrackedNucleus> tns = new ArrayList<TrackedNucleus>(lm
					.getValue().getAllTrackedNuclei());
			for (int k = 0; k < tns.size(); k++) {
				TrackedNucleus tna1 = tns.get(k);
				// if(tn1.getName().equalsIgnoreCase("ABPL")||tn1.getName().equalsIgnoreCase("ABPR")){
				for (int l = k + 1; l < tns.size(); l++) {
					TrackedNucleus tna2 = tns.get(l);
					int ot = getOverlappingTime(tna1, tna2);
					if (tna1 != tna2 && ot > 1) {
						String key;
						if (tna1.getName().compareToIgnoreCase(tna2.getName()) <= 0) {
							key = tna1.getName().toUpperCase() + "\t"
									+ tna2.getName().toUpperCase();
						} else {
							key = tna2.getName().toUpperCase() + "\t"
									+ tna1.getName().toUpperCase();
						}
						Map<String, List<Float>> map = result.get(key);
						if (map == null) {
							map = new HashMap<String, List<Float>>();
							result.put(key, map);
						}
						TrackedNucleus first = tna1;
						TrackedNucleus second = tna2;
						if (tna1.getFirstTimePoint() > tna2.getFirstTimePoint()) {
							first = tna2;
							second = tna1;
						}
						for (int t = second.getFirstTimePoint() + startT; t < second
								.getFirstTimePoint() + startT + ot; t++) {
							Float d = first.getNucleusForTimePoint(t)
									.distanceTo(
											second.getNucleusForTimePoint(t));
							List<Float> list = new ArrayList<Float>();
							list.add(d);
							map.put(lm.getKey(), list);
						}

					}// }}
				}
			}
		}
		return result;
	}

	public static void reOrientLineage(LineageMeasurement lm) throws Exception {
		int correction = getCorrection(lm);
		TrackedNucleus ABaT = lm.retrieveTrackedNucleusByName("ABa");
		TrackedNucleus ABpT = lm.retrieveTrackedNucleusByName("ABp");
		TrackedNucleus P2T = lm.retrieveTrackedNucleusByName("P2");
		TrackedNucleus EMST = lm.retrieveTrackedNucleusByName("EMS");
		Nucleus ABa = ABaT.getNucleusForTimePoint(correction);
		Nucleus ABp = ABpT.getNucleusForTimePoint(correction);
		Nucleus P2 = P2T.getNucleusForTimePoint(correction);
		Nucleus EMS = EMST.getNucleusForTimePoint(correction);
		org.kuleuven.math.matrix.Matrix<Integer, Integer> transformationMatrix = GetAxes
				.getOURTransformationMatrix(GetAxes.getAxes(
						ABa.getCoordinates(), ABp.getCoordinates(),
						P2.getCoordinates(), EMS.getCoordinates()));
		for (Nucleus n : lm.nucleusMap.keySet()) {
			Vector<Integer> adapted = GetAxes.transform(transformationMatrix,
					n.getCoordinates(), GetAxes.ORIENT4CELL);
			n.setCoordinates(adapted);
		}
	}

	public Map<String, Map<String, List<Float>>> getAngles(
			Map<String, LineageMeasurement> lms, Set<String> wildTypes) {
		Map<LineageMeasurement, Integer> corrections = getCorrections(lms
				.values());
		Map<String, Map<String, List<Float>>> result = new HashMap<String, Map<String, List<Float>>>();
		Map<String, Map<String, Vector<Integer>>> div = new HashMap<String, Map<String, Vector<Integer>>>();
		Set<String> keys = new HashSet<String>();
		for (Entry<String, LineageMeasurement> e : lms.entrySet()) {
			LineageMeasurement lm = (e.getValue());
			Matrix<Integer, Integer> axes = transformationMatrices.get(e
					.getKey());
			if (axes != null) {
				List<Division> l = lm.lineage.getDivisions();
				Map<String, Vector<Integer>> divMap = new HashMap<String, Vector<Integer>>();
				div.put(e.getKey(), divMap);
				for (Division d : l) {
					Pair<String, Vector<Integer>> t = d.getDivisionAxis();
					String key = t.object1;
					divMap.put(key, t.object2);
					keys.add(key);
				}
			}
		}
		Vector<Integer> APax = new ArrayVector<Integer>(Space.threeD);
		APax.set(0, -1);
		Vector<Integer> DVax = new ArrayVector<Integer>(Space.threeD);
		DVax.set(1, 1);
		Vector<Integer> LRax = new ArrayVector<Integer>(Space.threeD);
		LRax.set(2, 1);

		List<String> order = new ArrayList<String>(lms.keySet());
		for (String key : keys) {
			Map<String, Vector<Integer>> vectors = new HashMap<String, Vector<Integer>>();
			List<Vector<Integer>> wtvectors = new ArrayList<Vector<Integer>>();
			Map<String, List<Float>> map = new HashMap<String, List<Float>>();
			result.put(key, map);
			for (String e : order) {
				if (transformationMatrices.containsKey(e)&&transformationMatrices.get(e)!=null) {
					Matrix<Integer, Integer> axes = transformationMatrices
							.get(e);
					if (div.get(e).containsKey(key)) {
						Vector<Integer> d = div.get(e).get(key);
						d.normalize();
						Vector<Integer> adapted = GetAxes.transform(axes, d,
								orientationMode);
						vectors.put(e, adapted);
						if (wildTypes.contains(e)) {
							wtvectors.add(adapted);
						}
					}
				}
			}
			ArrayVector<Integer> mean = null;
			if (wtvectors.size() >= 3) {
				mean = new ArrayVector<Integer>(Space.threeD);
				Vector.meanVector(mean, wtvectors);
			}
			for (String e : order) {
				if (vectors.containsKey(e)) {
					List<Float> entries = new ArrayList<Float>();
					Vector<Integer> v = vectors.get(e);
					if (mean != null)
						entries.add((float) Math.acos(mean.cosine(v)));
					else
						entries.add(Float.NaN);
					entries.add((float) Math.acos(APax.cosine(v)));
					entries.add((float) Math.acos(DVax.cosine(v)));
					entries.add((float) Math.acos(LRax.cosine(v)));
					map.put(e, entries);
				}
			}

		}
		return result;
	}

	public void getVariancePerTimePoint(Map<String, LineageMeasurement> lms,
			List<String> sequence, String outDir) {

		Map<LineageMeasurement, Integer> corrections = getCorrections(lms
				.values());
		int last = 0;
		for (Entry<String, LineageMeasurement> lm : lms.entrySet()) {
			last = Math.max(last, lm.getValue().getLastTimePoint()
					- corrections.get(lm.getValue()));
		}
		for (int i = 1; i <= last; i++) {
			Map<String, Map<String, Nucleus>> map = new HashMap<String, Map<String, Nucleus>>();
			Set<String> names = new HashSet<String>();
			for (Entry<String, LineageMeasurement> e : lms.entrySet()) {
				Set<Nucleus> l = e.getValue().getNucleiOverTime()
						.get(i + corrections.get(e.getValue()));
				Map<String, Nucleus> nucmap = new HashMap<String, Nucleus>();
				map.put(e.getKey(), nucmap);
				if (l != null) {
					for (Nucleus n : l) {
						nucmap.put(n.getName(), n);
						names.add(n.getName());
					}
				}
			}
			String outfile = "PositionVariance_" + i + ".txt";
			String header = "cell1\tcell2";
			header += "\t" + StringUtilities.join(sequence, "\t");
			header += "\tmean\tSD";
			List<String> lines = new ArrayList<String>();
			lines.add(header);
			List<String> nameList = new ArrayList<String>(names);
			for (int h = 0; h < nameList.size(); h++) {
				String first = nameList.get(h);

				for (int j = h + 1; j < nameList.size(); j++) {
					String second = nameList.get(j);
					String line = first + "\t" + second;
					List<Float> distances = new ArrayList<Float>();
					for (String file : sequence) {
						Nucleus n1 = map.get(file).get(first);
						Nucleus n2 = map.get(file).get(second);
						if (n1 != null && n2 != null) {
							Float f = n1.distanceTo(n2);
							line += "\t" + f;
							distances.add(f);
						} else {
							line += "\tNA";
						}

					}
					line += "\t" + MathEMC.mean(distances) + "\t"
							+ MathEMC.getSD(distances);
					lines.add(line);
				}
			}
			TextFileUtilities.saveToFile(lines, outDir + outfile);
		}

	}

	private void toFile(String filename,
			Map<String, Map<String, List<Float>>> data, String header,
			List<String> indexSequence) {
		List<String> lines = new ArrayList<String>();
		lines.add(header);
		for (Entry<String, Map<String, List<Float>>> e : data.entrySet()) {
			if (e.getValue().size() > 0) {
				StringBuffer sb = new StringBuffer(e.getKey());
				Map<String, List<Float>> map = e.getValue();
				int size = map.values().iterator().next().size();
				for (String key : indexSequence) {
					if (map.containsKey(key)) {
						for (Float v : map.get(key)) {
							sb.append("\t");
							sb.append(v);
						}
					} else {
						for (int i = 0; i < size; i++) {
							sb.append("\tNA");
						}
					}
				}
				lines.add(sb.toString());
			}
		}
		TextFileUtilities.saveToFile(lines, filename);
	}

	private List<String> getCellMovementMiniHeader() {
		List<String> c = new ArrayList<String>();
		c.add("birth");
		c.add("time");
		c.add("distance");
		c.add("distance/time");
		return c;
	}

	private Map<String, Map<String, Nucleus>> getPositions(
			Map<String, LineageMeasurement> lms) {
		Map<String, Map<String, Nucleus>> result = new HashMap<String, Map<String, Nucleus>>();
		for (Entry<String, LineageMeasurement> e : lms.entrySet()) {
			Matrix<Integer, Integer> transformationMatrix = transformationMatrices
					.get(e.getKey());
			Map<Integer, Set<Nucleus>> not = e.getValue().getNucleiOverTime();
			Map<String, Nucleus> nucleusMap = new HashMap<String, Nucleus>(
					2 * e.getValue().nucleusMap.size());
			if (transformationMatrix != null) {

				for (Entry<Integer, Set<Nucleus>> eNot : not.entrySet()) {
					for (Nucleus n : eNot.getValue()) {
						Nucleus copy = new Nucleus(n);
						Vector<Integer> adapted = GetAxes.transform(
								transformationMatrix, copy.getCoordinates(),
								orientationMode);
						TrackedNucleus tn = e.getValue()
								.getTrackedNucleusForNucleus(n);
						if (tn.getLastTimePoint() == n.timePoint) {
							copy.setCoordinates(adapted);
							nucleusMap.put(e.getValue().nucleusMap.get(n)
									.getName(), copy);
						}
					}
				}
				float[] measures = embryoSizes.get(e.getValue());
				float minAP = measures[0];
				float minDV = measures[2];
				float minLR = measures[4];
				float maxAP = measures[1];
				float maxDV = measures[3];
				float maxLR = measures[5];
				float lengthAP = maxAP - minAP;
				float lengthDV = maxDV - minDV;
				float lengthLR = maxLR - minLR;
				for (Entry<String, Nucleus> entry : nucleusMap.entrySet()) {
					Nucleus n = entry.getValue();
					n.coordinates.set(0, (n.coordinates.get(0) - minAP)
							/ lengthAP);
					n.coordinates.set(1, (n.coordinates.get(1) - minDV)
							/ lengthDV);
					n.coordinates.set(2, (n.coordinates.get(2) - minLR)
							/ lengthLR);
					Map<String, Nucleus> rm = result.get(entry.getKey());
					if (rm == null) {
						rm = new HashMap<String, Nucleus>();
						result.put(entry.getKey(), rm);
					}
					rm.put(e.getKey(), n);
				}
			}
		}
		return result;
	}

	private Map<String, Map<String, List<Integer>>> getTimings(
			Map<String, LineageMeasurement> lms,
			Map<LineageMeasurement, Integer> corrections) {
		Map<String, Map<String, List<Integer>>> result = new HashMap<String, Map<String, List<Integer>>>();
		for (Entry<String, LineageMeasurement> e : lms.entrySet()) {
			LineageMeasurement lm = e.getValue();
			Integer correction = corrections.get(lm);
			for (TrackedNucleus tn : lm.getAllTrackedNuclei()) {
				List<Integer> f = new ArrayList<Integer>();
				f.add(tn.getFirstTimePoint() - correction);
				f.add(tn.getLastTimePoint() - correction);
				Map<String, List<Integer>> m = result.get(tn.getName());
				if (m == null) {
					m = new HashMap<String, List<Integer>>();
					result.put(tn.getName(), m);
				}
				m.put(e.getKey(), f);
			}
		}
		return result;
	}

	private Map<String, Map<String, List<Float>>> getCellMovements(
			Map<String, LineageMeasurement> lms, boolean useNetMovement,
			Map<LineageMeasurement, Integer> corrections) {
		Map<LineageMeasurement, Map<String, TrackedNucleus>> nameIndex = new HashMap<LineageMeasurement, Map<String, TrackedNucleus>>();
		Set<String> nuclei = new HashSet<String>();
		for (LineageMeasurement lm : lms.values()) {
			Map<String, TrackedNucleus> nameIndex2 = new HashMap<String, TrackedNucleus>();
			for (TrackedNucleus tn : lm.lineage.getAllChildrenBelowNode()) {
				if (getValidTimeOfExistence(tn,startT) > 1) {
					nuclei.add(tn.getName());
					nameIndex2.put(tn.getName(), tn);
				}
			}
			nameIndex.put(lm, nameIndex2);
		}

		Map<String, Map<String, List<Float>>> result = new HashMap<String, Map<String, List<Float>>>();
		for (String key : nuclei) {
			Map<String, List<Float>> sub = new HashMap<String, List<Float>>();
			result.put(key, sub);
			for (Entry<String, LineageMeasurement> e : lms.entrySet()) {

				LineageMeasurement lm = e.getValue();
				float[] measures = embryoSizes.get(lm);
				
				//float APsize = measures[1] - measures[0];
				Integer c = corrections.get(lm);
				TrackedNucleus tn = nameIndex.get(lm).get(key);
				// lm.retrieveTrackedNucleusByName(key);
				if (tn != null && getValidTimeOfExistence(tn,startT) > 1) {
					Integer timepassed = getValidTimeOfExistence(tn,startT);
					Float m = null;
					if (useNetMovement)
						m = tn.getNetMovement(startT);
					else
						m = tn.getTotalMovement(startT);

					List<Float> out = new ArrayList<Float>();
					out.add((float) tn.getFirstTimePoint() - c);
					out.add(timepassed.floatValue());
					out.add(m );
					out.add(m / timepassed.floatValue());
					sub.put(e.getKey(), out);
				}
			}
		}
		return result;
	}

	public static int getValidTimeOfExistence(TrackedNucleus tn,int startBuffer) {
		return (tn.getLastTimePoint() - tn.getFirstTimePoint() - startBuffer+ 1);
	}

	public static int getCorrection(LineageMeasurement lm) {
		TrackedNucleus ABa = lm.retrieveTrackedNucleusByName("ABa");
		TrackedNucleus ABp = lm.retrieveTrackedNucleusByName("ABp");
		if (ABa != null && ABp != null) {

			return Math.min(ABa.getLastTimePoint(), ABp.getLastTimePoint());
		}
		return 1;
	}

	public static Map<LineageMeasurement, Integer> getCorrections(
			Collection<LineageMeasurement> lms) {
		Map<LineageMeasurement, Integer> corrections = new HashMap<LineageMeasurement, Integer>();
		for (LineageMeasurement e : lms) {
			corrections.put(e, getCorrection(e));
		}
		return corrections;
	}

	private Map<LineageMeasurement, int[]> getStartAndEnd(
			Collection<LineageMeasurement> lms, int round) {

		Map<LineageMeasurement, int[]> corrections = new HashMap<LineageMeasurement, int[]>();
		for (LineageMeasurement e : lms) {
			List<Integer> starts = new ArrayList<Integer>();
			List<Integer> ends = new ArrayList<Integer>();

			String[] daughterList = getDaughterAdditions(round, baseCells);
			for (String name : daughterList) {
				TrackedNucleus cellTN = e.retrieveTrackedNucleusByName(name);
				if (cellTN != null) {
					starts.add(cellTN.getFirstTimePoint());
					ends.add(cellTN.getLastTimePoint());
				}
			}

			int[] bounds = { (int) MathEMC.median(starts) + 3,
					(int) MathEMC.median(ends) - 3 };
			corrections.put(e, bounds);

		}

		return corrections;
	}

	private String[] getDaughterAdditions(int round, String[] mothers) {
		if (round == 0) {
			String[] result = mothers.clone();
			return result;
		} else {
			String[] result = new String[2 * mothers.length];
			int index = 0;
			for (String mother : mothers) {
				result[index++] = mother + "a";
				result[index++] = mother + "p";
			}
			return getDaughterAdditions(round - 1, result);
		}
	}

	public static Map<String, LineageMeasurement> getLineagesDir(String path,String outDir) {
		Map<String, LineageMeasurement> lms = new HashMap<String, LineageMeasurement>();
		File dir = new File(path);
		if (dir.exists() && dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.isDirectory() && !file.getName().equals(outDir)) {
					Map<String, LineageMeasurement> map = getLineages(file);
					int i = 1;
					for (Entry<String, LineageMeasurement> e : map.entrySet()) {
						String name = file.getName() + "-" + i++;
						lms.put(name, e.getValue());
					}
				}
			}
		}
		return lms;
	}

	public static Map<String, LineageMeasurement> getLineages(File dir) {
		Map<String, LineageMeasurement> lms = new HashMap<String, LineageMeasurement>();

		if (dir.exists() && dir.isDirectory()) {
			for (String file : dir.list()) {
				if (file.endsWith("zip")) {
					LineageMeasurement lm;
					try {
						float xyRes = 0.1f;
						Matcher m = p.matcher(file);
						if (m.matches()) {
							xyRes = Float.parseFloat(m.group(1));
							System.out.println(file + "\t" + xyRes);
						}
						lm = StarryNightFormatIO
								.readNuclei(dir.getAbsolutePath() + s + file,
										xyRes, 1f, 1f);
						file = file.substring(0, file.length() - 4);
						lm.setName(file);
						lms.put(file, lm);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
		return lms;
	}

	public static Map<String, LineageMeasurement> getLineages(String path) {
		File dir = new File(path);
		return getLineages(dir);
	}

	private int getOverlappingTime(TrackedNucleus tn1, TrackedNucleus tn2) {
		if (tn1.getFirstTimePoint() > tn2.getFirstTimePoint()) {
			TrackedNucleus temp = tn2;
			tn2 = tn1;
			tn1 = temp;
		}
		if ((tn2.getFirstTimePoint() + startT) <= tn1.getLastTimePoint()) {
			if (tn2.getLastTimePoint() <= tn1.getLastTimePoint()) {
				return tn2.getLastTimePoint() - tn2.getFirstTimePoint()
						- startT + 1;
			} else {
				return tn1.getLastTimePoint() - tn2.getFirstTimePoint()
						- startT + 1;
			}
		}
		return 0;
	}

	private List<String> getNucleiNames(Map<String, LineageMeasurement> lms,
			Map<LineageMeasurement, int[]> bounds, int s2) {
		Set<String> names = new HashSet<String>();
		for (LineageMeasurement lm : lms.values()) {
			int[] a = bounds.get(lm);
			if (a != null) {
				List<Nucleus> n = lm.getNucleiForATimepoint(a[s2]);
				for (Nucleus nuc : n) {
					TrackedNucleus tn = lm.getTrackedNucleusForNucleus(nuc);
					if (!(tn.getName().contains("olar")
							|| tn.getName().contains("ucleus") || tn.getName()
							.contains("ill")))
						names.add(tn.getName());

				}
			}
		}
		List<String> nucNames = new ArrayList<String>(names);
		Collections.sort(nucNames);
		return nucNames;
	}

	
	private float[] getDistanceMatrix(LineageMeasurement lm, int value,
			List<String> names) {
		int n = names.size();
		float[] result = new float[(n * n - n) / 2];
		int index1 = 0;
		List<Nucleus> nuc = lm.getNucleiForATimepoint(value);
		int[] indices = new int[n];
		for (int current = 0; current < n; current++) {
			String name = names.get(current);
			int index = -1;
			int i = 0;
			while (index < 0 && i < nuc.size()) {
				if (nuc.get(i).getName().equals(name)) {
					index = i;
				}
				i++;
			}
			indices[current] = index;
		}

		for (int i = 0; i < n; i++) {
			if (indices[i] >= 0) {
				int base = (int) (i * (n - (float) (i + 1) / 2f));
				Nucleus first = nuc.get(indices[i]);// correct(nuc.get(indices[i]),
													// lm);
				for (int j = i + 1; j < n; j++) {
					if (indices[j] >= 0) {
						int index = base + j - i - 1;
						Nucleus second = nuc.get(indices[j]);// correct(nuc.get(indices[j]),
																// lm);
						result[index] = second.distanceTo(first);
					}
				}
			}
		}
		return result;
	}

	private Nucleus correct(Nucleus n, LineageMeasurement lm) {
		float[] measures = embryoSizes.get(lm);
		float lengthAP = measures[1] - measures[0];
		float lengthDV = measures[3] - measures[2];
		float lengthLR = measures[5] - measures[4];
		Nucleus n2 = new Nucleus(n);
		n2.coordinates.divide(lengthAP);
		// n2.coordinates.set(1, (n2.coordinates.get(1)) / lengthAP);
		// n2.coordinates.set(2, (n2.coordinates.get(2)) / lengthAP);
		// n2.coordinates.set(0, (n2.coordinates.get(0) - measures[0]) /
		// lengthAP);
		// n2.coordinates.set(1, (n2.coordinates.get(1) - measures[2]) /
		// lengthDV);
		// n2.coordinates.set(2, (n2.coordinates.get(2) - measures[4]) /
		// lengthLR);
		return n2;
	}
	private void OLDrun() {
		Map<String, LineageMeasurement> wt = getLineages(baseDirectory
				+ wildTypes);
		Map<String, LineageMeasurement> pheno = getLineages(baseDirectory
				+ phenoTypes);
		List<String> sequence = new ArrayList<String>(wt.keySet());
		sequence.addAll(pheno.keySet());
		Map<LineageMeasurement, Integer> corrections = getCorrections(wt
				.values());
		corrections.putAll(getCorrections(pheno.values()));
		Map<String, LineageMeasurement> all = new HashMap<String, LineageMeasurement>(
				wt);
		all.putAll(pheno);
		String baseName = baseDirectory + out + s + phenoTypes;
		if (doPositions) {

		}
		if (doMovements) {
			Map<String, Map<String, List<Float>>> netmov = getCellMovements(
					all, true, corrections);
			Map<String, Map<String, List<Float>>> totalmov = getCellMovements(
					all, false, corrections);
			String header = "cell";
			List<String> headers = getCellMovementMiniHeader();
			for (String h : sequence) {
				for (String head : headers) {
					header += "\t" + h + "_" + head;
				}
			}
			toFile(baseName + "_movementNet.txt", netmov, header, sequence);
			toFile(baseName + "_movementTotal.txt", totalmov, header, sequence);
		}
		if (doCellDistances) {
			Map<String, Map<String, List<Float>>> avdis = getAverageDistances(all);
			String header = "cell1\tcell2";
			header += "\t" + StringUtilities.join(sequence, "\t");
			toFile(baseName + "avDis.txt", avdis, header, sequence);
		}
		if (doDistancesPerTime) {
			String outDir = baseDirectory + out + s + phenoTypes
					+ "_VariancesPerTime" + s;
			File f = new File(outDir);
			if (!f.exists()) {
				f.mkdir();
			}
			getVariancePerTimePoint(all, sequence, outDir);
		}
		if (doAngles) {
			String header = "cell1\tcell2";
			for (String file : sequence) {
				header += "\tT_" + file + "\tmean_" + file + "\tAP_" + file
						+ "\tDV_" + file + "\tLR_" + file;
			}
			Map<String, Map<String, List<Float>>> angles = getAngles(all,
					wt.keySet());
			toFile(baseName + "angles.txt", angles, header, sequence);
		}

	}

}
