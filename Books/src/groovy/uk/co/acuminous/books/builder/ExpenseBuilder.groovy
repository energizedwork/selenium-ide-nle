package uk.co.acuminous.books.builder

import org.apache.commons.lang.RandomStringUtils

import uk.co.acuminous.books.Expense

class ExpenseBuilder extends BooksEntityBuilder {

    ExpenseBuilder() {
        factories.put 'Expense.reference', { def entity -> RandomStringUtils.randomAlphanumeric(5) }        
    }

    Collection getAutoAssignedFieldNames() {
        ['incurred', 'narrative', 'category', 'amount']
    }

    Expense build() {
        entity = new Expense()
        assignValues()
        return entity
    }

    Expense buildAndSave() {
        build()
        save()
        save(entity.amount) // Cascades don't work with mockDomain
        return entity
    }    
}