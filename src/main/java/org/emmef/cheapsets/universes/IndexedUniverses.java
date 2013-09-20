package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkNotNull;

import org.emmef.cheapsets.IndexedUniverse;

public class IndexedUniverses {
	
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
