<g:set var="tableId" value="expensesTable" />
<g:set var="dialogId" value="expensesTableDialog" />
<table id="${tableId}" class="list-data" cellspacing="0">
    <thead>
        <tr>
            <th class="date"><g:message code="expense.incurred"/></th>
            <th class="narrative"><g:message code="expense.narrative"/></th>
            <th class="category"><g:message code="expense.category"/></th>            
            <th class="currency"><g:message code="amount.net"/></th>
            <th class="currency"><g:message code="amount.vat"/></th>
            <th class="currency"><g:message code="amount.gross"/></th>
            <th class="currency vatReclaimed"><g:message code="amount.vatReclaimed"/></th>            
            <th class="icon">&nbsp;</th>
            <th class="icon">&nbsp;</th>            
        </tr>
    </thead>
    <g:render template="/expense/tableData" />
    <tbody class="controls">
        <tr>
            <td colspan="11"><input class="selectable" type="button" value="Add" onclick="$('#${tableId}')[0].dialog()"/></td>
        </tr>
    </tbody>
</table>
<div id="${dialogId}"/>
<g:javascript>
    $(document).ready(function() {
        var crudTable = $('#${tableId}').crudify({
            refreshUrl: '${g.createLink(controller: 'expense', action: 'list')}',
            dialogUrl: '${g.createLink(controller: 'expense', action: 'dialog')}',
            deleteUrl: '${g.createLink(controller: 'expense', action: 'delete')}',
            saveUrl: '${g.createLink(controller: 'expense', action: 'save')}',
            hasErrors: function(html) {
                return html.indexOf("errors") >= 0;
            },
            dialogOptions: {
                title: 'Expense',
                autoOpen: false,
                modal: true,
                width: '44em'
            }
        });        
        crudTable.refresh();
    });
</g:javascript>
