package org.emmef.cheapsets.universes;

import java.util.Collection;
import java.util.Iterator;

import org.emmef.cheapsets.IndexedUniverse;
import org.emmef.cheapsets.util.DefaultIndexedUniverseIterator;
import org.emmef.cheapsets.util.IndexedUniverseHelper;

public abstract class AbstractIndexedUniverse<T> implements IndexedUniverse<T> {
	@Override
	public final boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	@Override
	public final boolean isEmpty() {
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return new DefaultIndexedUniverseIterator<T>(this);
	}

	@Override
	public Object[] toArray() {
		return IndexedUniverseHelper.toArray(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> U[] toArray(U[] a) {
		return IndexedUniverseHelper.toArray((IndexedUniverse<? extends U>)this, a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return IndexedUniverseHelper.containsAll(this, c);
	}
	
	@Override
	public boolean add(T e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
}
