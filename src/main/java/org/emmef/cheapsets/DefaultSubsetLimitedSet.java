package org.emmef.cheapsets;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

class DefaultSubsetLimitedSet<E, S extends IndexSet<S>> extends SubsetLimitedSet<E> {
	private final IndexedSubset<E> universe;
	private final S indexSet; 
	
	DefaultSubsetLimitedSet(IndexedSubset<E> universe, S indexSet) {
		this.universe = checkNotNull(universe, "universe");
		this.indexSet = indexSet;
	}

	@Override
	public int size() {
		return indexSet.count();
	}

	@Override
	public boolean isEmpty() {
		return indexSet.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		int indexOf = universe.indexOf(o);
		
		return indexOf >= 0 && indexSet.presentAt(indexOf);
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private int position = 0;
			private int deletePosition = -1;
			private E next = null;

			@Override
			public boolean hasNext() {
				if (next != null) {
					return true;
				}
				while (next == null && position < universe.indexSize()) {
					E element = universe.elementAt(position);
					if (element != null && indexSet.presentAt(position)) {
						next = element;
						return true;
					}
					position++;
				}
				return false;
			}

			@Override
			public E next() {
				if (hasNext()) {
					E result = next;
					next = null;
					deletePosition = position++;
					return result;
				}
				
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				if (deletePosition == -1) {
					throw new IllegalStateException("Can only call remove() ONCE, directly after a call to next*(");
				}
				indexSet.removeAt(deletePosition);
				deletePosition = -1;
			}
		};
	}

	@Override
	public Object[] toArray() {
		int size = indexSet.count();
		Object[] result = new Object[size];
		int indexSize = universe.indexSize();
		int idx = 0;
		for (int i = 0; i < indexSize; i++) {
			if (indexSet.presentAt(i)) {
				result[idx++] = universe.elementAt(i);
			}
		}
		if (idx == size) {
			return result;
		}
		throw new IllegalStateException("universe.size() does not correspond to the actual number of elements");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		int size = indexSet.count();
		T[] result;
		if (a.length < size) {
			result = (T[])Array.newInstance(a.getClass().getComponentType(), size);
		}
		else {
			result = a;
			if (a.length > size) {
				result[size] = null;
			}
		}
		int indexSize = universe.indexSize();
		int idx = 0;
		for (int i = 0; i < indexSize; i++) {
			if (indexSet.presentAt(i)) {
				result[idx++] = (T)universe.elementAt(i);
			}
		}
		
		if (idx == size) {
			return result;
		}
		
		throw new IllegalStateException("universe.size() does not correspond to the actual number of elements");
	}

	@Override
	public boolean add(E e) {
		return indexSet.setAt(validIndexOf(e));
	}

	@Override
	public boolean remove(Object o) {
		int indexOf = universe.indexOf(o);
		
		return indexOf >= 0 && indexSet.removeAt(indexOf);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsAll(Collection<?> c) {
		if (c instanceof DefaultSubsetLimitedSet && ((DefaultSubsetLimitedSet<?, ?>) c).universe == universe) {
			return indexSet.containsAll(((DefaultSubsetLimitedSet<?, S>) c).indexSet);
		}
		if (c instanceof List) {
			List<?> list = (List<?>) c;
			int size = list.size();
			for (int i = 0; i < size; i++) {
				if (!contains(list.get(i))) {
					return false;
				}
			}
		}
		else {
			for (Object o : c) {
				if (!contains(o)) {
					return false;
				}
			}
		}
		
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (c instanceof DefaultSubsetLimitedSet && ((DefaultSubsetLimitedSet<?, ?>) c).universe == universe) {
			return indexSet.addAll(((DefaultSubsetLimitedSet<?, S>) c).indexSet);
		}
		boolean changed = false;
		if (c instanceof List) {
			List<?> list = (List<?>) c;
			int size = list.size();
			for (int i = 0; i < size; i++) {
				changed |= indexSet.setAt(validIndexOf(list.get(i)));
			}
		}
		else {
			for (Object o : c) {
				changed |= indexSet.setAt(validIndexOf(o));			
			}
		}
		
		return changed;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean retainAll(Collection<?> c) {
		if (c instanceof DefaultSubsetLimitedSet && ((DefaultSubsetLimitedSet<?, ?>) c).universe == universe) {
			return indexSet.retainAll(((DefaultSubsetLimitedSet<?, S>) c).indexSet);
		}
		boolean changed = false;
		for (int i = 0; i < universe.indexSize(); i++) {
			if (indexSet.presentAt(i) && !c.contains(universe.elementAt(i))) {
				indexSet.removeAt(i);
				changed = true;
			}
		}
		
		return changed;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean removeAll(Collection<?> c) {
		if (c instanceof DefaultSubsetLimitedSet && ((DefaultSubsetLimitedSet<?, ?>) c).universe == universe) {
			return indexSet.removeAll(((DefaultSubsetLimitedSet<?, S>) c).indexSet);
		}
		boolean changed = false;
		if (c instanceof List) {
			List<?> list = (List<?>) c;
			int size = list.size();
			for (int i = 0; i < size; i++) {
				int indexOf = universe.indexOf(list.get(i));
				if (indexOf >= 0) {
					changed |= indexSet.removeAt(indexOf);
				}
			}
		}
		else {
			for (Object o : c) {
				int indexOf = universe.indexOf(o);
				if (indexOf >= 0) {
					changed |= indexSet.removeAt(indexOf);
				}
			}
		}
		
		return changed;
	}

	@Override
	public void clear() {
		indexSet.clear();
	}

	@Override
	public DefaultSubsetLimitedSet<E, S> clone() {
		return new DefaultSubsetLimitedSet<E, S>(universe, indexSet.clone());
	}
	
	@Override
	public DefaultSubsetLimitedSet<E, S> cloneEmpty() {
		return new DefaultSubsetLimitedSet<E, S>(universe, indexSet.cloneEmpty());
	}
	
	public int hashCode() {
		int hash = 0;
		int indexSize = universe.indexSize();
		for (int i = 0; i < indexSize; i++) {
			if (indexSet.presentAt(i)) {
				hash += universe.elementAt(i).hashCode();
			}
		}

		return hash;
	};
	
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Set)) {
			return false;
		}
		
		Set<?> set = (Set<?>)(o);
		
		if (size() != set.size()) {
			return false;
		}
		
		return containsAll(set) && set.containsAll(this);
	};
	
	public String toString() {
		if (isEmpty()) {
			return "[]";
		}
		
		StringBuilder string = new StringBuilder();
		int indexSize = universe.indexSize();
		
		boolean first = true;
		for (int i = 0; i < indexSize; i++) {
			if (indexSet.presentAt(i)) {
				if (first) {
					first = false;
					string.append('[');
				}
				else {
					string.append(',');
				}
				
				string.append(universe.elementAt(i));
			}
		}
		string.append(']');
		
		return string.toString();
	};
	
	IndexedSubset<E> getUniverse() {
		return universe;
	}
	
	private int validIndexOf(Object element) {
		int indexOf = universe.indexOf(element);
		
		if (indexOf >= 0) {
			return indexOf;
		}
		
		throw new ElementNotInSubsetException("Element is not backed by " + IndexedSubset.class.getSimpleName() + ": " + element);
	}

}
