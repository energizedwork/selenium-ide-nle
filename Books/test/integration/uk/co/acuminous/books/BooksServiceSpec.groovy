package uk.co.acuminous.books

import uk.co.acuminous.books.builder.InvoiceBuilder

import org.joda.time.LocalDate
import grails.plugin.spock.IntegrationSpec
import uk.co.acuminous.test.TestUtils
import org.joda.time.DateTime

@Mixin(TestUtils)
class BooksServiceSpec extends IntegrationSpec {

    BooksService booksService

    def "I can get the current tax year"() {
        given:
            TaxYear expectedTaxYear = createTaxYear('01/03/2008')

        when:
            TaxYear actualTaxYear
            runAsIf testDate, {
                actualTaxYear = booksService.currentTaxYear
            }

        then:
            actualTaxYear == expectedTaxYear

        where:
            testDate << parseDateTimes(["01/03/2008", "28/02/2009", "15/07/2008"])
    }

    def "I can get invoices settled in the specified tax year"() {
        given:
            TaxYear taxYear = createTaxYear('01/03/2008')
            LocalDate raised = parseLocalDate('01/01/2008')
            Invoice invoice = new InvoiceBuilder().raised(raised).settled(settled).buildAndSave()

        when:
            List<Invoice> invoices = booksService.getInvoices(taxYear)

        then:
            invoices.size() == 1
            invoices[0].reference == invoice.reference

        where:
            settled << parseLocalDates(['01/03/2008', '28/02/2009', '15/07/2008'])
    }

    def "Invoices outside the current tax year are excluded"() {
        given:
            TaxYear taxYear = createTaxYear('01/03/2008')
            new InvoiceBuilder().raised(parseLocalDate(raised)).settled(parseLocalDate(settled)).buildAndSave()

        when:
            List invoices = booksService.getInvoices(taxYear)

        then:
            invoices.size() == 0

        where:
            raised       | settled
            '27/02/2008' | '27/02/2008'
            '15/07/2008' | '01/03/2009'
            '01/03/2009' | '01/03/2009'
    }


    def "Unsettled invoices are included when I specify the current tax year"() {
        given:
            DateTime testDate = parseDateTime('01/07/2008')        
            TaxYear taxYear = createTaxYear('01/03/2008')
            new InvoiceBuilder().raised(parseLocalDate('01/03/2006')).settled(null).buildAndSave()
            new InvoiceBuilder().raised(parseLocalDate('01/03/2005')).settled(null).buildAndSave()

        when:
            List invoices
            runAsIf testDate, {
                invoices = booksService.getInvoices(taxYear)
            }

        then:
            invoices.size() == 2
    }


    def "Unsettled invoices are excluded when I specify previous tax years"() {
        given:
            DateTime testDate = parseDateTime('01/07/2009')
            TaxYear taxYear = createTaxYear('01/03/2008')
            new InvoiceBuilder().raised(parseLocalDate('01/03/2007')).settled(null).buildAndSave()
            new InvoiceBuilder().raised(parseLocalDate('01/03/2006')).settled(null).buildAndSave()

        when:
            List invoices
            runAsIf testDate, {
                invoices = booksService.getInvoices(taxYear)
            }

        then:
            invoices.size() == 0
    }

    def "Invoices are ordered by when they were raised then by reference"() {
        given:
            TaxYear taxYear = createTaxYear('01/03/2008')            
            new InvoiceBuilder().reference('A').raised(parseLocalDate('03/03/2008')).settled(parseLocalDate('01/04/2008')).buildAndSave()
            new InvoiceBuilder().reference('D').raised(parseLocalDate('01/03/2008')).settled(parseLocalDate('02/04/2008')).buildAndSave()
            new InvoiceBuilder().reference('B').raised(parseLocalDate('02/03/2008')).settled(parseLocalDate('03/04/2008')).buildAndSave()
            new InvoiceBuilder().reference('C').raised(parseLocalDate('02/03/2008')).settled(parseLocalDate('04/04/2008')).buildAndSave()

        when:
            List<Invoice> invoices = booksService.getInvoices(taxYear)

        then:
            invoices.size == 4
            invoices[0].reference == 'D'
            invoices[1].reference == 'B'
            invoices[2].reference == 'C'
            invoices[3].reference == 'A'
    }
}
