package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import org.emmef.cheapsets.IndexedUniverse;
import org.emmef.cheapsets.UniverseBasedSet;

import com.google.common.collect.ImmutableList;

public class IndexedUniverses {
	private static final List<UniverseCreator> DEFAULT_CREATOR_STRATEGY = ImmutableList.of(NaiveUniverseCreator.DEFAULT, HashedUniverseCreator.DEFAULT);

	public static <E> IndexedUniverse<E> createAlways(Set<E> universe, UniverseCreator... creators) {
		IndexedUniverse<E> easy = getEasyUniverseFromNullChecked(universe);
		
		if (easy != null) {
			return easy;
		}
		
		for (UniverseCreator creator : creators) {
			IndexedUniverse<E> indexedUniverse = creator.from(universe);
			if (indexedUniverse != null) {
				return indexedUniverse;
			}
		}
		
		return new MapIndexedUniverse<E>(universe);
	}
	
	public static <E> IndexedUniverse<E> createAlways(Set<E> universe, List<UniverseCreator> creators) {
		IndexedUniverse<E> easy = getEasyUniverseFromNullChecked(universe);
		
		if (easy != null) {
			return easy;
		}
		
		for (UniverseCreator creator : creators) {
			IndexedUniverse<E> indexedUniverse = creator.from(universe);
			if (indexedUniverse != null) {
				return indexedUniverse;
			}
		}
		
		return new MapIndexedUniverse<E>(universe);
	}
	
	public static <E> IndexedUniverse<E> create(Set<E> universe) {
		return createAlways(universe, DEFAULT_CREATOR_STRATEGY);
	}
	
	
	public static <E extends Comparable<E>> IndexedUniverse<E> createSorted(Set<E> universe) {
		IndexedUniverse<E> easy = getEasyUniverseFromNullChecked(universe);
		
		if (easy != null) {
			return easy;
		}
		
		return new SortedIndexedUniverse<>(universe);
	}
	
	private static <V> IndexedUniverse<V> getEasyUniverseFromNullChecked(Set<V> universe) {
		checkNotNull(universe, "universe");
		
		if (universe instanceof UniverseBasedSet) {
			return ((UniverseBasedSet<V>)universe).subSet();
		}
		
		if (universe instanceof UniverseBasedSet) {
			IndexedUniverse<V> castInstance = ((UniverseBasedSet<V>) universe).subSet();
			return castInstance;
		}
		
		if (universe.size() == 1) {
			return new SingleElementIndexedUniverse<>(universe.iterator().next());
		}

		return null;
	}
	
	public static String toString(IndexedUniverse<?> universe) {
		return universe != null ? appendTo(null, universe).toString() : "null";
	}

	public static StringBuilder appendTo(StringBuilder output, IndexedUniverse<?> universe) {
		checkNotNull(universe, "universe");
		
		StringBuilder builder = output != null ? output : new StringBuilder();
		
		builder.append('[');
		appendElementsTo(builder, universe);
		builder.append(']');
		
		return builder;
	}	

	public static StringBuilder appendElementsTo(StringBuilder output, IndexedUniverse<?> universe) {
		checkNotNull(universe, "universe");
		
		StringBuilder builder = output != null ? output : new StringBuilder();
		
		int bound = universe.indexBoundary();
		boolean first = true;
		
		for (int i = 0; i < bound; i++) {
			Object element = universe.elementAt(i);
			if (element != null) {
				if (first) {
					first = false;
				}
				else {
					builder.append(", ");
				}
				builder.append(element);
			}
		}
		
		return builder;
	}

}
