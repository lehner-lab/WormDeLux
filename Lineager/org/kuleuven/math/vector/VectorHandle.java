package org.kuleuven.math.vector;

public interface VectorHandle<D> {
  public D dimension();
  public int index();
  public double get();
  public void set(double value);
}
