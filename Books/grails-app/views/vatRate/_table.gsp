<g:set var="tableId" value="ratesTable"/>
<g:set var="dialogId" value="ratesTableDialog"/>
<table id="${tableId}" class="list-data" cellspacing="0">
    <thead>
        <tr>
            <th class="ddmmyyyy"><g:message code="vatRate.start"/></th>
            <th class="ddmmyyyy"><g:message code="vatRate.end"/></th>
            <th class="decimal"><g:message code="vatRate.chargeable"/></th>
            <th class="decimal"><g:message code="vatRate.payable"/></th>            
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
            refreshUrl: '${g.createLink(controller: 'vatRate', action: 'list')}',
            dialogUrl: '${g.createLink(controller: 'vatRate', action: 'dialog')}',
            deleteUrl: '${g.createLink(controller: 'vatRate', action: 'delete')}',
            saveUrl: '${g.createLink(controller: 'vatRate', action: 'save')}',
            hasErrors: function(html) {
                return html.indexOf("errors") >= 0;
            },
            dialogOptions: {title: 'VAT Rate', autoOpen: false, modal: true, width: '44em'}
        });        
        crudTable.refresh();
    });
</g:javascript>
