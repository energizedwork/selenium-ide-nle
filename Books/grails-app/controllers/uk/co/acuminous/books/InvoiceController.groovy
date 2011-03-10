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

package uk.co.acuminous.books

class InvoiceController {

    BooksService booksService

    def tab = {
        render(template: 'tab')
    }

    def list = {
        Map model = [:]
        model.invoices = booksService.getInvoices(session.taxYear)
        render(template: 'tableData', model: model)
    }

    def dialog = {
        Map model = [:]
        model.invoice = params.id ? Invoice.get(params.id) : new Invoice()
        render(template: 'dialog', model: model)
    }

    def save = {
        Invoice invoice = params.id ? Invoice.get(params.id) : new Invoice()
        invoice.properties = params
        invoice.save(flush:true)
        render(template: 'dialog', model: [invoice: invoice])
    }

    def delete = {
        Invoice invoice = Invoice.get(params.id)
        invoice.delete(flush:true)
        forward(action: 'list')
    }


}
