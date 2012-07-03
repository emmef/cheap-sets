package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import org.emmef.cheapsets.IndexedSubset;
import org.emmef.cheapsets.hash.HashFunction;
import org.emmef.cheapsets.util.IndexedUniverseHelper;

public class HashedArrayIndexedSubset<T> extends IndexFunctionIndexedUniverse<T> {
	private HashedArrayIndexedSubset(Object[] hashedElementArray, HashIndexedFunction function, int elementCount) {
		super(hashedElementArray, function, elementCount);
	}
	
	/**
	 * Creates a hash-based {@link IndexedSubset} that contains all elements from {@code values} 
	 *    uniquely mapped to an index, or returns {@code null} if that is not possible.
	 * <p>
	 * Elements can not be {@code null} and the set of elements cannot be empty. 
	 * <p>Initially, an attempt is made to fit all elements in an array that has a size that is the minimum
	 * power of two that is greater than or equal to the number of elements in {@code values}. Each
	 * hash function in {@code hashFunctions} will be tried in order, and the first that succeeds will 
	 * be used in the returned universe. If the attempt fails with all hash functions, the 
	 * array size is doubled. This doubling of size is called a power shift and can be done up to a 
	 * maximum of {@code powerShifts} times. The maximum value of {@code powerShifts} is limited internally 
	 * to 4.
	 * <p>
	 * If it is not possible to map all elements uniquely within the number of power shifts and combined
	 * with all provided hash functions, this method returns {@code null}.</p>
	 * 
	 * @param values universe values
	 * @param powerShifts maximum allowed number of size doubles
	 * @param hashFunctions list of hash functions to try for each size
	 * 
	 * @return a Hash-based {@link IndexedSubset} or <code>null</code> if that is not possible. 
	 */
	public static <T> IndexedSubset<T> createFrom(Set<T> values, int powerShifts, List<HashFunction> hashFunctions) {
		checkNotNull(values, "values");
		checkNotNull(hashFunctions, "hashFunctions");
		if (hashFunctions.isEmpty()) {
			return null;
		}
		
		int elementCount = values.size();
		
		checkArgument(elementCount > 0, "Indexed universe needs at least one element");
		
		if (elementCount == 1) {
			return new SingleElementIndexedUniverse<T>(values.toArray()[0]);
		}
		
		int size = equalOrGreaterPowerOfTwo(elementCount);
		int maxSize = getMaxSize(size, powerShifts);
		
		do {
			Object[] target = new Object[size];
			for (int i = 0; i < hashFunctions.size(); i++) {
				HashFunction hashFunction = hashFunctions.get(i);
				if (mappedUniquelyInTarget(values, target, hashFunction)) {
					return new HashedArrayIndexedSubset<T>(target, hashFunction.indexFunction(size), elementCount);
				}
				nullArray(target);
			}
			size <<= 1;
		}
		while (size <= maxSize);
		
		return null;
	}
	
	public static <T> IndexedSubset<T> createFrom(Set<T> values, int powerShifts) {
		return createFrom(values, powerShifts, HashFunction.DEFAULT_SAFE_HASHES);
	}
	
	public static <T> IndexedSubset<T> createFrom(Set<T> values) {
		return createFrom(values, 4, HashFunction.DEFAULT_SAFE_HASHES);
	}

	private static int equalOrGreaterPowerOfTwo(int elementCount) {
		if (elementCount > HashIndexedFunction.MAX_SIZE) {
			throw new IllegalArgumentException("Size (" + elementCount + ") exceeds the maximum number of elements in a " + HashedArrayIndexedSubset.class.getSimpleName() + " (" + HashIndexedFunction.MAX_SIZE + ")");
		}
		int size = 1;
		while (size < elementCount) {
			size <<= 1;
		}
		return size;
	}
	
	private static int getMaxSize(int size, int maxShifts) {
		int shifts = maxShifts < 0 ? 0 : maxShifts > 4 ? 4 : maxShifts;
		long sz = size;
		long max = HashIndexedFunction.MAX_SIZE;
		
		for (int i = 0; i < shifts && sz < max; i++) {
			sz *= 2;
		}
		
		return (int)Math.min(sz, max); 
	}
	
	private static <T> boolean mappedUniquelyInTarget(Set<T> values, Object[] target, HashFunction hashFunction) {
		checkNotNull(hashFunction, "hashFunction");
		int mask = target.length - 1;
		for (T element : values) {
			int hash = mask & hashFunction.hashCode(IndexedUniverseHelper.checkElementNotNull(element));
			if (target[hash] != null) {
				return false;
			}
			target[hash] = element;
		}
		
		return true;
	}
	
	private static void nullArray(Object[] target) {
		for (int i = 0; i < target.length; i++) {
			target[i] = null;
		}
	}
}
