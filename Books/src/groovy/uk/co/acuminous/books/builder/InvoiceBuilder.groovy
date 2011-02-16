package uk.co.acuminous.books.builder

import org.apache.commons.lang.RandomStringUtils
import uk.co.acuminous.books.Invoice

class InvoiceBuilder extends BooksEntityBuilder {

    InvoiceBuilder() {
        setFactory 'Invoice.refernece', { def entity -> RandomStringUtils.randomAlphanumeric(5) }        
    }

    Collection getAutoAssignedFieldNames() {
        ['reference', 'narrative', 'raised', 'settled', 'amount']
    }

    Invoice build() {
        entity = new Invoice()
        assignValues()
        return entity
    }

    Invoice buildAndSave() {
        build()        
        save()
        save(entity.amount) // Cascades don't work with mockDomain
        return entity
    }
}
