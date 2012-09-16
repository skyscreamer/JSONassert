package org.skyscreamer.jsonassert;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Provides the logic to compare two JSON entities.  This is the backend to {@link JSONAssert}, but it can
 * be programmed against directly to access the functionality.  (eg, to make something that works with a
 * non-JUnit test framework)
 */
public class JSONCompare {
    private JSONCompare() {}

    /**
     * Compares JSON string provided to the expected JSON string, and returns the results of the comparison.
     *
     * @param expectedStr Expected JSON string
     * @param actualStr JSON string to compare
     * @param mode Defines comparison behavior
     * @throws JSONException
     */
    public static JSONCompareResult compareJSON(String expectedStr, String actualStr, JSONCompareMode mode)
            throws JSONException
    {
        Object expected = JSONParser.parseJSON(expectedStr);
        Object actual = JSONParser.parseJSON(actualStr);
        if ((expected instanceof JSONObject) && (actual instanceof JSONObject)) {
            return compareJSON((JSONObject) expected, (JSONObject) actual, mode);
        }
        else if ((expected instanceof JSONArray) && (actual instanceof JSONArray)) {
            return compareJSON((JSONArray)expected, (JSONArray)actual, mode);
        }
        else if (expected instanceof JSONObject) {
            return new JSONCompareResult().fail("", "a JSON object", "a JSON array");
        }
        else {
            return new JSONCompareResult().fail("", "a JSON array", "a JSON object");
        }
    }

    /**
     * Compares JSONObject provided to the expected JSONObject, and returns the results of the comparison.
     *
     * @param expected Expected JSONObject
     * @param actual JSONObject to compare
     * @param mode Defines comparison behavior
     * @throws JSONException
     */
    public static JSONCompareResult compareJSON(JSONObject expected, JSONObject actual, JSONCompareMode mode)
            throws JSONException
    {
        JSONCompareResult result = new JSONCompareResult();
        compareJSON("", expected, actual, mode, result);
        return result;
    }

    /**
     * Compares JSONArray provided to the expected JSONArray, and returns the results of the comparison.
     *
     * @param expected Expected JSONArray
     * @param actual JSONArray to compare
     * @param mode Defines comparison behavior
     * @throws JSONException
     */
    public static JSONCompareResult compareJSON(JSONArray expected, JSONArray actual, JSONCompareMode mode)
            throws JSONException
    {
        JSONCompareResult result = new JSONCompareResult();
        compareJSONArray("", expected, actual, mode, result);
        return result;
    }

    private static void compareJSON(String prefix, JSONObject expected, JSONObject actual, JSONCompareMode mode, JSONCompareResult result)
            throws JSONException
    {
        // Check that actual contains all the expected values
        Set<String> expectedKeys = getKeys(expected);
        for(String key : expectedKeys) {
            Object expectedValue = expected.get(key);
            if (actual.has(key)) {
                Object actualValue = actual.get(key);
                String fullKey = prefix + key;
                compareValues(fullKey, expectedValue, actualValue, mode, result);
            }
            else {
                result.fail("Does not contain expected key: " + prefix + key);
            }
        }

        // If strict, check for vice-versa
        if (!mode.isExtensible()) {
            Set<String> actualKeys = getKeys(actual);
            for(String key : actualKeys) {
                if (!expected.has(key)) {
                    result.fail("Strict checking failed.  Got but did not expect: " + prefix + key);
                }
            }
        }
    }

    private static void compareValues(String fullKey, Object expectedValue, Object actualValue, JSONCompareMode mode, JSONCompareResult result) throws JSONException 
    {
        if (expectedValue.getClass().isAssignableFrom(actualValue.getClass())) {
            if (expectedValue instanceof JSONArray) {
                compareJSONArray(fullKey , (JSONArray)expectedValue, (JSONArray)actualValue, mode, result);
            }
            else if (expectedValue instanceof JSONObject) {
                compareJSON(fullKey + ".", (JSONObject) expectedValue, (JSONObject) actualValue, mode, result);
            }
            else if (!expectedValue.equals(actualValue)) {
                result.fail(fullKey, expectedValue, actualValue);
            }
        } else {
            if (isNull(expectedValue)) {
                result.fail(fullKey + ": expected null, but got " + classToType(actualValue));
            } else if (isNull(actualValue)) {
                result.fail(fullKey + ": expected " + classToType(expectedValue) + ", but got null");
            } else {
                result.fail("Values of " + fullKey + " have different types: expected " + classToType(expectedValue)
                        + ", but got " + classToType(actualValue));
            }
        }
    }

