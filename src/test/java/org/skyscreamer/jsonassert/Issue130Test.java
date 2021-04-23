package org.skyscreamer.jsonassert;

import org.json.JSONException;
import org.junit.Test;

/**CS304 Issue link: https://github.com/skyscreamer/JSONassert/issues/130
 * */

public class Issue130Test {
    /**CS304 Issue link: https://github.com/skyscreamer/JSONassert/issues/130
     * test case:
     * to test whether {@throws NullPointerException}
     * expected result:
     * java.lang.AssertionError: [1]
     * Expected: null
     *      got: a JSON object
     */
    @Test
    public void test130() throws JSONException {
        JSONAssert.assertEquals("[{id:1},]" , "[{id:1},{}]" , true);//not expected
    }
    /**CS304 Issue link: https://github.com/skyscreamer/JSONassert/issues/130
     * test case:
     * to test whether {@throws NullPointerException}
     * expected result:
     * java.lang.AssertionError: [1]
     * Expected: a JSON object
     *      got: null
     */
    @Test
    public void test130_0() throws JSONException {
        JSONAssert.assertEquals("[{id:1},{}]" , "[{id:1},]" , true);//expected
    }
    /**CS304 Issue link: https://github.com/skyscreamer/JSONassert/issues/130
     * test case:
     * to test the two same wrong jsons
     * expected result:
     * Test passed
     */
    @Test
    public void test130_1() throws JSONException {
        JSONAssert.assertEquals("[{id:1},]" , "[{id:1},]" , true);
    }
}