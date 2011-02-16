package uk.co.acuminous.books

import grails.plugin.spock.IntegrationSpec
import org.joda.time.LocalDate
import uk.co.acuminous.books.builder.AmountBuilder
import uk.co.acuminous.books.builder.ExpenseBuilder
import uk.co.acuminous.books.builder.InvoiceBuilder
import uk.co.acuminous.books.builder.VatRateBuilder
import static java.math.RoundingMode.HALF_UP

class ExpenseSpec extends IntegrationSpec {

    def "Indexes are assigned on save"() {
        given:
        Expense expense = new ExpenseBuilder().narrative('Some Narrative').category('Some Category').build()

        when:
        expense.save()

        then:
        expense.narrativeIndex == 'some-narrative'
        expense.categoryIndex == 'some-category'
    }

    def "Find all distinct narratives"() {
        given:
        someExpenses()

        when:
        List narratives = Expense.findAllNarrativesStartingWith('Som')

        then:
        narratives.size() == 3
        assert narratives[0] == 'Some narrative A'
        assert narratives[1] == 'Some narrative B'
        assert narratives[2] == 'Some narrative C'
    }


    def "Find all distinct categories"() {
        given:
        someExpenses()

        when:
        List categories = Expense.findAllCategoriesStartingWith('Som')

        then:
        categories.size() == 3
        assert categories[0] == 'Some category A'
        assert categories[1] == 'Some category B'
        assert categories[2] == 'Some category C'
    }

    void someExpenses() {
        new ExpenseBuilder().narrative('Some narrative C').category('Some category C').buildAndSave()
        new ExpenseBuilder().narrative('Some narrative C').category('Some category C').buildAndSave()
        new ExpenseBuilder().narrative('Some narrative A').category('Some category A').buildAndSave()
        new ExpenseBuilder().narrative('Some narrative B').category('Some category B').buildAndSave()        
        new ExpenseBuilder().narrative('Other narrative').category('Other category').buildAndSave()        
    }

}
