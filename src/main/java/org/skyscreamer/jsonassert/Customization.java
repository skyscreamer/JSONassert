package org.skyscreamer.jsonassert;

public final class Customization {
	private final String path;
	private final EqualsComparator<Object> comparator;

	public Customization(String path, EqualsComparator<Object> comparator) {
        assert path != null;
        assert comparator != null;
		this.path = path;
		this.comparator = comparator;
	}

	public static Customization customization(String path, EqualsComparator<Object> comparator) {
		return new Customization(path, comparator);
	}

    public boolean appliesToPath(String path) {
        return this.path.equals(path);
    }

    public boolean matches(Object actual, Object expected) {
        return comparator.equal(actual, expected);
    }
}
