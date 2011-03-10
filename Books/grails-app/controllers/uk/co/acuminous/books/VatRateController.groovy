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

class VatRateController {

    BooksService booksService

    def tab = {
        render(template: 'tab')
    }

    def list = {
        Map model = [:]
        model.rates = VatRate.list().sort { VatRate r1, VatRate r2 -> r2.start <=> r1.start ?: r2.chargeable <=> r1.chargeable }
        render(template: 'tableData', model: model)
    }

    def dialog = {
        Map model = [:]
        model.rate = params.id ? VatRate.get(params.id) : new VatRate()
        render(template: 'dialog', model: model)
    }

    def save = {
        VatRate rate = params.id ? VatRate.get(params.id) : new VatRate()
        rate.properties = params
        rate.save(flush:true)
        println rate.errors
        render(template: 'dialog', model: [rate: rate])
    }

    def delete = {
        VatRate rate = VatRate.get(params.id)
        rate?.delete(flush:true)
        forward(action: 'list')
    }

    def vatPicker = { VatPickerCmd cmd ->
        List rates = booksService.getVatRates(cmd.when)
        render booksui.vatPicker([value: cmd.selected, field: "amount.vatRate", rates:rates, noSelection:['null':'N/A']])
    }
}


class VatPickerCmd {
    LocalDate when
    Long selected    
}