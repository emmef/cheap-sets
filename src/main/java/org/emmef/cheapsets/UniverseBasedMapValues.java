package org.emmef.cheapsets;

import com.google.common.base.Objects;

class UniverseBasedMapValues<K, V> extends UniverseBasedMapView<K, V, V, UniverseBasedMapValues<K, V>> {

	public UniverseBasedMapValues(UniverseBasedMap<K, V> map) {
		super(map);
	}

	@Override
	public boolean contains(Object o) {
		return getMap().containsValue(o);
	}

	@Override
	protected V elementAt(int index) {
		return getMap().getAt(index);
	}
	
	@Override
	public boolean remove(Object o) {
		if (o == null) {
			return false;
		}
		boolean modified = false;
		int indexSize = getSubSet().indexSize();
		for (int i = 0; i < indexSize; i++) {
			Object value = getMap().getAt(i);
			if (value != null && o.equals(value)) {
				modified = getMap().setAt(i, null) != null;
			}
		}
		return modified;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof UniverseBasedMapValues)) {
			return false;
		}

		UniverseBasedMapValues<?,?> set = (UniverseBasedMapValues<?,?>)obj;
		
		if (getSubSet() != set.getSubSet()) {
			return false;
		}
		int indexSize = getSubSet().indexSize();
		UniverseBasedMap<K, V> myMap = getMap();
		UniverseBasedMap<?,?> otherMap = set.getMap();
		for (int i = 0; i < indexSize; i++) {
			if (!Objects.equal(myMap.getAt(i), otherMap.getAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		int indexSize = getSubSet().indexSize();
		for (int i = 0; i < indexSize; i++) {
			Object o = elementAt(i);
			hash += o != null ? o.hashCode() : 0;
			hash *= 31;
		}
		return hash;
	}

	@Override
	boolean modifyAllFromEquivalent(UniverseBasedMapValues<K, V> equivalent, Modification modification) {
		switch (modification) {
		case REMOVE:
			return removeFromCollection(equivalent);
		case RETAIN:
			return retainFromCollection(equivalent);
		default:
			throw new IllegalStateException();
		}
	}
}
