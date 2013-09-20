package org.emmef.cheapsets;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.emmef.cheapsets.universes.IndexedUniverses;
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
public class UniverseBasedSet<E> implements Set<E>, Cloneable {
	private final IndexedUniverse<E> universe;
	private final IndexSet indexSet;
	
	UniverseBasedSet(IndexedUniverse<E> universe, IndexSet indexSet) {
		this.universe = checkNotNull(universe, "universe");
		this.indexSet = indexSet.clone();
	}
	
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
	public UniverseBasedSet(IndexedUniverse<E> universe) {
		this.universe = checkNotNull(universe, "universe");
		this.indexSet = IndexSet.Build.emptyFor(universe);
	}
	
	/**
	 * Creates a new {@link UniverseBasedSet} that is based on the provided universe of values.
	 * <p>
	 * The set doesn't contain any elements yet: use {@link #add(Object)} or 
	 * {@link #addAll(Collection)} to add them.
	 * <p>
	 * This implementation uses a default strategy to decide what type of indexed universe
	 * will be used by the set (see {@link IndexedUniverses#create(Set)}.
	 * 
	 * @param subset subset to use as a base for this set.
	 * @return a new, {@code non-null} {@link UniverseBasedSet}
	 * @throws NullPointerException if the subset is {@code null}.
	 */
	public UniverseBasedSet(Set<E> universe) {
		this(createIndexedUniverse(universe));
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws ElementNotInUniverseException if the element is not supported 
	 *     by the {@link IndexedUniverse} that backs this set.
	 * @see IndexedUniverse
	 */
	@Override
	public boolean add(E e) {
		return indexSet.setAt(validIndexOf(e));
	}

	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws ElementNotInUniverseException if the element is not supported 
	 *     by the {@link IndexedUniverse} that backs this set.
	 * @see IndexedUniverse
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (c instanceof UniverseBasedSet && ((UniverseBasedSet<?>) c).universe == universe) {
			return indexSet.addAll(((UniverseBasedSet<?>) c).indexSet);
		}
		boolean changed = false;
		if (c instanceof List) {
			List<?> list = (List<?>) c;
			int size = list.size();
			for (int i = 0; i < size; i++) {
				changed |= indexSet.setAt(validIndexOf(list.get(i)));
			}
		}
		else {
			for (Object o : c) {
				changed |= indexSet.setAt(validIndexOf(o));			
			}
		}
		
		return changed;
	}
	

	@Override
	public int size() {
		return indexSet.count();
	}

