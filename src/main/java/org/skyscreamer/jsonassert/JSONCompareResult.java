package org.skyscreamer.jsonassert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bean for holding results from JSONCompare.
 */
public class JSONCompareResult {
    private boolean _success;
    private String _message;
    private String _field;
    private Object _expected;
    private Object _actual;
    private final List<FieldComparisonFailure> _fieldFailures = new ArrayList<FieldComparisonFailure>();

    /**
     * Default constructor.
     */
    public JSONCompareResult() {
        this(true, null);
    }

    private JSONCompareResult(boolean success, String message) {
        _success = success;
        _message = message == null ? "" : message;
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
        return _message;
    }

    /**
     * Get the list of failures on field comparisons
     */
    public List<FieldComparisonFailure> getFieldFailures() {
        return Collections.unmodifiableList(_fieldFailures);
    }

    /**
     * Actual field value
     * 
     * @return a {@code JSONObject}, {@code JSONArray} or other {@code Object}
     *         instance, or {@code null} if the comparison did not fail on a
     *         particular field
     * @deprecated Superseded by {@link #getFieldFailures()}
     */
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
    public Object getExpected() {
        return _expected;
    }
    
    /**
     * Check if comparison failed on any particular fields
     */
    public boolean isFailureOnField() {
        return !_fieldFailures.isEmpty();
    }

    /**
     * Dot-separated path the the field that failed comparison
     * 
     * @return a {@code String} instance, or {@code null} if the comparison did
     *         not fail on a particular field
     * @deprecated Superseded by {@link #getFieldFailures()}
     */
    public String getField() {
        return _field;
    }
    
    protected void fail(String message) {
        _success = false;
        if (_message.length() == 0) {
            _message = message;
        }
        else {
            _message += " ; " + message;
        }
    }

    /**
     * Identify that the comparison failed
     * @param field Which field failed
     * @param expected Expected result
     * @param actual Actual result
     */
    protected void fail(String field, Object expected, Object actual) {
        _fieldFailures.add(new FieldComparisonFailure(field, expected, actual));
        this._field = field;
        this._expected = expected;
        this._actual = actual;
        fail(formatFailureMessage(field, expected, actual));
    }

    private String formatFailureMessage(String field, Object expected, Object actual) {
        StringBuffer message= new StringBuffer();
        message.append(field);
        message.append("\nExpected: ");
        message.append(expected + "");
        message.append("\n     got: ");
        message.append(actual + "");
        message.append("\n");
        return message.toString();
    }
}
