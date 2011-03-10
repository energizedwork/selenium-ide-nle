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

import org.joda.time.LocalDate

class VatReturnController {

    BooksService booksService

    def tab = {
        render(template: 'tab')
    }

    def list = {
        List vatReturns = booksService.getVatReturns(session.taxYear)
        Map model = [vatReturns: vatReturns]
        render(template: 'tableData', model: model)
    }

    def dialog = {
        Map model = [:]
        model.vatReturn = params.id ? VatReturn.get(params.id) : new VatReturn()
        render(template: 'dialog', model: model)
    }

    def calculate = { CalculateVatReturnCmd cmd ->
        VatReturn vatReturn = new VatReturn().forPeriod(cmd.start, cmd.end)
        Map data = [
            success: true,
            financials: getFinancials(vatReturn)
        ]
        SafeJsonConverter.render(data, response)
    }

    def save = {
        VatReturn vatReturn = params.id ? VatReturn.get(params.id) : new VatReturn()
        vatReturn.properties = params
        vatReturn.save(flush:true)
        render(template: 'dialog', model: [vatReturn: vatReturn])
    }

    def delete = {
        VatReturn vatReturn = VatReturn.get(params.id)
        vatReturn?.delete(flush:true)
        forward(action: 'list')
    }

    Map getFinancials(VatReturn vatReturn) {
        Map financials = [:]
        vatReturn.financials.each { String fieldName, BigDecimal value ->
            financials[fieldName] = [text: booksui.vatReturnAmount(value: value), value: value]
        }
        return financials
    }
}

class CalculateVatReturnCmd {
    Long id
    LocalDate start
    LocalDate end
}

class SafeJsonConverter extends grails.converters.JSON {

    SafeJsonConverter(target) {
        super(target)
    }

    static void render(target, response) {
        SafeJsonConverter jsonConverter = new SafeJsonConverter(target)
        jsonConverter.render(response)
    }

    void value(Object o) {
        if (o == null || o.class == null) {
            super.value(o)
        } else if (o.class.isAssignableFrom(BigDecimal)) {
            value(o.toString())
        } else {
            super.value(o);
        }
    }
}
