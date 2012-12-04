package org.emmef.cheapsets;

import static com.google.common.base.Preconditions.*;

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
			CollectionFeature.SUPPORTS_REMOVE, 
			CollectionSize.ANY);
			
	private static final Set<String> UNIVERSE = ImmutableSet.of("Aap", "Noot", "Mies", "Wim", "Zus", "Jet", "Diederik", "Knoopsgat", "Volledig", "Knip");
	
	private final IndexType indexType;

	public TestCheapSetGenerator(IndexType indexType) {
		this.indexType = checkNotNull(indexType, "indexType");
	}

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
		SubsetLimitedSet<String> created = SubsetLimitedSet.create(indexType.create(UNIVERSE));
		
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