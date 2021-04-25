package org.skyscreamer.jsonassert;

import org.json.JSONException;
import org.junit.Test;

public class BigNumberTest {
    // This test fails
    @Test
    public void testBigDecimalDifferent() throws JSONException {
        JSONAssert.assertNotEquals(
                "{ \"value\": 1234567890.1234567890123456 }",
                "{ \"value\": 1234567890.12345678900000 }",
                true
        );

    }


    @Test
    public void testBigDecimalEqual() throws JSONException {
        JSONAssert.assertEquals(
                "{ \"value\": 1234567890.1234567890123456 }",
                "{ \"value\": 1234567890.1234567890123456 }",
                true
        );
    }

    @Test
    public void testBigNumberDifferent() throws JSONException {
        JSONAssert.assertNotEquals(
                "{ \"value\": 12345678901234567890123456 }",
                "{ \"value\": 12345678901234567800000000 }",
                true
        );
    }

    @Test
    public void testBigNumberEqual() throws JSONException {
        JSONAssert.assertEquals(
                "{ \"value\": 12345678901234567890123456 }",
                "{ \"value\": 12345678901234567890123456 }",
                true
        );
    }

    @Test
    public void testNumberDemicalEqual() throws JSONException {
        JSONAssert.assertEquals(
                "{ \"value\": 12345678901234567890123456 }",
                "{ \"value\": 12345678901234567890123456.0000000000000 }",
                true
        );
    }

    @Test
    public void testSmallNumberDemicalEqual1() throws JSONException {
        JSONAssert.assertEquals(
                "{ \"value\": 1.23 }",
                "{ \"value\": 1.2300000000 }",
                true
        );
    }

    @Test
    public void testSmallNumberDemicalEqual2() throws JSONException {
        JSONAssert.assertEquals(
                "{ \"value\": 1 }",
                "{ \"value\": 1.00000000 }",
                true
        );
    }

}
