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

import org.junit.Test;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;

public class JSONCustomComparatorTest {

    String actual = "{\"first\":\"actual\", \"second\":1}";
    String expected = "{\"first\":\"expected\", \"second\":1}";

    String deepActual = "{\n" +
            "    \"outer\":\n" +
            "    {\n" +
            "        \"inner\":\n" +
            "        {\n" +
            "            \"value\": \"actual\",\n" +
            "            \"otherValue\": \"foo\"\n" +
            "        }\n" +
            "    }\n" +
            "}";
    String deepExpected = "{\n" +
            "    \"outer\":\n" +
            "    {\n" +
            "        \"inner\":\n" +
            "        {\n" +
            "            \"value\": \"expected\",\n" +
            "            \"otherValue\": \"foo\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    String simpleWildcardActual = "{\n" +
            "  \"foo\": {\n" +
            "    \"bar1\": {\n" +
            "      \"baz\": \"actual\"\n" +
            "    },\n" +
            "    \"bar2\": {\n" +
            "      \"baz\": \"actual\"\n" +
            "    }\n" +
            "  }\n" +
            "}";
    String simpleWildcardExpected = "{\n" +
            "  \"foo\": {\n" +
            "    \"bar1\": {\n" +
            "      \"baz\": \"expected\"\n" +
            "    },\n" +
            "    \"bar2\": {\n" +
            "      \"baz\": \"expected\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

    String deepWildcardActual = "{\n" +
            "  \"root\": {\n" +
            "    \"baz\": \"actual\",\n" +
            "    \"foo\": {\n" +
            "      \"baz\": \"actual\",\n" +
            "      \"bar\": {\n" +
            "        \"baz\": \"actual\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
    String deepWildcardExpected = "{\n" +
            "  \"root\": {\n" +
            "    \"baz\": \"expected\",\n" +
            "    \"foo\": {\n" +
            "      \"baz\": \"expected\",\n" +
            "      \"bar\": {\n" +
            "        \"baz\": \"expected\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    String rootDeepWildcardActual = "{\n" +
            "  \"baz\": \"actual\",\n" +
            "  \"root\": {\n" +
            "    \"baz\": \"actual\",\n" +
            "    \"foo\": {\n" +
            "      \"baz\": \"actual\",\n" +
            "      \"bar\": {\n" +
            "        \"baz\": \"actual\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
    String rootDeepWildcardExpected = "{\n" +
            "  \"baz\": \"expected\",\n" +
            "  \"root\": {\n" +
            "    \"baz\": \"expected\",\n" +
            "    \"foo\": {\n" +
            "      \"baz\": \"expected\",\n" +
            "      \"bar\": {\n" +
            "        \"baz\": \"expected\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    int comparatorCallCount = 0;
    ValueMatcher<Object> comparator = new ValueMatcher<Object>() {
        @Override
        public boolean equal(Object o1, Object o2) {
            comparatorCallCount++;
            return o1.toString().equals("actual") && o2.toString().equals("expected");
        }
    };

    @Test
    public void whenPathMatchesInCustomizationThenCallCustomMatcher() {
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("first", comparator));
        JSONCompareResult result = compareJSON(expected, actual, jsonCmp);
        assertTrue(result.getMessage(),  result.passed());
        assertEquals(1, comparatorCallCount);
    }

    @Test
    public void whenDeepPathMatchesCallCustomMatcher() {
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("outer.inner.value", comparator));
        JSONCompareResult result = compareJSON(deepExpected, deepActual, jsonCmp);
        assertTrue(result.getMessage(), result.passed());
        assertEquals(1, comparatorCallCount);
    }

    @Test
    public void whenSimpleWildcardPathMatchesCallCustomMatcher() {
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("foo.*.baz", comparator));
        JSONCompareResult result = compareJSON(simpleWildcardExpected, simpleWildcardActual, jsonCmp);
        assertTrue(result.getMessage(), result.passed());
        assertEquals(2, comparatorCallCount);
    }

    @Test
    public void whenTrailingWildcardPathMatchesCallCustomMatcher() {
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("foo.*.*", comparator));
        JSONCompareResult result = compareJSON(simpleWildcardExpected, simpleWildcardActual, jsonCmp);
        assertTrue(result.getMessage(), result.passed());
        assertEquals(2, comparatorCallCount);
    }

    @Test
    public void whenTrailingWildcardMatchesOnlyOneLevel() {
        Customization customization = new Customization("foo.*", comparator);
        assertTrue(customization.appliesToPath("foo.bar"));
        assertFalse(customization.appliesToPath("foo.bar.baz"));
        assertFalse(customization.appliesToPath("foo."));
        assertFalse(customization.appliesToPath("other.bar"));
    }

    @Test
    public void whenTrailingDeepWildcardMatchesMultipleLevels() {
        Customization customization = new Customization("foo.**", comparator);
        assertTrue(customization.appliesToPath("foo.bar"));
        assertTrue(customization.appliesToPath("foo.bar.baz"));
        assertFalse(customization.appliesToPath("foo."));
        assertFalse(customization.appliesToPath("other.bar"));
    }

    @Test
    public void whenWildcardOnlyPathsMatch() {
        Customization simple = new Customization("*", comparator);
        assertTrue(simple.appliesToPath("foo"));
        assertFalse(simple.appliesToPath("foo.bar"));
        assertFalse(simple.appliesToPath(""));

        Customization deep = new Customization("**", comparator);
        assertTrue(deep.appliesToPath("foo"));
        assertTrue(deep.appliesToPath("foo.bar"));
        assertFalse(deep.appliesToPath(""));
    }

    @Test
    public void whenTripleWildcardMatchesMultipleLevels() {
        Customization customization = new Customization("foo.***", comparator);
        assertTrue(customization.appliesToPath("foo.b"));
        assertTrue(customization.appliesToPath("foo.bar"));
        assertTrue(customization.appliesToPath("foo.bar.baz"));
        assertFalse(customization.appliesToPath("foo."));
    }

    @Test
    public void whenTripleWildcardMatchesBeforeProperty() {
        Customization customization = new Customization("foo.***.bar", comparator);
        assertTrue(customization.appliesToPath("foo.bar"));
        assertTrue(customization.appliesToPath("foo.b.bar"));
        assertTrue(customization.appliesToPath("foo.b.c.bar"));
        assertFalse(customization.appliesToPath("foo.baz"));
    }

    @Test
    public void whenTrailingWildcardFollowsTripleWildcard() {
        Customization customization = new Customization("foo.***.*", comparator);
        assertTrue(customization.appliesToPath("foo.b"));
        assertTrue(customization.appliesToPath("foo.b.c"));
        assertFalse(customization.appliesToPath("foo."));
    }

    @Test
    public void whenDeepWildcardPathMatchesCallCustomMatcher() {
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("root.**.baz", comparator));
        JSONCompareResult result = compareJSON(deepWildcardExpected, deepWildcardActual, jsonCmp);
        assertTrue(result.getMessage(), result.passed());
        assertEquals(3, comparatorCallCount);
    }

    @Test
    public void whenRootDeepWildcardPathMatchesCallCustomMatcher() {
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("**.baz", comparator));
        JSONCompareResult result = compareJSON(rootDeepWildcardExpected, rootDeepWildcardActual, jsonCmp);
        assertTrue(result.getMessage(), result.passed());
        assertEquals(4, comparatorCallCount);
    }
}
