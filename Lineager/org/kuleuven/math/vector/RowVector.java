package org.kuleuven.math.vector;

import java.util.Iterator;

import org.kuleuven.math.matrix.Matrix;
import org.kuleuven.math.space.Space;

public class RowVector<R, C> extends Vector<C> {
  protected Matrix<R, C> matrix;
  protected R row;
  
  public RowVector(Matrix<R, C> matrix, R row) {
    this.matrix = matrix;
    this.row = row;
  }
  
  @Override
public void set(C index, double value) {
    matrix.set(row, index, value);
  }

  @Override
public double get(C index) {
    return matrix.get(row, index);
  }

  @Override
public Space<C> getSpace() {
    return matrix.getColumnSpace();
  }

  @Override
public void setSpace(Space<C> space) {
  }
  
  @Override
public VectorCursor<C> getCursor() {
    return new RowVectorCursor();
  }

  @Override
public VectorCursor<C> getNonzeroCursor() {
    return new NonzeroVectorCursor<C>(new RowVectorCursor());
  }
  
  @Override
public VectorSlaveCursor<C> getSlaveCursor() {
    return new RowVectorSlaveCursor();
  }

  @Override
public int getStoredValueCount() {
    return getSpace().getDimensions();
  }

  protected class RowVectorHandle implements VectorHandle<C> {
    protected C dimension;
    
    @Override
	public C dimension() {
      return dimension;
    }

    @Override
	public int index() {
      return getSpace().indexOfObject(dimension);
    }

    @Override
	public double get() {
      return matrix.get(row, dimension);
    }

    @Override
	public void set(double value) {
      matrix.set(row, dimension, value);
    }
  }
  
  protected class RowVectorSlaveCursor extends RowVectorHandle implements VectorSlaveCursor<C> {
    @Override
	public void synchronize(VectorHandle<C> vectorHandle) {
      dimension = vectorHandle.dimension();
    }
  }
  
  protected class RowVectorCursor extends RowVectorHandle implements VectorCursor<C> {
    protected Iterator<C> iterator;
    
    public RowVectorCursor() {
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
public void set(Vector vector) {
  }
}
