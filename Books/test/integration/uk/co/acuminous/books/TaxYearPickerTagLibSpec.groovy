package uk.co.acuminous.books

import grails.test.*
import uk.co.acuminous.test.TestUtils
import org.gmock.WithGMock
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.joda.time.DateTime
import org.joda.time.Interval
import uk.co.acuminous.books.utils.BooksUtils
import static uk.co.acuminous.test.MessageMatcher.stubMessages
import static org.hamcrest.Matchers.instanceOf
import grails.plugin.spock.GroovyPagesSpec
import groovy.util.slurpersupport.GPathResult


@WithGMock
@Mixin(TestUtils)
class TaxYearPickerTagLibSpec extends GroovyPagesSpec  {

    def g
    def html

    void 'tax year picker handles no tax years'() {

        when:
            render '<booksui:taxYearPicker/>'

        then:
            html.div.@id == 'taxYearPicker'
            html.div.text() == 'No tax years found'
    }

    void 'tax year picker markup is correct'() {

        given:
            someTaxYears()

        when:
            render '<booksui:taxYearPicker/>'

        then:
            html.form.@id == 'taxYearPickerForm'
            html.form.label.'@for' == 'taxYearPicker'
            html.form.label.text() == 'Tax Year:'
            html.form.select.@id == 'taxYearPicker'
            html.form.select.@name == 'taxYear.id'
            html.form.select.option.size() == 3
            html.form.script.size() == 1
    }

    void 'all tax years are listed'() {

        given:
            someTaxYears()

        when:
            render '<booksui:taxYearPicker/>'

        then:
            GPathResult select = html.form.select
            select.option[0].text() == '26-Nov-10 to 25-Nov-11'
            select.option[1].text() == '26-Nov-09 to 25-Nov-10'
            select.option[2].text() == '26-Nov-08 to 25-Nov-09'                
    }

    void 'tax year is selected'() {

        List taxYears
        given:
            taxYears = someTaxYears()


        when:
            render '<booksui:taxYearPicker selected="${selectedTaxYear}"/>', [selectedTaxYear: taxYears[1]]

        then:
            GPathResult select = html.form.select
            select.option[1].@selected == 'selected'
            select.option[0].@selected == ''
            select.option[2].@selected == ''
    }

    List someTaxYears() {
        List taxYears = []
        DateTime start = new DateTime(2010, 11, 26, 0, 0, 0, 0)
        (1..3).each { int offset ->
            TaxYear taxYear = new TaxYear(start: start, end: start.plusYears(1).minusMillis(1))
            assert taxYear.save(flush:true), taxYear.errors
            start = start.minusYears(1)
            taxYears << taxYear
        }
        return taxYears
    }

    def render(String template, params = [:]) {
        this.template = template
        this.params = params
        println "\n$output"
        html = new XmlSlurper().parseText("""<!DOCTYPE html [<!ENTITY nbsp "_">]>\n<results>${output}</results>""")
    }
}
