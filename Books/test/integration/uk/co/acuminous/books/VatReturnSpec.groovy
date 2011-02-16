package uk.co.acuminous.books

import org.joda.time.LocalDate
import uk.co.acuminous.books.builder.InvoiceBuilder
import uk.co.acuminous.books.builder.AmountBuilder
import uk.co.acuminous.books.builder.VatRateBuilder
import static java.math.RoundingMode.HALF_UP
import grails.plugin.spock.IntegrationSpec
import uk.co.acuminous.books.builder.ExpenseBuilder


class VatReturnSpec extends IntegrationSpec {

    LocalDate periodEnd = new LocalDate().minusYears(1)
    LocalDate periodStart = periodEnd.minusMonths(1)
    LocalDate inPeriod = periodStart.plusDays(15)
    LocalDate beforePeriod = periodStart.minusDays(1)
    LocalDate afterPeriod = periodEnd.plusDays(1)

    VatRate rate1
    VatRate rate2
    VatRate rate3

    def setup() {
        rate1 = new VatRateBuilder().chargeable(0.175G).payable(0.135G).buildAndSave()
        rate2 = new VatRateBuilder().chargeable(0.150G).payable(0.125G).buildAndSave()
        rate3 = new VatRateBuilder().chargeable(0.00G).payable(0.00G).buildAndSave()
    }

    def "Creating a VAT return considers all invoices settled within the period"() {
        given:
        Amount amount = new AmountBuilder().vatRate(rate1).build()
        new InvoiceBuilder().raised(beforePeriod).settled(beforePeriod).amount(amount).buildAndSave()
        new InvoiceBuilder().raised(beforePeriod).settled(periodStart).amount(amount).buildAndSave()
        new InvoiceBuilder().raised(beforePeriod).settled(inPeriod).amount(amount).buildAndSave()
        new InvoiceBuilder().raised(beforePeriod).settled(periodEnd).amount(amount).buildAndSave()
        new InvoiceBuilder().raised(beforePeriod).settled(afterPeriod).amount(amount).buildAndSave()

        when:
        VatReturn vatReturn = new VatReturn().forPeriod(periodStart, periodEnd)

        then:
        vatReturn.invoices.size() == 3
    }


    def "Creating a VAT return considers all expenses incurred within the period"() {
        given:
        Amount amount = new AmountBuilder().build()
        new ExpenseBuilder().incurred(beforePeriod).amount(amount).buildAndSave()
        new ExpenseBuilder().incurred(periodStart).amount(amount).buildAndSave()
        new ExpenseBuilder().incurred(inPeriod).amount(amount).buildAndSave()
        new ExpenseBuilder().incurred(periodEnd).amount(amount).buildAndSave()
        new ExpenseBuilder().incurred(afterPeriod).amount(amount).buildAndSave()

        when:
        VatReturn vatReturn = new VatReturn().forPeriod(periodStart, periodEnd)

        then:
        vatReturn.expenses.size() == 3
    }

    def "VAT due on sales is the sum of all VAT due within the period"() {
        given:
        someInvoices()

        when:
        VatReturn vatReturn = new VatReturn().forPeriod(periodStart, periodEnd)

        then:        
        BigDecimal total = (
            (117.50G * 0.135G).setScale(2, HALF_UP) +
            (57.50G * 0.125G).setScale(2, HALF_UP) +
            (230.00G * 0.125G).setScale(2, HALF_UP)
        )

        vatReturn.vatDueOnSales == total                
    }

    def "VAT due on sales is zero when there are no VAT invoices within the period"() {
        when:
        VatReturn vatReturn = new VatReturn().forPeriod(periodStart, periodEnd)

        then:
        vatReturn.vatDueOnSales == 0.00G
    }

    def "VAT due on acquisitions from other ec member states is zero"() {
        when:
        VatReturn vatReturn = new VatReturn().forPeriod(periodStart, periodEnd)

        then:
        vatReturn.vatDueOnAcquisitionsFromOtherEcMemberStates == 0.00G
    }

    def "Total VAT due is the sum of VAT due on sales and on acquisitions"() {
        given:
        someInvoices()

        when:
        VatReturn vatReturn = new VatReturn().forPeriod(periodStart, periodEnd)

        then:
        vatReturn.vatDueTotal == vatReturn.vatDueOnSales + vatReturn.vatDueOnAcquisitionsFromOtherEcMemberStates
    }

    void "VAT reclaimed is the sum of VAT reclaimed from purchases"() {
        given:
        someExpenses()

        when:
        VatReturn vatReturn = new VatReturn().forPeriod(periodStart, periodEnd)

        then:
        vatReturn.vatReclaimedOnPurchases == 16.00G
    }

