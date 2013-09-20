package org.emmef.cheapsets;

import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import com.google.common.collect.testing.MapTestSuiteBuilder;

@RunWith(AllTests.class)
public class SubSetLimitedMapTest {
	private static final TestSuite GENERATED_SUITE = generateTestSuiteForAllIndexTypes();
	
	private static TestSuite generateTestSuiteForAllIndexTypes() {
		TestSuite generatedSuite = new TestSuite(SubSetLimitedMapTest.class.getSimpleName());
		
		for (IndexType type : IndexType.values()) {
			TestCheapMapGenerator generator = new TestCheapMapGenerator(type);
			
			TestSuite singleSuite = MapTestSuiteBuilder.using(generator)
					.named(generator.getName())
					.withFeatures(generator.features())
					.createTestSuite();
			
			Enumeration<Test> tests = singleSuite.tests();
			
			while (tests.hasMoreElements()) {
				generatedSuite.addTest(tests.nextElement());
			}
		}
		return generatedSuite;
	}
	

	public static TestSuite suite() {
		return GENERATED_SUITE;
	}
}