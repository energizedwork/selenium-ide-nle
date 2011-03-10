/*
 * Copyright 2010 Acuminous Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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