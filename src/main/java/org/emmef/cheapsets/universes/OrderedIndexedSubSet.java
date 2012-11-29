package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Set;

public final class OrderedIndexedSubSet<T extends Comparable<T>> extends AbstractIndexedSubset<T> {
	private final Object[] universe;

	public OrderedIndexedSubSet(Set<T> universe) {
		this.universe = checkNotNull(universe, "universe").toArray();
		Arrays.sort(this.universe);
		for (int i = 1; i < this.universe.length; i++) {
			@SuppressWarnings("unchecked")
			T o1 = (T) this.universe[i - 1];
			@SuppressWarnings("unchecked")
			T o2 = (T) this.universe[i];
			if (o1.compareTo(o2) == 0) {
				throw new IllegalArgumentException("Cannot have universe elements that compare equal");
			}
		}
	}

	@Override
	public int indexOf(Object element) {
		int search = Arrays.binarySearch(this.universe, element);
		
		return search >= 0 ? search : -1;
	}

	@Override
	public int indexSize() {
		return universe.length;
	}

	@Override
	public int size() {
		return universe.length;
	}

	@Override
	public T elementAt(int index) {
		if (index < universe.length) {
			@SuppressWarnings("unchecked")
			T cast = (T) universe[index];
			return cast;
		}
		
		throw new IndexOutOfBoundsException(index + " >= " + universe.length);
	}
}
