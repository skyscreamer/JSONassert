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
