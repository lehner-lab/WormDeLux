package org.kuleuven.math.vector;

public interface VectorSlaveCursor<D> extends VectorHandle<D> {
  public void synchronize(VectorHandle<D> vectorHandle);
}
