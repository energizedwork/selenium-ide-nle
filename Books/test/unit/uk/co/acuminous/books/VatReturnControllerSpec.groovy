package uk.co.acuminous.books

import grails.plugin.spock.ControllerSpec
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.gmock.WithGMock
import org.joda.time.LocalDate
import uk.co.acuminous.books.builder.VatReturnBuilder

import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.instanceOf
import static uk.co.acuminous.test.TestUtils.hasEntry

import org.hamcrest.Matcher

@WithGMock
class VatReturnControllerSpec extends ControllerSpec {

    BooksUiTagLib booksUiTagLib

    def setup() {
        mockDomain VatReturn

        booksUiTagLib = mock(BooksUiTagLib)
        mock(controller).getBooksui().returns(booksUiTagLib).stub()

        PluginManagerHolder.pluginManager = [hasGrailsPlugin: { String name -> true }] as GrailsPluginManager
    }

    def cleanup() {
        PluginManagerHolder.pluginManager = null        
    }

    def "Tab action uses the correct template"() {
        when:
        controller.tab()

        then:
        renderArgs.template == 'tab'
    }

    def "List action uses the correct template"() {
        given:
        BooksService mockBooksService = Mock(BooksService)
        controller.booksService = mockBooksService
        
        when:
        controller.list()

        then:
        renderArgs.template == 'tableData'
    }

    def "List action adds the years VAT returns to the model"() {
        given:
            BooksService mockBooksService = Mock(BooksService)
            controller.booksService = mockBooksService
            TaxYear taxYear = new TaxYear()
            controller.session.taxYear = taxYear
            List expectedVatReturns = [1, 2, 3]

        when:
            controller.list()

        then:
            1 * mockBooksService.getVatReturns(taxYear) >> expectedVatReturns
            renderArgs.model.vatReturns == expectedVatReturns
    }

    def "Dialog action uses the correct template"() {
        when:
        controller.dialog()

        then:
        renderArgs.template == 'dialog'
    }

    def "Dialog action creates a new VAT return if none is specified"() {
        given:
        VatReturn mockVatReturn = mock(VatReturn, constructor())

        when:
        play {
            controller.dialog()
        }

        then:
        renderArgs.model.vatReturn == mockVatReturn
    }

    def "Dialog action retrieves an existing VAT return if one is specified"() {
        given:
        VatReturn vatReturn = new VatReturnBuilder().buildAndSave()

        when:
        controller.params.id = vatReturn.id
        controller.dialog()

        then:
        renderArgs.model.vatReturn == vatReturn
    }

    def "Calculate action returns VAT return financials as JSON for the specified period"() {
        given:
        stubVatReturnConstructor(start, end, [totalVatDue: amount])

        booksUiTagLib.vatReturnAmount(hasEntry('value', amount)).returns(amountAsText)

        Matcher dataMatcher = allOf ([
            hasEntry('success', true),
            hasEntry('financials', allOf([
                hasFinancialEntry('totalVatDue', amount, amountAsText)
            ]))                
        ])
        mock(SafeJsonConverter).static.render(dataMatcher, controller.response)

        when:
        play {
            CalculateVatReturnCmd cmd = new CalculateVatReturnCmd(start: start, end: end)
            controller.calculate(cmd)
        }

        then:
        'success'

        where:
        start | end      | amount                 | amountAsText
        today | tomorrow | new BigDecimal('3412') | '3,412'        
    }

    def "Save action persistes a new VatReturn"() {
        when:
        controller.params.start = start
        controller.params.end = end
        controller.params.vatDueOnSales = vatDueOnSales
        controller.save()

        then:
        VatReturn vatReturn = theOnlyVatReturn
        vatReturn.start == start
        vatReturn.end == end
        vatReturn.vatDueOnSales == vatDueOnSales

        where:
        start           | end                         | vatDueOnSales
        new LocalDate() | new LocalDate().plusDays(1) | 43234.23
    }


    def "Save action updates an existing VatReturn"() {
        given:
        VatReturn vatReturn = new VatReturnBuilder().buildAndSave()

        when:
        controller.params.id = vatReturn.id
        controller.params.start = start
        controller.params.end = end
        controller.params.vatDueOnSales = vatDueOnSales
        controller.save()

        then:
        vatReturn == theOnlyVatReturn
        vatReturn.start == start
        vatReturn.end == end
        vatReturn.vatDueOnSales == vatDueOnSales

        where:
        start           | end                         | vatDueOnSales
        new LocalDate() | new LocalDate().plusDays(1) | 43234.23
    }

    def "Save action renders the dialog"() {
        when:
        controller.params.start = new LocalDate()
        controller.params.end = new LocalDate().plusDays(1)
        controller.save()

        then:
        renderArgs.model.vatReturn == theOnlyVatReturn
        renderArgs.template == 'dialog'
    }


    def "Delete action deletes a vatReturn"() {
        given:
        VatReturn vatReturn = new VatReturnBuilder().buildAndSave()

        when:
        controller.params.id = vatReturn.id
        controller.delete()

        then:
        VatReturn.count() == 0
    }

    def "Delete action forwards to list"() {
        given:
        VatReturn vatReturn = new VatReturnBuilder().buildAndSave()

        when:
        controller.params.id = vatReturn.id
        controller.delete()

        then:
        forwardArgs.action == 'list'
    }


    def "Delete action tolerates missing rates"() {
        when:
        controller.params.id = 99
        controller.delete()

        then:
        forwardArgs.action == 'list'
    }

    LocalDate getToday() {
        new LocalDate()
    }

    LocalDate getTomorrow() {
        new LocalDate().plusDays(1)
    }

    VatReturn stubVatReturnConstructor(LocalDate start, LocalDate end, Map financials) {
        VatReturn vatReturn = mock(VatReturn, constructor())
        vatReturn.forPeriod(start, end).returns(vatReturn)
        vatReturn.financials.returns(financials)
        return vatReturn
    }

    private hasFinancialEntry(String fieldName, BigDecimal value, String text) {
        return hasEntry(fieldName, allOf([
                hasEntry('text', text),
                hasEntry('value', value)
        ]))
    }

    VatReturn getTheOnlyVatReturn() {
        assert VatReturn.count() == 1
        return VatReturn.list()[0]
    }

}
