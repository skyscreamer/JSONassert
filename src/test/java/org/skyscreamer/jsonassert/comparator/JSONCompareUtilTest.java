package org.skyscreamer.jsonassert.comparator;

import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Test JSONCompareUtil
 *
 * @author Carter Page <carter@skyscreamer.org>
 */
public class JSONCompareUtilTest {
    @Test
    public void testGetCardinalityMap() {
        final int NUM_A = 76;
        final int NUM_B = 3;
        final int NUM_C = 0;
        final int NUM_D = 1;
        final int NUM_E = 2;

        List<String> listToTest = new ArrayList<String>(NUM_A + NUM_B + NUM_C + NUM_D + NUM_E);
        for (int i = 0; i < NUM_A; ++i) listToTest.add("A");
        for (int i = 0; i < NUM_B; ++i) listToTest.add("B");
        for (int i = 0; i < NUM_C; ++i) listToTest.add("C");
        for (int i = 0; i < NUM_D; ++i) listToTest.add("D");
        for (int i = 0; i < NUM_E; ++i) listToTest.add("E");
        Collections.shuffle(listToTest);

        Map<String, Integer> cardinalityMap = JSONCompareUtil.getCardinalityMap(listToTest);
        Assert.assertEquals(NUM_A, cardinalityMap.get("A").intValue());
        Assert.assertEquals(NUM_B, cardinalityMap.get("B").intValue());
        Assert.assertNull(cardinalityMap.get("C"));
        Assert.assertEquals(NUM_D, cardinalityMap.get("D").intValue());
        Assert.assertEquals(NUM_E, cardinalityMap.get("E").intValue());
    }
}
