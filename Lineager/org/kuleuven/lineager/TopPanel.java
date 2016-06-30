package org.kuleuven.lineager;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class TopPanel extends JPanel {
	private SpringLayout layout;
	private int locationFirst;
	private int spacingHorizontal;
	private int spacingVertical;

	public TopPanel() {
		layout = new SpringLayout();
		setLayout(layout);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setPreferredSize(new Dimension(700, 100));
	}

	public TopPanel(int border, Dimension preferredSize, int locationFirst, int spacingHorizontal,int spacingVertical) {
		layout = new SpringLayout();
		setLayout(layout);
		setBorder(BorderFactory.createEmptyBorder(border, border, border, border));
		setPreferredSize(preferredSize);
		this.locationFirst = locationFirst;
		this.spacingHorizontal= spacingHorizontal;
		this.spacingVertical=spacingVertical;
	}

	public void addFirst(Component c, int xLocation) {
		this.add(c);
		layout.putConstraint(SpringLayout.NORTH, c, 0, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, c, xLocation, SpringLayout.WEST, this);
	}

	public void addFirst(Component c) {
		addFirst(c, locationFirst);
	}

	public void pegToTheLeft(Component newC, Component ref) {
		this.add(newC);
		layout.putConstraint(SpringLayout.NORTH, newC, 0, SpringLayout.NORTH, ref);
		layout.putConstraint(SpringLayout.EAST, newC, -spacingHorizontal, SpringLayout.WEST, ref);
	}

	public void pegToTheLeft(Component newC, Component ref,int distance) {
		this.add(newC);
		layout.putConstraint(SpringLayout.NORTH, newC, 0, SpringLayout.NORTH, ref);
		layout.putConstraint(SpringLayout.EAST, newC, -distance, SpringLayout.WEST, ref);
	}

	public void pegToTheSouth(Component newC, Component ref) {
		pegToTheSouth(newC, ref, spacingVertical);
	}

	public void pegToTheSouth(Component newC, Component ref, int distance) {
		this.add(newC);
		layout.putConstraint(SpringLayout.NORTH, newC, distance, SpringLayout.SOUTH, ref);
		layout.putConstraint(SpringLayout.WEST, newC, 0, SpringLayout.WEST, ref);
	}

	public void pegToTheRight(Component newC, Component ref) {
		this.add(newC);
		layout.putConstraint(SpringLayout.NORTH, newC, 0, SpringLayout.NORTH, ref);
		layout.putConstraint(SpringLayout.WEST, newC, spacingHorizontal, SpringLayout.EAST, ref);
	}
}
