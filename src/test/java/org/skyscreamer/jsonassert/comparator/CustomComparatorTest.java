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

package org.skyscreamer.jsonassert.comparator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import junit.framework.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

/**
 * @author <a href="mailto:aiveeen@gmail.com">Ivan Zaytsev</a>
 *         2013-01-04
 */
public class CustomComparatorTest {

    private static class ArrayOfJsonObjectsComparator extends DefaultComparator {
        public ArrayOfJsonObjectsComparator(JSONCompareMode mode) {
            super(mode);
        }

        @Override
        public void compareJSONArray(String prefix, JSONArray expected, JSONArray actual, JSONCompareResult result) throws JSONException {
            compareJSONArrayOfJsonObjects(prefix, expected, actual, result);
        }
    }

    @Test
    public void testFullArrayComparison() throws Exception {
        JSONCompareResult compareResult = JSONCompare.compareJSON(
                "[{id:1}, {id:3}, {id:5}]",
                "[{id:1}, {id:3}, {id:6}, {id:7}]", new ArrayOfJsonObjectsComparator(JSONCompareMode.LENIENT)
        );

        Assert.assertTrue(compareResult.failed());
        String message = compareResult.getMessage().replaceAll("\n", "");
        Assert.assertTrue(message, message.matches(".*id=5.*Expected.*id=6.*Unexpected.*id=7.*Unexpected.*"));
    }
}
