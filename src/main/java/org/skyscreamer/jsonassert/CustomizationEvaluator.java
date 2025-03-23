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

final class CustomizationEvaluator {
    private CustomizationEvaluator() {}

    /**
     * Return true if actual value matches expected value using this
     * Customization's comparator. The equal method used for comparison depends
     * on type of comparator.
     *
     * @param comparator Comparator to use while comparing JSON items
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
    public static boolean matches(ValueMatcher<Object> comparator, String prefix, Object actual, Object expected,
                           JSONCompareResult result) throws ValueMatcherException {
        if (comparator instanceof LocationAwareValueMatcher) {
            return ((LocationAwareValueMatcher<Object>)comparator).equal(prefix, actual, expected, result);
        }
        return comparator.equal(actual, expected);
    }
}
