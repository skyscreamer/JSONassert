Changelog
=========

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

