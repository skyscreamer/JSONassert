package org.skyscreamer.jsonassert;

import org.hamcrest.Matcher;

/**
 * Delegate equality comparison to an org.hamcrest.Matcher
 * @param <T>
 */
public final class HamcrestEqualsComparator<T> implements EqualsComparator<T> {

    private final Matcher<?> matcher;

    public HamcrestEqualsComparator(Matcher<?> matcher) {
        this.matcher = matcher;
    }

    /**
     * The matcher is invoked on the first parameter, ignoring the second
     * @param o1
     * @param o2 ignored
     * @return true if mather matches o1
     */
    @Override
    public boolean equal(T o1, T o2) {
        return matcher.matches(o1);
    }
}
