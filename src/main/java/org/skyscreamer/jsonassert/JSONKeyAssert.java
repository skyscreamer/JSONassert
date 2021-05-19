package org.skyscreamer.jsonassert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

import java.util.Iterator;

/**
 * Class SpecialCompareChange
 * Use to handle the comparation ignore the data compare
 *
 * @author Maijie Xie
 * @version 1.0
 * Created on 8/5/2021
 */
public class JSONKeyAssert {
    private JSONKeyAssert() {
    }
    /**
     * Asserts that the JSONArray provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param strict Enables strict checking
     * @throws JSONException JSON parsing error
     */
    public static void assertEquals(String expectedStr, String actualStr, boolean strict)
            throws JSONException {
        expectedStr=removeValue(JSONParser.parseJSON(expectedStr)).toString();
        actualStr  =removeValue(JSONParser.parseJSON(actualStr)).toString();
        JSONAssert.assertEquals("", expectedStr, actualStr, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }
    /**
     * Asserts that the JSONArray provided matches the expected string.  If it isn't it throws an
     * {@link AssertionError}.
     *
     * @param expectedStr Expected JSON string
     * @param actualStr String to compare
     * @param strict Enables strict checking
     * @throws JSONException JSON parsing error
     */
    public static void assertNotEquals(String expectedStr, String actualStr, boolean strict)
            throws JSONException {
        expectedStr= removeValue(JSONParser.parseJSON(expectedStr)).toString();
        actualStr  =removeValue(JSONParser.parseJSON(actualStr)).toString();
        JSONAssert.assertNotEquals(expectedStr, actualStr, strict ? JSONCompareMode.STRICT : JSONCompareMode.LENIENT);
    }
    /**
     * Remove JSONObject's data
     * @param item Expected JSONObject
     * @throws JSONException JSON parsing error
     */
    private static JSONObject removeValue(JSONObject item) throws JSONException {
        JSONObject Temp=new JSONObject();
        Iterator iterator = item.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Temp.put(key, 0);
        }
        return Temp;

    }
    /**
     * Remove Object's data
     * @param item Expected Object
     * @throws JSONException JSON parsing error
     */
    private static Object removeValue(Object item) throws JSONException{
        if(item instanceof JSONArray){
            return removeValue((JSONArray) item);
        }
        if(item instanceof JSONObject){
            return removeValue((JSONObject)item);
        }
        return null;

    }

    /**
     * Remove JSONArray's data
     * @param item Expected JSONArray
     * @throws JSONException JSON parsing error
     */
    private static JSONArray removeValue(JSONArray item) throws JSONException {
        if (item.length() > 0) {
            for (int i = 0; i < item.length(); i++) {
                JSONObject job = item.getJSONObject(i);
                Iterator iterator = job.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    job.put(key, 0);
                }
            }
        }
        return  item;
    }
}
