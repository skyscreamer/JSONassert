package org.skyscreamer.jsonassert;


import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


public class MyTestWithNoneEqualLengthArray {

    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/108
    /*
    Test two normal json arrays with different size
    should throw exceptions about details of each array's content
     */
    @Test
    public void test1() throws JSONException {
        String expected="[1,2,3]";
        String actual="[4,5]";
        JSONAssert.assertEquals(expected,actual,false);
    }

    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/108
    /*
    Test two complex json arrays with json object in content
    should throw exceptions about details of the json objects in each array
     */
    @Test
    public void test2() throws JSONException {
        String expected="[{a1:1},2,3]";
        String actual="[{a1:1},5]";
        JSONAssert.assertEquals(expected,actual,false);
    }

    //CS304 (manually written) Issue link: https://github.com/skyscreamer/JSONassert/issues/108
    /*
    Test more complex json arrays with nested arrays in content
    should throw exceptions about details of nested arrays of each complex array
     */
    @Test
    public void test3() throws JSONException {
        String expected="[{a1:1},[2,3],4]";
        String actual="[[2,3],5]";
        JSONAssert.assertEquals(expected,actual,false);
    }
}
