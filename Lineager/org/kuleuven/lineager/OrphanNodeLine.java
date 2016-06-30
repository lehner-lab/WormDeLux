package org.kuleuven.lineager;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

public class OrphanNodeLine extends JComponent {
	protected Color color = Color.LIGHT_GRAY;
	private int x1;
	private int x2;
	private int y1;
	private int y2;
	private NodeLine nl;

	public OrphanNodeLine(int x1, int position, int y1, int y2) {
		this.x1 = x1;
		this.x2 = position;
		this.y1 = y1;
		this.y2 = y2;
	}

	public OrphanNodeLine(NodeLine nl, int position) {
		this.x2 = position;// TODO Auto-generated constructor stub
		this.nl = nl;
	}

	@Override
	public void paint(Graphics graphics) {

		Color c = graphics.getColor();
		graphics.setColor(color);
		if (nl != null) {
			graphics.drawLine(nl.x1, nl.y1, x2, nl.y2);
		} else {
			graphics.drawLine(x1, y1, x2, y2);
		}
		graphics.setColor(c);
	}

}
