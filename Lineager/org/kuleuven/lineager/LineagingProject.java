package org.kuleuven.lineager;

import java.io.File;

import org.kuleuven.collections.SortedListMap;

public class LineagingProject {

	private LineagerObjectExchange linexch;
	private double XYresolution = 0.1;
	private double Zresolution = 1d;

	
	public LineagingProject() {
		linexch = new LineagerObjectExchange();
		linexch.setName("New project");
	}

	public LineagerObjectExchange getLinexch() {
		return linexch;
	}

	public void setLinexch(LineagerObjectExchange linexch) {
		this.linexch = linexch;
	}

	public double getXYresolution() {
		return XYresolution;
	}

	public void setXYresolution(double xyRes) {
		this.XYresolution = xyRes;
	}

	public double getZresolution() {
		return Zresolution;
	}

	public void setZresolution(double zRes) {
		this.Zresolution = zRes;
	}

	public String getName() {
		return linexch.getName();
	}

	public void setName(String name) {
		linexch.setName(name);
	}

	public File getImageDir() {
		return linexch.getLineagerFrame().getImageDirectory();
	}

	public void setImageDirAndFileMap(File f,
			SortedListMap<Integer, SortedListMap<Integer, String>> fileMap) {
		LineagerPanel lf = linexch.getLineagerFrame();
		lf.initializeImageStore(f, fileMap);
		lf.setTimeAndPlaneFieldsEditable();

	}

}
