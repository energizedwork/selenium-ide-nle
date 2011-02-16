<tbody class="data">
    <g:each var="vatReturn" in="${vatReturns}">
        <g:set var="start"><joda:format value="${vatReturn.start}" /></g:set>
        <g:set var="end"><joda:format value="${vatReturn.end}" /></g:set>        
        <tr class="selectable" onclick="$('#vatReturnsTable')[0].dialog('${vatReturn.id}');">
            <td class="date">${start}</td>
            <td class="date">${end}</td>
            <td class="currency"><booksui:vatReturnAmount bean="${vatReturn}" field="totalValueOfSalesExVat"/></td>
            <td class="currency"><booksui:vatReturnAmount bean="${vatReturn}" field="totalValueOfPurchasesExVat"/></td>
            <td class="currency"><booksui:vatReturnAmount bean="${vatReturn}" field="vatDueTotal"/></td>            
            <td class="icon"><booksui:deleteEntityIcon target="${vatReturn}" prompt="${g.message(code:"vatReturn.delete.confirm", args:[start, end])}" /></td>
        </tr>
    </g:each>
</tbody>