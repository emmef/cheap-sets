package org.emmef.cheapsets.universes;


import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.emmef.cheapsets.IndexedUniverse;

import com.google.common.collect.Maps;

/**
 * Implements an {@link IndexedUniverse} that uses an array for 
 *     element lookup and a map for index lookup.
 *     
 * <p>This is a kind of a last-resort solution that always works but 
 * is not very lightweight. On the other hand, the idea of an 
 *
 * @param <T> type of element to contain.
 */
public class MapIndexedUniverse<T> implements IndexedUniverse<T> {
	private final Map<T, Integer> elementToIndex;
	private final Object[] array;
	
	public MapIndexedUniverse(Set<T> values) {
		this.elementToIndex = createElementToIndexMap(values);
		int i = 0;
		
		for (T element : values) {
			checkNotNull(element, IndexedUniverse.class.getSimpleName() + " cannot contain null elements");
			
			elementToIndex.put(element, Integer.valueOf(i++));
		}
		
		if (elementToIndex.size() != values.size()) {
			throw new IllegalArgumentException("Cannot create an index set of equal size as original values");
		}
		
		this.array = createPopulation(elementToIndex);
	}
	
	@Override
	public int indexOf(Object element) {
		Integer indexObj = elementToIndex.get(element);
		
		int indexOf = indexObj != null ? indexObj.intValue() : -1;
		
		if (indexOf == -1) {
			return -1;
		}
		
		Object elem = array[indexOf];
		
		return elem != null && elem.equals(element) ? indexOf : -1;  
	}

	@Override
	public int indexBoundary() {
		return elementToIndex.size();
	}

	@Override
	public T elementAt(int index) {
		@SuppressWarnings("unchecked")
		T result = (T) array[index];
		
		return result;
	}
	
	@Override
	public int size() {
		return elementToIndex.size();
	}
	
	@Override
	public String toString() {
		return IndexedUniverses.toString(this);
	}
	
	private static <T> Object[] createPopulation(Map<T, Integer> elementToIndex) {
		Object result[] = new Object[elementToIndex.size()];
		
		for (Entry<T, Integer> entry : elementToIndex.entrySet()) {
			result[entry.getValue()] = entry.getKey();
		}
		
		return result;
	}
	
	private static <T> Map<T, Integer> createElementToIndexMap(Set<T> values) {
		if (values instanceof SortedSet) {
			SortedSet<T> sortedSet = (SortedSet<T>) values;
			Comparator<? super T> comparator = sortedSet.comparator();
			if (comparator != null) {
				return Maps.newTreeMap(comparator);
			}
			return new TreeMap<T, Integer>();
		}
		return Maps.newHashMap();
	}
}


