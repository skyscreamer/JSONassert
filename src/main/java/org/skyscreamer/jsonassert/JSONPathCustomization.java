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

import com.jayway.jsonpath.JsonPath;

/**
 * Specifies a <a href="https://goessner.net/articles/JsonPath/">JSONPath</a>-based customisation.
 * <br><br>
 * For supported queries, refer to:
 * <ul>
 *     <li><a href="https://github.com/json-path/JsonPath">Documentation</a> of the <b>Jayway JsonPath project</b></li>
 *     <li><a href="https://goessner.net/articles/JsonPath/">Documentation</a> of the <b>original Stefan Goessner JsonPath implementation</b></li>
 * </ul>
 *
 * @author Shane B. (<a href="mailto:shane@wander.dev">shane@wander.dev</a>)
 */
public class JSONPathCustomization {
    private static final ValueMatcher<Object> IGNORE_FIELD = (expected, actual) -> true;

    private final JsonPath jsonPath;
    private final ValueMatcher<Object> comparator;

    /**
     * Defines a JSONAssert customisation.
     * <br>
     * For all fields that match the specified path,
     * the defined custom comparator {@link ValueMatcher} will be evaluated in place of comparing exactly
     * to the <code>expected</code> JSON.
     * @param jsonPath      <a href="https://goessner.net/articles/JsonPath/">JSONPath</a> expression specifying which items the <code>comparator</code> should be evaluated for.
     * @param comparator    Comparator that should be applied for all items that match the <code>jsonPath</code> expression
     */
    public JSONPathCustomization(String jsonPath, ValueMatcher<Object> comparator) {
        assert jsonPath != null;
        assert comparator != null;
        this.jsonPath = JsonPath.compile(jsonPath);
        this.comparator = comparator;
    }

    /**
     * Utility that constructs a generic customisation that will ignore all fields matching the JSONPath query.
     * @param jsonPath  <a href="https://goessner.net/articles/JsonPath/">JSONPath</a>-query specifying which
     *                  elements should get ignored
     */
    public static JSONPathCustomization ofIgnore(String jsonPath) {
        return new JSONPathCustomization(jsonPath, IGNORE_FIELD);
    }

    public JsonPath getJsonPath() {
        return jsonPath;
    }

    /**
     * Return true if actual value matches expected value using this
     * Customization's comparator. The equal method used for comparison depends
     * on type of comparator.
     * See: {@link Customization}; this is an exact copy.
     *
     * @param prefix
     *            JSON path of the JSON item being tested (only used if
     *            comparator is a LocationAwareValueMatcher)
     * @param actual
     *            JSON value being tested
     * @param expected
     *            expected JSON value
     * @param result
     *            JSONCompareResult to which match failure may be passed (only
     *            used if comparator is a LocationAwareValueMatcher)
     * @return true if expected and actual equal or any difference has already
     *         been passed to specified result instance, false otherwise.
     * @throws ValueMatcherException
     *             if expected and actual values not equal and ValueMatcher
     *             needs to override default comparison failure message that
     *             would be generated if this method returned false.
     */
    public boolean matches(String prefix, Object actual, Object expected,
                           JSONCompareResult result) throws ValueMatcherException {
        return CustomizationEvaluator.matches(comparator, prefix, actual, expected, result);
    }
}
