package uk.co.acuminous.books

import org.gmock.WithGMock
import grails.plugin.spock.TagLibSpec
import uk.co.acuminous.books.builder.InvoiceBuilder
import uk.co.acuminous.test.TestUtils
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.instanceOf
import groovy.util.slurpersupport.GPathResult
import uk.co.acuminous.attachment.MetaAttachment
import uk.co.acuminous.books.builder.MetaAttachmentBuilder
import org.hamcrest.Matcher
import static uk.co.acuminous.test.MessageMatcher.stubMessages

@WithGMock
@Mixin(TestUtils)
class MetaAttachmentsTagLibSpec extends TagLibSpec {

    def g
    def html

    void setup() {
        g = mock()        
        mock(tagLib).getG().returns(g).stub()
        mockDomains Invoice, Amount, MetaAttachment
        stubMessages(g)

        PluginManagerHolder.pluginManager = [hasGrailsPlugin: { String name -> true }] as GrailsPluginManager        
    }

    def cleanup() {
        PluginManagerHolder.pluginManager = null
    }

    def "Attachments icon has correct html"() {
        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()
        g.resource(mapMatcher([dir:'/images', file:'attachment.gif'])).returns '/foo.jpg'

        when:
        attachmentsIcon([target: invoice])

        then:
        html.img.@id == "toggle-attachments-${invoice.id}"
        html.img.@src == "/foo.jpg"
        html.script == "\$('#toggle-attachments-${invoice.id}').bind('click', Books.attachments.toggle);"
    }

    def "Table heading spans the entire column range"() {
        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()

        when:
        renderTableHeading([target: invoice, columns: columns], {})

        then:
        html.tr.th == 'code:metaAttachment.title'        
        html.tr.th.@colspan == colspan

        where:
        columns | colspan
        (5..9)  | '5'
        (1..9)  | '9'
    }

    def "Table heading is positioned using blank td elements"() {
        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()

        when:
        renderTableHeading([target: invoice, columns: columns], {})

        then:
        html.tr.children()[padding].name() == 'th'
        html.tr.td.size() == padding
        html.tr.td.each { GPathResult td ->
            assert td.text() == '_'
        }

        where:
        columns | padding
        (5..9)  | 4
        (1..9)  | 0      
    }

    def "Download section is rendered correctly"() {
        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()
        
        List metaAttachments = (1..3).collect {
            MetaAttachment ma = new MetaAttachmentBuilder().buildAndSave()
            g.link(linkMatcher('attachment', 'download', ma.id), instanceOf(Closure)).returns("link-${ma.id}")
            mock(tagLib).renderDeleteIcon([metaAttachment: ma]).returns "delete-${ma.id}"            
            return ma
        }

        when:
        renderDownloadSection([target: invoice, attachments: metaAttachments, columns: columns], {})

        then:
        html.tr.size() == 3
        html.tr.eachWithIndex { GPathResult tr, Integer index ->
            assert tr.td[filenameCell].@colspan.text() == colspan
            assert tr.td[filenameCell].text() == "link-${metaAttachments[index].id}"
            assert tr.td[deleteCell].text() == "delete-${metaAttachments[index].id}"
        }

        where:
        columns | colspan | filenameCell | deleteCell
        (1..3)  | '2'     | 0            | 1
        (5..9)  | '4'     | 4            | 5               
    }

    def "Attachment delete icons are rendered correctly"() {
        given:
        MetaAttachment ma = new MetaAttachmentBuilder().buildAndSave()
        g.createLink(linkMatcher('attachment', 'delete', ma.id)).returns "delete-${ma.id}"        
        g.resource(mapMatcher([dir:'/images/skin', file:'database_delete.png'])).returns '/foo.jpg'

        when:
        renderDeleteIcon([metaAttachment: ma], {})

        then:
        html.img.@id == "delete-attachment-${ma.id}"
        html.img.@src == '/foo.jpg'
        html.script == "\$('#delete-attachment-${ma.id}').bind('click', {url:'delete-${ma.id}', prompt:'code:metaAttachment.delete.confirm'}, Books.attachments.del);"
    }

    def "Attachment upload section spans the entire range excluding the delete column"() {
        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()
        g.render(renderMatcher('/fragments/fileUpload', [attachmentOwner:invoice])).returns('fileUpload')

        when:
        renderUploadSection([target: invoice, columns: columns], {})

        then:
        html.tr.@class.text().split(' ').find { it == "toggle-attachments-${invoice.id}" }        
        html.tr.td[uploadCell].@colspan == colspan
        html.tr.td[uploadCell] == 'fileUpload'

        where:
        columns | uploadCell | colspan
        (5..9)  | 4          | '5'
        (1..9)  | 0          | '9'
    }

    def "Attachment upload section is positioned using blank td elements"() {
        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()
        g.render(renderMatcher('/fragments/fileUpload', [attachmentOwner:invoice])).returns('fileUpload')

        when:
        renderUploadSection([target: invoice, columns: columns], {})

        then:
        html.tr.td.size() == padding + 1
        html.tr.td.eachWithIndex { GPathResult td, Integer index ->
            if (index != uploadCell) {
                td == '_'
            }
        }

        where:
        columns | padding | uploadCell
        (5..9)  | 4       | 4
        (1..9)  | 0       | 0      
    }

    def methodMissing(String name, args) {
        String result
        play {
            result = super.methodMissing(name, args)
        }
        println "\n$result"        
        html = new XmlSlurper().parseText("""<!DOCTYPE html [<!ENTITY nbsp "_">]>\n<results>${result}</results>""")
    }

    private Matcher linkMatcher(String controller, String action, Serializable id) {
        allOf([
            hasEntry('controller', controller),
            hasEntry('action', action),
            hasEntry('id', id)
        ])
    }

    private Matcher renderMatcher(String gsp, Map model) {
        allOf([
            hasEntry('template', gsp),
            hasEntry('model', mapMatcher(model))
        ])
    }

    private Matcher mapMatcher(Map map) {
        List entries = []
        map.each { k, v ->
            entries << hasEntry(k, v)
        }
        return allOf(entries)
    }

}
