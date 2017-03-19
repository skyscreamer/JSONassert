/*
 * Copyright 2012 Duncan Mackinder.
 *
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

/**
 * 
 */
package org.skyscreamer.jsonassert;

/**
 * A ValueMatcher extension that provides location in form of prefix to the equals method.
 * 
 * @author Duncan Mackinder
 *
 */
public interface LocationAwareValueMatcher<T> extends ValueMatcher<T> {

	/**
	 * Match actual value with expected value. If match fails any of the
	 * following may occur, return false, pass failure details to specified
	 * JSONCompareResult and return true, or throw ValueMatcherException
	 * containing failure details. Passing failure details to JSONCompareResult
	 * or returning via ValueMatcherException enables more useful failure
	 * description for cases where expected value depends entirely or in part on
	 * configuration of the ValueMatcher and therefore expected value passed to
	 * this method will not give a useful indication of expected value.
	 * 
	 * @param prefix
	 *            JSON path of the JSON item being tested
	 * @param actual
	 *            JSON value being tested
	 * @param expected
	 *            expected JSON value
	 * @param result
	 *            JSONCompareResult to which match failure may be passed
	 * @return true if expected and actual equal or any difference has already
	 *         been passed to specified result instance, false otherwise.
	 * @throws ValueMatcherException
	 *             if expected and actual values not equal and ValueMatcher
	 *             needs to override default comparison failure message that
	 *             would be generated if this method returned false.
	 */
	boolean equal(String prefix, T actual, T expected, JSONCompareResult result) throws ValueMatcherException;
}
