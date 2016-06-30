package org.kuleuven.lineager;

import java.awt.Graphics;
import java.awt.Point;

import org.kuleuven.collections.Node;
import org.kuleuven.lineagetree.HasDistance;

public class NodeLine {
	public Node<HasDistance> cluster;

	public Node<HasDistance> getCluster() {
		return cluster;
	}

	public int x1, y1, x2, y2;

	public NodeLine(Node<HasDistance> cluster) {
		this.cluster = cluster;
	}

	public void set(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	public void draw(Graphics graphics) {
		graphics.drawLine(x1, y1, x2, y2);
	}

	public boolean contains(Point point) {
		if (y1 == y2)
			return (point.x >= x1) && (point.x <= x2) && (Math.abs(point.y - y1) <= 2);
		else
			return (point.y >= y1) && (point.y <= y2) && (Math.abs(point.x - x1) <= 2);
	}
	@Override
	public boolean equals(Object arg0) {
		if(arg0 instanceof NodeLine){
			NodeLine other = (NodeLine)arg0;
			if(other.getCluster().equals(getCluster())){
				return true;
			}
		}
		return super.equals(arg0);
	}
}
