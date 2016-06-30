package org.kuleuven.lineager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LinePopup extends JFrame implements ActionListener {

	private JTextField firstField;
	private JTextField secondField;
	private JButton cancel;
	private JButton OK ;
	private Viewer3dFrame v3f;
	private JButton clear;
	private JButton color;
	private Color selectedColor=Color.red;
	

	public LinePopup(Viewer3dFrame v3f) {
		this.v3f=v3f;
		setSize(600,600);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLocationRelativeTo(null);
		

		JPanel controlsV = new JPanel(new GridLayout(4, 2));
		controlsV.add(new JLabel("First nucleus"));
		firstField = new JTextField();
		firstField.addActionListener(this);
		controlsV.add(firstField);
		controlsV.add(new JLabel("Second nucleus"));
		secondField = new JTextField();
		controlsV.add(secondField);
		secondField.addActionListener(this);
		clear=new JButton("Clear All Lines");
		clear.addActionListener(this);
		controlsV.add(clear);
		color =new JButton("Set Color");
		color.addActionListener(this);
		controlsV.add(color);
		cancel=new JButton("Cancel");
		cancel.addActionListener(this);
		controlsV.add(cancel);
		OK =new JButton("OK");
		OK.addActionListener(this);
		controlsV.add(OK);
		getContentPane().add(controlsV);
		this.pack();
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(OK)){
			v3f.setCellGroupLine(firstField.getText(),secondField.getText(),selectedColor);
			this.setVisible(false);
		}else if(arg0.getSource().equals(cancel)){
			firstField.setText("");
			secondField.setText("");
			this.setVisible(false);
		}else if(arg0.getSource().equals(color))
		{
			selectedColor=JColorChooser.showDialog(this, "Picka a Color", Color.red);
		}else if(arg0.getSource().equals(clear)){
			firstField.setText("");
			secondField.setText("");
			v3f.removeCellGroupLines();
			this.setVisible(false);
		}
			
		
	}
}
