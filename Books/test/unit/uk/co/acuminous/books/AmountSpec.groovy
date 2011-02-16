package uk.co.acuminous.books

import grails.plugin.spock.UnitSpec
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import uk.co.acuminous.books.builder.AmountBuilder
import uk.co.acuminous.test.TestUtils

@Mixin(TestUtils)
class AmountSpec extends UnitSpec {

    def setup() {
        PluginManagerHolder.pluginManager = [hasGrailsPlugin: { String name -> true }] as GrailsPluginManager
        mockDomain Amount
    }

    def cleanup() {
        PluginManagerHolder.pluginManager = null        
    }

    def "An amount must have a net value"() {

        given: "an amount with no net value"
        Amount amount = new AmountBuilder().net(null).build()

        expect: "validation to fail because of missing net value"
        fieldError(amount, 'net', 'nullable')
    }

    def "An amount must have vat"() {

        given: "an amount with no vat"
        Amount amount = new AmountBuilder().vat(null).build()

        expect: "validation to fail because of missing vat value"
        fieldError(amount, 'vat', 'nullable')
    }

    def "An amount must have a gross value"() {

        given: "an amount with no gross value"
        Amount amount = new AmountBuilder().gross(null).build()

        expect: "validation to fail because of missing gross value"
        fieldError(amount, 'gross', 'nullable')
    }

    def "An amount does not need a vat rate"() {

        given: "an amount with no vat rate"
        Amount amount = new AmountBuilder().vatRate(null).build()

        expect: "validation to pass"
        amount.validate()
    }


    def "Gross must equal net plus vat"() {

        given: "an where gross does not equal net plus vat"
        Amount amount = new AmountBuilder().net(1.0G).vat(1.0G).gross(3.0G).build()

        expect: "validation to fail because of invalid gross"
        fieldError(amount, 'gross', 'vatcheck')
    }
}
