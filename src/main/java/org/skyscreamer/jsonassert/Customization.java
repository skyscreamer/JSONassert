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

import java.util.regex.Pattern;

/**
 * Associates a custom matcher to a specific jsonpath.
 */
public final class Customization {
	private final Pattern path;
	private final ValueMatcher<Object> comparator;

	public Customization(String path, ValueMatcher<Object> comparator) {
        assert path != null;
        assert comparator != null;
		this.path = Pattern.compile(buildPattern(path));
		this.comparator = comparator;
	}

	private String buildPattern(String path) {
		return buildPatternLevel1(path);
	}

	private String buildPatternLevel1(String path) {
		String regex = "\\*\\*\\.";
		String replacement = "(?:.+\\.)?";

		StringBuilder sb = new StringBuilder();
		String[] parts = path.split(regex);
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];

			sb.append(buildPatternLevel2(part));
			if (i < parts.length - 1) {
				sb.append(replacement);
			}
		}

		return sb.toString();
	}

	private String buildPatternLevel2(String s) {
		if (s.isEmpty()) {
			return "";
		}
		String regex = "\\*\\*";
		String replacement = ".+";

		StringBuilder sb = new StringBuilder();
		String[] parts = s.split(regex);
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];

			sb.append(buildPatternLevel3(part));
			if (i < parts.length - 1) {
				sb.append(replacement);
            }
        }
		return sb.toString();
	}

	private String buildPatternLevel3(String s) {
		if (s.isEmpty()) {
			return "";
		}

		String regex = "\\*";
		String replacement = "[^\\.]+";

		StringBuilder sb = new StringBuilder();
		String[] parts = s.split(regex);
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];

			sb.append(Pattern.quote(part));
			if (i < parts.length - 1) {
				sb.append(replacement);
            }
        }
		return sb.toString();
	}

	public static Customization customization(String path, ValueMatcher<Object> comparator) {
		return new Customization(path, comparator);
	}

    public boolean appliesToPath(String path) {
        return this.path.matcher(path).matches();
    }

	/**
	 * Return true if actual value matches expected value using this
	 * Customization's comparator. Calls to this method should be replaced by
	 * calls to matches(String prefix, Object actual, Object expected,
	 * JSONCompareResult result).
	 *
	 * @param actual
	 *            JSON value being tested
	 * @param expected
	 *            expected JSON value
	 * @return true if actual value matches expected value
	 */
    @Deprecated
    public boolean matches(Object actual, Object expected) {
        return comparator.equal(actual, expected);
    }

	/**
	 * Return true if actual value matches expected value using this
	 * Customization's comparator. The equal method used for comparison depends
	 * on type of comparator.
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
		if (comparator instanceof LocationAwareValueMatcher) {
			return ((LocationAwareValueMatcher<Object>)comparator).equal(prefix, actual, expected, result);
		}
		return comparator.equal(actual, expected);
	}
}
