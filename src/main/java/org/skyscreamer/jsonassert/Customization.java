package org.skyscreamer.jsonassert;

/**
 * Associates a custom matcher to a specific jsonpath.
 */
public final class Customization {
	private final String path;
	private final ValueMatcher<Object> comparator;

	public Customization(String path, ValueMatcher<Object> comparator) {
        assert path != null;
        assert comparator != null;
		this.path = path;
		this.comparator = comparator;
	}

	public static Customization customization(String path, ValueMatcher<Object> comparator) {
		return new Customization(path, comparator);
	}

    public boolean appliesToPath(String path) {
        return this.path.equals(path);
    }

	/**
	 * Return true if actual value matches expected value using this
	 * Customization's comparator. Calls to this method should be replaced by
	 * calls to matches(String prefix, Object actual, Object expected,
	 * JSONCompareResult result).
	 * 
	 * @param actual
	 *            JSON value being tested
	 * @param expected
	 *            expected JSON value
	 * @return true if actual value matches expected value
	 */
    @Deprecated
    public boolean matches(Object actual, Object expected) {
        return comparator.equal(actual, expected);
    }
    
	/**
	 * Return true if actual value matches expected value using this
	 * Customization's comparator. The equal method used for comparison depends
	 * on type of comparator.
	 * 
	 * @param prefix
	 *            JSON path of the JSON item being tested (only used if
	 *            comparator is a LocationAwareValueMatcher)
	 * @param actual
	 *            JSON value being tested
	 * @param expected
	 *            expected JSON value
	 * @param result
	 *            JSONCompareResult to which match failure may be passed (only
	 *            used if comparator is a LocationAwareValueMatcher)
	 * @return true if expected and actual equal or any difference has already
	 *         been passed to specified result instance, false otherwise.
	 * @throws ValueMatcherException
	 *             if expected and actual values not equal and ValueMatcher
	 *             needs to override default comparison failure message that
	 *             would be generated if this method returned false.
	 */
	public boolean matches(String prefix, Object actual, Object expected,
			JSONCompareResult result) throws ValueMatcherException {
		if (comparator instanceof LocationAwareValueMatcher) {
			return ((LocationAwareValueMatcher<Object>)comparator).equal(prefix, actual, expected, result);
		}
		return comparator.equal(actual, expected);
	}
}
