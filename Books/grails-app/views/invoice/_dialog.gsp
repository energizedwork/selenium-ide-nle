<g:if test="${invoice?.hasErrors()}">
    <div class="errors"><g:renderErrors bean="${invoice}" /></div>                
</g:if>
<g:form name="invoiceForm" class="dialog" controller="invoice" action="save">
    <booksui:hiddenField bean="${invoice}" field="id"/>
    <table>
        <tbody class="data">
            <tr>
                <th><label for="raised"><g:message code="invoice.raised"/></label></th>
                <td><booksui:datePicker bean="${invoice}" field="raised" /></td>
            </tr>
            <tr>
                <th><label for="settled"><g:message code="invoice.settled" /></label></th>
                <td><booksui:datePicker bean="${invoice}" field="settled" /></td>
            </tr>
            <tr>
                <th><label for="reference"><g:message code="invoice.reference"/></label></th>
                <td><booksui:textField bean="${invoice}" field="reference" maxlength="255"/></td>
            </tr>
            <tr>
                <th><label for="narrative"><g:message code="invoice.narrative"/></label></th>
                <td><booksui:textField bean="${invoice}" field="narrative" maxlength="255"/></td>
            </tr>
            <tr>
                <th><label for="amount-net"><g:message code="amount.net"/></label></th>
                <td><booksui:currencyField bean="${invoice}" field="amount.net" /></td>
            </tr>
            <tr>
                <th><label for="amount-vat"><g:message code="amount.vat"/></label></th>
                <td><booksui:currencyField bean="${invoice}" field="amount.vat"/></td>
            </tr>
            <tr>
                <th><label for="amount-gross"><g:message code="amount.gross"/></label></th>
                <td><booksui:currencyField bean="${invoice}" field="amount.gross"/></td>
            </tr>                           
        </tbody>
    </table>
</g:form>
<g:javascript>
    $(document).ready(function() {
        $('#invoiceForm').vatify({
            baseId: 'amount',
            dateField: '#raised',
            vatPickerUrl: '${g.createLink(controller:"vatRate", action:"vatPicker")}',
            rateId: '${g.fieldValue(bean:invoice, field:"amount.vatRate.id")}'
        });
    });
</g:javascript>