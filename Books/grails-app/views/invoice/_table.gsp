<g:set var="tableId" value="invoicesTable"/>
<g:set var="dialogId" value="invoicesTableDialog"/>
<table id="${tableId}" class="list-data" cellspacing="0">
    <thead>
        <tr>
            <th class="date"><g:message code="invoice.raised"/></th>
            <th class="date"><g:message code="invoice.settled"/></th>
            <th class="reference"><g:message code="invoice.reference"/></th>
            <th class="currency"><g:message code="amount.net"/></th>
            <th class="currency"><g:message code="amount.vat"/></th>
            <th class="currency"><g:message code="amount.gross"/></th>
            <th class="icon">&nbsp;</th>            
            <th class="icon">&nbsp;</th>            
        </tr>
    </thead>
    <g:render template="/invoice/tableData" />
    <tbody class="controls">
        <tr>
            <td colspan="10"><input class="selectable" type="button" value="Add" onclick="$('#${tableId}')[0].dialog()"/></td>
        </tr>
    </tbody>
</table>
<div id="${dialogId}"/>
<g:javascript>
    $(document).ready(function() {
        var crudTable = $('#${tableId}').crudify({
            refreshUrl: '${g.createLink(controller: 'invoice', action: 'list')}',
            dialogUrl: '${g.createLink(controller: 'invoice', action: 'dialog')}',
            deleteUrl: '${g.createLink(controller: 'invoice', action: 'delete')}',
            saveUrl: '${g.createLink(controller: 'invoice', action: 'save')}',
            hasErrors: function(html) {
                return html.indexOf("errors") >= 0;
            },
            dialogOptions: {
                title: 'Invoice',
                autoOpen: false,
                modal: true,
                width: '44em'
            }
        });        
        crudTable.refresh();
    });
</g:javascript>
