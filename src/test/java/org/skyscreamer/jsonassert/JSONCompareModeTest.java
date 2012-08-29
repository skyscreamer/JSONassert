package org.skyscreamer.jsonassert;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;
import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT_ORDER;

import org.junit.Test;

/**
 * Unit tests for {@link JSONCompareMode}
 */
public class JSONCompareModeTest {
    @Test
    public void testWithStrictOrdering() {
        assertTrue(LENIENT.withStrictOrdering(true).hasStrictOrder());
        assertTrue(LENIENT.withStrictOrdering(true).isExtensible());
        assertTrue(NON_EXTENSIBLE.withStrictOrdering(true).hasStrictOrder());
        assertFalse(NON_EXTENSIBLE.withStrictOrdering(true).isExtensible());
        
        assertEquals(STRICT.withStrictOrdering(true), STRICT);
        assertEquals(STRICT_ORDER.withStrictOrdering(true), STRICT_ORDER);
    }
    
    @Test
    public void testWithoutStrictOrdering() {
        assertFalse(STRICT_ORDER.withStrictOrdering(false).hasStrictOrder());
        assertTrue(STRICT_ORDER.withStrictOrdering(false).isExtensible());
        assertFalse(STRICT.withStrictOrdering(false).hasStrictOrder());
        assertFalse(STRICT.withStrictOrdering(false).isExtensible());
        
        assertEquals(LENIENT.withStrictOrdering(false), LENIENT);
        assertEquals(NON_EXTENSIBLE.withStrictOrdering(false), NON_EXTENSIBLE);
    }
    
    @Test
    public void testWithExtensibility() {
        assertTrue(NON_EXTENSIBLE.withExtensible(true).isExtensible());
        assertFalse(NON_EXTENSIBLE.withExtensible(true).hasStrictOrder());
        assertTrue(STRICT.withExtensible(true).isExtensible());
        assertTrue(STRICT.withExtensible(true).hasStrictOrder());
        
        assertEquals(LENIENT.withExtensible(true), LENIENT);
        assertEquals(STRICT_ORDER.withExtensible(true), STRICT_ORDER);
    }
    
    @Test
    public void testWithoutExtensibility() {
        assertFalse(STRICT_ORDER.withExtensible(false).isExtensible());
        assertTrue(STRICT_ORDER.withExtensible(false).hasStrictOrder());
        assertFalse(LENIENT.withExtensible(false).isExtensible());
        assertFalse(LENIENT.withExtensible(false).hasStrictOrder());
        
        assertEquals(STRICT.withExtensible(false), STRICT);
        assertEquals(NON_EXTENSIBLE.withExtensible(false), NON_EXTENSIBLE);
    }
}
