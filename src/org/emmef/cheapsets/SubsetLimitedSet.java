package org.emmef.cheapsets;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.emmef.cheapsets.hash.HashFunction;
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
	public static <E> SubsetLimitedSet<E> createEmpty(IndexedSubset<E> subset) {
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
	
	public static <E> SubsetLimitedSet<E> baseOn(Set<E> set, boolean tryHashBasedSubsets) {
		if (set instanceof SubsetLimitedSet) {
			return ((SubsetLimitedSet<E>)set).clone();
		}

		return createEmpty(IndexedSubSets.createSubset(set, tryHashBasedSubsets));
	}
	
	public static <E> SubsetLimitedSet<E> baseOn(Set<E> set, List<HashFunction> hashFunctions) {
		if (set instanceof SubsetLimitedSet) {
			return ((SubsetLimitedSet<E>)set).clone();
		}
		
		return createEmpty(IndexedSubSets.createSubset(set, hashFunctions));
	}
	
	public static <E> SubsetLimitedSet<E> baseOn(Set<E> set) {
		if (set instanceof SubsetLimitedSet) {
			return ((SubsetLimitedSet<E>)set).clone();
		}
		
		return createEmpty(IndexedSubSets.createSubset(set, HashFunction.DEFAULT_SAFE_HASHES));
	}
	
	public static <E> SubsetLimitedSet<E> copyOf(Set<E> set, boolean tryHashBasedSubsets) {
		SubsetLimitedSet<E> result = baseOn(set, tryHashBasedSubsets);
		
		result.addAll(set);
		
		return result;
	}
	
	public static <E> SubsetLimitedSet<E> copyOf(Set<E> set, List<HashFunction> hashFunctions) {
		SubsetLimitedSet<E> result = baseOn(set, hashFunctions);
		
		result.addAll(set);
		
		return result;
	}
	
	public static <E> SubsetLimitedSet<E> copyOff(Set<E> set) {
		SubsetLimitedSet<E> result = baseOn(set);
		
		result.addAll(set);
		
		return result;
	}

}
