package org.skyscreamer.jsonassert;

import static org.skyscreamer.jsonassert.Allowance.DISALLOWED;

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
       return compareJSON(expectedStr, actualStr, mode.asBehavior());
    }

	public static JSONCompareResult compareJSON(String expectedStr, String actualStr, Behavior behavior) throws JSONException
	{
		Object expected = JSONParser.parseJSON(expectedStr);
		Object actual = JSONParser.parseJSON(actualStr);
		if ((expected instanceof JSONObject) && (actual instanceof JSONObject)) {
			return compareJSON((JSONObject) expected, (JSONObject) actual, behavior);
		}
		else if ((expected instanceof JSONArray) && (actual instanceof JSONArray)) {
			return compareJSON((JSONArray)expected, (JSONArray)actual, behavior);
		}
		else if (expected instanceof JSONObject) {
			return new JSONCompareResult().fail("", expected, actual);
		}
		else {
			return new JSONCompareResult().fail("", expected, actual);
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
        return compareJSON(expected, actual, mode.asBehavior());
    }

	public static JSONCompareResult compareJSON(JSONObject expected, JSONObject actual, Behavior behavior) throws JSONException
	{
		JSONCompareResult result = new JSONCompareResult();
		compareJSON("", expected, actual, behavior, result);
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
        return compareJSON(expected, actual, mode.asBehavior());
    }

	public static JSONCompareResult compareJSON(JSONArray expected, JSONArray actual, Behavior behavior) throws JSONException
	{
		JSONCompareResult result = new JSONCompareResult();
		compareJSONArray("", expected, actual, behavior, result);
		return result;
	}

    private static void compareJSON(String prefix, JSONObject expected, JSONObject actual, Behavior behavior, JSONCompareResult result)
            throws JSONException
    {
        // Check that actual contains all the expected values
	    for(String key : getKeys(expected)) {
            Object expectedValue = expected.get(key);
            if (actual.has(key)) {
                Object actualValue = actual.get(key);
                compareValues(qualify(prefix, key), expectedValue, actualValue, behavior, result);
            }
            else {
                result.missing(prefix, key);
            }
        }

        // If non-extensible, check for vice-versa
        if (behavior.extraFieldsAre(DISALLOWED)) {
	        for(String key : getKeys(actual)) {
                if (!expected.has(key)) {
                    result.unexpected(prefix, key);
                }
            }
        }
    }

    private static String qualify(String prefix, String key) {
        return "".equals(prefix) ? key : prefix + "." + key;
    }

    private static void compareValues(String fullKey, Object expectedValue, Object actualValue, Behavior behavior, JSONCompareResult result) throws JSONException
    {
        Customization customization = behavior.getCustomization(fullKey);
        if (customization != null) {
            if (!customization.matches(actualValue, expectedValue)) {
                result.fail(fullKey, expectedValue, actualValue);
            }
        } else if (expectedValue.getClass().isAssignableFrom(actualValue.getClass())) {
            if (expectedValue instanceof JSONArray) {
                compareJSONArray(fullKey , (JSONArray)expectedValue, (JSONArray)actualValue, behavior, result);
            }
            else if (expectedValue instanceof JSONObject) {
                compareJSON(fullKey, (JSONObject) expectedValue, (JSONObject) actualValue, behavior, result);
            }
            else if (!expectedValue.equals(actualValue)) {
                result.fail(fullKey, expectedValue, actualValue);
            }
        } else {
            result.fail(fullKey, expectedValue, actualValue);
        }
    }

    @SuppressWarnings("unchecked")
    private static void compareJSONArray(String key, JSONArray expected, JSONArray actual, Behavior behavior,
                                         JSONCompareResult result) throws JSONException
    {
        if (expected.length() != actual.length()) {
            result.fail(key + "[]: Expected " + expected.length() + " values but got " + actual.length());
            return;
        }
        else if (expected.length() == 0) {
            return; // Nothing to compare
        }

        if (behavior.anyArrayOrderIs(DISALLOWED)) {
            for(int i = 0 ; i < expected.length() ; ++i) {
                Object expectedValue = expected.get(i);
                Object actualValue = actual.get(i);
                compareValues(key + "[" + i + "]", expectedValue, actualValue, behavior, result);
            }
        }
        else if (allSimpleValues(expected)) {
            Map<Object, Integer> expectedCount = CollectionUtils.getCardinalityMap(jsonArrayToList(expected));
            Map<Object, Integer> actualCount = CollectionUtils.getCardinalityMap(jsonArrayToList(actual));
            for(Object o : expectedCount.keySet()) {
                if (!actualCount.containsKey(o)) {
                    result.missing(key + "[]", o);
                }
                else if (!actualCount.get(o).equals(expectedCount.get(o))) {
                    result.fail(key + "[]: Expected " + expectedCount.get(o) + " occurrence(s) of " + o
                            + " but got " + actualCount.get(o) + " occurrence(s)");
                }
            }
            for(Object o : actualCount.keySet()) {
                if (!expectedCount.containsKey(o)) {
                    result.unexpected(key + "[]", o);
                }
            }
        }
        else if (allJSONObjects(expected)) {
            String uniqueKey = findUniqueKey(expected);
            if (uniqueKey == null || !isUsableAsUniqueKey(uniqueKey, actual)) {
                // An expensive last resort
                recursivelyCompareJSONArray(key, expected, actual, behavior, result);
                return;
            }
            Map<Object, JSONObject> expectedValueMap = arrayOfJsonObjectToMap(expected, uniqueKey);
            Map<Object, JSONObject> actualValueMap = arrayOfJsonObjectToMap(actual, uniqueKey);
            for(Object id : expectedValueMap.keySet()) {
                if (!actualValueMap.containsKey(id)) {
                    result.missing(formatUniqueKey(key, uniqueKey, id), expectedValueMap.get(id));
                    continue;
                }
                JSONObject expectedValue = expectedValueMap.get(id);
                JSONObject actualValue = actualValueMap.get(id);
                compareValues(formatUniqueKey(key, uniqueKey, id), expectedValue, actualValue, behavior, result);
            }
            for(Object id : actualValueMap.keySet()) {
                if (!expectedValueMap.containsKey(id)) {
                    result.unexpected(formatUniqueKey(key, uniqueKey, id), actualValueMap.get(id));
                }
            }
        }
        else if (allJSONArrays(expected)) {
            // An expensive last resort
            recursivelyCompareJSONArray(key, expected, actual, behavior, result);
            return;
        }
        else {
            // An expensive last resort
            recursivelyCompareJSONArray(key, expected, actual, behavior, result);
            return;
        }
    }

    private static String formatUniqueKey(String key, String uniqueKey, Object value) {
        return key + "[" + uniqueKey + "=" + value + "]";
    }

    // This is expensive (O(n^2) -- yuck), but may be the only resort for some cases with loose array ordering, and no
    // easy way to uniquely identify each element.
    private static void recursivelyCompareJSONArray(String key, JSONArray expected, JSONArray actual,
                                                    Behavior behavior, JSONCompareResult result) throws JSONException {
        Set<Integer> matched = new HashSet<Integer>();
        for(int i = 0 ; i < expected.length() ; ++i) {
            Object expectedElement = expected.get(i);
            boolean matchFound = false;
            for(int j = 0 ; j < actual.length() ; ++j) {
                Object actualElement = actual.get(j);
				if (matched.contains(j) || !actualElement.getClass().equals(expectedElement.getClass())) {
                    continue;
                }
                if (expectedElement instanceof JSONObject) {
                    if (compareJSON((JSONObject)expectedElement, (JSONObject)actualElement, behavior).passed()) {
                        matched.add(j);
                        matchFound = true;
                        break;
                    }
                }
                else if (expectedElement instanceof JSONArray) {
                    if (compareJSON((JSONArray)expectedElement, (JSONArray)actualElement, behavior).passed()) {
                        matched.add(j);
                        matchFound = true;
                        break;
                    }
                }
                else if (expectedElement.equals(actualElement)) {
                    matched.add(j);
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
                result.fail(key + "[" + i + "] Could not find match for element " + expectedElement);
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
