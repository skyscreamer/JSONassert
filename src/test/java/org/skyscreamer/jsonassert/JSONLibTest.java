package org.skyscreamer.jsonassert;

import org.json.JSONException;
import org.junit.Test;
import com.alibaba.fastjson.*;
import com.google.gson.*;
import org.json.*;

// CS304 Issue Link: https://github.com/skyscreamer/JSONassert/issues/89
// test if the three lib of json can be used
public class JSONLibTest {

    @Test
    public void fastJsonTest()throws com.alibaba.fastjson.JSONException {
        String json = "{\"id\":\"abc\"}";
        JSON.parse(json);
    }

    @Test
    public void gsonTest(){
        String json = "{\"id\":\"abc\"}";
        new com.google.gson.JsonParser().parse(json).getAsJsonObject();
    }

    @Test
    public void androidJsonTest()throws org.json.JSONException {
        String json = "{\"id\":\"abc\"}";
        JSONParser.parseJSON(json);
    }
}
