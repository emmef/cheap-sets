package org.emmef.cheapsets;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestSetGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;

public final class TestCheapSetGenerator implements TestSetGenerator<String>, TestFeatures {
	private static final ImmutableList<Feature<?>> FEATURES = ImmutableList.<Feature<?>>of(
			CollectionFeature.ALLOWS_NULL_QUERIES, 
			CollectionFeature.SUPPORTS_ADD, 
			CollectionFeature.SUPPORTS_ADD_ALL, 
			CollectionFeature.SUPPORTS_CLEAR, 
			CollectionFeature.SUPPORTS_REMOVE, 
			CollectionFeature.SUPPORTS_REMOVE_ALL, 
			CollectionFeature.SUPPORTS_RETAIN_ALL,
			CollectionSize.ANY);
			
	private static final Set<String> UNIVERSE = ImmutableSet.of("Aap", "Noot", "Mies", "Wim", "Zus", "Jet", "Diederik", "Knoopsgat", "Volledig", "Knip");
	
	private static final IndexedSubset<String> SUBSET = IndexedSubSets.createSubset(UNIVERSE);

	@Override
	public SampleElements<String> samples() {
		Iterator<String> iterator = UNIVERSE.iterator();
		return new SampleElements<String>(iterator.next(), iterator.next(), iterator.next(), iterator.next(), iterator.next());
	}

	@Override
	public String[] createArray(int length) {
		return new String[length];
	}

	@Override
	public Iterable<String> order(List<String> insertionOrder) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> create(Object... elements) {
		SubsetLimitedSet<String> created = SubsetLimitedSet.create(SUBSET);
		
		for (Object element : elements) {
			created.add((String)element);
		}
		
		return created;
	}
	
	@Override
	public Iterable<Feature<?>> features() {
		return FEATURES; 
	}
	
	@Override
	public String getName() {
		return SubsetLimitedSet.class.getSimpleName() + "Test";
	}
}