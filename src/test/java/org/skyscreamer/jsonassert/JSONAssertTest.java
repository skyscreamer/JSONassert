package org.skyscreamer.jsonassert;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static org.skyscreamer.jsonassert.JSONCompareMode.*;

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
        testPass("{id:1,name:\"Joe1\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                 "{id:1,name:\"Joe1\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                 STRICT); // Exact to exact (strict)
        testFail("{id:1,name:\"Joe2\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe2\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                STRICT); // Out-of-order fails (strict)
        testFail("{id:1,name:\"Joe3\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe3\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                STRICT_ORDER); // Out-of-order fails (strict order)
 /* ? was testPass ? */      testPass("{id:1,name:\"Joe4\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe4\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                LENIENT); // Out-of-order ok
 /* ? was testPass ? */     testPass("{id:1,name:\"Joe5\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe5\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                NON_EXTENSIBLE); // Out-of-order ok
        testFail("{id:1,name:\"Joe6\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe6\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
                STRICT); // Mismatch (strict)
        testFail("{id:1,name:\"Joe7\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe7\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
                LENIENT); // Mismatch
        testFail("{id:1,name:\"Joe8\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe8\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
                STRICT_ORDER); // Mismatch
        testFail("{id:1,name:\"Joe9\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe9\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
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
        expected.put("id", new Integer(12345));
        actual.put("id", new Long(12345));
        JSONAssert.assertEquals(expected, actual, true);
        JSONAssert.assertEquals(actual, expected, true);
    }

    @Test
    public void testEquivalentIntAndDouble() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", new Integer(12345));
        actual.put("id", new Double(12345.0));
        JSONAssert.assertEquals(expected, actual, true);
        JSONAssert.assertEquals(actual, expected, true);
    }

    @Test(expected = AssertionError.class)
    public void testAssertNotEqualsWhenEqualStrict() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", new Integer(12345));
        actual.put("id", new Double(12345));
        JSONAssert.assertNotEquals(expected, actual, true);
    }

    @Test(expected = AssertionError.class)
    public void testAssertNotEqualsWhenEqualLenient() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", new Integer(12345));
        actual.put("id", new Double(12345));
        JSONAssert.assertNotEquals(expected, actual, false);
    }

    @Test()
    public void testAssertNotEqualsWhenEqualDiffObjectsStrict() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", new Integer(12345));
        expected.put("name", "Joe");
        actual.put("id", new Double(12345));
        JSONAssert.assertNotEquals(expected, actual, true);
    }

    @Test(expected = AssertionError.class)
    public void testAssertNotEqualsWhenEqualDiffObjectsLenient() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", new Integer(12345));
        expected.put("name", "Joe");
        actual.put("name", "Joe");
        actual.put("id", new Double(12345));
        JSONAssert.assertNotEquals(expected, actual, false);
    }

    @Test()
    public void testAssertNotEqualsWhenDifferentStrict() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", new Integer(12345));
        actual.put("id", new Double(12346));
        JSONAssert.assertNotEquals(expected, actual, true);
    }

    @Test()
    public void testAssertNotEqualsWhenDifferentLenient() throws JSONException {
        JSONObject expected = new JSONObject();
        JSONObject actual = new JSONObject();
        expected.put("id", new Integer(12345));
        actual.put("id", new Double(12346));
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
    public void testAssertNotEqualsStringAndJSONObject() throws JSONException {
        JSONObject actual = new JSONObject();
        actual.put("id", new Double(12345));
        JSONAssert.assertEquals("{id:12345}", actual, false);
        JSONAssert.assertNotEquals("{id:12346}", actual, false);
    }

    @Test()
    public void testAssertNotEqualsJSONArray() throws JSONException {
        JSONArray actual = new JSONArray(Arrays.asList(1, 2, 3));
        JSONAssert.assertEquals("[1,2,3]", actual, false);
        JSONAssert.assertNotEquals("[1,2,4]", actual, false);
        JSONAssert.assertNotEquals("[1,3,2]", actual, true);
        JSONAssert.assertNotEquals(new JSONArray(Arrays.asList(1, 2, 4)), actual, false);
        JSONAssert.assertNotEquals(new JSONArray(Arrays.asList(1, 3, 2)), actual, true);
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
}
