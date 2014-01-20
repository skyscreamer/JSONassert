package org.skyscreamer.jsonassert;

/**
 * Exception that may be thrown by ValueMatcher subclasses to provide more detail on why matches method failed.
 * 
 * @author Duncan Mackinder
 *
 */
public class ValueMatcherException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String expected;
	
	private final String actual;
	
	/**
	 * Create new ValueMatcherException
	 * 
	 * @param message
	 *            description of exception
	 * @param expected
	 *            value expected by ValueMatcher
	 * @param actual
	 *            value being tested by ValueMatcher
	 */
	public ValueMatcherException(String message, String expected, String actual) {
		super(message);
		this.expected = expected;
		this.actual = actual;
	}

	/**
	 * Create new ValueMatcherException
	 * 
	 * @param message
	 *            description of exception
	 * @param cause
	 *            cause of ValueMatcherException
	 * @param expected
	 *            value expected by ValueMatcher
	 * @param actual
	 *            value being tested by ValueMatcher
	 */
	public ValueMatcherException(String message, Throwable cause, String expected, String actual) {
		super(message, cause);
		this.expected = expected;
		this.actual = actual;
	}

	/**
	 * @return the expected value
	 */
	public String getExpected() {
		return expected;
	}

	/**
	 * @return the actual value
	 */
	public String getActual() {
		return actual;
	}

}
