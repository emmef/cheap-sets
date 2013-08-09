package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.emmef.cheapsets.IndexedUniverse;

public class NaiveArrayUniverse<T> implements IndexedUniverse<T> {
	private final Object[] universe;

	public NaiveArrayUniverse(Set<T> universe) {
		checkNotNull(universe, "universe");
		this.universe = new Object[universe.size()];
		int i = 0;
		for (Object o : universe) {
			checkNotNull(o, "Universe element cannot be null");
			this.universe[i++] = o;
		}
	}
	
	@Override
	public int indexOf(Object element) {
		if (element == null) {
			return -1;
		}
		for (int i = 0; i < universe.length; i++) {
			if (universe[i].equals(element)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int indexBoundary() {
		return universe.length;
	}

	@Override
	public T elementAt(int index) {
		if (index < universe.length) {
			@SuppressWarnings("unchecked")
			T element = (T)universe[index];
			return element;
		}
		throw new IndexOutOfBoundsException(index +  ">= " + universe.length);
	}

	@Override
	public int size() {
		return universe.length;
	}
}
