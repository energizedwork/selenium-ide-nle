 <tbody class="data">
    <g:each var="expense" in="${expenses}">
        <tr class="selectable" onclick="$('#expensesTable')[0].dialog('${expense.id}');">
            <td class="date"><joda:format value="${expense.incurred}" /></td>
            <td class="narrative"><g:fieldValue bean="${expense}" field="narrative" /></td>
            <td class="category"><g:fieldValue bean="${expense}" field="category" /></td>            
            <td class="currency"><g:formatNumber number="${expense.amount.net}" type="currency" /></td>
            <td class="currency"><g:formatNumber number="${expense.amount.vat}" type="currency" /></td>
            <td class="currency"><g:formatNumber number="${expense.amount.gross}" type="currency" /></td>
            <td class="currency"><g:formatNumber number="${expense.amount.vatReclaimed}" type="currency" /></td>
            <td class="icon"><ma:attachmentsIcon target="${expense}" /></td>
            <td class="icon"><booksui:deleteEntityIcon target="${expense}" prompt="${g.message(code:'expense.delete.confirm', args:[expense.amount.gross])}" /></td>
        </tr>
        <ma:attachmentsTable target="${expense}" attachments="${expense.attachments}" columns="${(6..9)}" />
    </g:each>
    <g:javascript>
        Books.hideOrShowAttachments()
    </g:javascript>
</tbody>