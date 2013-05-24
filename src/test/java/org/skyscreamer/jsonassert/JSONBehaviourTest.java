package org.skyscreamer.jsonassert;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.json.JSONException;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.skyscreamer.jsonassert.Behavior.STRICT;
import static org.skyscreamer.jsonassert.Customization.customization;
import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;

public class JSONBehaviourTest {

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

    EqualsComparator<Object> comparator = new EqualsComparator<Object>() {
        @Override
        public boolean equal(Object o1, Object o2) {
            return o1.toString().equals("actual") && o2.toString().equals("expected");
        }
    };

    @Test
    public void whenPathMatchesInCustomizationThenCallCustomMatcher() throws JSONException {
        Behavior behavior = STRICT.with(customization("first", comparator));
        JSONCompareResult result = compareJSON(expected, actual, behavior);
        assertTrue(result.getMessage(),  result.passed());
    }

    @Test
    public void whenDeepPathMatchesCallCustomMatcher() throws JSONException {
        Behavior behavior = STRICT.with(customization("outer.inner.value", comparator));
        JSONCompareResult result = compareJSON(deepExpected, deepActual, behavior);
        assertTrue(result.getMessage(), result.passed());
    }

}
