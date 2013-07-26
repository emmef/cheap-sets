package org.emmef.cheapsets;

import static com.google.common.base.Preconditions.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.emmef.cheapsets.hash.HashFunction;
import org.emmef.cheapsets.universes.HashedArrayIndexedSubset;
import org.emmef.cheapsets.universes.MapBasedIndexedSubset;
import org.emmef.cheapsets.universes.NaiveArrayUniverse;
import org.emmef.cheapsets.universes.OrderedIndexedSubSet;

import com.google.common.collect.ImmutableList;

public class IndexedSubSets {
	public static final int DEFAULT_NAIVE_THRESHOLD = 10;
	
	public static Stats createStats() {
		return new Stats();
	}

	/**
	 * Creates an indexed subset based on the given universe.
	 * <p>
	 * A best-effort will be used to create a set that isn't too big or to slow:
	 * <ul>
	 * <li>If the set is smaller than {@link #DEFAULT_NAIVE_THRESHOLD}, a  
	 * 
	 * @param universe
	 * @return
	 */
	public static <E> IndexedSubset<E> create(Set<E> universe) {
		IndexedSubset<E> idempotent = getIdempotentFromNotNull(universe);
		
		if (idempotent != null) {
			return idempotent;
		}
		
		return createSubSet(universe, HashFunction.DEFAULT_FUNCTIONS, DEFAULT_NAIVE_THRESHOLD);
	}
	
	public static <E> IndexedSubset<E> create(Set<E> universe, List<HashFunction> hashFunctions, int naiveThreshold) {
		IndexedSubset<E> idempotent = getIdempotentFromNotNull(universe);
		
		if (idempotent != null) {
			return idempotent;
		}
		checkHashFunctions(hashFunctions);
		
		return createSubSet(universe, hashFunctions, naiveThreshold);
	}
	
	public static <E> IndexedSubset<E> createUseNoHashes(Set<E> universe) {
		IndexedSubset<E> idempotent = getIdempotentFromNotNull(universe);
		
		if (idempotent != null) {
			return idempotent;
		}
		return createSubSet(universe, ImmutableList.<HashFunction>of(), DEFAULT_NAIVE_THRESHOLD);
	}
	
	public static <E extends Comparable<E>> IndexedSubset<E> createSorted(Set<E> universe) {
		IndexedSubset<E> idempotent = getIdempotentFromNotNull(universe);
		
		if (idempotent != null) {
			return idempotent;
		}
		
		return new OrderedIndexedSubSet<>(universe);
	}

	public static <E> IndexedSubset<E> createHashed(Set<E> universe) {
		IndexedSubset<E> idempotent = getIdempotentFromNotNull(universe);
		
		if (idempotent != null) {
			return idempotent;
		}
		
		return createHashedSubSet(universe, HashFunction.DEFAULT_FUNCTIONS);
	}

	public static <E> IndexedSubset<E> createHashed(Set<E> universe, List<HashFunction> hashFunctions) {
		IndexedSubset<E> idempotent = getIdempotentFromNotNull(universe);
		
		if (idempotent != null) {
			return idempotent;
		}
		checkHashFunctions(hashFunctions);
		
		return createHashedSubSet(universe, hashFunctions);
	}
	
	private static <E> IndexedSubset<E> createHashedSubSet(Set<E> universe, List<HashFunction> hashFunctions) {
		IndexedSubset<E> hashedSet = HashedArrayIndexedSubset.createFrom(universe, 4, hashFunctions);
		
		if (hashedSet != null) {
			Stats.addHashBased();
			return hashedSet;
		}
		
		throw new IllegalArgumentException("Cannot create a hash-based indexed subset without collisions"); 
	}
	
	private static <E> IndexedSubset<E> createSubSet(Set<E> set, List<HashFunction> hashFunctions, int naiveThreshold) {
		if (set.size() < naiveThreshold) {
			Stats.addNaive();
			return new NaiveArrayUniverse<E>(set);
		}
		
		if (hashFunctions != null && !hashFunctions.isEmpty()) {
			IndexedSubset<E> hashedSet = HashedArrayIndexedSubset.createFrom(set, 4, hashFunctions);
			if (hashedSet != null) {
				Stats.addHashBased();
				return hashedSet;
			}
		}
		
		Stats.addMapBased();
		return new MapBasedIndexedSubset<E>(set);
	}

	private static void checkHashFunctions(List<HashFunction> hashFunctions) {
		checkNotNull(hashFunctions, "hashFunctions");
		
		if (hashFunctions.isEmpty()) {
			throw new IllegalArgumentException("Must provide a non-empty list of hash functions"); 
		}
	}
	
	private static <V> IndexedSubset<V> getIdempotentFromNotNull(Set<V> universe) {
		checkNotNull(universe, "universe");
		
		if (universe instanceof IndexedSubset) {
			Stats.addIdempotentBased();
			return (IndexedSubset<V>)universe;
		}
		
		if (universe instanceof DefaultSubsetLimitedSet) {
			Stats.addIdempotentBased();
			@SuppressWarnings("unchecked")
			IndexedSubset<V> castInstance = ((DefaultSubsetLimitedSet<V,?>) universe).subSet();
			return castInstance;
		}

		return null;
	}

	public static class Stats {
		private static final boolean useStats = Boolean.valueOf(System.getProperty(IndexedSubSets.class.getName().toLowerCase() + ".enable-stats"));  
		private static final AtomicInteger hashBasedFrequency = new AtomicInteger();
		private static final AtomicInteger naiveFrequency = new AtomicInteger();
		private static final AtomicInteger mapBasedFrequency = new AtomicInteger();
		private static final AtomicInteger idempotentFrequency = new AtomicInteger();
		
		static void addIdempotentBased() {
			if (useStats) {
				idempotentFrequency.incrementAndGet();
			}
		}

		static void addMapBased() {
			if (useStats) {
				mapBasedFrequency.incrementAndGet();
			}
		}

		static void addNaive() {
			if (useStats) {
				naiveFrequency.incrementAndGet();
			}
		}

		static void addHashBased() {
			if (useStats) {
				hashBasedFrequency.incrementAndGet();
			}
		}

		static final Stats create() {
			return new Stats();
		}
		
		Stats() {
		}
		
		private final int idempotent = idempotentFrequency.get();
		private final int naive = naiveFrequency.get();
		private final int hashed = hashBasedFrequency.get();
		private final int mapped = mapBasedFrequency.get();
		private final boolean disabled = !useStats;

		public int getIdempotent() {
			return idempotent;
		}

		public int getNaive() {
			return naive;
		}

		public int getHashed() {
			return hashed;
		}

		public int getMapped() {
			return mapped;
		}
		
		@Override
		public String toString() {
			if (disabled) {
				return "IndexSubSets.Stats(DISABLED)";
			}
			return "IndexSubSets.Stats(idempotent=" + idempotent + "; naive=" + naive + "; hashed=" + hashed + "; mapped=" + mapped + ")";
		}
		
		public String getPercentages() {
			if (disabled) {
				return "IndexSubSets.Stats(DISABLED)";
			}
			int total = idempotent + naive + hashed + mapped;
			if (total == 0) {
				return "IndexSubSets.Stats(NONE)";
			}
			return "IndexSubSets.Stats(idempotent=" + (100L * idempotent) / total + "%; naive=" + (100L * naive / total)+ "%; hashed=" + (100L * hashed / total) + "%; mapped=" + (100L * mapped / total) + "%)";
		}
	}
}
