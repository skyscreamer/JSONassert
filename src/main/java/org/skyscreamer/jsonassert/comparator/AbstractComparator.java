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

package org.skyscreamer.jsonassert.comparator;

import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.arrayOfJsonObjectToMap;
import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.findUniqueKey;
import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.formatUniqueKey;
import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.getKeys;
import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.isUsableAsUniqueKey;
import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.jsonArrayToList;
import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.qualify;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompareResult;

/**
 * This class provides a skeletal implementation of the {@link JSONComparator}
 * interface, to minimize the effort required to implement this interface.
 */
public abstract class AbstractComparator implements JSONComparator {

    /**
     * Compares JSONObject provided to the expected JSONObject, and returns the results of the comparison.
     *
     * @param expected Expected JSONObject
     * @param actual   JSONObject to compare
     * @throws JSONException JSON parsing error
     */
    @Override
    public final JSONCompareResult compareJSON(JSONObject expected, JSONObject actual, List<String> keysToIgnore) throws JSONException {
        JSONCompareResult result = new JSONCompareResult();
        compareJSON("", expected, actual, keysToIgnore, result);
        return result;
    }

    /**
     * Compares JSONArray provided to the expected JSONArray, and returns the results of the comparison.
     *
     * @param expected Expected JSONArray
     * @param actual   JSONArray to compare
     * @throws JSONException JSON parsing error
     */
    @Override
    public final JSONCompareResult compareJSONArray(JSONArray expected, JSONArray actual, List<String> keysToIgnore) throws JSONException {
        JSONCompareResult result = new JSONCompareResult();
        compareJSONArray("", expected, actual, keysToIgnore, result);
        return result;
    }

    protected void checkJsonObjectKeysActualInExpected(String prefix, JSONObject expected, JSONObject actual, List<String> keysToIgnore, JSONCompareResult result) {
        Set<String> actualKeys = getKeys(actual);
        for (String key : actualKeys) {
        	String keyPath = qualify(prefix, key);
            if(keysToIgnore.contains(keyPath)) {
            	System.out.println("[JSONComparator] Ignore key: " + keyPath);
            	
            	continue;
            }
            
            if (!expected.has(key)) {
                result.unexpected(prefix, key);
            }
        }
    }

    protected void checkJsonObjectKeysExpectedInActual(String prefix, JSONObject expected, JSONObject actual, List<String> keysToIgnore, JSONCompareResult result) throws JSONException {
        Set<String> expectedKeys = getKeys(expected);
        for (String key : expectedKeys) {
            Object expectedValue = expected.get(key);
            if (actual.has(key)) {
                Object actualValue = actual.get(key);
                String keyPath = qualify(prefix, key);
                if(keysToIgnore.contains(keyPath)) {
                	System.out.println("[JSONComparator] Ignore key: " + keyPath);
                	
                	continue;
                }
                
               if( keysToIgnore.stream().filter(k -> {
                	if(k.contains("[*]")) {
                		return true;
                	}
                	else {
                		return false;
                	}
                	
                }).collect(Collectors.toList()).contains(keyPath.replaceAll("\\[[0-9]+\\]", "\\[\\*\\]"))) {
                	System.out.println("[JSONComparator] Ignore key: " + keyPath);
                	
                	continue;
                }
                
                compareValues(keyPath, expectedValue, actualValue, keysToIgnore, result);
            } else {
                result.missing(prefix, key);
            }
        }
    }

