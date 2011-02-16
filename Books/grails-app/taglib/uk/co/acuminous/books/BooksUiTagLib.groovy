package uk.co.acuminous.books

import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.converters.JSON
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import static uk.co.acuminous.books.utils.BooksUtils.*

class BooksUiTagLib {

    static namespace = 'booksui'
    BooksService booksService

    GrailsApplication grailsApplication

    def tab = { Map attrs, def body ->
        String title = g.message(code: attrs.title)
        String url = g.createLink(controller: attrs.controller, action: 'tab')

        new MarkupBuilder(out).li {
            a(title: title, href: url) {
                mkp.yield title
            }
        }
    }

    def tabContents = { Map attrs, def body ->
        String title = g.message(code: attrs.title)
        String id = title.replaceAll(' ', '_')
        new MarkupBuilder(out).div(id: id, class:"tabContent ui-tabs-hide") {
            mkp.yield ''
        }
    }

    def hiddenField = { Map attrs, def body ->
        processInputFieldAttributes(attrs)
        out << g.hiddenField(attrs, body)
    }

    def textField = { Map attrs, def body ->
        processInputFieldAttributes(attrs)
        out << g.textField(attrs, body)
    }

    def textArea = { Map attrs, def body ->
        processInputFieldAttributes(attrs)
        out << g.textField(attrs, body)
    }

    def decoratedField = { Map attrs, def body ->
        String valueForDisplay = attrs.remove('valueForDisplay') ?: ''
        out << hiddenField(attrs)

        String displayId = attrs.id + '-display'
        new MarkupBuilder(out).div(id: displayId, class: "decoratedField") {
            mkp.yield valueForDisplay
        }
    }

    def autoCompleter = { Map attrs, def body ->
        out << textField(attrs, body)
        String nameValuePairs = subMap(['source', 'minLength', 'delay'], attrs).collect { String name, String value ->
            "${name}: ${value}"
        }.join(',')
        
        new MarkupBuilder(out).script(type:"text/javascript") {
            mkp.yield "\$('#${attrs.id}').autocomplete({ ${nameValuePairs} })"
        }
    }

    def currencyField = { Map attrs, def body ->
        if (!attrs.bean.errors?.hasFieldErrors(attrs.field)) {
            attrs.value = g.formatNumber(number: getPropertyValue(attrs), type: 'currency', currencySymbol: '')
        }
        processInputFieldAttributes(attrs)
        out << g.textField(attrs, body)
    }

    def percentageField = { Map attrs, def body ->
        if (!attrs.bean.errors?.hasFieldErrors(attrs.field)) {
            attrs.value = g.formatNumber(number: getPropertyValue(attrs), format:"#.#%")
        }
        processInputFieldAttributes(attrs)
        out << g.textField(attrs, body)
    }

    def vatPicker = { Map attrs, def body ->
        List rates = attrs.remove('rates')
        attrs.from = [:]
        rates.each { VatRate rate ->
            String text = g.formatNumber(number:rate.chargeable, format:"#.#%")
            attrs.from[text] = rate.id
        }

        Map jsonData = [:]
        rates.each { VatRate rate ->
            jsonData[rate.id] = rate.chargeable
        }

        attrs.name = "${attrs.field + '.id'}"
        attrs['class'] = attrs.containsKey('class') ? "${attrs['class']} vatRate" : 'vatRate'
        out << select(attrs, body)

        new MarkupBuilder(out).script(type:"text/javascript") {
            mkp.yield "\$('#${attrs.id}')[0].rates = ${jsonData as JSON};"
        }
    }

    def vatReturnAmount = { Map attrs, def body ->
        BigDecimal amount = attrs.containsKey('value') ? attrs.value : getPropertyValue(attrs)
        if (amount == 0G) {
            out << g.message(code: 'vatReturn.none')
        } else {
            out << g.formatNumber(number: amount, type: 'currency', currencySymbol: '')
        }
    }

    def vatReturnRow = { Map attrs, def body ->
        String rowId = attrs.field + '-row'
        new MarkupBuilder(out).tr(id:rowId) {
            th {
                div(class:'wrapper') {
                    div(class:'text') {
                        mkp.yield g.message(code:"vatReturn.${attrs.field}")
                    }
                    div(class:'number') {
                        mkp.yield attrs.number
                    }
                }
            }
            td {
                String text = booksui.vatReturnAmount(bean: attrs.bean, field: attrs.field)
                BigDecimal value = getPropertyValue(attrs)
                mkp.yieldUnescaped booksui.decoratedField(bean: attrs.bean, field: attrs.field, value: value, valueForDisplay: text)
            }
        }
    }


    def deleteEntityIcon = { Map attrs, def body ->
        String targetId = attrs.target.id
        String iconId = "delete-${targetId}"
        String imgSrc = g.resource(dir:'/images/skin', file:'database_delete.png')

        MarkupBuilder builder = new MarkupBuilder(out)
        builder.img(id:iconId, src: imgSrc, class:'selectable')
        builder.script(type:'text/javascript') {
            mkp.yield "\$('#$iconId').bind('click', {id:'${targetId}', prompt: '${attrs.prompt}'}, Books.deleteIcon.onClick);"
        }
    }
    
    def datePicker = { Map attrs, def body ->
        processInputFieldAttributes(attrs)
        
        out << g.textField(attrs)

        out << g.javascript([:], {
           ("\$('#${attrs.id}').datepicker({dateFormat: '${datePickerFormat}'});")
        })
    }

    def select = { Map attrs, def body ->
        processInputFieldAttributes(attrs)
        if (attrs.from instanceof Map) {
            applyDefault('optionValue', 'key', attrs)
            applyDefault('optionKey', 'value', attrs)
        }

        out << g.select(attrs)
    }

    def fieldValue = { Map attrs, def body ->
        out << getFieldValue(attrs)
    }

    private Object processInputFieldAttributes(Map attrs) {
        applyDefault('value', getFieldValue(attrs), attrs)
        applyDefault('name', attrs.field, attrs)
        applyDefault('id', getId(attrs.name), attrs)

        attrs.remove('default')
        attrs.remove('bean')
        attrs.remove('field')
    }

    private String getFieldValue(Map attrs) {

        boolean hasValue = getPropertyValue(attrs) != null
        boolean isSaved = attrs.bean?.id != null
        boolean hasDefault = attrs.containsKey('default')

        String value = ''        
        if (hasValue || isSaved) {
            value = g.fieldValue(bean: attrs.bean, field: attrs.field)
        } else if (hasDefault) {
            value = attrs.default
        }
        
        return value
    }

    private def getPropertyValue(Map attrs) {
        def bean = attrs.bean
        def value
        attrs.field.split(/\./).each { String propertyName ->
            if (bean != null) {
                bean = bean."${propertyName}"
            }
            value = bean
        }
        return value
    }

    private void applyDefault(String name, def value, Map attrs) {
        if (!attrs.containsKey(name)) {
            attrs[name] = value
        }
    }

    private Map subMap(List names, Map source, Map dest = [:]) {
        names.each { String name ->
            if (source.containsKey(name)) {
                dest[name] = source[name]
            }
        }
        return dest
    }

    private String getId(String text) {
        return sanitiseNonWordCharacters(stripTrailingId(text))        
    }

    private String stripTrailingId(String text) {
        return text.replaceAll(/\.id$/, '')
    }

    private String sanitiseNonWordCharacters(String text) {
        return text.replaceAll(/[\W]+/, '-')
    }
}
