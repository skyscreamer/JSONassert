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

import org.junit.Assert;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

/**
 * Unit tests for RegularExpressionValueMatcher
 * 
 * @author Duncan Mackinder
 * 
 */
public class RegularExpressionValueMatcherTest {
	private static final String ARRAY_ELEMENT_PREFIX = "d.results[0].__metadata.uri";
	private static final String JSON_STRING_WITH_ARRAY = "{d:{results:[{__metadata:{uri:\"http://localhost:80/Person('1')\",type:Person},id:1}]}}";
	private static final String CONSTANT_URI_REGEX_EXPECTED_JSON = "{d:{results:[{__metadata:{uri:X}}]}}";

	private void doTest(String jsonPath, String regex, String expectedJSON,
			String actualJSON) throws JSONException {
		JSONAssert.assertEquals(expectedJSON, actualJSON, null, new CustomComparator(
				JSONCompareMode.STRICT_ORDER, new Customization(jsonPath,
						new RegularExpressionValueMatcher<Object>(regex))));
	}

	@Test
	public void constantRegexWithSimplePathMatchsStringAttribute() throws JSONException {
		doTest("a", "v.", "{a:x}", "{a:v1}");
	}

	@Test
	public void constantRegexWithThreeLevelPathMatchsStringAttribute() throws JSONException {
		doTest("a.b.c", ".*Is.*", "{a:{b:{c:x}}}", "{a:{b:{c:thisIsAString}}}");
	}

	@Test
	public void dynamicRegexWithSimplePathMatchsStringAttribute() throws JSONException {
		doTest("a", null, "{a:\"v.\"}", "{a:v1}");
	}

	@Test
	public void dynamicRegexWithThreeLevelPathMatchsStringAttribute() throws JSONException {
		doTest("a.b.c", null, "{a:{b:{c:\".*Is.*\"}}}",
				"{a:{b:{c:thisIsAString}}}");
	}

	@Test
	public void constantRegexMatchesStringAttributeInsideArray() throws JSONException {
		doTest(ARRAY_ELEMENT_PREFIX, "http://localhost:80/Person\\('\\d+'\\)", CONSTANT_URI_REGEX_EXPECTED_JSON, JSON_STRING_WITH_ARRAY);
	}

    @Test
    public void dynamicRegexMatchesStringAttributeInsideArray() throws JSONException {
        doTest(ARRAY_ELEMENT_PREFIX, null, "{d:{results:[{__metadata:{uri:\"http://localhost:80/Person\\\\('\\\\d+'\\\\)\"}}]}}", JSON_STRING_WITH_ARRAY);
    }

    @Test
    public void dynamicRegexMatchesStringAttributeInsideArrayWithNoArgConstructor() throws JSONException {
		JSONAssert.assertEquals("{d:{results:[{__metadata:{uri:\"http://localhost:80/Person\\\\('\\\\d+'\\\\)\"}}]}}", JSON_STRING_WITH_ARRAY, null, new CustomComparator(
				JSONCompareMode.STRICT_ORDER, new Customization(ARRAY_ELEMENT_PREFIX,
						new RegularExpressionValueMatcher<Object>())));
    }

    @Test
    public void failsWhenDynamicRegexInvalid() throws JSONException {
    	try {
    		doTest(ARRAY_ELEMENT_PREFIX, null, "{d:{results:[{__metadata:{uri:\"http://localhost:80/Person('\\\\d+'\\\\)\"}}]}}", JSON_STRING_WITH_ARRAY);
    	}
    	catch (AssertionError e) {
    		Assert.assertTrue("Invalid exception message returned: "+ e.getMessage(), e.getMessage().startsWith(ARRAY_ELEMENT_PREFIX + ": Dynamic expected pattern invalid: "));
    	}
    }

    @Test
    public void failsWhenDynamicRegexDoesNotMatchStringAttributeInsideArray() throws JSONException {
    	try {
    		doTest(ARRAY_ELEMENT_PREFIX, null, "{d:{results:[{__metadata:{uri:\"http://localhost:80/Person\\\\('\\\\w+'\\\\)\"}}]}}", JSON_STRING_WITH_ARRAY);
    	}
    	catch (AssertionError e) {
    		Assert.assertTrue("Invalid exception message returned: "+ e.getMessage(), e.getMessage().startsWith(ARRAY_ELEMENT_PREFIX + ": Dynamic expected pattern did not match value"));
    	}
    }

    @Test
    public void failsWhenConstantRegexInvalid() throws JSONException {
    	try {
    		doTest(ARRAY_ELEMENT_PREFIX, "http://localhost:80/Person\\\\['\\\\d+'\\\\)", CONSTANT_URI_REGEX_EXPECTED_JSON, JSON_STRING_WITH_ARRAY);
    	}
    	catch (IllegalArgumentException e) {
    		Assert.assertTrue("Invalid exception message returned: "+ e.getMessage(), e.getMessage().startsWith("Constant expected pattern invalid: "));
    	}
    }

    @Test
    public void failsWhenConstantRegexDoesNotMatchStringAttributeInsideArray() throws JSONException {
    	try {
    		doTest(ARRAY_ELEMENT_PREFIX, "http://localhost:80/Person\\\\('\\\\w+'\\\\)", CONSTANT_URI_REGEX_EXPECTED_JSON, JSON_STRING_WITH_ARRAY);
    	}
    	catch (AssertionError e) {
    		Assert.assertTrue("Invalid exception message returned: "+ e.getMessage(), e.getMessage().startsWith(ARRAY_ELEMENT_PREFIX + ": Constant expected pattern did not match value"));
    	}
    }
}
