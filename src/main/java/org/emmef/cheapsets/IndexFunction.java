package org.emmef.cheapsets;

/**
 * Describes a unique index for a universe of objects.
 * <p>
 * All objects in a universe are required to have a different 
 * index. 
 */
public interface IndexFunction {
	/**
	 * Returns the unique index of the element, which lies between {@code 0} and
	 * {@link #indexSize()} - 1.
	 * 
	 * @param element element to search index for
	 * @return an integer between {@code 0} and
	 *         {@link #indexSize()} - 1.
	 * @documented 2013-07-26
	 */
	int indexOf(Object element);
	
	/**
	 * Returns the size of the index.
	 * 
	 * @return a positive integer
	 * @documented 2013-07-26
	 */
	int indexSize();
}
