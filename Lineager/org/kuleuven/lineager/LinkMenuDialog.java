package org.kuleuven.lineager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.kuleuven.collections.Node;
import org.kuleuven.lineagetree.LineageMeasurement;
import org.kuleuven.lineagetree.Nucleus;
import org.kuleuven.lineagetree.TrackedNucleus;
import org.kuleuven.utilities.StringUtilities;

public class LinkMenuDialog extends JDialog {
	private static final long serialVersionUID = -2028008704353352898L;
	private final LineageMeasurement lm;
	private JComboBox parentsDropDownBox;
	private JTextField parentTimeField;
	private JTextField limitChildTime;
	private JComboBox childDropDownBox;
	private Integer parenttime;
	private JButton acceptButton;
	private JButton closeButton;
	private boolean divisionMode = true;
	protected boolean restrictMode = true;

	public LinkMenuDialog(final LineagerObjectExchange loe) {
		this.lm = loe.getLineageMeasurement();
		this.parenttime = loe.getCurrentTime();
		setMinimumSize(new Dimension(300, 200));
		setTitle("Link Nuclei");

		JPanel main = new JPanel(new GridLayout(7, 2));
		getContentPane().add(main);
		JRadioButton jr1 = new JRadioButton("Division");
		jr1.setSelected(true);
		main.add(jr1);
		jr1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				divisionMode = true;
			}
		});
		JRadioButton jr2 = new JRadioButton("Combine");
		jr2.setActionCommand("Combine");
		ButtonGroup bg = new ButtonGroup();
		bg.add(jr1);
		bg.add(jr2);
		main.add(jr2);

		jr2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				divisionMode = false;
			}
		});
		JRadioButton restrictJR = new JRadioButton("Restrict");
		restrictJR.setSelected(true);
		main.add(restrictJR);
		restrictJR.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				restrictMode = true;
				fillParentsBox();
				fillChildrensBox();

			}
		});
		JRadioButton showAllJR = new JRadioButton("Show all");
		showAllJR.setActionCommand("Show All");
		ButtonGroup bg2 = new ButtonGroup();
		bg2.add(restrictJR);
		bg2.add(showAllJR);
		main.add(showAllJR);

		showAllJR.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				restrictMode = false;
				fillParentsBox();
				fillChildrensBox();
			}
		});

		main.add(new JLabel("Set parent end time"));
		parentTimeField = new JTextField(parenttime.toString());
		parentTimeField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String text = parentTimeField.getText();
				if (StringUtilities.isInteger(text)) {
					Integer newTime = Integer.parseInt(text);
					if (checkTime(newTime)) {
						parenttime = newTime;
						limitChildTime.setText(Integer.toString(parenttime + 1));
						fillParentsBox();
						fillChildrensBox();
					}
				}
			}
		});
		main.add(parentTimeField);
		main.add(new JLabel("Parent"));
		parentsDropDownBox = new JComboBox();
		fillParentsBox();
		main.add(parentsDropDownBox);
		main.add(new JLabel("Child start time"));

		limitChildTime = new JTextField(Integer.toString(parenttime + 1));
		limitChildTime.setEditable(false);
		limitChildTime.setBackground(Color.LIGHT_GRAY);
		limitChildTime.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String text = limitChildTime.getText();
				/*
				 * if (StringUtilities.isInteger(text)) { Integer newTime =
				 * Integer.parseInt(text); if (checkTime(newTime)) {
				 * childrenTime = newTime; fillChildrensBox(); } }
				 */// functionality removed for now

			}
		});
		main.add(limitChildTime);
		main.add(new JLabel("Child"));
		childDropDownBox = new JComboBox();
		fillChildrensBox();
		main.add(childDropDownBox);

		acceptButton = new JButton("Accept");
		acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (parentsDropDownBox.getItemCount() > 0 && childDropDownBox.getItemCount() > 0) {

					TrackedNucleus papa = (TrackedNucleus) parentsDropDownBox.getSelectedItem();
					TrackedNucleus child = (TrackedNucleus) childDropDownBox.getSelectedItem();
					if (divisionMode) {
						changeParentChildRelation(papa, child);
					} else {
						mergeConsecutiveNuclei(papa, child);
					}
					fillChildrensBox();
					fillParentsBox();
					loe.getLineageActionHandler().actionPerformed(
							new ActionEvent(this, 1, LineagerActionHandler.FOCUS));
				}

			}

		});
		main.add(acceptButton);
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);

				// setVisible(false);
				dispose();
			}
		});
		main.add(closeButton);

	}

	@Override
	protected void finalize() throws Throwable {
		System.out.println("LinkMenuDialog destroyed");
		super.finalize();
	}

	private void fillParentsBox() {
		List<Nucleus> map = lm.getNucleiForATimepoint(parenttime);
		parentsDropDownBox.removeAllItems();
		parentsDropDownBox.addItem(lm.lineage.getID());
		for (Nucleus n : map) {
			TrackedNucleus tn = lm.getTrackedNucleusForNucleus(n);
			Node<TrackedNucleus> node = lm.lineage.getNode(tn);
			if (tn.getLastTimePoint() == parenttime) {
				if (restrictMode) {
					if (node != null && node.getChildren().size() < 2) {
						parentsDropDownBox.addItem(tn);
					}
				} else {
					parentsDropDownBox.addItem(tn);
				}
			}
		}
	}

	private void fillChildrensBox() {
		List<Nucleus> list = lm.getNucleiForATimepoint(parenttime + 1);
		childDropDownBox.removeAllItems();
		for (Nucleus n : list) {
			TrackedNucleus tn = lm.getTrackedNucleusForNucleus(n);
			Set<TrackedNucleus> parents = lm.lineage.getParents(tn);
			if (tn.getFirstTimePoint() == (parenttime + 1)) {
				if (restrictMode) {
					if (parents == null || parents.size() == 0
							|| parents.contains(lm.lineage.getID())) {
						childDropDownBox.addItem(tn);
					}
				} else {
					childDropDownBox.addItem(tn);
				}
				{

				}
			}
		}
	}

	public boolean checkTime(Integer i) {
		return lm.getNucleiOverTime().containsKey(i);
	}

	public boolean changeParentChildRelation(TrackedNucleus parent, TrackedNucleus child) {
		return lm.changeParentChildRelation(parent, child);
	}

	private boolean mergeConsecutiveNuclei(TrackedNucleus papa, TrackedNucleus child) {
		return lm.mergeConsecutiveNuclei(papa, child);
	}

}
