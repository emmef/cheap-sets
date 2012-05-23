package org.emmef.cheapsets;

import java.util.Set;

/**
 * An immutable subset of values that can each be mapped to a unique index.
 * 
 * <p>
 * For an {@link Enum}, the enum values represents the universe. It is 
 * easy to construct very efficient collections based on the fact that the 
 * collections can only contain values from the enum. The collections can 
 * use the {@link Enum#ordinal()} value to use a bitmap that indicates if 
 * the element is present.
 * <p>
 * The {@link IndexedUniverse} is based on the same principle, but for 
 * types that do not have a compiler-enforced limitation on the number of
 * possible values. The {@link #indexOf(Object)} determines the index of 
 * an element in the universe. As the compiler doesn't limit the argument
 * value, the method return {@code -1} if the element is not present or 
 * is {@code null}.
 * <p>
 * The number of elements in the universe is returned by {@link #size()}.
 * <p>
 * Though every element in the universe has to correspond to an index, 
 * not every index has to correspond to an element. For instance, if the 
 * implementation uses a hash computation, the index size can be bigger 
 * than the actual number of elements. The index size is returned by 
 * {@link #indexSize()}. If the index size is greater than the 
 * number of elements, there are unmapped indexes.
 *       
 * @param <T> type of elements in the universe 
 */
public interface IndexedUniverse<T> extends Set<T> {
	/**
	 * Retrieves the index of the element if it is contained, {@code -1} otherwise.
	 * 
	 * <p>The collection cannot contain {@code null}. In that case, {@code -1} is returned.</p>
	 * 
	 * @param element element to search.
	 * @return -1 or an integer that is <em>smaller</em> than {@link #indexSize()}{@code - 1} 
	 */
	int indexOf(Object element);
	/**
	 * Determines the index size.
	 * 
	 * <p>The index size is always equal or greater than the number of elements
	 * in the universe. The number of elements in the universe is returned by 
	 * {@link #size()}.</p>
	 * <p>The index of an element is always smaller than the index size.</p>
	 * 
	 * @return a positive integer that is equal or greater than {@link #size()}.
	 */
	int indexSize();
	/**
	 * Returns the element at the specified {@code index} or {@code null} if there is 
	 * no element at the specified position.
	 *
	 * @param index index on which to search for an element.
	 * @return
	 * @throws IllegalArgumentException if {@code index < 0} or {@code index >= }{@link #indexSize()}.
	 */
	T elementAt(int index);
}