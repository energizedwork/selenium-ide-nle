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