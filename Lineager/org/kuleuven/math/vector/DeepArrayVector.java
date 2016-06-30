package org.kuleuven.math.vector;

import org.kuleuven.math.space.Space;

public class DeepArrayVector<D> extends IndexedVector<D> {
  protected Space<D> space;
  protected double[][] values;
  protected int secondIndex;
  
  public DeepArrayVector(Space<D> space, double[][] values, int secondIndex) {
    this.space = space;
    this.values = values;
    this.secondIndex = secondIndex;
  }
  
  @Override
public Space<D> getSpace() {
    return space;
  }

  @Override
public void setSpace(Space<D> space) {
  }

  @Override
public void setByIndex(int index, double value) {
    values[index][secondIndex] = value;
  }

  @Override
public double getByIndex(int index) {
    return values[index][secondIndex];
  }
}
