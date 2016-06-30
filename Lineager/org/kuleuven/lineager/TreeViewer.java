package org.kuleuven.lineager;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.kuleuven.collections.Node;
import org.kuleuven.lineagetree.DistancedTree;
import org.kuleuven.lineagetree.HasDistance;
import org.kuleuven.userinterface.models.ZoomModel;

public class TreeViewer extends JComponent implements ChangeListener {
	public static int leftToRight = 1;
	public static int topToBottom = 0;
	protected int preferredDepth = 250;
	protected ZoomModel zoomModel;
	protected double distanceFactor, distanceIndicator = 0;
	protected boolean dragging = false;
	protected boolean showDistanceIndicator = false;
	protected int dendrogramDepth, rootDepth = 5;
	protected NodeLine mouseOverClusterLine;
	protected NodeLine selectedClusterLine;
	protected Stack<Color> colorStack = new Stack<Color>();
	protected Color thresholdColor = Color.blue, highlightColor = Color.magenta;
	protected List<ChangeListener> selectedClusterListeners = new ArrayList<ChangeListener>();
	protected List<ChangeListener> hooverOverClusterListeners = new ArrayList<ChangeListener>();

	protected List<ChangeListener> thresholdListeners = new ArrayList<ChangeListener>();
	protected List<DragAndDropListener<HasDistance>> dragAndDropListeners = new ArrayList<DragAndDropListener<HasDistance>>();
	protected Map<Node<HasDistance>, NodeLine> clusterLines = new HashMap<Node<HasDistance>, NodeLine>();
	protected DistancedTree<HasDistance> tree;
	protected List<HasDistance> objects;

	protected boolean orientationRotated = false;
	private int endBuffer = 1;
	private Double maxDistance = null;
	private List<JComponent> objectsToDraw = new ArrayList<JComponent>();

	public double getStart() {
		return tree.getStart();
	}

	public void addComponentToDraw(JComponent component) {
		objectsToDraw.add(component);
	}

	public void removeComponentToDraw(JComponent component) {
		objectsToDraw.remove(component);
	}

	public void resetComponentsToDraw() {
		objectsToDraw = new ArrayList<JComponent>();
	}

	public double getEnd() {
		return tree.getDistance();
	}

	public void setMaxDistance(Double maxDistance) {
		if (maxDistance != null && maxDistance < getEnd() && maxDistance > getStart())
			this.maxDistance = maxDistance;

		else {
			this.maxDistance = null;
		}
		resetComponentsToDraw();
		// repaint();
	}

	public Double getMaxDistance() {
		return this.maxDistance;
		// else
		// return getEnd();
	}

	public double getDistanceFactor() {
		if (distanceFactor == 0)
			setDistanceFactor();
		return distanceFactor;
	}

	public void setDistanceFactor() {
		if (dendrogramDepth == 0) {
			dendrogramDepth = getTreeDepth() - rootDepth - endBuffer;
		}
		double d = tree.getDistance();
		if (maxDistance != null)
			d = maxDistance;
		distanceFactor = dendrogramDepth / (d - tree.getStart());
	}

	private void setOrientation(int orientation) {
		if (orientation == leftToRight) {
			orientationRotated = false;
		} else if (orientation == topToBottom) {
			orientationRotated = true;
		}
	}

	public int getOrientation() {
		if (orientationRotated)
			return topToBottom;
		else
			return leftToRight;
	}

	protected int getTreeDepth() {
		if (orientationRotated)
			return getHeight();
		else
			return getWidth();

	}

	public TreeViewer(int orientation) {
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
		setOrientation(orientation);

	}

