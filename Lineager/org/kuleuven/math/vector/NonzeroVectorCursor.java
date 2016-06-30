package org.kuleuven.math.vector;

public class NonzeroVectorCursor<D> implements VectorCursor<D> {
  protected VectorCursor<D> vectorCursor;
  
  public NonzeroVectorCursor(VectorCursor<D> vectorCursor) {
    this.vectorCursor = vectorCursor;
    
    while (vectorCursor.isValid() && vectorCursor.get() == 0)
      vectorCursor.next();
  }
  
  @Override
public boolean isValid() {
    return vectorCursor.isValid();
  }

  @Override
public void next() {
    do
      vectorCursor.next();
    while (vectorCursor.isValid() && vectorCursor.get() == 0);
  }

  @Override
public D dimension() {
    return vectorCursor.dimension();
  }

  @Override
public int index() {
    return vectorCursor.index();
  }

  @Override
public double get() {
    return vectorCursor.get();
  }

  @Override
public void set(double value) {
  }
}
