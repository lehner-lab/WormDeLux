package org.kuleuven.math.vector;

import java.io.Serializable;
import java.util.Iterator;

import org.kuleuven.collections.MapCursor;
import org.kuleuven.collections.SortedIntList2FloatMap;
import org.kuleuven.collections.SortedIntList2FloatMap.MapEntry;
import org.kuleuven.math.space.IntegerSpace;
import org.kuleuven.math.space.Space;

public class SparseVectorInt2Float extends Vector<Integer> implements Serializable {
  private static final long serialVersionUID = 2830857831592496295L;
  transient public SortedIntList2FloatMap values;

  public SparseVectorInt2Float() {
    values = new SortedIntList2FloatMap();
  }

  public SparseVectorInt2Float(SortedIntList2FloatMap map) {
    values = map;
  }

  public SparseVectorInt2Float(Vector<Integer> vector) {
    values = new SortedIntList2FloatMap();
    set(vector);
  }
 

  public SparseVectorInt2Float sparseElementWiseSparseInnerProduct(SparseVectorInt2Float other) {

    SortedIntList2FloatMap shorter = other.values;
    SortedIntList2FloatMap longer = values;
    SortedIntList2FloatMap resultmap = new SortedIntList2FloatMap(shorter.size() + longer.size());
    if (shorter.size() != 0 && longer.size() != 0){
      if (shorter.size() > longer.size()) {
        shorter = longer;
        longer = other.values;
      }
      int longerlowestIndex = 0;
      int longerhighestIndex = longer.size() - 1;
      int longerlowest = longer.getKey(longerlowestIndex);
      int shorterlowestIndex = 0;
      int shorterhighestIndex = shorter.size() - 1;
      while (shorterlowestIndex <= shorterhighestIndex) {
        int key = shorter.getKey(shorterlowestIndex);
        if (key >= longerlowest) {
          int index = longer.guidedGetIndexForKey(key, longerlowestIndex, longerhighestIndex + 1);
          if (index < longer.size()) {
            longerlowestIndex = index;
            if (longer.getKey(index) == key) {
              float product = longer.getValue(index) * shorter.getValue(shorterlowestIndex);
              resultmap.addEntry(key, product);
              if (longerlowestIndex < longerhighestIndex - 1)
                longerlowestIndex++;
            }
            longerlowest = longer.getKey(longerlowestIndex);
          }
        }
        shorterlowestIndex++;
      }
    }
    return new SparseVectorInt2Float(resultmap);
  }

