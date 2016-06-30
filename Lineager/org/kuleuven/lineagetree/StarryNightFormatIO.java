package org.kuleuven.lineagetree;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.kuleuven.collections.ComparatorFactory;
import org.kuleuven.collections.SortedList;
import org.kuleuven.collections.SortedListMap;
import org.kuleuven.utilities.ProcessUtilities;
import org.kuleuven.utilities.StringUtilities;
import org.kuleuven.utilities.TextFileUtilities;

public class StarryNightFormatIO {

	private static Pattern p = Pattern.compile("(?<!._)t(\\d+)-nuclei$");
	private static String separator = "\\s*,\\s*";
	public static float radiusFactor = 1f;
	/**
	 * index on columns
	 */
	@SuppressWarnings("unused")
	private final static int INDEX = 0, X = 5, Y = 6, Z = 7, IDENTITY = 9, SIZE = 8, WT = 10,
			STATUS = 1, PRED = 2, SUCC1 = 3, SUCC2 = 4, RWT = 11, RSUM = 12, RCOUNT = 13,
			ASSIGNEDID = 14;

	private static Map<Integer, List<String>> readDirectoryAndFiles(String dirPath)
			throws Exception {
		Map<Integer, List<String>> result = new TreeMap<Integer, List<String>>();
		File dir = new File(dirPath);

		String[] list = null;
		if (dir.exists()) {
			if (dir.isDirectory()) {

				list = dir.list();
			} else if (p.matcher(dir.getName()).find()) {
				File parent = dir.getParentFile();
				if(parent.isDirectory()){
					dir=parent;
					list=parent.list();
				}
			}
			else {
				throw new Exception("Tried to load lineage from " + dirPath + " but failed");
			}
		}
		for (int i = 0; list != null && i < list.length; i++) {
			Matcher m = p.matcher(list[i]);
			String directorySeparator = ProcessUtilities.getDirectorySeparator();
			if (m.find()) {
				Integer id = Integer.parseInt(m.group(1));

				List<String> lines = TextFileUtilities.loadFromFile(dir.getAbsolutePath()
						+ directorySeparator + list[i]);
				result.put(id, lines);

			}
		}
		return result;
	}

