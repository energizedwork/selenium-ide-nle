<g:if test="${rate?.hasErrors()}">
    <div class="errors"><g:renderErrors bean="${rate}" /></div>
</g:if>
<g:form name="rateForm" class="dialog" controller="vatRate" action="save">
    <booksui:hiddenField bean="${rate}" field="id"/>
    <table>
        <tbody class="data">
            <tr>
                <th><g:message code="vatRate.start"/></th>
                <td><booksui:datePicker bean="${rate}" field="start" /></td>
            </tr>
            <tr>
                <th><g:message code="vatRate.end"/></th>
                <td><booksui:datePicker bean="${rate}" field="end" /></td>
            </tr>
            <tr>
                <th><g:message code="vatRate.chargeable"/></th>
                <td><booksui:percentageField bean="${rate}" field="chargeable" maxlength="5"/></td>
            </tr>
            <tr>
                <th><g:message code="vatRate.payable"/></th>
                <td><booksui:percentageField bean="${rate}" field="payable" maxlength="5"/></td>
            </tr>        
        </tbody>
    </table>
</g:form>