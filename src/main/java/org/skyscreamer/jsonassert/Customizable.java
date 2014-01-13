package org.skyscreamer.jsonassert;

/**
 * Interface implemented by classes that determine if JSON elements require
 * custom comparisons when compared by CustomComparator.
 * 
 * @author Duncan Mackinder
 * 
 */
public interface Customizable {

	public abstract boolean appliesToPath(String path);

	public abstract boolean matches(Object actual, Object expected);

}