    protected void compareJSONArrayOfJsonObjects(String key, JSONArray expected, JSONArray actual, List<String> keysToIgnore, JSONCompareResult result) throws JSONException {
        String uniqueKey = findUniqueKey(expected);
        if (uniqueKey == null || !isUsableAsUniqueKey(uniqueKey, actual)) {
            // An expensive last resort
            recursivelyCompareJSONArray(key, expected, actual, keysToIgnore, result);
            return;
        }
        Map<Object, JSONObject> expectedValueMap = arrayOfJsonObjectToMap(expected, uniqueKey);
        Map<Object, JSONObject> actualValueMap = arrayOfJsonObjectToMap(actual, uniqueKey);
        for (Object id : expectedValueMap.keySet()) {
            if (!actualValueMap.containsKey(id)) {
                result.missing(formatUniqueKey(key, uniqueKey, id), expectedValueMap.get(id));
                continue;
            }
            JSONObject expectedValue = expectedValueMap.get(id);
            JSONObject actualValue = actualValueMap.get(id);
            compareValues(formatUniqueKey(key, uniqueKey, id), expectedValue, actualValue, keysToIgnore, result);
        }
        for (Object id : actualValueMap.keySet()) {
            if (!expectedValueMap.containsKey(id)) {
                result.unexpected(formatUniqueKey(key, uniqueKey, id), actualValueMap.get(id));
            }
        }
    }

    protected void compareJSONArrayOfSimpleValues(String key, JSONArray expected, JSONArray actual, List<String> keysToIgnore, JSONCompareResult result) throws JSONException {
        Map<Object, Integer> expectedCount = JSONCompareUtil.getCardinalityMap(jsonArrayToList(expected));
        Map<Object, Integer> actualCount = JSONCompareUtil.getCardinalityMap(jsonArrayToList(actual));
        for (Object o : expectedCount.keySet()) {
            if(keysToIgnore!=null && keysToIgnore.contains(key)) {
            	System.out.println("[JSONComparator] Ignore key: " + key);
            	
            	continue;
            }
            
            if (!actualCount.containsKey(o)) {
                result.missing(key + "[]", o);
            } else if (!actualCount.get(o).equals(expectedCount.get(o))) {
                result.fail(key + "[]: Expected " + expectedCount.get(o) + " occurrence(s) of " + o
                        + " but got " + actualCount.get(o) + " occurrence(s)");
            }
        }
        for (Object o : actualCount.keySet()) {
            if (!expectedCount.containsKey(o)) {
                result.unexpected(key + "[]", o);
            }
        }
    }

    protected void compareJSONArrayWithStrictOrder(String key, JSONArray expected, JSONArray actual, List<String> keysToIgnore, JSONCompareResult result) throws JSONException {
        for (int i = 0; i < expected.length(); ++i) {
            Object expectedValue = expected.get(i);
            Object actualValue = actual.get(i);
            compareValues(key + "[" + i + "]", expectedValue, actualValue, keysToIgnore, result);
        }
    }

    // This is expensive (O(n^2) -- yuck), but may be the only resort for some cases with loose array ordering, and no
    // easy way to uniquely identify each element.
    // This is expensive (O(n^2) -- yuck), but may be the only resort for some cases with loose array ordering, and no
    // easy way to uniquely identify each element.
    protected void recursivelyCompareJSONArray(String key, JSONArray expected, JSONArray actual, List<String> keysToIgnore,
                                               JSONCompareResult result) throws JSONException {
        Set<Integer> matched = new HashSet<Integer>();
        for (int i = 0; i < expected.length(); ++i) {
            Object expectedElement = expected.get(i);
            boolean matchFound = false;
            for (int j = 0; j < actual.length(); ++j) {
                Object actualElement = actual.get(j);
                if (matched.contains(j) || !actualElement.getClass().equals(expectedElement.getClass())) {
                    continue;
                }
                if (expectedElement instanceof JSONObject) {
                    if (compareJSON((JSONObject) expectedElement, (JSONObject) actualElement, keysToIgnore).passed()) {
                        matched.add(j);
                        matchFound = true;
                        break;
                    }
                } else if (expectedElement instanceof JSONArray) {
                    if (compareJSONArray((JSONArray) expectedElement, (JSONArray) actualElement, keysToIgnore).passed()) {
                        matched.add(j);
                        matchFound = true;
                        break;
                    }
                } else if (expectedElement.equals(actualElement)) {
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
}
