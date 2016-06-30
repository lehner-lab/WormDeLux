package org.kuleuven.math.vector;

import java.util.List;

import org.kuleuven.math.space.Space;

public class DeepListVector<D> extends IndexedVector<Vector<D>> {
  protected Space<Vector<D>> space;
  protected List<Vector<D>> vectors;
  protected D secondDimension;
  
  public DeepListVector(Space<Vector<D>> space, List<Vector<D>> vectors, D secondDimension) {
    this.space = space;
    this.vectors = vectors;
    this.secondDimension = secondDimension;
  }
  
  @Override
public Space<Vector<D>> getSpace() {
    return space;
  }

  @Override
public void setSpace(Space<Vector<D>> space) {
  }

  @Override
public void setByIndex(int index, double value) {
    vectors.get(index).set(secondDimension, value);
  }

  @Override
public double getByIndex(int index) {
    return vectors.get(index).get(secondDimension);
  }
}
