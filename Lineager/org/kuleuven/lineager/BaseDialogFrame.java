package org.kuleuven.lineager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class BaseDialogFrame extends JFrame implements ActionListener {

	protected static final long serialVersionUID = 302564217699184175L;
	protected JPanel base;
	protected TopPanel top;
	protected JButton saveButton;
	protected JButton cancelButton;

	protected String CANCEL="Cancel";
	protected String SAVE = "Save";

	public BaseDialogFrame() {
		base = new JPanel(new BorderLayout());
		top = defineTopPanel();
		base.add(top, BorderLayout.CENTER);
	

		JPanel bottom = new JPanel();
	
		saveButton = new JButton("  OK  ");
		saveButton.setActionCommand(SAVE);
		saveButton.addActionListener(this);

	cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand(CANCEL);
		cancelButton.addActionListener(this);
		// cancelButton.setPreferredSize(buttonDimension);
		bottom.add(saveButton);
		bottom.add(cancelButton);
		base.add(bottom, BorderLayout.SOUTH);
		getContentPane().add(base);
		setResizable(false);
	}

	protected void addFirstComponent(String label, Component component) {
		JLabel name = new JLabel(label);
		top.addFirst(component);
		top.pegToTheLeft(name, component);
	}

	protected void addComponent(String label,JComponent component,Component componentAbove){
		top.pegToTheSouth(component, componentAbove);
		JLabel l =new JLabel(label);
		top.pegToTheLeft(l, component);
	}
	protected abstract TopPanel defineTopPanel();
}
