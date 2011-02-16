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