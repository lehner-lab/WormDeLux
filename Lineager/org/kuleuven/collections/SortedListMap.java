package org.kuleuven.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SortedListMap<K, V> implements  Serializable, Map<K, V> {
  private static final long serialVersionUID = -2937148184646196003L;
  protected ArrayList<MapEntry<K,V>> mapEntries = new ArrayList<MapEntry<K,V>>();
  protected Comparator<K> comparator;

  public Comparator<K> getComparator() {
	return comparator;
}

public class MapEntry<K,V> implements java.util.Map.Entry<K, V> {
    protected K key;
    protected V value;

    @Override
	public K getKey() {
      return key;
    }

    @Override
	public V getValue() {
      return value;
    }

    public MapEntry(K key, V value) {
      this.key = key;
      this.value = value;
    }

    @Override
	public V setValue(V newvalue) {
      V old = this.value;
      this.value=newvalue;
      return old;
    }
   @Override
public String toString() {
	
	return "["+ key.toString() + ":"+ value.toString() + "]";
}
  }

  public SortedListMap(Comparator<K> comparator) {
    this.comparator = comparator;

  }

  /**
   * special function for efficiency if you add an entry to end by bypassing
   * binarysearch
   */
  public void addEntry(K key, V value) {
    mapEntries.add(new MapEntry(key, value));
  }

  public SortedListSet<K> getKeySet() {
    SortedListSet<K> result = new SortedListSet<K>(comparator);
    List<K> inbetween = new ArrayList<K>();
    Iterator<K> iterator = keyIterator();
    while (iterator.hasNext()) {
      inbetween.add(iterator.next());
    }
    result.setSortedList(inbetween);
    return result;
  }

  public Iterator<V> iterator() {
    return new Iterator<V>() {
      int index = 0;

      @Override
	public boolean hasNext() {
        return index < mapEntries.size();
      }

      @Override
	public V next() {
        return mapEntries.get(index++).value;
      }

      @Override
	public void remove() {
    	  mapEntries.remove(index-1);
      }
    };
  }

  public Integer getIndexForKey(K key) {
    return binarySearch(key);
  }

  public Integer guidedGetIndexForKey(K key, int low, int high) {
    return binarySearch(key, low, high);
  }

  @Override
public V put(K key, V element) {
    int index = binarySearch(key);
    V result = null;
    if (index < mapEntries.size()) {
      MapEntry<K,V> mapEntry = mapEntries.get(index);

      if (comparator.compare(key, mapEntry.key) == 0) {
        result = mapEntry.value;
        mapEntry.value = element;
      }
      else
        mapEntries.add(index, new MapEntry(key, element));
    }
    else
      mapEntries.add(index, new MapEntry(key, element));
    return result;
  }

  @Override
public boolean containsKey(Object key) {
    V v = get(key);
    if (v == null) {
      return false;
    }
    else {
      return true;
    }
  }

  public void putAll(SortedListMap<K, ? extends V> map) {
    Iterator<? extends K> iterator = map.keyIterator();
    while (iterator.hasNext()) {
      K k = iterator.next();
      put(k, map.get(k));
    }
  }

  public V guidedGet(K key, int low, int high) {
    int index = binarySearch(key, low, high);

    if (index < mapEntries.size()) {
      MapEntry<K,V> mapEntry = mapEntries.get(index);

      if (comparator.compare(key, mapEntry.key) == 0)
        return mapEntry.value;
    }

    return null;
  }

  @Override
public V get(Object key) {
    try {
      K castKey = (K) key;
      return guidedGet(castKey, 0, mapEntries.size());
    } catch (ClassCastException e) {
      // we do nothing now, just return null
      return null;
    }

  }

  public K getKey(int index) {
    return mapEntries.get(index).key;
  }

  public V getValue(int index) {
    return mapEntries.get(index).value;
  }
  public void setValue(int index, V value) {
	    mapEntries.get(index).setValue(value);
	  }

  @Override
public V remove(Object key) {

    try {
      K castKey = (K) key;
      int index = binarySearch(castKey);

      if (index < mapEntries.size()) {
        MapEntry<K,V> mapEntry = mapEntries.get(index);

        if (comparator.compare(castKey, mapEntry.key) == 0) {
          mapEntries.remove(index);
          return mapEntry.value;
        }
      }

      return null;
    } catch (ClassCastException e) {
      // we do nothing now, just return null
      return null;
    }
  }

  protected int binarySearch(K key, int low, int high) {
    int middle;

    while (low < high) {
      middle = (low + high) / 2;

      if (comparator.compare(key, mapEntries.get(middle).key) > 0)
        low = middle + 1;
      else
        high = middle;
    }

    return low;
  }

  protected int binarySearch(K key) {
    int low = 0, high = mapEntries.size();
    return binarySearch(key, low, high);
  }

  public void pack() {
    mapEntries.trimToSize();
  }

  @Override
public int size() {
    return mapEntries.size();
  }

  @Override
public void clear() {
    mapEntries.clear();
  }

  public Iterator<K> keyIterator() {
    return new KeyIterator();
  }

  public Iterator<MapEntry<K,V>> entryIterator() {
    return mapEntries.iterator();
  }

  private class KeyIterator implements Iterator<K>, Serializable {
    private static final long serialVersionUID = -2585856627438009997L;
    protected Iterator<MapEntry<K, V>> iterator = mapEntries.iterator();

    @Override
	public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
	public K next() {
      return iterator.next().key;
    }

    @Override
	public void remove() {
    }

  }

  @Override
public boolean containsValue(Object value) {
    boolean control = false;
    int i=0;
    while (i<mapEntries.size()&&!control){
      if(mapEntries.get(i++).value.equals(value)){
        control = true;
      }
    }
    return control;
  }
/**
 * this does not preserve ORDER!
 */
  @Override
public Set<java.util.Map.Entry<K, V>> entrySet() {
    Set<java.util.Map.Entry<K, V>> result =new HashSet<java.util.Map.Entry<K, V>>();
    result.addAll(mapEntries);
    return result;
  }

  @Override
public boolean isEmpty() {
    if (size()==0){
      return true;
    }
    return false;
  }

  
  @Override
public void putAll(Map<? extends K, ? extends V> m) {
    
    for (Entry<? extends K, ? extends V> entry: m.entrySet()) {
      
      put(entry.getKey(), entry.getValue());
    }
    
  }

  @Override
public Collection<V> values() {
    Iterator<MapEntry<K,V>> it = entryIterator();
    List<V> val = new ArrayList<V>();
   while (it.hasNext()){
     val.add(it.next().value);
     
   }
    return val;
  }

  @Override
public Set<K> keySet() {
    
    return getKeySet();
  };

}
