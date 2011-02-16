package uk.co.acuminous.books.builder

import java.math.RoundingMode
import uk.co.acuminous.books.VatRate

class VatRateBuilder extends BooksEntityBuilder {

    VatRateBuilder() {
        setFactory 'VatRate.chargeable', { def entity -> new BigDecimal(Math.random()).setScale(3, RoundingMode.HALF_UP) }
        setFactory 'VatRate.payable', { def entity -> new BigDecimal(Math.random()).setScale(3, RoundingMode.HALF_UP) }
    }

    Collection getAutoAssignedFieldNames() {
        ['start', 'chargeable', 'payable']
    }

    VatRate build() {
        entity = new VatRate()
        assignValues()
        return entity
    }
}