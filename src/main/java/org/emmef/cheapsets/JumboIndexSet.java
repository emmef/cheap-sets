package org.emmef.cheapsets;

/**
 * Implementation of {@link IndexSet} that has a bound that is 
 * a multiple of 64, and thus supports indices between 0 and that
 * multiple minus one.
 * 
 * @documented 2013-07-26
 */
class JumboIndexSet implements IndexSet<JumboIndexSet> {
	private final long[] present;

	private JumboIndexSet(long[] present) {
		this.present = present.clone();
	}
	
	public JumboIndexSet(int size) {
		if (size < 1) {
			throw new IllegalArgumentException(JumboIndexSet.class.getSimpleName() + ": size (" + size + ") must positive");
		}
		this.present = new long [(size + 63) / 64];
	}
	
	@Override
	public int count() {
		int count = 0;
		
		for (int i = 0; i < present.length; i++) {
			count += Long.bitCount(present[i]); 
		}
		
		return count;
	}
	
	@Override
	public int bound() {
		return 64 * present.length;
	}
	
	@Override
	public boolean isEmpty() {
		for (int i = 0; i < present.length; i++) {
			if (present[i] != 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean presentAt(int index) {
		int validIndex = validIndex(index);
		int elem = validIndex >> 6;
		long bit = 1L << (validIndex & 0x3f);
		
		return (present[elem] & bit) != 0;
	}

	@Override
	public boolean setAt(int index) {
		int validIndex = validIndex(index);
		int elem = validIndex >> 6;
		long bit = 1L << (validIndex & 0x3f);
		
		long oldPresent = present[elem];
		long newPresent = oldPresent | bit;
		
		present[elem] = newPresent;
		
		return oldPresent != newPresent;
	}

	@Override
	public boolean removeAt(int index) {
		int validIndex = validIndex(index);
		int elem = validIndex >> 6;
		long bit = 1L << (validIndex & 0x3f);
		
		long oldPresent = present[elem];
		long newPresent = oldPresent & (-1L ^ bit);
		
		present[elem] = newPresent;
		
		return oldPresent != newPresent;
	}

	@Override
	public boolean containsAll(JumboIndexSet set) {
		checkJumboArgument(set);
		for (int i = 0; i < present.length; i++) {
			long other = set.present[i];
			if ((other & present[i]) != other) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean addAll(JumboIndexSet set) {
		checkJumboArgument(set);
		boolean changed = false;
		
		for (int i = 0; i < present.length; i++) {
			long oldPresent = present[i];
			long newPresent = oldPresent | set.present[i];
			changed |= oldPresent != newPresent;
		}
		
		return changed;
	}

	@Override
	public boolean retainAll(JumboIndexSet set) {
		checkJumboArgument(set);
		boolean changed = false;
		
		for (int i = 0; i < present.length; i++) {
			long oldPresent = present[i];
			long newPresent = oldPresent & set.present[i];
			changed |= oldPresent != newPresent;
		}
		
		return changed;
	}

	@Override
	public boolean removeAll(JumboIndexSet set) {
		checkJumboArgument(set);
		boolean changed = false;
		
		for (int i = 0; i < present.length; i++) {
			long oldPresent = present[i];
			long newPresent = oldPresent & (-1L ^ set.present[i]);
			changed |= oldPresent != newPresent;
		}
		
		return changed;
	}
	
	@Override
	public void clear() {
		for (int i = 0; i < present.length; i++) {
			present[i] = 0;
		}
	}

	@Override
	public JumboIndexSet cloneEmpty() {
		return new JumboIndexSet(present.length * 64);
	}
	
	@Override
	public JumboIndexSet clone() {
		return new JumboIndexSet(present);
	}
	
	private final int validIndex(int index) {
		if (index >= 0 && index < (present.length >> 6)) {
			return index;
		}
		
		throw new IndexOutOfBoundsException("Index (" + index + ") must be between 0 and " + ((present.length >> 6) - 1)); 
	}

	private void checkJumboArgument(JumboIndexSet set) {
		if (present.length != set.present.length) {
			throw new IllegalArgumentException("Both " + JumboIndexSet.class.getSimpleName() + "s must have the same length");
		}
	}
}
