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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT_ORDER;

/**
 * Unit tests for {@link JSONAssert}
 */
public class JSONAssertTest {
    @Test
    public void testString() throws JSONException {
        testPass("\"Joe\"", "\"Joe\"", STRICT);
        testPass("\"Joe\"", "\"Joe\"", LENIENT);
        testPass("\"Joe\"", "\"Joe\"", NON_EXTENSIBLE);
        testPass("\"Joe\"", "\"Joe\"", STRICT_ORDER);
        testFail("\"Joe\"", "\"Joe1\"", STRICT);
        testFail("\"Joe\"", "\"Joe2\"", LENIENT);
        testFail("\"Joe\"", "\"Joe3\"", NON_EXTENSIBLE);
        testFail("\"Joe\"", "\"Joe4\"", STRICT_ORDER);
    }

    @Test
    public void testNumber() throws JSONException {
        testPass("123", "123", STRICT);
        testPass("123", "123", LENIENT);
        testPass("123", "123", NON_EXTENSIBLE);
        testPass("123", "123", STRICT_ORDER);
        testFail("123", "1231", STRICT);
        testFail("123", "1232", LENIENT);
        testFail("123", "1233", NON_EXTENSIBLE);
        testFail("123", "1234", STRICT_ORDER);
        testPass("0", "0", STRICT);
        testPass("-1", "-1", STRICT);
        testPass("0.1", "0.1", STRICT);
        testPass("1.2e5", "1.2e5", STRICT);
        testPass("20.4e-1", "20.4e-1", STRICT);
        testFail("310.1e-1", "31.01", STRICT); // should fail though numbers are the same?
    }

    @Test
    public void testSimple() throws JSONException {
        testPass("{id:1}", "{id:1}", STRICT);
        testFail("{id:1}", "{id:2}", STRICT);
        testPass("{id:1}", "{id:1}", LENIENT);
        testFail("{id:1}", "{id:2}", LENIENT);
        testPass("{id:1}", "{id:1}", NON_EXTENSIBLE);
        testFail("{id:1}", "{id:2}", NON_EXTENSIBLE);
        testPass("{id:1}", "{id:1}", STRICT_ORDER);
        testFail("{id:1}", "{id:2}", STRICT_ORDER);
    }

    @Test
    public void testSimpleStrict() throws JSONException {
        testPass("{id:1}", "{id:1,name:\"Joe\"}", LENIENT);
        testFail("{id:1}", "{id:1,name:\"Joe\"}", STRICT);
        testPass("{id:1}", "{id:1,name:\"Joe\"}", STRICT_ORDER);
        testFail("{id:1}", "{id:1,name:\"Joe\"}", NON_EXTENSIBLE);
    }

    @Test
    public void testReversed() throws JSONException {
        testPass("{name:\"Joe\",id:1}", "{id:1,name:\"Joe\"}", LENIENT);
        testPass("{name:\"Joe\",id:1}", "{id:1,name:\"Joe\"}", STRICT);
        testPass("{name:\"Joe\",id:1}", "{id:1,name:\"Joe\"}", NON_EXTENSIBLE);
        testPass("{name:\"Joe\",id:1}", "{id:1,name:\"Joe\"}", STRICT_ORDER);
    }

    @Test // Currently JSONAssert assumes JSONObject.
    public void testArray() throws JSONException {
        testPass("[1,2,3]","[1,2,3]", STRICT);
        testPass("[1,2,3]","[1,3,2]", LENIENT);
        testFail("[1,2,3]","[1,3,2]", STRICT);
        testFail("[1,2,3]","[4,5,6]", LENIENT);
        testPass("[1,2,3]","[1,2,3]", STRICT_ORDER);
        testPass("[1,2,3]","[1,3,2]", NON_EXTENSIBLE);
        testFail("[1,2,3]","[1,3,2]", STRICT_ORDER);
        testFail("[1,2,3]","[4,5,6]", NON_EXTENSIBLE);
    }

    @Test
    public void testNested() throws JSONException {
        testPass("{id:1,address:{addr1:\"123 Main\", addr2:null, city:\"Houston\", state:\"TX\"}}",
                "{id:1,address:{addr1:\"123 Main\", addr2:null, city:\"Houston\", state:\"TX\"}}", STRICT);
        testFail("{id:1,address:{addr1:\"123 Main\", addr2:null, city:\"Houston\", state:\"TX\"}}",
                "{id:1,address:{addr1:\"123 Main\", addr2:null, city:\"Austin\", state:\"TX\"}}", STRICT);
    }

