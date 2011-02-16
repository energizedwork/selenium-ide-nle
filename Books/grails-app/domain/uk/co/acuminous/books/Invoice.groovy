package uk.co.acuminous.books

import org.joda.time.LocalDate
import uk.co.acuminous.attachment.MetaAttachment

class Invoice {

    String reference
    String narrative
    LocalDate raised
    LocalDate settled
    Amount amount
    Set<MetaAttachment> attachments

    static hasMany = [attachments: MetaAttachment]

    static mapping = {
        attachments cascade:"all,delete-orphan"
    }    

    static constraints = {
        reference(blank: false, unique: true)
        narrative(nullable:true)
        raised(validator: { LocalDate raised, Invoice invoice ->
            if (raised.isAfter(new LocalDate())) {
                return 'afterToday'
            }
        })
        settled(nullable: true, validator: { LocalDate settled, Invoice invoice ->
            if (invoice.raised && settled?.isBefore(invoice.raised)) {
                return 'beforeRaised'
            } else if (settled && settled.isAfter(new LocalDate())) {
                return 'afterToday'
            }
        })
    }

    String toString() {
        return "Invoice[id:$id, reference:$reference, narrative:$narrative, raised:$raised, settled:$settled, amount:$amount]"
    }
}
