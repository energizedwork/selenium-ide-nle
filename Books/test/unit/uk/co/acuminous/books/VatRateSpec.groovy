package uk.co.acuminous.books

import uk.co.acuminous.books.builder.VatRateBuilder
import grails.plugin.spock.UnitSpec
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.joda.time.LocalDate
import uk.co.acuminous.test.TestUtils

@Mixin(TestUtils)
class VatRateSpec extends UnitSpec {

    def setup() {
        PluginManagerHolder.pluginManager = [hasGrailsPlugin: { String name -> true }] as GrailsPluginManager
        mockDomain VatRate
    }

    def cleanup() {
        PluginManagerHolder.pluginManager = null
    }

    def "A rate must have a start date"() {

        given: "a rate with no start date"
        VatRate rate = new VatRateBuilder().start(null).build()

        expect: "validation to fail because of missing start date"
        fieldError(rate, 'start', 'nullable')
    }

    def "A rate must have a chargeable value"() {

        given: "a rate with no chargeable value"
        VatRate rate = new VatRateBuilder().chargeable(null).build()

        expect: "validation to fail because of missing chargeable value"
        fieldError(rate, 'chargeable', 'nullable')
    }


    def "A rate must have a payable value"() {

        given: "a rate with no payable value"
        VatRate rate = new VatRateBuilder().payable(null).build()

        expect: "validation to fail because of missing payable value"
        fieldError(rate, 'payable', 'nullable')
    }


    def "A rate does not need an end date"() {

        given: "a rate with no end date"
        VatRate rate = new VatRateBuilder().end(null).build()

        expect: "validation to pass"
        rate.validate()
    }

    def "A rate's end date can be on or after its start date"() {

        given: "a rate starting today"
        VatRate rate = new VatRateBuilder().start(new LocalDate()).build()

        when:
        rate.end = end

        then: "the rate is valid"
        rate.validate()

        where:
        end << [new LocalDate(), new LocalDate().plusDays(1)]
    }

    def "A rate's end date cannot be before its start date"() {

        given: "A rate starting today, but ending yesterday"
        LocalDate today = new LocalDate()
        VatRate rate = new VatRateBuilder().start(today).end(today.minusDays(1)).build()

        expect: "Validation to fail because the end date is before the start date"
        fieldError(rate, 'end', 'before.start')
    }
}
