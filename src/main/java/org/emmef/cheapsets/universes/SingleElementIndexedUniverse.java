package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkNotNull;

import org.emmef.cheapsets.IndexedUniverse;

public class SingleElementIndexedUniverse<T> implements IndexedUniverse<T> {
	Object element;

	public SingleElementIndexedUniverse(Object element) {
		this.element = checkNotNull(element, "element");
	}
	
	@Override
	public int indexOf(Object element) {
		return this.element.equals(element) ? 0 : -1;
	}

	@Override
	public int indexBoundary() {
		return 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T elementAt(int index) {
		if (index != 0) {
			throw new IndexOutOfBoundsException();
		}
		return (T) element;
	}

	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public String toString() {
		return IndexedUniverses.toString(this);
	}
}
