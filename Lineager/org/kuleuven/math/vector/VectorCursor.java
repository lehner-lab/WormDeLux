package org.kuleuven.math.vector;

public interface VectorCursor<D> extends VectorHandle<D> {
  public boolean isValid();
  public void next();
}
