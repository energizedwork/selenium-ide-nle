package uk.co.acuminous.books.utils

import org.joda.time.DateTimeZone
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class BooksUtils {
    static DateTimeZone getTimeZone() {
        return DateTimeZone.forID('Europe/London') 
    }

    static String getDatePickerFormat() {
        ConfigurationHolder.config.datapicker.format.org.joda.time.LocalDate
    }

    static String getJodaFormat() {
        ConfigurationHolder.config.jodatime.format.org.joda.time.LocalDate        
    }
}
