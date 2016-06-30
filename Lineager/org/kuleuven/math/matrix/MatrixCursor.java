package org.kuleuven.math.matrix;

public interface MatrixCursor<D1, D2> extends MatrixHandle<D1, D2> {
  public boolean isValid();
  public void next();
}
