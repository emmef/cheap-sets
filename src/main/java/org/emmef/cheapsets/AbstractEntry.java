package org.emmef.cheapsets;

import java.util.Map.Entry;

import com.google.common.base.Objects;

/**
 * Implements {@link #equals(Object)}, {@link #hashCode()} and 
 * {@link #toString()} contract according to the specifications in 
 * {@link Entry}.
 * 
 * @see Entry
 */
public abstract class AbstractEntry<K, V> implements Entry<K, V> {
	/**
	 * @see Entry#hashCode()
	 */
	@Override
	public final int hashCode() {
		Object key = getKey(); 
		Object value = getValue();
		
		return (key != null ? key.hashCode() : 0) ^ (value != null ? value.hashCode() : 0); 
	};
	
	/**
	 * @see Entry#equals(Object)
	 */
	@Override
	public final boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof Entry)) {
			return false;
		}
		
		Entry<?,?> entry = (Entry<?,?>)obj;
		
		return Objects.equal(getKey(), entry.getKey()) && Objects.equal(getValue(), entry.getValue());
	}
	
	
	@Override
	public final String toString() {
		K key = getKey();
		if (key == null) {
			return "null=" + getValue();
		}
		return key.toString() + "=" + getValue();
	}
	
}
