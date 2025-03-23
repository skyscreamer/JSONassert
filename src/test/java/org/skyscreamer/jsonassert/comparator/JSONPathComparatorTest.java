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

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.JSONPathCustomization;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link JSONPathCustomization} comparator, at {@link org.json.JSONObject} level
 *
 * @author Shane B. (<a href="mailto:shane@wander.dev">shane@wander.dev</a>)
 */
public class JSONPathComparatorTest {

    @Test
    public void whenNoCustomizations() {
        JSONPathComparator comparator = new JSONPathComparator(JSONCompareMode.STRICT);

        JSONObject expected = new JSONObject();
        expected.put("foo", "bar");
        JSONArray expectedArray = new JSONArray();
        expectedArray.put(1);
        expectedArray.put(2);
        expected.put("array", expectedArray);

        JSONObject actual = new JSONObject();
        actual.put("foo", "bar");
        JSONArray actualArray = new JSONArray();
        actualArray.put(1);
        actualArray.put(2);
        actual.put("array", actualArray);

        JSONCompareResult result = new JSONCompareResult();

        comparator.compareJSON("", expected, actual, result);

        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenLenientAndNoCustomizations() {
        JSONPathComparator comparator = new JSONPathComparator(JSONCompareMode.LENIENT);

        JSONObject expected = new JSONObject();
        expected.put("foo", "bar");
        JSONArray expectedArray = new JSONArray();
        expectedArray.put(1);
        expectedArray.put(2);
        expected.put("array", expectedArray);

        JSONObject actual = new JSONObject();
        actual.put("foo", "bar");
        actual.put("baz", "bax");
        JSONArray actualArray = new JSONArray();
        actualArray.put(2);
        actualArray.put(1);
        actual.put("array", actualArray);

        JSONCompareResult result = new JSONCompareResult();

        comparator.compareJSON("", expected, actual, result);

        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenNonExtensibleAndNoCustomizations() {
        JSONPathComparator comparator = new JSONPathComparator(JSONCompareMode.NON_EXTENSIBLE);

        JSONObject expected = new JSONObject();
        expected.put("foo", "bar");
        JSONArray expectedArray = new JSONArray();
        expectedArray.put(1);
        expectedArray.put(2);
        expected.put("array", expectedArray);

        JSONObject actual = new JSONObject();
        actual.put("foo", "bar");
        actual.put("baz", "bax");
        JSONArray actualArray = new JSONArray();
        actualArray.put(2);
        actualArray.put(1);
        actual.put("array", actualArray);

        JSONCompareResult result = new JSONCompareResult();

        comparator.compareJSON("", expected, actual, result);

        assertTrue(result.getMessage(), result.failed());
        assertEquals(1, result.getFieldUnexpected().size());
        assertEquals("", result.getFieldUnexpected().get(0).getField());
        assertEquals("baz", result.getFieldUnexpected().get(0).getActual());
    }

    @Test
    public void whenStrictOrderAndNoCustomizations() {
        JSONPathComparator comparator = new JSONPathComparator(JSONCompareMode.STRICT_ORDER);

        JSONObject expected = new JSONObject();
        expected.put("foo", "bar");
        JSONArray expectedArray = new JSONArray();
        expectedArray.put(1);
        expectedArray.put(2);
        expected.put("array", expectedArray);

        JSONObject actual = new JSONObject();
        actual.put("foo", "bar");
        actual.put("baz", "bax");
        JSONArray actualArray = new JSONArray();
        actualArray.put(2);
        actualArray.put(1);
        actual.put("array", actualArray);

        JSONCompareResult result = new JSONCompareResult();

        comparator.compareJSON("", expected, actual, result);

        assertTrue(result.getMessage(), result.failed());
        assertEquals(2, result.getFieldFailures().size());
        assertEquals("array[0]", result.getFieldFailures().get(0).getField());
        assertEquals("array[1]", result.getFieldFailures().get(1).getField());
    }

    @Test
    public void whenStrictAndExcludeMismatch() {
        JSONPathComparator comparator = new JSONPathComparator(
                JSONCompareMode.STRICT,
                new JSONPathCustomization("$.baz", (expectedNode, actualNode) -> actualNode.equals("not expected!"))
        );

        JSONObject expected = new JSONObject();
        expected.put("foo", "bar");
        expected.put("baz", "expected!");
        JSONArray expectedArray = new JSONArray();
        expectedArray.put(1);
        expectedArray.put(2);
        expected.put("array", expectedArray);

        JSONObject actual = new JSONObject();
        actual.put("foo", "bar");
        actual.put("baz", "not expected!");
        JSONArray actualArray = new JSONArray();
        actualArray.put(1);
        actualArray.put(2);
        actual.put("array", actualArray);

        JSONCompareResult result = new JSONCompareResult();

        comparator.compareJSON("", expected, actual, result);
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenStrictAndOnMismatch() {
        JSONPathComparator comparator = new JSONPathComparator(
                JSONCompareMode.STRICT,
                new JSONPathCustomization("$.foo", (expectedNode, actualNode) -> true)
        );

        JSONObject expected = new JSONObject();
        expected.put("foo", "bar");
        expected.put("baz", "expected!");
        JSONArray expectedArray = new JSONArray();
        expectedArray.put(1);
        expectedArray.put(2);
        expected.put("array", expectedArray);

        JSONObject actual = new JSONObject();
        actual.put("foo", "bar");
        actual.put("baz", "not expected!");
        JSONArray actualArray = new JSONArray();
        actualArray.put(1);
        actualArray.put(2);
        actual.put("array", actualArray);

        JSONCompareResult result = new JSONCompareResult();

        comparator.compareJSON("", expected, actual, result);
        assertTrue(result.getMessage(), result.failed());
        assertEquals(1, result.getFieldFailures().size());
        assertEquals("baz", result.getFieldFailures().get(0).getField());
    }

    @Test
    public void whenStrictAndExcludeArrayMismatch() {
        JSONPathComparator comparator = new JSONPathComparator(
                JSONCompareMode.STRICT,
                new JSONPathCustomization("$.array[*]", (expectedNode, actualNode) -> true)
        );

        JSONObject expected = new JSONObject();
        expected.put("foo", "bar");
        JSONArray expectedArray = new JSONArray();
        expectedArray.put(1);
        expectedArray.put(2);
        expected.put("array", expectedArray);

        JSONObject actual = new JSONObject();
        actual.put("foo", "bar");
        JSONArray actualArray = new JSONArray();
        actualArray.put(4);
        actualArray.put(5);
        actual.put("array", actualArray);

        JSONCompareResult result = new JSONCompareResult();

        comparator.compareJSON("", expected, actual, result);
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenStrictAndFailureDueToOneItemInArrayMismatch() {
        JSONPathComparator comparator = new JSONPathComparator(
                JSONCompareMode.STRICT,
                new JSONPathCustomization("$.array[0:2]", (expectedNode, actualNode) -> true)
        );

        JSONObject expected = new JSONObject();
        expected.put("foo", "bar");
        JSONArray expectedArray = new JSONArray();
        expectedArray.put(1);
        expectedArray.put(2);
        expectedArray.put(3);
        expected.put("array", expectedArray);

        JSONObject actual = new JSONObject();
        actual.put("foo", "bar");
        JSONArray actualArray = new JSONArray();
        actualArray.put(1);
        actualArray.put(2);
        actualArray.put(99999);
        actual.put("array", actualArray);

        JSONCompareResult result = new JSONCompareResult();

        comparator.compareJSON("", expected, actual, result);
        assertTrue(result.getMessage(), result.failed());
        assertEquals(1, result.getFieldFailures().size());
        assertEquals("array[2]", result.getFieldFailures().get(0).getField());
    }

    @Test
    public void whenStrictAndFailureDueToInnerObjectMismatch() {
        JSONPathComparator comparator = new JSONPathComparator(
                JSONCompareMode.STRICT,
                new JSONPathCustomization("$.foo", (expectedNode, actualNode) -> true)
        );

        JSONObject expected = new JSONObject();
        expected.put("foo", "bar");
        JSONObject expectedInner = new JSONObject();
        expectedInner.put("foo", "bar");
        expected.put("inner", expectedInner);


        JSONObject actual = new JSONObject();
        actual.put("foo", "bar");
        JSONObject actualInner = new JSONObject();
        actualInner.put("foo", "mismatch!");
        actual.put("inner", actualInner);

        JSONCompareResult result = new JSONCompareResult();

        comparator.compareJSON("", expected, actual, result);
        assertTrue(result.getMessage(), result.failed());
        assertEquals(1, result.getFieldFailures().size());
        assertEquals("inner.foo", result.getFieldFailures().get(0).getField());
    }

    @Test
    public void whenStrictAndIgnoreNestedMismatch() {
        JSONPathComparator comparator = new JSONPathComparator(
                JSONCompareMode.STRICT,
                new JSONPathCustomization("$..inner", (expectedNode, actualNode) -> true)
        );

        JSONObject expected = new JSONObject();
        expected.put("foo", "bar");
        JSONObject expectedInner = new JSONObject();
        expectedInner.put("foo", "bar");
        expected.put("inner", expectedInner);


        JSONObject actual = new JSONObject();
        actual.put("foo", "bar");
        JSONObject actualInner = new JSONObject();
        actualInner.put("foo", "mismatch!");
        actual.put("inner", actualInner);

        JSONCompareResult result = new JSONCompareResult();

        comparator.compareJSON("", expected, actual, result);
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenMultiThreading() {
        // try to recreate a scenario where the inner cache/and inner
        // this.actual reference might get screwed up
        // (ideally test writers should use separate instances of JSONPathComparator
        // to not block on it in their tests)
        JSONPathComparator comparator = new JSONPathComparator(
                JSONCompareMode.STRICT,
                new JSONPathCustomization("$.foo", (expectedNode, actualNode) -> actualNode.equals("bar")),
                new JSONPathCustomization("$.inner.baz", (expectedNode, actualNode) -> actualNode.equals("match!"))
        );

        final JSONObject expected = new JSONObject();
        expected.put("foo", "bar");
        JSONObject expectedInner = new JSONObject();
        expectedInner.put("baz", "match!");
        expected.put("inner", expectedInner);

        List<MultithreadedTestCase> tests = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                tests.add(new MultithreadedTestCase("match!", false));
            } else {
                tests.add(new MultithreadedTestCase("mismatch!", true));
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CompletableFuture[] tasks = tests.stream().map(testRun ->
                CompletableFuture.runAsync(() -> {

                    JSONObject actual = new JSONObject();
                    actual.put("foo", "bar");
                    JSONObject actualInner = new JSONObject();
                    actualInner.put("baz", testRun.innerBaz);
                    actual.put("inner", actualInner);

                    JSONCompareResult result = new JSONCompareResult();

                    // this should be thread-safe
                    comparator.compareJSON("", expected, actual, result);

                    assertEquals(result.getMessage(), result.failed(), testRun.shouldFail);
                }, executorService)
        ).toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(tasks).join();
        // all the assertEquals above should succeed!
        executorService.shutdown();
    }

    private static class MultithreadedTestCase {
        private final String innerBaz;
        private final boolean shouldFail;

        private MultithreadedTestCase(String innerBaz, boolean shouldFail) {
            this.innerBaz = innerBaz;
            this.shouldFail = shouldFail;
        }
    }
}
