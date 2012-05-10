package org.emmef.cheapsets.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.emmef.cheapsets.IndexedUniverse;

public class IndexedUniverseHelper {
	public static Object[] toArray(IndexedUniverse<?> universe) {
		int size = universe.size();
		Object[] result = new Object[size];
		int indexSize = universe.size();
		int idx = 0;
		for (int i = 0; i < indexSize; i++) {
			Object element = universe.elementAt(i);
			if (element != null) {
				result[idx++] = element;
			}
		}
		if (idx == size) {
			return result;
		}
		throw new IllegalStateException("universe.size() does not correspond to the actual number of elements");
	}

	public static <T> T[] toArray(IndexedUniverse<? extends T> universe, T[] a) {
		int size = universe.size();
		@SuppressWarnings("unchecked")
		T[] result = (T[])Array.newInstance(a.getClass().getComponentType(), size);
		int indexSize = universe.size();
		int idx = 0;
		for (int i = 0; i < indexSize; i++) {
			T element = universe.elementAt(i);
			if (element != null) {
				result[idx++] = element;
			}
		}
		if (idx == size) {
			return result;
		}
		throw new IllegalStateException("universe.size() does not correspond to the actual number of elements");
	}
	
	public static boolean containsAll(IndexedUniverse<?> universe, Collection<?> collection) {
		if (collection instanceof List) {
			List<?> list = (List<?>)collection;
			int size = collection.size();
			for (int i = 0; i < size; i++) {
				if (!universe.contains(list.get(i))) {
					return false;
				}
			}
		}
		else {
			for (Object o : collection) {
				if (!universe.contains(o)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static <T extends Comparable<T>> boolean isStrictMonotonicallyIncreasing(T[] elements) {
		checkNotNull(elements, "elements");
		checkArgument(elements.length != 0, "Need at least 1 element");
		T previous = checkNotNull(elements[0], "elements must not be null");
		for (int i = 1; i < elements.length; i++) {
			T current = checkNotNull(elements[i], "elements most not be null");

			if (previous.compareTo(current) >= 0) {
				return false;
			}
			previous = current;
		}
		
		return true;
	}
	
	public static <T> boolean isStrictMonotonicallyIncreasing(T[] elements, Comparator<? super T> comparator) {
		checkNotNull(elements, "elements");
		checkNotNull(comparator, "comparator");
		checkArgument(elements.length != 0, "Need at least 1 element");
		T previous = checkNotNull(elements[0], "elements must not be null");
		for (int i = 1; i < elements.length; i++) {
			T current = checkNotNull(elements[i], "elements most not be null");

			if (comparator.compare(previous, current) >= 0) {
				return false;
			}
			previous = current;
		}
		
		return true;
	}

}
