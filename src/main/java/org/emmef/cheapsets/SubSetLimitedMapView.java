package org.emmef.cheapsets;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

abstract class SubSetLimitedMapView<K, V, T, U extends SubSetLimitedMapView<K, V, ?, ?>> implements Collection<T> {
	enum Modification { REMOVE, RETAIN }
	
	private final SubSetLimitedMap<K, V> map;

	public SubSetLimitedMapView(SubSetLimitedMap<K, V> map) {
		this.map = map;
	}
	
	@Override
	public int size() {
		return map.size();
	}
	
	public final boolean add(T e) {
		throw new UnsupportedOperationException();
	};
	
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			final int maxPosition = map.getSubset().indexSize() - 1;
			int position = 0;
			int removeAt = -1;
			T next;

			@Override
			public boolean hasNext() {
				if (next != null) {
					return true;
				}
				if (position >= maxPosition) {
					return false;
				}
				next = elementAt(position);
				while (next == null && position < maxPosition) {
					position++;
					next = elementAt(position);
				}
				
				return next != null;
			}

			@Override
			public T next() {
				if (hasNext()) {
					T result = next;
					next = null;
					removeAt = position++;
					return result;
				}
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				if (removeAt >= 0) {
					map.setAt(removeAt, null);
					removeAt = -1;
					return;
				}
				throw new IllegalStateException();
			}
		};
	}

	@Override
	public Object[] toArray() {
		int size = map.size();
		Object[] result = new Object[size];
		int indexSize = map.getSubset().indexSize();
		int idx = 0;
		for (int i = 0; i < indexSize; i++) {
			Object element = elementAt(i);
			if (element != null) {
				result[idx++] = element;
			}
		}
		if (idx == size) {
			return result;
		}
		throw new ConcurrentModificationException("size does not correspond to the actual number of elements");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <W> W[] toArray(W[] a) {
		int size = map.size();
		W[] result;
		if (a.length < size) {
			result = (W[])Array.newInstance(a.getClass().getComponentType(), size);
		}
		else {
			result = a;
			if (a.length > size) {
				result[size] = null;
			}
		}
		
		int idx = 0;
		int indexSize = map.getSubset().indexSize();
		
		for (int i = 0; i < indexSize; i++) {
			W element = (W)elementAt(i);
			if (element != null) {
				result[idx++] = element;
			}
		}
		
		if (idx == size) {
			return result;
		}
		
		throw new ConcurrentModificationException("size does not correspond to the actual number of elements");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		if (c instanceof List) {
			List<?> list = (List<?>)c;
			for (int i = 0; i < list.size(); i++) {
				if (!contains(list.get(i))) {
					return false;
				}
			}
			return true;
		}
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public final boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return modifyFrom(c, Modification.REMOVE);
	}

	private boolean sameErasedClassAndSubSet(Collection<?> c) {
		return getClass().equals(c.getClass()) && getSubSet().equals(((SubSetLimitedMapView<?,?,?,?>)c).getSubSet());
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return modifyFrom(c, Modification.RETAIN);
	}

	@Override
	public final void clear() {
		map.clear();
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	SubSetLimitedMap<K, V> getMap() {
		return map;
	}
	
	protected abstract T elementAt(int index);
	abstract boolean modifyAllFromEquivalent(U equivalent, Modification modification);
	
	IndexedSubset<K> getSubSet() {
		return map.getSubset();
	}
	
	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		
		text.append('[');
		boolean first = true;
		int indexSize = map.getSubset().indexSize();
		for (int i = 0; i < indexSize; i++) {
			T elementAt = elementAt(i);
			if (elementAt != null) {
				if (first) {
					first = false;
				}
				else {
					text.append(',');
				}
				text.append(elementAt);
			}
		}
		text.append(']');
		return text.toString();
	}

	protected boolean removeFromCollection(Collection<?> collection) {
		boolean modified = false;
		for (Object element : collection) {
			modified |= remove(element);
		}
		return modified;
	}

	protected boolean removeFromList(List<?> list) {
		int count = list.size();
		boolean modified = false;
		for (int i = 0; i < count; i++) {
			modified |= remove(list.get(i));
		}

		return modified;
	}
	
	protected boolean retainFromCollection(Collection<?> c) {
		int indexSize = map.getSubset().indexSize();
		boolean modified = false;
		for (int i = 0; i < indexSize; i++) {
			T elementAt = elementAt(i);
			if (elementAt != null && !c.contains(elementAt)) {
				modified = true;
				map.setAt(i, null);
			}
		}
		return modified;
	}

	private boolean modifyFrom(Collection<?> c, Modification modification) {
		checkNotNull(c, "c");
		if (sameErasedClassAndSubSet(c)) {
			@SuppressWarnings("unchecked")
			U equivalent = (U)c;
			return modifyAllFromEquivalent(equivalent, modification);
		}
		if (modification == Modification.RETAIN) {
			return retainFromCollection(c);
		}
		if (c instanceof List) {
			return removeFromList((List<?>)c);
		}
		
		return removeFromCollection(c);
	}
}

