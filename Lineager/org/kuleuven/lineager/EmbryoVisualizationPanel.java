package org.kuleuven.lineager;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Group;
import javax.media.j3d.Light;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.kuleuven.lineagetree.Lineage2StatisticsScript;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.math.matrix.Matrix;
import org.kuleuven.utilities.ImageUtilities;
import org.kuleuven.utilities.StringUtilities;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class EmbryoVisualizationPanel extends Canvas3D implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2509900737511496421L;

	private double heigth = 120;
	private double width = 270;
	private SimpleUniverse iUniverse;
	List<ActionListener> actionlisteners = new ArrayList<ActionListener>();
	private Map<LineageMeasurement, Integer> corrections = new HashMap<LineageMeasurement, Integer>();
	private boolean showChildren;
	private boolean showParents;
	private boolean showSisterLines;
	private boolean trackNuclei;

	private Integer time = 1;
	private Integer trackingStart = 1;
	private int trackingDetail = 1;

	private Map<LineageMeasurement, Color> lms = new HashMap<LineageMeasurement, Color>();
	private Map<LineageMeasurement, Map<String, Color>> sublineageColors = new HashMap<LineageMeasurement, Map<String, Color>>();
	private BranchGroup masterBranchGroup;
	private PickCanvas iPickCanvas;

	Map<LineageMeasurement, Embryo3D> embryos = new HashMap<LineageMeasurement, Embryo3D>();
	private Set<LineageMeasurement> hiddenLineages = new HashSet<LineageMeasurement>();// horrible
																						// duplication
	private Set<LineageMeasurement> flipped = new HashSet<LineageMeasurement>();// horribly
																				// ugly
	private Set<String> hiddenNuclei = new HashSet<String>();
	private Set<String> hiddenSisterLines = new HashSet<String>();
	private Transform3D overallTransform;
	private Integer sliceCenter = 50;
	private Integer sliceBreadth = 100;

	private TransformGroup overallTransformGroup;
	private Vector3f viewingPlatformTransform;
	private String selectedNucleusNames = "";
	private boolean hideNotHighlightedNuclei = false;
	private int embryoOrientationMode = 1;

	private float cylinderDiameter = 0.01f;;

	private float cylinderLength = 1.2f;

	public int getEmbryoOrientationMode() {
		return embryoOrientationMode;
	}

	private ArrayList<CellGroupDefinition> cellGroupLines = new ArrayList<CellGroupDefinition>();

	private boolean showAxes = false;

	private BranchGroup axes;



	public void setCustomEmbryoOrientationMode(int embryoOrientationMode) {
		this.embryoOrientationMode = embryoOrientationMode;
		System.out.println("reorienting!");
		for (Entry<LineageMeasurement, Embryo3D> e : embryos.entrySet()) {
			if (e.getValue() != null) {
				Matrix<Integer, Integer> m = GetAxes.getAlignmentByMS(e.getKey(),
								getTime() + corrections.get(e.getKey()) - 1);
				e.getValue().setTransformationMatrix(m);
			}
		}
		updateSceneGraph();
	}

	public void setEmbryoOrientationMode(int embryoOrientationMode) {
		this.embryoOrientationMode = embryoOrientationMode;
		for (Entry<LineageMeasurement, Embryo3D> e : embryos.entrySet()) {
			if (e.getValue() != null) {
				e.getValue().setEmbryoOrientationMode(embryoOrientationMode);
			}
		}
		updateSceneGraph();
	}

	public static String SELECTIONCHANGED = "new selected nuclei in EVP";

	// private TransformGroup iTranslateGroup;
	// private Transform3D iTranslate = new Transform3D();;
	// private Matrix4d iMatrix = new Matrix4d();

	public EmbryoVisualizationPanel() {
		super(SimpleUniverse.getPreferredConfiguration());

		// defaultSubLineageColors();
		setSize((int) Math.floor(width), (int) Math.floor(heigth));
		iUniverse = new SimpleUniverse(this);
		// iUniverse.getViewingPlatform().setNominalViewingTransform();

		viewingPlatformTransform = new Vector3f(0, 0, 2.5f);
		setViewingPlatformTransform();
		addMouseListener(this);
		// iTranslateGroup = viewingPlatform.getViewPlatformTransform();
		// iTranslateGroup.getTransform(iTranslate);
		// iTranslate.get(iMatrix);
	}

	public boolean isHideNotHighlightedNuclei() {
		return hideNotHighlightedNuclei;
	}

	public void setHideNotHighlightedNuclei(boolean hideNotHighlightedNuclei) {
		this.hideNotHighlightedNuclei = hideNotHighlightedNuclei;
	}

	public void addActionListener(ActionListener al) {
		this.actionlisteners.add(al);
	}

	public Map<LineageMeasurement, Integer> getCorrections() {
		return corrections;
	}

	public void setCorrections(Map<LineageMeasurement, Integer> corrections) {
		this.corrections = corrections;
	}

	public void setShowChildren(boolean showChildren) {
		this.showChildren = showChildren;
		updateShowOptions();

	}

	/*
	 * a hack. Improve in due time.
	 */
	public List<Nucleus> getShownNuclei() {
		List<Nucleus> n = new ArrayList<Nucleus>();
		for (Embryo3D e : embryos.values()) {
			n.addAll(e.getShownNuclei());
		}
		return n;
	}

	/*
	 * a hack. Improve in due time.
	 */
	public Map<Nucleus, Color> getShownNucleiColors() {
		Map<Nucleus, Color> colors = new HashMap<Nucleus, Color>();
		for (Embryo3D e : embryos.values()) {
			Map<String, Color> map = e.getColorMap();
			List<Nucleus> n = e.getShownNuclei();
			for (Nucleus nuc : n) {
				colors.put(nuc, map.get(nuc.getName()));
			}
		}
		return colors;
	}

	public void setShowSisterLines(boolean selected) {
		this.showSisterLines = selected;
		updateShowOptions();
	}

	public boolean isShowSisterLines() {
		return showSisterLines;
	}

	public void setShowParents(boolean showParents) {
		this.showParents = showParents;
		updateShowOptions();
	}

	private void updateShowOptions() {
		for (Entry<LineageMeasurement, Embryo3D> e : embryos.entrySet()) {
			e.getValue().setShowChildren(showChildren);
			e.getValue().setShowParents(showParents);
			e.getValue().setShowSisterLines(showSisterLines);
		}

	}

	public TransformGroup getOverallTransformGroup() {
		return overallTransformGroup;
	}

	public String getSelectedNucleusNames() {
		return selectedNucleusNames;
	}

	private void setViewingPlatformTransform() {
		ViewingPlatform viewingPlatform = iUniverse.getViewingPlatform();
		Transform3D transform = new Transform3D();
		TransformGroup transformGroup = viewingPlatform
				.getMultiTransformGroup().getTransformGroup(0);
		// transformGroup.getTransform(transform);
		transform.setTranslation(viewingPlatformTransform);
		transformGroup.setTransform(transform);

	}

	public void movePlatformOut() {
		viewingPlatformTransform.z = viewingPlatformTransform.z + 0.1f;
		setViewingPlatformTransform();
	}

	public void movePlatformIn() {
		viewingPlatformTransform.z = viewingPlatformTransform.z - 0.1f;
		setViewingPlatformTransform();
	}

	public void movePlatformRight() {
		viewingPlatformTransform.x = viewingPlatformTransform.x + 0.1f;
		setViewingPlatformTransform();
	}

	public void movePlatformLeft() {
		viewingPlatformTransform.x = viewingPlatformTransform.x - 0.1f;
		setViewingPlatformTransform();
	}

	public void movePlatformUp() {
		viewingPlatformTransform.y = viewingPlatformTransform.y - 0.1f;
		setViewingPlatformTransform();
	}

	public void movePlatformDown() {
		viewingPlatformTransform.y = viewingPlatformTransform.y + 0.1f;
		setViewingPlatformTransform();
	}

	public void addLineageMeasurement(LineageMeasurement lm) {
		int i = lms.size();
		String colorName = ImageUtilities.COLORS[i%ImageUtilities.COLORS.length];
		i++;
		Color color = ColorConstants.name2Color.get(colorName).get();
		lms.put(lm, color);
		System.out.println(lm.getName() + "\t" + colorName);
		Map<String, Color> sublineages = new HashMap<String, Color>();
		if (sublineageColors.size() > 0) {
			Map<String, Color> sublineageExample = sublineageColors.values()
					.iterator().next();
			for (Entry<String, Color> e : sublineageExample.entrySet()) {
				sublineages.put(e.getKey(), color);
			}
		}
		sublineageColors.put(lm, sublineages);
		embryos.put(lm, null);
		corrections.put(lm, Lineage2StatisticsScript.getCorrection(lm));
		initialize();
		setViewingPlatformTransform();
	}

	public void update() {
		for (Entry<LineageMeasurement, Embryo3D> e : embryos.entrySet()) {
			e.setValue(null);
			corrections.put(e.getKey(),
					Lineage2StatisticsScript.getCorrection(e.getKey()));
		}
		initialize();
		setViewingPlatformTransform();
	}

	public void removeLineageMeasurement(LineageMeasurement lm) {
		if (embryos.containsKey(lm)) {
			hiddenLineages.remove(lm);
			lms.remove(lm);
			sublineageColors.remove(lm);
			embryos.remove(lm);
			corrections.remove(lm);
			if (masterBranchGroup != null) {
				masterBranchGroup.detach();
			}
			masterBranchGroup = null;
			overallTransform = null;
			overallTransformGroup = null;
			for (Entry<LineageMeasurement, Embryo3D> e : embryos.entrySet()) {
				e.setValue(null);
			}
			initialize();
			setViewingPlatformTransform();
		}
	}

	public void initialize() {
		if (masterBranchGroup != null)
			masterBranchGroup.detach();
		masterBranchGroup = updateSceneGraph();
		iUniverse.addBranchGraph(masterBranchGroup);

		iPickCanvas = new PickCanvas(this, masterBranchGroup);
		iPickCanvas.setMode(PickTool.BOUNDS);
		iPickCanvas.setTolerance(1f);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		iPickCanvas.setShapeLocation(e);
		PickResult[] results = iPickCanvas.pickAll();
		selectedNucleusNames = getPickedNucleusNames(results);
		for (ActionListener al : actionlisteners) {
			al.actionPerformed(new ActionEvent(this, 1, SELECTIONCHANGED));
		}
		// to be extended
	}

	public void setTimePointForLineage(LineageMeasurement lm, int time) {
		Embryo3D e = embryos.get(lm);
		if (e != null) {
			e.setTime(time);
		}
	}

	public int getTimePointForLineage(LineageMeasurement lm) {
		Embryo3D e = embryos.get(lm);
		if (e != null) {
			return e.getTime();
		}
		return Integer.MAX_VALUE;
	}

	public void moveLineageToNextTimePoint(LineageMeasurement lm) {
		Embryo3D e = embryos.get(lm);
		if (e != null) {
			e.nextTimePoint();
		}
	}

	public void moveLineageToPreviousTimePoint(LineageMeasurement lm) {
		Embryo3D e = embryos.get(lm);
		if (e != null) {
			e.previousTimePoint();
		}
	}

	public BranchGroup updateSceneGraph() {

		if (masterBranchGroup == null) {
			BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0,
					0.0), 90.0);
			masterBranchGroup = new BranchGroup();
			masterBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
			// Color3f bgColor = new Color3f(0.3f, 0.3f, 0.3f); // lite blue
			Color3f bgColor = new Color3f(1f, 1f, 1f); // lite blue
			Color3f lColor1 = new Color3f(1f, 1f, 1f);
			Vector3d lPos1 = new Vector3d(0.0, 0.0, 2.0);
			Vector3f lDirect1 = new Vector3f(lPos1);
			lDirect1.negate();
			Light lgt1 = new DirectionalLight(lColor1, lDirect1);
			lgt1.setInfluencingBounds(bounds);
			masterBranchGroup.addChild(lgt1);
			Background bg = new Background(bgColor);
			bg.setApplicationBounds(bounds);
			masterBranchGroup.addChild(bg);

		}

		if (overallTransform == null)
			overallTransform = new Transform3D();
		if (overallTransformGroup == null) {
			overallTransformGroup = new TransformGroup(overallTransform);
			overallTransformGroup
					.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			overallTransformGroup
					.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			overallTransformGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
			overallTransformGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
			masterBranchGroup.addChild(overallTransformGroup);

			MouseRotate myMouseRotate = new MouseRotate();
			myMouseRotate.setTransformGroup(overallTransformGroup);
			myMouseRotate.setSchedulingBounds(new BoundingSphere());
			masterBranchGroup.addChild(myMouseRotate);
			for (Entry<LineageMeasurement, Embryo3D> e : embryos.entrySet()) {
				Embryo3D embryo = e.getValue();
				if (embryo != null && !hiddenLineages.contains(e.getKey())) {
					overallTransformGroup.addChild(embryo);
				}
			}
		}
		if (showAxes) {
			if (axes != null)
				axes.detach();

			axes = new BranchGroup();
			axes.setCapability(BranchGroup.ALLOW_DETACH);

			axes.addChild(getDVAxes(getAxesAppearance()));
			axes.addChild(getLRAxes(getAxesAppearance()));
			axes.addChild(getAPAxes(getAxesAppearance()));
			overallTransformGroup.addChild(axes);
		}

		for (Entry<LineageMeasurement, Embryo3D> e : embryos.entrySet()) {
			Embryo3D embryo = e.getValue();
			if (embryo == null && !hiddenLineages.contains(e.getKey())) {
				if (hideNotHighlightedNuclei) {
					embryo = new Embryo3D(e.getKey(),
							new HashMap<String, Color3f>(),
							ColorConstants.gray, 1, 1.0f,
							getEmbryoOrientationMode());
				} else {
					embryo = new Embryo3D(e.getKey(),
							new HashMap<String, Color3f>(),
							ColorConstants.gray, 1, 0.8f,
							getEmbryoOrientationMode());
				}
				e.setValue(embryo);
				Map<String, Color3f> c = new HashMap<String, Color3f>();
				for (Map.Entry<String, Color> ec : sublineageColors.get(
						e.getKey()).entrySet()) {
					Color3f col = new Color3f(ec.getValue());
					c.put(ec.getKey(), col);
				}
				embryo.setSubLineageColors(c);

				embryo.setShowSisterLines(showSisterLines);

				embryo.setShowChildren(showChildren);
				embryo.setShowParents(showParents);
				embryo.setShowTraces(isTrackNuclei(), getTrackingDetail());
				int correction = corrections.get(e.getKey());
				embryo.setTraceStart(trackingStart + correction - 1);
				embryo.fixColor(lms.get(e.getKey()));

				embryo.hideNuclei(hiddenNuclei);
				embryo.hideSisterLines(hiddenSisterLines);
				embryo.setSliceBoundaries(sliceCenter, sliceBreadth);

				for (CellGroupDefinition cgd : cellGroupLines) {
					embryo.addCellGroupCylinder(Pattern.compile(cgd.name1,Pattern.CASE_INSENSITIVE), Pattern.compile(cgd.name2,Pattern.CASE_INSENSITIVE), cgd.color);
				}
				overallTransformGroup.addChild(embryo);

			} else {
			}

		}
		// updateColors();
		setTime(time);
		return masterBranchGroup;
	}

	private Appearance getAxesAppearance() {
		Color3f sColor = new Color3f(0.0f, 0.0f, 0.0f);
		Color3f eColor = new Color3f(1.0f, 1.0f, 1.0f);
		Material m = new Material(eColor, eColor, sColor, sColor, 100.0f);
		m.setLightingEnable(true);
		Appearance app = new Appearance();
		app.setMaterial(m);
		return app;
	}

	private TransformGroup getDVAxes(Appearance app) {
		Vector3f direction = new Vector3f(0, 1, 0);
		Transform3D t3d = new Transform3D();
		Quat4f quat = new Quat4f(0, 0, 0, 1);
		t3d.set(quat);

		Cylinder cy = new Cylinder(cylinderDiameter, cylinderLength, app);
		Transform3D translate = new Transform3D();
		translate.set(new Vector3f(0, 0, 0));
		translate.mul(t3d);

		TransformGroup tg = new TransformGroup(translate);
		tg.addChild(cy);
		return tg;
	}

	private TransformGroup getAPAxes(Appearance app) {
		Vector3f direction = new Vector3f(1, 0, 0);
		Vector3f axis = new Vector3f();
		axis.x = 0f;
		axis.y = 1f;
		axis.z = 0f;
		axis.normalize();
		// When the intended direction is a point on the yAxis,
		// rotate on x
		// Compute the quaternion transformations
		final float angleX = axis.angle(direction);
		final float a = axis.x * (float) Math.sin(angleX / 2f);
		final float b = axis.y * (float) Math.sin(angleX / 2f);
		final float c = axis.z * (float) Math.sin(angleX / 2f);
		final float d = (float) Math.cos(angleX / 2f);
		System.out.println(a + "\t" + b + "\t" + c + "\t" + d);
		Transform3D t3d = new Transform3D();
		Quat4f quat = new Quat4f(0, 0, 1, 1);
		t3d.set(quat);

		Cylinder cy = new Cylinder(cylinderDiameter, 2 * cylinderLength, app);
		Transform3D translate = new Transform3D();
		translate.set(new Vector3f(0, 0, 0));
		translate.mul(t3d);

		TransformGroup tg = new TransformGroup(translate);
		tg.addChild(cy);
		return tg;
	}

	private TransformGroup getLRAxes(Appearance app) {
		Vector3f direction = new Vector3f(0, 1, 0);
		Transform3D t3d = new Transform3D();
		Quat4f quat = new Quat4f(1, 0, 0, 1);
		t3d.set(quat);

		Cylinder cy = new Cylinder(cylinderDiameter, cylinderLength, app);
		Transform3D translate = new Transform3D();
		translate.set(new Vector3f(0, 0, 0));
		translate.mul(t3d);

		TransformGroup tg = new TransformGroup(translate);
		tg.addChild(cy);
		return tg;
	}

	private String getPickedNucleusNames(PickResult[] results) {
		String s = "none";
		List<String> names = new ArrayList<String>();
		// v.add(0, s);
		if (results != null) {
			for (int i = (results.length - 1); i >= 0; i--) {
				Primitive p = (Primitive) results[i]
						.getNode(PickResult.PRIMITIVE);

				if (p != null) {
					String pname = p.getClass().getName();
					if (pname.indexOf("NamedSphere") >= 0) {
						s = ((NamedSphere) p).iName;
						names.add(s);
					}
				}
			}
		}
		if (names.size() > 0) {
			s = StringUtilities.join(names, ", ");
		}
		return s;
	}

	public Map<LineageMeasurement, Map<String, Color>> getSubLineagePerMeasurementColors() {
		Map<LineageMeasurement, Map<String, Color>> result = new HashMap<LineageMeasurement, Map<String, Color>>();
		for (Entry<LineageMeasurement, Embryo3D> e : embryos.entrySet()) {
			Map<String, Color> slc = new HashMap<String, Color>();
			for (Entry<String, Color3f> e2 : e.getValue().getSubLineageColors()
					.entrySet()) {
				slc.put(e2.getKey(), e2.getValue().get());
			}
			result.put(e.getKey(), slc);
		}
		return result;
	}

	private void updateColors() {
		for (LineageMeasurement lm : lms.keySet()) {
			if (!hiddenLineages.contains(lm)) {
				setColorForLineage(lm, lms.get(lm));
				if (sublineageColors.containsKey(lm)) {
					setEmbryoColors(lm, sublineageColors.get(lm));
				}
			}
		}
		// update();
	}

	private void setEmbryoColors(LineageMeasurement lm, Map<String, Color> map) {
		Embryo3D e = embryos.get(lm);
		if (e != null) {
			Map<String, Color3f> c = new HashMap<String, Color3f>();
			for (Map.Entry<String, Color> ec : map.entrySet()) {
				Color3f col = new Color3f(ec.getValue());
				c.put(ec.getKey(), col);
			}
			e.setSubLineageColors(c);
		}
	}

	public void setSubLineageColorForLineage(LineageMeasurement lm,
			String name, Color color) {
		Map<String, Color> map = sublineageColors.get(lm);
		if (map != null) {
			map.put(name, color);
		}
		updateColors();
	}

	private void setEmbryoSublineageColors(LineageMeasurement lm, String name,
			Color color) {
		Embryo3D e = embryos.get(lm);
		Color3f col = new Color3f(color);
		if (e != null) {
			Map<String, Color3f> c = e.getSubLineageColors();
			c.put(name, col);
			e.setSubLineageColors(c);
		}
	}

	public void setColorForLineage(LineageMeasurement value, Color color) {
		if (embryos.containsKey(value))
			embryos.get(value).fixColor(color);
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
		for (Entry<LineageMeasurement, Embryo3D> e : embryos.entrySet()) {
			int correction = corrections.get(e.getKey());
			Embryo3D em = e.getValue();
			if (em != null) {
				e.getValue().setTime(time + correction - 1);
			}
		}
	}

	public void removeSubLineageColor(String name) {
		for (Entry<LineageMeasurement, Map<String, Color>> entry : sublineageColors
				.entrySet()) {
			entry.getValue().remove(name);
		}
		for (Embryo3D e : embryos.values()) {
			e.removeSubLineageColors(name);
		}
		hiddenNuclei.remove(name);
		updateSceneGraph();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// mouseClicked(e);
	}

	public void hideNotHighlightedNuclei(boolean hide) {
		this.hideNotHighlightedNuclei = hide;
		for (Embryo3D e : embryos.values()) {

			if (hide)
				e.hideNuclei();
			// e.setTransparencyValue(1f);
			else {
				e.showNuclei();
				// e.setTransparencyValue(0.8f);
			}
		}
		updateSceneGraph();
	}

	public void setLineageColor(LineageMeasurement lm, Color color) {
		if (lms.containsKey(lm)) {
			lms.put(lm, color);
		}
		updateColors();
	}

	public void hideLineageMeasurement(LineageMeasurement lm) {
		if (masterBranchGroup != null)
			masterBranchGroup.detach();
		masterBranchGroup = null;
		hiddenLineages.add(lm);

		for (Entry<LineageMeasurement, Embryo3D> e : embryos.entrySet()) {
			overallTransformGroup.removeChild(e.getValue());
		}
		// embryos.put(lm, null);
		embryos.remove(lm);
		overallTransformGroup = null;

		initialize();
	}

	public void showLineage(LineageMeasurement lm) {
		hiddenLineages.remove(lm);
		embryos.put(lm, null);
		corrections.put(lm, Lineage2StatisticsScript.getCorrection(lm));
		initialize();
		setViewingPlatformTransform();
	}

	public void toggleShowNuclei(String name) {
		if (hiddenNuclei.contains(name)) {
			hiddenNuclei.remove(name);
			for (Embryo3D e : embryos.values()) {
				e.showNucleus(name);
			}
		} else {
			hiddenNuclei.add(name);
			for (Embryo3D e : embryos.values()) {
				e.hideNucleus(name);
			}
		}

	}

	public void toggleSublineageSisterLines(String name) {
		if (hiddenSisterLines.contains(name)) {
			hiddenSisterLines.remove(name);
			for (Embryo3D e : embryos.values()) {
				e.showSisterLines(name);
			}
		} else {
			hiddenSisterLines.add(name);
			for (Embryo3D e : embryos.values()) {
				e.hideSisterLines(name);
			}
		}

	}

	public Set<String> gethiddenSisterLines() {

		return hiddenSisterLines;
	}

	public boolean isTrackNuclei() {
		return trackNuclei;
	}

	public void setTrackNuclei(boolean trackNuclei) {
		this.trackNuclei = trackNuclei;
		for (Embryo3D e : embryos.values()) {
			if (e != null)
				e.setShowTraces(trackNuclei, getTrackingDetail());
		}
	}

	public Integer getTrackingStart() {
		return trackingStart;
	}

	public void setTrackingStart(Integer trackingStart) {
		this.trackingStart = trackingStart;
		for (Map.Entry<LineageMeasurement, Embryo3D> e : embryos.entrySet()) {
			int correction = corrections.get(e.getKey());
			if (e.getValue() != null) {
				e.getValue().setTraceStart(trackingStart + correction - 1);
			}
		}
	}

	public void setSliceBoundaries(Integer newSliceCenter, Integer sliceBreadth) {
		this.sliceCenter = newSliceCenter;
		this.sliceBreadth = sliceBreadth;
		for (Embryo3D e : embryos.values()) {
			e.setSliceBoundaries(newSliceCenter, sliceBreadth);
		}

		updateSceneGraph();
	}

	public Set<LineageMeasurement> getHiddenLineages() {
		return hiddenLineages;
	}

	public void setHiddenLineages(Set<LineageMeasurement> hiddenLineages) {
		this.hiddenLineages = hiddenLineages;
	}

	public Set<String> getHiddenNuclei() {
		return hiddenNuclei;
	}

	public void setHiddenNuclei(Set<String> hiddenNuclei) {
		this.hiddenNuclei = hiddenNuclei;
	}

	public Map<LineageMeasurement, Color> getLms() {
		return lms;
	}

	public void setLms(Map<LineageMeasurement, Color> lms) {
		this.lms = lms;
	}

	public Map<LineageMeasurement, Map<String, Color>> getSublineageColors() {
		return sublineageColors;
	}

	public void setSublineageColors(
			Map<LineageMeasurement, Map<String, Color>> sublineageColors) {
		this.sublineageColors = sublineageColors;
		updateColors();
	}

	public Transform3D getOverallTransform() {
		return overallTransform;
	}

	public int getTrackingDetail() {
		return trackingDetail;
	}

	public void setTrackingDetail(int trackingDetail) {
		this.trackingDetail = trackingDetail;
		for (Map.Entry<LineageMeasurement, Embryo3D> e : embryos.entrySet()) {

			if (e.getValue() != null) {
				e.getValue().setShowTraces(trackNuclei, trackingDetail);
			}
		}
	}

	private class CellGroupDefinition {
		String name1;
		String name2;
		Color color;

		public CellGroupDefinition(String name1, String name2, Color color) {
			this.name1 = name1;
			this.name2 = name2;
			this.color = color;
		}
	}

	public void setCellGroupLine(String text, String text2, Color color) {
		cellGroupLines.add(new CellGroupDefinition(text, text2, color));
		for (Embryo3D e : embryos.values()) {
			e.addCellGroupCylinder(Pattern.compile(text,Pattern.CASE_INSENSITIVE), Pattern.compile(text2,Pattern.CASE_INSENSITIVE), color);
		}
	}

	public void toggleShowAxis() {
		showAxes = !showAxes;
		if (!showAxes) {
			if (masterBranchGroup != null)
				masterBranchGroup.detach();
			masterBranchGroup = null;
			if (axes != null) {
				axes.detach();
				axes = null;
			}

			for (Entry<LineageMeasurement, Embryo3D> e : embryos.entrySet()) {
				overallTransformGroup.removeChild(e.getValue());
			}
			overallTransformGroup = null;

			initialize();

		} else {
			updateSceneGraph();
		}
	}

	public void removeCellGroupLines() {
		cellGroupLines.clear();
		for (Embryo3D e : embryos.values()) {
			e.removeCellGroupCylinders();
		}

	}
}
