package org.kuleuven.lineagetree;

import org.kuleuven.collections.Tree;

public abstract class DistancedTree<E> extends Tree<E> implements HasDistance {

	public DistancedTree(E root) {
		super(root);
	}

	@Override
	public abstract double getDistance();

	public abstract double getStart();
}
