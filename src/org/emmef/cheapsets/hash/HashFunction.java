package org.emmef.cheapsets.hash;

import org.emmef.cheapsets.IndexFunction;
import org.emmef.cheapsets.universes.HashIndexedFunction;
import org.emmef.cheapsets.universes.HashedArrayIndexedUniverse;

import com.google.common.collect.ImmutableList;

/**
 * Returns hash codes for elements. A hash function is used by the 
 * {@link HashedArrayIndexedUniverse} to index its elements.
 */
public interface HashFunction {

	int hashCode(Object element);
	
	/**
	 * Get an index function that is based on this hash function and 
	 *    limited to {@code size} elements, where size is a power of two. 
	 * @param size maximum size of the index
	 * @return a {@code non-null} {@link IndexFunction}
	 */
	HashIndexedFunction indexFunction(int size);
	
	public static final HashFunction TRANSPARENT = TransparentHashFunction.INSTANCE;
	public static final HashFunction SMEAR = SmearHashFunction.INSTANCE;
	public static final HashFunction IDENTITY = IdentityHashFunction.INSTANCE;
	public static final HashFunction IDENTITY_SMEAR = IdentitySmearHashFunction.INSTANCE;
	
	public static final ImmutableList<HashFunction> DEFAULT_SAFE_HASHES = ImmutableList.of(TRANSPARENT, SMEAR);
	public static final ImmutableList<HashFunction> DEFAULT_IDENTITY_HASHES = ImmutableList.of(IDENTITY, IDENTITY_SMEAR);
}
