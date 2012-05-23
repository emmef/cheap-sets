package org.emmef.cheapsets;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class AbstractIndexedUniverseSet<E, S extends IndexSet<S>> implements IndexedUniverseSet<E> {
	private final IndexedUniverse<E> universe;
	private final S indexSet; 
	
	public AbstractIndexedUniverseSet(IndexedUniverse<E> universe, S indexSet) {
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
			private E next = null;

			@Override
			public boolean hasNext() {
				if (next != null) {
					return true;
				}
				while (next == null && position < universe.indexSize()) {
					E element = universe.elementAt(position);
					if (indexSet.presentAt(position)) {
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
					return result;
				}
				
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
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
		T[] result = (T[])Array.newInstance(a.getClass().getComponentType(), size);
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
		if (c instanceof AbstractIndexedUniverseSet && ((AbstractIndexedUniverseSet<?, ?>) c).universe == universe) {
			return indexSet.containsAll(((AbstractIndexedUniverseSet<?, S>) c).indexSet);
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
		if (c instanceof AbstractIndexedUniverseSet && ((AbstractIndexedUniverseSet<?, ?>) c).universe == universe) {
			return indexSet.addAll(((AbstractIndexedUniverseSet<?, S>) c).indexSet);
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
		if (c instanceof AbstractIndexedUniverseSet && ((AbstractIndexedUniverseSet<?, ?>) c).universe == universe) {
			return indexSet.retainAll(((AbstractIndexedUniverseSet<?, S>) c).indexSet);
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
		if (c instanceof AbstractIndexedUniverseSet && ((AbstractIndexedUniverseSet<?, ?>) c).universe == universe) {
			return indexSet.removeAll(((AbstractIndexedUniverseSet<?, S>) c).indexSet);
		}
		boolean changed = false;
		if (c instanceof List) {
			List<?> list = (List<?>) c;
			int size = list.size();
			for (int i = 0; i < size; i++) {
				changed |= indexSet.removeAt(validIndexOf(list.get(i)));
			}
		}
		else {
			for (Object o : c) {
				changed |= indexSet.removeAt(validIndexOf(o));			
			}
		}
		
		return changed;
	}

	@Override
	public void clear() {
		indexSet.clear();
	}

	@Override
	public AbstractIndexedUniverseSet<E, S> clone() {
		return new AbstractIndexedUniverseSet<E, S>(universe, indexSet.clone());
	}
	
	@Override
	public AbstractIndexedUniverseSet<E, S> cloneEmpty() {
		return new AbstractIndexedUniverseSet<E, S>(universe, indexSet.cloneEmpty());
	}
	
	IndexedUniverse<E> getUniverse() {
		return universe;
	}
	
	private int validIndexOf(Object element) {
		int indexOf = universe.indexOf(element);
		
		if (indexOf >= 0) {
			return indexOf;
		}
		
		throw new IllegalArgumentException("Element is not backed by " + IndexedUniverse.class.getSimpleName() + ": " + element);
	}

}
