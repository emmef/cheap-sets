package org.emmef.cheapsets;

import java.util.Map;

import com.google.common.base.Objects;

class UniverseBasedMapEntrySet<K, V> extends UniverseBasedMapSetView<K, V, Map.Entry<K, V>, UniverseBasedMapEntrySet<K, V>> {
	public UniverseBasedMapEntrySet(UniverseBasedMap<K, V> map) {
		super(map);
	}
	
	@Override
	public boolean contains(Object o) {
		if (!(o instanceof Map.Entry)) {
			return false;
		}
		Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
		int indexOf = getSubSet().indexOf(entry.getKey());
		
		return indexOf >= 0 && Objects.equal(getMap().getAt(indexOf),entry.getValue());
	}

	@Override
	protected java.util.Map.Entry<K, V> elementAt(final int index) {
		return getMap().getAt(index) != null ? new UniverseBasedMapEntry<K, V>(getMap(), index) : null;
	}
	
	@Override
	public boolean remove(Object object) {
		if (object instanceof Map.Entry) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>)object;
			
			return getMap().remove(entry.getKey()) != null;
		}
		
		return false;
	}

	@Override
	boolean modifyAllFromEquivalent(UniverseBasedMapEntrySet<K, V> equivalent, Modification modification) {
		boolean modified = false;
		int indexSize = getSubSet().indexSize();
		
		switch (modification) {
		case REMOVE:
			for (int i = 0; i < indexSize; i++) {
				V value = equivalent.getMap().getAt(i);
				if (value != null && value.equals(getMap().getAt(i))) {
					modified = true;
					getMap().setAt(i, null);
				}
			}
			break;
		case RETAIN:
			for (int i = 0; i < indexSize; i++) {
				V value = equivalent.getMap().getAt(i);
				if (value == null) {
					modified |= getMap().setAt(i, null) != null;
				}
				else {
					Object myValue = getMap().getAt(i);
					if (!value.equals(myValue)) {
						modified = true;
						getMap().setAt(i, null);
					}
				}
			}
			break;
		default:
			throw new IllegalStateException(); 
		}
		
		return modified;
	}
}
