package org.kuleuven.math.vector;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;

import org.kuleuven.collections.SortedListMap;
import org.kuleuven.math.space.Space;

public class SparseVectorSingleFloat<D> extends Vector<D> implements Serializable {
  private static final long serialVersionUID = -7684412519029854775L;
  transient public SortedListMap<D, Float> values;
  transient public Space<D> space;

  public SparseVectorSingleFloat() {

    values = new SortedListMap<D, Float>(new SparseVectorKeyComparator());
  }

  public SparseVectorSingleFloat(Space<D> space) {
    this.space = space;
    values = new SortedListMap<D, Float>(new SparseVectorKeyComparator());
  }

  public SparseVectorSingleFloat(Vector<D> vector) {
    space = vector.getSpace();
    values = new SortedListMap<D, Float>(new SparseVectorKeyComparator());
    set(vector);
  }

  public double sparseInnerProduct(SparseVectorSingleFloat<D> other) {
    SortedListMap<D, Float> shorter = other.values;
    SortedListMap<D, Float> longer = values;
    if (shorter.size() > longer.size()) {
      shorter = longer;
      longer = other.values;
    }

    double innerproduct = 0f;
    int longerlowestIndex = 0;
    int longerhighestIndex = longer.size() - 1;
    int longerlowest = space.indexOfObject(longer.getKey(longerlowestIndex));
    int longerhighest = space.indexOfObject(longer.getKey(longerhighestIndex));
    int shorterlowestIndex = 0;
    int shorterhighestIndex = shorter.size() - 1;
    while (shorterlowestIndex <= shorterhighestIndex) {
      D key = shorter.getKey(shorterlowestIndex);
      int shorterlowest = space.indexOfObject(key);
      if (shorterlowest >= longerlowest) {
        int index = longer.guidedGetIndexForKey(key, longerlowestIndex, longerhighestIndex + 1);
        if (index < longer.size()) {
          longerlowestIndex = index;
          if (longer.getKey(index).equals(key)) {
            innerproduct += longer.getValue(index) * shorter.getValue(shorterlowestIndex);
            if (longerlowestIndex < longerhighestIndex - 1)
              longerlowestIndex++;
          }
          longerlowest = space.indexOfObject(longer.getKey(longerlowestIndex));
        }
      }
      shorterlowestIndex++;
      if (shorterlowestIndex < shorterhighestIndex) {
        key = shorter.getKey(shorterhighestIndex);
        int shorterhighest = space.indexOfObject(key);
        if (shorterhighest <= longerhighest) {
          int index = longer.guidedGetIndexForKey(key, longerlowestIndex, longerhighestIndex + 1);
          if (index < longer.size()) {
            longerhighestIndex = index;
            if (longer.getKey(index).equals(key)) {
              innerproduct += longer.getValue(index) * shorter.getValue(shorterhighestIndex);
              if (longerlowestIndex < longerhighestIndex - 1)
                longerhighestIndex--;
            }
            longerhighest = space.indexOfObject(longer.getKey(longerhighestIndex));
          }
        }
        shorterhighestIndex--;
      }
    }
    return innerproduct;
  }

  /**
   * superfancy high performance cosine function. Whips Any ass up to now.
   * 
   * @param other
   * @return
   */
  public double sparseCosine(SparseVectorSingleFloat<D> other) {
    double result = 0;
    double denominator = norm() * other.norm();
    if (denominator > 0) {
      double innerproduct = sparseInnerProduct(other);
      result = new Double(innerproduct / denominator).floatValue();
    }
    return result;

  }

  public double jaccard(SparseVectorSingleFloat<D> other) {
    double result = 0;
    double innerproduct = sparseInnerProduct(other);
    double denominator = getSquaredNorm() + other.getSquaredNorm() - innerproduct;
    if (denominator > 0) {
      result = new Double(innerproduct / denominator).floatValue();
    }
    return result;
  }

  @Override
public void set(D index, double value) {
    if (value != 0d)
      values.put(index, new Double(value).floatValue());
    else
      values.remove(index);
  }

  public void setFloat(D index, float value) {
    if (value != 0d)
      values.put(index, value);
    else
      values.remove(index);
  }

  public float getFloat(D index) {
    Float value = values.get(index);

    if (value != null)
      return value;
    else
      return 0;

  }

  @Override
public double get(D index) {
    Float value = values.get(index);

    if (value != null)
      return value.doubleValue();
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
public int getStoredValueCount() {
    return values.size();
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
      Float value = values.get(dimension);

      if (value != null)
        return value.doubleValue();
      else
        return 0;
    }

    @Override
	public void set(double value) {
      if (value != 0d)
        values.put(dimension, new Double(value).floatValue());
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

  @Override
public Space<D> getSpace() {
    return space;
  }

  @Override
public void setSpace(Space<D> space) {
    this.space = space;
  }

  public class SparseVectorKeyComparator implements Serializable, Comparator<D> {
    private static final long serialVersionUID = 1481833931150717597L;

    @Override
	public int compare(D o1, D o2) {
      return space.indexOfObject(o1) - space.indexOfObject(o2);
    }

  }
}