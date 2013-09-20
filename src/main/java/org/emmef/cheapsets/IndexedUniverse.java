package org.emmef.cheapsets;

/**
 * Represents a fixed universe of elements that are each associated with a unique index.
 * <p>
 * The index of an element can be larger than the number of elements in the 
 * universe, as indices don't have to be consecutive. For example, universes 
 * that are based on hashing generally have a fill-grade and leave gaps 
 * between the indices.
 * 
 * @param <T> element type
 * 
 * @documented 2013-08-09
 */
public interface IndexedUniverse<T> {
	/**
	 * Returns the index that is related to {@code element}.
	 * <p>
	 * If the universe contains {@code element}, its index is returned. The index lies between 0 (inclusive) and {@link #indexBoundary()} (exclusive)
	 * <p>
	 * If the universe does not contain {@code element}, -1 is returned.
	 * 
	 * @param element element to search index for
	 * @return an integer {@code i}, where -1 &le; {@code i} &lt; {@link #indexBoundary()}.
	 * 
	 * @documented 2013-09-20
	 */
	int indexOf(Object element);
	
	/**
	 * Returns the upper boundary to indices returned by {@link #indexOf(Object)}.
	 * <p>
	 * All values returned by {@link #indexOf(Object)} must be lower than this boundary.
	 * <p>
	 * The boundary can be greater than the fixed number of elements in the universe. 
	 * For example, a hash-based universe will generally have a boundary that is a power
	 * of two above the number of elements. 
	 * 
	 * @return a positive integer
	 * @documented 2013-09-20
	 */
	int indexBoundary();
	
	/**
	 * Returns the element at the specified {@code index} or {@code null} if there is 
	 *     no element at the specified index.
	 *
	 * @param index index on which to search for an element.
	 * @return the element at the specified {@code index} or {@code null} if there is 
	 *     no element at the specified position.
	 * @throws IllegalArgumentException if {@code index} &lt; {@code 0} or {@code index} &ge; {@link #indexBoundary()}.
	 */
	T elementAt(int index);
	
	/**
	 * Returns the size of this universe.
	 * <p>
	 * The returned value is equal to or less than {@link #indexBoundary()},
	 * see {@link #indexBoundary()}. 
	 * 
	 * @return a positive integer &le; {@link #indexBoundary()}
	 */
	int size();
}
