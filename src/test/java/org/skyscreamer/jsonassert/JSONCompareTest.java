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

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;

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
        assertThat(result, failsWithMessage(equalTo("[]: Expected 1 values but got 0")));
    }

    @Test
    public void reportsArrayMissingExpectedElement() throws JSONException {
        JSONCompareResult result = compareJSON("[4]", "[7]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[]\nExpected: 4\n     but none found\n ; []\nUnexpected: 7\n")));
        assertEquals(result.getFieldMissing().size(), 1);
        assertEquals(result.getFieldUnexpected().size(), 1);
    }

    @Test
    public void reportsMismatchedFieldValues() throws JSONException {
        JSONCompareResult result = compareJSON("{\"id\": 3}", "{\"id\": 5}", LENIENT);
        assertThat(result, failsWithMessage(equalTo("id\nExpected: 3\n     got: 5\n")));
        assertThat(result, failsWithMessage(equalTo("id\nExpected: 3\n     got: 5\n")));
    }

    @Test
    public void reportsMissingField() throws JSONException {
        JSONCompareResult result = compareJSON("{\"obj\": {\"id\": 3}}", "{\"obj\": {}}", LENIENT);
        assertThat(result, failsWithMessage(equalTo("obj\nExpected: id\n     but none found\n")));
        assertEquals(result.getFieldMissing().size(), 1);
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
        assertThat(result, failsWithMessage(equalTo("id\nExpected: 3\n     got: null\n")));
    }

    @Test
    public void reportsUnexpectedNonNull() throws JSONException {
        JSONCompareResult result = compareJSON("{\"id\": null}", "{\"id\": \"abc\"}", LENIENT);
        assertThat(result, failsWithMessage(equalTo("id\nExpected: null\n     got: abc\n")));
    }

    @Test
    public void reportsUnexpectedFieldInNonExtensibleMode() throws JSONException {
        JSONCompareResult result = compareJSON("{\"obj\": {}}", "{\"obj\": {\"id\": 3}}", NON_EXTENSIBLE);
        assertThat(result, failsWithMessage(equalTo("obj\nUnexpected: id\n")));
        assertEquals(result.getFieldUnexpected().size(), 1);
    }

    @Test
    public void reportsMismatchedTypes() throws JSONException {
        JSONCompareResult result = compareJSON("{\"arr\":[]}", "{\"arr\":{}}", LENIENT);
        assertThat(result, failsWithMessage(equalTo("arr\nExpected: a JSON array\n     got: a JSON object\n")));
    }

    @Test
    public void reportsWrongSimpleValueCountInUnorderedArray() throws JSONException {
        JSONCompareResult result = compareJSON("[5, 5]", "[5, 7]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[]: Expected 2 occurrence(s) of 5 but got 1 occurrence(s) ; []\nUnexpected: 7\n")));
        assertEquals(result.getFieldUnexpected().size(), 1);
    }

    @Test
    public void reportsMissingJSONObjectWithUniqueKeyInUnorderedArray() throws JSONException {
        JSONCompareResult result = compareJSON("[{\"id\" : 3}]", "[{\"id\" : 5}]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[id=3]\nExpected: a JSON object\n     but none found\n ; " +
                "[id=5]\nUnexpected: a JSON object\n")));
        assertEquals(result.getFieldMissing().size(), 1);
        assertEquals(result.getFieldUnexpected().size(), 1);
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

    @Test
    public void reportsUnmatchedJSONArrayWhereExpectedContainsNonnullIntegerButActualContainsNullElement() throws JSONException {
        JSONCompareResult result = compareJSON("[ 3 ]", "[ null ]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[]\nExpected: 3\n     but none found\n ; " +
                "[]\nUnexpected: null\n")));
    }

    @Test
    public void reportsUnmatchedJSONArrayWhereExpectedContainsNullElementButActualContainsNonnullInteger() throws JSONException {
        JSONCompareResult result = compareJSON("[ null ]", "[ 3 ]", LENIENT);
        assertThat(result, failsWithMessage(equalTo("[]\nExpected: null\n     but none found\n ; " +
                "[]\nUnexpected: 3\n")));
    }

    @Test
    public void reportsStrictUnmatchedJSONArrayWhereExpectedContainsNonnullIntegerButActualContainsNullElement() throws JSONException {
        JSONCompareResult result = compareJSON("[ 3 ]", "[ null ]", STRICT);
        assertThat(result, failsWithMessage(equalTo("[0]\nExpected: 3\n     got: null\n")));
    }

    @Test
    public void reportsStrictUnmatchedJSONArrayWhereExpectedContainsNullButActualContainsNonnullInteger() throws JSONException {
        JSONCompareResult result = compareJSON("[ null ]", "[ 3 ]", STRICT);
        assertThat(result, failsWithMessage(equalTo("[0]\nExpected: null\n     got: 3\n")));
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
