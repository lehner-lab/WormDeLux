package org.kuleuven.lineagetree;

import java.util.List;

import org.kuleuven.collections.Node;

public class LineageNode extends Node<TrackedNucleus> implements IteratorAcceptor{

	public LineageNode(TrackedNucleus id) {
		super(id);

	}
	
	public void getDivisions(List<Division> divisions) {
		if (getChildren().size() > 0) {
			Division div = new Division(getID());
			for (Node<TrackedNucleus> tn : children.values()) {
				div.addDaughter(tn.getID());
				LineageNode ln = (LineageNode) tn;
				ln.getDivisions(divisions);
			}
			divisions.add(div);
		}

	}
	@Override
	public void accept(ITagent it) {
		it.run(this);
	}
}
