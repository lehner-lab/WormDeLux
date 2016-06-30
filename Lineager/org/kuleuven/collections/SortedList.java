package org.kuleuven.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SortedList<E> implements List<E> {
	protected List<E> elements = new ArrayList<E>();
	protected Comparator<E> comparator;

	public SortedList(Comparator<E> comparator) {
		this.comparator = comparator;
	}

	protected int binarySearch(E element) {
		int low = 0, middle, high = elements.size();

		while (low < high) {
			middle = low + (high - low) / 2;

			if (comparator.compare(element, elements.get(middle)) > 0)
				low = middle + 1;
			else
				high = middle;
		}

		return low;
	}

	@Override
	public boolean add(E element) {
		int index = binarySearch(element);
		elements.add(index, element);
		return true;
	}

	@Override
	public E get(int index) {
		return elements.get(index);
	}

	@Override
	public boolean remove(Object element) {
		try {
			E e = (E) element;
			int i = removeElement(e);
			if (i == -1)
				return false;
			return true;
		} catch (ClassCastException e) {
			return false;
		}

	}

	public int removeElement(E element) {
		int index = binarySearch(element);

		if (index < elements.size() && elements.get(index) == element) {
			elements.remove(index);
			return index;
		} else
			return -1;
	}

	@Override
	public void clear() {
		elements.clear();
	}

	@Override
	public boolean contains(Object element) {
		if (indexOf(element) == -1)
			return false;
		else
			return true;

	}

	@Override
	@SuppressWarnings("unchecked")
	public int indexOf(Object element) {
		try {
			E castKey = (E) element;

			int index = binarySearch(castKey);

			if (index < elements.size() && elements.get(index) == element)
				return index;
			else
				return -1;
		} catch (ClassCastException e) {
			return -1;
		}
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public Iterator<E> iterator() {

		return elements.listIterator();
	}

	@Override
	public void add(int arg0, E arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		for (E e : arg0) {
			add(e);
		}
		return true;
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends E> arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		for (Object o : arg0) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		if (size() == 0) {
			return true;
		}
		return false;
	}

	@Override
	public int lastIndexOf(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<E> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<E> listIterator(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E remove(int arg0) {
		
		 return elements.remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E set(int arg0, E arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<E> subList(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
