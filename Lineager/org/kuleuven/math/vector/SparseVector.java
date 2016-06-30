package org.kuleuven.math.vector;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;

import org.kuleuven.collections.SortedListMap;
import org.kuleuven.math.space.Space;
import org.kuleuven.visitor.Visitor;

public class SparseVector<D> extends Vector<D> implements Serializable {
  private static final long serialVersionUID = 7973211925353447858L;
  public Space<D> space;
  public SortedListMap<D, Double> values;
  
  public SparseVector(Space<D> space) {
    this.space = space;    
    values = new SortedListMap<D, Double>(new SparseVectorKeyComparator());
  }
  
  public SparseVector(){
    values = new SortedListMap<D, Double>(new SparseVectorKeyComparator());
  }

  public SparseVector(Vector<D> vector) {
    space = vector.getSpace();
    values = new SortedListMap<D, Double>(new SparseVectorKeyComparator());
    set(vector);
  }
  
  
  @Override
public void set(D index, double value) {
    if (value != 0d)
      values.put(index, value);
    else
      values.remove(index);
  }

  @Override
public double get(D index) {
    Double value = values.get(index);
    
    if (value != null)
      return value;
    else
      return 0;
  }
  
  @Override
public void set(Vector<D> vector) {
    values.clear();
    VectorCursor<D> cursor = vector.getNonzeroCursor();
    
    while (cursor.isValid()) {
      set(cursor.dimension(), cursor.get());
      cursor.next();
    }
  }
    
  @Override
public Space<D> getSpace() {
    return space;
  }

  @Override
public void setSpace(Space<D> space) {
    this.space = space;
  }
  
  @Override
public Object accept(Visitor visitor) {
    return null;//visitor.visitSparseVector(this);
  }
  
  @Override
public String toString() {
    return "Sparse vector";
  }
  
  @Override
public VectorCursor<D> getCursor() {
    return new SparseVectorCursor();
  }
  
  @Override
public VectorCursor<D> getNonzeroCursor() {
    return new SparseVectorNonzeroCursor();
  }

  @Override
public VectorSlaveCursor<D> getSlaveCursor() {
    return new SparseVectorSlaveCursor();
  }
  
  @Override
public int getStoredValueCount() {
    return values.size();
  }

  protected class SparseVectorHandle implements VectorHandle<D> {
    D dimension;
    
    @Override
	public D dimension() {
      return dimension;
    }

    @Override
	public int index() {
      return space.indexOfObject(dimension);
    }

    @Override
	public double get() {
      Double value = values.get(dimension);
      
      if (value != null)
        return value;
      else
        return 0;
    }

    @Override
	public void set(double value) {
      if (value != 0d)
        values.put(dimension, value);
      else
        values.remove(dimension);
    }
  }
  
  protected class SparseVectorSlaveCursor extends SparseVectorHandle implements VectorSlaveCursor<D> {
    @Override
	public void synchronize(VectorHandle<D> vectorHandle) {
      dimension = vectorHandle.dimension();
    }
  }
  
  protected class SparseVectorCursor extends SparseVectorHandle implements VectorCursor<D> {
    protected Iterator<D> iterator;
    
    public SparseVectorCursor() {
      iterator = space.iterator();
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

  protected class SparseVectorNonzeroCursor extends SparseVectorHandle implements VectorCursor<D>, Serializable {
    private static final long serialVersionUID = 2287253547250643918L;
    protected Iterator<D> iterator;
    
    public SparseVectorNonzeroCursor() {
      iterator = values.keyIterator();
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
  protected class SparseVectorKeyComparator implements Serializable, Comparator<D> {
    private static final long serialVersionUID = 1481833931150717597L;

    @Override
	public int compare(D o1, D o2) {
      return space.indexOfObject(o1) - space.indexOfObject(o2);
    }
    
  }
}
