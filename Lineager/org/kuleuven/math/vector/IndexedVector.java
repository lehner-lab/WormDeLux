package org.kuleuven.math.vector;

public abstract class IndexedVector<D> extends Vector<D> {
  public IndexedVector() {
    super();
  }
  
  public IndexedVector(Vector<D> vector) {
    super(vector);
  }
  
  public abstract double getByIndex(int index);
  public abstract void setByIndex(int index, double value);
  
  @Override
public void set(D object, double value) {
    setByIndex(getSpace().indexOfObject(object), value);
  }
  
  @Override
public void set(Vector<D> vector) {
    VectorCursor<D> cursor = vector.getNonzeroCursor();
    int index = 0;
    
    while (cursor.isValid()) {
      int cursorIndex = cursor.index();
      
      while (index < cursorIndex) {
        setByIndex(index, 0);
        index++;
      }
      
      setByIndex(cursorIndex, cursor.get());
      cursor.next();
      index++;
    }
  }
  
  @Override
public double get(D object) {
    return getByIndex(getSpace().indexOfObject(object));
  }
  
  @Override
public VectorCursor<D> getCursor() {
    return new IndexedVectorCursor();
  }
  
  @Override
public VectorCursor<D> getNonzeroCursor() {
    return new IndexedVectorNonzeroCursor();
  }

  @Override
public VectorSlaveCursor<D> getSlaveCursor() {
    return new IndexedVectorSlaveCursor();
  }

  @Override
public int getStoredValueCount() {
    return getSpace().getDimensions();
  }

  protected class IndexedVectorHandle implements VectorHandle<D> {
    protected int index = 0;
    
    @Override
	public D dimension() {
      return getSpace().objectForIndex(index);
    }

    @Override
	public int index() {
      return index;
    }

    @Override
	public double get() {
      return getByIndex(index);
    }

    @Override
	public void set(double value) {
      setByIndex(index, value);
    }
  }
  
  protected class IndexedVectorCursor extends IndexedVectorHandle implements VectorCursor<D> {
    @Override
	public boolean isValid() {
      return index < getSpace().getDimensions();
    }

    @Override
	public void next() {
      index++;
    }
  }
  
  protected class IndexedVectorNonzeroCursor extends IndexedVectorHandle implements VectorCursor<D> {
    @Override
	public boolean isValid() {
      return index < getSpace().getDimensions();
    }

    @Override
	public void next() {
      do 
        index++;
      while (index < getSpace().getDimensions() && getByIndex(index) == 0);
    }
  }
  
  protected class IndexedVectorSlaveCursor extends IndexedVectorHandle implements VectorSlaveCursor<D> {
    @Override
	public void synchronize(VectorHandle<D> vectorHandle) {
      index = vectorHandle.index();
    }
  }
}

