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