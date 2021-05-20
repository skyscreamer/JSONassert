package org.skyscreamer.jsonassert;

import org.json.JSONException;
import org.junit.Test;

public class JSONPrecisionTest
{
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/115
    @Test
    public void testArrayCompareDifferingPrecision() throws JSONException {
        String actual =   "[{ \"Foo\" : 1.0000 }]";
        String expected = "[{ \"Foo\" : 1   }]";
        JSONAssert.assertEquals(expected, actual, false);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/115
    @Test
    public void testArrayCompareSamePrecision() throws JSONException {
        String actual =   "[{ \"Foo\" : 1.0   }]";
        String expected = "[{ \"Foo\" : 1.0   }]";
        JSONAssert.assertEquals(expected, actual, false);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/115
    @Test
    public void testObjectCompareDifferingPrecision() throws JSONException {
        String actual =   "{ \"Foo\" : 1.00 }";
        String expected = "{ \"Foo\" : 1   }";
        JSONAssert.assertEquals(expected, actual, true);
    }
    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/115
    @Test
    public void testObjectCompareSamePrecision() throws JSONException {
        String actual =   "{ \"Foo\" : 1.00   }";
        String expected = "{ \"Foo\" : 1   }";
        JSONAssert.assertEquals(expected, actual, true);
    }
}
