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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

import java.util.ArrayList;
import java.util.Set;

import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.getKeys;

/**
 * Provides API to compare two JSON entities.  This is the backend to {@link JSONAssert}, but it can
 * be programmed against directly to access the functionality.  (eg, to make something that works with a
 * non-JUnit test framework)
 */
public final class JSONCompare {
    private JSONCompare() {
    }
    /**
     * Replace the original String, add "\"" to both size of the values in expectedStr.
     * Check if the original String should be replaced, if not, return "", else return the replaced String.
     * @param expectedStr Strings that need to be replaced.
     * @param expected jsonObject that was parsed by expectedStr.
     * @return the replaced String.
     * @throws JSONException JSON parsing error.
     */
    public static String repalceString(String expectedStr,Object expected) throws JSONException{
        Set<String> re = getKeys((JSONObject) expected);
        Object expectedValue;
        String result = "";
        ArrayList<String> name=new ArrayList<String>();
        ArrayList<String> sval=new ArrayList<String>();  // value in String format;
        for(String r:re){
            expectedValue = ((JSONObject) expected).get(r);
            if(expectedValue instanceof Double){
                getPair(expectedStr,name,sval);
                for(int i=0;i<name.size();i++){
                    if(!String.valueOf(expectedValue).equals(sval.get(i)))
                        result = expectedStr.replace(sval.get(i),"\""+sval.get(i)+"\"");
                }
            }
        }
        return result;
    }
    /**
     * Split the input json(expectedStr) in String format. Get the output names and values
     * store them to ArrayList name and ArrayList sval.
     * @param expectedStr Strings that need to be splited.
     * @param name The names in the expectedStr
     * @param sval The values in the expectedStr
     */
    public static void getPair(String expectedStr, ArrayList<String> name,ArrayList<String> sval){
        int POS = 2;
        while(expectedStr.charAt(0)!='['){
            while(expectedStr.charAt(POS-1)==' '){
                POS++;
            }
            if(expectedStr.charAt(POS-1)!='"') break;
            POS++;
            StringBuffer buf = new StringBuffer();
            while(expectedStr.charAt(POS-1)!='"'){
                buf.append(expectedStr.charAt(POS-1));
                POS++;
            }
            String n = buf.toString();
            POS++;
            name.add(n);
            //   Object val = ((JSONObject) expected).get(n);
//            if(!(val instanceof Double)) continue;
            while(expectedStr.charAt(POS-1)==' '){
                POS++;
            }
            if(expectedStr.charAt(POS-1)!=':') break;
            POS++;
            int j = expectedStr.charAt(POS-1);
            while(expectedStr.charAt(POS-1)==' '){
                POS++;
            }
            StringBuffer buf2 = new StringBuffer();
            while(expectedStr.charAt(POS-1)!=' '&&expectedStr.charAt(POS-1)!='}'){
                buf2.append(expectedStr.charAt(POS-1));
                POS++;
            }
            String v = buf2.toString();
            sval.add(v);
            if(expectedStr.charAt(POS-1)=='}'){
                break;
            }
        }
    }

    private static JSONComparator getComparatorForMode(JSONCompareMode mode) {
        return new DefaultComparator(mode);
    }

