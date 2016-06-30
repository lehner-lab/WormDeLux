package org.kuleuven.math.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListSpace<D> extends DefaultSpace<D> {
  
  public List<D> list;  
  protected Map<D, Integer> index = new HashMap<D, Integer>();

  public ListSpace(List<D> list) {
    this.list = new ArrayList<D>(list);
    int i = 0;
    
    for (D object: list)
      index.put(object, i++);
  }
  public ListSpace(Collection<D> collection){
    this(new ArrayList<D>(collection));
  }
  
  @Override
public int getDimensions() {
    return list.size();
  }

  @Override
public int indexOfObject(D object) {
    Integer result = index.get(object);
    if (result == null)
      return -1;
    else
      return result;
  }

  @Override
public D objectForIndex(int index) {
    return list.get(index);
  }

  @Override
public Iterator<D> iterator() {
    return list.iterator();
  }
}
