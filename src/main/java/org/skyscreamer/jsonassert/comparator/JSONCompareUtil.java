package org.skyscreamer.jsonassert.comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Utility class that contains Json manipulation methods
 */
public final class JSONCompareUtil {
    private static Integer INTEGER_ONE = new Integer(1);

    private JSONCompareUtil() {}

    /*public static Map<Object,JSONObject> orig-arrayOfJsonObjectToMap(JSONArray array, String uniqueKey) throws JSONException {
        Map<Object, JSONObject> valueMap = new HashMap<Object, JSONObject>();
        for(int i = 0 ; i < array.length() ; ++i) {
            JSONObject jsonObject = (JSONObject)array.get(i);
            Object id = jsonObject.get(uniqueKey);
            valueMap.put(id, jsonObject);
        }
        return valueMap;
    }*/
    /*  public static Map<Object,Object> old-arrayOfJsonObjectToMap(JSONArray array, String uniqueKey) throws JSONException {
        Map<Object, Object> valueMap = new HashMap<Object, Object>();
        for(int i = 0 ; i < array.length() ; ++i) {
            JSONObject jsonObject = (JSONObject)array.get(i);
            // iterate thru the possible fields in the array elements themselves
            // this is for arrays of structures
            Iterator k =jsonObject.keys();
            while(k.hasNext()) {
                // get the next object in the object of the array element
            	Object name =  k.next();
                // get that objects value
            	Object value =  jsonObject.get((String) name);
                // hash the 'name' of the field and its 'value'
            	valueMap.put(name, value);
            }
        }
        return valueMap;
    }*/
    
        public static Map<Object,Object> JsonObjectToMap(JSONObject object) throws JSONException {
        Map<Object, Object> valueMap = new HashMap<Object, Object>();
        // iterate thru the possible fields in the json object
        Iterator k =object.keys();
        while(k.hasNext()) {
            // get the next object in the object of the array element
            Object name =  k.next();
            // get that objects value
            Object value =  object.get((String) name);
            // hash the 'name' of the field and its 'value'
            valueMap.put(name, value);
        }
        return valueMap;
    }
    /* dead code with unique key 
    public static Map<Object,Object> arrayOfJsonObjectToMap(JSONObject object, String uniqueKey) throws JSONException {
        Map<Object, Object> valueMap = new HashMap<Object, Object>();
        // iterate thru the possible fields in the json object
        Iterator k =object.keys();
        while(k.hasNext()) {
            // get the next object in the object of the array element
            Object name =  k.next();
            // get that objects value
            Object value =  object.get((String) name);
            // hash the 'name' of the field and its 'value'
            valueMap.put(name, value);
        }
        return valueMap;
    } */


    public static String findUniqueKey(JSONArray expected) throws JSONException {
        // Find a unique key for the object (id, name, whatever)
        JSONObject o = (JSONObject)expected.get(0); // There's at least one at this point
        for(String candidate : getKeys(o)) {
            if (isUsableAsUniqueKey(candidate, expected)) return candidate;
        }
        // No usable unique key :-(
        return null;
    }

    /**
     * {@code candidate} is usable as a unique key if every element in the
     * {@code array} is a JSONObject having that key, and no two values are the same.
     */
    public static boolean isUsableAsUniqueKey(String candidate, JSONArray array) throws JSONException {
        Set<Object> seenValues = new HashSet<Object>();
        for (int i = 0 ; i < array.length() ; i++) {
            Object item = array.get(i);
            if (item instanceof JSONObject) {
                JSONObject o = (JSONObject) item;
                if (o.has(candidate)) {
                    Object value = o.get(candidate);
                    if (isSimpleValue(value) && !seenValues.contains(value)) {
                        seenValues.add(value);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public static List<Object> jsonArrayToList(JSONArray expected) throws JSONException {
        List<Object> jsonObjects = new ArrayList<Object>(expected.length());
        for(int i = 0 ; i < expected.length() ; ++i) {
            jsonObjects.add(expected.get(i));
        }
        return jsonObjects;
    }

    public static boolean allSimpleValues(JSONArray array) throws JSONException {
        for(int i = 0 ; i < array.length() ; ++i) {
            if (!isSimpleValue(array.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSimpleValue(Object o) {
        return !(o instanceof JSONObject) && !(o instanceof JSONArray);
    }

    public static boolean allJSONObjects(JSONArray array) throws JSONException {
        for(int i = 0 ; i < array.length() ; ++i) {
            if (!(array.get(i) instanceof JSONObject)) {
                return false;
            }
        }
        return true;
    }

    public static boolean allJSONArrays(JSONArray array) throws JSONException {
        for(int i = 0 ; i < array.length() ; ++i) {
            if (!(array.get(i) instanceof JSONArray)) {
                return false;
            }
        }
        return true;
    }

    public static Set<String> getKeys(JSONObject jsonObject) {
        Set<String> keys = new TreeSet<String>();
        Iterator<?> iter = jsonObject.keys();
        while(iter.hasNext()) {
            keys.add((String)iter.next());
        }
        return keys;
    }

    public static String qualify(String prefix, String key) {
        return "".equals(prefix) ? key : prefix + "." + key;
    }

    public static String formatUniqueKey(String key, String uniqueKey, Object value) {
        return key + "[" + uniqueKey + "=" + value + "]";
    }

    public static <T> Map<T, Integer> getCardinalityMap(final Collection<T> coll) {
        Map count = new HashMap<T, Integer>();
        for (T item : coll) {
            Integer c = (Integer) (count.get(item));
            if (c == null) {
                count.put(item, INTEGER_ONE);
            } else {
                count.put(item, new Integer(c.intValue() + 1));
            }
        }
        return count;
    }
}
