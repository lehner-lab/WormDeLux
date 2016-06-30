package org.kuleuven.math.vector;

import org.kuleuven.math.space.Space;

public class ArrayVector<D> extends IndexedVector<D> {
  public double[] values;
  public Space<D> space;

  public ArrayVector(Space<D> space) {
    super();
    
    setSpace(space);
  }
  
  public ArrayVector(Vector<D> vector) {
    super(vector);
  }
  
  public ArrayVector(Space<D> space, double[] array) {
    this.space = space;
    values = array;
  }
  
  @Override
public void setByIndex(int index, double value) {
    values[index] = value;
  }
  
  @Override
public double getByIndex(int index) {
    return values[index];
  }

  @Override
public Space<D> getSpace() {
    return space;
  }

  @Override
public void setSpace(Space<D> space) {
    this.space = space;
    values = new double[space.getDimensions()];
  }
}
