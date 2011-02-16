package uk.co.acuminous.test

import org.joda.time.DateTimeUtils
import org.joda.time.DateTime
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import static org.hamcrest.Matchers.equalTo
import org.springframework.mock.web.MockHttpServletResponse
import org.joda.time.format.DateTimeFormat
import org.joda.time.LocalDate
import org.joda.time.Interval
import uk.co.acuminous.books.utils.BooksUtils
import uk.co.acuminous.books.TaxYear
import uk.co.acuminous.books.builder.TaxYearBuilder

class TestUtils {
    boolean fieldError(def bean, String fieldName, String code) {
        assert !bean.validate()
        assert bean.errors.errorCount == 1
        assert bean.errors.getFieldError(fieldName)
        assert bean.errors.getFieldError(fieldName).code == code
        return true
    }

    void mockDomains(Class... classes) {
        classes.each { Class domainClass ->
            delegate.mockDomain domainClass
        }
    }

    void runAsIf(DateTime when, Closure callable) {
        try {
            DateTimeUtils.setCurrentMillisFixed(when.millis)
            callable()
        } finally {
            DateTimeUtils.setCurrentMillisSystem()
        }
    }

    private MockHttpServletResponse getResponse() {
        return controller.response
    }

    private Integer getStatus() {
        return controller.response.status
    }

    private String getContent() {
        return controller.response.contentAsString
    }
    
    static Matcher hasEntry(Object key, Object value) {
        Matcher keyMatcher = Matcher.isAssignableFrom(key.class) ? key : equalTo(key)
        Matcher valueMatcher = Matcher.isAssignableFrom(value.class) ? value : equalTo(value)
        Matchers.hasEntry(keyMatcher, valueMatcher)
    }

    static List parseDateTimes(List dateTimes) {
        dateTimes.collect { parseDateTime it }
    }

    static DateTime parseDateTime(String text) {
        text ? DateTimeFormat.forPattern('dd/MM/yyyy').parseDateTime(text) : null
    }

    static List parseLocalDates(List dates) {
        dates.collect { parseLocalDate it }
    }

    static LocalDate parseLocalDate(String text) {
        parseDateTime(text)?.toLocalDate()
    }

    private TaxYear createTaxYear(String text) {
        DateTime start = parseDateTime(text).withZoneRetainFields(BooksUtils.timeZone)
        DateTime end = start.plusYears(1).minusMillis(1)
        new TaxYearBuilder().start(start).end(end).buildAndSave()

    }
}
