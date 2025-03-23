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

package org.skyscreamer.jsonassert;

import org.json.JSONArray;
import org.junit.Test;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import static org.junit.Assert.*;

/**
 * Tests for the shared utility class {@link CustomizationEvaluator}
 *
 * @author Shane B. (<a href="mailto:shane@wander.dev">shane@wander.dev</a>)
 */
public class CustomizationEvaluatorTest {

    @Test
    public void testMatchesTrue() {
        boolean result = CustomizationEvaluator.matches((a, b) -> true, "foo", "any", "any", new JSONCompareResult());
        assertTrue(result);
    }

    @Test
    public void testMatchesFalse() {
        boolean result = CustomizationEvaluator.matches((a, b) -> false, "foo", "any", "any", new JSONCompareResult());
        assertFalse(result);
    }

    @Test
    public void testMatchesTrueLocationAware() {
        JSONArray expected = new JSONArray();
        expected.put("foo");
        JSONArray actual = new JSONArray();
        actual.put("foo");
        JSONCompareResult result = new JSONCompareResult();
        boolean returnedResult = CustomizationEvaluator.matches(
                new ArrayValueMatcher<>(
                        new CustomComparator(
                                JSONCompareMode.LENIENT,
                                new Customization("any[0]", (a, b) -> true)), 0, 1),
                "any",
                actual,
                expected,
                result
        );
        assertTrue(returnedResult);
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void testMatchesFalseLocationAware() {
        JSONArray expected = new JSONArray();
        expected.put("foo");
        JSONArray actual = new JSONArray();
        actual.put("foo");
        JSONCompareResult result = new JSONCompareResult();
        boolean returnedResult = CustomizationEvaluator.matches(
                new ArrayValueMatcher<>(
                        new CustomComparator(
                                JSONCompareMode.LENIENT,
                                new Customization("any[0]", (a, b) -> false)), 0, 1),
                "any",
                actual,
                expected,
                result
        );
        assertTrue(returnedResult);
        assertTrue(result.getMessage(), result.failed());
        assertEquals(1, result.getFieldFailures().size());
        assertEquals("any[0]", result.getFieldFailures().get(0).getField());
    }
}
