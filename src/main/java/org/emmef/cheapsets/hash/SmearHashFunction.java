package org.emmef.cheapsets.hash;


/**
 * Hash function that returns the hash-code of the element, optimized by a 
 * smearing algorithm.
 * <p>
 * The smearing algorithm was written by Doug Lea with assistance from members 
 * of JCP JSR-166 Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain.
 * 
 * @see HashFunction
 */
public enum SmearHashFunction implements HashFunction {
	INSTANCE;
	
	/**
	 * Returns a smeared (optimized) hash code for the provided hash.
	 * <p>
	 * This algorithm was written by Doug Lea with assistance from members of JCP
	 * JSR-166 Expert Group and released to the public domain, as explained at
	 * http://creativecommons.org/licenses/publicdomain
	 * <p>
	 * @param hashCode original hash code
	 * @return a smeared (optimized) hash code for the provided hash.
	 */
	public static int smear(int hashCode) {
		hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
		return hashCode ^ (hashCode >>> 7) ^ (hashCode >>> 4);
	}
	
	@Override
	public int hashCode(Object element) {
		return element != null ? SmearHashFunction.smear(element.hashCode()) : 0;
	}
}
