package org.emmef.cheapsets;

import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import com.google.common.collect.testing.SetTestSuiteBuilder;

@RunWith(AllTests.class)
public class SubSetLimitedSetTest {

	public static TestSuite suite() {
		TestSuite generatedSuite = new TestSuite(SubSetLimitedSetTest.class.getSimpleName());
		
		for (IndexType type : IndexType.values()) {
			TestCheapSetGenerator generator = new TestCheapSetGenerator(type);
			
			TestSuite singleSuite = SetTestSuiteBuilder.using(generator)
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
}