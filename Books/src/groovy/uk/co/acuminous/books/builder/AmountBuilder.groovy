/*
 * Copyright 2010 Stephen Mark Cresswell
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
