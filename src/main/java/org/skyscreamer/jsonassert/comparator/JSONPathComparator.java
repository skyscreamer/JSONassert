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

package org.skyscreamer.jsonassert.comparator;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.JSONPathCustomization;
import org.skyscreamer.jsonassert.ValueMatcherException;

import java.util.*;

/**
 * Provides Custom matching support like {@link CustomComparator} but via <a href="https://goessner.net/articles/JsonPath/">JSONPath</a>
 * <br><br>
 * This class is thread-safe, but reusing instances of it would result in blocking against it.
 * So <b>consider instantiating different instances of it per testcase</b> if running a multi-threaded/parallel test executor.
 *
 * @author Shane B. (<a href="mailto:shane@wander.dev">shane@wander.dev</a>)
 */
public class JSONPathComparator extends DefaultComparator {
    private final Configuration jsonPathConfig;

    private final Collection<JSONPathCustomization> customizations;

    private final Map<JSONPathCustomization, Object> resultCache = new HashMap<>();

    private JSONObject actual = null;

    /**
     * Create an instance of the JSONPath based comparator.
     * <br>
     * This is similar to {@link CustomComparator}, except that the path specification supports
     * full <a href="https://goessner.net/articles/JsonPath/">JSONPath</a> via
     * <a href="https://github.com/jayway/JsonPath">com.jayway.jsonpath:json-path</a>.
     * <br>
     * This means that {@link CustomComparator}'s more limited path specification is supported
     * <b>and</b> full JSONPath queries are supported too.
     * <br><br>
     * If multiple {@link JSONPathCustomization} are specified that match the same JSON element,
     * then all of them will be evaluated. If any of their comparators return <code>false</code> for the matching element,
     * then the comparison will be considered as failed overall.
     * <br><br>
     * Refer to class {@link JSONPathCustomization} for examples.
     *
     * @param mode              Comparator mode
     * @param customizations    Validation specification overrides for all nodes that match the specified JSONPath queries
     */
    public JSONPathComparator(JSONCompareMode mode, JSONPathCustomization... customizations) {
        super(mode);
        this.jsonPathConfig = new Configuration.ConfigurationBuilder()
                .jsonProvider(new JsonOrgJsonProvider()).build();

        this.customizations = Arrays.asList(customizations);
    }

    @Override
    public synchronized void compareJSON(String prefix, JSONObject expected, JSONObject actual, JSONCompareResult result) {
        // synchronised to make reuse of a comparator technically possible.
        // Main reason to capture and set the member here rather than in the constructor is
        // to not hugely change the implementation details of JSONAssert, while still maintaining
        // the necessary pointers to the top level of the JSONObject hierarchy.
        // This means that in a multithreaded environment we will block on this method.
        // So: devs should ideally not reuse instances of this comparator across tests that are meant to run in parallel.
        boolean isRootCall = this.actual == null;
        if (isRootCall) {
            this.actual = actual;
            this.resultCache.clear();
        }

        super.compareJSON(prefix, expected, actual, result);

        if (isRootCall) {
            this.actual = null;
        }
    }

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result) {
        // This is very similar to CustomComparator.
        // In fact, I question why CustomComparator supports some level of wildcard matching,
        // but only compares a *single* Customisation being evaluated.
        // It appears quite possible have multiple Customisations with a matching path in it...
        List<JSONPathCustomization> customizations = getCustomization(actualValue);
        if (!customizations.isEmpty()) {
            try {
                // Does *any* of the customisations result in a test failure?
                for (JSONPathCustomization customization : customizations) {
                    if (!customization.matches(prefix, expectedValue, actualValue, result)) {
                        result.fail(prefix, expectedValue, actualValue);
                    }
                }
            } catch (ValueMatcherException e) {
                result.fail(prefix, e);
            }
        } else {
            super.compareValues(prefix, expectedValue, actualValue, result);
        }
    }

    /**
     * Some implementation details need to be known here:
     * <br><br>
     * {@link org.skyscreamer.jsonassert} uses the {@link org.json} implementation of JSON in Java,
     * as does the {@link JsonOrgJsonProvider} for {@link com.jayway.jsonpath}.
     * <br><br>
     * Both libraries will ultimately recursively navigate the JSON tree of the parsed JSON objects.
     * They will <b>reuse</b> the same instances (i.e. the same references/pointers) of the JSON objects
     * in this tree, which is only parsed once (initially).
     * <br>
     * Furthermore, observe that in Java, due to
     * <a href="https://docs.oracle.com/javase/tutorial/java/data/autoboxing.html">Primitive Boxing</a>,
     * even primitive datatypes in the tree (<code>long</code>, <code>int</code>, ...) will actually
     * be a pointer/reference.
     * <br><br>
     * Consequently, it is a completely valid strategy to evaluate a JSONPath expression against the JSON tree
     * and find all results, and compare those against any node in the recursively navigated JSON tree
     * by reference/pointer (i.e. <code>==</code>) to determine if they are the same object.
     * <br><br>
     * Here, we use this strategy to determine all the customisation rules applicable to a given JSON Tree node.
     *
     * @param actualValue The object to find all applicable {@link JSONPathCustomization}s for
     * @return  Customisations applicable to this object
     */
    private List<JSONPathCustomization> getCustomization(Object actualValue) {
        List<JSONPathCustomization> applicableCustomisations = new ArrayList<>();
        for (JSONPathCustomization c : customizations) {
            // some implementation details need to be known here.
            // JSONAssert uses the org.json
            Object results = this.getCachedResult(c);

            if (results instanceof JSONArray) {
                // multiple results for JSONPath expression
                // (something like $.items[*] might return this)
                for (Object o : (JSONArray) results) {
                    if (o == actualValue) {
                        applicableCustomisations.add(c);
                    }
                }
            } else if (results == actualValue) {
                applicableCustomisations.add(c);
            }
        }
        return applicableCustomisations;
    }

    /**
     * Avoids performing queries multiple times. The result will always be the same.
     */
    private Object getCachedResult(JSONPathCustomization c) {
        if (this.resultCache.containsKey(c)) {
            return this.resultCache.get(c);
        }
        Object result = c.getJsonPath().read(this.actual, this.jsonPathConfig);
        this.resultCache.put(c, result);
        return result;
    }
}
