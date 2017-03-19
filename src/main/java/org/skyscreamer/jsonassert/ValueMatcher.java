package org.skyscreamer.jsonassert;

/**
 * Represents a value matcher that can compare two objects for equality.
 *
 * @param <T> the object type to compare
 */
public interface ValueMatcher<T> {

    /**
     * Compares the two provided objects whether they are equal.
     *
     * @param o1 the first object to check
     * @param o2 the object to check the first against
     * @return true if the objects are equal, false otherwise
     */
    boolean equal(T o1, T o2);

}