	@Override
	public boolean isEmpty() {
		return indexSet.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		int indexOf = universe.indexOf(o);
		
		return indexOf >= 0 && indexSet.presentAt(indexOf);
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private int position = 0;
			private int deletePosition = -1;
			private E next = null;

			@Override
			public boolean hasNext() {
				if (next != null) {
					return true;
				}
				while (next == null && position < universe.indexBoundary()) {
					E element = universe.elementAt(position);
					if (element != null && indexSet.presentAt(position)) {
						next = element;
						return true;
					}
					position++;
				}
				return false;
			}

			@Override
			public E next() {
				if (hasNext()) {
					E result = next;
					next = null;
					deletePosition = position++;
					return result;
				}
				
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				if (deletePosition == -1) {
					throw new IllegalStateException("Can only call remove() ONCE, directly after a call to next*(");
				}
				indexSet.removeAt(deletePosition);
				deletePosition = -1;
			}
		};
	}

	@Override
	public Object[] toArray() {
		int size = indexSet.count();
		Object[] result = new Object[size];
		int indexSize = universe.indexBoundary();
		int idx = 0;
		for (int i = 0; i < indexSize; i++) {
			if (indexSet.presentAt(i)) {
				result[idx++] = universe.elementAt(i);
			}
		}
		if (idx == size) {
			return result;
		}
		throw new IllegalStateException("universe.size() does not correspond to the actual number of elements");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		int size = indexSet.count();
		T[] result;
		if (a.length < size) {
			result = (T[])Array.newInstance(a.getClass().getComponentType(), size);
		}
		else {
			result = a;
			if (a.length > size) {
				result[size] = null;
			}
		}
		int indexSize = universe.indexBoundary();
		int idx = 0;
		for (int i = 0; i < indexSize; i++) {
			if (indexSet.presentAt(i)) {
				result[idx++] = (T)universe.elementAt(i);
			}
		}
		
		if (idx == size) {
			return result;
		}
		
		throw new IllegalStateException("universe.size() does not correspond to the actual number of elements");
	}

	@Override
	public boolean remove(Object o) {
		int indexOf = universe.indexOf(o);
		
		return indexOf >= 0 && indexSet.removeAt(indexOf);
	}
	

	@Override
	public boolean containsAll(Collection<?> c) {
		if (c instanceof UniverseBasedSet && ((UniverseBasedSet<?>) c).universe == universe) {
			return indexSet.containsAll(((UniverseBasedSet<?>) c).indexSet);
		}
		if (c instanceof List) {
			List<?> list = (List<?>) c;
			int size = list.size();
			for (int i = 0; i < size; i++) {
				if (!contains(list.get(i))) {
					return false;
				}
			}
		}
		else {
			for (Object o : c) {
				if (!contains(o)) {
					return false;
				}
			}
		}
		
		return true;
	}


	@Override
	public boolean retainAll(Collection<?> c) {
		if (c instanceof UniverseBasedSet && ((UniverseBasedSet<?>) c).universe == universe) {
			return indexSet.retainAll(((UniverseBasedSet<?>) c).indexSet);
		}
		boolean changed = false;
		for (int i = 0; i < universe.indexBoundary(); i++) {
			if (indexSet.presentAt(i) && !c.contains(universe.elementAt(i))) {
				indexSet.removeAt(i);
				changed = true;
			}
		}
		
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if (c instanceof UniverseBasedSet && ((UniverseBasedSet<?>) c).universe == universe) {
			return indexSet.removeAll(((UniverseBasedSet<?>) c).indexSet);
		}
		boolean changed = false;
		if (c instanceof List) {
			List<?> list = (List<?>) c;
			int size = list.size();
			for (int i = 0; i < size; i++) {
				int indexOf = universe.indexOf(list.get(i));
				if (indexOf >= 0) {
					changed |= indexSet.removeAt(indexOf);
				}
			}
		}
		else {
			for (Object o : c) {
				int indexOf = universe.indexOf(o);
				if (indexOf >= 0) {
					changed |= indexSet.removeAt(indexOf);
				}
			}
		}
		
		return changed;
	}

	@Override
	public void clear() {
		indexSet.clear();
	}

	static <E> UniverseBasedSet<E> copyOf(UniverseBasedSet<E> original) {
		return new UniverseBasedSet<E>(original.universe, original.indexSet.clone());
	}

	static <E> UniverseBasedSet<E> basedOn(UniverseBasedSet<E> original) {
		return new UniverseBasedSet<E>(original.universe);
	}
	
	public int hashCode() {
		int hash = 0;
		int indexSize = universe.indexBoundary();
		for (int i = 0; i < indexSize; i++) {
			if (indexSet.presentAt(i)) {
				hash += universe.elementAt(i).hashCode();
			}
		}

		return hash;
	};
	
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Set)) {
			return false;
		}
		
		Set<?> set = (Set<?>)(o);
		
		if (size() != set.size()) {
			return false;
		}
		
		return containsAll(set) && set.containsAll(this);
	};
	
	public String toString() {
		if (isEmpty()) {
			return "[]";
		}
		
		StringBuilder string = new StringBuilder();
		int indexSize = universe.indexBoundary();
		
		boolean first = true;
		for (int i = 0; i < indexSize; i++) {
			if (indexSet.presentAt(i)) {
				if (first) {
					first = false;
					string.append('[');
				}
				else {
					string.append(',');
				}
				
				string.append(universe.elementAt(i));
			}
		}
		string.append(']');
		
		return string.toString();
	};
	
	public final IndexedUniverse<E> subSet() {
		return universe;
	}
	
	private int validIndexOf(Object element) {
		int indexOf = universe.indexOf(element);
		
		if (indexOf >= 0) {
			return indexOf;
		}
		
		throw new ElementNotInUniverseException("Element is not backed by " + IndexedUniverse.class.getSimpleName() + ": " + element);
	}
	
	private static <E> IndexedUniverse<E> createIndexedUniverse(Set<E> universe) {
		checkNotNull(universe, "universe");
		
		if (universe instanceof UniverseBasedSet) {
			return ((UniverseBasedSet<E>)universe).subSet();
		}
		
		return IndexedUniverses.create(universe);
	}
}
