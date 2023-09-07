package org.skyscreamer.jsonassert;


import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

public class JSONArrayObjectsTest {

  public static final UUID RANDOM_ID_1 = UUID.randomUUID();
  public static final UUID RANDOM_ID_2 = UUID.randomUUID();
  public static final String ARRAY_NAME = "arrayName";
  public static final String ID = "id";
  public static final String DATE = "date";

  @Test
  public void testJsonArrayWithIgnoredFieldAndProvidedIdField() throws JSONException {

    JSONArray jsonArray1 = getJsonArray(RANDOM_ID_1, RANDOM_ID_2);
    JSONArray jsonArray2 = getJsonArray(RANDOM_ID_2, RANDOM_ID_1);

    JSONObject jsonObject1 = new JSONObject();
    jsonObject1.put(ARRAY_NAME, jsonArray1);

    JSONObject jsonObject2 = new JSONObject();
    jsonObject2.put(ARRAY_NAME, jsonArray2);

    CustomComparator customComparator = new CustomComparator(
        JSONCompareMode.LENIENT,
        new Customization("**." + DATE, getDummyMatcher()), //ignoring this field
        new Customization(ARRAY_NAME, ID)
    );

    JSONAssert.assertEquals(jsonObject1, jsonObject2, customComparator);
  }

  private static ValueMatcher<Object> getDummyMatcher() {
    return new ValueMatcher<Object>() {
      @Override
      public boolean equal(Object o1, Object o2) {
        return true;
      }
    };
  }

  private static JSONArray getJsonArray(UUID randomId2, UUID randomId1) throws JSONException {
    JSONArray jsonArray1 = new JSONArray();

    JSONObject jsonObject1 = new JSONObject();
    jsonObject1.put(ID, randomId2);
    jsonObject1.put(DATE, UUID.randomUUID());

    JSONObject jsonObject2 = new JSONObject();
    jsonObject2.put(ID, randomId1);
    jsonObject2.put(DATE, UUID.randomUUID());

    jsonArray1.put(jsonObject1);
    jsonArray1.put(jsonObject2);
    return jsonArray1;
  }
}