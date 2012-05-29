package org.emmef.cheapsets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.emmef.cheapsets.hash.HashFunction;
import org.emmef.cheapsets.universes.HashedArrayIndexedSubset;
import org.emmef.cheapsets.universes.MapBasedIndexedSubset;
import org.emmef.cheapsets.universes.NaiveArrayUniverse;

import com.google.common.collect.ImmutableList;

public class IndexedSubSets {
	
	public static final int DEFAULT_NAIVE_THRESHOLD = 10;
	
	public static Stats createStats() {
		return new Stats();
	}
	
	@SuppressWarnings("unchecked")
	public static <E> IndexedSubset<E> createSubset(Set<E> set, List<HashFunction> hashFunctions, int naiveThreshold) {
		if (set instanceof IndexedSubset) {
			Stats.addIdempotentBased();
			return (IndexedSubset<E>)set;
		}
		if (set instanceof DefaultSubsetLimitedSet) {
			Stats.addIdempotentBased();
			return ((DefaultSubsetLimitedSet<E,?>) set).getUniverse();
		}
		if (hashFunctions != null && !hashFunctions.isEmpty()) {
			IndexedSubset<E> hashedSet = HashedArrayIndexedSubset.createFrom(set, 4, hashFunctions);
			if (hashedSet != null) {
				Stats.addHashBased();
				return hashedSet;
			}
		}
		
		if (set.size() < naiveThreshold) {
			Stats.addNaive();
			return new NaiveArrayUniverse<E>(set);
		}

		Stats.addMapBased();
		return new MapBasedIndexedSubset<E>(set);
	}
	public static <E> IndexedSubset<E> createSubset(Set<E> universe) {
		return createSubset(universe, HashFunction.DEFAULT_SAFE_HASHES, DEFAULT_NAIVE_THRESHOLD);
	}
	
	public static <E> IndexedSubset<E> createSubsetNoHashes(Set<E> universe) {
		return createSubset(universe, ImmutableList.<HashFunction>of(), DEFAULT_NAIVE_THRESHOLD);
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
