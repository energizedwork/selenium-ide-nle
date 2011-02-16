package uk.co.acuminous.books.jbehave.converter

import org.jbehave.core.steps.ParameterConverters.ParameterConverter
import java.lang.reflect.Type
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import uk.co.acuminous.books.utils.BooksUtils
import org.joda.time.DateTime

class JodaConverter implements ParameterConverter {

    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern('dd-MMM-yy')
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern('dd-MMM-yy HH:mm:ss')

    boolean accept(Type type) {
        return [DateTime, LocalDate].contains(type)
    }

    Object convertValue(String value, Type type) {
        switch(type) {
            case LocalDate:
                return DATE_FORMATTER.parseDateTime(value).withZone(BooksUtils.timeZone).toLocalDate()
            case DateTime:
                DATE_TIME_FORMATTER.parseDateTime(value).withZone(BooksUtils.timeZone)
        }
    }
}
