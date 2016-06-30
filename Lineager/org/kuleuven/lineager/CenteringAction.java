package org.kuleuven.lineager;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;

import org.kuleuven.collections.SortedListMap;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.math.MathEMC;
import org.kuleuven.math.space.IntegerSpace;
import org.kuleuven.math.space.Space;
import org.kuleuven.math.vector.ArrayVector;
import org.kuleuven.math.vector.Vector;


public class CenteringAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 472562039320050972L;
	private WormDelux wormDelux;
	private int windowOverhang = 5;
	Space<Integer> space = IntegerSpace.threeD;

	public CenteringAction(WormDelux wormDelux) {
		super("Center lineage");
		this.wormDelux = wormDelux;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		LineagingProject lp = wormDelux.getSelectedProject();
		if (lp != null) {
			LineagerObjectExchange linexch = lp.getLinexch();

			if (linexch != null) {
				LineageMeasurement lm = linexch.getLineageMeasurement();
				if (lm != null) {
					List<double[]> centers = new ArrayList<double[]>();
					SortedListMap<Integer, Set<Nucleus>> map = (SortedListMap<Integer, Set<Nucleus>>) lm
							.getNucleiOverTime();
					Iterator<Set<Nucleus>> iterator = map.iterator();
					while (iterator.hasNext()) {
						double[] center = getMedianCenter(iterator.next());
						centers.add(center);
					}
					for (int i = 0; i < centers.size(); i++) {
						int start = Math.max(0, i - windowOverhang);
						int end = Math.min(centers.size() - 1, i + windowOverhang);
						double[] v = meanVector(centers.subList(start, end + 1));
						Set<Nucleus> nuclei = map.getValue(i);
						for (Nucleus n : nuclei) {
							Vector<Integer> vector = n.getCoordinates();
							ArrayVector<Integer> newV = new ArrayVector<Integer>(space);
							newV.set(0, vector.get(0) - v[0]);
							newV.set(1, vector.get(1) - v[1]);
							newV.set(2, vector.get(2) - v[2]);
							n.setCoordinates(newV);
						}
						map.setValue(i, nuclei);
					}
				}
			}
		}
	}

	private double[] getMedianCenter(Set<Nucleus> value) {
		List<Double> x = new ArrayList<Double>();
		List<Double> y = new ArrayList<Double>();
		List<Double> z = new ArrayList<Double>();
		for (Nucleus nuc : value) {
			x.add(nuc.getX());
			y.add(nuc.getY());
			z.add(nuc.getZ());
		}
		double xVal = MathEMC.mean(x);
		double yVal = MathEMC.mean(y);
		double zVal = MathEMC.mean(z);
		double[] result = { xVal, yVal, zVal };
		return result;
	}

	private double[] getCenter(Set<Nucleus> value) {
		List<double[]> vectors = new ArrayList<double[]>(value.size());
		for (Nucleus nuc : value) {
			double[] val = { nuc.getX(), nuc.getY(), nuc.getZ() };
			vectors.add(val);
		}
		double[] t_center = meanVector(vectors);
		return t_center;
	}

	private double[] meanVector(List<double[]> vectors) {
		double[] xA = new double[vectors.size()];
		double[] yA = new double[vectors.size()];
		double[] zA = new double[vectors.size()];
		for (int i = 0; i < vectors.size(); i++) {
			double[] vector = vectors.get(i);
			xA[i] = vector[0];
			yA[i] = vector[1];
			zA[i] = vector[2];
		}
		double xVal = MathEMC.mean(xA);
		double yVal = MathEMC.mean(yA);
		double zVal = MathEMC.mean(zA);
		double[] result = { xVal, yVal, zVal };
		return result;
	}

}
