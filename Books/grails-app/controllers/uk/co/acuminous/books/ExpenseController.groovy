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