package org.emmef.cheapsets;

/**
 * Implementation of {@link IndexSet} that has a bound of 64,
 * and thus supports indices between 0 and 63.
 * 
 * @documented 2013-07-26
 */
class SmallIndexSet implements IndexSet<SmallIndexSet> {
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
	public int bound() {
		return 64;
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
	public boolean containsAll(SmallIndexSet set) {
		return (set.present & present) == set.present;
	}

	@Override
	public boolean addAll(SmallIndexSet set) {
		long old = present;
		present |= set.present;
		return old != present;
	}

	@Override
	public boolean retainAll(SmallIndexSet set) {
		long old = present;
		present &= set.present;
		return old != present;
	}

	@Override
	public boolean removeAll(SmallIndexSet set) {
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
