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
	
	public ValueMatcherException(String message, String expected, String actual) {
		super(message);
		this.expected = expected;
		this.actual = actual;
	}
	
	public ValueMatcherException(Throwable cause, String expected, String actual) {
		super(cause);
		this.expected = expected;
		this.actual = actual;
	}

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
