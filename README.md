This is a hacked JSONassert
==========

skyscreamer does not support java 9+ module systems yet, we provide this hacked version of JSONassert package for java 9+ applications.
This repo is published to the Nexus-BIP public and snapshot repo.

Usage
-------

        <dependency>
            <groupId>no.ssb.org.skyscreamer</groupId>
            <artifactId>jsonassert</artifactId>
            <version>1.5.2-SNAPSHOT</version>
            <scope>test</scope>
        </dependency>

JSONassert
==========

Write JSON unit tests in less code.  Great for testing REST interfaces.

Summary
-------

Write JSON tests as if you are comparing a string.  Under the covers, JSONassert converts your string into a JSON object and compares the logical structure and data with the actual JSON.  When _strict_ is set to false (recommended), it forgives reordering data and extending results (as long as all the expected elements are there), making tests less brittle.

Supported test frameworks:

 * [JUnit](http://junit.org)

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

Error Messages
--------------

We tried to make error messages easy to understand.  This is really important, since it gets hard for the eye to pick out the difference, particularly in long JSON strings.  For example:

    String expected = "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"bird\",\"fish\"]}],pets:[]}";
    String actual = "{id:1,name:\"Joe\",friends:[{id:2,name:\"Pat\",pets:[\"dog\"]},{id:3,name:\"Sue\",pets:[\"cat\",\"fish\"]}],pets:[]}"
    JSONAssert.assertEquals(expected, actual, false);

returns the following:

    friends[id=3].pets[]: Expected bird, but not found ; friends[id=3].pets[]: Contains cat, but not expected

Which tells you that the pets array under the friend where id=3 was supposed to contain "bird", but had "cat" instead.  (Maybe the cat ate the bird?)

* * *

QuickStart
----------

To use, [download the JAR](https://github.com/skyscreamer/JSONassert/releases) or add the following to your project's pom.xml:

    <dependency>
        <groupId>org.skyscreamer</groupId>
        <artifactId>jsonassert</artifactId>
        <version>1.5.0</version>
	<scope>test</scope>
    </dependency>

Write tests like this:

<code>JSONAssert.assertEquals(<i>expectedJSONString</i>, <i>actualJSON</i>, <i>strictMode</i>);</code>

[More examples in our cookbook](http://jsonassert.skyscreamer.org/cookbook.html)

* * *

Who uses JSONassert?
--------------------
 + [yoga](https://github.com/skyscreamer/yoga) - A relational REST framework
 + [hamcrest-json](https://github.com/hertzsprung/hamcrest-json) - Hamcrest matchers for comparing JSON documents
 + [Mule ESB](http://www.mulesoft.org/)
 + [GroupDocs](http://groupdocs.com/)
 + [Shazam](http://www.shazam.com/)
 + [Thucydides](http://thucydides.net/)

* * *

org.json
--------

This implementation uses a clean-room implementation of the org.json
library implemented for the Android system, released under the Apache 2.0 license. See
[com.vaadin.external.google:android-json](http://search.maven.org/#artifactdetails%7Ccom.vaadin.external.google%7Candroid-json%7C0.0.20131108.vaadin1%7Cjar)
That jar does **not** include the org.skyscreamer.jsonassert.JSONString interface, so a new implementation of that interface is added to this source.

Resources
---------

[JavaDoc](http://jsonassert.skyscreamer.org/apidocs/index.html)

