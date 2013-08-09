package org.emmef.cheapsets;

import java.util.Set;

abstract class UniverseBasedMapSetView<K, V, T, U extends UniverseBasedMapSetView<K, V, T, ?>> extends UniverseBasedMapView<K, V, T, U> implements Set<T>{

	public UniverseBasedMapSetView(UniverseBasedMap<K, V> map) {
		super(map);
	}
	/**
	 * See {@link Set#equals(Object)}
	 */
	@Override
	public final boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Set)) {
			return false;
		}
		Set<?> set = (Set<?>)obj;
		if (size() != set.size()) {
			return false;
		}
		
		return containsAll(set) && set.containsAll(this);
	}
	
	/**
	 * See {@link Set#hashCode()}
	 */
	@Override
	public final int hashCode() {
		int hash = 0;
		int indexSize = getSubSet().indexBoundary();
		for (int i = 0; i < indexSize; i++) {
			Object o = elementAt(i);
			hash += o != null ? o.hashCode() : 0;
		}
		return hash;
	}
}
