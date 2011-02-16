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
