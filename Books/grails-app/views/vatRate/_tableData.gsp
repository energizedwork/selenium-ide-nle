<tbody class="data">
    <g:each var="rate" in="${rates}">
        <g:set var="rateDescription"><g:message code="vatRate.description" args="[rate.chargeable, rate.payable]" /></g:set>
        <tr class="selectable" onclick="$('#ratesTable')[0].dialog('${rate.id}');">
            <td class="date"><joda:format value="${rate.start}"/></td>
            <td class="date"><g:if test="${rate.end}"><joda:format value="${rate.end}"/></g:if><g:else>&nbsp;</g:else> </td>
            <td class="percentage"><g:formatNumber number="${rate.chargeable}" format="#.#%" /></td>
            <td class="percentage"><g:formatNumber number="${rate.payable}" format="#.#%" /></td>
            <td class="icon"><booksui:deleteEntityIcon target="${rate}" prompt="${g.message(code:'vatRate.delete.confirm', args:[rateDescription])}" /></td>
        </tr>
    </g:each>
</tbody>