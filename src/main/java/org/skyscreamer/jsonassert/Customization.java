package org.skyscreamer.jsonassert;

import org.hamcrest.Matcher;

public final class Customization {
	private final String path;
	private final Matcher<?> matcher;

	public Customization(String path, Matcher<?> matcher) {
		this.path = path;
		this.matcher = matcher;
	}

	public static Customization customization(String path, Matcher<?> matcher) {
		return new Customization(path, matcher);
	}
}
