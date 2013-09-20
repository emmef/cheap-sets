package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.emmef.cheapsets.IndexedUniverse;

public class NaiveUniverseCreator implements UniverseCreator {
	public static final int DEFAULT_NAIVE_THRESHOLD = 10;
	public static final UniverseCreator DEFAULT = new NaiveUniverseCreator(DEFAULT_NAIVE_THRESHOLD);
	public static final UniverseCreator ALL = new NaiveUniverseCreator();
	
	private int maxSize;

	private NaiveUniverseCreator() {
		maxSize = -1;
	}
	
	public NaiveUniverseCreator(int maxNaiveSize) {
		if (maxNaiveSize <= 0) {
			throw new IllegalArgumentException("Max size should positive");
		}
		this.maxSize = maxNaiveSize;
	}

	@Override
	public <E> IndexedUniverse<E> from(Set<E> universe) {
		checkNotNull(universe, "universe");
		
		if (maxSize == -1 || maxSize >= universe.size()) {
			return new NaiveArrayUniverse<>(universe);
		}
		
		return null;
	}
}
