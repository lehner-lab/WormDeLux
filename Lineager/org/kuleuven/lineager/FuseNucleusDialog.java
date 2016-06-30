package org.kuleuven.lineager;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;

import org.kuleuven.collections.ComparatorFactory;
import org.kuleuven.collections.SortedList;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.math.vector.ObjectAndDoubleEntry;

public class FuseNucleusDialog extends BaseDialogFrame {

	private LineagerObjectExchange loe;
	private JRadioButton forwardButton;
	private JRadioButton backwardButton;
	private JComboBox nucleusToMergeBox;
	private TrackedNucleus selected;
	private Integer time;
	private float distanceThreshold = 5f;
	private boolean forwardmode = true;

	public FuseNucleusDialog(LineagerObjectExchange lp) {
		this.loe = lp;
		this.selected = loe.getSelectedNucleus();
		this.time = loe.getCurrentTime();
		setTitle("Merge objects tracking same nucleus as " +selected.getName());
		forwardButton = new JRadioButton("Look forward");
		forwardButton.setSelected(true);

		backwardButton = new JRadioButton("Look backward");

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(forwardButton);
		group.add(backwardButton);

		// Register a listener for the radio buttons.
		forwardButton.addActionListener(this);
		backwardButton.addActionListener(this);
		top.addFirst(backwardButton);
		top.pegToTheLeft(forwardButton, backwardButton);
		nucleusToMergeBox = new JComboBox();
		addComponent("Select nucleus to merge", nucleusToMergeBox, backwardButton);
		nucleusToMergeBox.addActionListener(this);
		fillNucleusToMerge();
		pack();
	}

	private void fillNucleusToMerge() {
		LineageMeasurement lm = loe.getLineageMeasurement();
		int lastT = lm.getLastTimePoint();
		Map<TrackedNucleus, Float> map = new HashMap<TrackedNucleus, Float>();
		
		if (forwardmode) {
			Set<TrackedNucleus> orphans = lm.lineage.getOrphanNuclei();
			
			for (int i = time + 1; (i < time + 15) && i <= lastT; i++) {
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
		} else {
			Set<TrackedNucleus> childless = lm.lineage.getChildlessNuclei();
			for (int i = time - 1; (i > time - 15) && i >= 1; i--) {
				List<Nucleus> nuclei = lm.getNucleiForATimepoint(i);
				if (nuclei != null) {
					for (Nucleus nucleus : nuclei) {
						float score = scoreNucleus(nucleus, lm, childless);
						TrackedNucleus tn = lm.getTrackedNucleusForNucleus(nucleus);
						if (score > 0 && (!map.containsKey(tn) || map.get(tn) > score)) {
							map.put(tn, score);
						}
					}
				}
			}
		}
		SortedList<ObjectAndDoubleEntry<TrackedNucleus>> list = new SortedList<ObjectAndDoubleEntry<TrackedNucleus>>(
				ComparatorFactory.<TrackedNucleus> ObjectAndDoubleEntryBooleanComparatorAscending());
		for (Entry<TrackedNucleus, Float> e : map.entrySet()) {
			list.add(new ObjectAndDoubleEntry<TrackedNucleus>(e.getKey(), e.getValue()));
		}
		nucleusToMergeBox.removeAllItems();
		for (ObjectAndDoubleEntry<TrackedNucleus> tn : list) {
			nucleusToMergeBox.addItem(tn.key);
		}

	}

	private float scoreNucleus(Nucleus nucleus, LineageMeasurement lm, Set<TrackedNucleus> orphans) {
		Nucleus mother = selected.getNucleusForTimePoint(time);
		if (lm.getTrackedNucleusForNucleus(nucleus).equals(selected)) {
			return -1f;
		}
		float distance = mother.distanceTo(nucleus);
		if (distance < distanceThreshold) {
			float score = distance * distance;
			score += Math.sqrt(Math.abs(nucleus.getTimePoint() - time));
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
		return new TopPanel(10, new Dimension(500, 85), 230, 15, 10);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(forwardButton)) {
			forwardmode = true;
			fillNucleusToMerge();
		} else if (e.getSource().equals(backwardButton)) {
			forwardmode = false;
			fillNucleusToMerge();
		}else if (e.getSource().equals(nucleusToMergeBox)){
			TrackedNucleus tn = (TrackedNucleus) nucleusToMergeBox.getSelectedItem();
			LineageMeasurement lm = loe.getLineageMeasurement();
			if(tn!=null){
				loe.setSelectedNucleus(tn);
				loe.getLineageActionHandler().actionPerformed(
						new ActionEvent(this, 1, LineagerActionHandler.SELECTIONCHANGED));
			}
		}
		else if (e.getActionCommand().equals(SAVE)) {
			TrackedNucleus tn = (TrackedNucleus) nucleusToMergeBox.getSelectedItem();
			LineageMeasurement lm = loe.getLineageMeasurement();
			if(tn!=null){
			if (forwardmode) {
				lm.changeLifeTimeOfNucleus(selected,selected.getFirstTimePoint(), time);
				lm.changeLifeTimeOfNucleus(tn,
						time + 1, tn.getLastTimePoint());
				lm.mergeConsecutiveNuclei(selected, tn);
				loe.setSelectedNucleus(selected);
			} else {
				lm.changeLifeTimeOfNucleus(selected, time,
						selected.getLastTimePoint());
				
				lm.changeLifeTimeOfNucleus(tn, tn.getFirstTimePoint(),
						time - 1);
				tn.setName(selected.getName());
				lm.mergeConsecutiveNuclei(tn, selected);
				loe.setSelectedNucleus(tn);
			}
			loe.getLineageActionHandler().actionPerformed(
					new ActionEvent(loe.getLineagerFrame(), 1,
							LineagerActionHandler.TREECHANGED));
					}
			setVisible(false);
		} else if (e.getActionCommand().equals(CANCEL)) {
			setVisible(false);
		}

	}

}
