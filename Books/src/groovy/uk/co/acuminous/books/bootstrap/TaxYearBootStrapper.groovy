/*
 * Copyright 2010 Acuminous Ltd
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
