package org.kuleuven.math.vector;

public class ParallelVectorCursor<D> {
  protected VectorCursor<D> masterCursor;
  protected VectorSlaveCursor<D> slaveCursor;
  public VectorHandle<D> lhs, rhs;
  
  public ParallelVectorCursor(VectorCursor<D> masterCursor, VectorSlaveCursor<D> slaveCursor, boolean swap) {
    this.masterCursor = masterCursor;
    this.slaveCursor = slaveCursor;
    
    if (swap) {
      lhs = slaveCursor;
      rhs = masterCursor;
    }
    else {
      lhs = masterCursor;
      rhs = slaveCursor;
    }
    if (masterCursor.isValid())
      slaveCursor.synchronize(masterCursor);
  }
  
  public boolean isValid() {
    return masterCursor.isValid();
  }
  
  public void next() {
    masterCursor.next();
    
    if (masterCursor.isValid())
      slaveCursor.synchronize(masterCursor);
  }
}
