/*
 * Copyright 2012 Duncan Mackinder.
 *
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
