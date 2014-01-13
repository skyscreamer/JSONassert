package org.skyscreamer.jsonassert;

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
	private void doTest(String jsonPath, String regex, String expectedJSON,
			String actualJSON) throws JSONException {
		JSONAssert.assertEquals(expectedJSON, actualJSON, new CustomComparator(
				JSONCompareMode.STRICT_ORDER, new Customization(jsonPath,
						new RegularExpressionValueMatcher<Object>(regex))));
	}

	@Test
	public void fixedRegexMatchesStringAttributeInsideArray() throws JSONException {
		doTest("d.results[0].__metadata.uri", "http://localhost:80/Person\\('\\d+'\\)", "{d:{results:[{__metadata:{uri:X}}]}}", "{d:{results:[{__metadata:{uri:\"http://localhost:80/Person('1')\",type:Person},id:1}]}}");
	}
	
	@Test
	public void fixedRegexWithSimplePathMatchsStringAttribute() throws JSONException {
		doTest("a", "v.", "{a:x}", "{a:v1}");
	}

	@Test
	public void fixedRegexWithThreeLevelPathMatchsStringAttribute() throws JSONException {
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

}
