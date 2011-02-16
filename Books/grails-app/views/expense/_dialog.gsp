<%@ page import="uk.co.acuminous.books.VatRate" %>
<g:if test="${expense?.hasErrors()}">
    <div class="errors"><g:renderErrors bean="${expense}" /></div>                
</g:if>
<g:form name="expensesForm" class="dialog" controller="expense" action="save">
    <booksui:hiddenField bean="${expense}" field="id"/>
    <table>
        <tbody class="data">                        
            <tr>
                <th><label for="incurred"/><g:message code="expense.incurred"/></label></th>
                <td><booksui:datePicker bean="${expense}" field="incurred" /></td>
            </tr>
            <tr>
                <th><label for="cateogory"/><g:message code="expense.category"/></label></th>
                <td><booksui:autoCompleter bean="${expense}" field="category" maxLength="255" source="'${g.createLink(controller:'expense', action:'suggestCategory')}'"/></td>
            </tr>        
            <tr>
                <th><label for="narrative"/><g:message code="expense.narrative"/></label></th>
                <td><booksui:autoCompleter bean="${expense}" field="narrative" maxLength="255" source="'${g.createLink(controller:'expense', action:'suggestNarrative')}'"/></td>
            </tr>
            <tr>
                <th><label for="amount-net"/><g:message code="amount.net"/></label></th>
                <td><booksui:currencyField bean="${expense}" field="amount.net" /></td>
            </tr>
            <tr>
                <th><label for="amount-vat"/><g:message code="amount.vat"/></label></th>
                <td><booksui:currencyField bean="${expense}" field="amount.vat"/></td>
            </tr>
            <tr>
                <th><label for="amount-gross"/><g:message code="amount.gross"/></label></th>
                <td><booksui:currencyField bean="${expense}" field="amount.gross"/></td>
            </tr>
            <tr>
                <th><label for="amount-vatReclaimed"/><g:message code="amount.vatReclaimed"/></label></th>
                <td><booksui:currencyField bean="${expense}" field="amount.vatReclaimed"/></td>
            </tr>        
        </tbody>
    </table>
</g:form>
<g:javascript>
    $(document).ready(function() {
        $('#expensesForm').vatify({
            baseId: 'amount',
            dateField: '#incurred',
            vatPickerUrl: '${g.createLink(controller:"vatRate", action:"vatPicker")}',
            rateId: '${g.fieldValue(bean:invoice, field:"amount.vatRate.id")}'
        });       
    });
</g:javascript>