package uk.co.acuminous.books

import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.LocalDate
import uk.co.acuminous.books.utils.BooksUtils

class BooksService {

    boolean transactional = true
    boolean singleton = true

    List getTaxYears() {
        return TaxYear.list(sort: 'start', order: 'desc')
    }

    TaxYear getCurrentTaxYear() {
        DateTime now = new DateTime()
        TaxYear.withCriteria(uniqueResult:true) {
            le('start', now)
            ge('end', now)
        }
    }

    List getInvoices(TaxYear taxYear) {
        TaxYear currentTaxYear = getCurrentTaxYear()

        Invoice.withCriteria() {
            or {
                and {
                    ge('settled', taxYear?.start?.toLocalDate())
                    le('settled', taxYear?.end?.toLocalDate())
                }
                if (taxYear == currentTaxYear) {
                    isNull('settled')
                }
            }
            order('raised', 'asc')
            order('reference', 'asc')
        }
    }

    List getExpenses(TaxYear taxYear) {
        Expense.withCriteria() {
            ge('incurred', taxYear.start.toLocalDate())
            le('incurred', taxYear.end.toLocalDate())
            order('incurred', 'asc')
            order('narrative', 'asc')            
        }
    }

    List getVatRates(LocalDate date) {
        VatRate.withCriteria() {
            le('start', date)
            or {
                ge('end', date)
                isNull('end')
            }
            order('chargeable', 'asc')
            order('start', 'asc')
        }
    }

    List getVatReturns(TaxYear taxYear) {
        getVatReturns(taxYear.start.toLocalDate(), taxYear.end.toLocalDate())
    }

    List getVatReturns(LocalDate from, LocalDate to) {
        VatReturn.withCriteria() {
            ge('start', from)
            le('end', to)
            order('start', 'asc')
        }
    }
}
