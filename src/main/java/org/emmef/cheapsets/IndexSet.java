package org.emmef.cheapsets;

/**
 * Keeps information on whether an element on a specific index is
 * present. 
 */
interface IndexSet<T extends IndexSet<?>> extends Cloneable {
	/**
	 * Returns the number of elements that are currently present in the index.
	 * <p>
	 * The returned value is zero or positive but smaller than or equal to {@link #indexSize()}. 
	 * @return a zero or positive integer.
	 */
	int count();
	
	/**
	 * Returns whether no elements are present. 
	 * @return {@code true} if no elements are present, false otherwise.
	 */
	boolean isEmpty();
	
	/**
	 * Returns if the element at the specified place is present.
	 * @param index index of the element
	 * @return {@code true} if the element at the specified index is present, {@code false} otherwise. 
	 */
	boolean presentAt(int index);
	
	/**
	 * Marks the element at the specified index present and return {@code true} 
	 *     if the element at the specified index was not yet present.
	 * @param index index of the element
	 * @return {@code true} if this {@link IndexSet} changed as a result.
	 */
	boolean setAt(int index);
	
	/**
	 * Marks the element at the specified index NOT present and return {@code true} 
	 *     if the element at the specified index was present.
	 * @param index index of the element
	 * @return {@code true} if this {@link IndexSet} changed as a result.
	 */
	boolean removeAt(int index);
	
	/**
	 * Returns if all elements that are present in the provided set are also present
	 *     in this set. 
	 * @param set other index set
	 * @return {@code true} if all elements that are present in the provided set are also present
	 *     in this set, {@code false} otherwise.
	 */
	boolean containsAll(T set);
	
	/**
	 * Set all elements that are present in the provided set, present in this set.
	 * @param set other index set
	 * @return {@code true} if this {@link IndexSet} changed as a result.
	 */
	boolean addAll(T set);
	
	/**
	 * Keep only elements that are present in the provided set, present in this set.
	 * 
	 * @param set other index set
	 * @return {@code true} if this {@link IndexSet} changed as a result.
	 */
	boolean retainAll(T set);
	
	/**
	 * Set all elements that are present in the provided set, NOT present in this set.
	 * @param set other index set
	 * @return {@code true} if this {@link IndexSet} changed as a result.
	 */
	boolean removeAll(T set);
	
	/**
	 * Sets all elements to NOT present.
	 */
	void clear();
	
	/**
	 * Create a new index set that has the same index size ({@link #indexSize()} as this 
	 *     set, but has no elements present {@code (}{@link #count()}{@code == 0)}.
	 * @return a {@code non-null} {@link IndexSet}
	 */
	T cloneEmpty();
	
	/**
	 * Create a new index set that has the same index size ({@link #indexSize()} as this 
	 *     set and has the same elements present.
	 *     
	 * @return a {@code non-null} {@link IndexSet}
	 */
	T clone();
}
