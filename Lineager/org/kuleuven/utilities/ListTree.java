package org.kuleuven.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListTree<T, O> {
  public Map<T, ListTree<T, O>> subTree = new HashMap<T, ListTree<T, O>>();

  public O value = null;

  public boolean put(List<T> list, O object) {
    ListTree<T, O> node = this;
    for (T item: list) {
      ListTree<T, O> nextNode = node.subTree.get(item);
      if (nextNode == null) {
        nextNode = new ListTree<T, O>();
        node.subTree.put(item, nextNode);
      }
      node = nextNode;
    }
    if (node.value == null || !node.value.equals(object)) {
      node.value = object;
      return true;
    }
    //?if the object is already there why should the put return false?
    return false;
  }

  public ListTree<T, O> get(List<T> list) {
    ListTree<T, O> node = this;
    for (T item: list) {
      node = node.subTree.get(item);
      if (node == null)
        return null;
    }
    return node;
  }

  public List<ListTree<T, O>> terminatorSet() {
    List<ListTree<T, O>> resultList = new ArrayList<ListTree<T, O>>();
    if (value != null)
      resultList.add(this);
    for (ListTree<T, O> node: subTree.values())
      resultList.addAll(node.terminatorSet());
    return resultList;
  }

  // static final Comparator<String> lengthOrder = new Comparator<String>() {
  // public int compare(String s1, String s2) {
  // return s1.length() - s2.length();
  // }
  // };
}
