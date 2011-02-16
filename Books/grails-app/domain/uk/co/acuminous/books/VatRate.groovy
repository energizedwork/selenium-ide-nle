package uk.co.acuminous.books

import org.joda.time.LocalDate

class VatRate {

    LocalDate start
    LocalDate end
    BigDecimal chargeable
    BigDecimal payable

    static mapping = {
        start column: 'start_date'
        end column: 'end_date'
    }

    static constraints = {
        end nullable:true, validator: { LocalDate end, VatRate rate ->
            if (end && rate.start && end.isBefore(rate.start)) {
                return 'before.start'
            }
        }
        chargeable scale: 3
        payable scale: 3
    }

    String toString() {
        return "VatRate[id:$id, start:$start, end:$end, chargeable:$chargeable, payable:$payable]"
    }
}