package uk.co.acuminous.books

import org.joda.time.LocalDate
import static java.math.RoundingMode.HALF_UP
import static java.math.RoundingMode.DOWN
import uk.co.acuminous.attachment.MetaAttachment

class VatReturn {

    LocalDate start
    LocalDate end

    BigDecimal vatDueOnSales = 0.00G
    BigDecimal vatDueOnAcquisitionsFromOtherEcMemberStates = 0.00G
    BigDecimal vatDueTotal = 0.00G
    BigDecimal vatReclaimedOnPurchases = 0.00G
    BigDecimal vatNet = 0.00G
    BigDecimal totalValueOfSalesExVat = 0.00G
    BigDecimal totalValueOfPurchasesExVat = 0.00G
    BigDecimal totalValueOfSuppliesExVatToOtherEcMemberStates = 0G
    BigDecimal totalValueOfAcquisitionsExVatFromOtherEcMemberStates = 0G

    List _invoices
    List _expenses
    Set<MetaAttachment> attachments    

    static mapping = {
        start column: 'start_date'
        end column: 'end_date'
        attachments cascade:"all,delete-orphan"
    }

    static hasMany = [attachments: MetaAttachment]

    static constraints = {
        end validator: { LocalDate end, VatReturn vatReturn ->
            if (end && vatReturn.start && end.isBefore(vatReturn.start)) {
                return 'before.start'
            }
        }
    }

    static transients = ['_invoices', '_expenses', 'financials']

    VatReturn forPeriod(LocalDate start, LocalDate end) {
        this.start = start
        this.end = end

        reset()        
        calculateVatDueOnSales()
        calculateVatTotal()
        calculateVatReclaimedOnPurchases()
        calculateVatNet()
        calculateTotalValueOfSalesExVat()
        calculateTotalValueOfPurchasesExVat()
        
        return this
    }

    void reset() {
        vatDueOnSales = 0.00G
        vatDueOnAcquisitionsFromOtherEcMemberStates = 0.00G
        vatDueTotal = 0.00G
        vatReclaimedOnPurchases = 0.00G
        vatNet = 0.00G
        totalValueOfSalesExVat = 0.00G
        totalValueOfPurchasesExVat = 0.00G
        totalValueOfSuppliesExVatToOtherEcMemberStates = 0.00G
        totalValueOfAcquisitionsExVatFromOtherEcMemberStates = 0.00G        
    }

    void calculateVatDueOnSales() {
        invoices.each { Invoice invoice ->
            Amount amount = invoice.amount
            BigDecimal vatDue = amount.gross * amount.vatRate.payable
            vatDueOnSales += vatDue.setScale(2, HALF_UP)
        }        
    }

    void calculateVatTotal() {
        vatDueTotal = vatDueOnSales + vatDueOnAcquisitionsFromOtherEcMemberStates
    }

    void calculateVatReclaimedOnPurchases() {
        expenses.each { Expense expense ->
            vatReclaimedOnPurchases += expense.amount.vatReclaimed
        }
    }

    void calculateVatNet() {
        vatNet = vatDueTotal - vatReclaimedOnPurchases
    }

    void calculateTotalValueOfSalesExVat() {
        invoices.each { Invoice invoice ->
            totalValueOfSalesExVat += invoice.amount.net
        }
        totalValueOfSalesExVat = totalValueOfSalesExVat.setScale(0, DOWN)
    }

    void calculateTotalValueOfPurchasesExVat() {
        expenses.each { Expense expense ->
            totalValueOfPurchasesExVat += expense.amount.net
        }
        totalValueOfPurchasesExVat = totalValueOfPurchasesExVat.setScale(0, DOWN)
    }

    Map getFinancials() {
        ['vatDueOnSales': vatDueOnSales,
         'vatDueOnAcquisitionsFromOtherEcMemberStates': vatDueOnAcquisitionsFromOtherEcMemberStates,
         'vatDueTotal': vatDueTotal,
         'vatReclaimedOnPurchases': vatReclaimedOnPurchases,
         'vatNet': vatNet,
         'totalValueOfSalesExVat': totalValueOfSalesExVat,
         'totalValueOfPurchasesExVat': totalValueOfPurchasesExVat,
         'totalValueOfSuppliesExVatToOtherEcMemberStates': totalValueOfSuppliesExVatToOtherEcMemberStates,
         'totalValueOfAcquisitionsExVatFromOtherEcMemberStates': totalValueOfAcquisitionsExVatFromOtherEcMemberStates]
    }

    List getInvoices() {
        if (_invoices == null) {
            _invoices = Invoice.withCriteria() {
                ge('settled', start)
                le('settled', end)
            }
        }
        return _invoices            
    }

    List getExpenses() {
        if (_expenses == null) {
            _expenses = Expense.withCriteria() {
                ge('incurred', start)
                le('incurred', end)
            }
        }
        return _expenses
    }

    String toString() {
        return "VatReturn[id:$id, start:$start, end:$end]"
    }
}