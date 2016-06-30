package org.kuleuven.lineager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuleuven.lineagetree.Lineage2StatisticsScript;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.lineagetree.StarryNightFormatIO;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.math.matrix.ColumnVectorMatrix;
import org.kuleuven.math.matrix.RowArrayMatrix;
import org.kuleuven.math.space.IntegerSpace;
import org.kuleuven.math.space.Space;
import org.kuleuven.math.vector.ArrayVector;
import org.kuleuven.math.vector.Vector;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;

public class GetAxes {
	String baseDir = "/Users/rjelier/Projects/CE_data/SPIM/";
	String[] files = { "230412N2.zip" };
	public static int ORIENT4CELL = 1;
	public static int ORIENTMS = 2;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new GetAxes().run2();
	}

	public static Matrix getTransformationMatrix(List<Vector<Integer>> v) {
		Matrix m = MatrixFactory.dense(3, 3);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				m.setAsDouble(v.get(i).get(j), i, j);
			}
		}
		return m.inv();
	}

	public static org.kuleuven.math.matrix.Matrix<Integer, Integer> getOURTransformationMatrix(
			List<Vector<Integer>> v) throws Exception {
		RowArrayMatrix<Integer, Integer> m = new RowArrayMatrix<Integer, Integer>(
				IntegerSpace.threeD, IntegerSpace.threeD);
		for (int i = 0; i < v.size(); i++) {
			Vector<Integer> vector = v.get(i);
			for (int j = 0; j < v.size(); j++) {
				m.set(j, i, vector.get(j));
			}
		}
		return m.invert();
	}

	/**
	 * 
	 * @param transformMatrix
	 * @param vector
	 * @param mode
	 * @return
	 */
	public static Vector<Integer> transform(
			org.kuleuven.math.matrix.Matrix<Integer, Integer> transformMatrix,
			Vector<Integer> vector, int mode) {
		if (mode == ORIENTMS) {
			RowArrayMatrix<Integer, Integer> matrixF = new RowArrayMatrix<Integer, Integer>(
					IntegerSpace.oneD, IntegerSpace.threeD);
			for (int k = 0; k < 3; k++) {
				matrixF.set(0, k, vector.get(k));
			}
			RowArrayMatrix<Integer, Integer> result = matrixF
					.multiply(transformMatrix);
			Vector<Integer> resultV = new ArrayVector<Integer>(
					vector.getSpace());
			for (int i = 0; i < 3; i++) {
				resultV.set(i, result.get(0, i));
			}
			return resultV;
		} else if (mode == ORIENT4CELL) {
			RowArrayMatrix<Integer, Integer> matrixF = new RowArrayMatrix<Integer, Integer>(
					IntegerSpace.threeD, IntegerSpace.oneD);
			for (int k = 0; k < 3; k++) {
				matrixF.set(k, 0, vector.get(k));
			}
			RowArrayMatrix<Integer, Integer> result = new RowArrayMatrix<Integer, Integer>(
					IntegerSpace.threeD, IntegerSpace.oneD);
			transformMatrix.multiply(result, matrixF);

			// org.ujmp.core.Matrix result = transformationMatrix.mtimes(v);
			Vector<Integer> resultV = new ArrayVector<Integer>(
					vector.getSpace());
			for (int i = 0; i < 3; i++) {
				resultV.set(i, result.get(i, 0));
			}
			return resultV;
		}
		return null;
	}

	public static RowArrayMatrix<Integer, Integer> directedTransformationMatrix(
			List<Vector<Integer>> elongatedCloudOfPoints,
			Vector<Integer> positiveAP, Vector<Integer> negativeVentral,
			Vector<Integer> LRdirection) throws Exception {
		ColumnVectorMatrix resultx = new ColumnVectorMatrix(
				elongatedCloudOfPoints, Space.threeD);
		RowArrayMatrix dispersionMatrix = resultx.rowCovarianceCoordinates();
		ArrayVector eigenValues = new ArrayVector(
				dispersionMatrix.getRowSpace());
		RowArrayMatrix<Integer, Integer> eigenVectors = dispersionMatrix
				.eigenVectors(eigenValues);

		ArrayVector<Integer> center = new ArrayVector<Integer>(Space.threeD);
		Vector.meanVector(center, elongatedCloudOfPoints);
		Vector<Integer> centerTransform = transform(eigenVectors, center,
				ORIENTMS);

		Vector<Integer> APtransform = transform(eigenVectors, positiveAP,
				ORIENTMS);
		APtransform.subtract(centerTransform);
		if (APtransform.get(0) < 0) {
			for (int i = 0; i < 3; i++)
				eigenVectors.set(i, 0, -eigenVectors.get(i, 0));
		}
		Vector<Integer> DVtransform = transform(eigenVectors, negativeVentral,
				ORIENTMS);
		DVtransform.subtract(centerTransform);
		double angle = -0.5 * Math.PI
				- Math.atan2(DVtransform.get(1), DVtransform.get(2));
		RowArrayMatrix<Integer, Integer> m = new RowArrayMatrix<Integer, Integer>(
				Space.threeD, Space.threeD);
		m.constants(0);
		m.set(0, 0, 1);
		m.set(1, 1, Math.cos(angle));
		m.set(1, 2, -Math.sin(angle));
		m.set(2, 2, Math.cos(angle));
		m.set(2, 1, Math.sin(angle));
		eigenVectors = eigenVectors.multiply(m);
		Vector<Integer> LRdirectionTransform = transform(eigenVectors,
				LRdirection, ORIENTMS);
		if (LRdirectionTransform.get(2) > 0) {
			for (int i = 0; i < 3; i++)
				eigenVectors.set(i, 2, -eigenVectors.get(i, 2));
		}
		return eigenVectors;
	}

	public static org.kuleuven.math.matrix.Matrix<Integer, Integer> getAlignmentByMS(
			LineageMeasurement lm, int time) {
		return getAlignmentByMS(lm, time, false);
	}

	public static org.kuleuven.math.matrix.Matrix<Integer, Integer> getAlignmentByMS(
			LineageMeasurement lm, int time, boolean lateMode) {
		List<Vector<Integer>> positions = new ArrayList<Vector<Integer>>();

		Map<Integer, Set<Nucleus>> data = lm.getNucleiOverTime();

		int correction = Lineage2StatisticsScript.getCorrection(lm);
		for (int i = correction; i < Math.min(correction + 100,
				lm.getLastTimePoint()); i++) {
			Set<Nucleus> n = data.get(i);
			for (Nucleus nuc : n)
				positions.add(nuc.getCoordinates());
		}

		List<Nucleus> nucs = lm.getNucleiForATimepoint(time);
		List<Vector<Integer>> Cnucs = new ArrayList<Vector<Integer>>();
		List<Vector<Integer>> MSnucs = new ArrayList<Vector<Integer>>();
		List<Vector<Integer>> ABplnucs = new ArrayList<Vector<Integer>>();
		List<Vector<Integer>> ABprnucs = new ArrayList<Vector<Integer>>();
		for (Nucleus n : nucs) {
			if (lateMode
					&& (n.getName().startsWith("MSaap") | n.getName()
							.startsWith("MSpap"))) {
				MSnucs.add(n.getCoordinates());
			} else if (!lateMode && (n.getName().startsWith("MS")))
				MSnucs.add(n.getCoordinates());
			else if ((n.getName().startsWith("C")))
				Cnucs.add(n.getCoordinates());
			else if (n.getName().startsWith("ABpl"))
				ABplnucs.add(n.getCoordinates());
			else if (n.getName().startsWith("ABpr"))
				ABprnucs.add(n.getCoordinates());

		}
		ArrayVector<Integer> Ccenter = new ArrayVector<Integer>(Space.threeD);
		Vector.meanVector(Ccenter, Cnucs);
		ArrayVector<Integer> MScenter = new ArrayVector<Integer>(Space.threeD);
		// Vector.medianVector(MScenter, MSnucs);
		Vector.meanVector(MScenter, MSnucs);
		ArrayVector<Integer> ABplcenter = new ArrayVector<Integer>(Space.threeD);
		// Vector.medianVector(ABplcenter, ABplnucs);
		Vector.meanVector(ABplcenter, ABplnucs);
		ArrayVector<Integer> ABprcenter = new ArrayVector<Integer>(Space.threeD);
		// Vector.medianVector(ABprcenter, ABprnucs);
		Vector.meanVector(ABprcenter, ABprnucs);

		// TrackedNucleus ABplT = lm.retrieveTrackedNucleusByName("ABpl");
		// Nucleus ABpl =
		// ABplT.getNucleusForTimePoint(ABplT.getFirstTimePoint()+1);
		// TrackedNucleus ABprT = lm.retrieveTrackedNucleusByName("ABpr");
		// Nucleus ABpr =
		// ABprT.getNucleusForTimePoint(ABplT.getFirstTimePoint()+1);
		ABprcenter.subtract(ABplcenter);
		org.kuleuven.math.matrix.Matrix<Integer, Integer> axes = null;
		try {
			axes = GetAxes.directedTransformationMatrix(positions, Ccenter,
					MScenter, ABprcenter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return axes;

	}

	public static org.kuleuven.math.matrix.Matrix<Integer, Integer> getOrientLineageMatrix(
			LineageMeasurement lm) throws Exception {
		return getOrientLineageMatrix(lm, ORIENT4CELL);
	}

	public static Vector<Integer> adapt2(
			org.kuleuven.math.matrix.Matrix<Integer, Integer> transformationMatrix,
			Vector<Integer> divisionAxis) {
		RowArrayMatrix<Integer, Integer> matrixF = new RowArrayMatrix<Integer, Integer>(
				IntegerSpace.threeD, IntegerSpace.oneD);
		for (int k = 0; k < 3; k++) {
			matrixF.set(k, 0, divisionAxis.get(k));
		}
		RowArrayMatrix<Integer, Integer> result = new RowArrayMatrix<Integer, Integer>(
				IntegerSpace.threeD, IntegerSpace.oneD);
		transformationMatrix.multiply(result, matrixF);

		// org.ujmp.core.Matrix result = transformationMatrix.mtimes(v);
		Vector<Integer> resultV = new ArrayVector<Integer>(
				divisionAxis.getSpace());
		for (int i = 0; i < 3; i++) {
			resultV.set(i, result.get(i, 0));
		}
		return resultV;

	}

	private static Vector<Integer> adapt(org.ujmp.core.Matrix ax,
			Vector<Integer> divisionAxis) {
		org.ujmp.core.Matrix v = MatrixFactory.dense(3, 1);
		for (int i = 0; i < 3; i++) {
			v.setAsDouble(divisionAxis.get(i), i, 0);
		}
		org.ujmp.core.Matrix result = ax.mtimes(v);
		Vector<Integer> resultV = new ArrayVector<Integer>(
				divisionAxis.getSpace());
		for (int i = 0; i < 3; i++) {
			resultV.set(i, result.getAsDouble(i, 0));
		}
		return resultV;

	}

	public static org.kuleuven.math.matrix.Matrix<Integer, Integer> getOrientLineageMatrix(
			LineageMeasurement lm, int mode) throws Exception {
		if (mode == ORIENT4CELL) {
			int correction = Lineage2StatisticsScript.getCorrection(lm);
			TrackedNucleus ABaT = lm.retrieveTrackedNucleusByName("ABa");
			TrackedNucleus ABpT = lm.retrieveTrackedNucleusByName("ABp");
			TrackedNucleus P2T = lm.retrieveTrackedNucleusByName("P2");
			TrackedNucleus EMST = lm.retrieveTrackedNucleusByName("EMS");
			if (ABaT != null && ABpT != null && P2T != null && EMST != null) {
				Nucleus ABa = ABaT.getNucleusForTimePoint(correction);
				Nucleus ABp = ABpT.getNucleusForTimePoint(correction);
				Nucleus P2 = P2T.getNucleusForTimePoint(correction);
				Nucleus EMS = EMST.getNucleusForTimePoint(correction);
				org.kuleuven.math.matrix.Matrix<Integer, Integer> axes = GetAxes
						.getOURTransformationMatrix(GetAxes.getAxes(
								ABa.getCoordinates(), ABp.getCoordinates(),
								P2.getCoordinates(), EMS.getCoordinates()));
				return axes;
			}
		} else if (mode == ORIENTMS) {
			List<Vector<Integer>> positions = new ArrayList<Vector<Integer>>();

			Map<Integer, Set<Nucleus>> data = lm.getNucleiOverTime();

			int correction = Lineage2StatisticsScript.getCorrection(lm);
			for (int i = correction; i < Math.min(correction + 100,
					lm.getLastTimePoint()); i++) {
				Set<Nucleus> n = data.get(i);
				for (Nucleus nuc : n)
					positions.add(nuc.getCoordinates());
			}

			TrackedNucleus P2T = lm.retrieveTrackedNucleusByName("P2");
			TrackedNucleus MST = lm.retrieveTrackedNucleusByName("MS");
			TrackedNucleus ABplT = lm.retrieveTrackedNucleusByName("ABpl");
			TrackedNucleus ABprT = lm.retrieveTrackedNucleusByName("ABpr");
			if (P2T == null || MST == null || ABplT == null || ABprT == null) {
				return null;
			} else {
				Nucleus P2 = P2T.getNucleusForTimePoint(correction);
				Nucleus MS = MST.getNucleusForTimePoint(MST.getLastTimePoint());
				Nucleus ABpl = ABplT.getNucleusForTimePoint(ABplT
						.getFirstTimePoint());
				Nucleus ABpr = ABprT.getNucleusForTimePoint(ABplT
						.getFirstTimePoint());
				Vector<Integer> LRdirection = new ArrayVector<Integer>(
						ABpr.getCoordinates());
				LRdirection.subtract(ABpl.getCoordinates());
				org.kuleuven.math.matrix.Matrix<Integer, Integer> axes = GetAxes
						.directedTransformationMatrix(positions,
								P2.getCoordinates(), MS.getCoordinates(),
								LRdirection);
				return axes;
			}
		}
		return null;
	}

	public static List<Vector<Integer>> getAxes(Vector<Integer> ABa,
			Vector<Integer> ABp, Vector<Integer> P2, Vector<Integer> EMS) {
		return getAxes(ABa, ABp, P2, EMS, false);
	}

	public static List<Vector<Integer>> getAxes(Vector<Integer> ABa,
			Vector<Integer> ABp, Vector<Integer> P2, Vector<Integer> EMS,
			boolean flip) {
		ArrayVector<Integer> APaxis = new ArrayVector<Integer>(P2);
		ArrayVector<Integer> DVaxis = new ArrayVector<Integer>(EMS);
		APaxis.subtract(ABa);
		DVaxis.subtract(P2);
		Vector<Integer> AP_Projection = new ArrayVector<Integer>(APaxis);
		AP_Projection.multiply(APaxis.innerProduct(DVaxis)
				/ APaxis.getSquaredNorm());
		DVaxis.subtract(AP_Projection);
		ArrayVector<Integer> LRaxis = getCrossProduct(APaxis, DVaxis);
		if (flip)
			LRaxis.multiply(-1);
		APaxis.normalize();
		DVaxis.normalize();
		LRaxis.normalize();

		List<Vector<Integer>> axes = new ArrayList<Vector<Integer>>();

		axes.add(APaxis);
		axes.add(DVaxis);
		axes.add(LRaxis);
		return axes;
	}

	public void run2() {
		Vector<Integer> v = new ArrayVector<Integer>(IntegerSpace.threeD);
		v.set(0, 1);
		Vector<Integer> v2 = new ArrayVector<Integer>(IntegerSpace.threeD);
		v2.set(1, -1);
		ArrayVector<Integer> LRaxis = getCrossProduct(v, v2);
		LRaxis.dump();
	}

	public void run() {
		LineageMeasurement lm;
		try {
			lm = StarryNightFormatIO.readNuclei(baseDir + files[0], 0.1f, 1f,
					1f);

			List<Nucleus> n = new ArrayList<Nucleus>(lm.getNucleiOverTime()
					.get(Lineage2StatisticsScript.getCorrection(lm)));
			Vector<Integer> ABa = null;
			Vector<Integer> ABp = null;
			Vector<Integer> P2 = null;
			Vector<Integer> EMS = null;
			for (Nucleus n1 : n) {
				if (n1.getName().equalsIgnoreCase("ABa")) {
					ABa = n1.getCoordinates();
				} else if (n1.getName().equals("ABp")) {
					ABp = n1.getCoordinates();
				} else if (n1.getName().equals("P2")) {
					P2 = n1.getCoordinates();
				} else if (n1.getName().endsWith("EMS")) {
					EMS = n1.getCoordinates();
				}
			}
			List<Vector<Integer>> axes = getAxes(ABa, ABp, P2, EMS);
			int i = 0;
			for (Vector<Integer> axis : axes) {
				i++;
				int j = 0;
				for (Vector<Integer> axis2 : axes) {
					j++;
					// System.out.println(i + "\t" + j + "\t" +
					// axis.cosine(axis2) + "\n"+ axis.getNonzerosString());
				}
			}
			ArrayVector<Integer> APaxis = new ArrayVector<Integer>(ABp);
			APaxis.subtract(EMS);
			System.out.println(APaxis.getNonzerosString());
			org.kuleuven.math.matrix.Matrix<Integer, Integer> transformationMatrix = GetAxes
					.getOURTransformationMatrix(axes);
			RowArrayMatrix<Integer, Integer> APm = new RowArrayMatrix<Integer, Integer>(
					IntegerSpace.threeD, IntegerSpace.oneD);
			for (int k = 0; k < 3; k++) {
				APm.set(k, 0, APaxis.get(k));
			}
			RowArrayMatrix<Integer, Integer> result = new RowArrayMatrix<Integer, Integer>(
					IntegerSpace.threeD, IntegerSpace.oneD);

			transformationMatrix.multiply(result, APm);
			System.out.println(result.dumpToString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static ArrayVector<Integer> getCrossProduct(Vector<Integer> v1,
			Vector<Integer> v2) {
		ArrayVector<Integer> result = new ArrayVector<Integer>(
				IntegerSpace.threeD);
		result.set(0, v1.get(1) * v2.get(2) - v1.get(2) * v2.get(1));
		result.set(1, v1.get(2) * v2.get(0) - v1.get(0) * v2.get(2));
		result.set(2, v1.get(0) * v2.get(1) - v1.get(1) * v2.get(0));
		return result;
	}

	public static double minDistancePointToLine(Vector<Integer> linePoint1,
			Vector<Integer> linePoint2, Vector<Integer> targetPoint) {
		ArrayVector<Integer> inbetweenX0X1 = new ArrayVector<Integer>(
				targetPoint);
		ArrayVector<Integer> inbetweenX0X2 = new ArrayVector<Integer>(
				targetPoint);
		ArrayVector<Integer> inbetweenX1X2 = new ArrayVector<Integer>(
				linePoint1);
		inbetweenX0X1.subtract(linePoint1);
		inbetweenX0X2.subtract(linePoint2);
		inbetweenX1X2.subtract(linePoint2);
		ArrayVector<Integer> inbetweenCP = getCrossProduct(inbetweenX0X1,
				inbetweenX0X2);
		return inbetweenCP.norm() / inbetweenX1X2.norm();

	}

	public static ArrayVector<Integer> closestPointOnLine(
			Vector<Integer> linePoint1, Vector<Integer> linePoint2,
			Vector<Integer> targetPoint) {
		ArrayVector<Integer> inbetweenX1X0 = new ArrayVector<Integer>(
				linePoint1);
		ArrayVector<Integer> inbetweenX2X1 = new ArrayVector<Integer>(
				linePoint2);
		inbetweenX1X0.subtract(targetPoint);
		inbetweenX2X1.subtract(linePoint1);

		double t = -inbetweenX1X0.innerProduct(inbetweenX2X1)
				/ inbetweenX2X1.getSquaredNorm();
		inbetweenX2X1.multiply(t);
		inbetweenX2X1.add(linePoint1);
		return inbetweenX2X1;
	}

	public static void reOrientLineage(
			LineageMeasurement lm,
			org.kuleuven.math.matrix.Matrix<Integer, Integer> transformationMatrix,
			int mode) throws Exception {
		for (Nucleus n : lm.nucleusMap.keySet()) {
			Vector<Integer> adapted = GetAxes.transform(transformationMatrix,
					n.getCoordinates(), mode);
			n.setCoordinates(adapted);
		}
	}
}
