package org.kuleuven.math.matrix;

import org.kuleuven.math.space.Space;

public class HilbertMatrix extends Matrix {
  protected Space rowSpace, columnSpace;
  
  public HilbertMatrix(Space rowSpace, Space columnSpace) {
    setSpaces(rowSpace, columnSpace);
  }
  
  @Override
public void set(Object row, Object column, double value) {
  }

  @Override
public double get(Object row, Object column) {
    return 1.0d / (rowSpace.indexOfObject(row) + columnSpace.indexOfObject(column) + 1.0d);
  }

  @Override
public Space getRowSpace() {
    return rowSpace;
  }

  @Override
public Space getColumnSpace() {
    return columnSpace;
  }

  @Override
public void setSpaces(Space rowSpace, Space columnSpace) {
    this.rowSpace = rowSpace;
    this.columnSpace = columnSpace;
  }

  @Override
public MatrixCursor getRowCursor() {
    return null;
  }

  @Override
public MatrixCursor getColumnCursor() {
    return null;
  }
}
