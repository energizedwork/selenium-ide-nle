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

package uk.co.acuminous.books.jbehave.step

import org.jbehave.core.annotations.Given
import uk.co.acuminous.books.Invoice
import org.joda.time.LocalDate
import uk.co.acuminous.books.builder.InvoiceBuilder
import uk.co.acuminous.books.builder.AmountBuilder
import uk.co.acuminous.books.Amount
import uk.co.acuminous.books.VatRate

class TestFixtures {

    Map context

    public TestFixtures(Map context) {
        this.context = context
    }


    @Given('invoice $reference was settled on $settled')
    public void setupAnInvoice(String reference, LocalDate settled) {
        Amount amount = new AmountBuilder().build()
        Invoice invoice = new InvoiceBuilder()
            .reference(reference)
            .amount(amount)
            .raised(settled.minusWeeks(2))
            .settled(settled)
            .buildAndSave()
        context.result = invoice
    }


    @Given('invoice $reference was raised on $raised and settled on $settled')
    public void setupAnInvoice(String reference, LocalDate raised, LocalDate settled) {
        Amount amount = new AmountBuilder().build()
        Invoice invoice = new InvoiceBuilder()
            .reference(reference)
            .amount(amount)
            .raised(raised)
            .settled(settled)
            .buildAndSave()
        context.result = invoice
    }

    @Given('an invoice $reference for $net @ $chargeable that was raised on $raised and settled on $settled')
    public void setupAnInvoice(String reference, BigDecimal net, BigDecimal vatPercentage, LocalDate raised, LocalDate settled) {
        VatRate vatRate = getVatRate(vatPercentage / 100, raised)
        Amount amount = new AmountBuilder().net(net).vatRate(vatRate).build()
        Invoice invoice = new InvoiceBuilder()
            .reference(reference)
            .amount(amount)
            .raised(raised)
            .settled(settled)
            .buildAndSave()
        context.result = invoice
    }

    private VatRate getVatRate(BigDecimal chargeable, LocalDate date) {
        VatRate.withCriteria(uniqueResult:true) {
            eq('chargeable', chargeable)
            le('start', date)
            or {
                ge('end', date)
                isNull('end')
            }

        }
    }
}
