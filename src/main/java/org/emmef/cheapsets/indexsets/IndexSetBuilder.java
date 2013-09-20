package org.emmef.cheapsets.indexsets;

import org.emmef.cheapsets.IndexSet;
import org.emmef.cheapsets.IndexedUniverse;

public class IndexSetBuilder {
	public static IndexSet emptyFor(IndexedUniverse<?> universe) {
		int bounday = universe.indexBoundary();
		if (bounday <= 32) {
			return new MiniIndexSet();
		}
		if (bounday  <= 64) {
			return new SmallIndexSet();
		}
		
		return new JumboIndexSet(bounday);
	}
	
	public static IndexSet clone(IndexSet set) {
		return set.clone();
	}
	
	public static IndexSet cloneEmpty(IndexSet set) {
		return set.cloneEmpty();
	}
}