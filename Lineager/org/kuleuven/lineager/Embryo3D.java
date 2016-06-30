package org.kuleuven.lineager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.kuleuven.collections.ComparatorFactory;
import org.kuleuven.collections.SortedListMap;
import org.kuleuven.lineagetree.Lineage2StatisticsScript;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.math.matrix.Matrix;
import org.kuleuven.math.space.IntegerSpace;
import org.kuleuven.math.vector.ArrayVector;
import org.kuleuven.math.vector.Vector;

import com.sun.j3d.utils.geometry.Cylinder;

public class Embryo3D extends BranchGroup {
	private LineageMeasurement lm;
	int time = 1;
	private Matrix<Integer, Integer> transformationMatrix = null;

	private Map<String, Color3f> subLineageColors;

	private Map<TrackedNucleus, Color3f> colorMap;
	private Set<TrackedNucleus> hideSisterLines = new HashSet<TrackedNucleus>();
	private Set<TrackedNucleus> hideNuclei = new HashSet<TrackedNucleus>();
	private Set<String> hideSisterLinesNames = new HashSet<String>();
	private Set<String> hiddenNucleiNames = new HashSet<String>();

	private Set<String> ignoreNames = new HashSet<String>();
	private boolean showParents = false;
	private boolean showChildren = false;
	private boolean showSisterLines = true;
	private final Vector3f yAxis = new Vector3f(0f, 1f, 0f);
	private HashMap<TrackedNucleus, TrackedNucleus> daughterMap;
	private float width;
	private float heigth;
	private Vector<Integer> center = null;

	private Appearance app;
	private BranchGroup nucleiBG;
	public Color3f fixedColor = ColorConstants.gray;
	private SortedListMap<Integer, List<Nucleus>> nucleiOverTime;
	private HashMap<Nucleus, TrackedNucleus> nucleusMap;
	private float transparencyValue = 0.8f;
	private float cylinderDiameter = 0.01f;
	private boolean hideNucleiBool;
	private boolean showTraces = false;
	private int traceStart;
	private int correction;
	private List<CellGroupCylinder> cellGroupCylinders = new ArrayList<CellGroupCylinder>();

	private float maxShownAPcoordinate = Float.MAX_VALUE;
	private float minShownAPcoordinate = Float.MIN_VALUE;
	private float maxAP;
	private float minAP;
	private int orientationMode;
	private int traceDetail;

	public Embryo3D(LineageMeasurement lm,
			Map<String, Color3f> subLineageColors, Color3f fixedColor,
			int time, float transparency, int orientationMode) {
		this.transparencyValue = transparency;
		if (orientationMode == GetAxes.ORIENT4CELL && fourCellCheck(lm)) {
			this.orientationMode = orientationMode;
		} else {
			this.orientationMode = GetAxes.ORIENTMS;
		}
		
		this.lm = lm;

		this.correction = Lineage2StatisticsScript.getCorrection(lm);
		this.traceStart = correction;
		this.time = time;
		this.fixedColor = fixedColor;
		addIgnoreNames();
		System.out.println("make embryo3d");
		this.subLineageColors = new HashMap<String, Color3f>();
		if (subLineageColors != null)
			this.subLineageColors.putAll(subLineageColors);
		this.colorMap = new HashMap<TrackedNucleus, Color3f>();

		getWidthAndHeight();
		Color3f eColor = new Color3f(0.0f, 0.0f, 0.0f);
		Color3f sColor = new Color3f(1.0f, 1.0f, 1.0f);
		Material m = new Material(eColor, eColor, sColor, sColor, 100.0f);
		m.setLightingEnable(true);
		app = new Appearance();
		app.setMaterial(m);

		this.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		this.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		this.setCapability(Node.ENABLE_PICK_REPORTING);
		this.setCapability(Group.ALLOW_CHILDREN_WRITE);
		this.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		getTransformationMatrix();
		parseLineage(lm);
		initializeDaughterConnections();
		initializeColor();
		initialize();

	}

