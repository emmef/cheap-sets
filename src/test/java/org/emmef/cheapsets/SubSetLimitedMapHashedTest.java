package org.emmef.cheapsets;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import com.google.common.collect.testing.MapTestSuiteBuilder;

@RunWith(AllTests.class)
public class SubSetLimitedMapHashedTest {
	private static final TestCheapMapGenerator GENERATOR = new TestCheapMapGenerator(IndexType.HASH);
	
	private static final TestSuite GENERATED_SUITE = MapTestSuiteBuilder.using(GENERATOR)
			.named(GENERATOR.getName())
			.withFeatures(GENERATOR.features())
			.createTestSuite();

	public static TestSuite suite() {
		return GENERATED_SUITE;
	}
}