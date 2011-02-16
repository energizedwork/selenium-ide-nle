package uk.co.acuminous.books.bootstrap

import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import uk.co.acuminous.books.TaxYear
import org.joda.time.DateTime

class TaxYearBootStrapper {

    private static DateTimeFormatter DTF = DateTimeFormat.forPattern("dd/MM/yyyy")
    private static List TAX_YEARS = [
        [start: "01/03/2007", end: "29/02/2008"],
        [start: "01/03/2008", end: "28/02/2009"],
        [start: "01/03/2009", end: "28/02/2010"],
        [start: "01/03/2010", end: "28/02/2011"],
        [start: "01/03/2011", end: "29/02/2012"]            
    ]

    static void run() {
        new TaxYearBootStrapper().bootStrap()
    }

    void bootStrap() {
        if (shouldBootStrap) {
            TaxYear.withTransaction { def status ->
                TAX_YEARS.each { Map params ->
                    TaxYear taxYear = buildTaxYear(params)
                    assert taxYear.save(flush:true), taxYear.errors
                }
            }
        }
    }

    boolean getShouldBootStrap() {
        return TaxYear.count() == 0
    }

    TaxYear buildTaxYear(Map params) {
        return new TaxYear(
            start: parseDate(params.start),
            end: parseDate(params.end).plusDays(1).minusMillis(1)
        )
    }

    DateTime parseDate(String date) {
        return date ? DTF.parseDateTime(date) : null
    }
}
