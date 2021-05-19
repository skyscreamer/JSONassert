package org.skyscreamer.jsonassert;

import org.json.JSONException;
import org.junit.Test;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
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
public class JSONOverCharTest
{
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/113
    @Test
    public void testWithObjectEmpty() throws JSONException {
        JSONAssert.assertEquals("{}", "{}", JSONCompareMode.STRICT);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/113
    @Test
    public void testWithArrayEmpty() throws JSONException {
        JSONAssert.assertEquals("[]", "[]", JSONCompareMode.STRICT);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/113
    @Test(expected = AssertionError.class)
    public void testWithArrayLeftWord() throws JSONException {
        JSONAssert.assertEquals("[]", "[]a", LENIENT);
        JSONAssert.assertEquals("[]b", "[]a", LENIENT);
        JSONAssert.assertEquals("[]b", "[]a", LENIENT);
        JSONAssert.assertEquals("[{'a': 1}]", "[{'a': 1}]a", LENIENT);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/113
    @Test(expected = AssertionError.class)
    public void testWithObjectLeftword() throws JSONException {
        JSONAssert.assertEquals("{}", "{}a", LENIENT);
        JSONAssert.assertEquals("{}b", "{}a", LENIENT);
        JSONAssert.assertEquals("{}b", "a{}", LENIENT);
        JSONAssert.assertEquals("{'a': 1}", "{'a': 1}a", LENIENT);
    }

}
