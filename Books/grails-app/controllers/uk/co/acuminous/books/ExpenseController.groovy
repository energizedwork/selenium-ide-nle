package uk.co.acuminous.books

import grails.converters.JSON

class ExpenseController {

    BooksService booksService

    def tab = {
        render(template: 'tab')
    }
    
    def list = {
        Map model = [:]
        model.expenses = booksService.getExpenses(session.taxYear)
        render(template: 'tableData', model: model)
    }

    def dialog = {
        Map model = [:]
        model.expense = params.id ? Expense.get(params.id) : new Expense()        
        render(template: 'dialog', model: model)
    }

    def save = {
        Expense expense = params.id ? Expense.get(params.id) : new Expense()
        expense.properties = params
        expense.save(flush:true)

        render(template: 'dialog', model: [expense: expense])
    }

    def delete = {
        Expense expense = Expense.get(params.id)
        expense.delete(flush:true)
        forward(action: 'list')
    }

    def suggestNarrative = {
        render Expense.findAllNarrativesStartingWith(params.term) as JSON
    }

    def suggestCategory = {
        render Expense.findAllCategoriesStartingWith(params.term) as JSON
    }
}