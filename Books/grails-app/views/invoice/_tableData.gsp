<tbody class="data">
    <g:each var="invoice" in="${invoices}">
        <tr class="selectable" onclick="$('#invoicesTable')[0].dialog('${invoice.id}');">
            <td class="date"><joda:format value="${invoice.raised}" /></td>
            <td class="date"><g:if test="${invoice.settled}"><joda:format value="${invoice.settled}"/></g:if></td>
            <td class="reference"><g:fieldValue bean="${invoice}" field="reference" /></td>
            <td class="currency"><g:formatNumber number="${invoice.amount.net}" type="currency" /></td>
            <td class="currency"><g:formatNumber number="${invoice.amount.vat}" type="currency" /></td>
            <td class="currency"><g:formatNumber number="${invoice.amount.gross}" type="currency" /></td>
            <td class="icon"><ma:attachmentsIcon target="${invoice}" /></td>
            <td class="icon"><booksui:deleteEntityIcon target="${invoice}" prompt="${g.message(code:'invoice.delete.confirm', args:[invoice.reference])}" /></td>
        </tr>
        <ma:attachmentsTable target="${invoice}" attachments="${invoice.attachments}" columns="${(5..8)}" />
    </g:each>
    <g:javascript>
        Books.hideOrShowAttachments()
    </g:javascript>
</tbody>