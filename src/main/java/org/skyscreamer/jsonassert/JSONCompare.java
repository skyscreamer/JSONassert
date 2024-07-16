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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Provides API to compare two JSON entities.  This is the backend to {@link JSONAssert}, but it can
 * be programmed against directly to access the functionality.  (eg, to make something that works with a
 * non-JUnit test framework)
 */
public final class JSONCompare {
    private JSONCompare() {
    }

    private static JSONComparator getComparatorForMode(JSONCompareMode mode) {
        return new DefaultComparator(mode);
    }

    /**
     * Compares JSON string provided to the expected JSON string using provided comparator, and returns the results of
     * the comparison.
     * @param expectedStr Expected JSON string
     * @param actualStr JSON string to compare
     * @param comparator Comparator to use
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     * @throws IllegalArgumentException when type of expectedStr doesn't match the type of actualStr
     */
    public static JSONCompareResult compareJSON(String expectedStr, String actualStr, JSONComparator comparator)
            throws JSONException {
        Object expected = JSONParser.parseJSON(expectedStr);
        Object actual = JSONParser.parseJSON(actualStr);
        if ((expected instanceof JSONObject) && (actual instanceof JSONObject)) {
            return compareJSON((JSONObject) expected, (JSONObject) actual, comparator);
        }
        else if ((expected instanceof JSONArray) && (actual instanceof JSONArray)) {
            return compareJSON((JSONArray)expected, (JSONArray)actual, comparator);
        }
        else if (expected instanceof JSONString && actual instanceof JSONString) {
            return compareJson((JSONString) expected, (JSONString) actual);
        }
        else if (expected instanceof JSONObject) {
            return new JSONCompareResult().fail("", expected, actual);
        }
        else {
            return new JSONCompareResult().fail("", expected, actual);
        }
    }

  /**
     * Compares JSON object provided to the expected JSON object using provided comparator, and returns the results of
     * the comparison.
     * @param expected expected json object
     * @param actual actual json object
     * @param comparator comparator to use
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     */
    public static JSONCompareResult compareJSON(JSONObject expected, JSONObject actual, JSONComparator comparator)
            throws JSONException {
        return comparator.compareJSON(expected, actual);
    }

    /**
     * Compares JSON object provided to the expected JSON object using provided comparator, and returns the results of
     * the comparison.
     * @param expected expected json array
     * @param actual actual json array
     * @param comparator comparator to use
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     */
    public static JSONCompareResult compareJSON(JSONArray expected, JSONArray actual, JSONComparator comparator)
            throws JSONException {
        return comparator.compareJSON(expected, actual);
    }

    /**
     * Compares {@link JSONString} provided to the expected {@code JSONString}, checking that the
     * {@link org.json.JSONString#toJSONString()} are equal.
     *
     * @param expected Expected {@code JSONstring}
     * @param actual   {@code JSONstring} to compare
     * @return result of the comparison
     */
    public static JSONCompareResult compareJson(final JSONString expected, final JSONString actual) {
        final JSONCompareResult result = new JSONCompareResult();
        final String expectedJson = expected.toJSONString();
        final String actualJson = actual.toJSONString();
        if (!expectedJson.equals(actualJson)) {
          result.fail("");
        }
        return result;
    }

    /**
     * Compares JSON string provided to the expected JSON string, and returns the results of the comparison.
     *
     * @param expectedStr Expected JSON string
     * @param actualStr   JSON string to compare
     * @param mode        Defines comparison behavior
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     */
    public static JSONCompareResult compareJSON(String expectedStr, String actualStr, JSONCompareMode mode)
            throws JSONException {
        return compareJSON(expectedStr, actualStr, getComparatorForMode(mode));
    }
    
    //CS304 Issue link: https://github.com/skyscreamer/JSONassert/issues/129
    
