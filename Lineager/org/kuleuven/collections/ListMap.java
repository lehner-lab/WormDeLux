package org.kuleuven.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListMap<K, V> implements Map<K, V> {
  private List<V> list;
  private Map<K, V> map;
  private Comparator<V> comparator;
  public boolean issorted = true;
  
  public ListMap () {
    list = new ArrayList<V>();
    map = new HashMap<K, V>(); 
  }

  @Override
public void clear() {
    list.clear();
    map.clear();
    issorted = true;
  }

  @Override
public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  @Override
public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
public Set<Map.Entry<K, V>> entrySet() {
    return map.entrySet();
  }

  @Override
public V get(Object key) {
    return map.get(key);
  }

  @Override
public boolean isEmpty() {
    return list.isEmpty();
  }

  @Override
public Set<K> keySet() {
    return map.keySet();
  }

  @Override
public V put(K key, V value) {
    issorted = false;
    list.add(value);
    return map.put(key, value);
  }

  @Override
public void putAll(Map m) {
    issorted = false;
    map.putAll(m);
    list.addAll(m.values());
  }

  @Override
public V remove(Object key) {
    V object = map.remove(key);
    list.remove(object);
    return object;
  }

  @Override
public int size() {
    return map.size();
  }

  @Override
public Collection<V> values() {
    return map.values();
  }
  
  public List<V> asList() {
    return list;
  }
  
  public List<V> getSortedList() {
    if(!issorted) {
      Collections.sort(list, comparator);
      issorted = true;
    }
    return list;
  }

  public List<V> getSortedList(Comparator comparator) {
    
    return getSortedList();
  }
}