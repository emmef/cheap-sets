package org.emmef.cheapsets;

class MiniIndexSet implements IndexSet<MiniIndexSet> {
	private int present;

	private MiniIndexSet(int present) {
		this.present = present;
	}

	MiniIndexSet() {
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
	public boolean containsAll(MiniIndexSet set) {
		return (set.present & present) == set.present;
	}

	@Override
	public boolean addAll(MiniIndexSet set) {
		long old = present;
		present |= set.present;
		return old != present;
	}

	@Override
	public boolean retainAll(MiniIndexSet set) {
		long old = present;
		present &= set.present;
		return old != present;
	}

	@Override
	public boolean removeAll(MiniIndexSet set) {
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
