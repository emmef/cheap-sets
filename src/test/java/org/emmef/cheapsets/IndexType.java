package org.emmef.cheapsets;

import java.util.Set;

import org.emmef.cheapsets.universes.IndexedUniverses;

public enum IndexType {
	AUTO, 
	HASH,
	SORTED,
	NAIVE;
	
	public IndexedUniverse<String> create(Set<String> universe) {
		switch (this) {
		case HASH:
			return IndexedUniverses.createHashed(universe);
		case SORTED:
			return IndexedUniverses.createSorted(universe);
		case NAIVE:
			return IndexedUniverses.createNaive(universe);
		case AUTO:
		default:
			return IndexedUniverses.create(universe);
		}
	}
}
