package org.emmef.cheapsets.universes;

import java.util.Set;

import org.emmef.cheapsets.IndexedUniverse;

public enum MappedUniverseCreator implements UniverseCreator {
	INSTANCE;

	@Override
	public <E> IndexedUniverse<E> from(Set<E> universe) {
		return new MapIndexedUniverse<>(universe);
	}

}
