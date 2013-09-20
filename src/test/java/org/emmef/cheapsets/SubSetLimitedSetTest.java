package org.emmef.cheapsets;

import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import junit.framework.TestSuite;

import com.google.common.collect.testing.SetTestSuiteBuilder;

@RunWith(AllTests.class)
public class SubSetLimitedSetTest {
	private static final TestCheapSetGenerator GENERATOR = new TestCheapSetGenerator(IndexType.AUTO);
	
	private static final TestSuite GENERATED_SUITE = SetTestSuiteBuilder.using(GENERATOR)
			.named(GENERATOR.getName())
			.withFeatures(GENERATOR.features())
			.createTestSuite();

	public static TestSuite suite() {
		return GENERATED_SUITE;
	}
}