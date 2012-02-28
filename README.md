JSONassert
==========

Write JSON unit tests faster and with less code.  Great for testing REST interfaces.

Write and maintain this:

`
    JSONObject data = getRESTData("/friends/367.json");
    String expected ="{friends:[{id:123,name:\"Corby Page\"},"
        + "{id:456,name:\"Carter Page\"}]}";
    JSONAssert.assertEquals(expected, data, false);
`

NOT this:

`
    JSONObject data = getRESTData("/friends/367.json");
    Assert.assertTrue(data.has("friends"));
    Object friendsObject = data.get("friends");
    Assert.assertTrue(friendsObject instanceof JSONArray);
    JSONArray friends = (JSONArray) friendsObject;
    Assert.assertEquals(2, data.length());
    JSONObject friend1Obj = friends.getJSONObject(data.get(0));
    Assert.true(friend1Obj.has("id"));
    Assert.true(friend1Obj.has("name"));
    JSONObject friend2Obj = friends.getJSONObject(data.get(1));
    Assert.true(friend2Obj.has("id"));
    Assert.true(friend2Obj.has("name"));
    if ("Carter Page".equals(friend1Obj.getString("name"))) {
        Assert.assertEquals(123, friend1Obj.getInt("id"));
        Assert.assertEquals("Corby Page", friend2Obj.getString("name"));
        Assert.assertEquals(456, friend2Obj.getInt("id"));
    }
    else if ("Corby Page".equals(friend1Obj.getString("name"))) {
        Assert.assertEquals(456, friend1Obj.getInt("id"));
        Assert.assertEquals("Carter Page", friend2Obj.getString("name"));
        Assert.assertEquals(123, friend2Obj.getInt("id"));
    }
    else {
        Assert.fail("Expected either Carter or Corby, Got: "
            + friend1Obj.getString("name"));
    }
`

Right?
