package org.kuleuven.lineager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.math.space.Space;
import org.kuleuven.math.vector.ArrayVector;
import org.kuleuven.math.vector.Vector;

import com.sun.j3d.utils.geometry.Cylinder;

public class CellGroupCylinder {

	LineageMeasurement lm;
	Set<TrackedNucleus> group1;
	Set<TrackedNucleus> group2;
	float cylinderDiameter = 0.01f;
	Vector3f yAxis = new Vector3f(0f, 1f, 0f);
	private Color3f color = new Color3f(1, 0, 0);
	static final public int CENTER = 0;
	private boolean centerMode = false;

	public CellGroupCylinder(LineageMeasurement lm, List<TrackedNucleus> tn1,
			List<TrackedNucleus> tn2) {
		this.lm = lm;
		group1 = new HashSet<TrackedNucleus>();

		for (TrackedNucleus tn : tn1) {
			org.kuleuven.collections.Node<TrackedNucleus> node1 = lm.lineage
					.getNode(tn);
			group1.add(tn);
			if (node1 != null) {
				group1.addAll(node1.getAllChildrenBelowNode());
			}

		}
		group2 = new HashSet<TrackedNucleus>();
		for (TrackedNucleus tn : tn2) {
			org.kuleuven.collections.Node<TrackedNucleus> node2 = lm.lineage
					.getNode(tn);
			group2.add(tn);
			if (node2 != null) {
				group2.addAll(node2.getAllChildrenBelowNode());
			}
		}
	}

	public CellGroupCylinder(LineageMeasurement lm, List<TrackedNucleus> tn1,
			int option) {
		this.lm = lm;
		group1 = new HashSet<TrackedNucleus>();
		for (TrackedNucleus tn : tn1) {
			org.kuleuven.collections.Node<TrackedNucleus> node1 = lm.lineage
					.getNode(tn);
			group1.add(tn);
			if (node1 != null) {
				group1.addAll(node1.getAllChildrenBelowNode());
			}
		}
		if (option == CENTER) {
			centerMode = true;
		} else {
			group2 = group1;
		}
	}

	public void setColor(Color color) {

		this.color = new Color3f(color);
	}

	public TransformGroup getCylinderForTime(List<Nucleus> nucs,
			Map<Nucleus, TrackedNucleus> nucleusMap, Vector<Integer> center,
			float width) {
		List<Vector<Integer>> nucGroup1 = new ArrayList<Vector<Integer>>();
		List<Vector<Integer>> nucGroup2 = new ArrayList<Vector<Integer>>();
		for (Nucleus nuc : nucs) {
			TrackedNucleus tn = nucleusMap.get(nuc);
			if (group1.contains(tn)) {
				nucGroup1.add(nuc.getCoordinates());
			} else if (!centerMode && group2.contains(tn)) {
				nucGroup2.add(nuc.getCoordinates());
			}
		}
		Vector<Integer> res1 = new ArrayVector<Integer>(Space.threeD);
		Vector.meanVector(res1, nucGroup1);
		if (!centerMode) {
			Vector<Integer> res2 = new ArrayVector<Integer>(Space.threeD);
			Vector.meanVector(res2, nucGroup2);
			return getCylinder(res1, res2, getAppearance(), center, width);
		} else {
			return getCylinder(res1, center, getAppearance(), center, width);
		}

	}

	private Appearance getAppearance() {
		Color3f eColor = new Color3f(0.0f, 0.0f, 0.0f);
		Color3f sColor = color;
		Material m = new Material(eColor, eColor, sColor, sColor, 100.0f);
		m.setLightingEnable(true);
		Appearance app = new Appearance();
		app.setMaterial(m);
		return app;
	}

	private Vector<Integer> transformCoordinates(Vector<Integer> coor,
			Vector<Integer> center, float width) {
		Vector<Integer> coordinate = new ArrayVector<Integer>(coor);
		coordinate.subtract(center);
		coordinate.divide(width / 2);
		coordinate.set(1, -coordinate.get(1)); // ?
		coordinate.set(2, -coordinate.get(2));// ?
		return coordinate;
	}

	private TransformGroup getCylinder(Vector<Integer> n,
			Vector<Integer> sister, Appearance app, Vector<Integer> center,
			float width) {
		Vector<Integer> v1 = transformCoordinates(n, center, width);
		Vector<Integer> v2 = transformCoordinates(sister, center, width);
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

			Cylinder cy = new Cylinder(cylinderDiameter, 1.2f, app);
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
}
