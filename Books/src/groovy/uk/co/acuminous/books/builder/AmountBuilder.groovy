package uk.co.acuminous.books.builder

import uk.co.acuminous.books.Amount
import static java.math.RoundingMode.HALF_UP

class AmountBuilder extends BooksEntityBuilder {

    AmountBuilder() {
        setFactory 'Amount.net', { def entity -> new BigDecimal(Math.random() * 100.0G).setScale(2, HALF_UP) }
        setFactory 'Amount.vat', { def entity ->
            if (entity.net != null && entity.vatRate != null) {
                (entity.net * entity.vatRate.chargeable).setScale(2, HALF_UP)
            } else {
                new BigDecimal(Math.random() * 100.0G).setScale(2, HALF_UP)
            }
        }
        setFactory 'Amount.gross', { def entity ->
            if (entity.net != null && entity.vat != null) {
                entity.net + entity.vat
            } else {
                new BigDecimal(Math.random() * 100.0G).setScale(2, HALF_UP)
            }
        }
    }

    Collection getAutoAssignedFieldNames() {
        ['net', 'vat', 'gross']
    }

    Amount build() {
        entity = new Amount()
        assignValues()
        return entity
    }
}
