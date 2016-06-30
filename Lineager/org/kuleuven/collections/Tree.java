package org.kuleuven.collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Tree<K> extends Node<K> {
	private Map<K, Set<K>> parentMap = new HashMap<K, Set<K>>();
	private boolean redundancyremoved = true;

	public Tree(K root) {
		super(root);
	}

	public Set<Node<K>> getAllLeafNodes() {
		Set<Node<K>> all = getAllNodesBelowNode();
		Set<Node<K>> result = new HashSet<Node<K>>();
		for (Node<K> node : all) {
			if (node.getChildren().size() == 0) {
				result.add(node);
			}
		}
		return result;
	}

	public void removeParentChildRelation(K parent, K child) {
		Set<K> parents = parentMap.get(child);
		parents.remove(parent);
		Node<K> childNode = getNode(parent).removeChild(child);

		add(childNode);
		redundancyremoved = false;
	}

	public void addParentChildRelation(K parent, K child) {
		redundancyremoved = false;
		Node<K> parentNode = getNode(parent);
		Node<K> childNode = getNode(child);

		if (parentNode == null)
			parentNode = this.add(parent);

		if (childNode == null)
			childNode = this.add(child);

		parentNode.add(childNode);

	}

	public Node<K> getRootNode() {
		Set<K> children = new HashSet<K>();
		for (Node<K> node : this.children.values()) {
			for (Node<K> child : node.children.values()) {
				children.add(child.id);
			}
		}
		for (K child : children)
			this.removeChild(child);

		if (!redundancyremoved)
			this.removeReduncancy();
		return this;
	}

	public Set<K> getParents(K child) {
		if (!redundancyremoved)
			removeReduncancy();
		return parentMap.get(child);
	}

	public Set<K> getAllParents(K child) {
		Set<K> result = new HashSet<K>();
		while (parentMap.containsKey(child)) {
			Set<K> p = getParents(child);
			child = p.iterator().next();
			result.addAll(p);
		}
		return result;
	}

	@Override
	public void removeReduncancy() {
		super.removeReduncancy();
		this.redundancyremoved = true;
		buildParentsMap(this);
	}

	private void buildParentsMap(Node<K> parent) {
		Map<K, Node<K>> childrenMap = parent.getChildrenTree();
		for (Node<K> child : childrenMap.values()) {
			Set<K> parentList = parentMap.get(child.id);
			if (parentList == null) {
				parentList = new HashSet<K>();
				parentMap.put(child.id, parentList);
			}
			parentList.add(parent.id);
			buildParentsMap(child);
		}
	}
}
