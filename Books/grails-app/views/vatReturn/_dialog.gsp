<g:if test="${vatReturn?.hasErrors()}">
    <div class="errors"><g:renderErrors bean="${vatReturn}" /></div>
</g:if>
<g:form name="vatReturnsForm" class="dialog" controller="vatReturn" action="save">
    <booksui:hiddenField bean="${vatReturn}" field="id" />

    <table class="period">
        <tbody>
            <tr>
                <th><label for="start"><g:message code="vatReturn.start"/></label></th>
                <td><booksui:datePicker bean="${vatReturn}" field="start" /></td>
                <th><label for="end"><g:message code="vatReturn.end"/></label></th>
                <td><booksui:datePicker bean="${vatReturn}" field="end" /></td>
            </tr>
        </tbody>
    </table>

    <table class="financials" cellpadding="0" cellspacing="0">
        <tbody>
            <booksui:vatReturnRow bean="${vatReturn}" field="vatDueOnSales" number="1" />
            <booksui:vatReturnRow bean="${vatReturn}" field="vatDueOnAcquisitionsFromOtherEcMemberStates" number="2" />
            <booksui:vatReturnRow bean="${vatReturn}" field="vatDueTotal" number="3" />
            <booksui:vatReturnRow bean="${vatReturn}" field="vatReclaimedOnPurchases" number="4" />
            <booksui:vatReturnRow bean="${vatReturn}" field="vatNet" number="5" />
            <booksui:vatReturnRow bean="${vatReturn}" field="totalValueOfSalesExVat" number="6" />
            <booksui:vatReturnRow bean="${vatReturn}" field="totalValueOfPurchasesExVat" number="7" />
            <booksui:vatReturnRow bean="${vatReturn}" field="totalValueOfSuppliesExVatToOtherEcMemberStates" number="8" />
            <booksui:vatReturnRow bean="${vatReturn}" field="totalValueOfAcquisitionsExVatFromOtherEcMemberStates" number="9" />
        </tbody>
    </table>
</g:form>
<g:javascript>
    $(document).ready(function() {
        var form = $('#vatReturnsForm');
        $('#start, #end').change(onVatPeriodChange);

        function onVatPeriodChange() {
            var idField = $('#id');            
            var startField = $('#start');
            var endField = $('#end');

            if (startField.val() && endField.val()) {
                 $.ajax({
                    url: "${g.createLink(controller: 'vatReturn', action: 'calculate')}",
                    data: {
                        id: idField.val(),
                        start: startField.val(),
                        end: endField.val()
                    },
                    success: function(result) {                        
                        if (result.success) {                            
                            $.each(result.financials, function(fieldName, data) {
                                $('#' + fieldName).val(data.value);
                                $('#' + fieldName + '-display').text(data.text);
                            });
                        };
                    }
                });
            }
        }
    });
</g:javascript>