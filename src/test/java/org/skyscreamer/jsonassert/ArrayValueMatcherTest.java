package org.skyscreamer.jsonassert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.MessageFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
	private static final String ARRAY_OF_JSONARRAYS = "{a:[[6,7,8],[9,10,11],[12,13,14],[19,20,21,22]]}";
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
		doTest("a", new ArrayValueMatcher<Object>(comparator, 0), "{a:[[6,7,8]]}", ARRAY_OF_JSONARRAYS);
	}

	@Test
	public void matchesSizeOfFirstThreeInnerArrays() throws JSONException {
		JSONComparator innerArraySizeComparator = new ArraySizeComparator(JSONCompareMode.STRICT_ORDER);
		doTest("a", new ArrayValueMatcher<Object>(innerArraySizeComparator, 0, 2), "{a:[[3]]}", ARRAY_OF_JSONARRAYS);
	}

	@Test
	public void failsWhenInnerArraySizeDoesNotMatch() throws JSONException {
		JSONComparator innerArraySizeComparator = new ArraySizeComparator(JSONCompareMode.STRICT_ORDER);
		doFailingMatchTest("a",
				new ArrayValueMatcher<Object>(innerArraySizeComparator),
				"{a:[[3]]}",
				ARRAY_OF_JSONARRAYS,
				"a\\[3\\]\\[\\]\\s*Expected:\\s*array size of 3 elements\\s*got:\\s*4 elements\\s*");
	}

	@Test
	public void failsWhenInnerJSONObjectArrayElementDoesNotMatch() throws JSONException {
		ArrayValueMatcher<Object> innerArrayValueMatcher = new ArrayValueMatcher<Object>(comparator, 1);
		JSONComparator innerArrayComparator = new CustomComparator(
				JSONCompareMode.LENIENT, new Customization("a[2]", innerArrayValueMatcher));
		doFailingMatchTest("a",
				new ArrayValueMatcher<Object>(innerArrayComparator, 2),  // tests inner array i.e. [12,13,14]
				"{a:[[99]]}",
				ARRAY_OF_JSONARRAYS,
				"a\\[2\\]\\[1\\]\\s*Expected:\\s*99\\s*got:\\s*13\\s*");
	}

	@Test
	public void matchesEveryElementOfJSONObjectArray() throws JSONException {
		doTest("a", new ArrayValueMatcher<Object>(comparator), "{a:[{type:row}]}", ARRAY_OF_JSONOBJECTS);
	}

	@Test
	public void failsWhenNotEveryElementOfJSONObjectArrayMatches() throws JSONException {
		doFailingMatchTest("a",
				new ArrayValueMatcher<Object>(comparator),
				"{a:[{background:white}]}",
				ARRAY_OF_JSONOBJECTS,
				"a\\[1\\]\\.background\\s*Expected:\\s*white\\s*got:\\s*grey\\s*;\\s*a\\[3\\]\\.background\\s*Expected:\\s*white\\s*got:\\s*grey\\s*");
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

	@Test
	public void failsWhenAppliedToNonArray() throws JSONException {
		try {
			doTest("a", new ArrayValueMatcher<Object>(comparator), "{a:[{background:white}]}", "{a:{attr1:value1,attr2:value2}}");
		}
		catch (IllegalArgumentException e) {
			assertEquals("Exception message", "ArrayValueMatcher applied to non-array actual value", e.getMessage());
			return;
		}
		fail("Did not throw IllegalArgumentException");
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

	/*
	 * Following tests contain copies of code quoted in ArrayValueMatcher JavaDoc and are included to verify that the exact code documented works as expected.
	 */
	@Test
	public void verifyIdAttributeOfFirstArrayElementMatches() throws JSONException {
		JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
		Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator, 0));
		JSONAssert.assertEquals("{a:[{id:1}]}", ARRAY_OF_JSONOBJECTS, new CustomComparator(JSONCompareMode.LENIENT, customization));
	}
	
	@Test
	public void verifyIdAttributeOfFirstArrayElementMatchesSimplifiedExpectedSyntax() throws JSONException {
		JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
		Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator, 0));
		JSONAssert.assertEquals("{a:{id:1}}", ARRAY_OF_JSONOBJECTS, new CustomComparator(JSONCompareMode.LENIENT, customization));
	}
	
	@Test
	public void verifyTypeAttributeOfSecondAndThirdElementMatchesRow() throws JSONException {
		JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
		Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator, 1, 2));
		JSONAssert.assertEquals("{a:[{type:row}]}", ARRAY_OF_JSONOBJECTS, new CustomComparator(JSONCompareMode.LENIENT, customization)); 
	}
	
	@Test
	public void verifyTypeAttributeOfEveryArrayElementMatchesRow() throws JSONException {
		 JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
		 Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator));
		 JSONAssert.assertEquals("{a:[{type:row}]}", ARRAY_OF_JSONOBJECTS, new CustomComparator(JSONCompareMode.LENIENT, customization));
	}
	
	@Test
	public void verifyEveryArrayElementWithCustomComparator() throws JSONException {
		// get length of array we will verify
		int aLength = ((JSONArray)((JSONObject)JSONParser.parseJSON(ARRAY_OF_JSONOBJECTS)).get("a")).length();
		// create array of customizations one for each array element
		RegularExpressionValueMatcher<Object> regExValueMatcher = new RegularExpressionValueMatcher<Object>("\\d+");  // matches one or more digits
		Customization[] customizations = new Customization[aLength];
		for (int i=0; i<aLength; i++) {
			String contextPath = "a["+i+"].id";
			customizations[i] = new Customization(contextPath, regExValueMatcher);
		}
		CustomComparator regExComparator = new CustomComparator(JSONCompareMode.STRICT_ORDER, customizations);

		ArrayValueMatcher<Object> regExArrayValueMatcher = new ArrayValueMatcher<Object>(regExComparator);
		Customization regExArrayValueCustomization = new Customization("a", regExArrayValueMatcher);
		CustomComparator regExCustomArrayValueComparator = new CustomComparator(JSONCompareMode.STRICT_ORDER, new Customization[] { regExArrayValueCustomization });
		JSONAssert.assertEquals("{a:[{id:X}]}", ARRAY_OF_JSONOBJECTS, regExCustomArrayValueComparator);
	}
	
	@Test
	public void verifyBackgroundAttributesOfEveryArrayElementAlternateBetweenWhiteAndGrey() throws JSONException {
		 JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
		 Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator));
		 JSONAssert.assertEquals("{a:[{background:white},{background:grey}]}", ARRAY_OF_JSONOBJECTS, new CustomComparator(JSONCompareMode.LENIENT, customization));
	}
	
	@Test
	public void verifyEveryElementOfArrayIsJSONArrayOfLength3() throws JSONException {
		 JSONComparator comparator = new ArraySizeComparator(JSONCompareMode.STRICT_ORDER);
		 Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator, 0, 2));
		 JSONAssert.assertEquals("{a:[[3]]}", ARRAY_OF_JSONARRAYS, new CustomComparator(JSONCompareMode.LENIENT, customization));
	}
	
	@Test
	public void verifySecondElementOfArrayIsJSONArrayWhoseFirstElementIs9() throws JSONException {
		 Customization innerCustomization = new Customization("a[1]", new ArrayValueMatcher<Object>(comparator, 0));
		 JSONComparator comparator = new CustomComparator(JSONCompareMode.LENIENT, innerCustomization);
		 Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator, 1));
		 JSONAssert.assertEquals("{a:[[9]]}", ARRAY_OF_JSONARRAYS, new CustomComparator(JSONCompareMode.LENIENT, customization));
	}
	
	@Test
	public void verifySecondElementOfArrayIsJSONArrayWhoseFirstElementIs9WithSimpliedExpectedString() throws JSONException {
		 Customization innerCustomization = new Customization("a[1]", new ArrayValueMatcher<Object>(comparator, 0));
		 JSONComparator comparator = new CustomComparator(JSONCompareMode.LENIENT, innerCustomization);
		 Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator, 1));
		 JSONAssert.assertEquals("{a:[9]}", ARRAY_OF_JSONARRAYS, new CustomComparator(JSONCompareMode.LENIENT, customization));
	}
	
	@Test
	public void verifySecondElementOfArrayIsJSONArrayWhoseFirstElementIs9WithEvenMoreSimpliedExpectedString() throws JSONException {
		 Customization innerCustomization = new Customization("a[1]", new ArrayValueMatcher<Object>(comparator, 0));
		 JSONComparator comparator = new CustomComparator(JSONCompareMode.LENIENT, innerCustomization);
		 Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator, 1));
		 JSONAssert.assertEquals("{a:9}", ARRAY_OF_JSONARRAYS, new CustomComparator(JSONCompareMode.LENIENT, customization));
	}
}
