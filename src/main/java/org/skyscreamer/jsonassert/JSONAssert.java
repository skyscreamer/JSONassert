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

import org.json.JSONArray;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

/**
 * <p>A set of assertion methods useful for writing tests methods that return JSON.</p>
 *
 * <p>There are two modes, strict and non-strict.  In most cases, you will probably want
 * to set strict to <i>false</i>, since that will make the tests less brittle.</p>
 *
 * <p>Strict tests require all of the elements requested to be returned, and only those elements
 * (ie, the tests are non-extensible).  Arrays of elements must be returned in the same
 * order as expected.  For example, say I'm expecting:</p>
 *
 * <code>{id:123,things['a','b','c']}</code>
 *
 * <p>The following would match when doing non-strict checking, but would fail on strict checking:</p>
 *
 * <code>{id:123,things['c','b','a'],anotherfield:'blah'}</code>
 *
 * <p><i>This library uses org.json.  It has fewer dependencies than other JSON libraries (like net.sf.json),
 * making JSONassert more portable.</i></p>
 *
 * <p>There are two known issues when dealing with non-strict comparisons:</p>
 * <ul>
 *     <li>Unless the order is strict, checking does not handle mixed types in the JSONArray
 *         (e.g. <code>[1,2,{a:"b"}]</code> or <code>[{pet:"cat"},{car:"Ford"}]</code>)</li>
 *     <li>Unless the order is strict, checking cannot handle arrays of arrays (e.g. <code>[[1,2],[3,4]]</code>)</li>
 * </ul>
 * <p>You do not have to worry about encountering a false positive or false negative in these two edge cases.
 * <i>JSONassert</i> will identify the conditions and throw a descriptive {@link IllegalArgumentException}.  These
 * cases will be fixed in future versions.</p>
 *
 */
public class JSONAssert {
    private JSONAssert() {}

