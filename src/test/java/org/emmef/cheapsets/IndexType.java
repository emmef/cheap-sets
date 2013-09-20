package org.emmef.cheapsets;

import java.util.Set;

public enum IndexType {
	AUTO, 
	HASH,
	SORTED,
	NAIVE;
	
	public IndexedUniverse<String> create(Set<String> universe) {
		switch (this) {
		case HASH:
			return IndexedSubSets.createHashed(universe);
		case SORTED:
			return IndexedSubSets.createSorted(universe);
		case NAIVE:
			return IndexedSubSets.createNaive(universe);
		case AUTO:
		default:
			return IndexedSubSets.create(universe);
		}
	}
}
