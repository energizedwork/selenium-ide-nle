package uk.co.acuminous.books

import org.joda.time.DateTime

class TaxYear {

    DateTime start
    DateTime end

    static mapping = {
        start column: 'start_date'
        end column: 'end_date'
    }

    boolean equals(Object other) {
        if (other == null) {
            return false
        } else if (!TaxYear.isAssignableFrom(other.class)) {
             return false
        } else {
            TaxYear otherTaxYear = (TaxYear) other
            return this.start == otherTaxYear.start && this.end == otherTaxYear.end 
        }

    }
}
