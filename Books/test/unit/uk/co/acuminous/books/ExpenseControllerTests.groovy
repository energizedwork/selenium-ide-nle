package uk.co.acuminous.books

import grails.test.ControllerUnitTestCase
import org.gmock.WithGMock
import org.joda.time.LocalDate

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import uk.co.acuminous.books.builder.ExpenseBuilder

@WithGMock
class ExpenseControllerTests extends ControllerUnitTestCase {

    def taglib
    def booksService

    protected void setUp() {
        super.setUp()

        taglib = mock(Object)
        mock(controller).g.returns(taglib).stub()

        booksService = mock(BooksService)
        controller.booksService = booksService

        mockDomains()        
    }

    protected void tearDown() {
        super.tearDown()
    }

    private void stubAllMessages() {
        //taglib.message(any(String)).returns('stubbed message').stub()
    }

    private void mockDomains() {
        [Expense, Amount].each {
            mockDomain it
        }
    }

    void testThatTabDisplaysTheExpensesPage() {
        play {
            controller.tab()
        }
        assertThat renderArgs.template, is(equalTo('tab'))
    }


    void testThatListDisplaysAListOfExpensesForTheCurrentTaxYear() {
        List expenses = ['expenses']
        TaxYear taxYear = new TaxYear()
        controller.request.session.taxYear = taxYear

        booksService.getExpenses(taxYear).returns(expenses)

        play {
            controller.list()
        }

        assertThat renderArgs.model.expenses, is(sameInstance(expenses))
        assertThat renderArgs.template, is(equalTo('tableData'))
    }

    void testThatICanShowABlankDialog() {
        Expense expense = mock(Expense, constructor())
        play {
            controller.dialog()
        }
        assertThat renderArgs.template, is(equalTo('dialog'))
        assertThat renderArgs.model.expense, is(sameInstance(expense))
    }

    void testThatICanShowAPopulatedDialog() {
        Expense expense = new ExpenseBuilder().buildAndSave()

        controller.params.id = expense.id
        play {
            controller.dialog()
        }
        assertThat renderArgs.template, is(equalTo('dialog'))
        assertThat renderArgs.model.expense, is(sameInstance(expense))
    }

    void testThatSaveCreatesANewExpense() {
        mockDomains()
        stubAllMessages()

        controller.params.incurred = new LocalDate()
        controller.params.category = 'wonder'
        controller.params.narrative = 'bar'
        controller.params.amount = new Amount(net: 1.0G, vat: 2.0G, gross: 3.0G)
        play {
            controller.save()
        }

        assertThat Expense.count(), is(equalTo(1))
        Expense expense = Expense.list()[0]
        assertThat expense.category, is(equalTo('wonder'))
        assertThat expense.amount.net, is(equalTo(1.0G))
    }

    void testThatSaveUpdatesAnExistingExpense() {
        mockDomains()
        stubAllMessages()

        Expense expense = new ExpenseBuilder().narrative('foo').buildAndSave()

        controller.params.id = expense.id
        controller.params.narrative = 'bar'
        play {
            controller.save()
        }

        assertThat Expense.count(), is(equalTo(1))
        assertThat expense.narrative, is(equalTo('bar'))
    }

    void testThatSaveReportsErrors() {
        mockDomains()

        play {
            controller.save()
        }

        assertThat Expense.count(), is(equalTo(0))
        assertThat renderArgs.model.expense.hasErrors(), is(true)
        assertThat renderArgs.template, is(equalTo('dialog'))
    }

    void testThatICanDeleteAnInvoice() {
        mockDomains()
        Expense expense = new ExpenseBuilder().buildAndSave()

        controller.params.id = expense.id
        play {
            controller.delete()
        }

        assertThat Expense.count(), is(equalTo(0))
        assertThat forwardArgs.action, is(equalTo('list'))
    }    
}