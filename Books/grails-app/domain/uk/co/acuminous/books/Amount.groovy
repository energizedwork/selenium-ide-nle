package uk.co.acuminous.books

class Amount {

    BigDecimal net
    BigDecimal vat
    VatRate vatRate
    BigDecimal gross
    BigDecimal vatReclaimed = 0.00G

    static belongsTo = [Invoice, Expense]

    static constraints = {
        gross(validator: { BigDecimal gross, Amount amount ->
            if (amount.net != null && amount.vat != null && gross != amount.net + amount.vat) {
                return 'vatcheck'
            }
        })
        vatRate(nullable: true)

    }

    String toString() {
        return "${this.class.simpleName}[id:$id, net:$net, vat:$vat, gross:$gross, vatRate:$vatRate, vatReclaimed:$vatReclaimed]"
    }
}
