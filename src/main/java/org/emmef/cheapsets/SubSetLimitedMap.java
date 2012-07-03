package org.emmef.cheapsets;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

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
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
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
			throw new IllegalArgumentException(getClass().getSimpleName() + " cannot contain null keys");
		}
		if (value == null) {
			throw new IllegalArgumentException(getClass().getSimpleName() + " cannot contain null values");
		}
		int indexOf = subset.indexOf(key);
		
		if (indexOf >= 0) {
			@SuppressWarnings("unchecked")
			V existing = (V)values[indexOf];
			values[indexOf] = value;
			if (existing == null) {
				size++;
			}
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
			@SuppressWarnings("unchecked")
			V result = (V)values[indexOf];
			values[indexOf] = null;
			if (result != null) {
				size--;
			}
			return result;
		}

		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if (m instanceof SubSetLimitedMap && ((SubSetLimitedMap<? extends K, ? extends V>)m).subset == subset) {
			Object[] otherValues = ((SubSetLimitedMap<? extends K, ? extends V>)m).values;
			for (int i = 0; i < subset.indexSize(); i++) {
				Object otherValue = otherValues[i];
				if (otherValue != null) {
					Object myValue = values[i];
					values[i] = otherValues[i];
					if (myValue == null) {
						size++;
					}
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
		return size > 0 ? new KeySet() : ImmutableSet.<K>of();
	}

	/**
	 * Returns an {@link UnmodifiableClassException} collection with 
	 * all the values.
	 * @return a {@code non-null} {@link Collection}
	 */
	@Override
	public Collection<V> values() {
		return size > 0 ? new Values() : ImmutableSet.<V>of();
	}

	/**
	 * Returns an unmodifiable set with all key-value entries.
	 * @return a {@code non-null} {@link Set} of {@link Map.Entry}
	 */
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return size > 0 ? new EntrySet() : ImmutableSet.<Map.Entry<K, V>>of();
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

	abstract class SubCollection<T> implements Collection<T> {
		@Override
		public int size() {
			return size;
		}
		
		@Override
		public boolean isEmpty() {
			return size == 0;
		}

		@Override
		public Iterator<T> iterator() {
			return new Iterator<T>() {
				T next;
				int position = 0;

				@Override
				public boolean hasNext() {
					while (next == null && position < subset.indexSize()) {
						next = elementAt(position++);
					}
					return next != null;
				}

				@Override
				public T next() {
					if (hasNext()) {
						T result = next;
						next = null;
						return result;
					}
					throw new NoSuchElementException();
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}};
		}

		@Override
		public Object[] toArray() {
			Object[] result = new Object[size];
			int indexSize = subset.indexSize();
			int idx = 0;
			for (int i = 0; i < indexSize; i++) {
				Object element = elementAt(i);
				if (element != null) {
					result[idx++] = element;
				}
			}
			if (idx == size) {
				return result;
			}
			throw new ConcurrentModificationException("size does not correspond to the actual number of elements");
		}

		@SuppressWarnings("unchecked")
		@Override
		public <U> U[] toArray(U[] a) {
			U[] result;
			if (a.length < size) {
				result = (U[])Array.newInstance(a.getClass().getComponentType(), size);
			}
			else {
				result = a;
				if (a.length > size) {
					result[size] = null;
				}
			}
			
			int idx = 0;
			int indexSize = subset.indexSize();
			
			for (int i = 0; i < indexSize; i++) {
				U element = (U)elementAt(i);
				if (element != null) {
					result[idx++] = element;
				}
			}
			
			if (idx == size) {
				return result;
			}
			
			throw new ConcurrentModificationException("size does not correspond to the actual number of elements");
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
		public boolean containsAll(Collection<?> c) {
			if (c instanceof List) {
				List<?> list = (List<?>)c;
				for (int i = 0; i < list.size(); i++) {
					if (!contains(list.get(i))) {
						return false;
					}
				}
				return true;
			}
			for (Object o : c) {
				if (!contains(o)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends T> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}
		
		protected abstract T elementAt(int index);
		
		IndexedSubset<K> getSubSet() {
			return subset;
		}
		
		Object[] getValues() {
			return values;
		}
	}
	
	private abstract class SubSet<T> extends SubCollection<T> implements Set<T> {
		/**
		 * See {@link Set#equals(Object)}
		 */
		@Override
		public boolean equals(Object obj) {
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
		public int hashCode() {
			int hash = 0;
			int indexSize = subset.indexSize();
			for (int i = 0; i < indexSize; i++) {
				Object o = elementAt(i);
				hash += o != null ? o.hashCode() : 0;
			}
			return hash;
		}
	}
	
	private class KeySet extends SubSet<K> implements Set<K> {
		@Override
		public boolean contains(Object o) {
			return containsKey(o);
		}

		@Override
		protected K elementAt(int index) {
			if (values[index] != null) {
				return subset.elementAt(index);
			}
			
			return null;
		}
	}
	
	private class EntrySet extends SubSet<Map.Entry<K, V>> implements Set<Map.Entry<K, V>> {
		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
			int indexOf = subset.indexOf(entry.getKey());
			
			return indexOf >= 0 && Objects.equal(values[indexOf],entry.getValue());
		}

		@Override
		protected java.util.Map.Entry<K, V> elementAt(final int index) {
			return new SubsetEntry(index);
		}
		
		private final class SubsetEntry implements Map.Entry<K, V> {
			private final int index;

			private SubsetEntry(int index) {
				this.index = index;
			}

			@Override
			public K getKey() {
				return subset.elementAt(index);
			}

			@SuppressWarnings("unchecked")
			@Override
			public V getValue() {
				return (V) values[index];
			}

			@Override
			public V setValue(V value) {
				throw new UnsupportedOperationException(); 
			}

			/**
			 * @See {@link Map.Entry#hashCode()}
			 */
			@Override
			public int hashCode() {
				K key = getKey();
				V value = getValue();
				int keyHash = key != null ? key.hashCode() : 0;
				int valueHash = value != null ? value.hashCode() : 0;
				return keyHash ^ valueHash;
			}

			/**
			 * @See {@link Map.Entry#equals(Object)}
			 */
			@Override
			public boolean equals(Object obj) {
				if (obj == this) {
					return true;
				}
				if (!(obj instanceof Map.Entry)) {
					return false;
				}
				
				Map.Entry<?,?> entry = (Map.Entry<?,?>)obj;
				
				return Objects.equal(entry.getKey(), getKey()) && Objects.equal(entry.getValue(), getValue());
			}
		}
	}
	
	private class Values extends SubCollection<V> {

		@Override
		public boolean contains(Object o) {
			return containsValue(o);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected V elementAt(int index) {
			return (V)values[index];
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof SubSetLimitedMap.Values)) {
				return false;
			}
			@SuppressWarnings("rawtypes")
			SubSetLimitedMap.Values set = (SubSetLimitedMap.Values)obj;
			
			if (subset != set.getSubSet()) {
				return false;
			}
			if (values.length != set.getValues().length) {
				return false;
			}
			for (int i = 0; i < values.length; i++) {
				if (!Objects.equal(values[i], set.getValues()[i])) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public int hashCode() {
			int hash = 17;
			int indexSize = subset.indexSize();
			for (int i = 0; i < indexSize; i++) {
				Object o = elementAt(i);
				hash += o != null ? o.hashCode() : 0;
				hash *= 31;
			}
			return hash;
		}

	}
}
