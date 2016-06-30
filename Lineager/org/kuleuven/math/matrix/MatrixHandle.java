package org.kuleuven.math.matrix;

import org.kuleuven.math.vector.Vector;

public interface MatrixHandle<D1, D2> {
  public D1 dimension();
  public int index();
  public Vector<D2> get();
}
