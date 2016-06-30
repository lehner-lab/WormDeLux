package org.kuleuven.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuleuven.utilities.StringUtilities;

public class SortedListSet<K> implements Set<K>,Serializable {

  private static final long serialVersionUID = -3522054629441473877L;
  protected List<K> setEntries;
  protected Comparator<K> comparator;
  @Override
public String toString(){
    StringBuffer result = new StringBuffer("[");
    result.append(StringUtilities.join(setEntries,","));
    result.append("]");
    return result.toString();
      }
    
  public SortedListSet(Comparator<K> comparator) {
    this.comparator = comparator;
    setEntries =  new ArrayList<K>();
  }
  public SortedListSet(Comparator<K> comparator,List<K> setEntries) {
    //if you use this constructor: know what you are doing!
    //the list is expected to be sorted exactly as would be done by the
    //given comparator!
    this.comparator = comparator;
    this.setEntries =  setEntries;
  }  
  public Comparator<K> getComparator(){
    return comparator;
  }
  @Override
public Iterator<K> iterator() {
    return new Iterator<K> () {
      int index = 0;
      
      @Override
	public boolean hasNext() {
        return index < setEntries.size();
      }

      @Override
	public K next() {
        return setEntries.get(index++);
      }

      @Override
	public void remove() {
      }
    };
  }
  
  @Override
public boolean add(K key) {

    
    int index = binarySearch(key);
    
    if (index < setEntries.size()) {
      K k = setEntries.get(index);
      
      if (comparator.compare(key, k) == 0){
        setEntries.set(index,key);
        
      }
      else
        setEntries.add(index, key);
    }
    else
      setEntries.add(index, key);
   
    //err... always true?
    return true;
  }
  
 
  
  
  protected int binarySearch(K key) {
    int low = 0, middle, high = setEntries.size();
    
    while (low < high) {
      middle = (low + high) / 2;
      
      if (comparator.compare(key, setEntries.get(middle)) > 0) 
        low = middle + 1;
      else
        high = middle;
    }
    
    return low;
  }
  
  @Override
public int size() {
    return setEntries.size();
  }
  
  @Override
public void clear() {
    setEntries.clear();
  }
  
  

  @Override
public boolean isEmpty() {
    if (setEntries.size()==0){
      return true;
    }
    else{
      return false;
    }
  }

  @Override
public boolean contains(Object key) {
    //listen, if you're going to be so stupid to put 
    //an object in here which is not of type K, all weird shit is going to happen.
    //Don't look at me then!
    //love Rob
    
   int index = binarySearch((K)key);
    
    if (index < setEntries.size()) {
      K k = setEntries.get(index);
      
      if (comparator.compare((K)key, k) == 0){
        return true;
      }
      else
        return false;
    }
    else
      return false;
  }

  /**This is a special function if you know FOR SURE that you have a list sorted
  *according to how the Comparator would sort, and you would like to set it
  * as the list underlying the set. Note that the old list is lost.*/
  public void setSortedList(List<K> list){
    setEntries = list;
  }
  
/**This returns the precious sorted list: Don't fuck it up! (please)*/
  public List<K> getSortedList(){
    
    return setEntries;
   
  }
  @Override
public Object[] toArray() {
    
    return setEntries.toArray();
  }

  @Override
public Object[] toArray(Object[] arg0) {
    
    return setEntries.toArray(arg0);
  }

 

  @Override
public boolean remove(Object key) {
    int index = binarySearch((K)key);
    
    if (index < setEntries.size()) {
      K k = setEntries.get(index);
      
      if (comparator.compare((K)key, k) == 0){
        setEntries.remove(index);
        return true;
      }
      else
        return false;
    }
    else
      return false;
  }

  
  @Override
public boolean containsAll(Collection arg0) {
    Iterator iterator = arg0.iterator();
    while (iterator.hasNext()){
      if(!contains(iterator.next())){
        return false;
      }
    }
    return true;
  }

  @Override
public boolean addAll(Collection<? extends K> arg0) {
    Iterator<? extends K> iterator = arg0.iterator();
    while (iterator.hasNext()){
      add(iterator.next());
    }
    return true;
  }

  @Override
public boolean retainAll(Collection arg0) {
    //not very efficient!!
    List temp = new ArrayList();
    Iterator iterator = arg0.iterator();
    while (iterator.hasNext()){
      Object object = iterator.next();
      if (contains(object)){
        temp.add(object);
      }
    }
    setEntries = new ArrayList<K>();
    addAll(temp);
    return true;
  }

  @Override
public boolean removeAll(Collection arg0) {
    Iterator iterator = arg0.iterator();
    while (iterator.hasNext()){
      remove(iterator.next());
    }
    return true;
    
  }

 
  public static Comparator<Integer> getAscendingIntegerComparator(){
    return new Comparator<Integer>(){
  

    @Override
	public int compare(Integer arg0, Integer arg1) {

      return arg0-arg1;
    }
    
  };
  }
  }
