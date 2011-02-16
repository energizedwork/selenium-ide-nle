package uk.co.acuminous.books

import org.joda.time.LocalDate
import uk.co.acuminous.attachment.MetaAttachment
import static uk.co.acuminous.util.Normalizer.normalize

class Expense {

    String category
    String categoryIndex
    String narrative
    String narrativeIndex
    LocalDate incurred
    Amount amount = new Amount()
    Set<MetaAttachment> attachments

    static hasMany = [attachments: MetaAttachment]

    static mapping = {
        attachments cascade:"all,delete-orphan"
    }

    static constraints = {
        category(blank:false)
        categoryIndex(nullable:true)
        narrative(blank: false)
        narrativeIndex(nullable: true)
        incurred(validator: { LocalDate incurred, Expense expense ->
            if (incurred.isAfter(new LocalDate())) {
                return 'afterToday'
            }
        })
    }

    static List findAllNarrativesStartingWith(String text) {
        Expense.executeQuery(
            'select distinct(narrative) from Expense where narrativeIndex like :index order by narrative asc',
            [index: "${normalize(text)}%"]
        )
    }

    static List findAllCategoriesStartingWith(String text) {
        Expense.executeQuery(
            'select distinct(category) from Expense where categoryIndex like :index order by category asc',
            [index: "${normalize(text)}%"]
        )
    }

    def beforeInsert = {
        updateIndexes()
    }

    def beforeUpdate = {
        updateIndexes()
    }

    void updateIndexes() {
        narrativeIndex = normalize(narrative)
        categoryIndex = normalize(category)
    }    

    String toString() {
        return "Expense[id:$id, narrative:$narrative, incurred:$incurred, amount:$amount]"
    }
}