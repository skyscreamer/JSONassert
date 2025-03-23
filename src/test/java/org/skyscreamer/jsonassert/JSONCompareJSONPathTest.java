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

import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import org.junit.Test;
import org.skyscreamer.jsonassert.comparator.JSONPathComparator;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;

/**
 * Tests for the {@link JSONPathComparator} comparator, at the JSON String level.
 *
 * @author Shane B. (<a href="mailto:shane@wander.dev">shane@wander.dev</a>)
 */
public class JSONCompareJSONPathTest {
    private static final Configuration CONFIG = Configuration.builder()
            .jsonProvider(new JsonOrgJsonProvider())
            .build();

    private static final Configuration CONFIG_NULLABLE_LEAVES = CONFIG.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

    final String bookStoreTemplate = "{\n" +
            "    \"store\": {\n" +
            "        \"book\": [\n" +
            "            {\n" +
            "                \"category\": \"reference\",\n" +
            "                \"author\": \"Nigel Rees\",\n" +
            "                \"title\": \"Sayings of the Century\",\n" +
            "                \"price\": 8.95\n" +
            "            },\n" +
            "            {\n" +
            "                \"category\": \"fiction\",\n" +
            "                \"author\": \"Evelyn Waugh\",\n" +
            "                \"title\": \"Sword of Honour\",\n" +
            "                \"price\": 12.99\n" +
            "            },\n" +
            "            {\n" +
            "                \"category\": \"fiction\",\n" +
            "                \"author\": \"Herman Melville\",\n" +
            "                \"title\": \"Moby Dick\",\n" +
            "                \"isbn\": \"0-553-21311-3\",\n" +
            "                \"price\": 8.99\n" +
            "            },\n" +
            "            {\n" +
            "                \"category\": \"fiction\",\n" +
            "                \"author\": \"J. R. R. Tolkien\",\n" +
            "                \"title\": \"The Lord of the Rings\",\n" +
            "                \"isbn\": \"0-395-19395-8\",\n" +
            "                \"price\": 22.99\n" +
            "            }\n" +
            "        ],\n" +
            "        \"bicycle\": {\n" +
            "            \"color\": \"red\",\n" +
            "            \"price\": 19.95\n" +
            "        }\n" +
            "    },\n" +
            "    \"expensive\": 10\n" +
            "}";

