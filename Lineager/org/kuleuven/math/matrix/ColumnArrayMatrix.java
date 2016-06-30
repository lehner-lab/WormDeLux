package org.kuleuven.math.matrix;

import org.kuleuven.math.space.Space;
import org.kuleuven.math.vector.ArrayVector;
import org.kuleuven.math.vector.Vector;

public class ColumnArrayMatrix<R, C> extends ArrayMatrix<R, C> {
  public ColumnArrayMatrix(Space<R> rowSpace, Space<C> columnSpace) {
    super(rowSpace, columnSpace);
  }
  
  public ColumnArrayMatrix(Matrix<R, C> matrix) {
    super(matrix);
  }

  @Override
public void set(R row, C column, double value) {
    values[columnSpace.indexOfObject(column)][rowSpace.indexOfObject(row)] = value;
  }

  @Override
public double get(R row, C column) {
    int i=columnSpace.indexOfObject(column);
    int j = rowSpace.indexOfObject(row);
    return values[columnSpace.indexOfObject(column)][rowSpace.indexOfObject(row)];
  }
  
  @Override
public void setSpaces(Space<R> rowSpace, Space<C> columnSpace) {
    this.rowSpace = rowSpace;
    this.columnSpace = columnSpace;

    values = new double[columnSpace.getDimensions()][rowSpace.getDimensions()];
  }
  
  @Override
public Vector<R> getColumn(C column) {
    return new ArrayVector<R>(getRowSpace(), values[columnSpace.indexOfObject(column)]);
  }

  @Override
public MatrixCursor<R, C> getRowCursor() {
    return new ArrayMatrixFirstSpaceCursor<R, C>(rowSpace, columnSpace);
  }

  @Override
public MatrixCursor<C, R> getColumnCursor() {
    return new ArrayMatrixSecondSpaceCursor<R, C>(rowSpace, columnSpace);
  }

  
  public double getByIndex(Integer row, Integer column) {
    
    return values[row][column];
  }
}
