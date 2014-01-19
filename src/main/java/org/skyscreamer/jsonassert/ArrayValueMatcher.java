package org.skyscreamer.jsonassert;

import java.text.MessageFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

/**
 * A value matcher for arrays. This operates like STRICT_ORDER array match,
 * however if expected array has less elements than actual array the matching
 * process loops through the expected array to get expected elements for the
 * additional actual elements. In general the expected array will contain a
 * single element which is matched against each actual array element in turn.
 * This allows simple verification of constant array element components and
 * coupled with RegularExpressionValueMatcher can be used to match specific
 * array element components against a regular expression pattern. If the
 * expected object is not an array, a one element expected array is created
 * containing whatever is provided as the expected value.
 * 
 * @author Duncan Mackinder
 * 
 */
public class ArrayValueMatcher<T> implements LocationAwareValueMatcher<T> {
	private final JSONComparator comparator;
	private final int from;
	private final int to;

	/**
	 * Create ArrayValueMatcher to match every element in actual array against
	 * elements taken in sequence from expected array, repeating from start of
	 * expected array if necessary.
	 * 
	 * @param comparator
	 *            comparator to use to compare elements
	 */
	public ArrayValueMatcher(JSONComparator comparator) {
		this(comparator, 0, Integer.MAX_VALUE);
	}

	/**
	 * Create ArrayValueMatcher to match specified element in actual array
	 * against first element of expected array.
	 * 
	 * @param comparator
	 *            comparator to use to compare elements
	 */
	public ArrayValueMatcher(JSONComparator comparator, int i) {
		this(comparator, i, i);
	}

	/**
	 * Create ArrayValueMatcher to match every element in specified range
	 * (inclusive) from actual array against elements taken in sequence from
	 * expected array, repeating from start of expected array if necessary.
	 * 
	 * @param comparator
	 *            comparator to use to compare elements
	 * @from first element in actual array to compare
	 * @to last element in actual array to compare
	 */
	public ArrayValueMatcher(JSONComparator comparator, int from, int to) {
		assert comparator != null : "comparator null";
		assert from >= 0 : MessageFormat.format("from({0}) < 0", from);
		assert to >= from : MessageFormat.format("to({0}) < from({1})", to,
				from);
		this.comparator = comparator;
		this.from = from;
		this.to = to;
	}

	@Override
	/*
	 * NOTE: method defined as required by ValueMatcher interface but will never
	 * be called so defined simply to indicate match failure
	 */
	public boolean equal(T o1, T o2) {
		return false;
	}

	@Override
	public boolean equal(String prefix, T actual, T expected, JSONCompareResult result) {
		if (!(actual instanceof JSONArray)) {
			throw new IllegalArgumentException("ArrayValueMatcher applied to non-array actual value");
		}
		try {
			JSONArray actualArray = (JSONArray) actual;
			JSONArray expectedArray = expected instanceof JSONArray ? (JSONArray) expected: new JSONArray(new Object[] { expected });
			int first = Math.max(0, from);
			int last = Math.min(actualArray.length() - 1, to);
			int expectedLen = expectedArray.length();
			for (int i = first; i <= last; i++) {
				String elementPrefix = MessageFormat.format("{0}[{1}]", prefix, i);
				Object actualElement = actualArray.get(i);
				Object expectedElement = expectedArray.get((i - first) % expectedLen);
				comparator.compareValues(elementPrefix, expectedElement, actualElement, result);
			}
			// values must match since no exceptions thrown
			return true;
		}
		catch (JSONException e) {
			return false;
		}
	}

}
