package org.skyscreamer.jsonassert;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;



public class issue109Test {


    /**
     * intend to fix issue 109
     * CS304 Issue {@link: https://github.com/skyscreamer/JSONassert/issues/109}
     * Some users need to compare two JSONObject by ignoring some fields,
     * Could use JSONCompareMode.STRICT and new Customization to achieve this goal.
     * @Time 2021.5.13 Tingyan Feng
     */
    @Test
    public void test1() throws JSONException {
        JSONObject json1 = new JSONObject("{" +
                "\t\"level00\": {\n" +
                "\t\t\"level01\": [{\n" +
                "\t\t\t\"id\": \"11111111-1111-1111-1111-111111111111\",\n" +
                "\t\t\t\"name\": \"Name01\",\n" +
                "\t\t\t\"createdAt\": \"2018-01-01T11:11:11.111Z\",\n" +
                "\t\t\t\"updatedAt\": \"2018-01-01T11:11:11.111Z\",\n" +
                "\t\t\t\"clientApplication\": \"AAAAA\",\n" +
                "\t\t\t\"version\": 1,\n" +
                "\t\t\t\"options\": null\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": \"22222222-2222-2222-2222-222222222222\",\n" +
                "\t\t\t\"name\": \"Name02\",\n" +
                "\t\t\t\"createdAt\": \"2018-01-01T11:22:22.222Z\",\n" +
                "\t\t\t\"updatedAt\": \"2018-01-01T11:22:22.222Z\",\n" +
                "\t\t\t\"clientApplication\": \"AAAAA\",\n" +
                "\t\t\t\"version\": 1,\n" +
                "\t\t\t\"options\": null\n" +
                "\t\t}]\n" +
                "\t}\n" +
                "}");
        JSONObject json2 = new JSONObject("{\n" +
                "\t\"level00\": {\n" +
                "\t\t\"level01\": [{\n" +
                "\t\t\t\"id\": \"11111111-1111-1111-1111-111111111111\",\n" +
                "\t\t\t\"name\": \"Name01\",\n" +
                "\t\t\t\"createdAt\": \"2018-01-01T11:33:33.333Z\",\n" +
                "\t\t\t\"updatedAt\": \"2018-01-01T11:33:33.333Z\",\n" +
                "\t\t\t\"clientApplication\": \"AAAAA\",\n" +
                "\t\t\t\"version\": 1,\n" +
                "\t\t\t\"options\": null\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": \"22222222-2222-2222-2222-222222222222\",\n" +
                "\t\t\t\"name\": \"Name02\",\n" +
                "\t\t\t\"createdAt\": \"2018-01-01T11:44:44.444Z\",\n" +
                "\t\t\t\"updatedAt\": \"2018-01-01T11:44:44.444Z\",\n" +
                "\t\t\t\"clientApplication\": \"AAAAA\",\n" +
                "\t\t\t\"version\": 1,\n" +
                "\t\t\t\"options\": null\n" +
                "\t\t}]\n" +
                "\t}\n" +
                "}");


        JSONAssert.assertEquals(json1, json2,
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("**.updatedAt", (o1, o2) -> true),
                        new Customization("**.createdAt", (o1, o2) -> true)
                ));
    }

    @Test
    public void test2() throws JSONException {
        JSONObject json1 = new JSONObject("{" +
                "\t\"level00\": {\n" +
                "\t\t\"level01\": [{\n" +
                "\t\t\t\"id\": \"11111111-1111-1111-1111-111111111111\",\n" +
                "\t\t\t\"name\": \"Name01\",\n" +
                "\t\t\t\"createdAt\": \"2018-01-01T11:11:11.111Z\",\n" +
                "\t\t\t\"updatedAt\": \"2018-01-01T11:11:11.111Z\",\n" +
                "\t\t\t\"clientApplication\": \"AAAAA\",\n" +
                "\t\t\t\"version\": 1,\n" +
                "\t\t\t\"options\": null\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": \"22222222-2222-2222-2222-222222222222\",\n" +
                "\t\t\t\"name\": \"Name02\",\n" +
                "\t\t\t\"createdAt\": \"2018-01-01T11:22:22.222Z\",\n" +
                "\t\t\t\"updatedAt\": \"2018-01-01T11:22:22.222Z\",\n" +
                "\t\t\t\"clientApplication\": \"AAAAA\",\n" +
                "\t\t\t\"version\": 1,\n" +
                "\t\t\t\"options\": null\n" +
                "\t\t}]\n" +
                "\t}\n" +
                "}");
        JSONObject json2 = new JSONObject("{\n" +
                "\t\"level00\": {\n" +
                "\t\t\"level01\": [{\n" +
                "\t\t\t\"id\": \"11111111-1111-1111-1111-0\",\n" +
                "\t\t\t\"name\": \"Name02\",\n" +
                "\t\t\t\"createdAt\": \"2018-01-01T11:11:11.111Z\",\n" +
                "\t\t\t\"updatedAt\": \"2018-01-01T11:11:11.111Z\",\n" +
                "\t\t\t\"clientApplication\": \"AAAAA\",\n" +
                "\t\t\t\"version\": 1,\n" +
                "\t\t\t\"options\": null\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"id\": \"22222222-2222-2222-2222-222222222222\",\n" +
                "\t\t\t\"name\": \"Name02\",\n" +
                "\t\t\t\"createdAt\": \"2018-01-01T11:22:22.222Z\",\n" +
                "\t\t\t\"updatedAt\": \"2018-01-01T11:22:22.222Z\",\n" +
                "\t\t\t\"clientApplication\": \"AAAAA\",\n" +
                "\t\t\t\"version\": 1,\n" +
                "\t\t\t\"options\": null\n" +
                "\t\t}]\n" +
                "\t}\n" +
                "}");


        JSONAssert.assertEquals(json1, json2,
                new CustomComparator(JSONCompareMode.STRICT,
                        new Customization("**.id", (o1, o2) -> true),
                        new Customization("**.name", (o1, o2) -> true)
                ));
    }
}