    /**
     * The recursive method to check if the JSONObject has the target field. 
     * If not, return false; if have, remove the target field and return true. 
     * 
     * @param target The target field that we want to check
     * @param obj The target JSONObject that we want to check and remove the target field
     * @return A boolean value indicates whether the target field is found
     * @throws JSONException JSON parsing error
     */
    protected static boolean checkAndRemove(String target, JSONObject obj) throws JSONException {
        boolean result = false;
        Iterator<String> iterator = obj.keys();
        while (iterator.hasNext()){
            String key = iterator.next();
            Object value = obj.get(key);
            if (value instanceof JSONObject){
                result = checkAndRemove(target, (JSONObject)value);
            }else if (value instanceof JSONArray){
                result = false;
                JSONArray valueArray = (JSONArray) value;
                for (int i = 0; i < valueArray.length(); i++){
                    if (valueArray.get(i) instanceof JSONObject) {
                        result = result | checkAndRemove(target, ((JSONArray) value).getJSONObject(i));
                    }else if (valueArray.get(i) instanceof JSONArray){
                        result = result | checkAndRemove(target, (JSONArray) ((JSONArray) value).get(i));
                    }
                }
            }
        }
        if (obj.has(target)){
            result = true;
            obj.remove(target);
        }
        return result;
    }

    //CS304 Issue link: https://github.com/skyscreamer/JSONassert/issues/129

    /**
     * The recursive method to check if a JSON Array contains target field.
     * Add this method to avoid recursively constructed JSON Array
     * 
     * @param target The target field that we want to check
     * @param obj he target JSONArray that we want to check and remove the target field
     * @return A boolean value indicates whether the JSONArray contains the target field. 
     * @throws JSONException JSON parsing error
     */
    protected static boolean checkAndRemove(String target, JSONArray obj) throws JSONException {
        boolean result = false;
        for (int i = 0; i < obj.length(); i++){
            if (obj.get(i) instanceof JSONArray){
                result = result | checkAndRemove(target, (JSONArray) obj.get(i));
            }else if (obj.get(i) instanceof JSONObject){
                result = result | checkAndRemove(target, (JSONObject) obj.get(i));
            }
        }
        return result;
    }

    //CS304 Issue link: https://github.com/skyscreamer/JSONassert/issues/129

    /**
     * Provide a ignore list, compare the two JSONObject ignoring these fields. 
     * If the ignore list is empty, which means you don't want to ignore anything, then, please use other method without ignore. 
     * 
     * @param expect Expected JSONObject
     * @param actual JSONObject to compare
     * @param ignoreList The list of field name (in String) wanted to ignore
     * @param mode Defines comparison behavior
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     */
    public static JSONCompareResult compareJSONWithIgnore(JSONObject expect, JSONObject actual, ArrayList<String> ignoreList, JSONCompareMode mode) throws JSONException {
        if (ignoreList.size() == 0){
            JSONCompareResult result = new JSONCompareResult();
            result.fail("Please use other mode if don't want to ignore any field");
            return result;
        }
        ArrayList<String> errorFields = new ArrayList<String>();
        for (String toIgnore : ignoreList) {
            boolean have = checkAndRemove(toIgnore, expect) | checkAndRemove(toIgnore, actual);
            if (!have){
                errorFields.add(toIgnore);
            }
        }
        if (errorFields.size() == 0){
            return compareJSON(expect, actual, mode);
        }else{
            JSONCompareResult result = new JSONCompareResult();
            StringBuilder message = new StringBuilder("Following ignore field(s) not found:\n");
            for (String field: errorFields){
                message.append(field);
                message.append("\n");
            }
            result.fail(message.toString());
            return result;
        }
    }

    //CS304 Issue link: https://github.com/skyscreamer/JSONassert/issues/129

