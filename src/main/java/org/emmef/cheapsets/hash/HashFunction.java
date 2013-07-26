package org.emmef.cheapsets.hash;

import org.emmef.cheapsets.IndexFunction;
import org.emmef.cheapsets.universes.HashIndexedFunction;
import org.emmef.cheapsets.universes.HashedArrayIndexedSubset;

import com.google.common.collect.ImmutableList;

/**
 * Returns hash codes for elements. A hash function is used by the
 * {@link HashIndexedFunction} and the {@link HashedArrayIndexedSubset} to index
 * its elements.
 * 
 * @documented 2013-07-26
 */
public interface HashFunction {
	/**
	 * Returns the hash-code for the specified element.
	 * 
	 * @param element element to be hashed.
	 * @return the hash-code for the specified element.
	 * @documented 2013-07-26
	 */
	int hashCode(Object element);
	
	/**
	 * Get an index function that is based on this hash function and 
	 *    limited to {@code size} elements, where size is a power of two.
	 * 
	 * @param size maximum size of the index
	 * @return a {@code non-null} {@link IndexFunction}
	 * @documented 2013-07-26
	 */
	HashIndexedFunction indexFunction(int size);
	
	/**
	 * Function that gives the element's own hash-code or zero if the element is {@code null}.
	 *  
	 * @documented 2013-07-26
	 */
	HashFunction TRANSPARENT = TransparentHashFunction.INSTANCE;
	
	/**
	 * Function that gives the element's own hash-code smeared by an algorithm,
	 * or zero if the element is {@code null}.
	 * 
	 * @documented 2013-07-26
	 */
	HashFunction SMEAR = SmearHashFunction.INSTANCE;
	
	/**
	 * Function that returns {@link System#identityHashCode(Object)} for the element.
	 * 
	 * @documented 2013-07-26
	 */
	HashFunction IDENTITY = IdentityHashFunction.INSTANCE;
	
	/**
	 * Function that returns {@link System#identityHashCode(Object)} for the element, 
	 * smeared by an algorithm.
	 * 
	 * @documented 2013-07-26
	 */
	HashFunction IDENTITY_SMEAR = IdentitySmearHashFunction.INSTANCE;
	
	/**
	 * A list of hash functions, that use information in the elements.
	 * 
	 * @documented 2013-07-26
	 */
	ImmutableList<HashFunction> DEFAULT_FUNCTIONS = ImmutableList.of(TRANSPARENT, SMEAR);
	
	/**
	 * A list of hash functions, based on identity hash.
	 * 
	 * @documented 2013-07-26
	 */
	ImmutableList<HashFunction> DEFAULT_IDENTITY_FUNCTIONS = ImmutableList.of(IDENTITY, IDENTITY_SMEAR);
}
