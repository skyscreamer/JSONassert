JSONassert
==========

Write JSON unit tests faster and with less code.  Great for testing REST interfaces.

It works like you expect.  The strictness of the checking is tunable.  Get understandable results back.

Write and maintain this:

    JSONObject data = getRESTData("/friends/367.json");
    String expected = "{friends:[{id:123,name:\"Corby Page\"},{id:456,name:\"Carter Page\"}]}";
    JSONAssert.assertEquals(expected, data, false);

NOT this (ouch!):

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
        Assert.fail("Expected either Carter or Corby, Got: " + friend1Obj.getString("name"));
    }

* * *

QuickStart
----------

Add the following to your project's pom.xml:

    <dependency>
        <groupId>org.skyscreamer</groupId>
        <artifactId>jsonassert</artifactId>
        <version>0.9.0</version>
    </dependency>

And use JSONAssert.assertEquals just like you'd used Assert.assertEquals in existing JUnit test cases.  That's it.

Behind the scenes, JSONAssert, converts your "expected" string to a JSON object, and compares that to the result you want to test.  It performs a logical comparison -- much like the don't-do-this example above, but a lot cleaner.  On test failure, the result messages are very specific and should simplify troubleshooting.


* * *

Resources
---------

[JavaDoc](http://skyscreamer.org/JSONassert/javadoc/)

