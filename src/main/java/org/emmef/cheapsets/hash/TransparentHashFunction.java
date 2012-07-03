package org.emmef.cheapsets.hash;

import org.emmef.cheapsets.universes.HashIndexedFunction;

/**
 * Hash function that echoes the hash code of the element. 
 * 
 * @see HashFunction
 */
public enum TransparentHashFunction implements HashFunction {
	INSTANCE;
	
	@Override
	public int hashCode(Object element) {
		return element != null ? element.hashCode() : 0;
	}
	
	private static final HashIndexFunctionSet SET = new HashIndexFunctionSet(INSTANCE);
	
	@Override
	public HashIndexedFunction indexFunction(int size) {
		return SET.getIndexFunction(size);
	}
}