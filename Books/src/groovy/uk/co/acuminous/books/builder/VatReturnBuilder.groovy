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

import uk.co.acuminous.books.VatReturn
import org.joda.time.LocalDate

class VatReturnBuilder extends BooksEntityBuilder {

    VatReturnBuilder() {
        setFactory 'VatReturn.start', { def entity -> new LocalDate().withDayOfMonth(1) }
        setFactory 'VatReturn.end', { def entity -> entity.start.plusMonths(3).minusDays(1) }
    }

    Collection getAutoAssignedFieldNames() {
        ['start', 'end']
    }

    VatReturn build() {
        entity = new VatReturn()
        assignValues()
        return entity
    }
}