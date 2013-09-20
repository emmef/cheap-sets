package org.emmef.cheapsets.universes;

import org.emmef.cheapsets.IndexedUniverse;
import org.emmef.cheapsets.hash.HashFunction;

public final class HashIndexedUniverse<T> implements IndexedUniverse<T> {
	private final int elementCount;
	private final Object[] universe;
	private final int size;
	private final int mask;
	private final HashFunction hashFunction;

	HashIndexedUniverse(int elementCount, Object[] target, int size, HashFunction hashFunction) {
		this.elementCount = elementCount;
		this.universe = target;
		this.size = size;
		this.mask = size - 1;
		this.hashFunction = hashFunction;
	}

	@Override
	public int indexOf(Object element) {
		int index = mask & hashFunction.hashCode(element);
		
		if (universe[index] != null && universe[index].equals(element)) {
			return index;
		}
		
		return -1;
	}

	@Override
	public int indexBoundary() {
		return size;
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
		return elementCount;
	}
	
	@Override
	public String toString() {
		return IndexedUniverses.toString(this);
	}
}