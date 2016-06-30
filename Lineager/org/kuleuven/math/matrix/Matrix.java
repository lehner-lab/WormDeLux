package org.kuleuven.math.matrix;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.kuleuven.math.space.Space;
import org.kuleuven.math.vector.ColumnVector;
import org.kuleuven.math.vector.RowVector;
import org.kuleuven.math.vector.Vector;
import org.kuleuven.math.vector.VectorCursor;

public abstract class Matrix<R, C> {
  public Matrix() {
  }

  public Matrix(Space<R> rowSpace, Space<C> columnSpace) {
    setSpaces(rowSpace, columnSpace);
  }

  public Matrix(Matrix<R, C> matrix) {
    setSpaces(matrix.getRowSpace(), matrix.getColumnSpace());
    set(matrix);
  }

  public abstract void set(R row, C column, double value);

  public abstract double get(R row, C column);

  public abstract Space<R> getRowSpace();

  public abstract Space<C> getColumnSpace();

  public abstract void setSpaces(Space<R> rowSpace, Space<C> columnSpace);

  public abstract MatrixCursor<R, C> getRowCursor();

  public abstract MatrixCursor<C, R> getColumnCursor();

  public Vector<R> getColumn(C column) {
    return new ColumnVector<R, C>(this, column);
  }

  public Vector<C> getRow(R row) {
    return new RowVector<R, C>(this, row);
  }

  public void zeroes() {
    constants(0);
  }

  public void ones() {
    constants(1);
  }

  public void constants(double value) {
    for (R row: getRowSpace())
      for (C column: getColumnSpace())
        set(row, column, value);
  }

  public void identity() {
    zeroes();
    setMainDiagonal(1);
  }

  public void set(Matrix<R, C> matrix) {
    MatrixCursor<R, C> rowCursor = getRowCursor();
    MatrixCursor<R, C> sourceRowCursor = matrix.getRowCursor();

    while (rowCursor.isValid()) {
      VectorCursor<C> columnCursor = rowCursor.get().getCursor();
      VectorCursor<C> sourceColumnCursor = sourceRowCursor.get().getCursor();

      while (columnCursor.isValid()) {
        columnCursor.set(sourceColumnCursor.get());

        columnCursor.next();
        sourceColumnCursor.next();
      }

      rowCursor.next();
      sourceRowCursor.next();
    }
  }

  public void copyFrom(Matrix<R, C> matrix) {
    setSpaces(matrix.getRowSpace(), matrix.getColumnSpace());
    set(matrix);
  }

  public AllValueCursor<R, C> getAllValueCursor() {
    return new AllValueCursor<R, C>(this);
  }

  public void dump() {

    for (R row: getRowSpace()) {

      for (C column: getColumnSpace())
        System.out.print(get(row, column) + " | ");

      System.out.println();
    }

    System.out.println();
  }

  public void dumpToFile(FileOutputStream os) {
    try {
      StringBuffer line = new StringBuffer();
      for (C column: getColumnSpace()) {
        line.append("\t");
        line.append(column.toString());
      }
      line.append("\n");

      os.write(line.toString().getBytes());

      for (R row: getRowSpace()) {
        line = new StringBuffer();
        line.append(row.toString());
        for (C column: getColumnSpace()) {
          line.append("\t");
          line.append(get(row, column));
        }
        line.append("\n");
        os.write(line.toString().getBytes());
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public String dumpToString() {
    StringBuffer result = new StringBuffer();

    for (C column: getColumnSpace()) {
      result.append("\t");
      result.append(column.toString());
    }
    result.append("\n");
    for (R row: getRowSpace()) {
      result.append(row.toString());
      for (C column: getColumnSpace()) {
        result.append("\t");
        result.append(get(row, column));
      }
      result.append("\n");
    }
    return result.toString();
  }

  public <D> void multiply(Matrix<R, D> result, Matrix<C, D> rhs) {
    MatrixCursor<R, D> resultRowCursor = result.getRowCursor();
    MatrixCursor<R, C> lhsRowCursor = getRowCursor();

    while (lhsRowCursor.isValid()) {
      Vector<C> rowVector = lhsRowCursor.get();
      Vector<D> resultRowVector = resultRowCursor.get();

      VectorCursor<D> resultColumnCursor = resultRowVector.getCursor();
      MatrixCursor<D, C> rhsColumnCursor = rhs.getColumnCursor();

      while (rhsColumnCursor.isValid()) {
        resultColumnCursor.set(rowVector.innerProduct(rhsColumnCursor.get()));

        rhsColumnCursor.next();
        resultColumnCursor.next();
      }

      lhsRowCursor.next();
      resultRowCursor.next();
    }
  }

  public <D> RowArrayMatrix<R, D> multiply(Matrix<C, D> rhs) {
    RowArrayMatrix<R, D> result = new RowArrayMatrix<R, D>(getRowSpace(), rhs.getColumnSpace());
    multiply(result, rhs);
    return result;
  }

  public void multiply(double value) {
    AllValueCursor<R, C> cursor = getAllValueCursor();

    while (cursor.isValid()) {
      cursor.set(cursor.get() * value);
      cursor.next();
    }
  }

  public void divide(double value) {
    AllValueCursor<R, C> cursor = getAllValueCursor();

    while (cursor.isValid()) {
      cursor.set(cursor.get() / value);
      cursor.next();
    }
  }

  public void add(double value) {
    AllValueCursor<R, C> cursor = getAllValueCursor();

    while (cursor.isValid()) {
      cursor.set(cursor.get() + value);
      cursor.next();
    }
  }

  public Matrix<C, R> invert() throws Exception {
    PermutedLUDecomposition decomposition = new PermutedLUDecomposition(this);
    Matrix<C, R> inverse = new RowArrayMatrix<C, R>(getColumnSpace(), getRowSpace());
    inverse.identity();
    decomposition.substituteBack(inverse);

    return inverse;
  }

  public void setMainDiagonal(double value) {
    Iterator<R> rowIterator = getRowSpace().iterator();
    Iterator<C> columnIterator = getColumnSpace().iterator();

    while (rowIterator.hasNext() && columnIterator.hasNext())
      set(rowIterator.next(), columnIterator.next(), value);
  }

  public double maximum() {
    double result = Double.MIN_VALUE;

    for (R row: getRowSpace())
      for (C column: getColumnSpace())
        result = Math.max(get(row, column), result);

    return result;
  }

  public double minimum() {
    double result = Double.MAX_VALUE;
    AllValueCursor<R, C> cursor = getAllValueCursor();

    while (cursor.isValid()) {
      result = Math.min(cursor.get(), result);
      cursor.next();
    }

    return result;
  }
}