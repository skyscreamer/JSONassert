package org.skyscreamer.jsonassert.comparator;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import junit.framework.Assert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

/**
 * @author <a href="mailto:aiveeen@gmail.com">Ivan Zaytsev</a>
 *         2013-01-04
 */
public class CustomComparatorTest {

    private static class ArrayOfJsonObjectsComparator extends DefaultComparator {
        public ArrayOfJsonObjectsComparator(JSONCompareMode mode) {
            super(mode);
        }

        @Override
        public void compareJSONArray(String prefix, JSONArray expected, JSONArray actual, JSONCompareResult result) throws JSONException {
            compareJSONArrayOfJsonObjects(prefix, expected, actual, result);
        }
    }

    @Test
    public void testFullArrayComparison() throws Exception {
        JSONCompareResult compareResult = JSONCompare.compareJSON(
                "[{id:1}, {id:3}, {id:5}]",
                "[{id:1}, {id:3}, {id:6}, {id:7}]", new ArrayOfJsonObjectsComparator(JSONCompareMode.LENIENT)
        );

        Assert.assertTrue(compareResult.failed());
        String message = compareResult.getMessage().replaceAll("\n", "");
        assertThat(compareResult, failsWithMessage(equalTo("[{\"id\":1},{\"id\":3},{\"id\":5}]: Expected array size of 3 elements got 4 elements")));
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
