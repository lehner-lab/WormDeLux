package org.kuleuven.lineager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.kuleuven.collections.Node;
import org.kuleuven.lineagetree.HasDistance;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.userinterface.models.ZoomModel;
import org.kuleuven.utilities.StringUtilities;

public class LineageTreeViewer extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6692272719048643671L;
	private TreeComponent<TrackedNucleus> tv;

	public TreeComponent<TrackedNucleus> getTv() {
		return tv;
	}

	private JTextField hooverTextField;
	private LineagerObjectExchange loe;
	private JTextField maxTTextField;

	public LineageTreeViewer(final LineagerObjectExchange loe) {
		this.loe = loe;
		setTitle("Tree viewer");
		JPanel panel = new JPanel(new BorderLayout());
		getContentPane().add(panel);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		LineageMeasurement lm = loe.getLineageMeasurement();
		if (lm != null) {
			tv = new TreeComponent<TrackedNucleus>(lm.lineage);
			tv.setZoomModel(new ZoomModel(2.5));
			tv.setVisible(true);
			panel.add(tv, BorderLayout.CENTER);
			tv.addSelectedClusterListener(loe.getLineageActionHandler());
			JPanel bottomPanel = new JPanel(new BorderLayout());
			hooverTextField = new JTextField("");
			hooverTextField.setEditable(false);
			hooverTextField.setBackground(Color.LIGHT_GRAY);
			bottomPanel.add(hooverTextField, BorderLayout.CENTER);
			JPanel miniPanel = new JPanel();
			miniPanel.add(new JLabel("Max T"));
			maxTTextField = new JTextField("");
			maxTTextField.setPreferredSize(new Dimension(50, 25));
			maxTTextField.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String text = maxTTextField.getText();
					text.trim();
					if (StringUtilities.isInteger(text)
							&& Integer.parseInt(text) <= loe.getLineageMeasurement()
									.getLastTimePoint()) {
						Integer i = Integer.parseInt(text);
						tv.setMaxDistance(i.doubleValue());
						update();

					} else {
						maxTTextField.setText("");
						tv.setMaxDistance(null);
						update();
					}
				}
			});
			miniPanel.add(maxTTextField);
			bottomPanel.add(miniPanel, BorderLayout.EAST);
			panel.add(bottomPanel, BorderLayout.SOUTH);

			tv.addHooverOverClusterListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					Node<HasDistance> hoover = tv.getHooverOverCluster();
					if (hoover != null) {
						hooverTextField.setText(hoover.getID().toString());
					} else {
						hooverTextField.setText("");
					}
				}
			});

			tv.addDistanceIndicatorListener(loe.getLineageActionHandler());
			DragAndDropListener<TrackedNucleus> dd = new DragAndDropListener<TrackedNucleus>() {

				@Override
				public void dragAndDrop(Node<TrackedNucleus> acceptor, Node<TrackedNucleus> arrival) {
					// System.out.println(acceptor.getID().getLastTimePoint());
					// System.out.println(arrival.getID().getFirstTimePoint());
					System.out.println(loe.getLineageMeasurement().changeParentChildRelation(
							acceptor.getID(), arrival.getID()));
					loe.setSelectedNucleus(null);
					loe.getLineageActionHandler().actionPerformed(
							new ActionEvent(this, 1, LineagerActionHandler.SELECTIONCHANGED));
				}

			};
			tv.addDragAndDropListener(dd);

			setSize(400, 400);
			setVisible(true);
		}
	}

	public void showIndicator(boolean show) {
		tv.setShowDistanceIndicator(show);
	}

	public void setSelectedNucleus(TrackedNucleus tn) {
		tv.setSelectedCluster(tn);
	}

	public void pullThePlug() {
		WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}

	public void treeChanged() {
		tv.reparseTree();
	}

	public void update() {
	
		LineageMeasurement lm = loe.getLineageMeasurement();
		Set<TrackedNucleus> tns = lm.lineage.getOrphanNuclei();
		tv.resetComponentsToDraw();
		if (tv.getMaxDistance() != null) {
			Set<HasDistance> shownNuclei = new HashSet<HasDistance>();

			for (TrackedNucleus tn : loe.getLineageMeasurement().getAllTrackedNuclei()) {
				if (tn.getFirstTimePoint() <= tv.getMaxDistance()) {
					shownNuclei.add(tn);
				}
			}
			tv.reparseTree(shownNuclei);
			tns.retainAll(shownNuclei);
		} else {
			tv.reparseTree();
		}
		Integer time = loe.getCurrentTime();
		setSelectedNucleus(loe.getSelectedNucleus());
		tv.setDistanceIndicator(time);

		for (TrackedNucleus tn : tns) {
			Node n = lm.lineage.getNode(tn);
			NodeLine nl = tv.getClusterLine(n);
			if (nl != null) {
				double distance = tn.getFirstTimePoint();
				int position = tv.getPositionForDistance(distance);
				OrphanNodeLine onl = new OrphanNodeLine(nl, position);
				tv.addComponentToDraw(onl);
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(LineagerActionHandler.TREECHANGED)) {
			treeChanged();
			update();
		}

	}

}
