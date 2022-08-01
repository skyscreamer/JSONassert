package org.skyscreamer.jsonassert.comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import java.util.HashSet;
import java.util.Set;

import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.getKeys;

/*
实现的思路是，原先的代码根据不同的mode使用不同的比较器，于是我新建一个类继承原先的比较器，然后重写里面的比较方法。
当使用extend_with_null这种mode时，修改代码使用我新建的比较器。
 */
public class ExtendNullComparator extends DefaultComparator {

    public ExtendNullComparator(JSONCompareMode mode){
        super(mode);
    }

    // CS304 Issue link: https://github.com/skyscreamer/JSONassert/issues/120
    /**
     * Compare two JSON Object with mode EXTEND_WITH_NULL, only null values can be extended
     * in actual JSON Object.
     * The compare is implemented in a recursive way and if non-match happens the compare will end
     * @param prefix
     * @param expected
     * @param actual
     * @param result
     * @throws JSONException
     */
    public void compareJSON(String prefix, JSONObject expected, JSONObject actual, JSONCompareResult result) throws JSONException {
        Set<String> expectedKeys = getKeys(expected);
        for (String key : expectedKeys) {
            Object expectedValue = expected.get(key);
            if (actual.has(key)) {
                Object actualValue = actual.get(key);
                compareValues(key, expectedValue, actualValue, result);
            } else {
                result.missing("", key);
            }
        }

        Set<String> actualKeys=getKeys(actual);
        for(String key : actualKeys){
            Object actualValue =actual.get(key);
            if(!expected.has(key) && (!actualValue.toString().equals("null"))){
                result.fail("Extended actual value from key \""+key+"\" is Not Null");
                break;
            }
        }
    }


    // CS304 Issue link: https://github.com/skyscreamer/JSONassert/issues/120
    /**
     * Compare two JSON Array with mode EXTEND_WITH_NULL, only null values can be extended
     * in actual JSON Array.
     * The compare is implemented in a recursive way and if non-match happens the compare will end
     * @param prefix
     * @param expected
     * @param actual
     * @param result
     * @throws JSONException
     */
    public void compareJSONArray(String prefix, JSONArray expected, JSONArray actual, JSONCompareResult result) throws JSONException {
        Set<Integer> matched = new HashSet<Integer>();
        for (int i = 0; i < expected.length(); ++i) {
            Object expectedElement = JSONCompareUtil.getObjectOrNull(expected, i);
            boolean matchFound = false;
            for (int j = 0; j < actual.length(); ++j) {
                Object actualElement = JSONCompareUtil.getObjectOrNull(actual, j);
                if ((expectedElement == null && actualElement != null) || (expectedElement != null && actualElement == null)) {
                    continue;
                }
                if (matched.contains(j) || !actualElement.getClass().equals(expectedElement.getClass())) {
                    continue;
                }
                if (expectedElement instanceof JSONObject) {
                    if (compareJSON((JSONObject) expectedElement, (JSONObject) actualElement).passed()) {
                        matched.add(j);
                        matchFound = true;
                        break;
                    }
                } else if (expectedElement instanceof JSONArray) {
                    if (compareJSON((JSONArray) expectedElement, (JSONArray) actualElement).passed()) {
                        matched.add(j);
                        matchFound = true;
                        break;
                    }
                } else if (expectedElement.equals(actualElement)) {
                    matched.add(j);
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
                result.fail("[" + i + "] Could not find match for element " + expectedElement);
                return;
            }
        }

        for(int i = 0; i < actual.length(); ++i){
            if(!matched.contains(i) && (!actual.get(i).toString().equals("null"))){
                result.fail("Extended actual value "+actual.get(i).toString()+" is Not Null");
                break;
            }
        }
    }

}
