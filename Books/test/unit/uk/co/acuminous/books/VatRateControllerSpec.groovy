package uk.co.acuminous.books

import org.gmock.WithGMock
import org.joda.time.LocalDate

import grails.plugin.spock.ControllerSpec
import uk.co.acuminous.books.builder.VatRateBuilder
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.codehaus.groovy.grails.plugins.GrailsPluginManager

import static org.hamcrest.Matchers.*
import org.hamcrest.Matcher

@WithGMock
class VatRateControllerSpec extends ControllerSpec {


    def setup() {
        mockDomain VatRate

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
        when:
        controller.list()

        then:
        renderArgs.template == 'tableData'
    }

    def "Rates are listed primarily by start date"() {
        LocalDate today = new LocalDate()
        LocalDate tomorrow = today.plusDays(1)
        LocalDate yesterday = today.minusDays(1)

        given:
        new VatRateBuilder().start(today).buildAndSave()
        new VatRateBuilder().start(tomorrow).buildAndSave()
        new VatRateBuilder().start(yesterday).buildAndSave()

        when:
        controller.list()

        then:
        Collection rates = renderArgs.model.rates
        rates.size() == 3
        rates[0].start == tomorrow
        rates[1].start == today
        rates[2].start == yesterday
    }

    def "Rates are listed by chargeable after start date"() {
        LocalDate today = new LocalDate()
        LocalDate tomorrow = today.plusDays(1)
        LocalDate yesterday = today.minusDays(1)

        given:
        new VatRateBuilder().start(today).chargeable(0.15G).buildAndSave()
        new VatRateBuilder().start(today).chargeable(0.175G).buildAndSave()
        new VatRateBuilder().start(tomorrow).buildAndSave()
        new VatRateBuilder().start(yesterday).buildAndSave()

        when:
        controller.list()

        then:
        Collection rates = renderArgs.model.rates        
        rates[1].chargeable == 0.175G
        rates[2].chargeable == 0.15G
    }

    def "Dialog action uses the correct template"() {
        when:
        controller.dialog()

        then:
        renderArgs.template == 'dialog'
    }

    def "Dialog action creates a new rate if none is specified"() {
        given:
        VatRate mockRate = mock(VatRate, constructor())
        
        when:
        play {
            controller.dialog()
        }

        then:
        renderArgs.model.rate == mockRate
    }

    def "Dialog action retrieves an existing rate if one is specified"() {
        given:
        VatRate rate = new VatRateBuilder().buildAndSave()

        when:
        controller.params.id = rate.id
        controller.dialog()

        then:
        renderArgs.model.rate == rate
    }

    def "Save action persistes a new rate"() {
        given:
        LocalDate start = new LocalDate()
        BigDecimal chargeable = 0.175G
        BigDecimal payable= 0.13G

        when:
        controller.params.start = start
        controller.params.chargeable = chargeable
        controller.params.payable = payable
        controller.save()

        then:
        VatRate rate = theOnlyRate
        assertRate rate, start, chargeable, payable
    }


    def "Save action renders the dialog after persisting a new rate"() {
        when:
        controller.params.start = new LocalDate()
        controller.params.chargeable = 0.1G
        controller.params.payable = 0.1G
        controller.save()

        then:
        renderArgs.model.rate == theOnlyRate
        renderArgs.template == 'dialog'
    }

    def "Save action updates an existing rate"() {
        given:
        LocalDate today = new LocalDate()
        VatRate rate = new VatRateBuilder().start(today.minusDays(1)).buildAndSave()

        when:
        controller.params.id = rate.id
        controller.params.start = today
        controller.save()

        then:
        theOnlyRate.start == today
    }

    def "Save action renders the dialog after updating an existing rate"() {
        given:
        LocalDate today = new LocalDate()
        VatRate rate = new VatRateBuilder().start(today.minusDays(1)).buildAndSave()

        when:
        controller.params.id = rate.id
        controller.params.start = today
        controller.save()

        then:
        rate == theOnlyRate
        renderArgs.model.rate == rate
        renderArgs.template == 'dialog'
    }

    def "Delete action deletes a rate"() {
        given:
        VatRate rate = new VatRateBuilder().buildAndSave()

        when:
        controller.params.id = rate.id
        controller.delete()

        then:
        VatRate.count() == 0
    }

    def "Delete action forwards to list"() {
        given:
        VatRate rate = new VatRateBuilder().buildAndSave()

        when:
        controller.params.id = rate.id
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

    def "vatPicker action renders a vatPicker for the specified date "() {
        given:
        LocalDate today = new LocalDate()
        BooksService booksService = Mock(BooksService)
        controller.booksService = booksService
        List someRates = [1, 2]

        def mockTagLib = mock(BooksUiTagLib)
        mock(controller).getBooksui().returns(mockTagLib)

        Matcher paramsMatcher = allOf([
            hasEntry('value', null),
            hasEntry('field', 'amount.vatRate'),
            hasEntry('rates', someRates),
            hasEntry('noSelection', ['null':'N/A'])
        ])
        mockTagLib.vatPicker(paramsMatcher).returns('blah')

        when:
        VatPickerCmd cmd = new VatPickerCmd(when: today)
        play {
            controller.vatPicker(cmd)
        }

        then:
        1 * booksService.getVatRates(today) >> someRates
        controller.response.contentAsString == 'blah'
    }


    def "vatPicker action with the selected rate render a vatPicker"() {
        given:
        LocalDate today = new LocalDate()
        BooksService booksService = Mock(BooksService)
        controller.booksService = booksService
        List someRates = [1, 2]
        Long someSelection = 1

        def mockTagLib = mock(BooksUiTagLib)
        mock(controller).getBooksui().returns(mockTagLib)

        Matcher paramsMatcher = allOf([
            hasEntry('value', someSelection)
        ])
        mockTagLib.vatPicker(paramsMatcher).returns('blah')

        when:
        VatPickerCmd cmd = new VatPickerCmd(when: today, selected: someSelection)
        play {
            controller.vatPicker(cmd)
        }

        then:
        1 * booksService.getVatRates(today) >> someRates
        controller.response.contentAsString == 'blah'
    }


    private VatRate getTheOnlyRate() {
        VatRate.count() == 1
        return VatRate.list()[0]
    }

    private boolean assertRate(VatRate rate, LocalDate start, BigDecimal chargeable, BigDecimal payable) {
        assert rate.start == start
        assert rate.chargeable == chargeable
        assert rate.payable == payable
        return true
    }

}