	private static Map<Integer, List<String>> readNucleiZipFile(String file) {
		Map<Integer, List<String>> result = new TreeMap<Integer, List<String>>();
		try {
			ZipFile zf = new ZipFile(file);

			Enumeration<? extends ZipEntry> e = zf.entries();
			while (e.hasMoreElements()) {
				ZipEntry ze = e.nextElement();

				Matcher m = p.matcher(ze.getName());
			//	System.out.println(ze.getName());
				if (m.find()) {
					Integer id = Integer.parseInt(m.group(1));
					InputStream inputStream = zf.getInputStream(ze);
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					List<String> lines = new ArrayList<String>();
					String line = reader.readLine();
					while (line != null) {
						lines.add(line);
						line = reader.readLine();
					}
					result.put(id, lines);
					reader.close();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void writeLineage2StarryNiteFormat(LineageMeasurement lm, File directory) throws Exception {
		Map<Integer, SortedListMap<Integer, Nucleus>> nuclei = lm.getIndexedNuclei();
		int fieldcount = 0;

		for (Nucleus n : nuclei.values().iterator().next().values()) {
			String[] field = n.getAdditionalFields();
			if (field != null) {
				fieldcount = Math.max(fieldcount, field.length);
			}
		}

		Map<Integer, Map<Integer, String>> output = new HashMap<Integer, Map<Integer, String>>();
		for (TrackedNucleus tn : lm.getAllTrackedNuclei()) {
			Set<TrackedNucleus> parents = lm.lineage.getParents(tn);
			int predecessor = -1;
			if (parents != null && parents.size() > 0) {
				TrackedNucleus daddy = parents.iterator().next();
				if (daddy.nuclei.size() > 0) {
					Nucleus daddyNucleus = daddy.nuclei.get(daddy.nuclei.size() - 1);
					predecessor = daddyNucleus.getIndex();
				}
			}

			SortedList<Nucleus> nuclei2 = tn.getNuclei();
			int successor2 = -1;
			int start = 0;
			Nucleus current = nuclei2.get(start);

			for (int i = start + 1; i < nuclei2.size(); i++) {
				Nucleus next = nuclei2.get(i);
				int successor1 = next.getIndex();
				String line = makeStarryNiteLine(current, predecessor, successor1, successor2,
						lm.xyResolution, fieldcount);
				Map<Integer, String> lines = output.get(current.timePoint);
				if (lines == null) {
					lines = new HashMap<Integer, String>();
					output.put(current.timePoint, lines);
				}
				lines.put(current.getIndex(), line);
				predecessor = current.getIndex();
				current = next;
			}

			int successor1 = -1;
			System.out.println(tn);
			List<TrackedNucleus> children = lm.lineage.getNode(tn).getChildrenAsSortedList();
			if (children != null && children.size() > 0) {
				Nucleus successor = children.get(0).nuclei.get(0);
				successor1 = successor.getIndex();
			}
			if (children != null && children.size() > 1) {
				Nucleus successor = children.get(1).nuclei.get(0);
				successor2 = successor.getIndex();
			}
			String line = makeStarryNiteLine(current, predecessor, successor1, successor2,
					lm.xyResolution, fieldcount);
			Map<Integer, String> lines = output.get(current.timePoint);
			if (lines == null) {
				lines = new HashMap<Integer, String>();
				output.put(current.timePoint, lines);
			}
			lines.put(current.getIndex(), line);
		}

		String path = directory.getAbsolutePath() + File.separator;
		NumberFormat formatter = new DecimalFormat("000");

		for (Entry<Integer, Map<Integer, String>> e : output.entrySet()) {
			String s = formatter.format(e.getKey());
			String fileName = path + "t" + s + "-nuclei";
			List<Integer> keys = new ArrayList<Integer>(e.getValue().keySet());
			Collections.sort(keys);
			List<String> out = new ArrayList<String>();
			for (Integer id : keys) {
				out.add(e.getValue().get(id));
			}
			TextFileUtilities.saveToFileThrowsExceptioin(out, fileName);
		}

	}

	public static String makeStarryNiteLine(Nucleus n, int predecessor, int successor1,
			int successor2, float xyRes, int additionalFieldsNumber) {
		List<String> out = new ArrayList<String>();
		String[] additionalFields = n.getAdditionalFields();
		out.add(Integer.toString(n.getIndex()));
		out.add("1");
		out.add(Integer.toString(predecessor));
		out.add(Integer.toString(successor1));
		out.add(Integer.toString(successor2));
		out.add(Integer.toString((int) Math.round(n.getX() / xyRes)));
		out.add(Integer.toString((int) Math.round(n.getY() / xyRes)));
		out.add(Double.toString(n.getZ()));
		out.add(Integer.toString(Math.round(2 * n.getRadius() / xyRes)));
		out.add(n.getName());
		for (int i = 0; i < additionalFieldsNumber; i++) {
			if (additionalFields != null && additionalFields.length > i) {
				out.add(additionalFields[i]);
			} else {
				out.add("-1");
			}
		}
		return StringUtilities.join(out, ", ");
	}

	public static LineageMeasurement readNuclei(String directory, float xyresolution,
			float zresolution, float timeResolution) throws Exception {
		return readNuclei(directory, xyresolution, zresolution, timeResolution, 0,
				Integer.MAX_VALUE);
	}

	public static LineageMeasurement readNuclei(String directory, float xyresolution,
			float zresolution, float timeResolution, int startTime, int endTime) throws Exception {
		Map<Integer, List<String>> files = null;
		if (directory.endsWith(".zip")) {
			files = readNucleiZipFile(directory);
		} else {
			files = readDirectoryAndFiles(directory);
		}
		LineageMeasurement result = new LineageMeasurement();
		result.xyResolution = xyresolution;
		result.zResolution = zresolution;
		result.timeresolution = timeResolution;

		Map<Integer, SortedListMap<Integer, Nucleus>> nucleiOverTime = new HashMap<Integer, SortedListMap<Integer, Nucleus>>();
		List<NucleusTracking> tracks = new ArrayList<NucleusTracking>();
		List<Division> divisions = new ArrayList<Division>();

		for (Entry<Integer, List<String>> entry : files.entrySet()) {
			int timePoint = entry.getKey();
			if (timePoint >= startTime && timePoint <= endTime) {
				SortedListMap<Integer, Nucleus> nuclei = new SortedListMap<Integer, Nucleus>(
						ComparatorFactory.getAscendingIntegerComparator());

				List<String> lines = entry.getValue();
				for (int i = 0; i < lines.size(); i++) {
					String line = lines.get(i);
					line = line.trim();
					String[] cells = line.split(separator);
					if (cells.length <= IDENTITY) {
						System.out.println(directory + "\t" + entry.getKey() + "\t" + i + "\t"
								+ line);
					} else {
						float x = xyresolution * Float.parseFloat(cells[X]);
						float y = xyresolution * Float.parseFloat(cells[Y]);
						float z = zresolution * Float.parseFloat(cells[Z]);
						float radius = radiusFactor * 0.5f * xyresolution
								* Float.parseFloat(cells[SIZE]);
						String idString = cells[IDENTITY];
						int status = Integer.parseInt(cells[STATUS]);
						int index = Integer.parseInt(cells[INDEX]);
						if (idString != null && !idString.equalsIgnoreCase("")) {
							Nucleus n = new Nucleus(idString, index, status, x, y, z, radius, entry
									.getKey());
							if (cells.length > IDENTITY + 1) {
								String[] sel = new String[cells.length - IDENTITY - 1];
								System.arraycopy(cells, IDENTITY + 1, sel, 0, cells.length
										- IDENTITY - 1);
								n.setAdditionalFields(sel);
							}
							nuclei.put(index, n);
							// System.out.println(cells[SUCC2]);
							NucleusTracking nt = new NucleusTracking();
							nt.time1 = entry.getKey();
							nt.index1 = index;// nuclei.size() - 1
							if (entry.getKey() < endTime) {
								if (!cells[SUCC2].equalsIgnoreCase("-1")) {
									nt.index2 = -1;
									Division de = new Division();
									de.time1 = entry.getKey();
									de.index1 = index;
									de.index2a = Integer.parseInt(cells[SUCC1]);
									de.index2b = Integer.parseInt(cells[SUCC2]);
									divisions.add(de);
								} else {
									nt.index2 = Integer.parseInt(cells[SUCC1]);
								}
							}
							tracks.add(nt);
						}
						nucleiOverTime.put(entry.getKey(), nuclei);
					}

				}
			}
		}
		result.setNucleiOverTime(parseNucleiOverTime(nucleiOverTime));
		parseToLineage(nucleiOverTime, tracks, divisions, result);
		return result;
	}

	private static Map<Integer, Set<Nucleus>> parseNucleiOverTime(
			Map<Integer, SortedListMap<Integer, Nucleus>> nucleiOverTime) {
		Map<Integer, Set<Nucleus>> result = new SortedListMap<Integer, Set<Nucleus>>(
				ComparatorFactory.getAscendingIntegerComparator());
		for (Entry<Integer, SortedListMap<Integer, Nucleus>> e : nucleiOverTime.entrySet()) {
			result.put(e.getKey(), new HashSet<Nucleus>(e.getValue().values()));
		}
		return result;
	}

	private static void parseToLineage(
			Map<Integer, SortedListMap<Integer, Nucleus>> nucleiOverTime,
			List<NucleusTracking> tracks, List<Division> divisions, LineageMeasurement lineage) {
		Map<Nucleus, TrackedNucleus> result = new HashMap<Nucleus, TrackedNucleus>();

		for (NucleusTracking nt : tracks) {
			// System.out.println(nt.toString());
			Nucleus n1 = nucleiOverTime.get(nt.time1).get(nt.index1);
			TrackedNucleus tn1 = result.get(n1);
			if (tn1 == null) {
				tn1 = new TrackedNucleus();
				tn1.addNucleus(n1);
			}
			if ((nt.index2 >= 0)) {
				SortedListMap<Integer, Nucleus> Tmap = nucleiOverTime.get(nt.time1 + 1);
				if (Tmap != null && Tmap.containsKey(nt.index2)) {
					Nucleus n2 = Tmap.get(nt.index2);
					tn1.addNucleus(n2);
					TrackedNucleus tn2 = result.get(n2);
					if (tn2 != null) {
						tn1.mergeTrackedNucleus(tn2);
					}
				} else {
					System.out.println("nucleus " + nt.toString()
							+ " claims to exist in the next timepoint but does not appear to?");

				}
			}
			for (Nucleus n : tn1.nuclei) {
				result.put(n, tn1);
			}
		}

		for (Division de : divisions) {
			TrackedNucleus tnMum = result.get(nucleiOverTime.get(de.time1).get(de.index1));

			if (nucleiOverTime.containsKey(de.time1 + 1)) {

				Nucleus nDaughter1 = nucleiOverTime.get(de.time1 + 1).get(de.index2a);
				Nucleus nDaughter2 = nucleiOverTime.get(de.time1 + 1).get(de.index2b);
				if (tnMum == null || nDaughter1 == null || nDaughter2 == null) {
					System.out.println(tnMum.getName() + "\t " + de.time1 + "\t " + de.index1
							+ "\t " + de.index2a + "\t " + de.index2b);
				} else {
					TrackedNucleus tnDaughter1 = result.get(nDaughter1);
					TrackedNucleus tnDaughter2 = result.get(nDaughter2);

					lineage.lineage.addParentChildRelation(tnMum, tnDaughter1);
					lineage.lineage.addParentChildRelation(tnMum, tnDaughter2);
				}
			} else {
				System.out.println(tnMum.getName() + "\t " + de.time1 + "\t " + de.index1 + "\t "
						+ de.index2a + "\t " + de.index2b);
			}
		}

		Set<TrackedNucleus> tnsA = new HashSet<TrackedNucleus>(result.values());
		lineage.lineage.removeReduncancy();
		tnsA.removeAll(lineage.lineage.getAllChildrenBelowNode());

		for (TrackedNucleus tns : tnsA) {
			// System.out.println("orphan\t" + tns + " " + tns.nuclei.size() +
			// " " + tns.getFirstTimePoint());
			lineage.lineage.addParentChildRelation(lineage.lineage.getID(), tns);
		}
		lineage.lineage.removeReduncancy();
		lineage.nucleusMap = result;
		lineage.lineage.initializeIndices();
	}

	private static class NucleusTracking {
		int time1;
		int index1;
		int index2 = -1;

		@Override
		public String toString() {
			return "time: " + time1 + " index 1: " + index1 + " index 2 " + index2 + "\n";
		}
	}

	private static class Division {
		int time1;
		int index1;
		int index2a;
		int index2b;

		@Override
		public String toString() {

			return "time1: " + time1 + "\tindex1: " + index1 + "\tindex2a: " + index2a
					+ "\tindex2b: " + index2b + "\n";

		}
	}

}
