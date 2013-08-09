package org.emmef.cheapsets;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Set;
/**
 * Creates a set, whose members are limited to those in an {@link IndexedUniverse}.
 * <p>
 * An attempt to add elements that are not in the subset will cause an 
 * {@link ElementNotInUniverseException} to be thrown. Further, the set 
 * behaves as a normal {@link Set}.
 * 
 * @param <E> type of elements
 * @see Set
 * @see IndexedUniverse
 */
public abstract class UniverseBasedSet<E> implements Set<E>, Cloneable {
	
	/**
	 * Creates a new {@link UniverseBasedSet} that is based on the provided subset.
	 * <p>
	 * The set doesn't contain any elements yet: use {@link #add(Object)} or 
	 * {@link #addAll(Collection)} to add them.
	 * 
	 * @param subset subset to use as a base for this set.
	 * @return a new, {@code non-null} {@link UniverseBasedSet}
	 * @throws NullPointerException if the subset is {@code null}.
	 */
	public static <E> UniverseBasedSet<E> create(IndexedUniverse<E> subset) {
		checkNotNull(subset, "subset");
		int indexSize = subset.indexSize();
		if (indexSize <= 32) {
			return new DefaultUniverseBasedSet<E, MiniIndexSet>(subset, new MiniIndexSet());
		}
		else if (indexSize > 64) {
			return new DefaultUniverseBasedSet<E, JumboIndexSet>(subset, new JumboIndexSet(indexSize));
		}
		else {
			return new DefaultUniverseBasedSet<E, SmallIndexSet>(subset, new SmallIndexSet());
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws ElementNotInUniverseException if the element is not supported 
	 *     by the {@link IndexedUniverse} that backs this set.
	 * @see IndexedUniverse
	 */
	@Override
	public abstract boolean add(E element);
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws ElementNotInUniverseException if the element is not supported 
	 *     by the {@link IndexedUniverse} that backs this set.
	 * @see IndexedUniverse
	 */
	@Override
	public abstract boolean addAll(Collection<? extends E> c);

	/**
	 * Creates a new {@link UniverseBasedSet} with the same subset and elements in it.
	 * 
	 * @return a new, {@code non-null} {@link UniverseBasedSet}
	 */
	public abstract UniverseBasedSet<E> clone();

	/**
	 * Creates a new {@link UniverseBasedSet} with the same subset but no elements in it.
	 * <p>
	 * Use {@link #add(Object)} or {@link #addAll(Collection)} to add elements.  
	 * 
	 * @return a new, {@code non-null} {@link UniverseBasedSet}
	 */
	public abstract UniverseBasedSet<E> cloneEmpty();
	
	abstract IndexedUniverse<E> subSet();
}
