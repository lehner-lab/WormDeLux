package org.kuleuven.collections;

public interface MapCursor<K,V> {
  public boolean isValid();
  public void next();
  public V value();
  public K key();
  public void setValue(V value);
  public V remove();
}
