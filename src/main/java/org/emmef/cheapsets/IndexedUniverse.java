package org.emmef.cheapsets;

/**
 * Represents a universe of elements that are associated with a unique index.
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
	 * If the universe contains {@code element}, its index is returned and lies between {@code 0} 
	 * and {@link #indexBoundary()} - 1 (exclusive). If the universe does not contain {@code element}, 
	 * -1 is returned.
	 * 
	 * @param element element to search index for
	 * @return an integer between {@code -1} and
	 *         {@link #indexBoundary()} - 1.
	 * 
	 * @documented 2013-08-09
	 */
	int indexOf(Object element);
	
	/**
	 * Returns the upper boundary to indices returned by {@link #indexOf(Object)}.
	 * <p>
	 * All values returned by {@link #indexOf(Object)} must be lower than this boundary.
	 * <p>
	 * For example, as hash-based universe will generally have a boundary that is a power
	 * of two, where not all indices are used. 
	 * 
	 * @return a positive integer
	 * @documented 2013-08-09
	 */
	int indexBoundary();
	
	/**
	 * Returns the element at the specified {@code index} or {@code null} if there is 
	 *     no element at the specified position.
	 *
	 * @param index index on which to search for an element.
	 * @return the element at the specified {@code index} or {@code null} if there is 
	 *     no element at the specified position.
	 * @throws IllegalArgumentException if {@code index < 0} or {@code index >= }{@link #indexBoundary()}.
	 */
	T elementAt(int index);
	
	/**
	 * Returns the size of the universe.
	 * <p>
	 * The returned value is equal to or less than {@link #indexBoundary()}, 
	 * as the {@link IndexFunction#indexOf(Object)} does not necessarily 
	 * return all values between 0 (inclusive) and 
	 * {@link IndexFunction#indexBoundary()} (exclusive).  
	 * 
	 * @return a positive integer
	 */
	int size();
}
