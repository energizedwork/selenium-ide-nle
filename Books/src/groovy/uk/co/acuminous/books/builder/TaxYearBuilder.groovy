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

package uk.co.acuminous.books.builder

import org.apache.commons.lang.RandomStringUtils
import uk.co.acuminous.books.Invoice
import org.joda.time.LocalDate
import uk.co.acuminous.books.TaxYear

class TaxYearBuilder extends BooksEntityBuilder {

    TaxYearBuilder() {
        setFactory 'TaxYear.start', { def entity -> new LocalDate().minusMonths(1) }
        setFactory 'TaxYear.end', { def entity -> new LocalDate().plusMonths(1) }
    }

    Collection getAutoAssignedFieldNames() {
        ['start', 'end']
    }

    TaxYear build() {
        entity = new TaxYear()
        assignValues()
        return entity
    }

    TaxYear buildAndSave() {
        build()        
        save()
        return entity
    }
}
