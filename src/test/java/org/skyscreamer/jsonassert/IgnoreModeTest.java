package org.skyscreamer.jsonassert;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.internal.matchers.TypeSafeMatcher;

import static org.skyscreamer.jsonassert.JSONCompare.compareJSONWithIgnore;

import java.util.ArrayList;

public class IgnoreModeTest {
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/129
    //Test single JSON object
    @Test
    public void testNormalString() throws JSONException {
        String expected = "{a:12,currentTime:\"20:59\",b:233,c:345}";
        String actual = "{a:12,currentTime:\"21:09\",b:233,c:678}";
        ArrayList<String> ignore = new ArrayList<String>();
        ignore.add("currentTime");
        ignore.add("c");
        JSONAssert.assertEqualsWithIgnore(expected, actual, ignore, true);
        ignore.clear();
        ignore.add("c");
        JSONAssert.assertNotEqualsWithIgnore(expected, actual, ignore, true);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/129
    //Test recursively constructed JSON object
    @Test
    public void testRecursiveIgnore1() throws JSONException{
        String expected = "{a:{b:{c:1,d:2},e:3},f:[4,5],g:6}";
        String actual = "{a:{b:{c:10,d:2},e:3},f:[5,4],g:6}";
        ArrayList<String> ignore = new ArrayList<String>();
        ignore.add("f");
        ignore.add("c");
        JSONAssert.assertEqualsWithIgnore(expected, actual, ignore, true);
        ignore.clear();
        ignore.add("f");
        ignore.add("b");
        JSONAssert.assertEqualsWithIgnore(expected, actual, ignore, true);
        ignore.clear();
        ignore.add("a");
        JSONAssert.assertEqualsWithIgnore(expected, actual, ignore, false);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/129
    //Test recursively constructed JSON Array
    @Test
    public void testRecursiveIgnore2() throws JSONException{
        String excepted = "[[{a:1},{b:2}],[{c:3},{d:4}]]";
        String strictActual = "[[{a:1},{b:2}],[{c:3},{d:4}]]";
        String lenientActual = "[[{d:5},{c:3}],[{a:1},{b:2}]]";
        ArrayList<String> ignore = new ArrayList<String>();
        ignore.add("d");
        JSONAssert.assertEqualsWithIgnore(excepted, strictActual, ignore, true);
        JSONAssert.assertEqualsWithIgnore(excepted, lenientActual, ignore, false);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/129
    //Test recursively constructed JSON Array and JSON objects
    @Test
    public void testRecursiveIgnore3() throws JSONException{
        String expected = "[[{a:1,e:[[{f:5},{g:6}]]},{b:2}],[{c:3},{d:4}]]";
        String actual = "[[{a:1,e:[[{f:5},{g:7}]]},{b:2}],[{c:3},{d:4}]]";
        ArrayList<String> ignore = new ArrayList<String>();
        ignore.add("g");
        JSONAssert.assertEqualsWithIgnore(expected, actual, ignore, true);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/129
    //Test invalid ignore (ignore list that contains fields not appear)
    @Test
    public void testInvalidIgnore() throws JSONException{
        String expected = "{a:2,b:3}";
        String actual = "{a:2,b:3}";
        ArrayList<String> ignore = new ArrayList<String>();
        ignore.add("invalid");
        JSONCompareResult result = compareJSONWithIgnore(expected,actual,ignore,JSONCompareMode.STRICT);
        assertThat(result, failsWithMessage(equalTo("Following ignore field(s) not found:\ninvalid\n")));
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/129
    //Test empty ignore
    @Test
    public void testEmptyIgnore() throws JSONException{
        String expected = "{a:2,b:3}";
        String actual = "{a:2,b:3}";
        ArrayList<String> ignore = new ArrayList<String>();
        JSONCompareResult result = compareJSONWithIgnore(expected,actual,ignore,JSONCompareMode.STRICT);
        assertThat(result, failsWithMessage(equalTo("Please use other mode if don't want to ignore any field")));
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/129
    //Test normal JSON array
    @Test
    public void testJSONArray1() throws JSONException{
        String expected = "[{a:1,b:[1,2],c:3},{a:1,b:[4,5],c:3}]";
        String actual = "[{a:1,b:[2,1,3],c:3},{a:1,b:[5,4],c:3}]";
        ArrayList<String> ignore = new ArrayList<String>();
        ignore.add("b");
        JSONAssert.assertEqualsWithIgnore(expected, actual, ignore, true);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/129
    //Test normal JSON array
    @Test
    public void testJSONArray2() throws JSONException{
        String expected = "[{a:{b:{c:1,d:2},e:3},f:[4,5],g:6},{h:7,c:8}]";
        String actual = "[{a:{b:{c:10,d:2},e:3},f:[5,4],g:6},{h:7,c:9}]";
        ArrayList<String> ignore = new ArrayList<String>();
        ignore.add("f");
        ignore.add("c");
        JSONAssert.assertEqualsWithIgnore(expected, actual, ignore, true);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/129
    //Test complex case
    @Test
    public void testComplexCase1() throws JSONException{
        String expected = "{h:[{a:1},{b:2}],curTime:567,c:4,i:[{d:{e:5,f:6},j:10}]}";
        String actual = "{h:[{a:1},{b:2}],curTime:678,c:4,i:[{d:{e:8,f:9},j:10}]}";
        ArrayList<String> ignore = new ArrayList<String>();
        ignore.add("curTime");
        ignore.add("d");
        JSONAssert.assertEqualsWithIgnore(expected, actual, ignore, true);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/129
    //Test complex case
    @Test
    public void testComplexCase2() throws JSONException{
        String expected = "[{a:[{l:{b:{c:[{d:1}],e:2}}},{f:3}]},{i:{g:4,j:{k:5}}},{curTime:345}]";
        String actual = "[{curTime:678},{a:[{l:{b:{c:[{d:7}],e:2}}},{f:8}]},{i:{g:4,j:{k:6}}}]";
        ArrayList<String> ignore = new ArrayList<String>();
        ignore.add("d");
        ignore.add("f");
        ignore.add("k");
        ignore.add("curTime");
        JSONAssert.assertEqualsWithIgnore(expected, actual, ignore, false);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/129
    //Test assert method with JSONObject
    @Test
    public void testJSONObject() throws JSONException{
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("a",1);
        expected.put("b",2);
        expected.put("c",3);
        actual.put("a",1);
        actual.put("b",4);
        actual.put("c",5);
        ArrayList<String> ignore = new ArrayList<String>();
        ignore.add("b");
        ignore.add("c");
        JSONAssert.assertEqualsWithIgnore(expected, actual, ignore, true);
        ignore.remove(0);
        JSONAssert.assertNotEqualsWithIgnore(expected, actual, ignore, true);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/129
    //Test assert method with JSONArray
    @Test
    public void testJSONArray() throws JSONException{
        JSONArray expected = new JSONArray();
        JSONArray actual = new JSONArray();
        JSONObject obj1 = new JSONObject();
        JSONObject obj2 = new JSONObject();
        obj1.put("a", 1);
        obj1.put("b", 2);
        obj2.put("c", 3);
        obj2.put("d", 4);
        ArrayList<String> ignore = new ArrayList<String>();
        ignore.add("a");
        ignore.add("b");
        expected.put(obj1);
        expected.put(obj2);
        actual.put(obj2);
        actual.put(obj1);
        JSONAssert.assertEqualsWithIgnore(expected, actual, ignore, false);
        JSONAssert.assertNotEqualsWithIgnore(expected, actual, ignore, true);
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
