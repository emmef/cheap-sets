package org.emmef.cheapsets;

import java.util.Set;

public interface IndexedUniverseSet<E> extends Set<E>, Cloneable {
	public IndexedUniverseSet<E> clone();
	public IndexedUniverseSet<E> cloneEmpty();
}
