package org.emmef.cheapsets;

import java.util.List;
import java.util.Set;

import org.emmef.cheapsets.hash.HashFunction;
import org.emmef.cheapsets.universes.BimapIndexedUniverse;
import org.emmef.cheapsets.universes.HashedArrayIndexedUniverse;
import org.emmef.cheapsets.universes.NaiveArrayUniverse;

public class IndexedUniverseSets {
	
	@SuppressWarnings("unchecked")
	public static <E> IndexedUniverse<E> createUniverse(Set<E> universe, boolean tryHashes) {
		if (universe instanceof IndexedUniverse) {
			return (IndexedUniverse<E>)universe;
		}
		if (universe instanceof AbstractIndexedUniverseSet) {
			return ((AbstractIndexedUniverseSet<E,?>) universe).getUniverse();
		}
		if (tryHashes) {
			IndexedUniverse<E> set = HashedArrayIndexedUniverse.createFrom(universe);
			if (set != null) {
				return set;
			}
		}
		
		if (universe.size() < 4) {
			return new NaiveArrayUniverse<E>(universe);
		}
		
		return new BimapIndexedUniverse<E>(universe);
	}
	
	@SuppressWarnings("unchecked")
	public static <E> IndexedUniverse<E> createUniverse(Set<E> universe, List<HashFunction> hashFunctions) {
		if (universe instanceof IndexedUniverse) {
			return (IndexedUniverse<E>)universe;
		}
		if (universe instanceof AbstractIndexedUniverseSet) {
			return ((AbstractIndexedUniverseSet<E,?>) universe).getUniverse();
		}
		if (hashFunctions != null) {
			IndexedUniverse<E> set = HashedArrayIndexedUniverse.createFrom(universe, 4, hashFunctions);
			if (set != null) {
				return set;
			}
		}
		
		if (universe.size() < 4) {
			return new NaiveArrayUniverse<E>(universe);
		}
		
		return new BimapIndexedUniverse<E>(universe);
	}
	
	public static <E> IndexedUniverseSet<E> createEmpty(IndexedUniverse<E> universe) {
		int indexSize = universe.indexSize();
		if (indexSize > 64) {
			return new AbstractIndexedUniverseSet<E, JumboIndexSet>(universe, new JumboIndexSet(indexSize));
		}
		else {
			return new AbstractIndexedUniverseSet<E, SmallIndexSet>(universe, new SmallIndexSet(indexSize));
		}
	}
	
	public static <E> IndexedUniverseSet<E> createEmpty(Set<E> universe, boolean tryHashAnyway) {
		if (universe instanceof IndexedUniverse) {
			return createEmpty((IndexedUniverse<E>)universe);
		}
		
		if (tryHashAnyway) {
			IndexedUniverse<E> set = HashedArrayIndexedUniverse.createFrom(universe);
			if (set != null) {
				return createEmpty(set);
			}
		}
		
		if (universe.size() < 4) {
			return createEmpty(new NaiveArrayUniverse<E>(universe));
		}
		
		return createEmpty(new BimapIndexedUniverse<E>(universe));
	}
	
	public static <E> IndexedUniverseSet<E> createEmpty(Set<E> universe) {
		return createEmpty(universe, true);
	}
	
	public static <E> IndexedUniverseSet<E> copyOf(Set<E> source, boolean tryHashAnyway) {
		if (source instanceof AbstractIndexedUniverseSet) {
			return ((IndexedUniverseSet<E>) source).clone();
		}
		IndexedUniverseSet<E> created = createEmpty(source, tryHashAnyway);
		
		created.addAll(source);
		
		return created;
	}
	
	public static <E> IndexedUniverseSet<E> copyOf(Set<E> source) {
		return copyOf(source, true);
	}
}
