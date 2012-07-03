package org.emmef.cheapsets;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Set;
/**
 * Creates a set, whose members are limited to those in an {@link IndexedSubset}.
 * <p>
 * An attempt to add elements that are not in the subset will cause an 
 * {@link ElementNotInSubsetException} to be thrown. Further, the set 
 * behaves as a normal {@link Set}.
 * 
 * @param <E> type of elements
 * @see Set
 * @see IndexedSubset
 */
public abstract class SubsetLimitedSet<E> implements Set<E>, Cloneable {
	/**
	 * Adds an element to the set. 
	 *
	 * @param element element to be added
	 * @returns {@code true} if the set was modified as a result
	 * @throws ElementNotInSubsetException if the element is not supported 
	 *     by the subset that backs this set.
	 * @see Collection#add(Object)
	 */
	@Override
	public abstract boolean add(E element);
	
	/**
	 * Adds all elements from the collection to this set.
	 * 
	 * @param c collection from which all elements have to be added.
	 * @returns {@code true} if the set was modified as a result
	 * @throws ElementNotInSubsetException if the element is not supported 
	 *     by the subset that backs this set.
	 * @see Collection#addAll(Collection)
	 */
	@Override
	public abstract boolean addAll(Collection<? extends E> c);

	/**
	 * Creates a new {@link SubsetLimitedSet} with the same subset and elements in it.
	 * 
	 * @return a new, {@code non-null} {@link SubsetLimitedSet}
	 */
	public abstract SubsetLimitedSet<E> clone();

	/**
	 * Creates a new {@link SubsetLimitedSet} with the same subset but no elements in it.
	 * <p>
	 * Use {@link #add(Object)} or {@link #addAll(Collection)} to add elements.  
	 * 
	 * @return a new, {@code non-null} {@link SubsetLimitedSet}
	 */
	public abstract SubsetLimitedSet<E> cloneEmpty();
	
	/**
	 * Creates a new {@link SubsetLimitedSet} that is based on the provided subset.
	 * <p>
	 * The set doesn't contain any elements yet: use {@link #add(Object)} or 
	 * {@link #addAll(Collection)} to add them.
	 * 
	 * @param subset subset to use as a base for this set.
	 * @return a new, {@code non-null} {@link SubsetLimitedSet}
	 * @throws NullPointerException if the subset is {@code null}.
	 */
	public static <E> SubsetLimitedSet<E> create(IndexedSubset<E> subset) {
		checkNotNull(subset, "subset");
		int indexSize = subset.indexSize();
		if (indexSize <= 32) {
			return new DefaultSubsetLimitedSet<E, MiniIndexSet>(subset, new MiniIndexSet());
		}
		else if (indexSize > 64) {
			return new DefaultSubsetLimitedSet<E, JumboIndexSet>(subset, new JumboIndexSet(indexSize));
		}
		else {
			return new DefaultSubsetLimitedSet<E, SmallIndexSet>(subset, new SmallIndexSet());
		}
	}
}
