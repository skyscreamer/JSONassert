package org.skyscreamer.jsonassert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple JSON parsing utility.
 */
public class JSONParser {
    private JSONParser() {}

    /**
     * Takes a JSON string and returns either a {@link org.json.JSONObject} or {@link org.json.JSONArray},
     * depending on whether the string represents an object or an array.
     *
     * @param s Raw JSON string to be parsed
     * @return JSONObject or JSONArray
     * @throws JSONException
     */
    public static Object parseJSON(String s) throws JSONException {
        if (s.trim().startsWith("{")) {
            return new JSONObject(s);
        }
        else if (s.trim().startsWith("[")) {
            return new JSONArray(s);
        }
        throw new JSONException("Unparsable JSON string: " + s);
    }
}
