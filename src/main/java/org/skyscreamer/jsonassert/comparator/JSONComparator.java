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

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompareResult;

/**
 * Interface for comparison handler.
 *
 * @author <a href="mailto:aiveeen@gmail.com">Ivan Zaytsev</a>
 *         2013-01-04
 */
public interface JSONComparator {

    /**
     * Compares two {@link JSONObject}s and returns the result of the comparison in a {@link JSONCompareResult} object.
     *
     * @param expected the expected JSON object
     * @param actual   the actual JSON object
     * @return the result of the comparison
     * @throws JSONException JSON parsing error
     */
    JSONCompareResult compareJSON(JSONObject expected, JSONObject actual, List<String> keysToIgnore) throws JSONException;

    /**
     * Compares two {@link JSONObject}s on the provided path represented by {@code prefix} and
     * updates the result of the comparison in the {@code result} {@link JSONCompareResult} object.
     *
     * @param prefix   the path in the json where the comparison happens
     * @param expected the expected JSON object
     * @param actual   the actual JSON object
     * @param result   stores the actual state of the comparison result
     * @throws JSONException JSON parsing error
     */
    void compareJSON(String prefix, JSONObject expected, JSONObject actual, List<String> keysToIgnore, JSONCompareResult result) throws JSONException;

    /**
     * Compares two {@link Object}s on the provided path represented by {@code prefix} and
     * updates the result of the comparison in the {@code result} {@link JSONCompareResult} object.
     *
     * @param prefix        the path in the json where the comparison happens
     * @param expectedValue the expected value
     * @param actualValue   the actual value
     * @param result        stores the actual state of the comparison result
     * @throws JSONException JSON parsing error
     */
    void compareValues(String prefix, Object expectedValue, Object actualValue, List<String> keysToIgnore, JSONCompareResult result) throws JSONException;

    /**
     * Compares two {@link JSONArray}s and returns the result of the comparison in a {@link JSONCompareResult} object.
     *
     * @param expected the expected JSON array
     * @param actual   the actual JSON array
     * @return the result of the comparison
     * @throws JSONException JSON parsing error
     */
    JSONCompareResult compareJSONArray(JSONArray expected, JSONArray actual, List<String> keysToIgnore) throws JSONException;

    /**
     * Compares two {@link JSONArray}s on the provided path represented by {@code prefix} and
     * updates the result of the comparison in the {@code result} {@link JSONCompareResult} object.
     *
     * @param prefix   the path in the json where the comparison happens
     * @param expected the expected JSON array
     * @param actual   the actual JSON array
     * @param result   stores the actual state of the comparison result
     * @throws JSONException JSON parsing error
     */
    void compareJSONArray(String prefix, JSONArray expected, JSONArray actual, List<String> keysToIgnore, JSONCompareResult result) throws JSONException;
}
