/**
 * 
 */
package org.skyscreamer.jsonassert;

/**
 * A ValueMatcher extension that provides location in form of prefix to the equals method.
 * 
 * @author Duncan Mackinder
 *
 */
public interface LocationAwareValueMatcher<T> extends ValueMatcher<T> {

	boolean equal(String prefix, T actual, T expected, JSONCompareResult result);
}