    void "VAT net is the result of total VAT due minus VAT reclaimed on purchases"() {
        given:
        someInvoices()
        someExpenses()

        when:
        VatReturn vatReturn = new VatReturn().forPeriod(periodStart, periodEnd)

        then:
        vatReturn.vatNet == vatReturn.vatDueTotal - vatReturn.vatReclaimedOnPurchases
    }

    void "Total value of sales ex VAT is the sum of the invoice net amount "() {
        given:
        someInvoices()

        when:
        VatReturn vatReturn = new VatReturn().forPeriod(periodStart, periodEnd)

        then:
        vatReturn.totalValueOfSalesExVat == 446G
    }
    
    void "Total value of purchases ex VAT is the sum of the expense net amount "() {
        given:
        someExpenses()

        when:
        VatReturn vatReturn = new VatReturn().forPeriod(periodStart, periodEnd)

        then:
        vatReturn.totalValueOfPurchasesExVat == 892G
    }

    def "Total value of supplies ex VAT to other EC member states is zero"() {
        given:
        someInvoices()

        when:
        VatReturn vatReturn = new VatReturn().forPeriod(periodStart, periodEnd)

        then:
        vatReturn.totalValueOfSuppliesExVatToOtherEcMemberStates == 0G
    }
   
    def "Total value of acquisitions ex VAT from other EC member states is zero"() {
        given:
        someExpenses()
        
        when:
        VatReturn vatReturn = new VatReturn().forPeriod(periodStart, periodEnd)

        then:
        vatReturn.vatDueOnAcquisitionsFromOtherEcMemberStates == 0G
    }

    def "All values are reset before forPeriod is called"() {
        given:
        someInvoices()
        someExpenses()

        when:
        VatReturn vatReturn = new VatReturn().forPeriod(periodStart, periodEnd).forPeriod(periodStart, periodEnd)

        then:
        BigDecimal total = (
            (117.50G * 0.135G).setScale(2, HALF_UP) +
            (57.50G * 0.125G).setScale(2, HALF_UP) +
            (230.00G * 0.125G).setScale(2, HALF_UP)
        )

        vatReturn.vatDueOnSales == total
        vatReturn.vatDueOnAcquisitionsFromOtherEcMemberStates == 0.00G
        vatReturn.vatReclaimedOnPurchases == 16.00G
        vatReturn.vatDueTotal == vatReturn.vatDueOnSales + vatReturn.vatDueOnAcquisitionsFromOtherEcMemberStates        
        vatReturn.vatNet == vatReturn.vatDueTotal - vatReturn.vatReclaimedOnPurchases
        vatReturn.totalValueOfSalesExVat == 446.00G        
        vatReturn.totalValueOfPurchasesExVat == 892.00G
        vatReturn.totalValueOfSuppliesExVatToOtherEcMemberStates == 0.00G        
        vatReturn.vatDueOnAcquisitionsFromOtherEcMemberStates == 0.00G
    }


    void someInvoices() {
        Amount amount1 = new AmountBuilder().net(100.00G).vat(17.50G).vatRate(rate1).build()
        new InvoiceBuilder().raised(beforePeriod).settled(periodStart).amount(amount1).buildAndSave()

        Amount amount2 = new AmountBuilder().net(50.00G).vat(7.50G).vatRate(rate2).build()
        new InvoiceBuilder().raised(beforePeriod).settled(periodStart).amount(amount2).buildAndSave()

        Amount amount3 = new AmountBuilder().net(200.00G).vat(30.00G).vatRate(rate2).build()
        new InvoiceBuilder().raised(beforePeriod).settled(periodStart).amount(amount3).buildAndSave()

        Amount amount4 = new AmountBuilder().net(96.17G).vat(0.00G).vatRate(rate3).build()
        new InvoiceBuilder().raised(beforePeriod).settled(periodStart).amount(amount4).buildAndSave()        
    }

    void someExpenses() {
        Amount amount1 = new AmountBuilder().net(200.00G).vat(35.00G).build()
        new ExpenseBuilder().incurred(periodStart).amount(amount1).buildAndSave()

        Amount amount2 = new AmountBuilder().net(100.00G).vat(15.00G).vatReclaimed(1.00G).build()
        new ExpenseBuilder().incurred(periodStart).amount(amount2).buildAndSave()

        Amount amount3 = new AmountBuilder().net(400.00G).vat(60.00G).vatReclaimed(15.00G).build()
        new ExpenseBuilder().incurred(periodStart).amount(amount3).buildAndSave()

        Amount amount4 = new AmountBuilder().net(192.34G).vat(0.00G).build()
        new ExpenseBuilder().incurred(periodStart).amount(amount4).buildAndSave()
    }
}
