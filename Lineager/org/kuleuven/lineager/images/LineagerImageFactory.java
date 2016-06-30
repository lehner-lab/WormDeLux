package org.kuleuven.lineager.images;

import javax.swing.ImageIcon;

public class LineagerImageFactory {
	public static ImageIcon getCircleSImage() {
		return new ImageIcon(LineagerImageFactory.class.getResource("circleS.png"));
	}

	public static ImageIcon getCircleXSImage() {
		return new ImageIcon(LineagerImageFactory.class.getResource("circleXS.png"));
	}

	public static ImageIcon getUpArrowImage() {
		return new ImageIcon(LineagerImageFactory.class.getResource("upArrowS.png"));
	}

	public static ImageIcon getDownArrowImage() {
		return new ImageIcon(LineagerImageFactory.class.getResource("downArrowS.png"));
	}
	public static ImageIcon getLeftArrowImage() {
	    return new ImageIcon(LineagerImageFactory.class.getResource("leftArrowS.png"));
	  }
	public static ImageIcon getRightArrowImage() {
	    return new ImageIcon(LineagerImageFactory.class.getResource("rightArrowS.png"));
	  }



}
