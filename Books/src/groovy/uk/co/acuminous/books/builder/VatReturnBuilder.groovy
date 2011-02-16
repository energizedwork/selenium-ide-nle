package uk.co.acuminous.books.builder

import uk.co.acuminous.books.VatReturn
import org.joda.time.LocalDate

class VatReturnBuilder extends BooksEntityBuilder {

    VatReturnBuilder() {
        setFactory 'VatReturn.start', { def entity -> new LocalDate().withDayOfMonth(1) }
        setFactory 'VatReturn.end', { def entity -> entity.start.plusMonths(3).minusDays(1) }
    }

    Collection getAutoAssignedFieldNames() {
        ['start', 'end']
    }

    VatReturn build() {
        entity = new VatReturn()
        assignValues()
        return entity
    }
}