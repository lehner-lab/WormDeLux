package org.kuleuven.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FloatList implements List<Float>,Iterable<Float>{
  private float[] array;
  private int defaultCapacity = 8;
  private int size = 0;

  public FloatList() {
    array = new float[defaultCapacity];
  }

  public FloatList(int initialCapacity) {
    array = new float[initialCapacity];
  }

  public boolean add(float i) {
    if (size >= array.length)
      grow();
    array[size] = i;
    size++;
    return true;
  }

  public void add(int index, float element) {
    if (index < size) {
      if (size + 1 >= array.length)
        grow();

      System.arraycopy(array, index, array, index + 1, size - index);
      array[index] = element;
      size++;
    }
    else if (index == size) {
      add(element);
    }
    else {
      throw new IndexOutOfBoundsException();
    }

  }

  public float set(int index, float element) {
    if (index < size) {
      float current = array[index];
      array[index] = element;
      return current;
    }
    else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
public boolean addAll(Collection<? extends Float> collection) {
    if (size + collection.size() > array.length)
      setCapacity(size + collection.size());
    Iterator<? extends Float> iterator = collection.iterator();
    while (iterator.hasNext()) {
      array[size] = iterator.next();
      size++;
    }
    return true;
  }

  @Override
public Float remove(int index) throws ArrayIndexOutOfBoundsException {
    if (index < 0 || index >= size)
      throw new ArrayIndexOutOfBoundsException("list[" + index + "] is out of bounds (max " + (size - 1) + ")");
    Float result =get(index);
    System.arraycopy(array, index + 1, array, index, size - index - 1);
    size--;
    return result;
  }

  public float[] toFloatArray() {
    return array;
  }

  @Override
public void clear() {
    size = 0;
  }

  @Override
public Float get(int index) throws ArrayIndexOutOfBoundsException {
    if (index < 0 || index >= size)
      throw new ArrayIndexOutOfBoundsException("list[" + index + "] is out of bounds (max " + (size - 1) + ")");
    return array[index];
  }

  @Override
public int size() {
    return size;
  }

  public void trimToSize() {
    setCapacity(size);
  }

  private void grow() {
    int delta;
    if (array.length > 64)
      delta = array.length / 4;
    else
      delta = 16;
    setCapacity(array.length + delta);
  }

  private void setCapacity(int newCapacity) {
    float[] newArray = new float[newCapacity];
    System.arraycopy(array, 0, newArray, 0, size);
    array = newArray;
  }

@Override
public boolean add(Float e) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public void add(int index, Float element) {
	// TODO Auto-generated method stub
	
}



@Override
public boolean addAll(int index, Collection<? extends Float> c) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean contains(Object o) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean containsAll(Collection<?> c) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public int indexOf(Object o) {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public boolean isEmpty() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public Iterator<Float> iterator() {

	return new FloatIterator();
}
private class FloatIterator implements Iterator<Float> {
    private int index = 0;

    @Override
	public boolean hasNext() {

      return index < size;
    }

    @Override
	public Float next() {
      float val = array[index];
      index++;
      return val;
    }

    @Override
	public void remove() {
      if (index > 0) {
        FloatList.this.remove(index - 1);
        index--;
      }
    }
}


@Override
public int lastIndexOf(Object o) {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public ListIterator<Float> listIterator() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public ListIterator<Float> listIterator(int index) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public boolean remove(Object o) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean removeAll(Collection<?> c) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean retainAll(Collection<?> c) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public Float set(int index, Float element) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public List<Float> subList(int fromIndex, int toIndex) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public <T> T[] toArray(T[] a) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Object[] toArray() {
	// TODO Auto-generated method stub
	return null;
}

}
