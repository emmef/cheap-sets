package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.emmef.cheapsets.IndexFunction;

public class IndexFunctionIndexedUniverse<T> extends AbstractIndexedUniverse<T> {

	private final Object[] elements;
	private final IndexFunction function;
	private final int size;

	protected IndexFunctionIndexedUniverse(Object[] elements, IndexFunction function, int size) {
		checkNotNull(elements, "elements");
		checkNotNull(function, "function");
		checkArgument(elements.length == function.size(), "Array with elements must be equal to index size");
		checkArgument(size <= elements.length, "Number of elements must be smaller than or equal to index size");
		this.elements = elements;
		this.function = function;
		this.size = size;
	}
	
	@Override
	public int indexOf(Object element) {
		int indexOf = function.indexOf(element);
		
		Object elem = elements[indexOf];
		
		return elem != null && elem.equals(element) ? indexOf : -1;  
	}

	@Override
	public int indexSize() {
		return function.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T elementAt(int index) {
		return (T) elements[index];
	}

	@Override
	public int size() {
		return size;
	}

}
