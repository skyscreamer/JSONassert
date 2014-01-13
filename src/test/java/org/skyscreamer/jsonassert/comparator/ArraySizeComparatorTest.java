package org.skyscreamer.jsonassert.comparator;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * Unit tests for ArraySizeComparator
 * 
 * @author Duncan Mackinder
 *
 */
public class ArraySizeComparatorTest {
	private static final String twoElementArray = "{a:[b,c]}";
	
	private void doTest(String expectedJSON, String actualJSON) throws JSONException
	{
		JSONAssert.assertEquals(expectedJSON, actualJSON, new ArraySizeComparator(JSONCompareMode.STRICT_ORDER));
	}

	@Test
	public void succeedsWhenExactSizeExpected() throws JSONException {
		doTest("{a:[2]}", twoElementArray);
	}

	@Test
	public void succeedsWhenSizeWithinExpectedRange() throws JSONException {
		doTest("{a:[1,3]}", twoElementArray);
	}

	@Test
	public void succeedsWhenSizeIsMinimumOfExpectedRange() throws JSONException {
		doTest("{a:[2,4]}", twoElementArray);
	}

	@Test
	public void succeedsWhenSizeIsMaximumOfExpectedRange() throws JSONException {
		doTest("{a:[1,2]}", twoElementArray);
	}

	@Test(expected=AssertionError.class)
	public void failsWhenExpectedArrayTooShort() throws JSONException {
		doTest("{a:[]}", twoElementArray);
	}

	@Test(expected=AssertionError.class)
	public void failsWhenExpectedArrayTooLong() throws JSONException {
		doTest("{a:[1,2,3]}", twoElementArray);
	}

	@Test(expected=AssertionError.class)
	public void failsWhenExpectedNotAllSimpleTypes() throws JSONException {
		doTest("{a:[{y:1},2]}", twoElementArray);
	}

	@Test(expected=AssertionError.class)
	public void failsWhenExpectedNotAllSimpleInteger() throws JSONException {
		doTest("{a:[Z,2]}", twoElementArray);
	}

	@Test(expected=AssertionError.class)
	public void failsWhenExpectedMinimumTooSmall() throws JSONException {
		doTest("{a:[-1,6]}", twoElementArray);
	}

	@Test(expected=AssertionError.class)
	public void failsWhenExpectedMaximumTooSmall() throws JSONException {
		doTest("{a:[8,6]}", twoElementArray);
	}

}
