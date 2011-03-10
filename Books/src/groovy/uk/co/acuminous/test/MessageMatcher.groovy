/*
 * Copyright 2010 Stephen Mark Cresswell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.acuminous.test

import org.hamcrest.Description
import org.hamcrest.BaseMatcher


class MessageMatcher extends BaseMatcher {

    String code = ''

    static void stubMessages(def g) {
        MessageMatcher matcher = new MessageMatcher()
        g.message(matcher).returns(matcher).stub()
    }

    boolean matches(Object o) {
        code = ((Map) o).containsKey('code') ? o.code : ''
        return true;
    }

    void describeTo(Description description) { }

    String toString() {
        return "code:${code}"
    }
}