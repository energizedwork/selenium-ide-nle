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

import uk.co.acuminous.books.*
import org.joda.time.LocalDate
import uk.co.acuminous.test.EntityBuilder
import org.joda.time.DateTime

abstract class BooksEntityBuilder extends EntityBuilder {

    BooksEntityBuilder() {
        setFactory Amount, { def entity -> new AmountBuilder().build() }
        setFactory Expense, { def entity -> new ExpenseBuilder().build() }
        setFactory Invoice, { def entity -> new InvoiceBuilder().build() }
        setFactory VatRate, { def entity -> new VatRateBuilder().build() }
        setFactory LocalDate, { def entity -> new LocalDate() }
        setFactory DateTime, { def entity -> new DateTime() }
    }

}
