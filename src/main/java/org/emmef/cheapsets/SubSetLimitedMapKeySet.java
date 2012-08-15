package org.emmef.cheapsets;


class SubSetLimitedMapKeySet<K, V> extends SubSetLimitedMapSetView<K, V, K, SubSetLimitedMapKeySet<K, V>> {
	public SubSetLimitedMapKeySet(SubSetLimitedMap<K, V> map) {
		super(map);
	}

	@Override
	public boolean contains(Object o) {
		return getMap().containsKey(o);
	}

	@Override
	public boolean remove(Object o) {
		return getMap().remove(o) != null;
	}

	@Override
	protected K elementAt(int index) {
		if (getMap().getAt(index) != null) {
			return getMap().getSubset().elementAt(index);
		}
		return null;
	}

	@Override
	boolean modifyAllFromEquivalent(SubSetLimitedMapKeySet<K, V> equivalent, Modification modification) {
		boolean modified = false;
		int indexSize = getMap().getSubset().indexSize();
		switch (modification) {
		case REMOVE:
			for (int i = 0; i < indexSize; i++) {
				if (equivalent.getMap().getAt(i) != null) {
					modified |= getMap().setAt(i, null) != null;
				}
			}
			break;
		case RETAIN:
			for (int i = 0; i < indexSize; i++) {
				if (equivalent.getMap().getAt(i) == null) {
					modified = getMap().setAt(i, null) != null;
				}
			}
			break;
		default:
			throw new IllegalStateException(); 
		}

		return modified;
	}
}
