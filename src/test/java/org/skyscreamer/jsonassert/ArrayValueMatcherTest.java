package org.skyscreamer.jsonassert;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.MessageFormat;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.comparator.ArraySizeComparator;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

/**
 * Unit tests for ArrayValueMatcher
 * 
 * @author Duncan Mackinder
 *
 */
public class ArrayValueMatcherTest {

	private static final String ARRAY_OF_JSONOBJECTS = "{a:[{background:white,id:1,type:row},{background:grey,id:2,type:row},{background:white,id:3,type:row},{background:grey,id:4,type:row}]}";
	private static final String ARRAY_OF_INTEGERS = "{a:[1,2,3,4,5]}";
	private static final String ARRAY_OF_JSONARRAYS = "{a:[[7,8],[9,10],[12,13],[19,20,21]]}";
	private static final JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);

	private void doTest(String jsonPath, ArrayValueMatcher<Object> arrayValueMatcher, String expectedJSON,
			String actualJSON) throws JSONException {
		Customization customization = new Customization(jsonPath, arrayValueMatcher);
		JSONAssert.assertEquals(expectedJSON, actualJSON, new CustomComparator(JSONCompareMode.LENIENT, customization));
	}

	private void doFailingMatchTest(String jsonPath, ArrayValueMatcher<Object> arrayValueMatcher, String expectedJSON, String actualJSON, String expectedMessagePattern) throws JSONException {
		try {
			doTest(jsonPath, arrayValueMatcher, expectedJSON, actualJSON);
		}
		catch (AssertionError e) {
			String failureMessage = MessageFormat.format("Exception message ''{0}'', does not match expected pattern ''{1}''", e.getMessage(), expectedMessagePattern);
			assertTrue(failureMessage, e.getMessage().matches(expectedMessagePattern));
			return;
		}
		fail("AssertionError not thrown");
	}

	@Test
	public void matchesSecondElementOfJSONObjectArray() throws JSONException {
		doTest("a", new ArrayValueMatcher<Object>(comparator, 1), "{a:[{background:grey,id:2,type:row}]}", ARRAY_OF_JSONOBJECTS);
	}

	@Test
	public void failsWhenSecondElementOfJSONObjectArrayDoesNotMatch() throws JSONException {
		doFailingMatchTest("a",
				new ArrayValueMatcher<Object>(comparator, 1),
				"{a:[{background:DOES_NOT_MATCH,id:2,type:row}]}",
				ARRAY_OF_JSONOBJECTS,
				"a\\[1\\]\\.background\\s*Expected:\\s*DOES_NOT_MATCH\\s*got:\\s*grey\\s*");
	}

	@Test
	public void failsWhenThirdElementOfJSONObjectArrayDoesNotMatchInMultiplePlaces() throws JSONException {
		doFailingMatchTest("a",
				new ArrayValueMatcher<Object>(comparator, 2),
				"{a:[{background:DOES_NOT_MATCH,id:3,type:WRONG_TYPE}]}",
				ARRAY_OF_JSONOBJECTS,
				"a\\[2\\]\\.background\\s*Expected:\\s*DOES_NOT_MATCH\\s*got:\\s*white\\s*;\\s*a\\[2\\]\\.type\\s*Expected:\\s*WRONG_TYPE\\s*got:\\s*row\\s*");
	}

	@Test
	public void failsWhenTwoElementsOfJSONObjectArrayDoNotMatch() throws JSONException {
		doFailingMatchTest("a",
				new ArrayValueMatcher<Object>(comparator, 1, 2),
				"{a:[{background:DOES_NOT_MATCH,id:2,type:row},{background:white,id:3,type:WRONG_TYPE}]}",
				ARRAY_OF_JSONOBJECTS,
				"a\\[1\\]\\.background\\s*Expected:\\s*DOES_NOT_MATCH\\s*got:\\s*grey\\s*;\\s*a\\[2\\]\\.type\\s*Expected:\\s*WRONG_TYPE\\s*got:\\s*row\\s*");
	}

	@Test
	public void matchesThirdElementOfSimpleValueArray() throws JSONException {
		doTest("a", new ArrayValueMatcher<Object>(comparator, 2), "{a:[3]}", ARRAY_OF_INTEGERS);
	}

	@Test
	public void failsWhenTwoElementOfSimpleValueArrayDoNotMatch() throws JSONException {
		doFailingMatchTest("a", new ArrayValueMatcher<Object>(comparator, 3, 4), "{a:[3,4]}", ARRAY_OF_INTEGERS,
				"a\\[3\\]\\s*Expected:\\s3\\s*got:\\s*4\\s*;\\s*a\\[4\\]\\s*Expected:\\s*4\\s*got:\\s*5\\s*");
	}

	@Test
	public void matchesFirstElementOfArrayOfJSONArrays() throws JSONException {
		doTest("a", new ArrayValueMatcher<Object>(comparator, 0), "{a:[[7,8]]}", ARRAY_OF_JSONARRAYS);
	}

	@Test
	public void matchesSizeOfFirstThreeInnerArrays() throws JSONException {
		JSONComparator innerArraySizeComparator = new ArraySizeComparator(JSONCompareMode.STRICT_ORDER);
		doTest("a", new ArrayValueMatcher<Object>(innerArraySizeComparator, 0, 2), "{a:[[2]]}", ARRAY_OF_JSONARRAYS);
	}

	@Test
	public void failsWhenInnerArraySizeDoesNotMatch() throws JSONException {
		JSONComparator innerArraySizeComparator = new ArraySizeComparator(JSONCompareMode.STRICT_ORDER);
		doFailingMatchTest("a",
				new ArrayValueMatcher<Object>(innerArraySizeComparator),
				"{a:[[2]]}",
				ARRAY_OF_JSONARRAYS,
				"a\\[3\\]\\[\\]:\\s*Expected 2 values but got 3");
	}

	@Test
	public void failsWhenInnerJSONObjectArrayElementDoesNotMatch() throws JSONException {
		ArrayValueMatcher<Object> innerArrayValueMatcher = new ArrayValueMatcher<Object>(comparator, 1);
		JSONComparator innerArrayComparator = new CustomComparator(
				JSONCompareMode.LENIENT, new Customization("a[2]", innerArrayValueMatcher));
		doFailingMatchTest("a",
				new ArrayValueMatcher<Object>(innerArrayComparator, 2),  // tests inner array i.e. [12,13]
				"{a:[[14]]}",
				ARRAY_OF_JSONARRAYS,
				"a\\[2\\]\\[1\\]\\s*Expected:\\s*14\\s*got:\\s*13\\s*");
	}

	@Test
	public void matchesEveryElementOfJSONObjectArray() throws JSONException {
		doTest("a", new ArrayValueMatcher<Object>(comparator), "{a:[{type:row}]}", ARRAY_OF_JSONOBJECTS);
	}

	@Test
	public void matchesEveryElementOfJSONObjectArrayWhenRangeTooLarge() throws JSONException {
		doTest("a", new ArrayValueMatcher<Object>(comparator, 0, 500), "{a:[{type:row}]}", ARRAY_OF_JSONOBJECTS);
	}

	@Test
	public void matchesElementPairsStartingFromElement1OfJSONObjectArrayWhenRangeTooLarge() throws JSONException {
		doTest("a", new ArrayValueMatcher<Object>(comparator, 1, 500), "{a:[{background:grey},{background:white}]}", ARRAY_OF_JSONOBJECTS);
	}

	@Test
	public void matchesElementPairsStartingFromElement0OfJSONObjectArrayWhenRangeTooLarge() throws JSONException {
		doTest("a", new ArrayValueMatcher<Object>(comparator), "{a:[{background:white},{background:grey}]}", ARRAY_OF_JSONOBJECTS);
	}
	
	/*
	 * Following tests verify the ability to match an element containing either
	 * a simple value or a JSON object against simple value or JSON object
	 * without requiring expected value to be wrapped in an array reducing
	 * slightly the syntactic load on teh test author & reader.
	 */

	@Test
	public void simpleValueMatchesSecondElementOfJSONObjectArray() throws JSONException {
		doTest("a", new ArrayValueMatcher<Object>(comparator, 3), "{a:4}", ARRAY_OF_INTEGERS);
	}

	@Test
	public void jsonObjectMatchesSecondElementOfJSONObjectArray() throws JSONException {
		doTest("a", new ArrayValueMatcher<Object>(comparator, 1), "{a:{background:grey,id:2,type:row}}", ARRAY_OF_JSONOBJECTS);
	}

}
