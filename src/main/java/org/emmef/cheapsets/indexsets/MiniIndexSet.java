package org.emmef.cheapsets.indexsets;

import org.emmef.cheapsets.IndexSet;

/**
 * Implementation of {@link IndexSet} that has a bound of 32,
 * and thus supports indices between 0 and 31.
 * 
 * @documented 2013-07-26
 */
class MiniIndexSet implements IndexSet {
	private int present;

	private MiniIndexSet(int present) {
		this.present = present;
	}

	MiniIndexSet() {
		this(0);
	}
	
	@Override
	public int count() {
		return Integer.bitCount(present);
	}
	
	@Override
	public boolean isEmpty() {
		return present == 0;
	}

	@Override
	public boolean presentAt(int index) {
		return (present & (1L << index)) != 0;
	}

	@Override
	public boolean setAt(int index) {
		long old = present;
		present |= (1L << index);
		return old != present;
	}

	@Override
	public boolean removeAt(int index) {
		long old = present;
		present &= -1L ^ (1L << index);
		return old != present;
	}

	@Override
	public boolean containsAll(IndexSet indexSet) {
		MiniIndexSet set = (MiniIndexSet)indexSet;
		return (set.present & present) == set.present;
	}

	@Override
	public boolean addAll(IndexSet indexSet) {
		MiniIndexSet set = (MiniIndexSet)indexSet;
		long old = present;
		present |= set.present;
		return old != present;
	}

	@Override
	public boolean retainAll(IndexSet indexSet) {
		MiniIndexSet set = (MiniIndexSet)indexSet;
		long old = present;
		present &= set.present;
		return old != present;
	}

	@Override
	public boolean removeAll(IndexSet indexSet) {
		MiniIndexSet set = (MiniIndexSet)indexSet;
		long old = present;
		present &= -1L ^ set.present;
		return old != present;
	}
	
	@Override
	public void clear() {
		present = 0;
	}

	@Override
	public MiniIndexSet cloneEmpty() {
		return new MiniIndexSet(0);
	}
	
	@Override
	public MiniIndexSet clone() {
		return new MiniIndexSet(present);
	}

}
