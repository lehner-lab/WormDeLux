package org.kuleuven.math.space;

import java.io.Serializable;
import java.util.Iterator;

public class IntegerSpace extends DefaultSpace<Integer> implements Serializable {
  private static final long serialVersionUID = -5957501459753317173L;
  public int dimensions;
  public static Space<Integer> oneD = new IntegerSpace(1);
  public static Space<Integer> twoD = new IntegerSpace(2);
  public static Space<Integer> threeD = new IntegerSpace(3);

  
  public IntegerSpace(int dimensions) {
    this.dimensions = dimensions;
  }
  public IntegerSpace(){
    this.dimensions= Integer.MAX_VALUE;
  }
  
  @Override
public int getDimensions() {
    return dimensions;
  }

  @Override
public int indexOfObject(Integer object) {
    return object;
  }

  @Override
public Integer objectForIndex(int index) {
    return index;
  }

  @Override
public Iterator<Integer> iterator() {
    return new IntegerIterator(dimensions);
  }
}
