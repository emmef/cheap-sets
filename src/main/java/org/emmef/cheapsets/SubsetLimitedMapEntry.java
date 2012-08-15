package org.emmef.cheapsets;

final class SubsetLimitedMapEntry<K, V> extends AbstractEntry<K, V> {
	private final int index;
	private final SubSetLimitedMap<K, V> map;

	SubsetLimitedMapEntry(SubSetLimitedMap<K, V> map, int index) {
		this.map = map;
		this.index = index;
	}

	@Override
	public K getKey() {
		return map.getSubset().elementAt(index);
	}

	@Override
	public V getValue() {
		return (V) map.getAt(index);
	}

	@Override
	public V setValue(V value) {
		return map.setAt(index, value);
	}
}