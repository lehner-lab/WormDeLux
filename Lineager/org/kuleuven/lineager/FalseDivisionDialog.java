package org.kuleuven.lineager;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;

import org.kuleuven.collections.ComparatorFactory;
import org.kuleuven.collections.Node;
import org.kuleuven.collections.SortedList;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.math.vector.ObjectAndDoubleEntry;

public class FalseDivisionDialog extends BaseDialogFrame {

	private LineagerObjectExchange loe;
	private TrackedNucleus selected;
	private Integer time;
	private JComboBox childrenBox;

	public FalseDivisionDialog(LineagerObjectExchange lp) {
		loe = lp;
		setLocationRelativeTo(loe.getLineagerFrame());
		this.selected = loe.getSelectedNucleus();
		this.time = loe.getCurrentTime();
		String line = "Fix false division of " + selected.getName() + ", t=" + time;
		setTitle(line);
		childrenBox = new JComboBox();
		fillChildrenBox();
		childrenBox.addActionListener(this);
		addFirstComponent("Which child propagates the parent?", childrenBox);
		pack();
	}

	private void fillChildrenBox() {
		LineageMeasurement lm = loe.getLineageMeasurement();
		Node<TrackedNucleus> node = lm.lineage.getNode(selected);
		SortedList<ObjectAndDoubleEntry<TrackedNucleus>> list = new SortedList<ObjectAndDoubleEntry<TrackedNucleus>>(
				ComparatorFactory.<TrackedNucleus> ObjectAndDoubleEntryBooleanComparatorAscending());

		for (TrackedNucleus tn : node.getChildren()) {
			float score = scoreNucleus(tn.getNucleusForTimePoint(tn.getFirstTimePoint()), lm);
			list.add(new ObjectAndDoubleEntry<TrackedNucleus>(tn, score));
		}
		for (ObjectAndDoubleEntry<TrackedNucleus> tn : list) {
			childrenBox.addItem(tn.key);
		}

	}

	private float scoreNucleus(Nucleus nucleus, LineageMeasurement lm) {
		Nucleus mother = selected.getNucleusForTimePoint(time);
		float distance = mother.distanceTo(nucleus);
		return distance;
	}

	@Override
	protected TopPanel defineTopPanel() {
		return new TopPanel(10, new Dimension(500, 125), 230, 15, 10);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(childrenBox)) {
			TrackedNucleus tn = (TrackedNucleus) childrenBox.getSelectedItem();
			int target = tn.getFirstTimePoint();
			loe.setCurrentTime(target);
			loe.setSelectedNucleus(tn);
			loe.getLineageActionHandler().actionPerformed(
					new ActionEvent(this, 1, LineagerActionHandler.SELECTIONCHANGED));
		} else if (e.getActionCommand().equals(SAVE)) {
			TrackedNucleus tn = (TrackedNucleus) childrenBox.getSelectedItem();
			loe.getLineageMeasurement().mergeConsecutiveNuclei(selected, tn);
			loe.setCurrentTime(time);
			loe.setSelectedNucleus(selected);
			loe.getLineageActionHandler().actionPerformed(
					new ActionEvent(loe.getLineagerFrame(), 1,
							LineagerActionHandler.TREECHANGED));
			setVisible(false);
		} else if (e.getActionCommand().equals(CANCEL)) {
			setVisible(false);
		}

	}

}
