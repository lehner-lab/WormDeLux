package org.kuleuven.math.vector;

import org.kuleuven.math.space.IntegerSpace;

public class SimpleVector extends ArrayVector<Integer> {
  public SimpleVector(int dimensions) {
    super(new IntegerSpace(dimensions));
  }
}
