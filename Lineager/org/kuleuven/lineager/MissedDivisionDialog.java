package org.kuleuven.lineager;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;

import org.kuleuven.collections.ComparatorFactory;
import org.kuleuven.collections.SortedList;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.math.vector.ObjectAndDoubleEntry;

public class MissedDivisionDialog extends BaseDialogFrame {

	private TrackedNucleus selected;
	private Integer time;
	private JComboBox potentialDaughterBox1;
	int observedTimepoints = 10;
	float distanceThreshold = 7.5f;// in microns,now relaxed, earlier lm
									// resolution should be filled in
	// correctly for this! logic is 1/5 of 45
	// micron embryo separate nuclei
	private JComboBox potentialDaughterBox2;
	private JButton updateButton;
	private LineagerObjectExchange loe;

	public MissedDivisionDialog(LineagerObjectExchange loe) {
		setLocationRelativeTo(loe.getLineagerFrame());
		this.loe = loe;
		selected = loe.getSelectedNucleus();
		time = loe.getCurrentTime();
		String line = "Fix division of " + selected.getName() + ", t=" + time;
		setTitle(line);
		potentialDaughterBox1 = new JComboBox();
		potentialDaughterBox2 = new JComboBox();
		fillPotentialDaughterBox(potentialDaughterBox1);
		fillPotentialDaughterBox(potentialDaughterBox2);
		if (potentialDaughterBox1.getItemCount() > 1) {
			potentialDaughterBox2.removeItem(potentialDaughterBox1.getSelectedItem());
			potentialDaughterBox1.removeItem(potentialDaughterBox2.getSelectedItem());
		}
		potentialDaughterBox1.addActionListener(this);
		potentialDaughterBox2.addActionListener(this);
		addFirstComponent("Select first daughter", potentialDaughterBox1);
		addComponent("Select second daughter", potentialDaughterBox2, potentialDaughterBox1);
		updateButton = new JButton("Update");
		updateButton.addActionListener(this);
		pack();
	}

	private void fillPotentialDaughterBox(JComboBox daughterBox) {
		daughterBox.removeAllItems();
		LineageMeasurement lm = loe.getLineageMeasurement();
		Set<TrackedNucleus> orphans = lm.lineage.getOrphanNuclei();
		int lastT = lm.getLastTimePoint();
		Map<TrackedNucleus, Float> map = new HashMap<TrackedNucleus, Float>();
		for (int i = time + 1; (i < time + 10) && i <= lastT; i++) {
			List<Nucleus> nuclei = lm.getNucleiForATimepoint(i);
			if (nuclei != null) {
				for (Nucleus nucleus : nuclei) {
					float score = scoreNucleus(nucleus, lm, orphans);
					TrackedNucleus tn = lm.getTrackedNucleusForNucleus(nucleus);
					if (score > 0 && (!map.containsKey(tn) || map.get(tn) > score)) {
						map.put(tn, score);
					}
				}
			}
		}
		SortedList<ObjectAndDoubleEntry<TrackedNucleus>> list = new SortedList<ObjectAndDoubleEntry<TrackedNucleus>>(
				ComparatorFactory.<TrackedNucleus> ObjectAndDoubleEntryBooleanComparatorAscending());
		for (Entry<TrackedNucleus, Float> e : map.entrySet()) {
			list.add(new ObjectAndDoubleEntry<TrackedNucleus>(e.getKey(), e.getValue()));
		}
		for (ObjectAndDoubleEntry<TrackedNucleus> tn : list) {
			daughterBox.addItem(tn.key);
		}

	}

	private float scoreNucleus(Nucleus nucleus, LineageMeasurement lm, Set<TrackedNucleus> orphans) {
		Nucleus mother = selected.getNucleusForTimePoint(time);
		float distance = mother.distanceTo(nucleus);
		if (distance < distanceThreshold) {
			float score = distance * distance;
			score += Math.sqrt(nucleus.getTimePoint() - time);
			TrackedNucleus tn = lm.getTrackedNucleusForNucleus(nucleus);
			if (!orphans.contains(tn)) {
				score += 4f;
			}
			return score;
		}
		return -1f;
	}

