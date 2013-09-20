package org.emmef.cheapsets.universes;

import java.util.Set;

import org.emmef.cheapsets.IndexedUniverse;

public interface UniverseCreator {
	<E> IndexedUniverse<E> from(Set<E> universe);
}
