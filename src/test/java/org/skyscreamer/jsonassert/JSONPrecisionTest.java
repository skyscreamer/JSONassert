package org.skyscreamer.jsonassert;

import org.json.JSONException;
import org.junit.Test;

public class JSONPrecisionTest
{
    // This test fails
    @Test
    public void testArrayCompareDifferingPrecision() throws JSONException {
        String actual =   "[{ \"Foo\" : 1.0000 }]";
        String expected = "[{ \"Foo\" : 1   }]";
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    public void testArrayCompareSamePrecision() throws JSONException {
        String actual =   "[{ \"Foo\" : 1.0   }]";
        String expected = "[{ \"Foo\" : 1.0   }]";
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    public void testObjectCompareDifferingPrecision() throws JSONException {
        String actual =   "{ \"Foo\" : 1.00 }";
        String expected = "{ \"Foo\" : 1   }";
        JSONAssert.assertEquals(expected, actual, true);
    }

    @Test
    public void testObjectCompareSamePrecision() throws JSONException {
        String actual =   "{ \"Foo\" : 1.00   }";
        String expected = "{ \"Foo\" : 1   }";
        JSONAssert.assertEquals(expected, actual, true);
    }
}