package uk.co.acuminous.books

import grails.test.*
import uk.co.acuminous.books.builder.InvoiceBuilder
import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*
import org.gmock.WithGMock
import org.joda.time.LocalDate
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.codehaus.groovy.grails.plugins.GrailsPluginManager

@WithGMock
class InvoiceControllerTests extends ControllerUnitTestCase {

    def taglib
    def booksService

    protected void setUp() {
        super.setUp()

        PluginManagerHolder.pluginManager = [hasGrailsPlugin: { String name -> true }] as GrailsPluginManager

        taglib = mock(Object)
        mock(controller).g.returns(taglib).stub()

        booksService = mock(BooksService)
        controller.booksService = booksService
    }

    protected void tearDown() {
        super.tearDown()
        PluginManagerHolder.pluginManager = null
    }

    private void mockDomains() {
        [Invoice, Amount].each {
            mockDomain it
        }
    }

    void testThatTabDisplaysTheInvoicePage() {
        play {
            controller.tab()
        }
        assertThat renderArgs.template, is(equalTo('tab'))
    }

    void testThatListDisplaysAListOfInvoicesForTheCurrentTaxYear() {
        List invoices = ['invoices']
        TaxYear taxYear = new TaxYear()
        controller.request.session.taxYear = taxYear

        booksService.getInvoices(taxYear).returns(invoices)

        play {
            controller.list()
        }

        assertThat renderArgs.model.invoices, is(sameInstance(invoices))
        assertThat renderArgs.template, is(equalTo('tableData'))
    }

    void testThatICanShowABlankDialog() {
        Invoice invoice = mock(Invoice, constructor())
        play {
            controller.dialog()
        }
        assertThat renderArgs.template, is(equalTo('dialog'))
        assertThat renderArgs.model.invoice, is(sameInstance(invoice))
    }

    void testThatICanShowAPopulatedDialog() {
        mockDomains()
        Invoice invoice = new InvoiceBuilder().buildAndSave()

        controller.params.id = invoice.id
        play {
            controller.dialog()
        }
        assertThat renderArgs.template, is(equalTo('dialog'))
        assertThat renderArgs.model.invoice, is(sameInstance(invoice))
    }

    void testThatSaveCreatesANewInvoice() {
        mockDomains()

        controller.params.reference = 'foo'        
        controller.params.raised = new LocalDate()
        controller.params.narrative = 'bar'
        controller.params.amount = new Amount(net: 1.0G, vat: 2.0G, gross: 3.0G)
        play {
            controller.save()
        }

        assertThat Invoice.count(), is(equalTo(1))
        Invoice invoice = Invoice.list()[0]
        assertThat invoice.reference, is(equalTo('foo'))
        assertThat invoice.amount.net, is(equalTo(1.0G))
    }

    void testThatSaveUpdatesAnExistingInvoice() {
        mockDomains()

        Invoice invoice = new InvoiceBuilder().buildAndSave()

        controller.params.id = invoice.id
        controller.params.reference = 'foo'
        controller.params.amount = new Amount(net: 1.0G, vat: 2.0G, gross: 3.0G)
        play {
            controller.save()
        }


        assertThat Invoice.count(), is(equalTo(1))
        assertThat invoice.reference, is(equalTo('foo'))
        assertThat invoice.amount.net, is(equalTo(1.0G))
    }

    void testThatICanDeleteAnInvoice() {
        mockDomains()
        Invoice invoice = new InvoiceBuilder().buildAndSave()

        controller.params.id = invoice.id
        play {
            controller.delete()
        }

        assertThat Invoice.count(), is(equalTo(0))
        assertThat forwardArgs.action, is(equalTo('list'))
    }
}