    /**
     * Compares JSON string provided to the expected JSON string using provided comparator, and returns the results of
     * the comparison.
     * @param expectedStr Expected JSON string
     * @param actualStr JSON string to compare
     * @param comparator Comparator to use
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     * @throws IllegalArgumentException when type of expectedStr doesn't match the type of actualStr
     */
    public static JSONCompareResult compareJSON(String expectedStr, String actualStr, JSONComparator comparator)
            throws JSONException {
        Object expected = JSONParser.parseJSON(expectedStr);
        Object actual = JSONParser.parseJSON(actualStr);
        int in1 = expectedStr.indexOf("[");
        int in2 = actualStr.indexOf("[");
        int in3 = expectedStr.indexOf("{");
        int in4 = actualStr.indexOf("{");
        if(in1==-1&&in2==-1&&in3!=-1&&in4!=-1){
            String reexpectedStr = repalceString(expectedStr,expected);
            String reactualStr = repalceString(actualStr,actual);
            if(!reexpectedStr.equals(expectedStr)&&!actualStr.equals(reactualStr)&&!reexpectedStr.equals("")&&!reactualStr.equals("")){
                expected = JSONParser.parseJSON(reexpectedStr);
                actual = JSONParser.parseJSON(reactualStr);
            }
        }
        if ((expected instanceof JSONObject) && (actual instanceof JSONObject)) {
            return compareJSON((JSONObject) expected, (JSONObject) actual, comparator);
        }
        else if ((expected instanceof JSONArray) && (actual instanceof JSONArray)) {
            return compareJSON((JSONArray)expected, (JSONArray)actual, comparator);
        }
        else if (expected instanceof JSONString && actual instanceof JSONString) {
            return compareJson((JSONString) expected, (JSONString) actual);
        }
        else if (expected instanceof JSONObject) {
            return new JSONCompareResult().fail("", expected, actual);
        }
        else {
            return new JSONCompareResult().fail("", expected, actual);
        }
    }

  /**
     * Compares JSON object provided to the expected JSON object using provided comparator, and returns the results of
     * the comparison.
     * @param expected expected json object
     * @param actual actual json object
     * @param comparator comparator to use
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     */
    public static JSONCompareResult compareJSON(JSONObject expected, JSONObject actual, JSONComparator comparator)
            throws JSONException {
        return comparator.compareJSON(expected, actual);
    }

    /**
     * Compares JSON object provided to the expected JSON object using provided comparator, and returns the results of
     * the comparison.
     * @param expected expected json array
     * @param actual actual json array
     * @param comparator comparator to use
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     */
    public static JSONCompareResult compareJSON(JSONArray expected, JSONArray actual, JSONComparator comparator)
            throws JSONException {
        return comparator.compareJSON(expected, actual);
    }

    /**
     * Compares {@link JSONString} provided to the expected {@code JSONString}, checking that the
     * {@link org.json.JSONString#toJSONString()} are equal.
     *
     * @param expected Expected {@code JSONstring}
     * @param actual   {@code JSONstring} to compare
     * @return result of the comparison
     */
    public static JSONCompareResult compareJson(final JSONString expected, final JSONString actual) {
        final JSONCompareResult result = new JSONCompareResult();
        final String expectedJson = expected.toJSONString();
        final String actualJson = actual.toJSONString();
        if (!expectedJson.equals(actualJson)) {
          result.fail("");
        }
        return result;
    }

    /**
     * Compares JSON string provided to the expected JSON string, and returns the results of the comparison.
     *
     * @param expectedStr Expected JSON string
     * @param actualStr   JSON string to compare
     * @param mode        Defines comparison behavior
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     */
    public static JSONCompareResult compareJSON(String expectedStr, String actualStr, JSONCompareMode mode)
            throws JSONException {
        return compareJSON(expectedStr, actualStr, getComparatorForMode(mode));
    }

    /**
     * Compares JSONObject provided to the expected JSONObject, and returns the results of the comparison.
     *
     * @param expected Expected JSONObject
     * @param actual   JSONObject to compare
     * @param mode     Defines comparison behavior
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     */
    public static JSONCompareResult compareJSON(JSONObject expected, JSONObject actual, JSONCompareMode mode)
            throws JSONException {
        return compareJSON(expected, actual, getComparatorForMode(mode));
    }


    /**
     * Compares JSONArray provided to the expected JSONArray, and returns the results of the comparison.
     *
     * @param expected Expected JSONArray
     * @param actual   JSONArray to compare
     * @param mode     Defines comparison behavior
     * @return result of the comparison
     * @throws JSONException JSON parsing error
     */
    public static JSONCompareResult compareJSON(JSONArray expected, JSONArray actual, JSONCompareMode mode)
            throws JSONException {
        return compareJSON(expected, actual, getComparatorForMode(mode));
    }

}
