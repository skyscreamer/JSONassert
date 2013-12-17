package org.skyscreamer.jsonassert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * Simple JSON parsing utility.
 */
public class JSONParser {
    private static final String NUMBER_REGEX = "-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?";

    private JSONParser() {}

    /**
     * Takes a JSON string and returns either a {@link org.json.JSONObject} or {@link org.json.JSONArray},
     * depending on whether the string represents an object or an array.
     *
     * @param s Raw JSON string to be parsed
     * @return JSONObject or JSONArray
     * @throws JSONException
     */
    public static Object parseJSON(final String s) throws JSONException {
        if (s.trim().startsWith("{")) {
            return new JSONObject(s);
        }
        else if (s.trim().startsWith("[")) {
            return new JSONArray(s);
        } else if (s.trim().startsWith("\"")
                   || s.trim().matches(NUMBER_REGEX)) {
          return new JSONString() {
            @Override
            public String toJSONString() {
              return s;
            }
          };
        }
        throw new JSONException("Unparsable JSON string: " + s);
    }
}
