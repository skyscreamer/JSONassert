package org.skyscreamer.jsonassert;

/**
 * Bean for holding results from JSONCompare.
 */
public class JSONCompareResult {
    private boolean _success;
    private String _message;

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
    
    protected void fail(String message) {
        _success = false;
        if (_message.length() == 0) {
            _message = message;
        }
        else {
            _message += " ; " + message;
        }
    }

    protected void fail(String field, Object expected, Object actual) {
        StringBuffer message= new StringBuffer();
        message.append(field);
        message.append("\nExpected: ");
        message.append(expected + "");
        message.append("\n     got: ");
        message.append(actual + "");
        message.append("\n");
        fail(message.toString());
    }
}
