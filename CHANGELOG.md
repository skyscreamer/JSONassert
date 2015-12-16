Changelog
=========

Version 1.3.0 - 12/16/2015
--------------------------
 - Fix & improve ArrayValueMatcher JavaDoc (dmackinder)
     Fix final JavaDoc example and add new example showing how to verify
     every array element using a custom comparator
 - Fix URL in pom.xml (aukevanleeuwen)
 - Update JSONCompareResult.java adding 2 new lists for missing and unexpected fileds (riccorazza)
 - Includes missing imports in test class (javierseixas)

Version 1.2.3 - 2/5/2014
------------------------
 - This edition brought to you by dmackinder (thanks!)
 - Added array size comparator enhancements.
 - Added ArrayValueMatcher to simplify verification of range of array elements.
 - Improve diagnostics from RegularExpressionValueMatcher.
 - Deprecated former Customization.matches() signature

Version 1.2.2 - 12/31/2013
--------------------------
 - Add support for JSONString

Version 1.2.1 - 10/24/2013
--------------------------
 - Remove commons-collection dependency
 - Updated Customization class to allow path-matching, and matching of expected and actual values with user-provided EqualityComparator.
 - Added AssertNotEquals

Version 1.2.0 - 3/17/2013
-------------------------
 - Fixed handling comparison of equivalent values across long, int, and double
 - Add JSONCompareMode to asserts to allow for more options than strict/not-strict
 - Added hooks to override/extend comparison behavior via JSONComparator

Version 1.1.1 - 10/15/2012
--------------------------
 - Return diagnostics (instead of throwing an exception) when comparing JSONObject and JSONArray
 - Expose field comparison results as a list in JSONCompareResult
 - Fix bug where actual JSON array doesn't contain JSONObjects with unique keys
 - Improve diagnostics
 - Unify some diagnostics
 - Fix handling of arrays of booleans

Version 1.1.0 - 9/3/2012
------------------------
 - Added withStrictOrdering() and withExtensible() to JSONCompareMode
 - Javadoc fixes
 - Fix bug where expected and actual were reversed
 - Fix bug where nulls gave false positives in some cases
 - Simplified publishing Javadocs

Version 1.0.0 - 4/5/2012
------------------------
Initial release!

