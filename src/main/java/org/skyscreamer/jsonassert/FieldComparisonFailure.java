package org.skyscreamer.jsonassert;

/**
 * Models a failure when comparing two fields.
 */
public class FieldComparisonFailure {
    private final String _field;
    private final Object _expected;
    private final Object _actual;

    public FieldComparisonFailure(String field, Object expected, Object actual) {
        this._field = field;
        this._expected = expected;
        this._actual = actual;
    }

    public String getField() {
        return _field;
    }

    public Object getExpected() {
        return _expected;
    }

    public Object getActual() {
        return _actual;
    }
}
