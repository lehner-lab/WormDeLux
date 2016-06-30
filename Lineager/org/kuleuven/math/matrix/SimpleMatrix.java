package org.kuleuven.math.matrix;

import org.kuleuven.math.space.IntegerSpace;

public class SimpleMatrix extends RowArrayMatrix<Integer, Integer> {
  public SimpleMatrix(int rows, int columns) {
    super(new IntegerSpace(rows), new IntegerSpace(columns));
  }
}