    String jsonTemplateIdNesting = "{\n" +
            "    \"id\": \"123456\",\n" +
            "    \"other\": 1,\n" +
            "    \"more\": [1, 2, 3],\n" +
            "    \"evenMore\": {\n" +
            "        \"nesting\": {\n" +
            "            \"id\": \"123456\"\n" +
            "        },\n" +
            "        \"and\": \"then\",\n" +
            "        \"more\": [\n" +
            "            {\n" +
            "                \"id\": \"123456\",\n" +
            "                \"one\": \"foo\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": \"123456\",\n" +
            "                \"one\": \"bar\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"id\": \"123456\",\n" +
            "                \"one\": \"baz\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    String jsonTemplateMultipleValidations = "{\n" +
            "    \"foo\": \"bar\",\n" +
            "    \"items\": [\n" +
            "        {\n" +
            "            \"id\": 1,\n" +
            "            \"timestamp\": 1742679828951\n" +
            "        }, \n" +
            "        {\n" +
            "            \"id\": 2,\n" +
            "            \"timestamp\": 1742679828952\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 3,\n" +
            "            \"timestamp\": 1742679828953\n" +
            "        }\n" +
            "    ],\n" +
            "    \"timestamp\": 1742679828954,\n" +
            "    \"thisFieldWontMatchButShouldBeIgnored\": \"i don't match!\"\n" +
            "}";

    @Test
    public void whenBasicSetup() {
        DocumentContext expected = JsonPath.parse(bookStoreTemplate, CONFIG);
        expected.set("$.expensive", 11);

        JSONCompareResult result = compareJSON(
                expected.jsonString(),
                bookStoreTemplate,
                new JSONPathComparator(JSONCompareMode.STRICT,
                        new JSONPathCustomization("$.expensive", (expectedNode, actualNode) -> (int)actualNode == 10))
        );
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenBasicQueryInArray() {
        DocumentContext actual = JsonPath.parse(bookStoreTemplate, CONFIG);
        actual.set("$.store.book[1].title", "override!");

        JSONCompareResult result = compareJSON(
                bookStoreTemplate,
                actual.jsonString(),
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // this isn't jsonpath!
                        new JSONPathCustomization("$.store.book[1].title", (expectedNode, actualNode) -> actualNode.equals("override!")))
        );
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenInvalidQuery() {
        DocumentContext actual = JsonPath.parse(bookStoreTemplate, CONFIG);
        actual.set("$.store.book[1].title", "override!");

        // I think throwing here makes sense?
        assertThrows(PathNotFoundException.class, () -> compareJSON(
                bookStoreTemplate,
                actual.jsonString(),
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // this isn't jsonpath!
                        new JSONPathCustomization("/store/book[1]/title", (expectedNode, actualNode) -> actualNode.equals("override!")))
        ));
    }

    @Test
    public void whenPathStartingWithout$Test() {
        DocumentContext expected = JsonPath.parse(bookStoreTemplate, CONFIG);
        expected.set("$.expensive", 11);

        JSONCompareResult result = compareJSON(
                expected.jsonString(),
                bookStoreTemplate,
                new JSONPathComparator(JSONCompareMode.STRICT,
                        new JSONPathCustomization("expensive", (expectedNode, actualNode) -> (int)actualNode == 10))
        );
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenPathStartingWithout$ArrayTest() {
        DocumentContext actual = JsonPath.parse(bookStoreTemplate, CONFIG);
        actual.set("$.store.book[1].title", "override!");

        JSONCompareResult result = compareJSON(
                bookStoreTemplate,
                actual.jsonString(),
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // this isn't jsonpath!
                        new JSONPathCustomization("store.book[1].title", (expectedNode, actualNode) -> actualNode.equals("override!")))
        );
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenMultiFieldCustomValidationOverrideCase() {
        DocumentContext expected = JsonPath.parse(jsonTemplateMultipleValidations, CONFIG);
        // make it so that the template does not match anymore:
        expected.set("$.items[*].timestamp", 0L);
        expected.set("$.timestamp", 0L);
        expected.set("$.thisFieldWontMatchButShouldBeIgnored", "ignoreMe");

        JSONCompareResult result = compareJSON(
                expected.jsonString(),
                jsonTemplateMultipleValidations,
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // matches the nested timestamps inside of the array
                        new JSONPathCustomization("$.**.timestamp", (expectedNode, actualNode) -> actualNode instanceof Long),
                        // matches the timestamp at the root
                        new JSONPathCustomization("$.timestamp", (expectedNode, actualNode) -> (Long) actualNode == 1742679828954L),
                        // generic ignore
                        JSONPathCustomization.ofIgnore("$.thisFieldWontMatchButShouldBeIgnored"))
        );
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenHasNestedCustomValidation() {
        DocumentContext expected = JsonPath.parse(jsonTemplateIdNesting, CONFIG);
        expected.set("$..id", "0");

        JSONCompareResult result = compareJSON(
                expected.jsonString(),
                jsonTemplateIdNesting,
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // all the id fields, which do not match in actual vs expected, are being overridden here:
                        new JSONPathCustomization("$..id", (expectedNode, actualNode) -> actualNode.equals("123456")))
        );
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenHasUuidsWithCustomUuidValidation() {
        DocumentContext expected = JsonPath.parse(bookStoreTemplate, CONFIG_NULLABLE_LEAVES);
        // add these fields to the template for the test
        expected.set("$.store.book[*].uuid", "00000000-0000-0000-0000-000000000000");
        expected.set("$.store.bicycle.uuid", "00000000-0000-0000-0000-000000000000");

        // populate these fields with random ones for the test
        DocumentContext actual = JsonPath.parse(bookStoreTemplate, CONFIG_NULLABLE_LEAVES);
        for (int i = 0; i < 4; ++i) {
            actual.set("$.store.book[" + i + "].uuid", UUID.randomUUID().toString());
        }
        actual.set("$.store.bicycle.uuid", UUID.randomUUID().toString());

        JSONCompareResult result = compareJSON(
                expected.jsonString(),
                actual.jsonString(),
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // We ignore the field expensive at any level in the json IF the value of it is > 0 or exactly -1
                        new JSONPathCustomization(
                                "$..uuid",
                                (expectedNode, actualNode) -> {
                                    try {
                                        UUID.fromString((String) actualNode); // try to parse as UUID
                                        return true; // when parsing succeeds
                                    } catch (IllegalArgumentException e) {
                                        return false; // when parsing fails
                                    }
                                })
        ));
        // even though there was a UUID mismatch, this passes
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenHasUuidsWithCustomUuidValidationFailCase() {
        DocumentContext expected = JsonPath.parse(bookStoreTemplate, CONFIG_NULLABLE_LEAVES);
        // add these fields to the template for the test
        expected.set("$.store.book[*].uuid", "00000000-0000-0000-0000-000000000000");
        expected.set("$.store.bicycle.uuid", "00000000-0000-0000-0000-000000000000");

        // populate these fields with random ones for the test
        DocumentContext actual = JsonPath.parse(bookStoreTemplate, CONFIG_NULLABLE_LEAVES);
        for (int i = 0; i < 4; ++i) {
            actual.set("$.store.book[" + i + "].uuid", UUID.randomUUID().toString());
        }
        actual.set("$.store.bicycle.uuid", "not-valid-uuid");

        JSONCompareResult result = compareJSON(
                expected.jsonString(),
                actual.jsonString(),
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // We ignore the field expensive at any level in the json IF the value of it is > 0 or exactly -1
                        new JSONPathCustomization(
                                "$..uuid",
                                (expectedNode, actualNode) -> {
                                    try {
                                        UUID.fromString((String) actualNode); // try to parse as UUID
                                        return true; // when parsing succeeds
                                    } catch (IllegalArgumentException e) {
                                        return false; // when parsing fails
                                    }
                                })
                ));
        // even though there was a UUID mismatch, this passes
        assertTrue(result.getMessage(), result.failed());
        assertEquals(1, result.getFieldFailures().size());
        assertEquals("store.bicycle.uuid", result.getFieldFailures().get(0).getField());
    }

    @Test
    public void whenJsonPathMatchesAutoboxedPrimitive() {
        DocumentContext expected = JsonPath.parse(bookStoreTemplate, CONFIG);
        expected.set("$.expensive", 0);

        DocumentContext actual = JsonPath.parse(bookStoreTemplate, CONFIG);
        actual.set("$.expensive", -1);

        JSONCompareResult result = compareJSON(
                expected.jsonString(),
                actual.jsonString(),
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // We ignore the field expensive at any level in the json IF the value of it is > 0 or exactly -1
                        new JSONPathCustomization(
                                "$..expensive",
                                (expectedNode, actualNode) -> (int)actualNode > 0 || (int)actualNode == -1))
        );
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenJsonPathHasRegexQuery() {
        DocumentContext expected = JsonPath.parse(bookStoreTemplate, CONFIG);
        // consider this to be a kind of "placeholder value"
        // (this would usually cause a mismatch!)
        // (a more "real-world" example of something like this might be a UUID that is randomly populated)
        expected.set("$.store.book[?(@.isbn)].isbn", "0-000-00000-0");

        Set<String> allowedISBN = new HashSet<>();
        allowedISBN.add("0-553-21311-3");
        allowedISBN.add("0-395-19395-8");

        JSONCompareResult result = compareJSON(
                expected.jsonString(),
                bookStoreTemplate,
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // for all the books with an isbn field,
                        // customise to further manually verify that the isbn field must be in the allowed list
                        new JSONPathCustomization(
                                "$..book[?(@.isbn =~ /0-.*/)].isbn",
                                (expectedNode, actualNode) -> allowedISBN.contains((String) actualNode)))
        );
        assertTrue(result.getMessage(), result.passed());
    }

