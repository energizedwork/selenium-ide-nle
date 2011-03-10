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

class Amount {

    BigDecimal net
    BigDecimal vat
    VatRate vatRate
    BigDecimal gross
    BigDecimal vatReclaimed = 0.00G

    static belongsTo = [Invoice, Expense]

    static constraints = {
        gross(validator: { BigDecimal gross, Amount amount ->
            if (amount.net != null && amount.vat != null && gross != amount.net + amount.vat) {
                return 'vatcheck'
            }
        })
        vatRate(nullable: true)

    }

    String toString() {
        return "${this.class.simpleName}[id:$id, net:$net, vat:$vat, gross:$gross, vatRate:$vatRate, vatReclaimed:$vatReclaimed]"
    }
}
