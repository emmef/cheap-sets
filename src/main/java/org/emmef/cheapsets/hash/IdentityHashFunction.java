package org.emmef.cheapsets.hash;

import org.emmef.cheapsets.IndexFunction;

/**
 * A hash function that returns the system identity hash for the object.
 * <p>
 * Do not use this for elements that have a properly written {@link #equals(Object)}
 * and {@link #hashCode(Object)}, as the hash code for equivalent objects 
 * is very likely to be different.
 * 
 * @see HashFunction
 */
public enum IdentityHashFunction implements HashFunction {
	INSTANCE;
	
	@Override
	public int hashCode(Object element) {
		return System.identityHashCode(element);
	}
	
	@Override
	public IndexFunction indexFunction(int size) {
		return new HashIndexedFunction(size, this);
	}
}