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

package uk.co.acuminous.books

import groovy.xml.MarkupBuilder
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import static uk.co.acuminous.books.utils.BooksUtils.*
import org.joda.time.DateTime
import uk.co.acuminous.books.utils.BooksUtils


class TaxYearPickerTagLib {

    static namespace = 'booksui'
    static DateTimeFormatter DTF = new DateTimeFormat().forPattern(BooksUtils.jodaFormat)
    BooksService booksService


    def taxYearPicker = { Map attrs, def body ->

        String id = 'taxYearPicker'
        MarkupBuilder mb = new MarkupBuilder(out)

        List taxYears = booksService.getTaxYears()
        if (taxYears) {
            out << g.form([controller: 'taxYear', action: 'change', name:"${id}Form"], {
                mb.label(for: id) {
                    mb.mkp.yield g.message(code: "${id}.label")
                }
                mb.select(id: id, name: 'taxYear.id') {
                    taxYears.each { TaxYear taxYear ->

                        String startDate = formatDateTime(taxYear.start)
                        String endDate = formatDateTime(taxYear.end)

                        Map optionAttributes = [value: taxYear.id]
                        if (attrs.selected == taxYear) {
                            optionAttributes.selected = 'selected'
                        }

                        option(optionAttributes) {
                            mb.mkp.yield g.message(code: "${id}.display", args:[startDate, endDate])
                        }
                    }
                }
                out << g.javascript() {
                    mb.mkp.yield """
                    \$('#${id}').change(function() {
                        \$(this).parent().ajaxSubmit({
                            success: function() { \$('#${id}').trigger('tax-year-change') }
                        });
                        return false;
                    })
                    """
                }
            })            
        } else {
            mb.div(id:id) {
                mb.yield g.message(code: "${id}.empty")
            }
        }

        out << '' // flush for unit tests
    }

    String formatDateTime(DateTime dateTime) {
        return DTF.print(dateTime)
    }
}
