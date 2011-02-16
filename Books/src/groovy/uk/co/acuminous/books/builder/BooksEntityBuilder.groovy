package uk.co.acuminous.books.builder

import uk.co.acuminous.books.*
import org.joda.time.LocalDate
import uk.co.acuminous.test.EntityBuilder
import org.joda.time.DateTime

abstract class BooksEntityBuilder extends EntityBuilder {

    BooksEntityBuilder() {
        setFactory Amount, { def entity -> new AmountBuilder().build() }
        setFactory Expense, { def entity -> new ExpenseBuilder().build() }
        setFactory Invoice, { def entity -> new InvoiceBuilder().build() }
        setFactory VatRate, { def entity -> new VatRateBuilder().build() }
        setFactory LocalDate, { def entity -> new LocalDate() }
        setFactory DateTime, { def entity -> new DateTime() }
    }

}
