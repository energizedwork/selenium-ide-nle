package uk.co.acuminous.books.builder

import org.apache.commons.lang.RandomStringUtils
import uk.co.acuminous.books.Invoice
import org.joda.time.LocalDate
import uk.co.acuminous.books.TaxYear

class TaxYearBuilder extends BooksEntityBuilder {

    TaxYearBuilder() {
        setFactory 'TaxYear.start', { def entity -> new LocalDate().minusMonths(1) }
        setFactory 'TaxYear.end', { def entity -> new LocalDate().plusMonths(1) }
    }

    Collection getAutoAssignedFieldNames() {
        ['start', 'end']
    }

    TaxYear build() {
        entity = new TaxYear()
        assignValues()
        return entity
    }

    TaxYear buildAndSave() {
        build()        
        save()
        return entity
    }
}
