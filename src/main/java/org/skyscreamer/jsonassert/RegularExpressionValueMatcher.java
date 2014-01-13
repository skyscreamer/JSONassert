package org.skyscreamer.jsonassert;

import java.util.regex.Pattern;

import org.skyscreamer.jsonassert.ValueMatcher;

/**
 * A JSONassert value matcher that matches actual value to regular expression.
 * If non-null regular expression passed to constructor, then all actual values
 * will be compared against this pattern, ignoring any expected value passed to
 * equal method. If null regular expression passed to constructor, then expected
 * value passed to equals method will be treated as a regular expression.
 * 
 * @author Duncan Mackinder
 * 
 */
public class RegularExpressionValueMatcher<T> implements ValueMatcher<T> {

	private final Pattern expectedPattern;

	public RegularExpressionValueMatcher() {
		this(null);
	}

	public RegularExpressionValueMatcher(String expected) {
		expectedPattern = expected == null ? null : Pattern.compile(expected);
	}

	@Override
	public boolean equal(T actual, T expected) {
		String actualString = actual.toString();
		Pattern pattern = expectedPattern != null ? expectedPattern : Pattern
				.compile(expected.toString());
		return pattern.matcher(actualString).matches();
	}

}