	@Override
	protected TopPanel defineTopPanel() {
		return new TopPanel(10, new Dimension(500, 85), 190, 15, 10);
	}

	private void updateDaughterboxes() {
		fillPotentialDaughterBox(potentialDaughterBox1);
		fillPotentialDaughterBox(potentialDaughterBox2);
		if (potentialDaughterBox1.getItemCount() > 1) {
			potentialDaughterBox2.removeItem(potentialDaughterBox1.getSelectedItem());
			potentialDaughterBox1.removeItem(potentialDaughterBox2.getSelectedItem());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(CANCEL)) {
			setVisible(false);
		} else if (potentialDaughterBox1.getItemCount() > 0
				|| potentialDaughterBox2.getItemCount() > 0) {
			if (e.getSource().equals(potentialDaughterBox1)) {
				TrackedNucleus tn = (TrackedNucleus) potentialDaughterBox1.getSelectedItem();
				int target = time + 1;
				if (tn.getFirstTimePoint() > target) {
					target = tn.getFirstTimePoint();
				}
				loe.setCurrentTime(target);
				loe.setSelectedNucleus(tn);
				loe.getLineageActionHandler().actionPerformed(
						new ActionEvent(this, 1, LineagerActionHandler.SELECTIONCHANGED));

			} else if (e.getSource().equals(potentialDaughterBox2)) {
				TrackedNucleus tn = (TrackedNucleus) potentialDaughterBox2.getSelectedItem();
				int target = time + 1;
				if (tn.getFirstTimePoint() > target) {
					target = tn.getFirstTimePoint();
				}
				loe.setCurrentTime(target);
				loe.setSelectedNucleus(tn);
				loe.getLineageActionHandler().actionPerformed(
						new ActionEvent(this, 1, LineagerActionHandler.SELECTIONCHANGED));
			} else if (e.getSource().equals(updateButton)) {
				updateDaughterboxes();
			}
			if (e.getActionCommand().equals(SAVE)) {
				LineageMeasurement lm = loe.getLineageMeasurement();
				TrackedNucleus daughter1 = (TrackedNucleus) potentialDaughterBox1.getSelectedItem();
				TrackedNucleus daughter2 = (TrackedNucleus) potentialDaughterBox2.getSelectedItem();
				if (daughter1.equals(selected)) {
					daughter1 = null;
				} else if (daughter2.equals(selected)) {
					daughter2 = daughter1;
					daughter1 = null;
				}
				if (selected.getLastTimePoint() > time) {
					List<TrackedNucleus> out = lm.changeLifeTimeOfNucleus(selected, selected
							.getFirstTimePoint(), time);
					if (daughter1 == null)
						daughter1 = out.get(0);
				}
				if (daughter1 != null) {
					if (daughter1.getFirstTimePoint() != time + 1) {
						int timeStart = daughter1.getFirstTimePoint();
						int timeEnd = daughter1.getLastTimePoint();
						lm.changeLifeTimeOfNucleus(daughter1, time + 1, daughter1
								.getLastTimePoint());
						if (Math.abs(time - timeStart) > 1)
							loe.getLineageActionHandler().setTrackingMode(time + 1, daughter1,
									timeStart, timeEnd);
					}
					lm.changeParentChildRelation(selected, daughter1);
				}
				if (daughter2 != null) {
					if (daughter2.getFirstTimePoint() != time + 1) {
						int timeStart = daughter2.getFirstTimePoint();
						int timeEnd = daughter2.getLastTimePoint();
						lm.changeLifeTimeOfNucleus(daughter2, time + 1, daughter2
								.getLastTimePoint());
						if (Math.abs(time - timeStart) > 1)
							loe.getLineageActionHandler().setTrackingMode(time + 1, daughter2,
									timeStart, timeEnd);
					}
					lm.changeParentChildRelation(selected, daughter2);
				}
				loe.getLineageActionHandler().actionPerformed(
						new ActionEvent(loe.getLineagerFrame(), 1,
								LineagerActionHandler.TREECHANGED));
						setVisible(false);
			}

		} else {
			setVisible(false);
		}
	}
}
