package org.kuleuven.lineager;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TreeSlider extends JComponent {
	private static final long serialVersionUID = -1037205093134198130L;
	protected TreeViewer treeViewer;
	protected int precision = 3;
	DecimalFormat formatter = new DecimalFormat("#.#");

	public TreeSlider(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;

		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
		treeViewer.addDistanceListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				repaint();
			}
		});
	}

	public int getTrackStart() {
		return treeViewer.getRootWidth();
	}

	public int getTrackWidth() {
		return treeViewer.getWidth() - treeViewer.getRootWidth()-1;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(5, 25);
	}

	@Override
	public void paintComponent(Graphics g) {
		int y = getHeight() - 5;
		int left = getTrackStart();
		int width = getTrackWidth();
		int right = getWidth() - 1;
		double distancefactor = treeViewer.getDistanceFactor();
		double start = treeViewer.getStart();
		int sliderPosition = left
				+ (int) Math.round(distancefactor
						* (treeViewer.getDistanceIndicator() - start));

		g.setColor(treeViewer.getThresholdColor());
		g.fillRect(sliderPosition - 2, y - 3, 5, 7);

		g.setColor(Color.black);
		g.drawLine(left, y, right, y);

		int majorTickTop = y - 2;
		int majorTickBottom = y + 2;
		int majorTickCount = 4;

		if (start != treeViewer.getEnd()) {
			for (int i = 1; i < majorTickCount; i++) {

				int x = (int) (left + width * ((double) i / (double) majorTickCount));
				double val = (x - left) / treeViewer.getDistanceFactor() + start;
				String string = formatter.format(val);
				g.drawString(string, x - string.length() * 3, y - 5);
				g.drawLine(x, majorTickTop, x, majorTickBottom);
			}
			double val = (right - left) / treeViewer.getDistanceFactor()+ start;
			String string2 = formatter.format(val);
			g.drawString(string2, right - string2.length() * 8, y - 5);
			g.drawLine(right, majorTickTop, right, majorTickBottom);
		}
		String string = formatter.format(treeViewer.getStart());
		g.drawString(string, left, y - 5);
		g.drawLine(left, majorTickTop, left, majorTickBottom);
	

	}

	public double trackPositionToThreshold(int x) {
		/*
		 * double value = (double) (x - getTrackStart()) / (double)
		 * getTrackWidth(); value = Math.min(Math.max(0.0, value), 1.0); double
		 * precisionFactor = Math.pow(10, precision); double score =
		 * Math.round(value * precisionFactor) / precisionFactor; int
		 * sliderPosition = left + (int) Math.round((double) distancefactor *
		 * (treeViewer.getDistanceIndicator() - 1));
		 */

		double factor = treeViewer.getDistanceFactor();
		// System.out.println(x);
		return 1 + (x - getTrackStart()) / factor;
	}

	public int getPrecision() {
		return precision;
	}

	@Override
	public void processMouseEvent(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
			double score = trackPositionToThreshold(mouseEvent.getX());
			// System.out.println(score);
			treeViewer.setDistanceIndicator(score);
			repaint();
		}
	}

	@Override
	public void processMouseMotionEvent(MouseEvent mouseEvent) {
		if (mouseEvent.getID() == MouseEvent.MOUSE_DRAGGED) {
			treeViewer.setDistanceIndicator(trackPositionToThreshold(mouseEvent.getX()));
			repaint();
		}
	}

}
