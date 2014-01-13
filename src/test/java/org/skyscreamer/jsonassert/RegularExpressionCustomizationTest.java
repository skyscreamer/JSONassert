package org.skyscreamer.jsonassert;

/**
 * Unit tests for RegularExpressionCustomization
 * 
 * @author Duncan Mackinder
 * 
 */
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;

public class RegularExpressionCustomizationTest {

    String actual = "{\"first\":\"actual\", \"second\":1, \"third\":\"actual\"}";
    String expected = "{\"first\":\"expected\", \"second\":1, \"third\":\"expected\"}";

    String deepActual = "{\n" +
            "    \"outer\":\n" +
            "    {\n" +
            "        \"inner\":\n" +
            "		 [\n" +
            "	        {\n" +
            "   	         \"value\": \"actual\",\n" +
            "       	     \"otherValue\": \"foo\"\n" +
            "        	},\n" +
            "	        {\n" +
            "   	         \"value\": \"actual\",\n" +
            "       	     \"otherValue\": \"bar\"\n" +
            "        	},\n" +
            "	        {\n" +
            "   	         \"value\": \"actual\",\n" +
            "       	     \"otherValue\": \"baz\"\n" +
            "        	}\n" +
            "		 ]\n" +
            "    }\n" +
            "}";
    String deepExpected = "{\n" +
            "    \"outer\":\n" +
            "    {\n" +
            "        \"inner\":\n" +
            "		 [\n" +
            "	        {\n" +
            "   	         \"value\": \"expected\",\n" +
            "       	     \"otherValue\": \"foo\"\n" +
            "        	},\n" +
            "	        {\n" +
            "   	         \"value\": \"expected\",\n" +
            "       	     \"otherValue\": \"bar\"\n" +
            "        	},\n" +
            "	        {\n" +
            "   	         \"value\": \"expected\",\n" +
            "       	     \"otherValue\": \"baz\"\n" +
            "        	}\n" +
            "		 ]\n" +
            "    }\n" +
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
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new RegularExpressionCustomization(".*i.*", comparator)); // will match first and third but not second
        JSONCompareResult result = compareJSON(expected, actual, jsonCmp);
        assertTrue(result.getMessage(),  result.passed());
        assertEquals(2, comparatorCallCount);
    }

    @Test
    public void whenDeepPathMatchesCallCustomMatcher() throws JSONException {
        JSONComparator jsonCmp = new CustomComparator(JSONCompareMode.STRICT, new RegularExpressionCustomization("outer\\.inner\\[.*\\]\\.value", comparator));
        JSONCompareResult result = compareJSON(deepExpected, deepActual, jsonCmp);
        assertTrue(result.getMessage(), result.passed());
        assertEquals(3, comparatorCallCount);
    }

}
