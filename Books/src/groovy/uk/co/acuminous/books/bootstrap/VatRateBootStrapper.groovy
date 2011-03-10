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

package uk.co.acuminous.books.bootstrap

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat
import uk.co.acuminous.books.VatRate

class VatRateBootStrapper {

    private static DateTimeFormatter DTF = DateTimeFormat.forPattern("dd/MM/yy")
    private static List RATES = [
        [start: "01/01/2007", chargeable: 0G, payable: 0G],
        [start: "01/01/2007", end: "28/02/2008", chargeable: 0.175G, payable: 0.12G],
        [start: "01/03/2008", end: "31/12/2008", chargeable: 0.175G, payable: 0.13G],
        [start: "01/01/2009", end: "31/12/2009", chargeable: 0.15G, payable: 0.115G],
        [start: "01/01/2010", end: "03/01/2011", chargeable: 0.175G, payable: 0.13G],
        [start: "04/01/2011", chargeable: 0.2G, payable: 0.145G]            
    ]

    static void run() {
        new VatRateBootStrapper().bootStrap()
    }

    void bootStrap() {
        if (shouldBootStrap) {
            VatRate.withTransaction { def status ->
                RATES.each { Map params ->
                    VatRate rate = buildVatRate(params)
                    assert rate.save(flush:true), rate.errors
                }
            }
        }
    }

    boolean getShouldBootStrap() {
        return VatRate.count() == 0
    }

    VatRate buildVatRate(Map params) {
        return new VatRate(
            start: parseDate(params.start),
            end: parseDate(params.end),
            chargeable: params.chargeable,
            payable: params.payable)
    }

    LocalDate parseDate(String date) {
        return date ? DTF.parseDateTime(date).toLocalDate() : null
    }
}
