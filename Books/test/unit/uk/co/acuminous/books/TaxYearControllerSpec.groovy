package uk.co.acuminous.books

import grails.plugin.spock.ControllerSpec
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.gmock.WithGMock
import org.hamcrest.Matcher
import org.joda.time.LocalDate
import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.hasEntry
import org.joda.time.DateTime
import org.joda.time.Interval
import uk.co.acuminous.books.utils.BooksUtils

@WithGMock
class TaxYearControllerSpec extends ControllerSpec {

    def "change tax year"() {

        given:
            TaxYear expectedTaxYear = new TaxYear()
            TaxYearCmd cmd = new TaxYearCmd(taxYear: expectedTaxYear)

        when:
            controller.change(cmd)

        then:
            controller.request.session.taxYear == expectedTaxYear

    }

}
