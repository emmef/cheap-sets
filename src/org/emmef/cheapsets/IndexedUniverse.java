package org.emmef.cheapsets;

import java.util.Set;

public interface IndexedUniverse<T> extends Set<T> {
	/**
	 * Retrieves the index of the element if it is contained, {@code -1} otherwise.
	 * 
	 * <p>The collection cannot contain {@code null}, so if {@code element} is {@code null}, 
	 * {@code -1} is returned.</p>
	 * 
	 * @param element element to search.
	 * @return -1 or an integer that is <em>smaller</em> than {@link #indexSize()}{@code - 1} 
	 */
	int indexOf(Object element);
	/**
	 * Determines the maximum possible value that is returned by {@link #indexOf(Object)}.
	 * 
	 * <p>The following statements must be true:</p>
	 * <ul>
	 *   <li>value of {@link #indexOf(Object)} cannot be greater than {@link #indexSize()}{@code - 1}</li>
	 *   <li>{@link #size()}{@code <= }{@link #indexSize()}.</li>
	 * </ul>
	 * @return a positive integer that is equal or larger than {@link #size()}.
	 */
	int indexSize();
	/**
	 * Returns the element at the specified {@code index} or {@code null} if there is 
	 * no element at the specified position.
	 *
	 * <p>It {@link #indexSize()} is larger than {@link #size()}, it is possible that this 
	 * method return {@code null}. Not all implementations use all possible indices. 
	 * An example is where the index is decided by a hash-function.</p>
	 * 
	 * @param index index on which to search for an element.
	 * @return
	 * @throws IllegalArgumentException if {@code index < 0} or {@code index >= }{@link #indexSize()}.
	 */
	T elementAt(int index);
}