    private static boolean isNull(Object value) {
        return value.getClass().getSimpleName().equals("Null");
    }

    private static String classToType(Object value) {
        if (value instanceof JSONArray) {
            return "an array";
        } else if (value instanceof JSONObject) {
            return "an object";
        } else if (value instanceof String) {
            return "a string";
        } else {
            return value.getClass().getName();
        }
    }

    @SuppressWarnings("unchecked")
    private static void compareJSONArray(String key, JSONArray expected, JSONArray actual, JSONCompareMode mode,
                                         JSONCompareResult result) throws JSONException
    {
        if (expected.length() != actual.length()) {
            result.fail(key + "[]: Expected " + expected.length() + " values and got " + actual.length());
            return;
        }
        else if (expected.length() == 0) {
            return; // Nothing to compare
        }

        if (mode.hasStrictOrder()) {
            for(int i = 0 ; i < expected.length() ; ++i) {
                Object expectedValue = expected.get(i);
                Object actualValue = actual.get(i);
                compareValues(key + "[" + i + "]", expectedValue, actualValue, mode, result);
            }
        }
        else if (allSimpleValues(expected)) {
            Map<Object, Integer> expectedCount = CollectionUtils.getCardinalityMap(jsonArrayToList(expected));
            Map<Object, Integer> actualCount = CollectionUtils.getCardinalityMap(jsonArrayToList(actual));
            for(Object o : expectedCount.keySet()) {
                if (!actualCount.containsKey(o)) {
                    result.fail(key + "[]: Expected " + o + ", but not found");
                }
                else if (actualCount.get(o) != expectedCount.get(o)) {
                    result.fail(key + "[]: Expected contains " + expectedCount.get(o) + " " + o
                            + " actual contains " + actualCount.get(o));
                }
            }
            for(Object o : actualCount.keySet()) {
                if (!expectedCount.containsKey(o)) {
                    result.fail(key + "[]: Contains " + o + ", but not expected");
                }
            }
        }
        else if (allJSONObjects(expected)) {
            String uniqueKey = findUniqueKey(expected);
            if (uniqueKey == null || !isUsableAsUniqueKey(uniqueKey, actual)) {
                // An expensive last resort
                recursivelyCompareJSONArray(key, expected, actual, mode, result);
                return;
            }
            Map<Object, JSONObject> expectedValueMap = arrayOfJsonObjectToMap(expected, uniqueKey);
            Map<Object, JSONObject> actualValueMap = arrayOfJsonObjectToMap(actual, uniqueKey);
            for(Object id : expectedValueMap.keySet()) {
                if (!actualValueMap.containsKey(id)) {
                    result.fail(key + "[]: Expected but did not find object where " + uniqueKey + "=" + id);
                    continue;
                }
                JSONObject expectedValue = expectedValueMap.get(id);
                JSONObject actualValue = actualValueMap.get(id);
                compareValues(key + "[" + uniqueKey + "=" + id + "]", expectedValue, actualValue, mode, result);
            }
            for(Object id : actualValueMap.keySet()) {
                if (!expectedValueMap.containsKey(id)) {
                    result.fail(key + "[]: Contains object where \" + uniqueKey + \"=\" + id + \", but not expected");
                }
            }
        }
        else if (allJSONArrays(expected)) {
            // An expensive last resort
            recursivelyCompareJSONArray(key, expected, actual, mode, result);
            return;
        }
        else {
            // An expensive last resort
            recursivelyCompareJSONArray(key, expected, actual, mode, result);
            return;
        }
    }

