package org.skyscreamer.jsonassert.comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

/**
 * A JSONAssert array size comparator. Expected array should consist of either 1
 * or 2 integer values that define maximum and minimum size of that the actual
 * array is expected to be. If expected array contains a single integer value
 * then the actual array must contain exactly that number of elements.
 * 
 * @author Duncan Mackinder
 * 
 */
public class ArraySizeComparator extends DefaultComparator {

	public ArraySizeComparator(JSONCompareMode mode) {
		super(mode);
	}

	@Override
	public void compareJSONArray(String prefix, JSONArray expected,
			JSONArray actual, JSONCompareResult result) throws JSONException {
		if (expected.length() < 1 || expected.length() > 2) {
			result.fail(prefix + ": invalid expectation, length=" + expected.length());
			return;
		}
		if (!(expected.get(0) instanceof Number)) {
			result.fail(prefix + ": min expected length not a number: " + expected.get(0));
			return;
		}
		if ((expected.length() == 2 && !(expected.get(1) instanceof Number))) {
			result.fail(prefix + ": max expected length not a number: " + expected.get(1));
			return;
		}
		int minExpectedLength = expected.getInt(0);
		if (minExpectedLength < 0) {
			result.fail(prefix + ": invalid expectation, invalid min expected length: " + minExpectedLength);
			return;
		}
		int maxExpectedLength = expected.length() == 2? expected.getInt(1): minExpectedLength;
		if (maxExpectedLength < minExpectedLength) {
			result.fail(prefix + ": invalid expectation, invalid expected length range: "+ minExpectedLength+" to " + maxExpectedLength);
			return;
		}
		if (actual.length() < minExpectedLength || actual.length() > maxExpectedLength) {
			result.fail(prefix + "[]: Expected " + minExpectedLength + (expected.length() == 2? (" to "+maxExpectedLength): "")
					+ " values but got " + actual.length());
		}
	}

}
