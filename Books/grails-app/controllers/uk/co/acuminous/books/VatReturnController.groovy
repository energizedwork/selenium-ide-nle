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
