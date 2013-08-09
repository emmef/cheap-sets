package org.emmef.cheapsets.hash;


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
}