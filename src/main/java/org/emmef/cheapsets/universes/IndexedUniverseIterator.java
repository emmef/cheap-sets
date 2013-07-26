package org.emmef.cheapsets.universes;

import java.util.Iterator;
import java.util.NoSuchElementException;


final class IndexedUniverseIterator<T> implements Iterator<T> {
	private final AbstractIndexedSubset<T> universe;
	private T next = null;
	private int position = 0;

	public IndexedUniverseIterator(AbstractIndexedSubset<T> abstractIndexedUniverse) {
		universe = abstractIndexedUniverse;
	}

	@Override
	public boolean hasNext() {
		if (next != null) {
			return true;
		}
		do {
			next = universe.elementAt(position++);
		}
		while (next == null && position < universe.indexSize());
		
		return next != null;
	}

	@Override
	public T next() {
		if (hasNext()) {
			T result = next;
			next = null;
			return result;
		}
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}