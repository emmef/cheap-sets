package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

public class NaiveArrayUniverse<T> extends AbstractIndexedSubset<T> {
	private final List<T> universe;

	public NaiveArrayUniverse(Set<T> universe) {
		this.universe = ImmutableList.copyOf(checkNotNull(universe, "universe"));
	}
	
	@Override
	public int indexOf(Object element) {
		return universe.indexOf(element);
	}

	@Override
	public int indexSize() {
		return universe.size();
	}

	@Override
	public T elementAt(int index) {
		return universe.get(index);
	}

	@Override
	public int size() {
		return universe.size();
	}
}
