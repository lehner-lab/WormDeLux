package org.kuleuven.math.space;

import java.util.Iterator;

public class IntegerIterator implements Iterator<Integer> {
  protected int current, size;
  
  public IntegerIterator(int size) {
    current = 0;
    this.size = size; 
  }

  @Override
public boolean hasNext() {
    return current < size;
  }

  @Override
public Integer next() {
    return current++;
  }

  @Override
public void remove() {
  }
}
