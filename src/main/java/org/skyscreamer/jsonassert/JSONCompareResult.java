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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Bean for holding results from JSONCompare.
 */
public class JSONCompareResult {
    private boolean _success;
    private StringBuilder _message;
    private String _field;
    private Object _expected;
    private Object _actual;
    private final List<FieldComparisonFailure> _fieldFailures = new ArrayList<FieldComparisonFailure>();
    private final List<FieldComparisonFailure> _fieldMissing = new ArrayList<FieldComparisonFailure>();
    private final List<FieldComparisonFailure> _fieldUnexpected = new ArrayList<FieldComparisonFailure>();

    /**
     * Default constructor.
     */
    public JSONCompareResult() {
        this(true, null);
    }

    private JSONCompareResult(boolean success, String message) {
        _success = success;
        _message = new StringBuilder(message == null ? "" : message);
    }

    /**
     * Did the comparison pass?
     * @return True if it passed
     */
    public boolean passed() {
        return _success;
    }

    /**
     * Did the comparison fail?
     * @return True if it failed
     */
    public boolean failed() {
        return !_success;
    }

    /**
     * Result message
     * @return String explaining why if the comparison failed
     */
    public String getMessage() {
        return _message.toString();
    }

    /**
     * Get the list of failures on field comparisons
     * @return list of comparsion failures
     */
    public List<FieldComparisonFailure> getFieldFailures() {
        return Collections.unmodifiableList(_fieldFailures);
    }
    
    /**
     * Get the list of missed on field comparisons
     * @return list of comparsion failures
     */
    public List<FieldComparisonFailure> getFieldMissing() {
        return Collections.unmodifiableList(_fieldMissing);
    }
    
    /**
     * Get the list of failures on field comparisons
     * @return list of comparsion failures
     */
    public List<FieldComparisonFailure> getFieldUnexpected() {
        return Collections.unmodifiableList(_fieldUnexpected);
    }

    /**
     * Actual field value
     * 
     * @return a {@code JSONObject}, {@code JSONArray} or other {@code Object}
     *         instance, or {@code null} if the comparison did not fail on a
     *         particular field
     * @deprecated Superseded by {@link #getFieldFailures()}
     */
    @Deprecated
    public Object getActual() {
        return _actual;
    }
    
    /**
     * Expected field value
     * 
     * @return a {@code JSONObject}, {@code JSONArray} or other {@code Object}
     *         instance, or {@code null} if the comparison did not fail on a
     *         particular field
     * @deprecated Superseded by {@link #getFieldFailures()}
     */
    @Deprecated
    public Object getExpected() {
        return _expected;
    }
    
    /**
     * Check if comparison failed on any particular fields
     * @return true if there are field failures
     */
    public boolean isFailureOnField() {
        return !_fieldFailures.isEmpty();
    }
    
    /**
     * Check if comparison failed with missing on any particular fields
     * @return true if an expected field is missing
     */
    public boolean isMissingOnField() {
        return !_fieldMissing.isEmpty();
    }
    
    /**
     * Check if comparison failed with unexpected on any particular fields
     * @return true if an unexpected field is in the result
     */
    public boolean isUnexpectedOnField() {
        return !_fieldUnexpected.isEmpty();
    }

    /**
     * Dot-separated path the the field that failed comparison
     * 
     * @return a {@code String} instance, or {@code null} if the comparison did
     *         not fail on a particular field
     * @deprecated Superseded by {@link #getFieldFailures()}
     */
    @Deprecated
    public String getField() {
        return _field;
    }
    
    public void fail(String message) {
        _success = false;
        if (_message.length() == 0) {
            _message.append(message);
        } else {
            _message.append(" ; ").append(message);
        }
    }

    /**
     * Identify that the comparison failed
     * @param field Which field failed
     * @param expected Expected result
     * @param actual Actual result
     * @return result of comparision
     */
    public JSONCompareResult fail(String field, Object expected, Object actual) {
        _fieldFailures.add(new FieldComparisonFailure(field, expected, actual));
        this._field = field;
        this._expected = expected;
        this._actual = actual;
        fail(formatFailureMessage(field, expected, actual));
        return this;
    }

    /**
     * Identify that the comparison failed
     * @param field Which field failed
     * @param exception exception containing details of match failure
     * @return result of comparision
     */
    public JSONCompareResult fail(String field, ValueMatcherException exception) {
    	fail(field + ": " + exception.getMessage(), exception.getExpected(), exception.getActual());
        return this;
    }

     /**CS304 Issue link: https://github.com/skyscreamer/JSONassert/issues/127
     * modified by adding a case when {@code expected} or {@code actual} == null,
     * which the JSON may be an invalid JSON,
     * return a warning sentence to inform the tester.
     */
    private String formatFailureMessage(String field, Object expected, Object actual) {
        String describeExp = describe(expected);
        String describeAct = describe(actual);
        String message = field
                + "\nExpected: "
                + describeExp
                + "\n     got: "
                + describeAct
                + "\n";
        if(describeExp.equals("null")){
            message += "the expected JSON may be an invalid JSON"
                    + "\n";
        }
        if(describeAct.equals("null")){
            message += "the actual JSON may be an invalid JSON"
                    + "\n";
        }
        return message;
    }

    /**
     * Identify the missing field
     * @param field missing field
     * @param expected expected result
     * @return result of comparison
     */
    public JSONCompareResult missing(String field, Object expected) {
    	_fieldMissing.add(new FieldComparisonFailure(field, expected, null));
        fail(formatMissing(field, expected));
        return this;
    }

    private String formatMissing(String field, Object expected) {
        return field
                + "\nExpected: "
                + describe(expected)
                + "\n     but none found\n";
    }

    /**
     * Identify unexpected field
     * @param field unexpected field
     * @param actual actual result
     * @return result of comparison
     */
    public JSONCompareResult unexpected(String field, Object actual) {
    	_fieldUnexpected.add(new FieldComparisonFailure(field, null, actual));
        fail(formatUnexpected(field, actual));
        return this;
    }

    private String formatUnexpected(String field, Object actual) {
        return field
                + "\nUnexpected: "
                + describe(actual)
                + "\n";
    }

    /**CS304 Issue link: https://github.com/skyscreamer/JSONassert/issues/130
     * modified by adding a case when {@code value} == null,
     * return a String "null" to avoid {@exception NullPointer}
     */
    private static String describe(Object value) {
        if (value instanceof JSONArray) {
            return "a JSON array";
        } else if (value instanceof JSONObject) {
            return "a JSON object";
        }
        else if(value == null){
            return "null";
        }
        else {
            return value.toString();
        }
    }

    @Override
    public String toString() {
        return _message.toString();
    }
}
