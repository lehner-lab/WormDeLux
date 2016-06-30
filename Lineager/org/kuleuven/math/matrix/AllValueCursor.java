package org.kuleuven.math.matrix;

import org.kuleuven.math.vector.VectorCursor;

public class AllValueCursor<R, C> {
  protected MatrixCursor<R, C> rowCursor;
  protected VectorCursor<C> columnCursor;
  
  public AllValueCursor(Matrix<R, C> matrix) {
    rowCursor = matrix.getRowCursor();
    
    if (rowCursor.isValid())
      columnCursor = rowCursor.get().getCursor();
  }
  
  public boolean isValid() {
    return rowCursor.isValid() && columnCursor.isValid();
  }
  
  public void next() {
    columnCursor.next();
    
    if (!columnCursor.isValid()) {
      rowCursor.next();
      
      if (rowCursor.isValid())
        columnCursor = rowCursor.get().getCursor();
    }
  }
  public R getRowDimension(){
    return rowCursor.dimension();
  }
  public C getColumnDimension(){
    return columnCursor.dimension();
  }
  public double get() {
    return columnCursor.get();
  }
  
  public void set(double value) {
    columnCursor.set(value);
  }
}