	@Override
	public void paintComponent(Graphics g) {
		if (isVisible()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		g.setColor(Color.black);

		if (tree != null&& !isOpaque()) {
			Insets insets = getInsets();
			setDendrogramDepth();
			setDistanceFactor();

			g.translate(insets.left, insets.top);

			int bPosition = (int) Math.round(paintNode(g, tree));
			NodeLine clusterLine = getClusterLine(tree);
			setNodeLine(clusterLine, 0, bPosition, rootDepth, bPosition);
			clusterLine.draw(g);

			if (distanceIndicator >= 0d && showDistanceIndicator) {
				int dPostion = rootDepth
						+ (int) Math.round((distanceIndicator - getStart()) * distanceFactor);
				if (dPostion >= rootDepth || dPostion <= dendrogramDepth) {
					g.setColor(thresholdColor);
					drawLine(g, dPostion, 0, dPostion, getDendrogramBreadth());
				}
			}
			for (JComponent d : objectsToDraw) {
				d.paint(g);
			}
			if (mouseOverClusterLine != null) {
				g.setColor(highlightColor);
				mouseOverClusterLine.draw(g);
			}

			if (selectedClusterLine != null) {
				g.setColor(Color.red);
				selectedClusterLine.draw(g);
			}

		}
	}

	private void setDendrogramDepth() {
		dendrogramDepth = getTreeDepth() - rootDepth - 1;
	}

	private void setNodeLine(NodeLine nodeLine, int d1, int b1, int d2, int b2) {
		if (orientationRotated) {
			nodeLine.set(b1, d1, b2, d2);
		} else {
			nodeLine.set(d1, b1, d2, b2);
		}
	}

	private void drawLine(Graphics g, int d1, int b1, int d2, int b2) {
		if (orientationRotated) {
			g.drawLine(b1, d1, b2, d2);
		} else {
			g.drawLine(d1, b1, d2, b2);
		}
	}

	public void setZoomModel(ZoomModel zoomModel) {
		if (zoomModel != this.zoomModel) {
			if (this.zoomModel != null)
				this.zoomModel.removeChangeListener(this);

			this.zoomModel = zoomModel;

			if (zoomModel != null)
				zoomModel.addChangeListener(this);
		}
	}

	public void setDistanceIndicator(double value) {
		if (value != distanceIndicator) {
			distanceIndicator = value;
			repaint();
			fireThresholdChanged();
		}
	}

	protected void pushColor(Graphics g, Color color) {
		colorStack.push(g.getColor());
		g.setColor(color);
	}

	protected void popColor(Graphics g) {
		g.setColor(colorStack.pop());
	}

	protected double paintNode(Graphics g, Node<HasDistance> distancedNode) {
		if (mouseOverClusterLine != null && distancedNode == mouseOverClusterLine.cluster)
			pushColor(g, highlightColor);

		if (selectedClusterLine != null && distancedNode == selectedClusterLine.cluster)
			pushColor(g, Color.red);

		boolean overThreshold = false;

		if (g.getColor().equals(Color.black)
				&& ((1d - distancedNode.getID().getDistance()) > distanceIndicator)
				&& showDistanceIndicator) {
			g.setColor(thresholdColor);
			overThreshold = true;
		}

		double bPosition;
		if (distancedNode.countChildren() == 0 || isMaxD(distancedNode))
			bPosition = paintTerminalNode(g, distancedNode);
		else
			bPosition = paintBiSectingNode(g, distancedNode);

		if (overThreshold)
			g.setColor(Color.black);

		if (selectedClusterLine != null && distancedNode == selectedClusterLine.cluster)
			popColor(g);

		if (mouseOverClusterLine != null && distancedNode == mouseOverClusterLine.cluster)
			popColor(g);

		return bPosition;
	}

	private boolean isMaxD(Node<HasDistance> distancedNode) {

		return getMaxDistance() != null && distancedNode.getID().getDistance() >= getMaxDistance();

	}

	protected double paintTerminalNode(Graphics g, Node<HasDistance> singleObjectCluster) {
		double zoomFactor = zoomModel.getZoomFactor();
		int index = objects.indexOf(singleObjectCluster.getID());
		double result = (index * zoomFactor) + zoomFactor / 2d;
		// System.out.println(singleObjectCluster.getID() + " " + index+ " " +
		// result);
		return result;
	}

	public int getPositionForDistance(Double distance) {
		return rootDepth + (int) Math.round((distance - getStart()) * distanceFactor);
	}

	protected double paintBiSectingNode(Graphics g, Node<HasDistance> mergedCluster) {
		HasDistance hd = mergedCluster.getID();
		// System.out.println(hd + "\t" + hd.getDistance());

		int dPostion = rootDepth
				+ (int) Math.round((hd.getDistance() - getStart()) * distanceFactor);
		List<HasDistance> l = mergedCluster.getChildrenAsSortedList();
		int pos1 = -1;
		int pos2 = Integer.MAX_VALUE;
		for (HasDistance h : l) {
			double bPositionFirst = paintNode(g, mergedCluster.getNode(h));
			Double distance = h.getDistance();
			if (getMaxDistance() != null && distance > getMaxDistance())
				distance = getMaxDistance();

			int bPosition1 = (int) Math.round(bPositionFirst);
			int dPostion1 = rootDepth + (int) Math.round((distance - getStart()) * distanceFactor);
			NodeLine first = getClusterLine(mergedCluster.getNode(h));
			setNodeLine(first, dPostion, bPosition1, dPostion1, bPosition1);
			first.draw(g);
			pos1 = Math.max(bPosition1, pos1);
			pos2 = Math.min(bPosition1, pos2);
		}
		drawLine(g, dPostion, pos2, dPostion, pos1);

		return (pos1 + pos2) / 2;
	}

	public void reparseTree() {
		if (this.tree != null) {
			objects = tree.getAllChildrenBelowNodeAsSortedList();
			updateClusterLines();
		}
	}

	public void reparseTree(Set<HasDistance> shownChildren) {
		if (this.tree != null) {
			List<HasDistance> k = new ArrayList<HasDistance>(shownChildren);
			Collections.sort(k, this.tree.getComparator());
			objects = k;
			clusterLines.clear();
			repaint();
			// updateClusterLines(shownChildren);
		}

	}

	private void updateClusterLines(Set<HasDistance> shownChildren) {
		Set<Node<HasDistance>> set = tree.getAllNodesBelowNode();
		// Set<Node<HasDistance>> selectedSet = new
		// HashSet<Node<HasDistance>>();
		clusterLines.clear();
		for (Node<HasDistance> hd : set) {
			if (shownChildren.contains(hd.getID())) {
				// selectedSet.add(hd);
				getClusterLine(hd);
			}
		}

	}

	private void updateClusterLines() {
		Set<Node<HasDistance>> set = tree.getAllNodesBelowNode();
		for (Node<HasDistance> hd : set) {
			getClusterLine(hd);
		}
		clusterLines.keySet().retainAll(set);

	}

	public void setDistancedTree(DistancedTree distancedTree) {
		this.tree = distancedTree;

		clusterLines.clear();
		objects = new ArrayList<HasDistance>();

		if (distancedTree != null)
			objects = distancedTree.getAllChildrenBelowNodeAsSortedList();

		setClusterLines();
		/*
		 * for (HasDistance dt : objects) { System.out.println(dt); }
		 */
		revalidate();
		repaint();
	}

	private void setClusterLines() {
		for (Node<HasDistance> hd : tree.getAllNodesBelowNode()) {
			getClusterLine(hd);
		}

	}

	public NodeLine retrieveClusterLine(Node node) {
		return clusterLines.get(node);
	}

	protected NodeLine getClusterLine(Node<HasDistance> node) {
		NodeLine result = clusterLines.get(node);

		if (result == null) {
			result = new NodeLine(node);
			clusterLines.put(node, result);
		}

		return result;
	}

	protected int getDendrogramBreadth() {
		return (int) Math.ceil(objects.size() * zoomModel.getZoomFactor());
	}

	@Override
	public int getHeight() {
		if (orientationRotated) {

		} else {
			return getDendrogramBreadth();
		}
		return super.getHeight();
	}

	@Override
	public Dimension getPreferredSize() {
		Insets insets = getInsets();
		if (orientationRotated) {
			return new Dimension(getDendrogramBreadth() + insets.top + insets.bottom,
					preferredDepth + insets.left + insets.right);
		} else
			return new Dimension(preferredDepth + insets.left + insets.right,
					getDendrogramBreadth() + insets.top + insets.bottom);
	}

	public List<HasDistance> getObjects() {
		return objects;
	}

	@Override
	public void stateChanged(ChangeEvent changeEvent) {
		revalidate();
		repaint();
	}

	protected NodeLine getClusterLineWithPoint(Point point) {
		Iterator<NodeLine> iterator = clusterLines.values().iterator();
		NodeLine result = null;

		while (result == null && iterator.hasNext()) {
			NodeLine clusterLine = iterator.next();

			if (clusterLine.contains(point))
				result = clusterLine;
		}

		return result;
	}

	@Override
	public void processMouseMotionEvent(MouseEvent mouseEvent) {
		if (mouseEvent.getID() == MouseEvent.MOUSE_MOVED) {
			if (orientationRotated && (mouseEvent.getY() >= getHeight() - 2)) {
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			} else if (!orientationRotated && (mouseEvent.getX() >= getWidth() - 2)) {
				setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			} else {
				setCursor(Cursor.getDefaultCursor());
			}
			if (mouseEvent.getY() >= getHeight() - 2 || mouseEvent.getX() >= getWidth() - 2) {
				setMouseOverClusterLine(null);
			} else {
				setMouseOverClusterLine(getClusterLineWithPoint(mouseEvent.getPoint()));
			}
			repaint();

		} else if (mouseEvent.getID() == MouseEvent.MOUSE_DRAGGED) {

			if (mouseEvent.getY() >= getHeight() - 2 || mouseEvent.getX() >= getWidth() - 2) {
				setMouseOverClusterLine(null);
			} else {
				setMouseOverClusterLine(getClusterLineWithPoint(mouseEvent.getPoint()));
			}
			repaint();

		} else if (mouseEvent.getID() == MouseEvent.MOUSE_EXITED) {
			setMouseOverClusterLine(null);
		} else if (mouseEvent.getID() == MouseEvent.MOUSE_RELEASED) {

		}
	}

	private void throwDragAndDropEvent() {
		if (mouseOverClusterLine != null && selectedClusterLine != null) {
			for (DragAndDropListener<HasDistance> d : dragAndDropListeners) {
				d.dragAndDrop(mouseOverClusterLine.cluster, selectedClusterLine.cluster);
			}
		}

	}

	protected void setMouseOverClusterLine(NodeLine clusterLine) {
		if (mouseOverClusterLine != clusterLine) {
			mouseOverClusterLine = clusterLine;
			fireHooverOverClusterChanged();
			repaint();
		}
	}

	@Override
	public void processMouseEvent(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
			if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
				if (mouseEvent.getX() < getWidth() - 1 && mouseEvent.getY() < getHeight() - 1) {
					setSelectedClusterLine(getClusterLineWithPoint(mouseEvent.getPoint()));
					repaint();
					if (getSelectedCluster() != null)
						dragging = true;
				}
			} else if (mouseEvent.getID() == MouseEvent.MOUSE_RELEASED) {
				if (mouseEvent.getX() < getWidth() - 1 && mouseEvent.getY() < getHeight() - 1) {
					if (dragging && getClusterLineWithPoint(mouseEvent.getPoint()) != null
							&& !mouseOverClusterLine.equals(selectedClusterLine)) {
						throwDragAndDropEvent();
						repaint();
					}
				}
				dragging = false;

			} else if (mouseEvent.getID() == MouseEvent.MOUSE_CLICKED) {
				if (mouseEvent.getX() < getWidth() - 1 && mouseEvent.getY() < getHeight() - 1) {
					setSelectedClusterLine(getClusterLineWithPoint(mouseEvent.getPoint()));
					repaint();
				}
			}
		}
	}

	public void setSelectedCluster(HasDistance hd) {
		Node<HasDistance> node = tree.getNode(hd);
		if (node != null) {
			NodeLine clusterline = clusterLines.get(node);
			setSelectedClusterLine(clusterline);
		} else {
			setSelectedClusterLine(null);
		}
	}

	protected void setSelectedClusterLine(NodeLine clusterLine) {
		if ((clusterLine == null && selectedClusterLine != null)
				|| (clusterLine != null && !clusterLine.equals(selectedClusterLine))) {
			selectedClusterLine = clusterLine;
			if (clusterLine != null) {
				int y = Math.max(clusterLine.y1 - 100, 1);
				scrollRectToVisible(new Rectangle(clusterLine.x1, y, 1, 200));
			}
			fireSelectedClusterChanged();
			repaint();
		}

	}

	protected void fireSelectedClusterChanged() {
		ChangeEvent changeEvent = new ChangeEvent(this);

		for (ChangeListener selectedClusterListener : selectedClusterListeners)
			selectedClusterListener.stateChanged(changeEvent);
	}

	public void addSelectedClusterListener(ChangeListener changeListener) {
		selectedClusterListeners.add(changeListener);
	}

	public void removeSelectedClusterListener(ChangeListener changeListener) {
		selectedClusterListeners.remove(changeListener);
	}

	protected void fireHooverOverClusterChanged() {
		ChangeEvent changeEvent = new ChangeEvent(this);

		for (ChangeListener hooverOverClusterListener : hooverOverClusterListeners)
			hooverOverClusterListener.stateChanged(changeEvent);
	}

	public void addHooverOverClusterListener(ChangeListener changeListener) {
		hooverOverClusterListeners.add(changeListener);
	}

	public void removeHooverOverClusterListener(ChangeListener changeListener) {
		hooverOverClusterListeners.remove(changeListener);
	}

	protected void fireThresholdChanged() {
		ChangeEvent changeEvent = new ChangeEvent(this);

		for (ChangeListener thresholdListener : thresholdListeners)
			thresholdListener.stateChanged(changeEvent);
	}

	public void addDistanceListener(ChangeListener changeListener) {
		thresholdListeners.add(changeListener);
	}

	public void removeThresholdListener(ChangeListener changeListener) {
		thresholdListeners.remove(changeListener);
	}

	public Node<HasDistance> getSelectedCluster() {
		if (selectedClusterLine != null)
			return selectedClusterLine.cluster;
		else
			return null;
	}

	public Node<HasDistance> getHooverOverCluster() {
		if (mouseOverClusterLine != null)
			return mouseOverClusterLine.cluster;
		else
			return null;
	}

	public List<HasDistance> getSelectedObjects() {
		if (selectedClusterLine != null)
			return selectedClusterLine.cluster.getChildrenAsSortedList();
		else
			return null;
	}

	public int getRootWidth() {
		return rootDepth;
	}

	public double getDistanceIndicator() {
		return distanceIndicator;
	}

	public Color getThresholdColor() {
		return thresholdColor;
	}

	public boolean isShowDistanceIndicator() {
		return showDistanceIndicator;
	}

	public void setShowDistanceIndicator(boolean showDistanceIndicator) {
		this.showDistanceIndicator = showDistanceIndicator;
	}

	public void resetSelection() {
		selectedClusterLine = null;
		repaint();

	}

	@SuppressWarnings("unchecked")
	public void addDragAndDropListener(DragAndDropListener dd) {
		dragAndDropListeners.add(dd);

	}

}
