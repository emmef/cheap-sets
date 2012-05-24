package org.emmef.cheapsets;

import java.util.List;
import java.util.Set;

import org.emmef.cheapsets.hash.HashFunction;
import org.emmef.cheapsets.universes.HashedArrayIndexedSubset;
import org.emmef.cheapsets.universes.MapBasedIndexedSubset;
import org.emmef.cheapsets.universes.NaiveArrayUniverse;

import com.google.common.collect.ImmutableList;

public class IndexedSubSets {
	
	@SuppressWarnings("unchecked")
	public static <E> IndexedSubset<E> createSubset(Set<E> universe, List<HashFunction> hashFunctions) {
		if (universe instanceof IndexedSubset) {
			return (IndexedSubset<E>)universe;
		}
		if (universe instanceof DefaultSubsetLimitedSet) {
			return ((DefaultSubsetLimitedSet<E,?>) universe).getUniverse();
		}
		if (hashFunctions != null) {
			IndexedSubset<E> set = HashedArrayIndexedSubset.createFrom(universe, 4, hashFunctions);
			if (set != null) {
				return set;
			}
		}
		
		if (universe.size() < 4) {
			return new NaiveArrayUniverse<E>(universe);
		}
		
		return new MapBasedIndexedSubset<E>(universe);
	}

	public static <E> IndexedSubset<E> createSubset(Set<E> universe, boolean tryHashBasedSubsets) {
		if (tryHashBasedSubsets) {
			return createSubset(universe, HashFunction.DEFAULT_SAFE_HASHES);
		}
		else {
			return createSubset(universe, ImmutableList.<HashFunction>of());
		}
	}
	
	public static <E> IndexedSubset<E> createSubset(Set<E> universe) {
		return createSubset(universe, HashFunction.DEFAULT_SAFE_HASHES);
	}
}
