package org.emmef.cheapsets.util;

import java.lang.reflect.Array;
import java.util.Collection;
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

}