  public double sparseInnerProduct(SparseVectorInt2Float other) {
    SortedIntList2FloatMap shorter = other.values;
    SortedIntList2FloatMap longer = values;
    if (shorter.size() > longer.size()) {
      shorter = longer;
      longer = other.values;
    }
    double innerproduct = 0f;
    if (shorter.size() > 0) {
      int longerlowestIndex = 0;
      int longerhighestIndex = longer.size() - 1;
      int longerlowest = longer.getKey(longerlowestIndex);
      int longerhighest = longer.getKey(longerhighestIndex);
      int shorterlowestIndex = 0;
      int shorterhighestIndex = shorter.size() - 1;
      while (shorterlowestIndex <= shorterhighestIndex) {
        int key = shorter.getKey(shorterlowestIndex);
        if (key >= longerlowest) {
          int index = longer.guidedGetIndexForKey(key, longerlowestIndex, longerhighestIndex + 1);
          if (index < longer.size()) {
            longerlowestIndex = index;
            if (longer.getKey(index) == key) {
              innerproduct += longer.getValue(index) * shorter.getValue(shorterlowestIndex);
              if (longerlowestIndex < longerhighestIndex - 1)
                longerlowestIndex++;
            }
            longerlowest = longer.getKey(longerlowestIndex);
          }
        }
        shorterlowestIndex++;
        if (shorterlowestIndex < shorterhighestIndex) {
          key = shorter.getKey(shorterhighestIndex);
          if (key <= longerhighest) {
            int index = longer.guidedGetIndexForKey(key, longerlowestIndex, longerhighestIndex + 1);
            if (index < longer.size()) {
              longerhighestIndex = index;
              if (longer.getKey(index) == key) {
                innerproduct += longer.getValue(index) * shorter.getValue(shorterhighestIndex);
                if (longerlowestIndex < longerhighestIndex - 1)
                  longerhighestIndex--;
              }
              longerhighest = longer.getKey(longerhighestIndex);
            }
          }
          shorterhighestIndex--;
        }
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
  public double sparseCosine(SparseVectorInt2Float other) {
    double result = 0;
    double denominator = norm() * other.norm();
    if (denominator > 0) {
      double innerproduct = sparseInnerProduct(other);
      result = innerproduct / denominator;
    }
    return result;

  }

  public double jaccard(SparseVectorInt2Float other) {
    double result = 0;
    double innerproduct = sparseInnerProduct(other);
    double denominator = getSquaredNorm() + other.getSquaredNorm() - innerproduct;
    if (denominator > 0) {
      result = innerproduct / denominator;
    }
    return result;
  }

  public double dice(SparseVectorInt2Float other) {
    double result = 0;
    double innerproduct = sparseInnerProduct(other);
    double denominator = getSquaredNorm() + other.getSquaredNorm();
    if (denominator > 0) {
      result = 2d * innerproduct / denominator;
    }
    return result;
  }

  @Override
  public double get(Integer object) {
    float value = values.get(object.intValue());

    if (!Float.isNaN(value))
      return new Float(value).doubleValue();
    else
      return 0f;
  }

  @Override
  public Space<Integer> getSpace() {
    return new IntegerSpace();
  }

  @Override
  public int getStoredValueCount() {
    return values.size();
  }

  @Override
  public void set(Integer index, double value) {
    if (value != 0d)
      values.put(index, new Double(value).floatValue());
    else
      values.remove(index);
  }

  public void setFloat(Integer index, float value) {
    if (value != 0d)
      values.put(index, value);
    else
      values.remove(index);
  }

  public float getFloat(Integer index) {
    float value = values.get(index);

    if (!Float.isNaN(value))
      return value;
    else
      return 0;

  }

  @Override
  public void set(Vector<Integer> vector) {
    values.clear();
    VectorCursor<Integer> cursor = vector.getNonzeroCursor();

    while (cursor.isValid()) {
      set(cursor.dimension(), cursor.get());
      cursor.next();
    }
  }

  @Override
  public void setSpace(Space<Integer> space) {
  }

  @Override
public VectorCursor<Integer> getCursor() {
    return new SparseVectorNonzeroCursor();
  }

  @Override
public VectorCursor<Integer> getNonzeroCursor() {
    return new SparseVectorNonzeroCursor();
  }

  @Override
public VectorSlaveCursor<Integer> getSlaveCursor() {
    return new SparseVectorSlaveCursor();
  }

  public Iterator<MapEntry> entryIterator() {
    return values.entryIterator();
  }

  protected class SparseVectorHandle implements VectorHandle<Integer> {
    Integer dimension;

    @Override
	public Integer dimension() {
      return dimension;
    }

    @Override
	public int index() {
      return dimension;
    }

    @Override
	public double get() {
      float value = values.get(dimension);

      if (!Float.isNaN(value))
        return value;
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

  protected class SparseVectorSlaveCursor extends SparseVectorHandle implements VectorSlaveCursor<Integer> {
    @Override
	public void synchronize(VectorHandle<Integer> vectorHandle) {
      dimension = vectorHandle.dimension();
    }
  }

  protected class SparseVectorNonzeroCursor extends SparseVectorHandle implements VectorCursor<Integer>, Serializable {
    private static final long serialVersionUID = 2287253547250643918L;
    MapCursor<Integer, Float> cursor;

    public SparseVectorNonzeroCursor() {
      cursor = values.getEntryCursor();
    }

    @Override
	public boolean isValid() {
      return cursor.isValid();
    }

    @Override
	public void next() {
      cursor.next();
    }

    @Override
    public Integer dimension() {
      return cursor.key();
    }

    @Override
    public double get() {
      return cursor.value();
    }

    @Override
    public int index() {
      return values.getIndexForKey(dimension());
    }

    @Override
    public void set(double value) {
      if (value != 0d)
        cursor.setValue(new Double(value).floatValue());
      else
        cursor.remove();
      
    }
  }
}
