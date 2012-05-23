package org.emmef.cheapsets.universes;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.emmef.cheapsets.IndexFunction;
import org.emmef.cheapsets.hash.HashFunction;

public final class HashIndexedFunction implements IndexFunction {
	public static final int MAX_SIZE = 0x40000000; // maximum positive power of two

	private final HashFunction hashFunction;
	private final int mask;
	private final int size;

	public HashIndexedFunction(int size, HashFunction hashFunction) {
		checkArgument(size > 0 && Integer.bitCount(size) == 1, "Size for hash index must be a positive power of two");
		checkNotNull(hashFunction, "hashFunction cannot be null");
		
		this.size = size;
		this.hashFunction = hashFunction;
		this.mask = size - 1;
	}
	
	@Override
	public int indexOf(Object element) {
		return mask & hashFunction.hashCode(element);
	}
	
	@Override
	public int size() {
		return size;
	}
}
