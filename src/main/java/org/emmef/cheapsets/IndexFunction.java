package org.emmef.cheapsets;

/**
 * Describes a unique index for a universe of objects.
 * <p>
 * Objects that are not in the universe, should have an invalid 
 * index of {@code -1}.
 */
public interface IndexFunction {
	/**
	 * Returns the unique index of the element, which lies between {@code 0} and
	 * {@link #indexSize()} {@code - 1}.
	 * 
	 * @param element element to search index for
	 * @return the index of the element, which lies between {@code 0} and
	 *         {@link #indexSize()} {@code - 1}.
	 * @documented 2013-07-26
	 */
	int indexOf(Object element);
	
	/**
	 * Returns the size of the index.
	 * @return a positive integer
	 * @documented 2013-07-26
	 */
	int indexSize();
}
