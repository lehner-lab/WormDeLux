package org.kuleuven.lineager;

import ij.ImagePlus;
import ij.process.ColorProcessor;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.kuleuven.lineagetree.Lineage2StatisticsScript;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.lineagetree.StarryNightFormatIO;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.math.MathEMC;
import org.kuleuven.math.matrix.Matrix;
import org.kuleuven.math.vector.Vector;
import org.kuleuven.utilities.ImageUtilities;
import org.kuleuven.utilities.TextFileUtilities;

public class Slicer {
	String lineage = "/Users/rjelier/Projects/CE_lineaging/Lineages/wt/wt-100809.zip";
	String output = "/Users/rjelier/Projects/CE_lineaging/compressionMovementsModelling/voronoid/";
	double APthreshold = 1;
	int pixels = 1000;
	public static Color[] colors = { Color.red, Color.blue, Color.cyan, Color.magenta,
			Color.orange, Color.pink, Color.white, Color.yellow, Color.green, Color.lightGray,
			new Color(128, 157, 242), new Color(128, 0, 255), new Color(148, 0, 211),
			new Color(160, 82, 45), new Color(85, 107, 47), new Color(178, 34, 34),
			new Color(238, 203, 173), new Color(204, 255, 10), Color.DARK_GRAY };
	private static String[] colorNames = { "red", "blue", "cyan", "magenta", "orange", "pink",
			"white", "yellow", "green", "lightGray", "lightBlue", "darkPurple", "lightPurple",
			"brown", "darkGreen", "stoneRed", "salmon", "yellowgreen", "darkGray" };

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Slicer().run();
	}

	public static ImageProcessor getVoronoidSlice(List<Nucleus> nuclei, int width, int height,
			List<Color> colorMap) {
		if (colorMap.size() < nuclei.size()) {
			for (int i = colorMap.size() - 1; i < nuclei.size(); i++) {
				colorMap.add(colors[nuclei.size() % colors.length]);
			}
		}
		ArrayList<float[]> points = new ArrayList<float[]>();
		for (Nucleus n : nuclei) {
			float[] point = { (float) n.getY(), (float) n.getZ() };
			points.add(point);
		}
		FloatPolygon fp = QuickConvexHull.getQuickHullAsPolygon(points);
		fp = scale(fp, 1.1f);
		Rectangle rec = fp.getBounds();
		float s = (float) ((rec.getMaxX() - rec.getMinX()) / (width - 20f));
		float ox = (float) (rec.getMinX() - 5f * s);
		float oy = (float) (rec.getMinY() - 5f * s);
		
		ColorProcessor bp = new ColorProcessor(width, height);
		for (int x = 0; x < width; x++) {
			float xprime = x * s + ox;
			for (int y = 0; y < height; y++) {
				float yprime = y * s + oy;
				if (fp.contains(xprime, yprime)) {
					int min = 0;
					float distance = Float.MAX_VALUE;
					for (int i = 0; i < nuclei.size(); i++) {
						Nucleus n = nuclei.get(i);
						float dx = (float) (n.getY() - xprime);
						float dy = (float) (n.getZ() - yprime);
						float tempDis = (float) Math.sqrt(dx * dx + dy * dy);
						if (tempDis < distance) {
							min = i;
							distance = tempDis;
						}
					}
					Nucleus sel = nuclei.get(min);
					Color col = colorMap.get(min);
					int[] rgb = { col.getRed(), col.getGreen(), col.getBlue() };
					bp.putPixel(x, y, rgb);

				}
			}
		}
		return bp;
	}

	private void run() {
		String nucName = "ABalpppa";
		LineageMeasurement lm = null;
		Matrix<Integer, Integer> matrix = null;
		try {
			lm = StarryNightFormatIO.readNuclei(lineage, 0.1f, 1f, 1f);
			matrix = Lineage2StatisticsScript.getOrientLineageMatrix(lm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Set<TrackedNucleus> set = lm.retrieveTrackedNucleiByName(nucName);
		TrackedNucleus tn = set.iterator().next();
		ArrayList<float[]> points = new ArrayList<float[]>();
		for (int i = tn.getFirstTimePoint(); i <= tn.getLastTimePoint(); i++) {
			Nucleus ref = tn.getNucleusForTimePoint(i);
			Vector<Integer> refC = Lineage2StatisticsScript.adapt2(matrix, ref.getCoordinates());
			List<Nucleus> l = lm.getNucleiForATimepoint(i);
			for (Nucleus n : l) {
				Vector<Integer> adapted = Lineage2StatisticsScript.adapt2(matrix,
						n.getCoordinates());
				if (Math.abs(adapted.get(0) - refC.get(0)) < APthreshold) {
					float[] point = { (float) adapted.get(1), (float) adapted.get(2) };
					points.add(point);
				}
			}
		}
		FloatPolygon fp = QuickConvexHull.getQuickHullAsPolygon(points);
		fp = scale(fp, 1.1f);
		Rectangle rec = fp.getBounds();
		float s =  ((float)(rec.getMaxX() - rec.getMinX()) / (pixels - 20));
		float ox = (float) (rec.getMinX() - 5 * s);
		float oy = (float) (rec.getMinY() - 5 * s);
		ColorProcessor bp = new ColorProcessor(pixels, pixels);

		Map<String, Integer> colorMap = new HashMap<String, Integer>();
		// Nucleus ref = tn.getNucleusForTimePoint(tn.getFirstTimePoint()+1);

		for (int time = tn.getFirstTimePoint() + 1; time <= tn.getLastTimePoint(); time++) {
			Nucleus ref = tn.getNucleusForTimePoint(time);
			Nucleus refC = new Nucleus(ref);
			Vector<Integer> ada = Lineage2StatisticsScript.adapt2(matrix, refC.getCoordinates());
			refC.setCoordinates(ada);

			List<Nucleus> l = lm.getNucleiForATimepoint(time);

			List<Nucleus> selected = new ArrayList<Nucleus>();

			for (Nucleus n : l) {
				Nucleus a = new Nucleus(n);
				Vector<Integer> adapted = Lineage2StatisticsScript.adapt2(matrix,
						a.getCoordinates());
				a.setCoordinates(adapted);

				if (Math.abs(a.getCoordinates().get(0) - refC.getCoordinates().get(0)) < 3.5) {
					selected.add(a);
					System.out
							.println(time + "\t" + (selected.size() - 1) + "\t" + a.getName());
				}
			}
			for (int x = 0; x < pixels; x++) {
				float xprime = x * s + ox;
				for (int y = 0; y < pixels; y++) {
					float yprime = y * s + oy;
					if (fp.contains(xprime, yprime)) {
						int min = 0;
						float distance = Float.MAX_VALUE;
						for (int i = 0; i < selected.size(); i++) {
							Nucleus n = selected.get(i);
							float dx = (float) (n.getY() - xprime);
							float dy = (float) (n.getZ() - yprime);
							float tempDis = (float) Math.sqrt(dx * dx + dy * dy);
							if (tempDis < distance) {
								min = i;
								distance = tempDis;
							}
						}
						Nucleus sel = selected.get(min);
						Integer c = colorMap.get(sel.getName());
						if (c == null) {
							c = colorMap.size() % colors.length;
							colorMap.put(sel.getName(), c);
						}
						Color col = colors[c];
						int[] rgb = { col.getRed(), col.getGreen(), col.getBlue() };
						bp.putPixel(x, y, rgb);

					}
				}
			}
			ImageUtilities.saveAsTif(new ImagePlus("", bp),
					output + nucName + "_" + (time - tn.getFirstTimePoint()) + ".tif");
		}
		List<String> lines = new ArrayList<String>();
		for (Entry<String, Integer> e : colorMap.entrySet()) {
			String line = e.getKey() + "\t" + colorNames[e.getValue()];
			lines.add(line);
		}
		TextFileUtilities.saveToFile(lines, output + "colors_" + nucName + ".txt");
	}

	public static FloatPolygon scale(FloatPolygon fp, float f) {
		float xmean = MathEMC.mean(fp.xpoints);
		float ymean = MathEMC.mean(fp.ypoints);
		float[] xpoints = new float[fp.npoints];
		float[] ypoints = new float[fp.npoints];

		for (int i = 0; i < fp.npoints; i++) {
			xpoints[i] = f * (fp.xpoints[i] - xmean) + xmean;
			ypoints[i] = f * (fp.ypoints[i] - ymean) + ymean;
		}

		return new FloatPolygon(xpoints, ypoints);
	}

	public static List<ImageProcessor> getVoronoidSlices(List<List<Nucleus>> nuclei, int width,
			int height, Map<String, Color> colorMap) {
		ArrayList<float[]> points = new ArrayList<float[]>();
		for (List<Nucleus> nucPT : nuclei) {
			for (Nucleus n : nucPT) {
				float[] point = { (float) n.getY(), (float) n.getZ() };
				points.add(point);
			}
		}
		FloatPolygon fp = QuickConvexHull.getQuickHullAsPolygon(points);
		System.out.println(points.size() + "\t"+fp.npoints);
		for(int i=0;i<fp.npoints;i++){
			System.out.println(fp.xpoints[i]+"\t" + fp.ypoints[i]);
		}
		fp = scale(fp, 1.1f);
		Rectangle2D.Double rec = fp.getFloatBounds();
		float s =  ((float)(rec.getMaxX() - rec.getMinX()) / (width - 20));
		float ox =  ((float)rec.getMinX());// - 5 * s
		float oy =  ((float)rec.getMinY());
		System.out.println(s+ "\t"+ox+ "\t"+oy+ "\t"+rec);
		List<ImageProcessor> result = new ArrayList<ImageProcessor>();
		for (List<Nucleus> selected : nuclei) {
			ColorProcessor bp = new ColorProcessor(width, height);
			for (int x = 0; x < width; x++) {
				float xprime = x * s + ox;
				for (int y = 0; y < height; y++) {
					float yprime = y * s + oy;
					if (fp.contains(xprime, yprime)) {
						int min = 0;
						float distance = Float.MAX_VALUE;
						for (int i = 0; i < selected.size(); i++) {
							Nucleus n = selected.get(i);
							float dx = (float) (n.getY() - xprime);
							float dy = (float) (n.getZ() - yprime);
							float tempDis = (float) Math.sqrt(dx * dx + dy * dy);
							if (tempDis < distance) {
								min = i;
								distance = tempDis;
							}
						}
						Nucleus sel = selected.get(min);
						Color col = colorMap.get(sel.getName());
						if (col == null) {
							Integer c = selected.indexOf(sel) % colors.length;
							col = colors[c];
							colorMap.put(sel.getName(), col);
						}

						int[] rgb = { col.getRed(), col.getGreen(), col.getBlue() };
						bp.putPixel(x, y, rgb);

					}
				}
			}
			result.add(bp);
		}
		return result;

	}
}
