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

import java.text.MessageFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

/**
 * <p>A value matcher for arrays. This operates like STRICT_ORDER array match,
 * however if expected array has less elements than actual array the matching
 * process loops through the expected array to get expected elements for the
 * additional actual elements. In general the expected array will contain a
 * single element which is matched against each actual array element in turn.
 * This allows simple verification of constant array element components and
 * coupled with RegularExpressionValueMatcher can be used to match specific
 * array element components against a regular expression pattern. As a convenience to reduce syntactic complexity of expected string, if the
 * expected object is not an array, a one element expected array is created
 * containing whatever is provided as the expected value.</p>
 * 
 * <p>Some examples of typical usage idioms listed below.</p>
 * 
 * <p>Assuming JSON to be verified is held in String variable ARRAY_OF_JSONOBJECTS and contains:</p>
 * 
 * <pre>{@code
 * {a:[{background:white, id:1, type:row},
 *     {background:grey,  id:2, type:row},
 *     {background:white, id:3, type:row},
 *     {background:grey,  id:4, type:row}]}
 * }</pre>
 * 
 * <p>then:</p>
 * 
 * <p>To verify that the 'id' attribute of first element of array 'a' is '1':</p>
 * 
 * <pre>{@code
 * JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
 * Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator, 0));
 * JSONAssert.assertEquals("{a:[{id:1}]}", ARRAY_OF_JSONOBJECTS,
 *     new CustomComparator(JSONCompareMode.LENIENT, customization));
 * }</pre>
 *
 * <p>To simplify complexity of expected JSON string, the value <code>"a:[{id:1}]}"</code> may be replaced by <code>"a:{id:1}}"</code></p>
 * 
 * <p>To verify that the 'type' attribute of second and third elements of array 'a' is 'row':</p>
 * 
 * <pre>{@code
 * JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
 * Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator, 1, 2));
 * JSONAssert.assertEquals("{a:[{type:row}]}", ARRAY_OF_JSONOBJECTS,
 *     new CustomComparator(JSONCompareMode.LENIENT, customization));
 * }</pre>
 * 
 * <p>To verify that the 'type' attribute of every element of array 'a' is 'row':</p>
 * 
 * <pre>{@code
 * JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
 * Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator));
 * JSONAssert.assertEquals("{a:[{type:row}]}", ARRAY_OF_JSONOBJECTS,
 *     new CustomComparator(JSONCompareMode.LENIENT, customization));
 * }</pre>
 * 
 * <p>To verify that the 'id' attribute of every element of array 'a' matches regular expression '\d+'.  This requires a custom comparator to specify regular expression to be used to validate each array element, hence the array of Customization instances:</p>
 * 
 * <pre>{@code
 * // get length of array we will verify
 * int aLength = ((JSONArray)((JSONObject)JSONParser.parseJSON(ARRAY_OF_JSONOBJECTS)).get("a")).length();
 * // create array of customizations one for each array element
 * RegularExpressionValueMatcher<Object> regExValueMatcher =
 *     new RegularExpressionValueMatcher<Object>("\\d+");  // matches one or more digits
 * Customization[] customizations = new Customization[aLength];
 * for (int i=0; i<aLength; i++) {
 *     String contextPath = "a["+i+"].id";
 *     customizations[i] = new Customization(contextPath, regExValueMatcher);
 * }
 * CustomComparator regExComparator = new CustomComparator(JSONCompareMode.STRICT_ORDER, customizations);
 * ArrayValueMatcher<Object> regExArrayValueMatcher = new ArrayValueMatcher<Object>(regExComparator);
 * Customization regExArrayValueCustomization = new Customization("a", regExArrayValueMatcher);
 * CustomComparator regExCustomArrayValueComparator =
 *     new CustomComparator(JSONCompareMode.STRICT_ORDER, new Customization[] { regExArrayValueCustomization });
 * JSONAssert.assertEquals("{a:[{id:X}]}", ARRAY_OF_JSONOBJECTS, regExCustomArrayValueComparator);
 * }</pre>
 * 
 * <p>To verify that the 'background' attribute of every element of array 'a' alternates between 'white' and 'grey' starting with first element 'background' being 'white':</p>
 * 
 * <pre>{@code
 * JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
 * Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator));
 * JSONAssert.assertEquals("{a:[{background:white},{background:grey}]}", ARRAY_OF_JSONOBJECTS,
 *     new CustomComparator(JSONCompareMode.LENIENT, customization));
 * }</pre>
 * 
 * <p>Assuming JSON to be verified is held in String variable ARRAY_OF_JSONARRAYS and contains:</p>
 * 
 * <code>{a:[[6,7,8], [9,10,11], [12,13,14], [19,20,21,22]]}</code>
 * 
 * <p>then:</p>
 * 
 * <p>To verify that the first three elements of JSON array 'a' are JSON arrays of length 3:</p>
 * 
 * <pre>{@code
 * JSONComparator comparator = new ArraySizeComparator(JSONCompareMode.STRICT_ORDER);
 * Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator, 0, 2));
 * JSONAssert.assertEquals("{a:[[3]]}", ARRAY_OF_JSONARRAYS, new CustomComparator(JSONCompareMode.LENIENT, customization));
 * }</pre>
 *
 * <p>NOTE: simplified expected JSON strings are not possible in this case as ArraySizeComparator does not support them.</p>
 * 
 * <p>To verify that the second elements of JSON array 'a' is a JSON array whose first element has the value 9:</p>
 * 
 * <pre>{@code
 * JSONComparator innerComparator = new DefaultComparator(JSONCompareMode.LENIENT);
 * Customization innerCustomization = new Customization("a[1]", new ArrayValueMatcher<Object>(innerComparator, 0));
 * JSONComparator comparator = new CustomComparator(JSONCompareMode.LENIENT, innerCustomization);
 * Customization customization = new Customization("a", new ArrayValueMatcher<Object>(comparator, 1));
 * JSONAssert.assertEquals("{a:[[9]]}", ARRAY_OF_JSONARRAYS, new CustomComparator(JSONCompareMode.LENIENT, customization));
 * }</pre>
 *
 * <p>To simplify complexity of expected JSON string, the value <code>"{a:[[9]]}"</code> may be replaced by <code>"{a:[9]}"</code> or <code>"{a:9}"</code></p>
 * 
 * @author Duncan Mackinder
 * @param <T> Array Type
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
	 * @param index
	 *            index of the array element to be compared
	 */
	public ArrayValueMatcher(JSONComparator comparator, int index) {
		this(comparator, index, index);
	}

	/**
	 * Create ArrayValueMatcher to match every element in specified range
	 * (inclusive) from actual array against elements taken in sequence from
	 * expected array, repeating from start of expected array if necessary.
	 * 
	 * @param comparator
	 *            comparator to use to compare elements
	 * @param from first element in actual array to compared
	 * @param to last element in actual array to compared
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
			// any failures have already been passed to result, so return true
			return true;
		}
		catch (JSONException e) {
			return false;
		}
	}

}
