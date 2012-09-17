package org.skyscreamer.jsonassert;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.json.JSONException;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

/**
 * Unit tests for {@code JSONCompare}.
 */
public class JSONCompareTest {
    @Test
    public void succeedsWithEmptyArrays() throws JSONException {
        assertTrue(compareJSON("[]", "[]", LENIENT).passed());
    }

    @Test
    public void reportsArraysOfUnequalLength() throws JSONException {
        JSONCompareResult result = compareJSON("[4]", "[]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[]: Expected 1 values and got 0")));
    }

    @Test
    public void reportsArrayMissingExpectedElement() throws JSONException {
        JSONCompareResult result = compareJSON("[4]", "[7]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[]: Expected 4, but not found ; []: Contains 7, but not expected")));
    }

    @Test
    public void reportsMismatchedFieldValues() throws JSONException {
        JSONCompareResult result = compareJSON("{\"id\": 3}", "{\"id\": 5}", LENIENT);
        assertThat(result, failsWithMessage(equalTo("id\nExpected: 3\n     got: 5\n")));
    }

    @Test
    public void reportsUnexpectedArrayWhenExpectingObject() throws JSONException {
        JSONCompareResult result = compareJSON("{}", "[]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("\nExpected: a JSON object\n     got: a JSON array\n")));
    }

    @Test
    public void reportsUnexpectedObjectWhenExpectingArray() throws JSONException {
        JSONCompareResult result = compareJSON("[]", "{}", LENIENT);
        assertThat(result, failsWithMessage(equalTo("\nExpected: a JSON array\n     got: a JSON object\n")));
    }

    @Test
    public void reportsUnexpectedNull() throws JSONException {
        JSONCompareResult result = compareJSON("{\"id\": 3}", "{\"id\": null}", LENIENT);
        assertThat(result, failsWithMessage(equalTo("id: expected java.lang.Integer, but got null")));
    }

    @Test
    public void reportsUnexpectedNonNull() throws JSONException {
        JSONCompareResult result = compareJSON("{\"id\": null}", "{\"id\": \"abc\"}", LENIENT);
        assertThat(result, failsWithMessage(equalTo("id: expected null, but got a string")));
    }

    @Test
    public void reportsUnexpectedFieldInNonExtensibleMode() throws JSONException {
        JSONCompareResult result = compareJSON("{\"obj\": {}}", "{\"obj\": {\"id\": 3}}", NON_EXTENSIBLE);
        assertThat(result, failsWithMessage(equalTo("Got unexpected field: obj.id")));
    }

    @Test
    public void reportsMismatchedTypes() throws JSONException {
        JSONCompareResult result = compareJSON("{\"arr\":[]}", "{\"arr\":{}}", LENIENT);
        assertThat(result, failsWithMessage(equalTo("Values of arr have different types: expected an array, but got an object")));
    }

    @Test
    public void reportsWrongSimpleValueCountInUnorderedArray() throws JSONException {
        JSONCompareResult result = compareJSON("[5, 5]", "[5, 7]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[]: Expected 2 occurrence(s) of 5 but got 1 occurrence(s) ; []: Contains 7, but not expected")));
    }

    @Test
    public void reportsMissingJSONObjectWithUniqueKeyInUnorderedArray() throws JSONException {
        JSONCompareResult result = compareJSON("[{\"id\" : 3}]", "[{\"id\" : 5}]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[]: Expected but did not find object where id=3 ; " +
                "[]: Contains object where id=5, but not expected")));
    }

    @Test
    public void reportsUnmatchedJSONObjectInUnorderedArray() throws JSONException {
        JSONCompareResult result = compareJSON("[{\"address\" : {\"street\" : \"Acacia Avenue\"}}]", "[{\"age\" : 23}]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[0] Could not find match for element {\"address\":{\"street\":\"Acacia Avenue\"}}")));
    }

    @Test
    public void succeedsWithNestedJSONObjectsInUnorderedArray() throws JSONException {
        assertTrue(compareJSON("[{\"address\" : {\"street\" : \"Acacia Avenue\"}}, 5]", "[5, {\"address\" : {\"street\" : \"Acacia Avenue\"}}]", LENIENT).passed());
    }

    @Test
    public void succeedsWithJSONObjectsWithNonUniqueKeyInUnorderedArray() throws JSONException {
        String jsonDocument = "[{\"age\" : 43}, {\"age\" : 43}]";
        assertTrue(compareJSON(jsonDocument, jsonDocument, LENIENT).passed());
    }

    @Test
    public void succeedsWithSomeNestedJSONObjectsInUnorderedArray() throws JSONException {
        String jsonDocument = "[{\"age\" : 43}, {\"age\" : {\"years\" : 43}}]";
        assertTrue(compareJSON(jsonDocument, jsonDocument, LENIENT).passed());
    }

    @Test
    public void reportsUnmatchesIntegerValueInUnorderedArrayContainingJSONObject() throws JSONException {
        JSONCompareResult result = compareJSON("[{\"address\" : {\"street\" : \"Acacia Avenue\"}}, 5]", "[{\"address\" : {\"street\" : \"Acacia Avenue\"}}, 2]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[1] Could not find match for element 5")));
    }

    @Test
    public void reportsUnmatchedJSONArrayWhereOnlyExpectedContainsJSONObjectWithUniqueKey() throws JSONException {
        JSONCompareResult result = compareJSON("[{\"id\": 3}]", "[{}]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[0] Could not find match for element {\"id\":3}")));
    }

    @Test
    public void reportsUnmatchedJSONArrayWhereExpectedContainsJSONObjectWithUniqueKeyButActualContainsElementOfOtherType() throws JSONException {
        JSONCompareResult result = compareJSON("[{\"id\": 3}]", "[5]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[0] Could not find match for element {\"id\":3}")));
    }

    private Matcher<JSONCompareResult> failsWithMessage(final Matcher<String> expectedMessage) {
        return new TypeSafeMatcher<JSONCompareResult>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("a failed comparison with message ").appendDescriptionOf(expectedMessage);
            }

            @Override
            public boolean matchesSafely(JSONCompareResult item) {
                return item.failed() && expectedMessage.matches(item.getMessage());
            }
        };
    }
}
