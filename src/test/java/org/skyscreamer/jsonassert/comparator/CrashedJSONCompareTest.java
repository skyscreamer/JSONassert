package org.skyscreamer.jsonassert.comparator;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

import static org.junit.Assert.assertTrue;
import static org.skyscreamer.jsonassert.JSONCompare.compareJSON;

//CS304 Issue link:https://github.com/skyscreamer/JSONassert/issues/103
public class CrashedJSONCompareTest {
    @Test
    public void shouldJudgeValidJson() throws JSONException {
        String validJson = "{\"id\":\"abc\"}";
        String crashedJson1 = "{\"id\":'abc'}";
        String crashedJson2 = "{\"id\":abc}";
        assertTrue(JSONCompare.isJSON(validJson));
        assertTrue(JSONCompare.isJSON(crashedJson1));
        assertTrue(!JSONCompare.isJSON(crashedJson2));
    }
    @Test
    public void shouldFailOnCrashedJson() throws JSONException{
        String validJson = "{\"id\":\"abc\"}";
        String crashedJson = "{\"id\":abc}";
        JSONCompareResult jsonCompareResult = compareJSON(validJson,
                crashedJson, JSONCompareMode.STRICT);
        assertTrue(jsonCompareResult.failed());
    }
}
