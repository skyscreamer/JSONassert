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
 * Represents a value matcher that can compare two objects for equality.
 *
 * @param <T> the object type to compare
 */
public interface ValueMatcher<T> {

    /**
     * Compares the two provided objects whether they are equal.
     *
     * @param o1 the first object to check
     * @param o2 the object to check the first against
     * @return true if the objects are equal, false otherwise
     */
    boolean equal(T o1, T o2);

}
