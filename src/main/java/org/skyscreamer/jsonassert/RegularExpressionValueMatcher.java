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

package org.skyscreamer.jsonassert;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.skyscreamer.jsonassert.ValueMatcher;

/**
 * A JSONassert value matcher that matches actual value to regular expression.
 * If non-null regular expression passed to constructor, then all actual values
 * will be compared against this constant pattern, ignoring any expected value
 * passed to equal method. If null regular expression passed to constructor,
 * then expected value passed to equals method will be used to dynamically
 * specify regular expression pattern that actual value must match.
 * 
 * @author Duncan Mackinder
 * 
 */
public class RegularExpressionValueMatcher<T> implements ValueMatcher<T> {

	private final Pattern expectedPattern;

	/**
	 * Create RegularExpressionValueMatcher in which the pattern the actual
	 * value must match with be specified dynamically from the expected string
	 * passed to this matcher in the equals method.
	 */
	public RegularExpressionValueMatcher() {
		this(null);
	}

	/**
	 * Create RegularExpressionValueMatcher with specified pattern. If pattern
	 * is not null, it must be a valid regular expression that defines a
	 * constant expected pattern that every actual value must match (in this
	 * case the expected value passed to equal method will be ignored). If
	 * pattern is null, the pattern the actual value must match with be
	 * specified dynamically from the expected string passed to this matcher in
	 * the equals method.
	 * 
	 * @param pattern
	 *            if non null, regular expression pattern which all actual
	 *            values this matcher is applied to must match. If null, this
	 *            matcher will apply pattern specified dynamically via the
	 *            expected parameter to the equal method.
	 * @throws IllegalArgumentException
	 *             if pattern is non-null and not a valid regular expression.
	 */
	public RegularExpressionValueMatcher(String pattern) throws IllegalArgumentException {
		try {
			expectedPattern = pattern == null ? null : Pattern.compile(pattern);
		}
		catch (PatternSyntaxException e) {
			throw new IllegalArgumentException("Constant expected pattern invalid: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean equal(T actual, T expected) {
		String actualString = actual.toString();
		String expectedString = expected.toString();
		try {
			Pattern pattern = isStaticPattern() ? expectedPattern : Pattern
					.compile(expectedString);
			if (!pattern.matcher(actualString).matches()) {
				throw new ValueMatcherException(getPatternType() + " expected pattern did not match value", pattern.toString(), actualString);
			}
		}
		catch (PatternSyntaxException e) {
			throw new ValueMatcherException(getPatternType() + " expected pattern invalid: " + e.getMessage(), e, expectedString, actualString);
		}
		return true;
	}

	private boolean isStaticPattern() {
		return expectedPattern != null;
	}

	private String getPatternType() {
		return isStaticPattern()? "Constant": "Dynamic";
	}
}
