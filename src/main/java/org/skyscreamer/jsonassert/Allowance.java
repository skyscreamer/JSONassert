package org.skyscreamer.jsonassert;

public enum Allowance {
	DISALLOWED, ALLOWED;

	static Allowance fromBoolean(boolean b) {
		return b ? ALLOWED : DISALLOWED;
	}
}
