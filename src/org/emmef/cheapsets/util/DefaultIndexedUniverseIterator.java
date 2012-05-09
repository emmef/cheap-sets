package org.emmef.cheapsets.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.emmef.cheapsets.universes.AbstractIndexedUniverse;

public final class DefaultIndexedUniverseIterator<T> implements Iterator<T> {
	private final AbstractIndexedUniverse<T> universe;
	private T next = null;
	private int position = 0;

	/**
	 * @param abstractIndexedUniverse
	 */
	public DefaultIndexedUniverseIterator(AbstractIndexedUniverse<T> abstractIndexedUniverse) {
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