    @Test
    public void testVeryNested() throws JSONException {
        testPass("{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{p:\"blah\"}}}}}}}}}}}}}}}}",
                "{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{p:\"blah\"}}}}}}}}}}}}}}}}", STRICT);
        testFail("{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{p:\"blah\"}}}}}}}}}}}}}}}}",
                "{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{z:\"blah\"}}}}}}}}}}}}}}}}", STRICT);
    }

    @Test
    public void testSimpleArray() throws JSONException {
        testPass("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Exact to exact (strict)
                "{id:1,pets:[\"dog\",\"cat\",\"fish\"]}",
                STRICT);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Out-of-order fails (strict)
                "{id:1,pets:[\"dog\",\"fish\",\"cat\"]}",
                STRICT);
        testPass("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Out-of-order ok
                "{id:1,pets:[\"dog\",\"fish\",\"cat\"]}",
                LENIENT);
        testPass("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Out-of-order ok
                "{id:1,pets:[\"dog\",\"fish\",\"cat\"]}",
                NON_EXTENSIBLE);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Out-of-order fails (strict order)
                "{id:1,pets:[\"dog\",\"fish\",\"cat\"]}",
                STRICT_ORDER);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Mismatch
                "{id:1,pets:[\"dog\",\"cat\",\"bird\"]}",
                STRICT);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Mismatch
                "{id:1,pets:[\"dog\",\"cat\",\"bird\"]}",
                LENIENT);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Mismatch
                "{id:1,pets:[\"dog\",\"cat\",\"bird\"]}",
                STRICT_ORDER);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Mismatch
                "{id:1,pets:[\"dog\",\"cat\",\"bird\"]}",
                NON_EXTENSIBLE);
    }

    @Test
    public void testSimpleMixedArray() throws JSONException {
        testPass("{stuff:[321, \"abc\"]}", "{stuff:[\"abc\", 321]}", LENIENT);
        testFail("{stuff:[321, \"abc\"]}", "{stuff:[\"abc\", 789]}", LENIENT);
    }

    @Test
    public void testComplexMixedStrictArray() throws JSONException {
        testPass("{stuff:[{pet:\"cat\"},{car:\"Ford\"}]}", "{stuff:[{pet:\"cat\"},{car:\"Ford\"}]}", STRICT);
    }

    @Test
    public void testComplexMixedArray() throws JSONException {
        testPass("{stuff:[{pet:\"cat\"},{car:\"Ford\"}]}", "{stuff:[{pet:\"cat\"},{car:\"Ford\"}]}", LENIENT);
    }

    @Test
    public void testComplexArrayNoUniqueID() throws JSONException {
        testPass("{stuff:[{address:{addr1:\"123 Main\"}}, {address:{addr1:\"234 Broad\"}}]}",
                "{stuff:[{address:{addr1:\"123 Main\"}}, {address:{addr1:\"234 Broad\"}}]}",
                LENIENT);
    }

    @Test
    public void testSimpleAndComplexStrictArray() throws JSONException {
        testPass("{stuff:[123,{a:\"b\"}]}", "{stuff:[123,{a:\"b\"}]}", STRICT);
    }

    @Test
    public void testSimpleAndComplexArray() throws JSONException {
        testPass("{stuff:[123,{a:\"b\"}]}", "{stuff:[123,{a:\"b\"}]}", LENIENT);
    }

    @Test
    public void testComplexArray() throws JSONException {
        testPass("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                 "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                 STRICT); // Exact to exact (strict)
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                STRICT); // Out-of-order fails (strict)
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                STRICT_ORDER); // Out-of-order fails (strict order)
        testPass("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                LENIENT); // Out-of-order ok
        testPass("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                NON_EXTENSIBLE); // Out-of-order ok
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
                STRICT); // Mismatch (strict)
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
                LENIENT); // Mismatch
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
                STRICT_ORDER); // Mismatch
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
                NON_EXTENSIBLE); // Mismatch
    }

    @Test
    public void testArrayOfArraysStrict() throws JSONException {
        testPass("{id:1,stuff:[[1,2],[2,3],[],[3,4]]}", "{id:1,stuff:[[1,2],[2,3],[],[3,4]]}", STRICT);
        testFail("{id:1,stuff:[[1,2],[2,3],[3,4],[]]}", "{id:1,stuff:[[1,2],[2,3],[],[3,4]]}", STRICT);
    }

    @Test
    public void testArrayOfArrays() throws JSONException {
        testPass("{id:1,stuff:[[4,3],[3,2],[],[1,2]]}", "{id:1,stuff:[[1,2],[2,3],[],[3,4]]}", LENIENT);
    }

    @Test
    public void testLenientArrayRecursion() throws JSONException {
        testPass("[{\"arr\":[5, 2, 1]}]", "[{\"b\":3, \"arr\":[1, 5, 2]}]", LENIENT);
    }

    @Test
    public void testFieldMismatch() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{name:\"Pat\"}", "{name:\"Sue\"}", STRICT);
        FieldComparisonFailure comparisonFailure = result.getFieldFailures().iterator().next();
        Assert.assertEquals("Pat", comparisonFailure.getExpected());
        Assert.assertEquals("Sue", comparisonFailure.getActual());
        Assert.assertEquals("name", comparisonFailure.getField());
    }

    @Test
    public void testBooleanArray() throws JSONException {
        testPass("[true, false, true, true, false]", "[true, false, true, true, false]", STRICT);
        testPass("[false, true, true, false, true]", "[true, false, true, true, false]", LENIENT);
        testFail("[false, true, true, false, true]", "[true, false, true, true, false]", STRICT);
        testPass("[false, true, true, false, true]", "[true, false, true, true, false]", NON_EXTENSIBLE);
        testFail("[false, true, true, false, true]", "[true, false, true, true, false]", STRICT_ORDER);
    }

    @Test
    public void testNullProperty() throws JSONException {
        testFail("{id:1,name:\"Joe\"}", "{id:1,name:null}", STRICT);
        testFail("{id:1,name:null}", "{id:1,name:\"Joe\"}", STRICT);
    }

    @Test
    public void testIncorrectTypes() throws JSONException {
        testFail("{id:1,name:\"Joe\"}", "{id:1,name:[]}", STRICT);
        testFail("{id:1,name:[]}", "{id:1,name:\"Joe\"}", STRICT);
    }

    @Test
    public void testNullEquality() throws JSONException {
        testPass("{id:1,name:null}", "{id:1,name:null}", STRICT);
    }

    @Test
    public void testExpectedArrayButActualObject() throws JSONException {
        testFail("[1]", "{id:1}", LENIENT);
    }

    @Test
    public void testExpectedObjectButActualArray() throws JSONException {
        testFail("{id:1}", "[1]", LENIENT);
    }

    @Test
    public void testEquivalentIntAndLong() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Long.valueOf(12345));
        JSONAssert.assertEquals(expected, actual, true);
        JSONAssert.assertEquals(actual, expected, true);
    }

    @Test
    public void testEquivalentIntAndDouble() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12345.0));
        JSONAssert.assertEquals(expected, actual, true);
        JSONAssert.assertEquals(actual, expected, true);
    }

    @Test(expected = AssertionError.class)
    public void testAssertNotEqualsWhenEqualStrict() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertNotEquals(expected, actual, true);
    }

    @Test(expected = AssertionError.class)
    public void testAssertNotEqualsWhenEqualLenient() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertNotEquals(expected, actual, false);
    }

    @Test()
    public void testAssertNotEqualsWhenEqualDiffObjectsStrict() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        expected.put("name", "Joe");
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertNotEquals(expected, actual, true);
    }

    @Test(expected = AssertionError.class)
    public void testAssertNotEqualsWhenEqualDiffObjectsLenient() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        expected.put("name", "Joe");
        actual.put("name", "Joe");
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertNotEquals(expected, actual, false);
    }

    @Test()
    public void testAssertNotEqualsWhenDifferentStrict() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12346));
        JSONAssert.assertNotEquals(expected, actual, true);
    }

    @Test()
    public void testAssertNotEqualsWhenDifferentLenient() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12346));
        JSONAssert.assertNotEquals(expected, actual, false);
    }

    @Test()
    public void testAssertNotEqualsString() throws JSONException {
        JSONAssert.assertNotEquals("[1,2,3]", "[1,3,2]", STRICT);
        JSONAssert.assertNotEquals("[1,2,3]", "[1,2,4]", LENIENT);
        JSONAssert.assertNotEquals("[1,2,3]", "[1,3,2]", true);
        JSONAssert.assertNotEquals("[1,2,3]", "[1,2,4]", false);
    }

    @Test()
    public void testAssertEqualsString() throws JSONException {
        JSONAssert.assertEquals("[1,2,3]", "[1,2,3]", true);
        JSONAssert.assertEquals("{id:12345}", "{id:12345}", false);
        JSONAssert.assertEquals("{id:12345}", "{id:12345, name:\"john\"}", LENIENT);
        JSONAssert.assertEquals("{id:12345}", "{id:12345}", LENIENT);
        JSONAssert.assertEquals("{id:12345}", "{id:12345, name:\"john\"}", LENIENT);
    }

    @Test()
    public void testAssertNotEqualsStringAndJSONObject() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertEquals("{id:12345}", actual, false);
        JSONAssert.assertNotEquals("{id:12346}", actual, false);
    }

    private List<Object> buildJSONArray(int... val) {
        List<Object> data = Arrays.<Object>asList(val);
        return data;
    }

    @Test()
    public void testAssertNotEqualsJSONArray() throws JSONException {
        JSONArray actual = new JSONArray(buildJSONArray(1, 2, 3));
        JSONAssert.assertEquals("[1,2,3]", actual, false);
        JSONAssert.assertNotEquals("[1,2,4]", actual, false);
        JSONAssert.assertNotEquals("[1,3,2]", actual, true);
        JSONAssert.assertNotEquals(new JSONArray(buildJSONArray(1, 2, 4)), actual, false);
        JSONAssert.assertNotEquals(new JSONArray(buildJSONArray(1, 3, 2)), actual, true);
    }

    @Test
    public void testAssertEqualsStringJSONArrayBooleanWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(buildJSONArray(1, 2, 3));
        JSONAssert.assertEquals("Message", "[1,2,3]", actual, false);
        performAssertEqualsTestForMessageVerification("[1,2,4]", actual, false);
        performAssertEqualsTestForMessageVerification("[1,3,2]", actual, true);
    }

    @Test
    public void testAssertEqualsStringJSONArrayCompareModeWithMessage() throws JSONException {
        List<Object> data = Arrays.<Object>asList(1, 2, 3);
        JSONArray actual = new JSONArray(data);
        JSONAssert.assertEquals("Message", "[1,2,3]", actual, LENIENT);
        performAssertEqualsTestForMessageVerification("[1,2,4]", actual, LENIENT);
        performAssertEqualsTestForMessageVerification("[1,3,2]", actual, STRICT);
    }

    @Test
    public void testAssertEqualsJSONArray2BooleanWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(buildJSONArray(1, 2, 3));
        JSONAssert.assertEquals("Message", new JSONArray(buildJSONArray(1, 2, 3)), actual, false);
        performAssertEqualsTestForMessageVerification(new JSONArray(buildJSONArray(1, 2, 3)), actual, false);
        performAssertEqualsTestForMessageVerification(new JSONArray(buildJSONArray(1, 2, 3)), actual, true);
    }

    @Test
    public void testAssertEqualsJSONArray2JSONCompareWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(buildJSONArray(1, 2, 3));

        JSONAssert.assertEquals("Message", new JSONArray(buildJSONArray(1, 2, 3)), actual, LENIENT);
        performAssertEqualsTestForMessageVerification(new JSONArray(buildJSONArray(1, 2, 4)), actual, LENIENT);
        performAssertEqualsTestForMessageVerification(new JSONArray(buildJSONArray(1, 3, 2)), actual, STRICT);
    }

    @Test
    public void testAssertEqualsString2Boolean() throws JSONException {
        JSONAssert.assertEquals("Message", "{id:12345}", "{id:12345}", false);
        JSONAssert.assertEquals("Message", "{id:12345}", "{id:12345, name:\"john\"}", false);

        performAssertEqualsTestForMessageVerification("{id:12345}", "{id:12345, name:\"john\"}", true);
        performAssertEqualsTestForMessageVerification("{id:12345}", "{id:123456}", false);
    }

    @Test
    public void testAssertEqualsString2JSONCompare() throws JSONException {
        JSONAssert.assertEquals("Message", "{id:12345}", "{id:12345}", LENIENT);
        JSONAssert.assertEquals("Message", "{id:12345}", "{id:12345, name:\"john\"}", LENIENT);

        performAssertEqualsTestForMessageVerification("{id:12345}", "{id:12345, name:\"john\"}", STRICT);
        performAssertEqualsTestForMessageVerification("{id:12345}", "{id:123456}", LENIENT);
    }

    @Test
    public void testAssertEqualsStringJSONObjectBoolean() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertEquals("Message", "{id:12345}", actual, false);
        performAssertEqualsTestForMessageVerification("{id:12346}", actual, false);
        performAssertEqualsTestForMessageVerification("[1,2,3]", "[1,3,2]", true);
    }

    @Test
    public void testAssertEqualsStringJSONObjectJSONCompare() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertEquals("Message", "{id:12345}", actual, LENIENT);
        performAssertEqualsTestForMessageVerification("{id:12346}", actual, LENIENT);
        performAssertEqualsTestForMessageVerification("[1,2,3]", "[1,3,2]", STRICT);
    }

    @Test
    public void testAssertEqualsJSONObject2JSONCompare() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("name", "Joe");
        actual.put("id", Integer.valueOf(12345));
        JSONAssert.assertEquals("Message", expected, actual, LENIENT);

        expected.put("street", "St. Paul");
        performAssertEqualsTestForMessageVerification(expected, actual, LENIENT);

        expected = new JSONObject();
        actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12346));
        performAssertEqualsTestForMessageVerification(expected, actual, STRICT);
    }

    @Test
    public void testAssertEqualsJSONObject2Boolean() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("name", "Joe");
        actual.put("id", Integer.valueOf(12345));
        JSONAssert.assertEquals("Message", expected, actual, false);

        expected.put("street", "St. Paul");
        performAssertEqualsTestForMessageVerification(expected, actual, false);

        expected = new JSONObject();
        actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12346));
        performAssertEqualsTestForMessageVerification(expected, actual, true);
    }

    @Test
    public void testAssertEqualsString2JsonComparator() throws IllegalArgumentException, JSONException {
        JSONAssert.assertEquals("Message", "{\"entry\":{\"id\":x}}", "{\"entry\":{\"id\":1, \"id\":2}}",
            new CustomComparator(
                JSONCompareMode.STRICT,
                new Customization("entry.id",
                new RegularExpressionValueMatcher<Object>("\\d"))
         ));

        performAssertEqualsTestForMessageVerification("{\"entry\":{\"id\":x}}", "{\"entry\":{\"id\":1, \"id\":as}}",
            new CustomComparator(
                JSONCompareMode.STRICT,
                new Customization("entry.id",
                new RegularExpressionValueMatcher<Object>("\\d"))
        ));
    }

    @Test
    public void testAssertNotEqualsStringJSONArrayBooleanWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(buildJSONArray(1, 2, 3));
        JSONAssert.assertNotEquals("Message", "[1,4,3]", actual, false);
        JSONAssert.assertNotEquals("Message", "[1,4,3]", actual, true);
        performAssertNotEqualsTestForMessageVerification("[1,3,2]", actual, false);
        performAssertNotEqualsTestForMessageVerification("[1,2,3]", actual, true);
    }

    @Test
    public void testAssertNotEqualsStringJSONArrayCompareModeWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(buildJSONArray(1, 2, 3));
        JSONAssert.assertNotEquals("Message", "[1,2,4]", actual, LENIENT);
        JSONAssert.assertNotEquals("Message", "[1,2,4]", actual, STRICT);
        performAssertNotEqualsTestForMessageVerification("[1,3,2]", actual, LENIENT);
        performAssertNotEqualsTestForMessageVerification("[1,2,3]", actual, STRICT);
    }

    @Test
    public void testAssertNotEqualsJSONArray2BooleanWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(buildJSONArray(1, 2, 3));
        JSONAssert.assertNotEquals("Message", new JSONArray(buildJSONArray(1, 4, 3)), actual, false);
        performAssertNotEqualsTestForMessageVerification(new JSONArray(buildJSONArray(1, 3, 2)), actual, false);
        performAssertNotEqualsTestForMessageVerification(new JSONArray(buildJSONArray(1, 2, 3)), actual, true);
    }

    @Test
    public void testAssertNotEqualsJSONArray2JSONCompareWithMessage() throws JSONException {
        JSONArray actual = new JSONArray(buildJSONArray(1, 2, 3));

        JSONAssert.assertNotEquals("Message", new JSONArray(buildJSONArray(1, 4, 3)), actual, LENIENT);
        performAssertNotEqualsTestForMessageVerification(new JSONArray(buildJSONArray(1, 3, 2)), actual, LENIENT);
        performAssertNotEqualsTestForMessageVerification(new JSONArray(buildJSONArray(1, 2, 3)), actual, STRICT);
    }

    @Test
    public void testAssertNotEqualsString2Boolean() throws JSONException {
        JSONAssert.assertNotEquals("Message", "{id:12345}", "{id:45}", false);
        JSONAssert.assertNotEquals("Message", "{id:12345}", "{id:345, name:\"john\"}", false);

        performAssertNotEqualsTestForMessageVerification("{id:12345}", "{id:12345}", true);
        performAssertNotEqualsTestForMessageVerification("{id:12345}", "{id:12345, name:\"John\"}", false);
    }

    @Test
    public void testAssertNotEqualsString2JSONCompare() throws JSONException {
        JSONAssert.assertNotEquals("Message", "{id:12345}", "{id:123}", LENIENT);
        JSONAssert.assertNotEquals("Message", "{id:12345, name:\"John\"}", "{id:12345}", LENIENT);

        performAssertNotEqualsTestForMessageVerification("{id:12345}", "{id:12345, name:\"john\"}", LENIENT);
        performAssertNotEqualsTestForMessageVerification("{id:12345}", "{id:12345}", STRICT);
    }

    @Test
    public void testAssertNotEqualsStringJSONObjectBoolean() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertNotEquals("Message", "{id:1234}", actual, false);
        performAssertNotEqualsTestForMessageVerification("{id:12345}", actual, false);
        performAssertNotEqualsTestForMessageVerification("[1,2,3]", "[1,2,3]", true);
    }

    @Test
    public void testAssertNotEqualsStringJSONObjectJSONCompare() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", Double.valueOf(12345));
        JSONAssert.assertNotEquals("Message", "{id:1234}", actual, LENIENT);
        performAssertNotEqualsTestForMessageVerification("{id:12345}", actual, LENIENT);
        performAssertNotEqualsTestForMessageVerification("[1,2,3]", "[1,2,3]", STRICT);
    }

    @Test
    public void testAssertNtEqualsJSONObject2JSONCompare() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("name", "Joe");
        actual.put("id", Integer.valueOf(123));
        JSONAssert.assertNotEquals("Message", expected, actual, LENIENT);

        actual.remove("id");
        actual.put("id", Integer.valueOf(12345));
        performAssertNotEqualsTestForMessageVerification(expected, actual, LENIENT);

        expected = new JSONObject();
        actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12345));
        performAssertNotEqualsTestForMessageVerification(expected, actual, STRICT);
    }

    @Test
    public void testAssertNotEqualsJSONObject2Boolean() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("name", "Joe");
        actual.put("id", Integer.valueOf(123));
        JSONAssert.assertNotEquals("Message", expected, actual, false);

        actual.remove("id");
        actual.put("id", Integer.valueOf(12345));
        performAssertNotEqualsTestForMessageVerification(expected, actual, false);

        expected = new JSONObject();
        actual = new JSONObject();
        expected.put("id", Integer.valueOf(12345));
        actual.put("id", Double.valueOf(12345));
        performAssertNotEqualsTestForMessageVerification(expected, actual, true);
    }

    @Test
    public void testAssertNotEqualsString2JsonComparator() throws IllegalArgumentException, JSONException {
        JSONAssert.assertNotEquals("Message", "{\"entry\":{\"id\":x}}", "{\"entry\":{\"id\":1, \"id\":hh}}",
            new CustomComparator(
                JSONCompareMode.STRICT,
                new Customization("entry.id",
                new RegularExpressionValueMatcher<Object>("\\d"))
         ));

        performAssertNotEqualsTestForMessageVerification("{\"entry\":{\"id\":x}}", "{\"entry\":{\"id\":1, \"id\":2}}",
            new CustomComparator(
                JSONCompareMode.STRICT,
                new Customization("entry.id",
                new RegularExpressionValueMatcher<Object>("\\d"))
        ));
    }

    private void testPass(String expected, String actual, JSONCompareMode compareMode)
            throws JSONException
    {
        String message = expected + " == " + actual + " (" + compareMode + ")";
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, compareMode);
        Assert.assertTrue(message + "\n  " + result.getMessage(), result.passed());
    }

    private void testFail(String expected, String actual, JSONCompareMode compareMode)
            throws JSONException
    {
        String message = expected + " != " + actual + " (" + compareMode + ")";
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, compareMode);
        Assert.assertTrue(message, result.failed());
    }

    private void performAssertEqualsTestForMessageVerification(
        Object expected,
        Object actual,
        Object strictMode) throws JSONException {

        String message = "Message";
        String testShouldFailMessage = "The test should fail so that the message in AssertionError could be verified.";
        String strictModeMessage = "strictMode must be an instance of JSONCompareMode or Boolean";
        boolean assertEqualsFailed = true;
        if(expected instanceof String && actual instanceof String && strictMode instanceof JSONComparator) {
            try {
                JSONAssert.assertEquals(message, (String) expected, (String) actual, (JSONComparator) strictMode);
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        }
        else if(expected instanceof String && actual instanceof JSONArray) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertEquals(message, (String) expected, (JSONArray) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertEquals(message, (String) expected, (JSONArray) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof JSONArray && actual instanceof JSONArray) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertEquals(message, (JSONArray) expected, (JSONArray) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertEquals(message, (JSONArray) expected, (JSONArray) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof String && actual instanceof String) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertEquals(message, (String) expected, (String) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertEquals(message, (String) expected, (String) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof String && actual instanceof JSONObject) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertEquals(message, (String) expected, (JSONObject) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertEquals(message, (String) expected, (JSONObject) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof JSONObject && actual instanceof JSONObject) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertEquals(message, (JSONObject) expected, (JSONObject) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertEquals(message, (JSONObject) expected, (JSONObject) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else {
            fail("No overloaded method found to call");
        }
    }

    private void performAssertNotEqualsTestForMessageVerification(
        Object expected,
        Object actual,
        Object strictMode)
        throws JSONException {

        String message = "Message";
        String testShouldFailMessage = "The test should fail so that the message in AssertionError could be verified.";
        String strictModeMessage = "strictMode must be an instance of JSONCompareMode or Boolean";
        boolean assertEqualsFailed = true;
        if(expected instanceof String && actual instanceof String && strictMode instanceof JSONComparator) {
            try {
                JSONAssert.assertNotEquals(message, (String) expected, (String) actual, (JSONComparator) strictMode);
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        }
        else if(expected instanceof String && actual instanceof JSONArray) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertNotEquals(message, (String) expected, (JSONArray) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertNotEquals(message, (String) expected, (JSONArray) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof JSONArray && actual instanceof JSONArray) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertNotEquals(message, (JSONArray) expected, (JSONArray) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertNotEquals(message, (JSONArray) expected, (JSONArray) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof String && actual instanceof String) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertNotEquals(message, (String) expected, (String) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertNotEquals(message, (String) expected, (String) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof String && actual instanceof JSONObject) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertNotEquals(message, (String) expected, (JSONObject) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertNotEquals(message, (String) expected, (JSONObject) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else if(expected instanceof JSONObject && actual instanceof JSONObject) {
            try {
                if(strictMode instanceof JSONCompareMode) {
                    JSONAssert.assertNotEquals(message, (JSONObject) expected, (JSONObject) actual, (JSONCompareMode) strictMode);
                } else if(strictMode instanceof Boolean) {
                    JSONAssert.assertNotEquals(message, (JSONObject) expected, (JSONObject) actual, (Boolean) strictMode);
                } else {
                    fail(strictModeMessage);
                }
                assertEqualsFailed = false;
                fail(testShouldFailMessage); //will throw AssertionError
            } catch (AssertionError ae) {
                handleAssertionError(message, assertEqualsFailed, ae);
            }
        } else {
            fail("No overloaded method found to call");
        }
    }

    private void handleAssertionError(String message, boolean assertEqualsFailed, AssertionError ae) throws AssertionError {
        if(assertEqualsFailed) {
            verifyErrorMessage(message, ae);
        } else {
            throw ae;
        }
    }

    private void verifyErrorMessage(String message, AssertionError ae) {
        assertTrue(ae.getMessage().contains(message));
        assertTrue(ae.getMessage().startsWith(message));
    }
}
