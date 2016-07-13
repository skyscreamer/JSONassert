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
     * */
    public String toJSONString();

}
