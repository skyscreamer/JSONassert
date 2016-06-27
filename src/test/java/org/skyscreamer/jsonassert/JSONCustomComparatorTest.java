package org.skyscreamer.jsonassert;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

import static org.junit.Assert.assertEquals;
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
    public void whenPathMatchesInCustomizationThenCallCustomMatcher() throws JSONException {
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("first", comparator));
        JSONCompareResult result = compareJSON(expected, actual, jsonCmp);
        assertTrue(result.getMessage(),  result.passed());
        assertEquals(1, comparatorCallCount);
    }

    @Test
    public void whenDeepPathMatchesCallCustomMatcher() throws JSONException {
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("outer.inner.value", comparator));
        JSONCompareResult result = compareJSON(deepExpected, deepActual, jsonCmp);
        assertTrue(result.getMessage(), result.passed());
        assertEquals(1, comparatorCallCount);
    }

    @Test
    public void whenSimpleWildcardPathMatchesCallCustomMatcher() throws JSONException {
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("foo.*.baz", comparator));
        JSONCompareResult result = compareJSON(simpleWildcardExpected, simpleWildcardActual, jsonCmp);
        assertTrue(result.getMessage(), result.passed());
        assertEquals(2, comparatorCallCount);
    }

    @Test
    public void whenDeepWildcardPathMatchesCallCustomMatcher() throws JSONException {
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("root.**.baz", comparator));
        JSONCompareResult result = compareJSON(deepWildcardExpected, deepWildcardActual, jsonCmp);
        assertTrue(result.getMessage(), result.passed());
        assertEquals(3, comparatorCallCount);
    }

    @Test
    public void whenRootDeepWildcardPathMatchesCallCustomMatcher() throws JSONException {
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new Customization("**.baz", comparator));
        JSONCompareResult result = compareJSON(rootDeepWildcardExpected, rootDeepWildcardActual, jsonCmp);
        assertTrue(result.getMessage(), result.passed());
        assertEquals(4, comparatorCallCount);
    }
}
