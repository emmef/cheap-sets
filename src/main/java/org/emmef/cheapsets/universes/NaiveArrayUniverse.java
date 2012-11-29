package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

public class NaiveArrayUniverse<T> extends AbstractIndexedSubset<T> {
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
	public int indexSize() {
		return universe.length;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T elementAt(int index) {
		if (index < universe.length) {
			return (T)universe[index];
		}
		throw new IndexOutOfBoundsException(index +  ">= " + universe.length);
	}

	@Override
	public int size() {
		return universe.length;
	}
}