	private void getTransformationMatrix() {
		try {
			transformationMatrix = GetAxes.getOrientLineageMatrix(lm,
					orientationMode);
			if(transformationMatrix==null){
				transformationMatrix = GetAxes.getAlignmentByMS(lm, correction, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeHidden() {
		hideNuclei.clear();
		hideSisterLines.clear();

		Set<TrackedNucleus> shownNuclei = new HashSet<TrackedNucleus>();
		Set<TrackedNucleus> shownLines = new HashSet<TrackedNucleus>();

		for (Entry<String, Color3f> e : subLineageColors.entrySet()) {
			Set<TrackedNucleus> tns = lm
					.retrieveTrackedNucleiByName(e.getKey());
			if (!hiddenNucleiNames.contains(e.getKey())) {
				shownNuclei.addAll(tns);
			} else {
				hideNuclei.addAll(tns);
			}
			if (!hideSisterLinesNames.contains(e.getKey())) {
				shownLines.addAll(tns);
			} else {
				hideSisterLines.addAll(tns);
			}

		}

		Set<TrackedNucleus> add = new HashSet<TrackedNucleus>();
		if (showChildren) {
			for (TrackedNucleus tn : hideNuclei) {
				add.addAll(getChildren(tn, shownNuclei));
			}
		}
		if (showParents) {
			for (TrackedNucleus tn : hideNuclei) {
				add.addAll(getPaps(tn, shownNuclei));
			}
		}
		hideNuclei.addAll(add);

		add = new HashSet<TrackedNucleus>();
		if (showChildren) {
			for (TrackedNucleus tn : hideSisterLines) {
				add.addAll(getChildren(tn, shownLines));
			}
		}
		if (showParents) {
			for (TrackedNucleus tn : hideSisterLines) {
				add.addAll(getPaps(tn, shownLines));
			}
		}
		hideSisterLines.addAll(add);
	}

	public Map<String, Color3f> getSubLineageColors() {
		Map<String, Color3f> out = new HashMap<String, Color3f>(
				subLineageColors);
		return out;
	}

	public void setSubLineageColors(Map<String, Color3f> subLineageColors) {
		this.subLineageColors.clear();
		this.subLineageColors.putAll(subLineageColors);
		initializeColor();
		initialize();
	}

	public void setTransformationMatrix(Matrix<Integer, Integer> matrix) {
		transformationMatrix = matrix;
		center = null;
		// getTransformationMatrix();
		parseLineage(lm);
		// initializeDaughterConnections();
		// initializeColor();
		initialize();

	}

	private static boolean fourCellCheck(LineageMeasurement lm) {
		return (lm.retrieveTrackedNucleiByName("ABa").size() > 0
				&& lm.retrieveTrackedNucleiByName("ABp").size() > 0
				&& lm.retrieveTrackedNucleiByName("EMS").size() > 0 && lm
				.retrieveTrackedNucleiByName("P2").size() > 0);
	}

	public void setEmbryoOrientationMode(int mode) {
		if (orientationMode == GetAxes.ORIENT4CELL && fourCellCheck(lm)) {
			orientationMode = mode;
		} else {
			orientationMode = GetAxes.ORIENTMS;
		}
		center = null;
		getTransformationMatrix();
		parseLineage(lm);
		// initializeDaughterConnections();
		// initializeColor();
		initialize();

	}

	private void initializeDaughterConnections() {
		daughterMap = new HashMap<TrackedNucleus, TrackedNucleus>();
		for (TrackedNucleus tn : lm.getAllTrackedNuclei()) {
			org.kuleuven.collections.Node<TrackedNucleus> node = lm.lineage
					.getNode(tn);
			if (node.countChildren() > 1) {
				List<TrackedNucleus> tns = node.getChildrenAsSortedList();
				for (int i = 0; i < tns.size(); i++) {
					TrackedNucleus d1 = tns.get(i);
					for (int j = i + 1; j < tns.size(); j++) {
						TrackedNucleus d2 = tns.get(j);
						daughterMap.put(d1, d2);
						daughterMap.put(d2, d1);
					}
				}
			}
		}
	}

	private Set<TrackedNucleus> getPaps(TrackedNucleus tn,
			Set<TrackedNucleus> others) {
		Set<TrackedNucleus> paps = lm.lineage.getAllParents(tn);
		Set<TrackedNucleus> excluded = new HashSet<TrackedNucleus>(others);
		excluded.remove(tn);
		excluded.retainAll(paps);
		for (TrackedNucleus tnR : excluded) {
			paps.removeAll(lm.lineage.getParents(tnR));
		}
		return paps;
	}

	private Set<TrackedNucleus> getChildren(TrackedNucleus tn,
			Set<TrackedNucleus> others) {
		org.kuleuven.collections.Node<TrackedNucleus> node = lm.lineage
				.getNode(tn);
		if (node != null) {
			Set<TrackedNucleus> children = node.getAllChildrenBelowNode();
			Set<TrackedNucleus> excluded = new HashSet<TrackedNucleus>(others);
			excluded.remove(tn);
			excluded.retainAll(children);
			children.removeAll(excluded);
			for (TrackedNucleus tnR : excluded) {
				children.removeAll(lm.lineage.getNode(tnR)
						.getAllChildrenBelowNode());
			}
			return children;
		}
		return null;
	}

	private void initializeColor() {
		colorMap.clear();
		Map<TrackedNucleus, Color3f> tns = new HashMap<TrackedNucleus, Color3f>();
		for (Entry<String, Color3f> e : subLineageColors.entrySet()) {
			for (TrackedNucleus tn : lm.retrieveTrackedNucleiByName(e.getKey())) {
				tns.put(tn, e.getValue());
			}
		}
		for (Entry<TrackedNucleus, Color3f> tn : tns.entrySet()) {
			colorMap.put(tn.getKey(), tn.getValue());
			if (showParents) {
				Set<TrackedNucleus> paps = getPaps(tn.getKey(), tns.keySet());
				for (TrackedNucleus tn2 : paps) {
					colorMap.put(tn2, tn.getValue());
				}
			}
			if (showChildren) {
				Set<TrackedNucleus> children = getChildren(tn.getKey(),
						tns.keySet());
				if (children != null) {
					for (TrackedNucleus tn2 : children) {
						colorMap.put(tn2, tn.getValue());
					}
				}
			}
		}
	}

	public float getTransparencyValue() {
		return transparencyValue;
	}

	public void setTransparencyValue(float transparencyValue) {
		this.transparencyValue = transparencyValue;
	}

	private void addIgnoreNames() {
		ignoreNames.add("polar1");
		ignoreNames.add("nill");
	}

	private void parseLineage(LineageMeasurement lm2) {
		Map<Integer, Set<Nucleus>> not = lm.getNucleiOverTime();
		nucleiOverTime = new SortedListMap<Integer, List<Nucleus>>(
				ComparatorFactory.getAscendingIntegerComparator());
		nucleusMap = new HashMap<Nucleus, TrackedNucleus>(
				2 * lm.nucleusMap.size());
		this.maxAP = Float.MIN_VALUE;
		this.minAP = Float.MAX_VALUE;
		if (transformationMatrix != null) {

			for (Entry<Integer, Set<Nucleus>> eNot : not.entrySet()) {
				List<Nucleus> newNucs = new ArrayList<Nucleus>(eNot.getValue()
						.size());

				for (Nucleus n : eNot.getValue()) {
					Nucleus copy = new Nucleus(n);
					Vector<Integer> adapted;
					adapted = GetAxes.transform(transformationMatrix,
							copy.getCoordinates(), orientationMode);
					maxAP = (float) Math.max(adapted.get(0), maxAP);
					minAP = (float) Math.min(adapted.get(0), minAP);
					copy.setCoordinates(adapted);
					newNucs.add(copy);
					nucleusMap.put(copy, lm.nucleusMap.get(n));

				}
				nucleiOverTime.put(eNot.getKey(), newNucs);
			}
		}

	}

	public List<Nucleus> getShownNuclei() {
		List<Nucleus> nuclei = nucleiOverTime.get(time);
		List<Nucleus> shown = new ArrayList<Nucleus>();
		for (Nucleus n : nuclei) {
			TrackedNucleus tn = nucleusMap.get(n);
			Vector<Integer> coordinate = transformCoordinates(n
					.getCoordinates());
			float rf = ((n.getRadius()) / (width / 2));
			if (!hideNuclei.contains(tn) && colorMap.containsKey(tn)
					&& !outSideSlice(coordinate, rf)) {
				Nucleus newN = new Nucleus(n);
				newN.setCoordinates(coordinate);
				shown.add(newN);
			}
		}
		return shown;
	}

	public float getMaxShownAPcoordinate() {
		float result = (float) (maxShownAPcoordinate - center.get(0));
		result /= (width / 2);
		return result;
	}

	public void setMaxShownAPcoordinate(float maxAPcoordinate) {
		this.maxShownAPcoordinate = maxAPcoordinate;
	}

	public float getMinShownAPcoordinate() {
		float result = (float) (minShownAPcoordinate - center.get(0));
		result /= (width / 2);
		return result;
	}

	public float getMaxAP() {
		return maxAP;
	}

	public float getMinAP() {
		return minAP;
	}

	public void setMinShownAPcoordinate(float minAPcoordinate) {
		this.minShownAPcoordinate = minAPcoordinate;
	}

	public Embryo3D(LineageMeasurement lm, Map<String, Color3f> subLineageColors) {
		this(lm, subLineageColors, ColorConstants.gray, 1, 0.8f,
				GetAxes.ORIENT4CELL);
	}

	private void initialize() {
		if (nucleiBG != null) {
			removeAllChildren();
			nucleiBG.detach();
			nucleiBG = null;
			// System.gc();
		}
		nucleiBG = getNucleiBranchGroup();
		nucleiBG.compile();
		addChild(nucleiBG);
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		// if (time >= 0 && time <= lm.getLastTimePoint()) {
		this.time = time;
		initialize();
		// }
	}

	public void nextTimePoint() {
		// if ((time + 1) <= lm.getLastTimePoint()) {
		time++;
		initialize();
		// }
	}

	public void previousTimePoint() {
		// if((time-1) > 0){
		time--;
		initialize();
		// }
	}

	private void getWidthAndHeight() {
		Map<Integer, Set<Nucleus>> nuclei = lm.getNucleiOverTime();
		this.width = 0f;
		float minx = Float.MAX_VALUE;
		float maxx = Float.MIN_VALUE;
		this.heigth = 0f;
		for (Set<Nucleus> ns : nuclei.values()) {
			for (Nucleus n : ns) {
				if (!ignoreNames.contains(n.getName())) {
					minx = (float) Math.min(n.getX() - 0.5 * n.getRadius(),
							minx);
					maxx = (float) Math.max(n.getX() + 0.5 * n.getRadius(),
							maxx);
					this.heigth = (float) Math.max(this.heigth,
							Math.abs(n.getY()) + 0.5 * n.getRadius());
				}
			}
		}
		this.width = maxx - minx;
	}

	/**
	 * temporary fix
	 * 
	 * @param color
	 */
	public void fixColor(Color color) {
		System.out.println("fixColor");
		Color3f c = new Color3f(color);
		fixedColor = c;
		initialize();
	}

	private Appearance setColor(Color3f color) {
		Color3f eColor = new Color3f(0.0f, 0.0f, 0.0f);
		Color3f sColor = color;
		Material m = new Material(eColor, eColor, sColor, sColor, 100.0f);
		m.setLightingEnable(true);
		Appearance app = new Appearance();
		app.setMaterial(m);
		return app;
	}

	private Appearance getLineageColor(TrackedNucleus tn) {
		Appearance app = null;
		if (tn != null && colorMap.containsKey(tn)) {
			app = setColor(colorMap.get(tn));
		} else {
			app = setColor(fixedColor);
		}
		return app;
	}

	/*
	 * private Appearance getLineageColor(String name) { Appearance app = null;
	 * if (name.indexOf("Z") >= 0) name = "P"; // patch for germ line
	 * 
	 * for (String key : subLineageColors.keySet()) { if (name.indexOf(key) >=
	 * 0) { app = setColor(subLineageColors.get(key)); break; } } return app; }
	 */
	private Vector<Integer> transformCoordinates(Vector<Integer> coor) {
		Vector<Integer> coordinate = new ArrayVector<Integer>(coor);
		coordinate.subtract(center);
		coordinate.divide(width / 2);
		coordinate.set(1, -coordinate.get(1)); // ?
		coordinate.set(2, -coordinate.get(2));// ?
		return coordinate;
	}

	private BranchGroup getNucleiBranchGroup() {
		BranchGroup nucleiBG = new BranchGroup();
		nucleiBG.setCapability(BranchGroup.ALLOW_DETACH);
		nucleiBG.setCapability(Group.ALLOW_CHILDREN_WRITE);
		List<Nucleus> nuclei = nucleiOverTime.get(time);
		if (nuclei != null && nuclei.size() > 0) {
			getCenter();
			float rf;
			for (Nucleus n : nuclei) {
				TrackedNucleus tn = nucleusMap.get(n);
				Vector<Integer> coordinate = transformCoordinates(n
						.getCoordinates());
				rf = ((n.getRadius()) / (width / 2));
				Appearance app = new Appearance();
				app = getLineageColor(tn);
				if (hideNuclei.contains(tn) || !colorMap.containsKey(tn)
						|| outSideSlice(coordinate, rf)) {

					TransparencyAttributes tran = new TransparencyAttributes(
							TransparencyAttributes.BLENDED, transparencyValue);
					if (hideNucleiBool) {
						tran.setTransparency(1f);
					}
					app.setTransparencyAttributes(tran);

				}
				nucleiBG.addChild(makeNamedSphere(n.getName(), coordinate, rf,
						app));
			}
			if (showTraces) {

				for (TrackedNucleus tn : colorMap.keySet()) {
					// if (!hideNuclei.contains(tn)) {
					Appearance app = new Appearance();
					app = getLineageColor(tn);
					getTrace(tn, traceStart, time, app, nucleiBG, traceDetail);
					// }
				}
				// nucleiBG.addChild(tg);
			}
			Map<TrackedNucleus, Nucleus> tns = new HashMap<TrackedNucleus, Nucleus>();
			for (Nucleus n : nuclei) {
				tns.put(nucleusMap.get(n), n);
			}
			if (!showSisterLines) {
				tns.keySet().retainAll(colorMap.keySet());
				tns.keySet().removeAll(hideSisterLines);
			}
			for (CellGroupCylinder cgc : cellGroupCylinders) {
				nucleiBG.addChild(cgc.getCylinderForTime(nuclei, nucleusMap,
						center, width));
			}
			Set<TrackedNucleus> taken = new HashSet<TrackedNucleus>();
			for (Map.Entry<TrackedNucleus, Nucleus> tn : tns.entrySet()) {
				TrackedNucleus sister = daughterMap.get(tn.getKey());
				if (sister != null && tns.containsKey(sister)
						&& !taken.contains(sister) && !taken.contains(tn)) {
					taken.add(sister);
					taken.add(tn.getKey());
					Appearance app = getLineageColor(tn.getKey());
					if (hideSisterLines.contains(tn.getKey())
							|| hideSisterLines.contains(sister)
							|| !colorMap.containsKey(tn.getKey())
							|| !colorMap.containsKey(sister)) {
						TransparencyAttributes tran = new TransparencyAttributes(
								TransparencyAttributes.BLENDED,
								transparencyValue);
						app.setTransparencyAttributes(tran);
					}
					TransformGroup tg = getCylinder(tn.getValue(),
							tns.get(sister), app);
					nucleiBG.addChild(tg);

				}
			}

		}
		return nucleiBG;
	}

	private boolean outSideSlice(Vector<Integer> coordinate, float radius) {

		if ((coordinate.get(0) - radius) < getMaxShownAPcoordinate()
				&& (coordinate.get(0) + radius) > getMinShownAPcoordinate())
			return false;
		else
			return true;
	}

	private void getTrace(TrackedNucleus tn, int start, int end,
			Appearance app, BranchGroup bg, int detail) {

		int actualStart = Math.max(tn.getFirstTimePoint(), start);
		int actualEnd = Math.min(tn.getLastTimePoint(), end);
		int i = actualStart;
		while (i < actualEnd) {
			Nucleus n1 = new Nucleus(tn.getNucleusForTimePoint(i));
			Vector<Integer> adapted = GetAxes.transform(transformationMatrix,
					n1.getCoordinates(), orientationMode);
			n1.setCoordinates(adapted);
			int currentEnd = Math.min(actualEnd, i + detail);
			Nucleus n2 = new Nucleus(tn.getNucleusForTimePoint(currentEnd));
			adapted = GetAxes.transform(transformationMatrix,
					n2.getCoordinates(), orientationMode);
			n2.setCoordinates(adapted);
			TransformGroup tg = getCylinder(n1, n2, app);
			if (tg != null)
				bg.addChild(tg);
			i = currentEnd;
		}
	}

	private TransformGroup getCylinder(Nucleus n, Nucleus sister, Appearance app) {
		return getCylinder(n.getCoordinates(), sister.getCoordinates(), app);
	}

	private TransformGroup getCylinder(Vector<Integer> n,
			Vector<Integer> sister, Appearance app) {
		Vector<Integer> v1 = transformCoordinates(n);
		Vector<Integer> v2 = transformCoordinates(sister);
		v2.subtract(v1);
		if (v2.norm() == 0)
			return null;
		else {
			Vector3f direction = new Vector3f((float) v2.get(0),
					(float) v2.get(1), (float) v2.get(2));
			Vector3f axis = new Vector3f();
			axis.cross(yAxis, direction);
			axis.normalize();
			// When the intended direction is a point on the yAxis,
			// rotate on x
			if (Float.isNaN(axis.x) && Float.isNaN(axis.y)
					&& Float.isNaN(axis.z)) {
				axis.x = 1f;
				axis.y = 0f;
				axis.z = 0f;
			}
			// Compute the quaternion transformations
			final float angleX = yAxis.angle(direction);
			final float a = axis.x * (float) Math.sin(angleX / 2f);
			final float b = axis.y * (float) Math.sin(angleX / 2f);
			final float c = axis.z * (float) Math.sin(angleX / 2f);
			final float d = (float) Math.cos(angleX / 2f);

			Transform3D t3d = new Transform3D();
			Quat4f quat = new Quat4f(a, b, c, d);
			t3d.set(quat);

			Cylinder cy = new Cylinder(cylinderDiameter, (float) v2.norm(), app);
			v2.multiply(0.5);
			v1.add(v2);

			Transform3D translate = new Transform3D();
			translate.set(new Vector3f((float) v1.get(0), (float) v1.get(1),
					(float) v1.get(2)));
			translate.mul(t3d);

			TransformGroup tg = new TransformGroup(translate);
			tg.addChild(cy);
			return tg;
		}
	}

	private Node makeNamedSphere(String identity, Vector<Integer> coordinate,
			float rf, Appearance appearance) {
		Transform3D translate = new Transform3D();
		translate.set(new Vector3f((float) coordinate.get(0),
				(float) coordinate.get(1), (float) coordinate.get(2)));
		NamedSphere sph = new NamedSphere(identity, rf, appearance);
		TransformGroup tg = new TransformGroup(translate);
		tg.addChild(sph);
		return tg;
	}

	private void getCenter() {
		if (center == null) {
			center = new ArrayVector<Integer>(IntegerSpace.threeD);

			int max = 0;
			int i = Lineage2StatisticsScript.getCorrection(lm);
			int last = lm.getLastTimePoint() - 1;

			List<Vector<Integer>> centervectors = new ArrayList<Vector<Integer>>(
					last);
			while (i <= last && max <= nucleiOverTime.get(i).size() + 1) {
				List<Nucleus> n = nucleiOverTime.get(i);
				// System.out.println(i);
				i++;
				max = Math.max(max, n.size());
				List<Vector<Integer>> vectors = new ArrayList<Vector<Integer>>(
						n.size());
				for (Nucleus nuc : n) {
					if (!ignoreNames.contains(nuc.getName())) {
						vectors.add(nuc.getCoordinates());
					} else {
						System.out.println(nuc.getName());
					}
				}
				Vector<Integer> t_center = meanVector(vectors);
				centervectors.add(t_center);
			}
			center = meanVector(centervectors);
			// center.dumpNonzeros();

		}
	}

	private Vector<Integer> meanVector(List<Vector<Integer>> vectors) {
		Vector<Integer> t_center = new ArrayVector<Integer>(IntegerSpace.threeD);
		for (Vector<Integer> v : vectors) {
			for (int i = 0; i < 3; i++) {
				t_center.set(i, t_center.get(i) + v.get(i));
			}
		}
		t_center.divide(vectors.size());
		return t_center;
	}

	public void removeSubLineageColors(String name) {
		subLineageColors.remove(name);
		initializeColor();
		initialize();
	}

	public void setShowSisterLines(boolean showSisterLines) {
		this.showSisterLines = showSisterLines;
		System.out.println("Sister lines " + showSisterLines);
		initialize();
	}

	public void hideNuclei(Collection<String> names) {
		hiddenNucleiNames.addAll(names);
		initializeHidden();
		initialize();
	}

	public void hideNucleus(String name) {
		hiddenNucleiNames.add(name);
		initializeHidden();
		initialize();
	}

	public void showNucleus(String name) {
		hiddenNucleiNames.remove(name);
		initializeHidden();
		initialize();
	}

	public void hideSisterLines(Collection<String> names) {
		hideSisterLinesNames.addAll(names);
		initializeHidden();
		initialize();
	}

	public void hideSisterLines(String name) {
		hideSisterLinesNames.add(name);
		initializeHidden();
		initialize();
	}

	public void showSisterLines(String name) {
		hideSisterLinesNames.remove(name);
		initializeHidden();
		initialize();
	}

	public void hideNuclei() {
		this.hideNucleiBool = true;
		initialize();
	}

	public void showNuclei() {
		this.hideNucleiBool = false;
		initialize();
	}

	public void setSliceBoundaries(Integer newSliceCenter, Integer sliceBreadth) {
		if (sliceBreadth >= 100) {
			setMinShownAPcoordinate(Float.NEGATIVE_INFINITY);
			setMaxShownAPcoordinate(Float.POSITIVE_INFINITY);
		} else {
			float length = maxAP - minAP;
			float sliceBreadthReal = length * (sliceBreadth / 100f);
			float sliceCenter = minAP + length * (newSliceCenter / 100f);
			setMinShownAPcoordinate((sliceCenter - 0.5f * sliceBreadthReal));
			setMaxShownAPcoordinate(sliceCenter + 0.5f * sliceBreadthReal);
		}
	}

	public int getTraceStart() {
		return traceStart;
	}

	public void setTraceStart(int traceStart) {
		this.traceStart = traceStart;
		initialize();
	}

	public boolean isShowTraces() {
		return showTraces;
	}

	public void setShowTraces(boolean showTraces, int detail) {
		this.showTraces = showTraces;
		this.traceDetail = detail;
		initialize();
	}

	public boolean isShowParents() {
		return showParents;
	}

	public void setShowParents(boolean showParents) {
		this.showParents = showParents;
		initializeColor();
		initialize();
	}

	public boolean isShowChildren() {
		return showChildren;
	}

	public Map<String, Color> getColorMap() {
		Map<String, Color> colors = new HashMap<String, Color>();
		for (Entry<TrackedNucleus, Color3f> e : colorMap.entrySet()) {
			colors.put(e.getKey().toString(), e.getValue().get());
		}
		return colors;
	}

	public void setShowChildren(boolean showChildren) {
		this.showChildren = showChildren;
		initializeColor();
		initializeHidden();
		initialize();
	}

	public void addCellGroupCylinder(Pattern text1, Pattern text2, Color color) {
		List<TrackedNucleus> tn1 = lm.retrieveTrackedNucleusByName(text1);
		List<TrackedNucleus> tn2 = lm.retrieveTrackedNucleusByName(text2);
		if (tn1.size() > 0 && tn2.size() > 0) {
			CellGroupCylinder cgc = new CellGroupCylinder(lm, tn1, tn2);
			cgc.setColor(color);
			cellGroupCylinders.add(cgc);
		} else if (tn1.size() > 0 && text2.matcher("center").matches()) {
			CellGroupCylinder cgc = new CellGroupCylinder(lm, tn1,
					CellGroupCylinder.CENTER);
			cgc.setColor(color);
			cellGroupCylinders.add(cgc);
		}
		initialize();
	}

	public void removeCellGroupCylinders() {
		cellGroupCylinders.clear();
		initialize();
	}

}
