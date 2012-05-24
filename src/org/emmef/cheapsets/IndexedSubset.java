package org.emmef.cheapsets;

import java.util.Set;

/**
 * An immutable subset of values that can each be mapped to a unique index.
 * 
 * <p>
 * For an {@link Enum}, the enumeration values represent the subset. It is 
 * easy to construct very efficient collections based on the fact that the 
 * collections can only contain values in the enumeration. The collections can 
 * use the {@link Enum#ordinal()} value to use a bitmap that indicates if 
 * the element is present.
 * <p>
 * The {@link IndexedSubset} is based on the same principle, where 
 * {@link Enum#ordinal()} is replaced by {@link #indexOf(Object)}. Using 
 * an indexed subset makes it easy to create very lightweight collections. 
 * However, because the compiler does not limit the arguments passed to 
 * {@link #indexOf(Object)}, it returns {@code -1} if the argument is not 
 * a part of the subset or if it's {@code null}.
 * <p>
 * The number of elements in the subset is returned by {@link #size()}.
 * Though every element in the subset has to correspond to an index, 
 * not every index has to correspond to an element. For instance, if the 
 * subset implementation uses a hash computation, not all indexes have 
 * to be used and most probably aren't. In other words: the index size can 
 * be larger than the number of elements in the subset. This index size if  
 * returned by {@link #indexSize()}.
 *       
 * @param <T> type of elements in the subset
 */
public interface IndexedSubset<T> extends Set<T> {
	/**
	 * Retrieves the index of the element if it is contained, {@code -1} otherwise.
	 * <p>
	 * The subset cannot contain {@code null}, so passing that as an argument 
	 * causes a return value of {@code -1}.
	 * 
	 * @param element element to search.
	 * @return the index of the element in the subset or {@code -1} if the element is 
	 *     not a member of the subset. The index is zero or positive and smaller 
	 *     than {@link #indexSize()}. 
	 */
	int indexOf(Object element);
	/**
	 * Returns the index size.
	 * <p>
	 * The index size is always equal to or greater than the number of elements
	 * returned by {@link #size()}.
	 * 
	 * @return a positive integer that is equal to or greater than {@link #size()}.
	 */
	int indexSize();
	/**
	 * Returns the number of elements in the subset.
	 *  
	 * @return a positive integer that is equal to or smaller than {@link #indexSize()}.
	 */
	@Override
	int size();
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
}