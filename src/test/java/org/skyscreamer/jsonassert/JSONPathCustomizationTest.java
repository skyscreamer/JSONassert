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

import com.jayway.jsonpath.InvalidPathException;
import org.json.JSONArray;
import org.junit.Test;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import static org.junit.Assert.*;

/**
 * Tests for {@link JSONPathCustomization}s
 *
 * @author Shane B. (<a href="mailto:shane@wander.dev">shane@wander.dev</a>)
 */
public class JSONPathCustomizationTest {

    @Test
    public void testOfIgnore() {
        JSONPathCustomization cust = JSONPathCustomization.ofIgnore("$.foo.bar");
        assertNotNull(cust);
        assertNotNull(cust.getJsonPath());
        assertEquals("$['foo']['bar']", cust.getJsonPath().getPath());

        JSONCompareResult res = new JSONCompareResult();
        cust.matches("foo.bar", "any", "any", res);
        assertTrue(res.getMessage(), res.passed());
    }

    @Test
    public void testInvalidPath() {
        assertThrows(InvalidPathException.class, () -> new JSONPathCustomization("     ", (a, b) -> true));
    }

    @Test
    public void testGeneric() {
        JSONPathCustomization cust = new JSONPathCustomization("$.foo.bar", (a, b) -> true);
        assertNotNull(cust);
        assertNotNull(cust.getJsonPath());
        assertEquals("$['foo']['bar']", cust.getJsonPath().getPath());

        JSONCompareResult res = new JSONCompareResult();
        cust.matches("foo.bar", "any", "any", res);
        assertTrue(res.getMessage(), res.passed());
    }

    @Test
    public void testArrayValueMatcher() {
        ArrayValueMatcher<Object> arrayValueMatcher = new ArrayValueMatcher<Object>(
                new CustomComparator(JSONCompareMode.LENIENT, new Customization("foo.bar[0]", (expectedElement, actualElement) -> (int) actualElement == 1)), 0, 1);

        JSONPathCustomization cust = new JSONPathCustomization(
                "$.foo.bar",
                arrayValueMatcher
        );
        assertNotNull(cust);
        assertNotNull(cust.getJsonPath());
        assertEquals("$['foo']['bar']", cust.getJsonPath().getPath());

        JSONCompareResult res = new JSONCompareResult();
        JSONArray expected = new JSONArray();
        expected.put(1);
        expected.put(2);

        JSONArray actual = new JSONArray();
        actual.put(1);
        actual.put(2);

        cust.matches("foo.bar", actual, expected, res);
        assertTrue(res.getMessage(), res.passed());
    }
}
