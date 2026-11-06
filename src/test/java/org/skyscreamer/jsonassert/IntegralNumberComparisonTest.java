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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntegralNumberComparisonTest {
    @Test
    public void adjacentLongsInObjectsAreDifferent() {
        JSONAssert.assertNotEquals("{key:3965376318358833921}",
                "{key:3965376318358833920}", JSONCompareMode.STRICT);
    }

    @Test
    public void negativeLongsAreDifferent() {
        JSONAssert.assertNotEquals("{key:-3965376318358833921}",
                "{key:-3965376318358833920}", JSONCompareMode.STRICT);
    }

    @Test
    public void adjacentLongsInOrderedArraysAreDifferent() {
        JSONAssert.assertNotEquals("[3965376318358833921]",
                "[3965376318358833920]", JSONCompareMode.STRICT_ORDER);
    }

    @Test
    public void nestedLongsAreDifferent() {
        JSONAssert.assertNotEquals("{items:[{value:9007199254740992}]}",
                "{items:[{value:9007199254740993}]}", JSONCompareMode.LENIENT);
    }

    @Test
    public void adjacentBigIntegersAreDifferent() {
        JSONAssert.assertNotEquals("{key:9223372036854775808}",
                "{key:9223372036854775809}", JSONCompareMode.STRICT);
    }

    @Test
    public void bigIntegerAndLongBoundaryAreDifferent() {
        assertDifferent(Long.MAX_VALUE, new BigInteger("9223372036854775808"));
        assertDifferent(new BigInteger("-9223372036854775809"), Long.MIN_VALUE);
    }

    @Test
    public void bigIntegerValuesDoNotWrapToLong() {
        assertDifferent(BigInteger.ONE.shiftLeft(64), 0L);
    }

    @Test
    public void equalIntegralTypesCompareByValue() {
        Number[] values = {Byte.valueOf((byte) 17), Short.valueOf((short) 17),
                Integer.valueOf(17), Long.valueOf(17), BigInteger.valueOf(17)};
        for (Number expected : values) {
            for (Number actual : values) {
                assertSameNumber(expected, actual);
            }
        }
        assertSameNumber(Long.MAX_VALUE, BigInteger.valueOf(Long.MAX_VALUE));
        assertSameNumber(BigInteger.valueOf(Long.MIN_VALUE), Long.MIN_VALUE);
    }

    @Test
    public void equalLargeParsedNumbersRemainEqual() {
        JSONAssert.assertEquals("{key:3965376318358833921}",
                "{key:3965376318358833921}", JSONCompareMode.STRICT);
        JSONAssert.assertEquals("{key:9223372036854775808}",
                "{key:9223372036854775808}", JSONCompareMode.STRICT);
    }

    @Test
    public void mixedDecimalAndIntegralCoercionIsPreserved() {
        JSONAssert.assertEquals("{key:17}", "{key:17.0}", JSONCompareMode.STRICT);
        assertSameNumber(new BigDecimal("17.00"), 17L);
        assertSameNumber(17.0f, 17);
        assertSameNumber(17L, 17.0d);
        assertDifferent(17, 17.5d);
    }

    @Test
    public void floatingPointSpecialValuesKeepTheirBehavior() {
        assertSameNumber(-0.0d, 0.0d);
        assertSameNumber(Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        assertDifferent(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        assertDifferent(Double.NaN, Float.NaN);
    }

    @Test
    public void protectedComparisonOverrideIsStillUsed() {
        DefaultComparator comparator = new DefaultComparator(JSONCompareMode.STRICT) {
            @Override
            protected boolean areNotSameDoubles(Object expected, Object actual) {
                return false;
            }
        };
        JSONCompareResult result = new JSONCompareResult();
        comparator.compareValues("key", 9007199254740992L, 9007199254740993L, result);
        assertTrue(result.passed());
    }

    private static void assertSameNumber(Number expected, Number actual) {
        assertTrue(compare(expected, actual).passed());
    }

    private static void assertDifferent(Number expected, Number actual) {
        assertFalse(compare(expected, actual).passed());
    }

    private static JSONCompareResult compare(Number expected, Number actual) {
        JSONCompareResult result = new JSONCompareResult();
        new DefaultComparator(JSONCompareMode.STRICT).compareValues("key", expected, actual, result);
        return result;
    }
}
