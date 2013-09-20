package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.emmef.cheapsets.IndexedUniverse;

public enum SingleElementUniverseCreator implements UniverseCreator {
	INSTANCE; 
	
	@Override
	public <E> IndexedUniverse<E> from(Set<E> universe) {
		checkNotNull(universe, "universe");
		
		if (universe.size() == 1) {
			return new SingleElementIndexedUniverse<>(universe.iterator().next());
		}
		
		return null;
	}

}
