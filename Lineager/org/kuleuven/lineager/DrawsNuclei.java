package org.kuleuven.lineager;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.util.Set;

import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;

public class DrawsNuclei {

	public static ImageProcessor drawNuclei(ImageProcessor iproc, LineageMeasurement lm, int time,
			int plane) {
		if (!(iproc instanceof ColorProcessor))
			iproc = iproc.convertToRGB();
		iproc.setColor(Color.green);
		iproc.setLineWidth(2);
		float pZ = plane * lm.zResolution;
		Set<Nucleus> nucleusMap = lm.getNucleiOverTime().get(time);
		if(nucleusMap!=null){
		for (Nucleus n : nucleusMap) {
			double z = n.getZ();
			float r = n.getRadius();
			if ((z + r) >= pZ && (z - r) <= pZ) {

				double zrel =  pZ - z;
				float r2 = (float) Math.sqrt(r * r - zrel * zrel);
				iproc.drawOval((int)Math.round((-r2 + n.getX()) / lm.xyResolution), (int)Math.round((-r2 + n
						.getY())
						/ lm.xyResolution), 2 * Math.round(r2 / lm.xyResolution), 2 * Math.round(r2
						/ lm.xyResolution));

			}
		}}
		return iproc;
	}

	public static Nucleus getNucleus(int x, int y, int plane, int time, LineageMeasurement lm) {
		float pZ = plane * lm.zResolution;
		Set<Nucleus> nucleusMap = lm.getNucleiOverTime().get(time);
		if (nucleusMap != null) {
			for (Nucleus n : nucleusMap) {
				double z = n.getZ();
				float r = n.getRadius();
				if ((z + r) >= pZ && (z - r) <= pZ) {
					// (float) x
					double zrel =  pZ - z;
					float r2 = (float) Math.sqrt(r * r - zrel * zrel);
					float xcor = x * lm.xyResolution;
					float ycor = y * lm.xyResolution;
					float distance = (float) Math.sqrt((xcor - n.getX()) * (xcor - n.getX())
							+ (ycor - n.getY()) * (ycor - n.getY()));
					if (distance <= r2) {
						return n;
					}
				}
			}
		}
		return null;
	}

	public static void drawNucleus(ImageProcessor iproc, Nucleus n, int plane, Color color,
			LineageMeasurement lm) {
		if (!(iproc instanceof ColorProcessor))
			iproc = iproc.convertToRGB();
		iproc.setColor(color);
		iproc.setLineWidth(2);

		double z = n.getZ();
		float r = n.getRadius();
		float pZ = plane * lm.zResolution;

		if ((z + r) >= pZ && (z - r) <= pZ) {

			double zrel = pZ - z;
			float r2 = (float) Math.sqrt(r * r - zrel * zrel);
			iproc.drawOval((int)Math.round((-r2 + n.getX()) / lm.xyResolution), (int)Math.round((-r2 + n
					.getY())
					/ lm.xyResolution), 2 * Math.round(r2 / lm.xyResolution), 2 * Math.round(r2
					/ lm.xyResolution));

		}
	}

	public static void drawSelectedNucleus(ImageProcessor iproc, Nucleus n, int plane,
			LineageMeasurement lm) {
		drawNucleus(iproc, n, plane, Color.red, lm);
	}
}
