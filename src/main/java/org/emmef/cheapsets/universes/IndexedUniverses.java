package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.emmef.cheapsets.IndexedUniverse;
import org.emmef.cheapsets.UniverseBasedSet;
import org.emmef.cheapsets.hash.HashFunction;

import com.google.common.collect.ImmutableList;

public class IndexedUniverses {
	public static Stats createStats() {
		return new Stats();
	}

	/**
	 * Creates an indexed subset based on the given universe.
	 * <p>
	 * A best-effort will be used to create a set that isn't too big or to slow:
	 * <ul>
	 * <li>If the set is smaller than {@link NaiveUniverseCreator#DEFAULT_NAIVE_THRESHOLD}, a  
	 * 
	 * @param universe
	 * @return
	 */
	public static <E> IndexedUniverse<E> create(Set<E> universe) {
		IndexedUniverse<E> idempotent = getIdempotentFromNotNull(universe);
		
		if (idempotent != null) {
			return idempotent;
		}
		
		return createSubSet(universe, HashFunction.DEFAULT_FUNCTIONS, NaiveUniverseCreator.DEFAULT_NAIVE_THRESHOLD);
	}
	
	public static <E> IndexedUniverse<E> create(Set<E> universe, List<HashFunction> hashFunctions, int naiveThreshold) {
		IndexedUniverse<E> idempotent = getIdempotentFromNotNull(universe);
		
		if (idempotent != null) {
			return idempotent;
		}
		checkHashFunctions(hashFunctions);
		
		return createSubSet(universe, hashFunctions, naiveThreshold);
	}
	
	public static <E> IndexedUniverse<E> createUseNoHashes(Set<E> universe) {
		IndexedUniverse<E> idempotent = getIdempotentFromNotNull(universe);
		
		if (idempotent != null) {
			return idempotent;
		}
		return createSubSet(universe, ImmutableList.<HashFunction>of(), NaiveUniverseCreator.DEFAULT_NAIVE_THRESHOLD);
	}
	
	public static <E extends Comparable<E>> IndexedUniverse<E> createSorted(Set<E> universe) {
		IndexedUniverse<E> idempotent = getIdempotentFromNotNull(universe);
		
		if (idempotent != null) {
			return idempotent;
		}
		
		return new OrderedIndexedUniverse<>(universe);
	}
	

	public static IndexedUniverse<String> createNaive(Set<String> universe) {
		return new NaiveArrayUniverse<>(universe);
	}
	

	public static <E> IndexedUniverse<E> createHashed(Set<E> universe) {
		IndexedUniverse<E> idempotent = getIdempotentFromNotNull(universe);
		
		if (idempotent != null) {
			return idempotent;
		}
		
		return createHashedSubSet(universe, HashFunction.DEFAULT_FUNCTIONS);
	}

	public static <E> IndexedUniverse<E> createHashed(Set<E> universe, List<HashFunction> hashFunctions) {
		IndexedUniverse<E> idempotent = getIdempotentFromNotNull(universe);
		
		if (idempotent != null) {
			return idempotent;
		}
		checkHashFunctions(hashFunctions);
		
		return createHashedSubSet(universe, hashFunctions);
	}
	
	private static <E> IndexedUniverse<E> createHashedSubSet(Set<E> universe, List<HashFunction> hashFunctions) {
		IndexedUniverse<E> hashedUniverse = HashedUniverseCreator.createFrom(universe, 4, hashFunctions);
		
		if (hashedUniverse != null) {
			Stats.addHashBased();
			return hashedUniverse;
		}
		
		throw new IllegalArgumentException("Cannot create a hash-based indexed subset without collisions"); 
	}
	
	private static <E> IndexedUniverse<E> createSubSet(Set<E> set, List<HashFunction> hashFunctions, int naiveThreshold) {
		if (set.size() < naiveThreshold) {
			Stats.addNaive();
			return new NaiveArrayUniverse<E>(set);
		}
		
		if (hashFunctions != null && !hashFunctions.isEmpty()) {
			IndexedUniverse<E> hashedUniverse = HashedUniverseCreator.createFrom(set, 4, hashFunctions);
			if (hashedUniverse != null) {
				Stats.addHashBased();
				return hashedUniverse;
			}
		}
		
		Stats.addMapBased();
		return new MapIndexedUniverse<E>(set);
	}

	private static void checkHashFunctions(List<HashFunction> hashFunctions) {
		checkNotNull(hashFunctions, "hashFunctions");
		
		if (hashFunctions.isEmpty()) {
			throw new IllegalArgumentException("Must provide a non-empty list of hash functions"); 
		}
	}
	
	private static <V> IndexedUniverse<V> getIdempotentFromNotNull(Set<V> universe) {
		checkNotNull(universe, "universe");
		
		if (universe instanceof UniverseBasedSet) {
			Stats.addIdempotentBased();
			return ((UniverseBasedSet<V>)universe).subSet();
		}
		
		if (universe instanceof UniverseBasedSet) {
			Stats.addIdempotentBased();
			IndexedUniverse<V> castInstance = ((UniverseBasedSet<V>) universe).subSet();
			return castInstance;
		}

		return null;
	}
	
	public static String toString(IndexedUniverse<?> universe) {
		return universe != null ? appendTo(null, universe).toString() : "null";
	}

	public static StringBuilder appendTo(StringBuilder output, IndexedUniverse<?> universe) {
		checkNotNull(universe, "universe");
		
		StringBuilder builder = output != null ? output : new StringBuilder();
		
		builder.append('[');
		appendElementsTo(builder, universe);
		builder.append(']');
		
		return builder;
	}	

	public static StringBuilder appendElementsTo(StringBuilder output, IndexedUniverse<?> universe) {
		checkNotNull(universe, "universe");
		
		StringBuilder builder = output != null ? output : new StringBuilder();
		
		int bound = universe.indexBoundary();
		boolean first = true;
		
		for (int i = 0; i < bound; i++) {
			Object element = universe.elementAt(i);
			if (element != null) {
				if (first) {
					first = false;
				}
				else {
					builder.append(", ");
				}
				builder.append(element);
			}
		}
		
		return builder;
	}

	public static class Stats {
		private static final boolean useStats = Boolean.valueOf(System.getProperty(IndexedUniverses.class.getName().toLowerCase() + ".enable-stats"));  
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
