package org.kuleuven.lineager;

import java.awt.Rectangle;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeListener;

import org.kuleuven.collections.Node;
import org.kuleuven.lineagetree.DistancedTree;
import org.kuleuven.lineagetree.HasDistance;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.userinterface.models.ZoomModel;

public class TreeComponent<E> extends JScrollPane {
	private static final long serialVersionUID = 9084726022972863044L;
	private TreeViewer treeviewer;
	private TreeSlider treeslider;

	public TreeComponent(DistancedTree<E> tree) {
		this.treeviewer = new TreeViewer(1);
		treeviewer.setDistancedTree(tree);
		this.treeslider = new TreeSlider(treeviewer);
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setViewportView(treeviewer);

		setColumnHeaderView(treeslider);
	}

	public void addDistanceIndicatorListener(ChangeListener cl) {
		treeviewer.addDistanceListener(cl);
	}

	public void setZoomModel(ZoomModel zm) {
		treeviewer.setZoomModel(zm);
	}

	public void setMaxDistance(Double maxDistance) {
		treeviewer.setMaxDistance(maxDistance);
	}

	public Double getMaxDistance() {
		return treeviewer.getMaxDistance();
	}

	public void addSelectedClusterListener(LineagerActionHandler lineageActionHandler) {
		treeviewer.addSelectedClusterListener(lineageActionHandler);
	}

	public void addHooverOverClusterListener(ChangeListener changeListener) {
		treeviewer.addHooverOverClusterListener(changeListener);
	}

	public void addDragAndDropListener(DragAndDropListener<TrackedNucleus> dd) {
		treeviewer.addDragAndDropListener(dd);
	}

	public void setShowDistanceIndicator(boolean show) {
		treeviewer.setShowDistanceIndicator(show);
	}

	public void setSelectedCluster(HasDistance tn) {
		treeviewer.setSelectedCluster(tn);
	}

	public void addComponentToDraw(JComponent component) {
		treeviewer.addComponentToDraw(component);
	}

	public void removeComponentToDraw(JComponent component) {
		treeviewer.removeComponentToDraw(component);
	}

	public void resetComponentsToDraw() {
		repaint();
		treeslider.repaint();
		treeviewer.resetComponentsToDraw();
	}

	public NodeLine getClusterLine(Node<HasDistance> node) {
		return treeviewer.getClusterLine(node);
	}

	public int getPositionForDistance(Double distance) {
		return treeviewer.getPositionForDistance(distance);
	}

	public void scrollToSelectedCluster() {
		NodeLine clusterLine = treeviewer.getClusterLine(getSelectedCluster());
		if (clusterLine != null) {
			int y = Math.max(clusterLine.y1 - 100, 1);
			treeviewer.scrollRectToVisible(new Rectangle(clusterLine.x1, y, 1, 200));
		}
	}

	public void setDistanceIndicator(double time) {
		treeviewer.setDistanceIndicator(time);
	}

	public double getDistanceIndicator() {
		return treeviewer.getDistanceIndicator();
	}

	public void reparseTree() {
		treeviewer.reparseTree();
	}

	public Node<HasDistance> getSelectedCluster() {
		return treeviewer.getSelectedCluster();
	}

	public Node<HasDistance> getHooverOverCluster() {
		return treeviewer.getHooverOverCluster();
	}

	public void reparseTree(Set<HasDistance> shownNuclei) {
		treeviewer.reparseTree(shownNuclei);

	}

}