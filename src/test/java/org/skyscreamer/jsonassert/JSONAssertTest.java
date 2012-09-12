package org.skyscreamer.jsonassert;

import java.util.Arrays;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import static org.skyscreamer.jsonassert.JSONCompareMode.*;

/**
 * Unit tests for {@link JSONAssert}
 */
public class JSONAssertTest {
    @Test
    public void testSimple() throws JSONException {
        testPass("{id:1}", "{id:1}", true);
        testFail("{id:1}", "{id:2}", true);
        testPass("{id:1}", "{id:1}", false);
        testFail("{id:1}", "{id:2}", false);
    }

    @Test
    public void testSimpleStrict() throws JSONException {
        testPass("{id:1}", "{id:1,name:\"Joe\"}", false);
        testFail("{id:1}", "{id:1,name:\"Joe\"}", true);
    }

    @Test
    public void testReversed() throws JSONException {
        testPass("{name:\"Joe\",id:1}", "{id:1,name:\"Joe\"}", false);
        testPass("{name:\"Joe\",id:1}", "{id:1,name:\"Joe\"}", true);
    }

    @Test // Currently JSONAssert assumes JSONObject.
    public void testArray() throws JSONException {
        testPass("[1,2,3]","[1,2,3]", true);
        testPass("[1,2,3]","[1,3,2]", false);
        testFail("[1,2,3]","[1,3,2]", true);
        testFail("[1,2,3]","[4,5,6]", false);
    }

    @Test
    public void testNested() throws JSONException {
        testPass("{id:1,address:{addr1:\"123 Main\", addr2:null, city:\"Houston\", state:\"TX\"}}",
                "{id:1,address:{addr1:\"123 Main\", addr2:null, city:\"Houston\", state:\"TX\"}}", true);
        testFail("{id:1,address:{addr1:\"123 Main\", addr2:null, city:\"Houston\", state:\"TX\"}}",
                "{id:1,address:{addr1:\"123 Main\", addr2:null, city:\"Austin\", state:\"TX\"}}", true);
    }

    @Test
    public void testVeryNested() throws JSONException {
        testPass("{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{p:\"blah\"}}}}}}}}}}}}}}}}",
                "{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{p:\"blah\"}}}}}}}}}}}}}}}}", true);
        testFail("{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{p:\"blah\"}}}}}}}}}}}}}}}}",
                "{a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{l:{m:{n:{o:{z:\"blah\"}}}}}}}}}}}}}}}}", true);
    }

