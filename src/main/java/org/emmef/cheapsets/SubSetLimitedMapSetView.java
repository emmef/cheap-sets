package org.emmef.cheapsets;

import java.util.Set;

abstract class SubSetLimitedMapSetView<K, V, T, U extends SubSetLimitedMapSetView<K, V, T, ?>> extends SubSetLimitedMapView<K, V, T, U> implements Set<T>{

	public SubSetLimitedMapSetView(SubSetLimitedMap<K, V> map) {
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
		int indexSize = getSubSet().indexSize();
		for (int i = 0; i < indexSize; i++) {
			Object o = elementAt(i);
			hash += o != null ? o.hashCode() : 0;
		}
		return hash;
	}
}
