package org.kuleuven.lineager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.kuleuven.lineagetree.LineageMeasurement;

public class LMPanel2 extends JPanel implements ActionListener, ItemListener {

	private List<LineageMeasurement> lms;
	private Map<LineageMeasurement, Color> colors;
	private List<ActionListener> actionListener = new ArrayList<ActionListener>();
	private Map<JCheckBox, LineageMeasurement> boxes = new HashMap<JCheckBox, LineageMeasurement>();
	private Viewer3dFrame viewerPanel;
	private final String BG = "Background";
	private Font font;

	public LMPanel2(Map<LineageMeasurement, Color> colors, Viewer3dFrame v) {
		this.viewerPanel = v;
		this.colors = colors;
		this.font = new Font("SansSerif", Font.PLAIN, 12);
		fill();
	}

	private void fill() {
		Map<LineageMeasurement, Map<String, Color>> sublineageColors = viewerPanel
				.getSublineageColors();
		if (sublineageColors.size() > 0) {
			List<String> smap = new ArrayList<String>();
			if (sublineageColors.size() > 0) {
				smap.addAll(sublineageColors.entrySet().iterator().next().getValue().keySet());
			}
			setPreferredSize(new Dimension(100+20*sublineageColors.size(),100+20*smap.size()));
			setLayout(new GridLayout(2 + smap.size(),colors.size() + 1));
			// JLabel show = new JLabel("Show");
			JLabel lineage = new JLabel("Lineage", SwingConstants.CENTER);
			lineage.setFont(font);
			JLabel color = new JLabel("Base", SwingConstants.CENTER);
			color.setUI(new VerticalLabelUI(true));
			color.setFont(font);
			// add(show);
			add(lineage);
			add(color);
			for (String s : smap) {
				JLabel sublineage = new JLabel(s, SwingConstants.CENTER);
				add(sublineage);
			}

			for (Entry<LineageMeasurement, Color> e : colors.entrySet()) {
				JCheckBox b = new JCheckBox(e.getKey().getName());
				if (viewerPanel.isHidden(e.getKey())) {
					b.setSelected(false);
				} else {
					b.setSelected(true);
				}
				b.addItemListener(this);
				boxes.put(b, e.getKey());
				add(b);
				JButton jb = new JButton();
				jb.setActionCommand(e.getKey().getName() + ";" + BG);
				jb.addActionListener(this);
				jb.setBackground(e.getValue());
				add(jb);
				Map<String, Color> map = sublineageColors.get(e.getKey());
				for (String sl : smap) {
					JButton t = new JButton();
					t.setActionCommand(e.getKey().getName() + ";" + sl);
					t.addActionListener(this);
					t.setBackground(map.get(sl));
					add(t);
				}
			}
		}
		repaint();

	}

	public void addActionListener(ActionListener al) {
		actionListener.add(al);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String[] cells = e.getActionCommand().split(";");
		if (cells.length == 2) {
			for (Entry<LineageMeasurement, Color> lm : colors.entrySet()) {
				if (lm.getKey().getName().equals(cells[0])) {
					Color old;
					if (cells[1].equals(BG)) {
						old = lm.getValue();
					} else {
						old = viewerPanel.getSublineageColors().get(lm.getKey()).get(cells[1]);
					}
					Color newColor = JColorChooser.showDialog(this, "Choose " + cells[1]
							+ "color for " + cells[0], lm.getValue());
					if (cells[1].equals(BG)) {
						lm.setValue(newColor);
					} else {
						viewerPanel.setSublineageColor(lm.getKey(), cells[1], newColor);
					}

					for (ActionListener a : actionListener)
						a.actionPerformed(e);
					repaint();
					break;
				}
			}
		}
		removeAll();
		fill();

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		LineageMeasurement lm = boxes.get(source);
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			viewerPanel.hide(lm);
			for (ActionListener a : actionListener)
				a.actionPerformed(new ActionEvent(this, 10, "hide"));
		} else {
			viewerPanel.show(lm);
			for (ActionListener a : actionListener)
				a.actionPerformed(new ActionEvent(this, 10, "show"));
		}

	}

}
