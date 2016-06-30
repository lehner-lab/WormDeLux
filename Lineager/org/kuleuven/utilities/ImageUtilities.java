package org.kuleuven.utilities;


import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.Wand;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.io.RoiDecoder;
import ij.io.RoiEncoder;
import ij.measure.Calibration;
import ij.measure.Measurements;
import ij.plugin.ContrastEnhancer;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.RankFilters;
import ij.process.ByteProcessor;
import ij.process.ByteStatistics;
import ij.process.ColorStatistics;
import ij.process.FloatProcessor;
import ij.process.FloatStatistics;
import ij.process.FloodFiller;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.process.PolygonFiller;
import ij.process.ShortProcessor;
import ij.process.ShortStatistics;
import ij.process.StackStatistics;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.kuleuven.collections.Pair;

public class ImageUtilities {
	public static ImagePlus openImage(String filename) {
		Opener o = new Opener();
		ImagePlus ipL = o.openImage(filename);
		return ipL;
	}

	public static void saveScreenShot(Rectangle screen, String filePath) {
		Robot robot = null;
		try {
			robot = new Robot();
			BufferedImage image = robot.createScreenCapture(screen);
			ImagePlus ip = new ImagePlus("", image);
			saveAsTif(ip, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Roi growRoi(Roi roi, ImagePlus image, int pixels) {
		Rectangle rec = roi.getBoundingRect();
		int factor = 5;
		int x = rec.x - factor;
		int y = rec.y - factor;
		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		int width = rec.x - x + rec.width + factor;
		int heigth = rec.y - y + rec.height + factor;
		if ((x + width) > image.getWidth())
			width = image.getWidth() - x;
		if ((y + heigth) > image.getHeight())
			width = image.getHeight() - y;
		return new Roi(x, y, width, heigth);

	}

	public static void substractMedianBackGround(ImagePlus ipL, double medianRadius) {
		ImagePlus duplicate = new ImagePlus("backup", ipL.getProcessor().duplicate());
		RankFilters rf = new RankFilters();
		rf.rank(duplicate.getProcessor(), medianRadius, RankFilters.MEDIAN);
		// duplicate.getProcessor().multiply(0.9);
		ImageCalculator ic = new ImageCalculator();
		ic.calculate("Subtract", ipL, duplicate);

	}
	
	
	public static int[] getQuantiles(ImageProcessor ip, float[] quantiles) {
		int[] hist = ip.getHistogram();
		ImageStatistics stats = ImageStatistics.getStatistics(ip, Measurements.AREA
				| Measurements.MEAN, null);
		int[] result=new int[quantiles.length]; 
		for(int i=0;i<quantiles.length;i++){
			float quantile=quantiles[i];
			double sum = 0;
			double halfCount = stats.pixelCount * quantile;
			int j = 0;
			do {
				sum += hist[j++];
			} while (sum <= halfCount && j < hist.length);
			result[i]=j;
		}
		return result;
	}

	public static int[] getStackQuantiles(ImagePlus ip, float[] quantiles) {
		
		ImageStatistics stats = new StackStatistics(ip);
		long[] hist= stats.getHistogram();
//		ImageStatistics stats = ImageStatistics.getStatistics(ip, ImageStatistics.AREA| ImageStatistics.MEAN, null);
		int[] result=new int[quantiles.length]; 
		for(int i=0;i<quantiles.length;i++){
			float quantile=quantiles[i];
			double sum = 0;
			double halfCount = stats.pixelCount * quantile;
			int j = 0;
			do {
				sum += hist[j++];
			} while (sum <= halfCount && j < hist.length);
			result[i]=j;
		}
		return result;
	}

	
	
	public static double getQuantile(Roi roi, ImageProcessor ip, float quantile) {
		Rectangle backup = ip.getRoi();
		ImageProcessor mask = ip.getMask();
		ip.resetRoi();

		if (roi != null) {
			ip.setRoi(roi);
			ip.setMask(roi.getMask());
		}
		int[] hist = ip.getHistogram();
		double sum = 0;
		ImageStatistics stats = ImageStatistics.getStatistics(ip, Measurements.AREA
				| Measurements.MEAN, null);
		double halfCount = stats.pixelCount * quantile;
		int i = 0;
		do {
			sum += hist[i++];
		} while (sum <= halfCount && i < hist.length);

		ip.resetRoi();
		ip.setRoi(backup);
		ip.setMask(mask);

		return i;
	}

	public static ImageStatistics getImageStatistics(ImagePlus ip) {
		return ImageStatistics.getStatistics(ip.getProcessor(), Measurements.MEAN
				| Measurements.MEDIAN | Measurements.AREA | Measurements.CIRCULARITY
				| Measurements.MIN_MAX | Measurements.PERIMETER | Measurements.STD_DEV, ip
				.getCalibration());
	}

	public static Roi getRoiFromFile(String filename) {
		RoiDecoder roidec = new RoiDecoder(filename);
		try {
			return roidec.getRoi();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static boolean saveRoiToFile(Roi roi, String path) {
		RoiEncoder roie = new RoiEncoder(path);
		try {
			roie.write(roi);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void saveRoiListToZip(List<? extends Roi> rois, String path) {
		try {
			ZipOutputStream zio = new ZipOutputStream(new FileOutputStream(path));
			for (Integer i = 0; i < rois.size(); i++) {
				Roi roi = rois.get(i);
				ZipEntry ze = new ZipEntry(i.toString() + ".roi");
				zio.putNextEntry(ze);
				RoiEncoder re = new RoiEncoder(zio);
				re.write(roi);
			}
			zio.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<Roi> getRoiListFromZip(String filename) {
		ZipFile zf;
		List<Roi> rois = new ArrayList<Roi>();
		try {
			zf = new ZipFile(filename);
			Enumeration<? extends ZipEntry> e = zf.entries();
			while (e.hasMoreElements()) {
				ZipEntry ze = e.nextElement();
				InputStream inputStream = zf.getInputStream(ze);
				int size = (int) ze.getSize();
				byte[] data = new byte[size];
				int total = 0;
				while (total < size)
					total += inputStream.read(data, total, size - total);
				RoiDecoder roi = new RoiDecoder(data, ze.getName());
				inputStream.close();
				rois.add(roi.getRoi());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return rois;
	}

	static final int BYTE = 0, SHORT = 1, FLOAT = 2, RGB = 3;

	private static int getImageType(ImageProcessor ip) {
		if (ip instanceof ShortProcessor)
			return SHORT;
		else if (ip instanceof FloatProcessor)
			return FLOAT;
		else
			return BYTE;
	}

	boolean setThresholdLevels(ImagePlus imp, ImageProcessor ip) {
		int imageType = getImageType(ip);
		int imageType2;
		double level1, level2, fillColor;
		double t1 = ip.getMinThreshold();
		double t2 = ip.getMaxThreshold();
		boolean invertedLut = imp.isInvertedLut();
		boolean byteImage = ip instanceof ByteProcessor;

		if (t1 == ImageProcessor.NO_THRESHOLD) {
			ImageStatistics stats = imp.getStatistics();
			if (imageType != BYTE
					|| (stats.histogram[0] + stats.histogram[255] != stats.pixelCount)) {
				IJ.error("Particle Analyzer", "A thresholded image or 8-bit binary image is\n"
						+ "required. Threshold levels can be set using\n"
						+ "the Image->Adjust->Threshold tool.");
				return false;
			}
			if (invertedLut) {
				level1 = 255;
				level2 = 255;
				fillColor = 64;
			} else {
				level1 = 0;
				level2 = 0;
				fillColor = 192;
			}
		} else {
			level1 = t1;
			level2 = t2;
			if (imageType == BYTE) {
				if (level1 > 0)
					fillColor = 0;
				else if (level2 < 255)
					fillColor = 255;
			} else if (imageType == SHORT) {
				if (level1 > 0)
					fillColor = 0;
				else if (level2 < 65535)
					fillColor = 65535;
			} else if (imageType == FLOAT)
				fillColor = -Float.MAX_VALUE;
			else
				return false;
		}
		imageType2 = imageType;
		return true;
	}

	public static void saveAsTif(ImagePlus ipL, String path) {
		FileSaver fs = new FileSaver(ipL);
		fs.saveAsTiff(path);
	}

	public static List<PolygonRoi> findParticles(ImagePlus imp, double minCircularity,
			double maxCircularity, int minSize, int maxSize, boolean excludeEdgeParticles) {

		ImageProcessor ip = imp.getProcessor();
		ip.snapshot();
		List<Double> levels = getThresholdLevels(imp);
		if (levels == null)
			return null;

		Roi saveRoi = imp.getRoi();
		Polygon polygon = null;
		if (saveRoi != null && saveRoi.getType() != Roi.RECTANGLE && saveRoi.isArea())
			polygon = saveRoi.getPolygon();

		byte[] pixels = null;
		if (ip instanceof ByteProcessor)
			pixels = (byte[]) ip.getPixels();
		Rectangle r = ip.getRoi();
		ImageProcessor mask = ip.getMask();

		if (r.width < ip.getWidth() || r.height < ip.getHeight() || mask != null) {
			if (!eraseOutsideRoi(ip, r, mask, excludeEdgeParticles, polygon, levels))
				return null;
		}
		int offset;
		double value;
		int imagetype = getImageType(ip);

		List<PolygonRoi> result = new ArrayList<PolygonRoi>();
		for (int y = r.y; y < (r.y + r.height); y++) {
			offset = y * ip.getWidth();
			for (int x = r.x; x < (r.x + r.width); x++) {
				if (pixels != null)
					value = pixels[offset + x] & 255;
				else if (imagetype == SHORT)
					value = ip.getPixel(x, y);
				else
					value = ip.getPixelValue(x, y);

				if (value >= levels.get(0) && value <= levels.get(1)) {
					PolygonRoi roi = getParticle(x, y, ip, levels);
					imp.setRoi(saveRoi);
					if (checkParticle(imp, roi, excludeEdgeParticles, minCircularity,
							maxCircularity, minSize, maxSize)) {
						result.add(roi);
					}
					ip.setRoi(roi);
					ip.setValue(levels.get(2));
					ip.fill(roi.getMask());

				}
			}

		}

		imp.killRoi();
		ip.resetRoi();
		ip.reset();

		return result;

	}

	private static boolean checkParticle(ImagePlus ipL, Roi roi, boolean excludeEdgeParticles,
			double minCircularity, double maxCircularity, int minSize, int maxSize) {

		boolean include = true;
		if (excludeEdgeParticles) {
			if (ROItouchesEdge(ipL, roi))
				include = false;
		}
		if (include) {
			if ((minCircularity > 0.0 || maxCircularity < 1.0)) {
				double circularity = getCirculariy(ipL, roi);
				if (circularity < minCircularity || circularity > maxCircularity)
					include = false;
			}
		}
		if (include) {
			Roi save = ipL.getRoi();
			ipL.setRoi(roi);
			ImageStatistics stats = getImageStatistics(ipL);
			ipL.setRoi(save);
			if (stats.pixelCount < minSize || stats.pixelCount > maxSize) {
				include = false;
			}
		}
		return include;
	}

	static boolean eraseOutsideRoi(ImageProcessor ip, Rectangle r, ImageProcessor mask,
			boolean excludeEdgeParticles, Polygon polygon, List<Double> levels) {
		int width = ip.getWidth();
		int height = ip.getHeight();
		double fillColor = levels.get(2);
		double level1 = levels.get(0);
		double level2 = levels.get(1);
		int imageType = getImageType(ip);
		ip.setRoi(r);
		int minX = r.x;
		int maxX = r.x + r.width;
		int minY = r.y;
		int maxY = r.y + r.height;
		if (excludeEdgeParticles && polygon != null) {
			ImageStatistics stats = ImageStatistics
					.getStatistics(ip, Measurements.MIN_MAX, null);
			if (fillColor >= stats.min && fillColor <= stats.max) {
				double replaceColor = level1 - 1.0;
				if (replaceColor < 0.0 || replaceColor == fillColor) {
					replaceColor = level2 + 1.0;
					int maxColor = imageType == BYTE ? 255 : 65535;
					if (replaceColor > maxColor || replaceColor == fillColor) {
						IJ.error("Particle Analyzer", "Unable to remove edge particles");
						return false;
					}
				}
				for (int y = minY; y < maxY; y++) {
					for (int x = minX; x < maxX; x++) {
						int v = ip.getPixel(x, y);
						if (v == fillColor)
							ip.putPixel(x, y, (int) replaceColor);
					}
				}
			}
		}
		ip.setValue(fillColor);
		if (mask != null) {
			mask = mask.duplicate();
			mask.invert();
			ip.fill(mask);
		}
		ip.setRoi(0, 0, r.x, height);
		ip.fill();
		ip.setRoi(r.x, 0, r.width, r.y);
		ip.fill();
		ip.setRoi(r.x, r.y + r.height, r.width, height - (r.y + r.height));
		ip.fill();
		ip.setRoi(r.x + r.width, 0, width - (r.x + r.width), height);
		ip.fill();
		ip.resetRoi();
		// IJ.log("erase: "+fillColor+" "+level1+" "+level2+"
		// "+excludeEdgeParticles);
		// (new ImagePlus("ip2", ip.duplicate())).show();
		return true;
	}

	/**
	 * This function checks if a given ROI touches (crosses should not be
	 * possible beyond image ROI bounding box) the border of either the given
	 * image or of the set ROI in the given image.
	 * 
	 * @param ipl
	 * @param roi
	 * @return
	 */
	public static boolean ROItouchesEdge(ImagePlus ipl, Roi roi) {
		Roi refRoi = ipl.getRoi();
		Polygon polygon = null;
		if (refRoi != null && refRoi.getType() != Roi.RECTANGLE && refRoi.isArea())
			polygon = refRoi.getPolygon();
		Rectangle rec;
		int minX = 0;
		int minY = 0;
		int maxX = ipl.getWidth();
		int maxY = ipl.getHeight();
		if (refRoi != null) {
			rec = refRoi.getBounds();
			minX = rec.x;
			maxX = rec.x + rec.width;
			minY = rec.y;
			maxY = rec.y + rec.height;
		}
		Rectangle r = roi.getBounds();
		if (r.x == minX || r.y == minY || r.x + r.width == maxX || r.y + r.height == maxY)
			return true;
		if (polygon != null) {
			Polygon p2 = roi.getPolygon();
			if (p2 != null) {
				PathIterator pi = p2.getPathIterator(new AffineTransform());
				while (!pi.isDone()) {
					double[] coordinates = new double[6];
					int type = pi.currentSegment(coordinates);
					if (type != PathIterator.SEG_CLOSE) {
						if (!polygon.contains(coordinates[0], coordinates[1]))
							return true;
						if (type == PathIterator.SEG_QUADTO) {
							if (!polygon.contains(coordinates[2], coordinates[3]))
								return true;
						} else if (type == PathIterator.SEG_CUBICTO) {
							if (!polygon.contains(coordinates[2], coordinates[3])
									|| !polygon.contains(coordinates[4], coordinates[5]))
								return true;
						}
					}
					pi.next();
				}
			}
		}
		return false;
	}

	public static double getCirculariy(ImagePlus ip, Roi roi) {
		double perimeter = roi.getLength();
		Roi save = ip.getRoi();
		ip.setRoi(roi);
		ImageStatistics stats = getImageStatistics(ip);
		double circularity = perimeter == 0.0 ? 0.0 : 4.0 * Math.PI
				* (stats.pixelCount / (perimeter * perimeter));
		if (circularity > 1d)
			circularity = 1d;
		ip.setRoi(save);
		return circularity;
	}

	private static List<Double> getThresholdLevels(ImagePlus imp) {
		ImageProcessor ip = imp.getProcessor();
		double t1 = ip.getMinThreshold();
		double t2 = ip.getMaxThreshold();
		int imageType = getImageType(ip);
		List<Double> result = new ArrayList<Double>();
		if (t1 == ImageProcessor.NO_THRESHOLD) {
			ImageStatistics stats = imp.getStatistics();
			if (imageType != BYTE
					|| (stats.histogram[0] + stats.histogram[255] != stats.pixelCount)) {
				IJ.error("Particle Analyzer", "A thresholded image or 8-bit binary image is\n"
						+ "required. Threshold levels can be set using\n"
						+ "the Image->Adjust->Threshold tool.");
				return null;
			}
			if (imp.isInvertedLut()) {
				result.add(255d);
				result.add(255d);
				result.add(64d);
			} else {
				result.add(0d);
				result.add(0d);
				result.add(192d);
			}
		} else {
			result.add(t1);
			result.add(t2);
			if (imageType == BYTE) {
				if (t1 > 0)
					result.add(0d);
				else if (t2 < 255)
					result.add(255d);
			} else if (imageType == SHORT) {
				if (t1 > 0)
					result.add(0d);
				else if (t2 < 65535)
					result.add(65535d);
			} else if (imageType == FLOAT)
				result.add((double) -Float.MAX_VALUE);
		}
		if (result.size() == 3)
			return result;
		else
			return null;
	}

	static PolygonRoi getParticle(int x, int y, ImageProcessor ip, List<Double> levels) {

		// default for now...
		boolean floodFill = true;
		double level1 = levels.get(0);
		double level2 = levels.get(1);
		double fillColor = levels.get(2);

		Wand wand = new Wand(ip);
		PolygonFiller pf = new PolygonFiller();
		FloodFiller ff = null;
		if (floodFill) {
			ImageProcessor ipf = ip.duplicate();
			ipf.setValue(fillColor);
			ff = new FloodFiller(ipf);
		}
		wand.autoOutline(x, y, level1, level2);
		if (wand.npoints == 0) {
			System.out.println("Wand error: could not retrieve an area");
			return null;
		}
		PolygonRoi roi = new PolygonRoi(wand.xpoints, wand.ypoints, wand.npoints, Roi.TRACED_ROI);
		Rectangle r = roi.getBounds();
		if (r.width > 1 && r.height > 1) {
			PolygonRoi proi = roi;
			pf.setPolygon(proi.getXCoordinates(), proi.getYCoordinates(), proi.getNCoordinates());
			ip.setMask(pf.getMask(r.width, r.height));
			ip.fill(pf.getMask(r.width, r.height));
			if (floodFill)
				ff.particleAnalyzerFill(x, y, level1, level2, ip.getMask(), r);
		}
		return roi;
	}

	private static ImageStatistics getStatistics(ImageProcessor ip, int mOptions, Calibration cal) {
		switch (getImageType(ip)) {
		case BYTE:
			return new ByteStatistics(ip, mOptions, cal);
		case SHORT:
			return new ShortStatistics(ip, mOptions, cal);
		case FLOAT:
			return new FloatStatistics(ip, mOptions, cal);
		case RGB:
			return new ColorStatistics(ip, mOptions, cal);
		default:
			return null;
		}
	}

	public static void thresholdOnEntropy(ImageProcessor ip) {
		int index = entropySplit(ip.getHistogram());
		// System.out.println(index);
		ip.setThreshold(index, Integer.MAX_VALUE, ImageProcessor.BLACK_AND_WHITE_LUT);
		// ip.threshold(index);
	}

	public static void thresholdOnMaximumEntropy(ImageProcessor ip) {
		int index = HistogramThreshold.maximumEntropy(ip.getHistogram());
		ip.setThreshold(index, Integer.MAX_VALUE, ImageProcessor.BLACK_AND_WHITE_LUT);
		// ip.threshold(index);
	}

	private static int entropySplit(int[] hist) {

		// Normalize histogram, that is makes the sum of all bins equal
		// to 1.
		double sum = 0;
		for (int i = 0; i < hist.length; ++i) {
			sum += hist[i];
		}
		if (sum == 0) {
			// This should not normally happen, but...
			throw new IllegalArgumentException("Empty histogram: sum of all bins is zero.");
		}

		double[] normalizedHist = new double[hist.length];
		for (int i = 0; i < hist.length; i++) {
			normalizedHist[i] = hist[i] / sum;
		}

		//
		double[] pT = new double[hist.length];
		pT[0] = normalizedHist[0];
		for (int i = 1; i < hist.length; i++) {
			pT[i] = pT[i - 1] + normalizedHist[i];
		}

		// Entropy for black and white parts of the histogram
		final double epsilon = Double.MIN_VALUE;
		double[] hB = new double[hist.length];
		double[] hW = new double[hist.length];
		for (int t = 0; t < hist.length; t++) {
			// Black entropy
			if (pT[t] > epsilon) {
				double hhB = 0;
				for (int i = 0; i <= t; i++) {
					if (normalizedHist[i] > epsilon) {
						hhB -= normalizedHist[i] / pT[t] * Math.log(normalizedHist[i] / pT[t]);
					}
				}
				hB[t] = hhB;
			} else {
				hB[t] = 0;
			}

			// White entropy
			double pTW = 1 - pT[t];
			if (pTW > epsilon) {
				double hhW = 0;
				for (int i = t + 1; i < hist.length; ++i) {
					if (normalizedHist[i] > epsilon) {
						hhW -= normalizedHist[i] / pTW * Math.log(normalizedHist[i] / pTW);
					}
				}
				hW[t] = hhW;
			} else {
				hW[t] = 0;
			}
		}

		// Find histogram index with maximum entropy
		double jMax = hB[0] + hW[0];
		int tMax = 0;
		for (int t = 1; t < hist.length; ++t) {
			double j = hB[t] + hW[t];
			if (j > jMax) {
				jMax = j;
				tMax = t;
			}
		}

		return tMax;
	}

	private static Pair<Integer, Integer> rotate(Pair<Integer, Integer> pair, double rotateAngle) {

		double hypothenusa = Math.sqrt(pair.object1 * pair.object1 + pair.object2 * pair.object2);
		double addedrotation = Math.PI * 2 * (rotateAngle) / 360;
		double rotation = Math.asin(pair.object2 / hypothenusa);
		if (pair.object1 >= 0 && pair.object2 >= 0) {
			rotation += addedrotation;
		}
		if (pair.object1 < 0 && pair.object2 > 0) {
			rotation = addedrotation + Math.PI - rotation;
		} else if (pair.object1 <= 0 && pair.object2 <= 0) {
			rotation = addedrotation - rotation + Math.PI;
		} else if (pair.object1 > 0 && pair.object2 < 0) {
			rotation = addedrotation + rotation - Math.PI * 2;

		}

		int newx = (int) Math.round(Math.cos(rotation) * hypothenusa);
		int newy = (int) Math.round(Math.sin(rotation) * hypothenusa);

		return new Pair<Integer, Integer>(newx, newy);

	}

	public static Rectangle getBoundingBox(ImageProcessor ipL, Roi roi, double rotateAngle) {
		double centerx = 0.5 * ipL.getWidth();
		double centery = 0.5 * ipL.getHeight();
		int newroiX = (int) Math.round(roi.getBounds().getMinX() - centerx);
		int newroiY = (int) Math.round(roi.getBounds().getMinY() - centery);
		List<Pair<Integer, Integer>> coordinates = new ArrayList<Pair<Integer, Integer>>();
		Pair<Integer, Integer> pair = rotate(new Pair<Integer, Integer>(newroiX, newroiY),
				rotateAngle);
		int minX = pair.object1;
		int maxX = pair.object1;
		int minY = pair.object2;
		int maxY = pair.object2;
		coordinates.add(rotate(new Pair<Integer, Integer>(newroiX
				+ (int) roi.getBounds().getWidth(), newroiY), rotateAngle));
		coordinates.add(rotate(new Pair<Integer, Integer>(newroiX
				+ (int) roi.getBounds().getWidth(), newroiY + (int) roi.getBounds().getHeight()),
				rotateAngle));
		coordinates.add(rotate(new Pair<Integer, Integer>(newroiX, newroiY
				+ (int) roi.getBounds().getHeight()), rotateAngle));

		for (Pair<Integer, Integer> coord : coordinates) {
			if (minX > coord.object1) {
				minX = coord.object1;
			}
			if (minY > coord.object2) {
				minY = coord.object2;
			}
			if (maxY < coord.object2) {
				maxY = coord.object2;
			}
			if (maxX < coord.object1) {
				maxX = coord.object1;
			}
		}
		newroiX = (int) Math.round(minX + centerx);
		newroiY = (int) Math.round(minY + centery);
		int newroiWidth = maxX - minX;
		int newroiHeight = maxY - minY;
		Rectangle rec2 = new Rectangle(newroiX, newroiY, newroiWidth, newroiHeight);
		return rec2;
	}

	public static void valueStretch(ImageProcessor ipR, double min, double max) {
		ipR.resetMinAndMax();
		ipR.add(-min);
		ipR.multiply(255d / max);

	}

	public static void hardHistogramStretch(ImagePlus ipL) {
		if (getImageType(ipL.getProcessor()) == BYTE) {
			ipL.getProcessor().resetMinAndMax();
			ContrastEnhancer ce = new ContrastEnhancer();
			ce.stretchHistogram(ipL, 0.5);
			double min = ipL.getProcessor().getMin();
			double max = ipL.getProcessor().getMax();
			valueStretch(ipL.getProcessor(), min, max);
		}
	}

	public static ColorModel getGemColorModel() throws IOException {
		// int openBinaryLut(FileInfo fi, boolean isURL, boolean raw) throws
		// IOException
		{

			InputStream is = ImageUtilities.class.getResource("gem-256.lut").openStream();
			DataInputStream f = new DataInputStream(is);
			int nColors = 256;
			// attempt to read 32 byte NIH Image LUT header
			int id = f.readInt();
			if (id != 1229147980) { // 'ICOL'
				f.close();
				return null;
			}
			int version = f.readShort();
			nColors = f.readShort();
			int start = f.readShort();
			int end = f.readShort();
			long fill1 = f.readLong();
			long fill2 = f.readLong();
			int filler = f.readInt();
			byte[] reds = new byte[256];
			byte[] greens = new byte[256];
			byte[] blues = new byte[256];
			// IJ.write(id+" "+version+" "+nColors);
			f.read(reds, 0, nColors);
			f.read(greens, 0, nColors);
			f.read(blues, 0, nColors);
			if (nColors < 256)
				interpolate(reds, greens, blues, nColors);
			IndexColorModel cm = new IndexColorModel(8, 256, reds, greens, blues);
			return cm;
		}
	}

	public static ColorModel getFireColorModel() {
		byte[] reds = new byte[256];
		byte[] greens = new byte[256];
		byte[] blues = new byte[256];
		int[] r = { 0, 0, 1, 25, 49, 73, 98, 122, 146, 162, 173, 184, 195, 207, 217, 229, 240, 252,
				255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 };
		int[] g = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 35, 57, 79, 101, 117, 133, 147, 161,
				175, 190, 205, 219, 234, 248, 255, 255, 255, 255 };
		int[] b = { 0, 61, 96, 130, 165, 192, 220, 227, 210, 181, 151, 122, 93, 64, 35, 5, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 35, 98, 160, 223, 255 };
		for (int i = 0; i < r.length; i++) {
			reds[i] = (byte) r[i];
			greens[i] = (byte) g[i];
			blues[i] = (byte) b[i];
		}
		interpolate(reds, greens, blues, r.length);
		IndexColorModel cm = new IndexColorModel(8, 256, reds, greens, blues);
		return cm;
	}

	private static void interpolate(byte[] reds, byte[] greens, byte[] blues, int nColors) {
		byte[] r = new byte[nColors];
		byte[] g = new byte[nColors];
		byte[] b = new byte[nColors];
		System.arraycopy(reds, 0, r, 0, nColors);
		System.arraycopy(greens, 0, g, 0, nColors);
		System.arraycopy(blues, 0, b, 0, nColors);
		double scale = nColors / 256.0;
		int i1, i2;
		double fraction;
		for (int i = 0; i < 256; i++) {
			i1 = (int) (i * scale);
			i2 = i1 + 1;
			if (i2 == nColors)
				i2 = nColors - 1;
			fraction = i * scale - i1;
			// IJ.write(i+" "+i1+" "+i2+" "+fraction);
			reds[i] = (byte) ((1.0 - fraction) * (r[i1] & 255) + fraction * (r[i2] & 255));
			greens[i] = (byte) ((1.0 - fraction) * (g[i1] & 255) + fraction * (g[i2] & 255));
			blues[i] = (byte) ((1.0 - fraction) * (b[i1] & 255) + fraction * (b[i2] & 255));
		}
	}
	public static String[] COLORS = { "red", "blue", "green", "yellow", "cyan", "magenta", "pink",
		"gray", "white", "orange", "lsBlue", "aguamarine", "brown", "black" };


}
