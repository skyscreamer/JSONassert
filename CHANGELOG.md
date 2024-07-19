Changelog
=========

Version 2.0.0 - TBD
-------------------
 - TODO - placeholder

Version 1.5.3 - 6/28/2024
-------------------------
 - Revert Java release version from 21 to 8 due to breaking older compilers.

Version 1.5.2 - 6/14/2024
-------------------------
 - Fix CVE-2020-15250 JUnit vulnerability (https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2020-15250). Bump 
   dependencies.
 - Add gitIgnore file
 - README syntax error fix
 - Accidentally upgraded release to Java version 21

Version 1.5.1 - 7/4/2022
------------------------
Going to try to catch up on some ancient PRs, mainly around security and cleanup. Starting with accepted PRs that
didn't get released yet. To be followed hopefully shortly with another release.
 - Added convenience methods for JSONObject comparison using a custom JSONComparator (thanks jakob-o@!)
 - Fix issue #105: Issue when comparing JSONArray if any value is null (thanks suraj1291993@!)
 - Fixes security vulnerability associated with older version of junit

Version 1.5.0 - 3/19/2017
-------------------------
 - JSONassert now supports user-supplied error messages (thanks yasin3061@!)
 - Some refactoring / code health cleanup (thanks picimako@!)
 - License headers on individual files
 - Java 8 friendly javadocs

Version 1.4.0 - 10/30/2016
--------------------------
 - Change the implementation for org.json to one with a more open license
 - Fix null pointer exception (issue #48)
 - Support wildcards in Customization.path

Version 1.3.0 - 12/16/2015
--------------------------
 - Fix & improve ArrayValueMatcher JavaDoc (thanks dmackinder@!)
     Fix final JavaDoc example and add new example showing how to verify
     every array element using a custom comparator
 - Fix URL in pom.xml (aukevanleeuwen@)
 - Update JSONCompareResult.java adding 2 new lists for missing and unexpected fileds (thanks riccorazza@!)
 - Includes missing imports in test class (thanks javierseixas@!)

Version 1.2.3 - 2/5/2014
------------------------
 - This edition brought to you by dmackinder (thanks dmackinder!)
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
 - Updated Customization class to allow path-matching, and matching of expected and actual values with user-provided
   EqualityComparator.
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

