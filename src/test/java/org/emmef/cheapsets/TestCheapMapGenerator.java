package org.emmef.cheapsets;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.Feature;
import com.google.common.collect.testing.features.MapFeature;

public final class TestCheapMapGenerator implements TestMapGenerator<String, String>, TestFeatures {
	private static final ImmutableList<Feature<?>> FEATURES = ImmutableList.<Feature<?>>of(
			MapFeature.ALLOWS_NULL_QUERIES,
			MapFeature.SUPPORTS_CLEAR,
			MapFeature.SUPPORTS_REMOVE,
			MapFeature.SUPPORTS_PUT,
			MapFeature.SUPPORTS_PUT_ALL,
			CollectionFeature.ALLOWS_NULL_QUERIES,
			CollectionFeature.REMOVE_OPERATIONS, 
			CollectionFeature.SUPPORTS_ADD, 
			CollectionFeature.SUPPORTS_ADD_ALL, 
			CollectionSize.ANY);
			
	private static final Set<String> UNIVERSE = ImmutableSet.of("Aap", "Noot", "Mies", "Wim", "Zus", "Jet", "Diederik", "Knoopsgat", "Volledig", "Knip");
	private static final Set<String> VALUES = ImmutableSet.<String>builder().addAll(UNIVERSE).add("Pokemon", "6^%$^%565", "Test3").build();
	
	private static final List<String> UNIVERSE_LIST = ImmutableList.copyOf(UNIVERSE);
	private static final List<String> VALUES_LIST = ImmutableList.copyOf(VALUES);
	
	private static final IndexedSubset<String> SUBSET = IndexedSubSets.createSubset(UNIVERSE);
	
	private static final SampleElements<Entry<String, String>> SAMPLE_ELEMENTS = createSampleElements();
	
	@Override
	public SampleElements<Entry<String, String>> samples() {
		return SAMPLE_ELEMENTS;
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public Entry<String, String>[] createArray(int length) {
		return (Entry<String, String>[])Array.newInstance(Map.Entry.class, length);
	}


	@Override
	public Map<String, String> create(Object... elements) {
		SubSetLimitedMap<String, String> created = new SubSetLimitedMap<String,String>(SUBSET);
		for (Object element : elements) {
			@SuppressWarnings("unchecked")
			Entry<String,String> entry = (Entry<String,String>)element;
			created.put(entry.getKey(), entry.getValue());
		}
		
		return created;
	}
	
	@Override
	public Iterable<Feature<?>> features() {
		return FEATURES; 
	}
	
	@Override
	public String getName() {
		return SubSetLimitedMap.class.getSimpleName() + "Test";
	}

	@Override
	public Iterable<Entry<String, String>> order(List<Entry<String, String>> insertionOrder) {
		return insertionOrder;
	}

	@Override
	public String[] createKeyArray(int length) {
		return new String[length];
	}

	@Override
	public String[] createValueArray(int length) {
		return new String[length];
	}

	private static SampleElements<Entry<String, String>> createSampleElements() {
		Entry<String, String> entries[] = new StringString[5];
		
		Set<String> hadKeys = Sets.newHashSet();
		Set<String> hadValues = Sets.newHashSet();
		
		for (int i = 0; i < 5; i++) {
			entries[i] = new StringString(getUnusedFrom(UNIVERSE_LIST, hadKeys), getUnusedFrom(VALUES_LIST, hadValues));
		}
		
		SampleElements<Entry<String, String>> sampleElements = new SampleElements<Entry<String,String>>(entries[0], entries[1], entries[2], entries[3], entries[4]);
		
		return sampleElements;
	}

	private static String getUnusedFrom(List<String> universe,
			Set<String> previousValues) {
		String value = getRandomElementFrom(universe);
		while (previousValues.contains(value)) {
			value = getRandomElementFrom(universe);
		}
		previousValues.add(value);
		return value;
	}

	private static String getRandomElementFrom(List<String> list) {
		return list.get((int)(Math.random() * list.size()));
	}
	
	private static class StringString implements Entry<String,String> {
		private final String key;
		private String value;

		public StringString(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		public String setValue(String value) {
			String result = this.value;
			this.value = value;
			return result;
		}
		
		@Override
		public String toString() {
			return "" + getKey() + "=" + getValue();
		}
		
		/**
		 * @see Map.Entry#equals(Object) for contract
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof Map.Entry)) {
				return false;
			}
			Map.Entry<?,?> entry = (Entry<?, ?>) obj;
			
			return Objects.equal(key,  entry.getKey()) && Objects.equal(value,  entry.getValue());
		}
		
		/**
		 * @see Map.Entry#hashCode() for contract
		 */
		@Override
		public int hashCode() {
			return (key == null   ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());		
		}
	}
}