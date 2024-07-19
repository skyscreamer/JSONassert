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

/**
 * Simple JSON parsing utility.
 */
public class JSONParser {
    // regular expression to match a number in JSON format.  see http://www.json.org/fatfree.html.
    // "A number can be represented as integer, real, or floating point. JSON does not support octal or hex
    // ... [or] NaN or Infinity".
    private static final String NUMBER_REGEX = "-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?";

    private JSONParser() {}

    /**
     * Takes a JSON string and returns either a {@link org.json.JSONObject} or {@link org.json.JSONArray},
     * depending on whether the string represents an object or an array.
     *
     * @param s Raw JSON string to be parsed
     * @return JSONObject or JSONArray
     */
    public static Object parseJSON(final String s) {
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