    /**
     * Provide a ignore list, compare the two JSONArray ignoring these fields. 
     * If the ignore list is empty, which means you don't want to ignore anything, then, please use other method without ignore. 
     * @param expect Expected JSONArray
     * @param actual JSONArray to compare
     * @param ignoreList The list of field name (in String) wanted to ignore
     * @param mode Defines comparison behavior
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     */
    public static JSONCompareResult compareJSONWithIgnore(JSONArray expect, JSONArray actual, ArrayList<String> ignoreList, JSONCompareMode mode) throws JSONException{
        if (ignoreList.size() == 0){
            JSONCompareResult result = new JSONCompareResult();
            result.fail("Please use other mode if don't want to ignore any field");
            return result;
        }
        ArrayList<String> errorFields = new ArrayList<String>();
        for (String toIgnore: ignoreList){
            boolean have = false;
            for (int i = 0; i < expect.length(); i++){
                if (expect.get(i) instanceof JSONObject) {
                    JSONObject element = expect.getJSONObject(i);
                    have = checkAndRemove(toIgnore, element) | have;
                }else if (expect.get(i) instanceof JSONArray){
                    JSONArray element = (JSONArray) expect.get(i);
                    have = checkAndRemove(toIgnore, element);
                }

            }
            for (int i = 0; i < actual.length(); i++){
                if (actual.get(i) instanceof JSONObject) {
                    JSONObject element = actual.getJSONObject(i);
                    have = checkAndRemove(toIgnore, element) | have;
                }else if (actual.get(i) instanceof JSONArray){
                    JSONArray element = (JSONArray) actual.get(i);
                    have = checkAndRemove(toIgnore, element) | have;
                }
            }
            if (!have){
                errorFields.add(toIgnore);
            }
        }
        if (errorFields.size() == 0){
            return compareJSON(expect, actual, mode);
        }else{
            JSONCompareResult result = new JSONCompareResult();
            StringBuilder message = new StringBuilder("Following ignore field(s) not found:\n");
            for (String field: errorFields){
                message.append(field);
                message.append("\n");
            }
            result.fail(message.toString());
            return result;
        }
    }

    //CS304 Issue link: https://github.com/skyscreamer/JSONassert/issues/129

    /**
     * Compare JSON String provided to the expected JSON string and returns the results of the comparison.
     * 
     * @param expectedStr Expected JSON String
     * @param actualStr JSON String to compare
     * @param ignoreList The list of field name (in String) wanted to ignore
     * @param mode Defines comparison behavior
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     */
    public static JSONCompareResult compareJSONWithIgnore(String expectedStr, String actualStr, ArrayList<String> ignoreList, JSONCompareMode mode) throws JSONException {
        if (ignoreList.size() == 0){
            JSONCompareResult result = new JSONCompareResult();
            result.fail("Please use other mode if don't want to ignore any field");
            return result;
        }
        Object _expect = JSONParser.parseJSON(expectedStr);
        Object _actual = JSONParser.parseJSON(actualStr);
        if ((_expect instanceof JSONObject) && (_actual instanceof JSONObject)){
            return compareJSONWithIgnore((JSONObject)_expect, (JSONObject)_actual, ignoreList, mode);
        }else if ((_expect instanceof JSONArray) && (_actual instanceof JSONArray)){
            return compareJSONWithIgnore((JSONArray)_expect, (JSONArray)_actual, ignoreList, mode);
        }else if ((_expect instanceof JSONString) && (_actual instanceof JSONString)){
            return compareJSONWithIgnore(((JSONString)_expect).toJSONString(), ((JSONString)_actual).toJSONString(), ignoreList, mode);
        }else if (_expect instanceof JSONObject) {
            return new JSONCompareResult().fail("", _expect, _actual);
        }
        else {
            return new JSONCompareResult().fail("", _expect, _actual);
        }
    }

    /**
     * Compares JSONObject provided to the expected JSONObject, and returns the results of the comparison.
     *
     * @param expected Expected JSONObject
     * @param actual   JSONObject to compare
     * @param mode     Defines comparison behavior
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     */
    public static JSONCompareResult compareJSON(JSONObject expected, JSONObject actual, JSONCompareMode mode)
            throws JSONException {
        return compareJSON(expected, actual, getComparatorForMode(mode));
    }


    /**
     * Compares JSONArray provided to the expected JSONArray, and returns the results of the comparison.
     *
     * @param expected Expected JSONArray
     * @param actual   JSONArray to compare
     * @param mode     Defines comparison behavior
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     */
    public static JSONCompareResult compareJSON(JSONArray expected, JSONArray actual, JSONCompareMode mode)
            throws JSONException {
        return compareJSON(expected, actual, getComparatorForMode(mode));
    }

}
