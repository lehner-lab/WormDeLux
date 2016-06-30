package org.kuleuven.math.vector;

import java.util.Iterator;

import org.kuleuven.math.matrix.Matrix;
import org.kuleuven.math.space.Space;

public class ColumnVector<R, C> extends Vector<R> {
  protected Matrix<R, C> matrix;
  protected C column;
  
  public ColumnVector(Matrix<R, C> matrix, C column) {
    this.matrix = matrix;
    this.column = column;
  }
  
  @Override
public void set(R index, double value) {
    matrix.set(index, column, value);
  }

  @Override
public double get(R index) {
    return matrix.get(index, column);
  }

  @Override
public Space<R> getSpace() {
    return matrix.getRowSpace();
  }

  @Override
public void setSpace(Space<R> space) {
  }
  
  @Override
public VectorCursor<R> getCursor() {
    return new ColumnVectorCursor();
  }

  @Override
public VectorCursor<R> getNonzeroCursor() {
    return new NonzeroVectorCursor<R>(new ColumnVectorCursor());
  }
  
  @Override
public VectorSlaveCursor<R> getSlaveCursor() {
    return new ColumnVectorSlaveCursor();
  }

  @Override
public int getStoredValueCount() {
    return getSpace().getDimensions();
  }

  protected class ColumnVectorHandle implements VectorHandle<R> {
    protected R dimension;
    
    @Override
	public R dimension() {
      return dimension;
    }

    @Override
	public int index() {
      return getSpace().indexOfObject(dimension);
    }

    @Override
	public double get() {
      return matrix.get(dimension, column);
    }

    @Override
	public void set(double value) {
      matrix.set(dimension, column, value);
    }
  }
  
  protected class ColumnVectorSlaveCursor extends ColumnVectorHandle implements VectorSlaveCursor<R> {
    @Override
	public void synchronize(VectorHandle<R> vectorHandle) {
      dimension = vectorHandle.dimension();
    }
  }
  
  protected class ColumnVectorCursor extends ColumnVectorHandle implements VectorCursor<R> {
    protected Iterator<R> iterator;
    
    public ColumnVectorCursor() {
      iterator = getSpace().iterator();
      next();
    }
    
    @Override
	public boolean isValid() {
      return dimension != null;
    }

    @Override
	public void next() {
      if (iterator.hasNext())
        dimension = iterator.next();
      else
        dimension = null;
    }
  }

  @Override
public void set(Vector<R> vector) {
  }
}
