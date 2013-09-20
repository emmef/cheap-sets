package org.emmef.cheapsets.universes;

import java.util.Set;

import org.emmef.cheapsets.IndexedUniverse;

public interface UniverseCreator {
	/**
	 * Creates an indexed universe from the provided set of elements.
	 * <p>
	 * If the set of elements cannot result in an indexed universe of the specified type, 
	 * the method returns {@code null}. For example: if all elements have the same hashCode
	 * (and no identity hash function is used), a hash-based universe won't work. 
	 * 
	 * @param universe set of elements
	 * 
	 * @return an {@link IndexedUniverse}, or {@code null} if the set of elements cannot result in an indexed universe of the specified type.
	 */
	<E> IndexedUniverse<E> from(Set<E> universe);
}
