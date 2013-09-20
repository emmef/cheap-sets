package org.emmef.cheapsets;

/**
 * Implementation of {@link IndexSet} that has a bound of 64,
 * and thus supports indices between 0 and 63.
 * 
 * @documented 2013-07-26
 */
class SmallIndexSet implements IndexSet {
	private long present;

	private SmallIndexSet(long present) {
		this.present = present;
	}
	
	SmallIndexSet() {
		this(0);
	}

	@Override
	public int count() {
		return Long.bitCount(present);
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
		SmallIndexSet set = (SmallIndexSet)indexSet;
		return (set.present & present) == set.present;
	}

	@Override
	public boolean addAll(IndexSet indexSet) {
		SmallIndexSet set = (SmallIndexSet)indexSet;
		long old = present;
		present |= set.present;
		return old != present;
	}

	@Override
	public boolean retainAll(IndexSet indexSet) {
		SmallIndexSet set = (SmallIndexSet)indexSet;
		long old = present;
		present &= set.present;
		return old != present;
	}

	@Override
	public boolean removeAll(IndexSet indexSet) {
		SmallIndexSet set = (SmallIndexSet)indexSet;
		long old = present;
		present &= -1L ^ set.present;
		return old != present;
	}
	
	@Override
	public void clear() {
		present = 0;
	}

	@Override
	public SmallIndexSet cloneEmpty() {
		return new SmallIndexSet(0);
	}
	
	@Override
	public SmallIndexSet clone() {
		return new SmallIndexSet(present);
	}

}
