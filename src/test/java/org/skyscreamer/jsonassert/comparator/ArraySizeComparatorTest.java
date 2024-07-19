/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.skyscreamer.jsonassert.comparator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.MessageFormat;

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
	
	private void doTest(String expectedJSON, String actualJSON)
	{
		JSONAssert.assertEquals(expectedJSON, actualJSON, new ArraySizeComparator(JSONCompareMode.STRICT_ORDER));
	}

	private void doFailingMatchTest(String expectedJSON, String actualJSON, String expectedMessagePattern) {
		try {
			doTest(expectedJSON, actualJSON);
		}
		catch (AssertionError e) {
			String failureMessage = MessageFormat.format("Exception message ''{0}'', does not match expected pattern ''{1}''", e.getMessage(), expectedMessagePattern);
			assertTrue(failureMessage, e.getMessage().matches(expectedMessagePattern));
			return;
		}
		fail("AssertionError not thrown");
	}

	@Test
	public void succeedsWhenExactSizeExpected() {
		doTest("{a:[2]}", twoElementArray);
	}

	@Test
	public void succeedsWhenSizeWithinExpectedRange() {
		doTest("{a:[1,3]}", twoElementArray);
	}

	@Test
	public void succeedsWhenSizeIsMinimumOfExpectedRange() {
		doTest("{a:[2,4]}", twoElementArray);
	}

	@Test
	public void succeedsWhenSizeIsMaximumOfExpectedRange() {
		doTest("{a:[1,2]}", twoElementArray);
	}

	@Test
	public void failsWhenExpectedArrayTooShort() {
		doFailingMatchTest("{a:[]}", twoElementArray, "a\\[\\]: invalid expectation: expected array should contain either 1 or 2 elements but contains 0 elements");
	}

	@Test
	public void failsWhenExpectedArrayTooLong() {
		doFailingMatchTest("{a:[1,2,3]}", twoElementArray, "a\\[\\]: invalid expectation: expected array should contain either 1 or 2 elements but contains 3 elements");
	}

	@Test
	public void failsWhenExpectedNotAllSimpleTypes() {
		doFailingMatchTest("{a:[{y:1},2]}", twoElementArray, "a\\[\\]: invalid expectation: minimum expected array size '\\{\"y\":1\\}' not a number");
	}

	@Test
	public void failsWhenExpectedMinimumTooSmall() {
		doFailingMatchTest("{a:[-1,6]}", twoElementArray, "a\\[\\]: invalid expectation: minimum expected array size '-1' negative");
	}

	@Test
	public void failsWhenExpectedMaximumTooSmall() {
		doFailingMatchTest("{a:[8,6]}", twoElementArray, "a\\[\\]: invalid expectation: maximum expected array size '6' less than minimum expected array size '8'");
	}

	@Test
	public void failsWhenExpectedArraySizeNotANumber() {
		doFailingMatchTest("{a:[X]}", twoElementArray, "a\\[\\]: invalid expectation: expected array size 'X' not a number");
	}

	@Test
	public void failsWhenFirstExpectedArrayElementNotANumber() {
		doFailingMatchTest("{a:[MIN,6]}", twoElementArray, "a\\[\\]: invalid expectation: minimum expected array size 'MIN' not a number");
	}

	@Test
	public void failsWhenSecondExpectedArrayElementNotANumber() {
		doFailingMatchTest("{a:[8,MAX]}", twoElementArray, "a\\[\\]: invalid expectation: maximum expected array size 'MAX' not a number");
	}

	@Test
	public void failsWhenActualArrayTooShort() {
		doFailingMatchTest("{a:[3]}", twoElementArray, "a\\[\\]\\s*Expected:\\s*array size of 3 elements\\s*got:\\s*2 elements\\s*");
	}

	@Test
	public void failsWhenActualArrayLongerThanExpectedLength() {
		doFailingMatchTest("{a:[1]}", twoElementArray, "a\\[\\]\\s*Expected:\\s*array size of 1 elements\\s*got:\\s*2 elements\\s*");
	}

	@Test
	public void failsWhenActualArrayLongerThanMaxOfExpectedRange() {
		doFailingMatchTest("{a:[0,1]}", twoElementArray, "a\\[\\]\\s*Expected:\\s*array size of 0 to 1 elements\\s*got:\\s*2 elements\\s*");
	}

	/*
	 * Following tests are copied from ArraySizeComparator JavaDoc and are include to ensure code as documented work as expected.
	 */

	@Test
	public void succeedsWhenActualArrayContainsExactly3Elements() {
		JSONAssert.assertEquals("{a:[3]}", "{a:[7, 8, 9]}", new ArraySizeComparator(JSONCompareMode.LENIENT));
	}

	@Test
	public void succeedsWhenActualArrayContainsBetween2And6Elements() {
		JSONAssert.assertEquals("{a:[2,6]}", "{a:[7, 8, 9]}", new ArraySizeComparator(JSONCompareMode.LENIENT));
	}

}
