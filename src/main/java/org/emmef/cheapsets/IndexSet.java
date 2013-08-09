package org.emmef.cheapsets;

import java.util.Set;

/**
 * Registers the presence of elements at a limited set of indices.
 * <p>
 * The indices are limited between 0 (inclusive) and the result of
 * {@link #bound()} (exclusive).
 * <p>
 * An index set and an {@link IndexedUniverse} can be combined to 
 * create a rather lightweight {@link Set} or map implementation.
 * 
 * @see IndexedUniverse
 * 
 * @documented 2013-07-26
 */
interface IndexSet<T extends IndexSet<?>> extends Cloneable {
	/**
	 * Returns the number of elements that are currently present in the index.
	 * <p>
	 * The returned value is between 0 (inclusive) and the result of
	 * {@link #bound()} (exclusive).
	 * 
	 * @return a zero or positive integer.
	 * @documented 2013-07-26
	 */
	int count();
	
	/**
	 * Returns the positive index bound.
	 * <p>
	 * Allowed indices for this set are between 0 (inclusive) and bound
	 * (exclusive).
	 * 
	 * @return a positive integer.
	 * @documented 2013-07-26
	 */
	int bound();
	
	/**
	 * Returns whether the set is empty.
	 *
	 * @return {@code true} if no elements are present, false otherwise.
	 * @documented 2013-07-26
	 */
	boolean isEmpty();
	
	/**
	 * Returns if the element at the specified place is present.
	 * 
	 * @param index index of the element
	 * @return {@code true} if the element at the specified index is present, {@code false} otherwise.
	 * @documented 2013-07-26 
	 */
	boolean presentAt(int index);
	
	/**
	 * Marks the element at the specified index present and return {@code true} 
	 *     if the element at the specified index was not yet present.
	 * 
	 * @param index index of the element
	 * @return {@code true} if this {@link IndexSet} changed as a result.
	 * @documented 2013-07-26
	 */
	boolean setAt(int index);
	
	/**
	 * Marks the element at the specified index NOT present and return {@code true} 
	 *     if the element at the specified index was present.
	 * 
	 * @param index index of the element
	 * @return {@code true} if this {@link IndexSet} changed as a result.
	 * 
	 * @documented 2013-07-26
	 */
	boolean removeAt(int index);
	
	/**
	 * Returns if all elements that are present in the provided set are also present
	 *     in this set.
	 * 
	 * @param set other index set
	 * @return {@code true} if all elements that are present in the provided set are also present
	 *     in this set, {@code false} otherwise.
	 * 
	 * @documented 2013-07-26
	 */
	boolean containsAll(T set);
	
	/**
	 * Set all elements that are present in the provided set, present in this set.
	 * 
	 * @param set other index set
	 * @return {@code true} if this {@link IndexSet} changed as a result.
	 * @documented 2013-07-26
	 */
	boolean addAll(T set);
	
	/**
	 * Keep only elements that are present in the provided set, present in this set.
	 * 
	 * @param set other index set
	 * @return {@code true} if this {@link IndexSet} changed as a result.
	 * @documented 2013-07-26
	 */
	boolean retainAll(T set);
	
	/**
	 * Set all elements that are present in the provided set, NOT present in this set.
	 * 
	 * @param set other index set
	 * @return {@code true} if this {@link IndexSet} changed as a result.
	 * @documented 2013-07-26
	 */
	boolean removeAll(T set);
	
	/**
	 * Sets all elements to NOT present.
	 * @documented 2013-07-26
	 */
	void clear();
	
	/**
	 * Create a new index set that has the same index bound ({@link #bound()} as this 
	 *     set, but has no elements present {@code (}{@link #count()}{@code == 0)}.
	 * 
	 * @return a {@code non-null} {@link IndexSet}
	 * @documented 2013-07-26
	 */
	T cloneEmpty();
	
	/**
	 * Create a new index set that has the same index bound ({@link #bound()} as this 
	 *     set and has the same elements present.
	 * 
	 * @return a {@code non-null} {@link IndexSet}
	 * @documented 2013-07-26
	 */
	T clone();
}
