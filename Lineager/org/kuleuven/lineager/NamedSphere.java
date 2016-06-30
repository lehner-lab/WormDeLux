package org.kuleuven.lineager;

import javax.media.j3d.Appearance;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;

public class NamedSphere extends Sphere {
	String iName;

	public NamedSphere(String name, float r, Appearance a) {
		super(r, Primitive.GENERATE_NORMALS,40,a);
		iName = name;
	}
}

