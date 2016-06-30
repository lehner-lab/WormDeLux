package org.kuleuven.math.matrix;

import java.util.List;

import org.kuleuven.math.space.Space;
import org.kuleuven.math.vector.Vector;

public class ColumnVectorMatrix<V> extends VectorMatrix<V, V, Vector<V>> {
  public ColumnVectorMatrix(List<Vector<V>> columnVectors, Space<V> rowSpace) {
    super(columnVectors, rowSpace);
  }
  
  @Override
public void set(V row, Vector<V> column, double value) {
    column.set(row, value);
  }

  @Override
public double get(V row, Vector<V> column) {
    return column.get(row);
  }

  @Override
public Space<V> getRowSpace() {
    return vectorSpace;
  }

  @Override
public Space<Vector<V>> getColumnSpace() {
    return listSpace;
  }

  @Override
public Vector<V> getColumn(Vector<V> column) {
    return column;
  }

  @Override
public MatrixCursor<V, Vector<V>> getRowCursor() {
    return new VectorMatrixOrthogonalCursor();
  }

  @Override
public MatrixCursor<Vector<V>, V> getColumnCursor() {
    return new VectorMatrixVectorCursor();
  }
}
