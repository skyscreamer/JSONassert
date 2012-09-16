package org.skyscreamer.jsonassert;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;

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
    public void reportsWrongSimpleValueCountInUnorderedArray() throws JSONException {
        JSONCompareResult result = compareJSON("[5, 5]", "[5, 7]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[]: Expected contains 2 5 actual contains 1 ; []: Contains 7, but not expected")));
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
