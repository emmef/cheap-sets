package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import org.emmef.cheapsets.IndexedUniverse;
import org.emmef.cheapsets.hash.HashFunction;
import org.emmef.cheapsets.util.PowerOfTwo;

import com.google.common.collect.ImmutableList;

public class HashedUniverseCreator implements UniverseCreator {
	private static final int MAXIMUM_POWERSHIFTS = 4;

	private final int powerShifts;
	
	public static final UniverseCreator DEFAULT = new HashedUniverseCreator(4, HashFunction.DEFAULT_FUNCTIONS);
	public static final UniverseCreator DEFAULT_IDENTITY = new HashedUniverseCreator(4, HashFunction.DEFAULT_IDENTITY_FUNCTIONS);
	
	public HashedUniverseCreator(int powerShifts, List<HashFunction> hashFunctions) {
		checkArgument(powerShifts > 0, "Number of powershifts must be positive");
		
		if (powerShifts > MAXIMUM_POWERSHIFTS) {
			throw new IllegalArgumentException("Number of powershifts must be smaller than "+ MAXIMUM_POWERSHIFTS);
		}
		
		this.powerShifts = powerShifts;
		this.hashFunctions = ImmutableList.copyOf(checkNotNull(hashFunctions, "hashFunctions"));
	}

	private final List<HashFunction> hashFunctions;


	/**
	 * Creates a hash-based {@link IndexedUniverse} that contains all elements
	 * from {@code values} uniquely mapped to an index, or returns {@code null}
	 * if that is not possible.
	 * <p>
	 * Elements can not be {@code null} and the set of elements cannot be empty.
	 * <p>
	 * Initially, an attempt is made to fit all elements in an array that has a
	 * size that is the minimum power of two that is greater than or equal to
	 * the number of elements in {@code values}. Each hash function in
	 * {@code hashFunctions} will be tried in order, and the first that succeeds
	 * will be used in the returned universe. If the attempt fails with all hash
	 * functions, the array size is doubled. This doubling of size is called a
	 * power shift and can be done up to a maximum of {@code powerShifts} times.
	 * The maximum value of {@code powerShifts} is limited internally to 4.
	 * <p>
	 * If it is not possible to map all elements uniquely within the number of
	 * power shifts and combined with all provided hash functions, this method
	 * returns {@code null}.
	 * </p>
	 * 
	 * @param values universe values
	 * @param powerShifts maximum allowed number of size doubles
	 * @param hashFunctions list of hash functions to try for each size
	 * 
	 * @return a Hash-based {@link IndexedUniverse} or <code>null</code> if that
	 *         is not possible.
	 */
	@Override
	public <E> IndexedUniverse<E> from(Set<E> universe) {
		return HashedUniverseCreator.createFrom(universe, powerShifts, hashFunctions);
	}
	
	public static <T> IndexedUniverse<T> createFrom(Set<T> values, int powerShifts, List<HashFunction> hashFunctions) {
		checkNotNull(values, "values");
		checkNotNull(hashFunctions, "hashFunctions");
		checkArgument(powerShifts > 0, "Number of powershifts must be positive");
		
		if (hashFunctions.isEmpty()) {
			return null;
		}
		
		int elementCount = values.size();
		
		checkArgument(elementCount > 0, "Indexed universe needs at least one element");
		
		if (elementCount == 1) {
			return new SingleElementIndexedUniverse<T>((values.toArray()[0]));
		}
		
		int size = PowerOfTwo.sameOrBigger(elementCount);
		int maxSize = getMaxSize(size, powerShifts);
		
		do {
			Object[] target = new Object[size];
			for (int i = 0; i < hashFunctions.size(); i++) {
				HashFunction hashFunction = hashFunctions.get(i);
				if (mappedUniquelyInTarget(values, target, hashFunction)) {
					return new HashIndexedUniverse<T>(elementCount, target, size, hashFunction);
				}
				nullArray(target);
			}
			size <<= 1;
		}
		while (size <= maxSize);
		
		return null;
	}

	static void nullArray(Object[] target) {
		for (int i = 0; i < target.length; i++) {
			target[i] = null;
		}
	}

	static <T> boolean mappedUniquelyInTarget(Set<T> values, Object[] target, HashFunction hashFunction) {
		checkNotNull(hashFunction, "hashFunction");
		int mask = target.length - 1;
		for (T element : values) {
			int hash = mask & hashFunction.hashCode(checkNotNull(element, IndexedUniverse.class.getSimpleName() + " cannot contain null elements"));
			if (target[hash] != null) {
				return false;
			}
			target[hash] = element;
		}
		
		return true;
	}

	static int getMaxSize(int size, int maxShifts) {
		int shifts = Math.min(maxShifts, 4);
		long max = PowerOfTwo.MAX >> shifts;
			
		if (size > max) {
			return PowerOfTwo.MAX;
		}
		
		return size << shifts;
	}
}
