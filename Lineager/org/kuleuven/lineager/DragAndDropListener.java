package org.kuleuven.lineager;

import org.kuleuven.collections.Node;

public interface DragAndDropListener<K> {
	public void dragAndDrop(Node<K> acceptor, Node<K> arrival);
}
