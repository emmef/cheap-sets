package org.emmef.cheapsets.universes;


import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.emmef.cheapsets.util.IndexedUniverseHelper;

import com.google.common.collect.Maps;

public class BimapIndexedUniverse<T> extends AbstractIndexedUniverse<T> {
	private final Map<T, Integer> elementToIndex;
	private final Object[] elements;
	
	public BimapIndexedUniverse(Set<T> values) {
		this.elementToIndex = createElementToIndexMap(values);
		int size = values.size();
		this.elements = new Object[size];
		int i = 0;
		for (T element : values) {
			IndexedUniverseHelper.checkElementNotNull(element);
			
			elements[i] = element;
			elementToIndex.put(element, Integer.valueOf(i++));
		}
	}

	@Override
	public int indexOf(Object element) {
		Integer indexOf = elementToIndex.get(element);
		
		return indexOf != null ? indexOf.intValue() : -1;
	}

	@Override
	public int indexSize() {
		return elements.length;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T elementAt(int index) {
		return (T) elements[index];
	}

	@Override
	public int size() {
		return elements.length;
	}

	private Map<T, Integer> createElementToIndexMap(Set<T> values) {
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
