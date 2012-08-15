package org.emmef.cheapsets;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.instrument.UnmodifiableClassException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Maps keys inside an {@link IndexedSubset} to values.
 * <p>
 * Only keys that are in the subset are allowed and for both 
 * keys and values cannot be {@code null}.
 *
 * @param <K> type of keys
 * @param <V> type of values
 * @see IndexedSubSet
 * @see SubsetLimitedSet
 */
public class SubSetLimitedMap<K, V> implements Map<K, V> {
	private final IndexedSubset<K> subset;
	private final Object[] values;
	private int size;

	public SubSetLimitedMap(IndexedSubset<K> subset) {
		this.subset = checkNotNull(subset, "subset");
		this.values = new Object[subset.indexSize()];
		this.size = 0;
	}
	
	public SubSetLimitedMap(Map<K, V> map) {
		checkNotNull(map, "map");
		
		if (map instanceof SubSetLimitedMap) {
			SubSetLimitedMap<K, V> slm = (SubSetLimitedMap<K, V>)map; 
			this.subset = slm.subset;
			this.size = slm.size;
			this.values = slm.values.clone();
		}
		else {
			this.subset = IndexedSubSets.createSubset(map.keySet());
			this.values = new Object[this.subset.indexSize()];
			this.size = 0;
			putAll(map);
		}
	}
	
	@Override
	public final int size() {
		return size;
	}

	@Override
	public final boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		int indexOf = subset.indexOf(key);
		
		return indexOf >= 0 && values[indexOf] != null;
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null) {
			return false;
		}
		
		for (int i = 0; i < values.length; i++) {
			if (value.equals(values[i])) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		int indexOf = subset.indexOf(key);
		
		return indexOf >= 0 ? (V)values[indexOf] : null;
	}

	/**
	 * Map the key to the specified value if the key is backed by 
	 * the used subset ({@link IndexedSubset}).
	 * 
	 * @see Map
	 * @throws IllegalArgumentException if either key or value are {@code null}.
	 */
	@Override
	public V put(K key, V value) {
		if (key == null) {
			throw new NullPointerException(getClass().getSimpleName() + " cannot contain null keys");
		}
		if (value == null) {
			throw new NullPointerException(getClass().getSimpleName() + " cannot contain null values");
		}
		int indexOf = subset.indexOf(key);
		
		if (indexOf >= 0) {
			V existing = setAt(indexOf, value);
			return existing;
		}
		
		throw new IllegalArgumentException(getClass().getSimpleName() + ": key not backed by the subset that this maps key values are limited to: " + key);
	}

	@Override
	public V remove(Object key) {
		if (key == null) {
			return null;
		}
		
		int indexOf = subset.indexOf(key);
		
		if (indexOf >= 0) {
			return setAt(indexOf, null);
		}

		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if (m instanceof SubSetLimitedMap && ((SubSetLimitedMap<? extends K, ? extends V>)m).subset == subset) {
			Object[] otherValues = ((SubSetLimitedMap<? extends K, ? extends V>)m).values;
			for (int i = 0; i < subset.indexSize(); i++) {
				@SuppressWarnings("unchecked")
				V otherValue = (V)otherValues[i];
				if (otherValue != null) {
					setAt(i, otherValue);
				}
			}
		}
		else {
			for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
				put(entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	public void clear() {
		for (int i = 0; i < subset.indexSize(); i++) {
			values[i] = null;
		}
		size = 0;
	}

	/**
	 * Returns an unmodifiable set with all keys.
	 * @return a {@code non-null} {@link Set}
	 */
	@Override
	public Set<K> keySet() {
		return new SubSetLimitedMapKeySet<K, V>(this);
	}

	/**
	 * Returns an {@link UnmodifiableClassException} collection with 
	 * all the values.
	 * @return a {@code non-null} {@link Collection}
	 */
	@Override
	public Collection<V> values() {
		return new SubSetLimitedMapValues<K, V>(this);
	}

	/**
	 * Returns an unmodifiable set with all key-value entries.
	 * @return a {@code non-null} {@link Set} of {@link Map.Entry}
	 */
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new SubSetLimitedMapEntrySet<K, V>(this);
	}
	
	/**
	 * @see Map#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Map)) {
			return false;
		}
		
		return entrySet().equals(((Map<?,?>)obj).entrySet()); 
	}
	
	@Override
	public int hashCode() {
		return entrySet().hashCode();
	}

	final V setAt(int indexOf, V value) {
		@SuppressWarnings("unchecked")
		V existing = (V)values[indexOf];
		
		if (value == null) {
			values[indexOf] = null;
			if (existing != null) {
				size--;
			}
			return existing;
		}
		
		values[indexOf] = value;
		if (existing == null) {
			size++;
		}
		return existing;
	}
	
	@SuppressWarnings("unchecked")
	final V getAt(int indexOf) {
		return (V) values[indexOf];
	}
	
	IndexedSubset<K> getSubset() {
		return subset;
	}
}
