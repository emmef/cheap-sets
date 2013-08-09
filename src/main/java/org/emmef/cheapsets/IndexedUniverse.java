package org.emmef.cheapsets;

public interface IndexedUniverse<T> extends IndexFunction {
	
	/**
	 * Returns the element at the specified {@code index} or {@code null} if there is 
	 *     no element at the specified position.
	 *
	 * @param index index on which to search for an element.
	 * @return the element at the specified {@code index} or {@code null} if there is 
	 *     no element at the specified position.
	 * @throws IllegalArgumentException if {@code index < 0} or {@code index >= }{@link #indexSize()}.
	 */
	T elementAt(int index);
	
	/**
	 * Returns the size of the universe.
	 * <p>
	 * The returned value is equal to or less than {@link #indexSize()}, 
	 * as the {@link IndexFunction#indexOf(Object)} does not necessarily 
	 * return all values between 0 (inclusive) and 
	 * {@link IndexFunction#indexSize()} (exclusive).  
	 * 
	 * @return a positive integer
	 */
	int size();
}
