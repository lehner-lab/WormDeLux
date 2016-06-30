package org.kuleuven.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class SortedIntListSet implements Set<Integer>, Serializable {
  private static final long serialVersionUID = 7178025949709731944L;
  protected IntList entries;

  public SortedIntListSet() {
    entries = new IntList();
  }

  public SortedIntListSet(int initialCapacity) {
    entries = new IntList(initialCapacity);
  }

  public SortedIntListSet(IntList setEntries) {
    // if you use this constructor: know what you are doing!
    // the list is expected to be correctly sorted with only unique entries
    this.entries = setEntries;
  }

  @Override
public String toString() {
    StringBuffer result = new StringBuffer("[");
    Iterator iter = iterator();
    if (iter.hasNext()) {
      result.append(iter.next().toString());
    }
    while (iter.hasNext()) {
      result.append(",");
      result.append(iter.next().toString());
    }
    result.append("]");
    return result.toString();
  }

  @Override
public boolean add(Integer key) {
    int index = binarySearch(key);

    if (index < entries.size()) {
      int k = entries.getInt(index);

      if (key.intValue() == k) {
        return false;

      }
      else
        entries.add(index, key);
    }
    else
      entries.add(index, key);

    return true;
  }

  public boolean guidedContains(int key, int low, int high) {
    int index = binarySearch(key, low, high);

    if (index < entries.size()) {

      if (key == entries.getInt(index))
        return true;
    }
    return false;
  }

  public boolean contains(int key) {
    return guidedContains(key, 0, entries.size());
  }

  protected int binarySearch(int key, int low, int high) {
    int middle;

    while (low < high) {
      middle = (low + high) / 2;

      if (key > entries.getInt(middle))
        low = middle + 1;
      else
        high = middle;
    }

    return low;
  }

  public int binarySearch(int key) {
    int low = 0, high = entries.size();
    return binarySearch(key, low, high);
  }

  @Override
public boolean addAll(Collection<? extends Integer> c) {
    Iterator<? extends Integer> iterator = c.iterator();
    boolean check = false;
    while (iterator.hasNext()) {
      if (add(iterator.next())) {
        check = true;
      }
    }
    return check;
  }

  @Override
public void clear() {
    entries.clear();
  }

  @Override
public boolean contains(Object o) {
    if (o instanceof Integer) {
      Integer id = (Integer) o;
      return contains(id.intValue());
    }
    return false;
  }

  @Override
public boolean containsAll(Collection<?> c) {
    Iterator<?> iterator = c.iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof Integer) {
        Integer id = (Integer) o;
        if (!contains(id.intValue())) {
          return false;
        }
      }
      else
        return false;
    }
    return true;
  }

  /**
   * This is a special function if you know FOR SURE that you have a properly
   * sorted intlist with only unique entries, and you would like to set it as
   * the list underlying the set. Note that the old list is lost.
   */
  public void setSortedList(IntList list) {
    entries = list;
  }

  /** This returns the precious sorted list: Don't fuck it up! (please)
   *  You can do all that you want now that it is a copy..... GO YOUR GANG !
   * */
  public IntList getSortedList() {
    return new IntList(entries);
  }

  @Override
public boolean isEmpty() {
    if (entries.size() == 0)
      return true;
    else
      return false;
  }

  @Override
public Iterator<Integer> iterator() {

    return entries.iterator();
  }

  @Override
public boolean remove(Object o) {
    if (o instanceof Integer) {
      Integer key = (Integer) o;
      int index = binarySearch(key);
      if (index < entries.size()) {
        if (key == entries.getInt(index)) {
          entries.remove(index);
          return true;
        }
      }
    }
    return false;
  }

  @Override
public boolean removeAll(Collection<?> c) {
    Iterator it = c.iterator();
    boolean check = false;
    while (it.hasNext()) {
      if (remove(it.next())) {
        check = true;
      }
    }
    return check;
  }

  @Override
public boolean retainAll(Collection<?> c) {

    Iterator<Integer> it = iterator();
    boolean check = false;
    while (it.hasNext()) {
      Integer current = it.next();
      if (!c.contains(current)) {
        check = true;
        it.remove();
      }
    }
    return check;
  }

  @Override
public int size() {
    return entries.size();
  }

  @Override
public Object[] toArray() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
public <T> T[] toArray(T[] a) {
    // TODO Auto-generated method stub
    return null;
  }
public SortedIntListSet getSubstraction (SortedIntListSet set2){
  //hurryhurry, not perfectly optimized
  
  IntList resultList = new IntList(size());
  SortedIntListSet intersect = getIntersection(set2);
  for (int i=0;i<entries.size();i++){
    int key = entries.getInt(i);
    if (!intersect.contains(key)){
      resultList.add(key);
    }
  }
  SortedIntListSet result = new SortedIntListSet(resultList);
  return result;
}
  public SortedIntListSet getIntersection(SortedIntListSet set2) {
    SortedIntListSet shorter = set2;
    SortedIntListSet longer = this;
    if (shorter.size() > longer.size()) {
      shorter = longer;
      longer = set2;
    }
    IntList result = new IntList(shorter.size());
    IntList top = new IntList(shorter.size() / 2);
    int longerlowestIndex = 0;
    int longerhighestIndex = longer.size() - 1;
    int longerlowest = longer.getKeyForIndex(longerlowestIndex);
    int longerhighest = longer.getKeyForIndex(longerhighestIndex);
    int shorterlowestIndex = 0;
    int shorterhighestIndex = shorter.size() - 1;
    while (shorterlowestIndex <= shorterhighestIndex) {
      int key = shorter.getKeyForIndex(shorterlowestIndex);
      if (key >= longerlowest) {
        int index = longer.guidedGetIndexForKey(key, longerlowestIndex, longerhighestIndex + 1);
        if (index < longer.size()) {
          longerlowestIndex = index;
          if (longer.getKeyForIndex(index) == key) {
            result.add(key);
          }
          longerlowest = longer.getKeyForIndex(longerlowestIndex);
        }
      }
      shorterlowestIndex++;
      if (shorterlowestIndex < shorterhighestIndex) {
        key = shorter.getKeyForIndex(shorterhighestIndex);
        if (key <= longerhighest) {
          int index = longer.guidedGetIndexForKey(key, longerlowestIndex, longerhighestIndex + 1);
          if (index < longer.size()) {
            longerhighestIndex = index;
            if (longer.getKeyForIndex(index) == key) {
              top.add(key);
            }
            longerhighest = longer.getKeyForIndex(longerhighestIndex);
          }
        }
        shorterhighestIndex--;
      }
    }
    for (int i = top.size() - 1; i >= 0; i--)
      result.add(top.getInt(i));
    result.trimToSize();
    return new SortedIntListSet(result);
  }

  private int guidedGetIndexForKey(int key, int low, int high) {
    return binarySearch(key, low, high);
  }

  public int getKeyForIndex(int index) {
    return entries.getInt(index);
  }
}