    /**
     * Asserts that the JSONObject provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expectedStr Expected JSON string
     * @param actual JSONObject to compare
     * @param strict Enables strict checking
     */
    public static void assertEquals(String expectedStr, JSONObject actual, boolean strict) {
        assertEquals(expectedStr, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }
    
    /**
     * Asserts that the JSONObject provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actual JSONObject to compare
     * @param strict Enables strict checking
     */
    public static void assertEquals(String message, String expectedStr, JSONObject actual, boolean strict) {
        assertEquals(message, expectedStr, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }

    /**
     * Asserts that the JSONObject provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @see #assertEquals(String, JSONObject, boolean)
     * 
     * @param expectedStr Expected JSON string
     * @param actual JSONObject to compare
     * @param strict Enables strict checking
     */
    public static void assertNotEquals(String expectedStr, JSONObject actual, boolean strict) {
        assertNotEquals(expectedStr, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }
    
    /**
     * Asserts that the JSONObject provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @see #assertEquals(String, JSONObject, boolean)
     * 
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actual JSONObject to compare
     * @param strict Enables strict checking
     */
    public static void assertNotEquals(String message, String expectedStr, JSONObject actual, boolean strict) {
        assertNotEquals(message, expectedStr, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }

    /**
     * Asserts that the JSONObject provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expectedStr Expected JSON string
     * @param actual JSONObject to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertEquals(String expectedStr, JSONObject actual, JSONCompareMode compareMode) {
        assertEquals("", expectedStr, actual, compareMode);
    }
    
    /**
     * Asserts that the JSONObject provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actual JSONObject to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertEquals(String message, String expectedStr, JSONObject actual, JSONCompareMode compareMode)
    {
        Object expected = JSONParser.parseJSON(expectedStr);
        if (expected instanceof JSONObject) {
            assertEquals(message, (JSONObject)expected, actual, compareMode);
        }
        else {
            throw new AssertionError("Expecting a JSON array, but passing in a JSON object");
        }
    }

    /**
     * Asserts that the JSONObject provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @see #assertEquals(String, JSONObject, JSONCompareMode)
     * 
     * @param expectedStr Expected JSON string
     * @param actual JSONObject to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertNotEquals(String expectedStr, JSONObject actual, JSONCompareMode compareMode) {
        assertNotEquals("", expectedStr, actual, compareMode);
    }
    
    /**
     * Asserts that the JSONObject provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @see #assertEquals(String, JSONObject, JSONCompareMode)
     * 
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actual JSONObject to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertNotEquals(String message, String expectedStr, JSONObject actual,
                                       JSONCompareMode compareMode) {
        Object expected = JSONParser.parseJSON(expectedStr);
        if (expected instanceof JSONObject) {
            assertNotEquals(message, (JSONObject) expected, actual, compareMode);
        }
        else {
            throw new AssertionError("Expecting a JSON array, but passing in a JSON object");
        }
    }

    /**
     * Asserts that the JSONArray provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expectedStr Expected JSON string
     * @param actual JSONArray to compare
     * @param strict Enables strict checking
     */
    public static void assertEquals(String expectedStr, JSONArray actual, boolean strict) {
        assertEquals(expectedStr, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }
    
    /**
     * Asserts that the JSONArray provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actual JSONArray to compare
     * @param strict Enables strict checking
     */
    public static void assertEquals(String message, String expectedStr, JSONArray actual, boolean strict) {
        assertEquals(message, expectedStr, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }

    /**
     * Asserts that the JSONArray provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param expectedStr Expected JSON string
     * @param actual JSONArray to compare
     * @param strict Enables strict checking
     */
    public static void assertNotEquals(String expectedStr, JSONArray actual, boolean strict) {
        assertNotEquals(expectedStr, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }
    
    /**
     * Asserts that the JSONArray provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actual JSONArray to compare
     * @param strict Enables strict checking
     */
    public static void assertNotEquals(String message, String expectedStr, JSONArray actual, boolean strict) {
        assertNotEquals(message, expectedStr, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }

    /**
     * Asserts that the JSONArray provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expectedStr Expected JSON string
     * @param actual JSONArray to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertEquals(String expectedStr, JSONArray actual, JSONCompareMode compareMode) {
        assertEquals("", expectedStr, actual, compareMode);
    }
    
    /**
     * Asserts that the JSONArray provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actual JSONArray to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertEquals(String message, String expectedStr, JSONArray actual, JSONCompareMode compareMode) {
        Object expected = JSONParser.parseJSON(expectedStr);
        if (expected instanceof JSONArray) {
            assertEquals(message, (JSONArray) expected, actual, compareMode);
        }
        else {
            throw new AssertionError("Expecting a JSON object, but passing in a JSON array");
        }
    }

    /**
     * Asserts that the JSONArray provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param expectedStr Expected JSON string
     * @param actual JSONArray to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertNotEquals(String expectedStr, JSONArray actual, JSONCompareMode compareMode) {
        Object expected = JSONParser.parseJSON(expectedStr);
        if (expected instanceof JSONArray) {
            assertNotEquals((JSONArray) expected, actual, compareMode);
        }
        else {
            throw new AssertionError("Expecting a JSON object, but passing in a JSON array");
        }
    }
    
    /**
     * Asserts that the JSONArray provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actual JSONArray to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertNotEquals(String message, String expectedStr, JSONArray actual,
                                       JSONCompareMode compareMode) {
        Object expected = JSONParser.parseJSON(expectedStr);
        if (expected instanceof JSONArray) {
            assertNotEquals(message, (JSONArray) expected, actual, compareMode);
        }
        else {
            throw new AssertionError("Expecting a JSON object, but passing in a JSON array");
        }
    }

    /**
     * Asserts that the JSONArray provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param strict Enables strict checking
     */
    public static void assertEquals(String expectedStr, String actualStr, boolean strict) {
        assertEquals(expectedStr, actualStr, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }
    
    /**
     * Asserts that the JSONArray provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param strict Enables strict checking
     */
    public static void assertEquals(String message, String expectedStr, String actualStr, boolean strict) {
        assertEquals(message, expectedStr, actualStr, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }

    /**
     * Asserts that the JSONArray provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param strict Enables strict checking
     */
    public static void assertNotEquals(String expectedStr, String actualStr, boolean strict) {
        assertNotEquals(expectedStr, actualStr, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }
    
    /**
     * Asserts that the JSONArray provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param strict Enables strict checking
     */
    public static void assertNotEquals(String message, String expectedStr, String actualStr, boolean strict) {
        assertNotEquals(message, expectedStr, actualStr, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }

    /**
     * Asserts that the JSONArray provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertEquals(String expectedStr, String actualStr, JSONCompareMode compareMode) {
        assertEquals("", expectedStr, actualStr, compareMode);
    }
    
    /**
     * Asserts that the JSONArray provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertEquals(String message, String expectedStr, String actualStr, JSONCompareMode compareMode) {
        if (expectedStr==actualStr) return;
        if (expectedStr==null){
            throw new AssertionError("Expected string is null.");
        }else if (actualStr==null){
            throw new AssertionError("Actual string is null.");
        }
        JSONCompareResult result = JSONCompare.compareJSON(expectedStr, actualStr, compareMode);
        if (result.failed()) {
            throw new AssertionError(getCombinedMessage(message, result.getMessage()));
        }
    }

    /**
     * Asserts that the JSONArray provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertNotEquals(String expectedStr, String actualStr, JSONCompareMode compareMode) {
        assertNotEquals("", expectedStr, actualStr, compareMode);
    }
    
    /**
     * Asserts that the JSONArray provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertNotEquals(String message, String expectedStr, String actualStr,
                                       JSONCompareMode compareMode) {
        JSONCompareResult result = JSONCompare.compareJSON(expectedStr, actualStr, compareMode);
        if (result.passed()) {
            throw new AssertionError(getCombinedMessage(message, result.getMessage()));
        }
    }

    /**
     * Asserts that the json string provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param comparator Comparator
     */
    public static void assertEquals(String expectedStr, String actualStr, JSONComparator comparator) {
        assertEquals("", expectedStr, actualStr, comparator);
        
    }
    
    /**
     * Asserts that the json string provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param comparator Comparator
     */
    public static void assertEquals(String message, String expectedStr, String actualStr, JSONComparator comparator) {
        JSONCompareResult result = JSONCompare.compareJSON(expectedStr, actualStr, comparator);
        if (result.failed()) {
            throw new AssertionError(getCombinedMessage(message, result.getMessage()));
        }
    }

    /**
     * Asserts that the json string provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param comparator Comparator
     */
    public static void assertNotEquals(String expectedStr, String actualStr, JSONComparator comparator) {
        assertNotEquals("", expectedStr, actualStr, comparator);
    }
    
    /**
     * Asserts that the json string provided does not match the expected string.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param message Error message to be displayed in case of assertion failure
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param comparator Comparator
     */
    public static void assertNotEquals(String message, String expectedStr, String actualStr,
                                       JSONComparator comparator) {
        JSONCompareResult result = JSONCompare.compareJSON(expectedStr, actualStr, comparator);
        if (result.passed()) {
            throw new AssertionError(getCombinedMessage(message, result.getMessage()));
        }
    }

    /**
     * Asserts that the JSONObject provided matches the expected JSONObject.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expected Expected JSONObject
     * @param actual JSONObject to compare
     * @param comparator Comparator
     */
    public static void assertEquals(JSONObject expected, JSONObject actual, JSONComparator comparator) {
        assertEquals("", expected, actual, comparator);
    }
   
    /**
     * Asserts that the JSONObject provided matches the expected JSONObject.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param message Error message to be displayed in case of assertion failure
     * @param expected Expected JSONObject
     * @param actual JSONObject to compare
     * @param comparator Comparator
     */
    public static void assertEquals(String message, JSONObject expected, JSONObject actual, JSONComparator comparator) {
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, comparator);
        if (result.failed()) {
            throw new AssertionError(getCombinedMessage(message, result.getMessage()));
        }
    }

    /**
     * Asserts that the JSONObject provided does not match the expected JSONObject.  If it is it throws an
     * {@link AssertionError}.
     *
     * @param expected Expected JSONObject
     * @param actual JSONObject to compare
     * @param comparator Comparator
     */
    public static void assertNotEquals(JSONObject expected, JSONObject actual, JSONComparator comparator) {
        assertNotEquals("", expected, actual, comparator);
    }

    /**
     * Asserts that the JSONObject provided does not match the expected JSONObject.  If it is it throws an
     * {@link AssertionError}.
     *
     * @param message Error message to be displayed in case of assertion failure
     * @param expected Expected JSONObject
     * @param actual JSONObject to compare
     * @param comparator Comparator
     */
    public static void assertNotEquals(String message, JSONObject expected, JSONObject actual,
                                       JSONComparator comparator) {
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, comparator);
        if (result.passed()) {
            throw new AssertionError(getCombinedMessage(message, result.getMessage()));
        }
    }
    
    /**
     * Asserts that the JSONObject provided matches the expected JSONObject.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expected Expected JSONObject
     * @param actual JSONObject to compare
     * @param strict Enables strict checking
     */
    public static void assertEquals(JSONObject expected, JSONObject actual, boolean strict) {
        assertEquals(expected, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }
    
    /**
     * Asserts that the JSONObject provided matches the expected JSONObject.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param message Error message to be displayed in case of assertion failure
     * @param expected Expected JSONObject
     * @param actual JSONObject to compare
     * @param strict Enables strict checking
     */
    public static void assertEquals(String message, JSONObject expected, JSONObject actual, boolean strict) {
        assertEquals(message, expected, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }

    /**
     * Asserts that the JSONObject provided does not match the expected JSONObject.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param expected Expected JSONObject
     * @param actual JSONObject to compare
     * @param strict Enables strict checking
     */
    public static void assertNotEquals(JSONObject expected, JSONObject actual, boolean strict) {
        assertNotEquals(expected, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }
    
    /**
     * Asserts that the JSONObject provided does not match the expected JSONObject.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param message Error message to be displayed in case of assertion failure
     * @param expected Expected JSONObject
     * @param actual JSONObject to compare
     * @param strict Enables strict checking
     */
    public static void assertNotEquals(String message, JSONObject expected, JSONObject actual, boolean strict) {
        assertNotEquals(message, expected, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }

    /**
     * Asserts that the JSONObject provided matches the expected JSONObject.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expected Expected JSONObject
     * @param actual JSONObject to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertEquals(JSONObject expected, JSONObject actual, JSONCompareMode compareMode) {
        assertEquals("", expected, actual, compareMode);
    }
    
    /**
     * Asserts that the JSONObject provided matches the expected JSONObject.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param message Error message to be displayed in case of assertion failure
     * @param expected Expected JSONObject
     * @param actual JSONObject to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertEquals(String message, JSONObject expected, JSONObject actual,
                                    JSONCompareMode compareMode) {
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, compareMode);
        if (result.failed()) {
            throw new AssertionError(getCombinedMessage(message, result.getMessage()));
        }
    }

    /**
     * Asserts that the JSONObject provided does not match the expected JSONObject.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param expected Expected JSONObject
     * @param actual JSONObject to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertNotEquals(JSONObject expected, JSONObject actual, JSONCompareMode compareMode) {
        assertNotEquals("", expected, actual, compareMode);
    }
    
    /**
     * Asserts that the JSONObject provided does not match the expected JSONObject.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param message Error message to be displayed in case of assertion failure
     * @param expected Expected JSONObject
     * @param actual JSONObject to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertNotEquals(String message, JSONObject expected, JSONObject actual,
                                       JSONCompareMode compareMode) {
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, compareMode);
        if (result.passed()) {
            throw new AssertionError(getCombinedMessage(message, result.getMessage()));
        }
    }

    /**
     * Asserts that the JSONArray provided matches the expected JSONArray.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expected Expected JSONArray
     * @param actual JSONArray to compare
     * @param strict Enables strict checking
     */
    public static void assertEquals(JSONArray expected, JSONArray actual, boolean strict) {
        assertEquals("", expected, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }
    
    /**
     * Asserts that the JSONArray provided matches the expected JSONArray.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param message Error message to be displayed in case of assertion failure
     * @param expected Expected JSONArray
     * @param actual JSONArray to compare
     * @param strict Enables strict checking
     */
    public static void assertEquals(String message, JSONArray expected, JSONArray actual, boolean strict) {
        assertEquals(message, expected, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }

    /**
     * Asserts that the JSONArray provided does not match the expected JSONArray.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param expected Expected JSONArray
     * @param actual JSONArray to compare
     * @param strict Enables strict checking
     */
    public static void assertNotEquals(JSONArray expected, JSONArray actual, boolean strict) {
        assertNotEquals(expected, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }
    
    /**
     * Asserts that the JSONArray provided does not match the expected JSONArray.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param message Error message to be displayed in case of assertion failure
     * @param expected Expected JSONArray
     * @param actual JSONArray to compare
     * @param strict Enables strict checking
     */
    public static void assertNotEquals(String message, JSONArray expected, JSONArray actual, boolean strict) {
        assertNotEquals(message, expected, actual, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }

    /**
     * Asserts that the JSONArray provided matches the expected JSONArray.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expected Expected JSONArray
     * @param actual JSONArray to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertEquals(JSONArray expected, JSONArray actual, JSONCompareMode compareMode) {
        assertEquals("", expected, actual, compareMode);
    }
    
    /**
     * Asserts that the JSONArray provided matches the expected JSONArray.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param message Error message to be displayed in case of assertion failure
     * @param expected Expected JSONArray
     * @param actual JSONArray to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertEquals(String message, JSONArray expected, JSONArray actual, JSONCompareMode compareMode) {
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, compareMode);
        if (result.failed()) {
            throw new AssertionError(getCombinedMessage(message, result.getMessage()));
        }
    }

    /**
     * Asserts that the JSONArray provided does not match the expected JSONArray.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param expected Expected JSONArray
     * @param actual JSONArray to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertNotEquals(JSONArray expected, JSONArray actual, JSONCompareMode compareMode) {
        assertNotEquals("", expected, actual, compareMode);
    }
    
    /**
     * Asserts that the JSONArray provided does not match the expected JSONArray.  If it is it throws an
     * {@link AssertionError}.
     * 
     * @param message Error message to be displayed in case of assertion failure
     * @param expected Expected JSONArray
     * @param actual JSONArray to compare
     * @param compareMode Specifies which comparison mode to use
     */
    public static void assertNotEquals(String message, JSONArray expected, JSONArray actual,
                                       JSONCompareMode compareMode) {
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, compareMode);
        if (result.passed()) {
            throw new AssertionError(getCombinedMessage(message, result.getMessage()));
        }
    }
    
    private static String getCombinedMessage(String message1, String message2) {
        String combinedMessage = "";
        
        if(message1 == null || "".equals(message1)) {
            combinedMessage = message2;
        } else {
            combinedMessage = message1 + " " + message2;
        }
        return combinedMessage;
    }
}
