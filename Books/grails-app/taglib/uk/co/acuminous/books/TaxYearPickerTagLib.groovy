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
