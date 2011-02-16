<g:set var="tableId" value="attachmentsTable"/>
<g:set var="dialogId" value="attachmentsTableDialog"/>
<table id="${tableId}" class="attachments list-data" cellspacing="0">
    <thead>
        <tr>
            <th class="dateTime"><g:message code="attachment.created"/></th>
            <th class="url"><g:message code="attachment.url"/></th>            
            <th class="icon">&nbsp;</th>            
        </tr>
    </thead>
    <g:render template="/invoice/tableData" />
    <tbody class="controls">
        <tr>
            <td colspan="3">
                <g:form controller="attachment" action="upload" method="post" enctype="multipart/form-data">
                    <input type="file" name="file"/>
                    <g:hiddenField  name="ownerId" value="${entity?.id}" />
                    <g:hiddenField name="ownerClass" value="${entity?.class?.name}" />
                    <g:hiddenField name="ownerField" value="${field}" />
                    <g:submitButton name="upload" value="Add"/>
                </g:form>
            </td>
        </tr>
    </tbody>
</table>
<g:javascript>
    $(document).ready(function() {
        var crudTable = $('#${tableId}').crudify({
            refreshUrl: '${g.createLink(controller: 'attachment', action: 'list')}',
            dialogUrl: '${g.createLink(controller: 'attachment', action: 'download')}',
            deleteUrl: '${g.createLink(controller: 'attachment', action: 'delete')}',
            hasErrors: function(html) {
                return html.indexOf("errors") >= 0;
            },
            dialogOptions: {
            }
        });        
        crudTable.refresh();
    });
</g:javascript>
