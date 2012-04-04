JSONassert
==========

Write JSON unit tests in less code.  Great for testing REST interfaces.

Summary
-------

Write JSON tests as if you are comparing a string.  Under the covers, JSONassert converts your string into a JSON object and compares the logical structure and data with the actual JSON.  When _strict_ is set to false (recommended), it forgives reordering data and extending results (as long as all the expected elements are there), making tests less brittle.

Examples
--------

In JSONassert you write and maintain something like this:

    JSONObject data = getRESTData("/friends/367.json");
    String expected = "{friends:[{id:123,name:\"Corby Page\"},{id:456,name:\"Carter Page\"}]}";
    JSONAssert.assertEquals(expected, data, false);

instead of all this:

<pre><code><del>
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
</del></code></pre>

The more complicated your test, the bigger the benefit from JSONassert.

We tried to make error messages easy to understand.  This is really important, since it gets hard for the eye to pick out the difference, particularly in long JSON strings.  For example:

    String expected = "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}";
    String actual = "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}"
    JSONAssert.assertEquals(expected, actual, false);

returns the following:

    friends[id=3].pets[]: Expected bird, but not found ; friends[id=3].pets[]: Contains cat, but not expected

Which tells you that the pets array under friend with id=3 was supposed to have a bird, but had a cat instead.  (Maybe the cat ate the bird?)

* * *

QuickStart
----------

To use, [download the JAR](https://github.com/skyscreamer/JSONassert/downloads) or add the following to your project's pom.xml:

    <dependency>
        <groupId>org.skyscreamer</groupId>
        <artifactId>jsonassert</artifactId>
        <version>0.9.0</version>
    </dependency>

And use JSONAssert.assertEquals just like you'd used Assert.assertEquals in existing JUnit test cases.  That's it.

Behind the scenes, JSONAssert, converts your "expected" string to a JSON object, and compares that to the result you want to test.  It performs a logical comparison -- much like the don't-do-this example above, but a lot cleaner.  On test failure, the result messages are very specific and should simplify troubleshooting.


* * *

Who uses JSONassert?
--------------------
 + [yoga](https://github.com/skyscreamer/yoga) - A relational REST framework

* * *

Resources
---------

[JavaDoc](http://skyscreamer.org/JSONassert/javadoc/)