    @Test
    public void testSimpleArray() throws JSONException {
        testPass("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Exact to exact (strict)
                "{id:1,pets:[\"dog\",\"cat\",\"fish\"]}",
                true);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Out-of-order fails (strict)
                "{id:1,pets:[\"dog\",\"fish\",\"cat\"]}",
                true);
        testPass("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Out-of-order ok otherwise
                "{id:1,pets:[\"dog\",\"fish\",\"cat\"]}",
                false);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Mismatch (strict)
                "{id:1,pets:[\"dog\",\"cat\",\"bird\"]}",
                true);
        testFail("{id:1,pets:[\"dog\",\"cat\",\"fish\"]}", // Mismatch
                "{id:1,pets:[\"dog\",\"cat\",\"bird\"]}",
                false);
    }

    @Test
    public void testSimpleMixedArray() throws JSONException {
        testPass("{stuff:[321, \"abc\"]}", "{stuff:[\"abc\", 321]}", false);
        testFail("{stuff:[321, \"abc\"]}", "{stuff:[\"abc\", 789]}", false);
    }

    @Test
    public void testComplexMixedStrictArray() throws JSONException {
        testPass("{stuff:[{pet:\"cat\"},{car:\"Ford\"}]}", "{stuff:[{pet:\"cat\"},{car:\"Ford\"}]}", true);
    }

    @Test
    public void testComplexMixedArray() throws JSONException {
        testPass("{stuff:[{pet:\"cat\"},{car:\"Ford\"}]}", "{stuff:[{pet:\"cat\"},{car:\"Ford\"}]}", false);
    }

    @Test
    public void testComplexArrayNoUniqueID() throws JSONException {
        testPass("{stuff:[{address:{addr1:\"123 Main\"}}, {address:{addr1:\"234 Broad\"}}]}",
                "{stuff:[{address:{addr1:\"123 Main\"}}, {address:{addr1:\"234 Broad\"}}]}",
                false);
    }

    @Test
    public void testSimpleAndComplexStrictArray() throws JSONException {
        testPass("{stuff:[123,{a:\"b\"}]}", "{stuff:[123,{a:\"b\"}]}", true);
    }

    @Test
    public void testSimpleAndComplexArray() throws JSONException {
        testPass("{stuff:[123,{a:\"b\"}]}", "{stuff:[123,{a:\"b\"}]}", false);
    }

    @Test
    public void testComplexArray() throws JSONException {
        testPass("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                 "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                 true); // Exact to exact (strict)
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                true); // Out-of-order fails (strict)
        testPass("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:3,name:\"Sue\",pets:[\"fish\",\"bird\"]},{id:2,name:\"Pat\",pets:[\"dog\"]}],pets:[]}",
                false); // Out-of-order ok otherwise
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
                true); // Mismatch (strict)
        testFail("{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}",
                "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}",
                false); // Mismatch
    }

    @Test
    public void testArrayOfArraysStrict() throws JSONException {
        testPass("{id:1,stuff:[[1,2],[2,3],[],[3,4]]}", "{id:1,stuff:[[1,2],[2,3],[],[3,4]]}", true);
        testFail("{id:1,stuff:[[1,2],[2,3],[3,4],[]]}", "{id:1,stuff:[[1,2],[2,3],[],[3,4]]}", true);
    }

    @Test
    public void testArrayOfArrays() throws JSONException {
        testPass("{id:1,stuff:[[4,3],[3,2],[],[1,2]]}", "{id:1,stuff:[[1,2],[2,3],[],[3,4]]}", false);
    }
    
    @Test
    public void testLenientArrayRecursion() throws JSONException {
        testPass("[{\"arr\":[5, 2, 1]}]", "[{\"b\":3, \"arr\":[1, 5, 2]}]", false);
    }
   
    @Test 
    public void testFieldMismatch() throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON("{name:\"Pat\"}", "{name:\"Sue\"}", STRICT);
        Assert.assertEquals("Pat", result.getExpected());
        Assert.assertEquals("Sue", result.getActual());
        Assert.assertEquals("name", result.getField());

        FieldComparisonFailure comparisonFailure = result.getFieldFailures().iterator().next();
        Assert.assertEquals("Pat", comparisonFailure.getExpected());
        Assert.assertEquals("Sue", comparisonFailure.getActual());
        Assert.assertEquals("name", comparisonFailure.getField());
    }

    @Test
    public void testNullProperty() throws JSONException {
        testFail("{id:1,name:\"Joe\"}", "{id:1,name:null}", true);
        testFail("{id:1,name:null}", "{id:1,name:\"Joe\"}", true);
    }

    @Test
    public void testIncorrectTypes() throws JSONException {
        testFail("{id:1,name:\"Joe\"}", "{id:1,name:[]}", true);
        testFail("{id:1,name:[]}", "{id:1,name:\"Joe\"}", true);
    }

    @Test
    public void testNullEquality() throws JSONException {
        testPass("{id:1,name:null}", "{id:1,name:null}", true);
    }

    private void testPass(String expected, String actual, boolean strict)
        throws JSONException
    {
        String message = expected + " == " + actual;
        if (strict) {
            message += "(strict)";
        }
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, strict ? STRICT : LENIENT);
        Assert.assertTrue(message + "\n  " + result.getMessage(), result.passed());
    }

    private void testFail(String expected, String actual, boolean strict)
        throws JSONException
    {
        String message = expected + " != " + actual;
        if (strict) {
            message += "(strict)";
        }
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, strict ? STRICT : LENIENT);
        Assert.assertTrue(message, result.failed());
    }
}
