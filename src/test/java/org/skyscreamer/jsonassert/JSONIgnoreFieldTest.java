package org.skyscreamer.jsonassert;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.junit.Test;

public class JSONIgnoreFieldTest {

    String actual = "{\"first\":\"actual\", \"second\":1}";
    String expected = "{\"first\":\"expected\", \"second\":1}";

    String deepActual = "{\n" +
            "    \"outer\":\n" +
            "    {\n" +
            "        \"inner\":\n" +
            "        {\n" +
            "            \"value\": \"actual\",\n" +
            "            \"otherValue\": \"foo\"\n" +
            "        }\n" +
            "    }\n" +
            "}";
    String deepExpected = "{\n" +
            "    \"outer\":\n" +
            "    {\n" +
            "        \"inner\":\n" +
            "        {\n" +
            "            \"value\": \"expected\",\n" +
            "            \"otherValue\": \"foo\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    String arrayActual = "{\n" +
            "  \"foo\": {\n" +
            "    \"array\": [{\n" +
            "      \"ignore1\": \"actual\",\n" +
            "      \"ignore2\": \"actual\",\n" +
            "      \"realdata\": \"data\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"ignore1\": \"actual\",\n" +
            "      \"realdata\": \"data\"\n" +
            "    }]\n" +
            "  }\n" +
            "}";
    String arrayExpected = "{\n" +
            "  \"foo\": {\n" +
            "    \"array\": [{\n" +
            "      \"ignore1\": \"expected\",\n" +
            "      \"ignore2\": \"expected\",\n" +
            "      \"realdata\": \"data\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"ignore1\": \"expected\",\n" +
            "      \"realdata\": \"data\"\n" +
            "    }]\n" +
            "  }\n" +
            "}";
    
	@Test
	public void testIgnoreWith1Level() throws JSONException {
		List<String> keysToIgnore = new ArrayList<String>();
		keysToIgnore.add("first");
		
		JSONAssert.assertEquals(expected, actual, keysToIgnore, true);
	}

	@Test
	public void testIgnoreWithDeepLevel() throws JSONException {
		List<String> keysToIgnore = new ArrayList<String>();
		keysToIgnore.add("outer.inner.value");
		
		System.out.println(deepExpected);
		System.out.println(deepActual);
		
		JSONAssert.assertEquals(deepExpected, deepActual, keysToIgnore, true);
	}
	
	@Test
	public void testIgnoreWithinArray() throws JSONException {
		System.out.println(arrayActual);
		System.out.println(arrayExpected);
		
		List<String> keysToIgnore = new ArrayList<String>();
		keysToIgnore.add("foo.array[*].ignore1");
		keysToIgnore.add("foo.array[0].ignore2");
		
		JSONAssert.assertEquals(arrayExpected, arrayActual, keysToIgnore, true);
		
	}
}
