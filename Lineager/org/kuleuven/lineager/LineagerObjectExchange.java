package org.kuleuven.lineager;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.kuleuven.collections.SortedListMap;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.TrackedNucleus;

public class LineagerObjectExchange {
	private  File lastVistedDirectory = null;	
	private  LineagerPanel lineagerFrame = null;
	private  LineageMeasurement lm = null;
	private  TrackedNucleus selectedTrackedNucleus = null;
	private  Integer currentTime = 0;
	private  Integer currentPlane = 0;
	private  SortedListMap<Integer, SortedListMap<Integer, String>> fileMap = null;
	private  LineageTreeViewer lineageTreeViewer= null;
	private  LineagerActionHandler lah=null;
	private  Boolean keepFocus=false;
	private  Boolean showAll=true;
	private  Boolean useLut=false;
	private  boolean processImage;
	private File lineageDir=null;
	private String name ="Unnamed Project";
	private List<ActionListener> lineageListeners=new ArrayList<ActionListener>();
	
	
	public LineagerObjectExchange() {
		lineagerFrame=new LineagerPanel(this);
	}
	public  Boolean getShowAll() {
		return showAll;
	}

	public  void setShowAll(Boolean showAll) {
		this.showAll = showAll;
	}

	public  Boolean getKeepFocus() {
		return keepFocus;
	}

	public  void setKeepFocus(Boolean keepFocus) {
		this.keepFocus = keepFocus;
	}

	public  LineagerActionHandler getLineageActionHandler() {
		return lah;
	}

	public  void setLineageActionHandler(LineagerActionHandler lah) {
		this.lah = lah;
	}

	public  LineageTreeViewer getLineageTreeComponent() {
		return lineageTreeViewer;
	}

	public  void setLineageTreeViewer(LineageTreeViewer lineageTreeViewer) {
		this.lineageTreeViewer = lineageTreeViewer;
		addLineageChangeListener(lineageTreeViewer);
	}

	public  SortedListMap<Integer, SortedListMap<Integer, String>> getFileMap() {
		return fileMap;
	}

	public  void setFileMap(SortedListMap<Integer, SortedListMap<Integer, String>> fileMap) {
		this.fileMap = fileMap;
	}


	public  LineagerPanel getLineagerFrame() {
		return lineagerFrame;
	}

	public  void setLineagerFrame(LineagerPanel lf) {
		lineagerFrame = lf;
	}

	public  LineageMeasurement getLineageMeasurement() {
		return lm;
	}

	public  void setLineageMeasurement(LineageMeasurement lineageMeasurement) {
		lm = lineageMeasurement;
	}

	public  void setSelectedNucleus(TrackedNucleus selectedNucleus) {
		selectedTrackedNucleus = selectedNucleus;
	}

	public  TrackedNucleus getSelectedNucleus() {
		return selectedTrackedNucleus;
	}

	public  Integer getCurrentTime() {
		return currentTime;
	}

	public  void setCurrentTime(Integer currentTime) {
		if (fileMap != null && fileMap.containsKey(currentTime)) {
			this.currentTime = currentTime;
		}
	}

	public  Integer getCurrentPlane() {
		return currentPlane;
	}

	public  void setCurrentPlane(Integer currentPlane) {
		if (fileMap != null) {
			SortedListMap<Integer, String> planeMap = fileMap.get(currentTime);
			if (planeMap.containsKey(currentPlane)) {
				this.currentPlane=currentPlane;
			}
		}
	}

	public  void setUseLut(Boolean useLut) {
		this.useLut = useLut;
	}

	public  Boolean getUseLut() {
		return useLut;
	}

	public  void setProcessImage(boolean processImage) {
		this.processImage=processImage;
	}
	public  boolean getProcessImage(){
		return processImage;
	}

	public void setLineageDir(File lineageDir) {
		this.lineageDir=lineageDir;
	}
	public File getLineageDir(){
		return lineageDir;
	}
	public File getImageDir(){
		return getLineagerFrame().getImageDirectory();
	}
	public void setName(String name) {
		this.name  = name;
		
	}
	public String getName(){
		return name;
	}
	public void addLineageChangeListener(ActionListener al){
		this.lineageListeners.add(al);
	}
	public List<ActionListener> getLineageChangeListeners(){
		return lineageListeners;
	}
	
}
