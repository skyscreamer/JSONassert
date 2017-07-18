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

package org.json;

/**
 * The JSONString interface allows a toJSONString() method so that a class can change
 * the behavior of JSONObject.toString(), JSONArray.toString(), and JSONWriter.value(Object). 
 * The toJSONString method will be used instead of the default behavior of using the 
 * Object's toString() method and quoting the result.
 *
 * @author sasdjb
 *
 */
public interface JSONString {

    /**
     * The toJSONString method allows a class to produce its own JSON 
     * serialization.
     *
     * @return String representation of JSON object
     * */
    String toJSONString();

}
