<g:set var="tableId" value="vatReturnsTable"/>
<g:set var="dialogId" value="vatReturnsTableDialog"/>
<table id="${tableId}" class="list-data" cellspacing="0">
    <thead>
        <tr>
            <th class="date"><g:message code="vatReturn.start"/></th>
            <th class="date"><g:message code="vatReturn.end"/></th>
            <th class="currency"><g:message code="vatReturn.totalValueOfSalesExVat.short"/></th>
            <th class="currency"><g:message code="vatReturn.totalValueOfPurchasesExVat.short"/></th>
            <th class="currency"><g:message code="vatReturn.vatNet.short"/></th>            
            <th class="icon">&nbsp;</th>
        </tr>
    </thead>
    <g:render template="/invoice/tableData" />
    <tbody class="controls">
        <tr>
            <td colspan="8"><input class="selectable" type="button" value="Add" onclick="$('#${tableId}')[0].dialog()"/></td>
        </tr>
    </tbody>
</table>
<div id="${dialogId}"/>
<g:javascript>
    $(document).ready(function() {
        var crudTable = $('#${tableId}').crudify({
            refreshUrl: '${g.createLink(controller: 'vatReturn', action: 'list')}',
            dialogUrl: '${g.createLink(controller: 'vatReturn', action: 'dialog')}',
            deleteUrl: '${g.createLink(controller: 'vatReturn', action: 'delete')}',
            saveUrl: '${g.createLink(controller: 'vatReturn', action: 'save')}',
            hasErrors: function(html) {
                return html.indexOf("errors") >= 0;
            },
            dialogOptions: {
                title: 'VAT Return',
                autoOpen: false,
                modal: true,
                width: '35em'
            }
        });        
        crudTable.refresh();
    });
</g:javascript>
