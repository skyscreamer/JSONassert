/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skyscreamer.jsonassert;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for LENIENT comparison of simple-value arrays containing numerically equal
 * values of different runtime types (issue #212).
 */
public class LenientSimpleArrayNumberTest {

    @Test
    public void integerAndDoubleArrayElementsCompareEqual() throws Exception {
        assertTrue(JSONCompare.compareJSON("[1]", "[1.0]", JSONCompareMode.LENIENT).passed());
    }

    @Test
    public void exponentNotationComparesEqualToPlainInteger() throws Exception {
        assertTrue(JSONCompare.compareJSON("[1e2]", "[100]", JSONCompareMode.LENIENT).passed());
    }

    @Test
    public void differentNumericValuesStillFail() throws Exception {
        assertFalse(JSONCompare.compareJSON("[1]", "[2.0]", JSONCompareMode.LENIENT).passed());
    }

    @Test
    public void cardinalityIsRespectedAcrossNumericTypes() throws Exception {
        assertTrue(JSONCompare.compareJSON("[1, 2]", "[2.0, 1.0]", JSONCompareMode.LENIENT).passed());
        assertFalse(JSONCompare.compareJSON("[1, 1]", "[1.0, 2.0]", JSONCompareMode.LENIENT).passed());
    }

    @Test
    public void strictOrderComparisonUnaffected() throws Exception {
        assertTrue(JSONCompare.compareJSON("[1]", "[1.0]", JSONCompareMode.STRICT_ORDER).passed());
        assertTrue(JSONCompare.compareJSON("[1,2]", "[2,1]", JSONCompareMode.LENIENT).passed());
    }
}
