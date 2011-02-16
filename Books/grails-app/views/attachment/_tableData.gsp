<tbody class="data">
    <g:each var="attachment" in="${attachments}">
        <tr id="tr-attachment-${attachment.id}" class="selectable">
            <td class="dateTime"><joda:format value="${attachment.created}" /></td>
            <td class="url"><g:createLink controller="attachment" action="download" id="${attachment.id}" /></td>
            <td class="icon"><img id="delete-attachment-${attachment.id}" src="/images/skin/database_delete.png" />
                <g:javascript>
                    $('#delete-attachment-${attachment.id}').click(function(e) {
                        var cancellationMessage = '${g.message(code:"attachment.delete.confirm", args:[attachment.url])}';
                        $(this).parents('.crudTable')[0].del('${attachment.id}', cancellationMessage);
                        e.stopPropagation();
                    });
                </g:javascript>
            </td>
        </tr>
        <g:javascript>
            $('#tr-attachment-${attachment.id}').click(function(e) {
                document.location = '${g.createLink(controller: 'attachment', action: 'download')}${attachment.url}';
                e.stopPropagation();
            });
        </g:javascript>
    </g:each>
</tbody>