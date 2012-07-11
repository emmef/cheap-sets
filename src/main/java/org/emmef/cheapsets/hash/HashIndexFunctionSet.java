package org.emmef.cheapsets.hash;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.emmef.cheapsets.universes.HashIndexedFunction;

import com.google.common.collect.Maps;

public class HashIndexFunctionSet {
	private final HashFunction hashFunction;
	private final Map<Integer, HashIndexedFunction> sizeToFunctionMap = Maps.newHashMap();

	public HashIndexFunctionSet(HashFunction hashFunction) {
		this.hashFunction = checkNotNull(hashFunction, "function");
		for (int size = 1; size < HashIndexedFunction.MAX_SIZE; size <<= 1) {
			sizeToFunctionMap.put(Integer.valueOf(size), new HashIndexedFunction(size, this.hashFunction));
		}
	}
	
	public HashFunction getHashFunction() {
		return hashFunction;
	}
	
	public HashIndexedFunction getIndexFunction(int size) {
		if (size < 1 || Integer.bitCount(size) != 1) {
			throw new IllegalArgumentException("Size for hash indexed function (" + size + ") is not a positive power of two");
		}
		
		return sizeToFunctionMap.get(size);
	}
}