package org.kuleuven.math.matrix;

import java.util.List;

import org.kuleuven.math.space.Space;
import org.kuleuven.math.vector.Vector;

public class RowVectorMatrix<V> extends VectorMatrix<V, Vector<V>, V> {
  public RowVectorMatrix(List<Vector<V>> rowVectors, Space<V> columnSpace) {
    super(rowVectors, columnSpace);
  }
  
  @Override
public void set(Vector<V> row, V column, double value) {
    row.set(column, value);
  }

  @Override
public double get(Vector<V> row, V column) {
    return row.get(column);
  }

  @Override
public Space<Vector<V>> getRowSpace() {
    return listSpace;
  }

  @Override
public Space<V> getColumnSpace() {
    return vectorSpace;
  }

  @Override
public Vector<V> getRow(Vector<V> row) {
    return row;
  }

  @Override
public MatrixCursor<Vector<V>, V> getRowCursor() {
    return new VectorMatrixVectorCursor();
  }

  @Override
public MatrixCursor<V, Vector<V>> getColumnCursor() {
    return new VectorMatrixOrthogonalCursor();
  }
}
