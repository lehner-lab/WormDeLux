package org.kuleuven.collections;

import java.io.Serializable;
import java.util.Iterator;

public class SortedIntList2FloatMap implements Serializable {
  private static final long serialVersionUID = -2857486206204805801L;
  private IntList keys;
  private FloatList values;

  // make supersleak cursor! for high speed reading
  public SortedIntList2FloatMap() {
    keys = new IntList();
    values = new FloatList();
  }

  public SortedIntList2FloatMap(int initialCapacity) {
    keys = new IntList(initialCapacity);
    values = new FloatList(initialCapacity);
  }

  public SortedIntListSet getKeySet() {
    return new SortedIntListSet(keys);
  }
  public FloatList values(){
    return values;
  }
  /**
   * special function for efficiency if you add an entry to end by bypassing
   * binarysearch
   */
  public void addEntry(int key, float value) {
    keys.add(key);
    values.add(value);
  }

  public int getIndexForKey(int key) {
    return binarySearch(key);
  }

  public Integer guidedGetIndexForKey(int key, int low, int high) {
    return binarySearch(key, low, high);
  }

  public void put(int key, float element) {
    int index = binarySearch(key);

    if (index < keys.size()) {

      if (key == keys.getInt(index))
        values.set(index, element);
      else {
        keys.add(index, key);
        values.add(index, element);
      }
    }
    else {
      keys.add(index, key);
      values.add(index, element);
    }

  }

  public boolean containsKey(int key) {
    float v = get(key);
    if (Float.isNaN(v)) {
      return false;
    }
    else {
      return true;
    }
  }

  public void putAll(SortedIntList2FloatMap map) {
    Iterator<Integer> iterator = map.keyIterator();
    while (iterator.hasNext()) {
      int k = iterator.next();
      put(k, map.get(k));
    }
  }

  public float guidedGet(int key, int low, int high) {
    int index = binarySearch(key, low, high);

    if (index < keys.size()) {

      if (key == keys.getInt(index))
        return values.get(index);
    }

    return Float.NaN;
  }

  public float get(int key) {
    return guidedGet(key, 0, keys.size());
  }

  public int getKey(int index) {
    return keys.getInt(index);
  }

  public float getValue(int index) {
    return values.get(index);
  }

  public float remove(int key) {
    int index = binarySearch(key);

    if (index < keys.size()) {
      if (keys.getInt(index) == key) {
        keys.remove(index);
        float value = values.get(index);
        values.remove(index);
        return value;
      }
    }
    return Float.NaN;
  }

  protected int binarySearch(int key, int low, int high) {
    int middle;

    while (low < high) {
      middle = (low + high) / 2;

      if (key > keys.getInt(middle))
        low = middle + 1;
      else
        high = middle;
    }

    return low;
  }

  protected int binarySearch(int key) {
    int low = 0, high = keys.size();
    return binarySearch(key, low, high);
  }

  public void pack() {
    keys.trimToSize();
    values.trimToSize();
  }

  public int size() {
    return keys.size();
  }

  public void clear() {
    keys.clear();
    values.clear();
  }

  public Iterator<Integer> keyIterator() {
    return new KeyIterator();
  }

  public Iterator<MapEntry> entryIterator() {
    return new EntryIterator();
  }

  private class KeyIterator implements Iterator<Integer> {
    private int currentIndex = 0;

    @Override
	public boolean hasNext() {
      return currentIndex < keys.size();
    }

    @Override
	public Integer next() {
      return keys.getInt(currentIndex++);
    }

    @Override
	public void remove() {
    }

  };

  public class MapEntry {
    int key;
    float value;

    public MapEntry(int key, float value) {
      this.key = key;
      this.value = value;
    }

    public int getKey() {
      return key;
    }

    public float getValue() {
      return value;
    }
  }

  public MapCursor<Integer, Float> getEntryCursor() {
    return new EntryCursor();
  }

  protected class EntryCursor implements MapCursor<Integer, Float> {
    private int index = 0;
    private boolean removeCheck = false;

    @Override
	public boolean isValid() {
      return index < keys.size() && !removeCheck;
    }

    @Override
	public Integer key() {
      if (isValid())
        return keys.getInt(index);
      else
        return null;
    }

    @Override
	public void next() {
      if (removeCheck)
        removeCheck = false;
      index++;
    }

    @Override
	public void setValue(Float value) {
      if (isValid())
        values.set(index, value);
    }

    @Override
	public Float value() {
      if (isValid())
        return values.get(index);
      else
        return null;
    }

    @Override
	public Float remove() {
      if (isValid()) {
        Float value = values.get(index);
        values.remove(index);
        keys.remove(index);
        removeCheck = true;
        index--;
        return value;
      }
      else {
        return null;
      }
    }
  }

  protected class EntryIterator implements Iterator<MapEntry> {
    private int index = 0;

    @Override
	public boolean hasNext() {
      return index < keys.size();
    }

    @Override
	public MapEntry next() {
      return new MapEntry(keys.getInt(index), values.get(index++));
    }

    @Override
	public void remove() {
      int removeindex = index - 1;
      if (removeindex > 0) {
        keys.remove(removeindex);
        values.remove(removeindex);
        index = removeindex;
      }

    }

  }

}
