package org.kuleuven.storecaching;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class StoreMapCaching<K, V> {
  protected Map<K, SoftReference<V>> index = new HashMap<K, SoftReference<V>>();
  
  public abstract int size();
  
  protected abstract V getEntryFromStoreWithID(K id);
  
  protected abstract Map<K, V> getEntriesFromStoreWithIDs(Collection<K> ids);
  
  protected abstract void setEntryInStore(K id,V value); 
  public void clear(){
    index.clear();
  }
  
  
  public V get(K key) {
    V result = getFromCache(key);
    
    if (result == null)
      return fetch(key);
    else
      return result;
  }
  public void set(K id, V value){
    setEntryInStore(id,value);
    index.put(id, new SoftReference<V>(value));
  }
  
  protected V getFromCache(K key) {
    SoftReference<V> softReference = index.get(key);
    
    if (softReference != null) {
      V result = softReference.get();
      
      if (result == null)
        index.remove(key);
      
      return result;
    }
    else
      return null;
  }
  
  protected V fetch(K key) {
    V v = getEntryFromStoreWithID(key);
    index.put(key, new SoftReference<V>(v));
    return v;
  }
  
  public Map<K, V> getSubMap(Collection<K> keys) {
    Map<K, V> result = new HashMap<K, V>(new Long(Math.round(1.34*keys.size())).intValue());
    List<K> keysToFetch = new ArrayList<K>();
    
    for (K key: keys) {
      V v = getFromCache(key);
      
      if (v != null)
        result.put(key, v);
      else
        keysToFetch.add(key);
    }
    if (!keysToFetch.isEmpty()) {
      Map<K, V> entries = getEntriesFromStoreWithIDs(keysToFetch);
      result.putAll(entries);
      for (Entry<K,V> entry: entries.entrySet()) {
        if (entry.getValue()!=null){
          
          index.put(entry.getKey(), new SoftReference<V>(entry.getValue()));
        }
      }
    }
    
    return result;
  }
  
}