    @Test
    public void whenJsonPathHasRegexQueryFailCase() {
        DocumentContext expected = JsonPath.parse(bookStoreTemplate, CONFIG);
        // consider this to be a kind of "placeholder value"
        // (this would usually cause a mismatch!)
        // (a more "real-world" example of something like this might be a UUID that is randomly populated)
        expected.set("$.store.book[?(@.isbn)].isbn", "0-000-00000-0");

        Set<String> allowedISBN = new HashSet<>();
        allowedISBN.add("0-553-21311-3");

        JSONCompareResult result = compareJSON(
                expected.jsonString(),
                bookStoreTemplate,
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // for all the books with an isbn field,
                        // customise to further manually verify that the isbn field must be in the allowed list
                        new JSONPathCustomization(
                                "$..book[?(@.isbn =~ /0-.*/)].isbn",
                                (expectedNode, actualNode) -> allowedISBN.contains((String) actualNode)))
        );
        assertTrue(result.getMessage(), result.failed());
        assertEquals(1, result.getFieldFailures().size());
        assertEquals("store.book[3].isbn", result.getFieldFailures().get(0).getField());
    }

    @Test
    public void whenJsonPathHasRegexQueryWithNoMatchesFailCase() {
        DocumentContext expected = JsonPath.parse(bookStoreTemplate, CONFIG);
        // consider this to be a kind of "placeholder value"
        // (this would usually cause a mismatch!)
        // (a more "real-world" example of something like this might be a UUID that is randomly populated)
        expected.set("$.store.book[?(@.isbn)].isbn", "0-000-00000-0");

        Set<String> allowedISBN = new HashSet<>();
        allowedISBN.add("0-553-21311-3");
        allowedISBN.add("0-395-19395-8");

        JSONCompareResult result = compareJSON(
                expected.jsonString(),
                bookStoreTemplate,
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // for all the books with an isbn field,
                        // customise to further manually verify that the isbn field must be in the allowed list
                        // (there are no isbns that start with "1-"!)
                        new JSONPathCustomization(
                                "$..book[?(@.isbn =~ /1-.*/)].isbn",
                                (expectedNode, actualNode) -> allowedISBN.contains((String) actualNode)))
        );
        assertTrue(result.getMessage(), result.failed());
        assertEquals(2, result.getFieldFailures().size());
        assertEquals("store.book[2].isbn", result.getFieldFailures().get(0).getField());
        assertEquals("store.book[3].isbn", result.getFieldFailures().get(1).getField());
    }

    @Test
    public void whenIgnoringOnlyNestedFieldsWithIdenticalNames() {
        DocumentContext actual = JsonPath.parse(bookStoreTemplate, CONFIG);
        actual.set("$.store.book[*].price", 0.0f);
        actual.set("$.store.bicycle.price", 1.0f);

        JSONCompareResult result = compareJSON(
                bookStoreTemplate,
                actual.jsonString(),
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // ignore the non-matching of the price of all the books, (but not that of the bicycle)
                        JSONPathCustomization.ofIgnore("$.store.book..price"))
        );
        assertTrue(result.getMessage(), result.failed());
        assertEquals(1, result.getFieldFailures().size());
        assertEquals("store.bicycle.price", result.getFieldFailures().get(0).getField());
    }

    @Test
    public void whenJsonPathHasMathThenMatchingWorks() {
        DocumentContext actual = JsonPath.parse(bookStoreTemplate, CONFIG);
        actual.set("$..price", 0.1f);

        JSONCompareResult result = compareJSON(
                bookStoreTemplate,
                actual.jsonString(),
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // ignore all the prices that are <= 0.2
                        JSONPathCustomization.ofIgnore("$.store.book[?(@.price <= 0.2)].price"))
        );
        assertTrue(result.getMessage(), result.failed());
        assertEquals(1, result.getFieldFailures().size());
        assertEquals("store.bicycle.price", result.getFieldFailures().get(0).getField());
    }

    @Test
    public void whenJsonPathSelectsSubRangeThenMatchingWorks() {
        DocumentContext actual = JsonPath.parse(bookStoreTemplate, CONFIG);
        actual.set("$.store.book[0:2].author", "this should normally cause a mismatch");

        JSONCompareResult result = compareJSON(
                bookStoreTemplate,
                actual.jsonString(),
                new JSONPathComparator(JSONCompareMode.STRICT,
                        // ignore authors 0 and 1, which would otherwise be a mismatch
                        JSONPathCustomization.ofIgnore("$.store.book[0:2].author"))
        );
        assertTrue(result.getMessage(), result.passed());
    }
}
