package org.emmef.cheapsets;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.instrument.UnmodifiableClassException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Maps keys inside an {@link IndexedUniverse} to values.
 * <p>
 * Only keys that are in the subset are allowed and for both 
 * keys and values cannot be {@code null}.
 *
 * @param <K> type of keys
 * @param <V> type of values
 * @see IndexedSubSet
 * @see UniverseBasedSet
 */
public class UniverseBasedMap<K, V> implements Map<K, V> {
	private final IndexedUniverse<K> subset;
	private final Object[] values;
	private int size;

	public UniverseBasedMap(IndexedUniverse<K> subset) {
		this.subset = checkNotNull(subset, "subset");
		this.values = new Object[subset.indexBoundary()];
		this.size = 0;
	}
	
	/**
	 * Creates an empty map, based on the same sub set as the provided set.
	 * 
	 * @param set set to use the indexed sub set from
	 * @return an empty map 
	 */
	public static <K, V> UniverseBasedMap<K, V> basedOn(UniverseBasedSet<K> set) {
		checkNotNull(set, "set");
		
		return new UniverseBasedMap<>(set.subSet());
	}
	
	public UniverseBasedMap(Map<K, V> map) {
		checkNotNull(map, "map");
		
		if (map instanceof UniverseBasedMap) {
			UniverseBasedMap<K, V> slm = (UniverseBasedMap<K, V>)map; 
			this.subset = slm.subset;
			this.size = slm.size;
			this.values = slm.values.clone();
		}
		else {
			this.subset = IndexedSubSets.create(map.keySet());
			this.values = new Object[this.subset.indexBoundary()];
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
	 * {@inheritDoc}
	 * 
	 * @throws ElementNotInUniverseException if the key is not supported 
	 *     by the {@link IndexedUniverse} that backs this set.
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
		throw new ElementNotInUniverseException(getClass().getSimpleName() + ": key not backed by subset that this maps key values are limited to: " + key);
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

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ElementNotInUniverseException if some of the keys are not supported 
	 *     by the {@link IndexedUniverse} that backs this set.
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if (m instanceof UniverseBasedMap && ((UniverseBasedMap<? extends K, ? extends V>)m).subset == subset) {
			Object[] otherValues = ((UniverseBasedMap<? extends K, ? extends V>)m).values;
			for (int i = 0; i < subset.indexBoundary(); i++) {
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
		for (int i = 0; i < subset.indexBoundary(); i++) {
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
		return new UniverseBasedMapKeySet<K, V>(this);
	}

	/**
	 * Returns an {@link UnmodifiableClassException} collection with 
	 * all the values.
	 * @return a {@code non-null} {@link Collection}
	 */
	@Override
	public Collection<V> values() {
		return new UniverseBasedMapValues<K, V>(this);
	}

	/**
	 * Returns an unmodifiable set with all key-value entries.
	 * @return a {@code non-null} {@link Set} of {@link Map.Entry}
	 */
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new UniverseBasedMapEntrySet<K, V>(this);
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
	public String toString() {
		if (isEmpty()) {
			return "[]";
		}
		StringBuilder text = new StringBuilder();
		text.append('[');
		int indexSize = subset.indexBoundary();
		boolean first = true;
		for (int i = 0; i < indexSize; i++) {
			Object value = values[i];
			if (value != null) {
				if (first) {
					first = false;
				}
				else {
					text.append(',');
				}
				text.append(subset.elementAt(i));
				text.append('=');
				text.append(value);
			}
		}
		text.append(']');
		return text.toString();
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
		try {
			return (V) values[indexOf];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println();
			throw e;
		}
	}
	
	IndexedUniverse<K> getSubset() {
		return subset;
	}
}
