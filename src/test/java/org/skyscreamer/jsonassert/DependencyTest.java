package org.skyscreamer.jsonassert;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for our external/third-party dependencies.
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class DependencyTest {
    @Test
    public void nop() {
        // Cloudbees doesn't like a unit test class with no tests
    }

    //@Test  // For https://github.com/skyscreamer/JSONassert/issues/25
    public void testJSonGetLong() throws Exception {
        Long target = -4611686018427386614L;
        String targetString = target.toString();

        JSONObject value = new JSONObject().put("id", target);
        Assert.assertEquals(target, (Long) value.getLong("id"));  //Correct: when put as long getLong is correct

        value = new JSONObject().put("id", targetString);
        Assert.assertEquals(target, (Long) Long.parseLong(value.getString("id"))); //Correct: when put as String getString is correct
        Assert.assertEquals(target, (Long) value.getLong("id")); //Bug: Having json convert the string to long fails
    }
}
