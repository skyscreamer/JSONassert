/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.skyscreamer.jsonassert;

/**
 * <p>These different modes define different behavior for the comparison of JSON for testing.
 * Each mode encapsulates two underlying behaviors: extensibility and strict ordering.</p>
 *
 * <table border="1">
 *     <caption>
 *         Behavior of JSONCompareMode
 *     </caption>
 *     <tr><th>&nbsp;</th><th>Extensible</th><th>Strict Ordering</th></tr>
 *     <tr><th>STRICT</th><th>no</th><th>yes</th></tr>
 *     <tr><th>LENIENT</th><th>yes</th><th>no</th></tr>
 *     <tr><th>NON_EXTENSIBLE</th><th>no</th><th>no</th></tr>
 *     <tr><th>STRICT_ORDER</th><th>yes</th><th>yes</th></tr>
 * </table>
 *
 * <p>If extensibility not allowed, then all of the expected values must match in what's being tested,
 * but any additional fields will cause the test to fail.  When extensibility is allowed, all values
 * must still match.  For example, if you're expecting:</p>
 *
 * <code>{id:1,name:"Carter"}</code>
 *
 * <p>Then the following will pass when <i>extensible</i>, and will fail when not:</p>
 *
 * <code>{id:1,name:"Carter",favoriteColor:"blue"}</code>
 *
 * <p>If <i>strict ordering</i> is enabled, JSON arrays must be in strict sequence.  For example, if you're expecting:</p>
 *
 * <code>{id:1,friends:[{id:<b>2</b>},{id:<b>3</b>}]}</code>
 *
 * <p>Then the following will fail strict ordering, but will otherwise pass:</p>
 *
 * <code>{id:1,friends:[{id:<b>3</b>},{id:<b>2</b>}]}</code>
 *
 */
public enum JSONCompareMode {
    /**
     * Strict checking.  Not extensible, and strict array ordering.
     */
    STRICT(false, true),
    /**
     * Lenient checking.  Extensible, and non-strict array ordering.
     */
    LENIENT(true, false),
    /**
     * Non-extensible checking.  Not extensible, and non-strict array ordering.
     */
    NON_EXTENSIBLE(false, false),
    /**
     * Strict order checking.  Extensible, and strict array ordering.
     */
    STRICT_ORDER(true, true),
    
    /**
     * Extensible checking, and only null values can be extended
     */
    EXTEND_WITH_NULL(true, false,true);

    private final boolean _extensible;
    private final boolean _strictOrder;
    private  boolean _isNullExtensible=false;

    JSONCompareMode(boolean extensible, boolean strictOrder) {
        _extensible = extensible;
        _strictOrder = strictOrder;
    }
    
    
    JSONCompareMode(boolean extensible, boolean strictOrder,boolean isNullExtensible){
        _extensible = extensible;
        _strictOrder = strictOrder;
        _isNullExtensible=isNullExtensible;
    }

    /**
     * Is extensible
     * @return True if results can be extended from what's expected, otherwise false.
     */
    public boolean isExtensible() {
        return _extensible;
    }

    /**
     * Strict order required
     * @return True if results require strict array ordering, otherwise false.
     */
    public boolean hasStrictOrder() {
        return _strictOrder;
    }
    
    /**
     * Get the equivalent {@code JSONCompareMode} with or without strict ordering.
     * 
     * @param strictOrdering if true, requires strict ordering of array elements
     * @return the equivalent {@code JSONCompareMode}
     */
    public JSONCompareMode withStrictOrdering(boolean strictOrdering) {
        if (strictOrdering) {
            return isExtensible() ? STRICT_ORDER : STRICT;
        } else {
            return isExtensible() ? LENIENT : NON_EXTENSIBLE;
        }
    }

    /**
     * Get the equivalent {@code JSONCompareMode} with or without extensibility.
     * 
     * @param extensible if true, allows keys in actual that don't appear in expected
     * @return the equivalent {@code JSONCompareMode}
     */
    public JSONCompareMode withExtensible(boolean extensible) {
        if (extensible) {
            return hasStrictOrder() ? STRICT_ORDER : LENIENT;
        } else {
            return hasStrictOrder() ? STRICT : NON_EXTENSIBLE;
        }
    }
    
    /**
     * Get whether the mode allow extend with null values
     * @return true if the mode allow extend with null values
     */
    public boolean isNullExtensible(){
        return _isNullExtensible;
    }
}
