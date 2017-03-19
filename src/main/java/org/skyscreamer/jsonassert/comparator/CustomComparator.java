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

import org.json.JSONException;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.ValueMatcherException;

import java.util.Arrays;
import java.util.Collection;

public class CustomComparator extends DefaultComparator {

    private final Collection<Customization> customizations;

    public CustomComparator(JSONCompareMode mode,  Customization... customizations) {
        super(mode);
        this.customizations = Arrays.asList(customizations);
    }

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result) throws JSONException {
        Customization customization = getCustomization(prefix);
        if (customization != null) {
            try {
    	        if (!customization.matches(prefix, actualValue, expectedValue, result)) {
                    result.fail(prefix, expectedValue, actualValue);
                }
            }
            catch (ValueMatcherException e) {
                result.fail(prefix, e);
            }
        } else {
            super.compareValues(prefix, expectedValue, actualValue, result);
        }
    }

    private Customization getCustomization(String path) {
        for (Customization c : customizations)
            if (c.appliesToPath(path))
                return c;
        return null;
    }
}
