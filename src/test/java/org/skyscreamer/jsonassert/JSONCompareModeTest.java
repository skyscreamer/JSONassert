/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
        
        assertEquals(STRICT, STRICT.withStrictOrdering(true));
        assertEquals(STRICT_ORDER, STRICT_ORDER.withStrictOrdering(true));
    }
    
    @Test
    public void testWithoutStrictOrdering() {
        assertFalse(STRICT_ORDER.withStrictOrdering(false).hasStrictOrder());
        assertTrue(STRICT_ORDER.withStrictOrdering(false).isExtensible());
        assertFalse(STRICT.withStrictOrdering(false).hasStrictOrder());
        assertFalse(STRICT.withStrictOrdering(false).isExtensible());
        
        assertEquals(LENIENT, LENIENT.withStrictOrdering(false));
        assertEquals(NON_EXTENSIBLE, NON_EXTENSIBLE.withStrictOrdering(false));
    }
    
    @Test
    public void testWithExtensibility() {
        assertTrue(NON_EXTENSIBLE.withExtensible(true).isExtensible());
        assertFalse(NON_EXTENSIBLE.withExtensible(true).hasStrictOrder());
        assertTrue(STRICT.withExtensible(true).isExtensible());
        assertTrue(STRICT.withExtensible(true).hasStrictOrder());
        
        assertEquals(LENIENT, LENIENT.withExtensible(true));
        assertEquals(STRICT_ORDER, STRICT_ORDER.withExtensible(true));
    }
    
    @Test
    public void testWithoutExtensibility() {
        assertFalse(STRICT_ORDER.withExtensible(false).isExtensible());
        assertTrue(STRICT_ORDER.withExtensible(false).hasStrictOrder());
        assertFalse(LENIENT.withExtensible(false).isExtensible());
        assertFalse(LENIENT.withExtensible(false).hasStrictOrder());
        
        assertEquals(STRICT, STRICT.withExtensible(false));
        assertEquals(NON_EXTENSIBLE, NON_EXTENSIBLE.withExtensible(false));
    }
}
