package org.emmef.cheapsets;

import java.util.Set;

import org.emmef.cheapsets.universes.HashedUniverseCreator;
import org.emmef.cheapsets.universes.MapIndexedUniverse;
import org.emmef.cheapsets.universes.NaiveArrayUniverse;
import org.emmef.cheapsets.universes.SortedIndexedUniverse;
import org.emmef.cheapsets.universes.SingleElementIndexedUniverse;

public enum IndexType {
	SINGLE, 
	NAIVE,
	SORTED,
	HASH,
	MAPPED,
	;
	
	public IndexedUniverse<String> create(Set<String> universe) {
		switch (this) {
		case HASH:
			return HashedUniverseCreator.DEFAULT.from(universe);
		case SINGLE:
			return universe.size() == 1 ? new SingleElementIndexedUniverse<String>(universe) : new NaiveArrayUniverse<>(universe);
		case SORTED:
			return new SortedIndexedUniverse<String>(universe);
		case NAIVE:
			return new NaiveArrayUniverse<>(universe);
		case MAPPED:
			return new MapIndexedUniverse<String>(universe);
		}
		throw new IllegalStateException("");
	}
}
