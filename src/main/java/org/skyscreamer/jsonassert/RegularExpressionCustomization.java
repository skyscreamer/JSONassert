package org.skyscreamer.jsonassert;

import java.util.regex.Pattern;

/**
 * Associates a custom matcher to a jsonpath identified by a regular expression
 * 
 * @author Duncan Mackinder
 */
public final class RegularExpressionCustomization implements Customizable {
	private final Pattern path;
	private final ValueMatcher<Object> comparator;

	public RegularExpressionCustomization(String path, ValueMatcher<Object> comparator) {
        assert path != null;
        assert comparator != null;
		this.path = Pattern.compile(path);
		this.comparator = comparator;
	}

	public static Customizable customization(String path, ValueMatcher<Object> comparator) {
		return new RegularExpressionCustomization(path, comparator);
	}

    /* (non-Javadoc)
	 * @see org.skyscreamer.jsonassert.Customizable#appliesToPath(java.lang.String)
	 */
    @Override
	public boolean appliesToPath(String path) {
        return this.path.matcher(path).matches();
    }

    /* (non-Javadoc)
	 * @see org.skyscreamer.jsonassert.Customizable#matches(java.lang.Object, java.lang.Object)
	 */
    @Override
	public boolean matches(Object actual, Object expected) {
        return comparator.equal(actual, expected);
    }
}