    // This is expensive (O(n^2) -- yuck), but may be the only resort for some cases with loose array ordering, and no
    // easy way to uniquely identify each element.
    private static void recursivelyCompareJSONArray(String key, JSONArray expected, JSONArray actual,
                                                    JSONCompareMode mode, JSONCompareResult result) throws JSONException {
        Set<Integer> matched = new HashSet<Integer>();
        for(int i = 0 ; i < actual.length() ; ++i) {
            Object actualElement = actual.get(i);
            boolean matchFound = false;
            for(int j = 0 ; j < expected.length() ; ++j) {
                if (matched.contains(j) || !actual.get(i).getClass().equals(expected.get(j).getClass())) {
                    continue;
                }
                if (actualElement instanceof JSONObject) {
                    if (compareJSON((JSONObject)expected.get(j), (JSONObject)actualElement, mode).passed()) {
                        matched.add(j);
                        matchFound = true;
                        break;
                    }
                }
                else if (actualElement instanceof JSONArray) {
                    if (compareJSON((JSONArray)expected.get(j), (JSONArray)actualElement, mode).passed()) {
                        matched.add(j);
                        matchFound = true;
                        break;
                    }
                }
                else if (actualElement.equals(expected.get(j))) {
                    matched.add(j);
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
                result.fail("Could not find match for element " + actualElement);
                return;
            }
        }
    }

    private static Map<Object,JSONObject> arrayOfJsonObjectToMap(JSONArray array, String uniqueKey) throws JSONException {
        Map<Object, JSONObject> valueMap = new HashMap<Object, JSONObject>();
        for(int i = 0 ; i < array.length() ; ++i) {
            JSONObject jsonObject = (JSONObject)array.get(i);
            Object id = jsonObject.get(uniqueKey);
            valueMap.put(id, jsonObject);
        }
        return valueMap;
    }

    private static String findUniqueKey(JSONArray expected) throws JSONException {
        // Find a unique key for the object (id, name, whatever)
        JSONObject o = (JSONObject)expected.get(0); // There's at least one at this point
        for(String candidate : getKeys(o)) {
            if (isUsableAsUniqueKey(candidate, expected)) return candidate;
        }
        // No usable unique key :-(
        return null;
    }

    /**
     * {@code candidate} is usable as a unique key if every element in the
     * {@code array} is a JSONObject having that key, and no two values are the same.
     */
    private static boolean isUsableAsUniqueKey(String candidate, JSONArray array) throws JSONException {
        Set<Object> seenValues = new HashSet<Object>();
        for (int i = 0 ; i < array.length() ; i++) {
            Object item = array.get(i);
            if (item instanceof JSONObject) {
                JSONObject o = (JSONObject) item;
                if (o.has(candidate)) {
                    Object value = o.get(candidate);
                    if (isSimpleValue(value) && !seenValues.contains(value)) {
                        seenValues.add(value);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private static List<Object> jsonArrayToList(JSONArray expected) throws JSONException {
        List<Object> jsonObjects = new ArrayList<Object>(expected.length());
        for(int i = 0 ; i < expected.length() ; ++i) {
            jsonObjects.add(expected.get(i));
        }
        return jsonObjects;
    }

    private static boolean allSimpleValues(JSONArray array) throws JSONException {
        for(int i = 0 ; i < array.length() ; ++i) {
            if (!isSimpleValue(array.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSimpleValue(Object o) {
        return !(o instanceof JSONObject) && !(o instanceof JSONArray);
    }

    private static boolean allJSONObjects(JSONArray array) throws JSONException {
        for(int i = 0 ; i < array.length() ; ++i) {
            if (!(array.get(i) instanceof JSONObject)) {
                return false;
            }
        }
        return true;
    }

    private static boolean allJSONArrays(JSONArray array) throws JSONException {
        for(int i = 0 ; i < array.length() ; ++i) {
            if (!(array.get(i) instanceof JSONArray)) {
                return false;
            }
        }
        return true;
    }

    private static Set<String> getKeys(JSONObject jsonObject) {
        Set<String> keys = new TreeSet<String>();
        Iterator<?> iter = jsonObject.keys();
        while(iter.hasNext()) {
            keys.add((String)iter.next());
        }
        return keys;
    }
}
