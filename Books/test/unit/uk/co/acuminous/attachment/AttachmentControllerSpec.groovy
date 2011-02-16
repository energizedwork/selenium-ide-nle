package uk.co.acuminous.attachment

import grails.plugin.spock.ControllerSpec
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.gmock.WithGMock
import uk.co.acuminous.books.builder.MetaAttachmentBuilder
import uk.co.acuminous.books.Invoice
import uk.co.acuminous.test.TestUtils
import uk.co.acuminous.books.Amount
import org.springframework.web.multipart.commons.CommonsMultipartFile

import org.springframework.mock.web.MockHttpServletResponse
import uk.co.acuminous.books.builder.VatReturnBuilder
import uk.co.acuminous.books.VatReturn
import uk.co.acuminous.books.builder.InvoiceBuilder

@WithGMock
@Mixin(TestUtils)
class AttachmentControllerSpec extends ControllerSpec {

    def mockGrailsTagLib

    def setup() {
        PluginManagerHolder.pluginManager = [hasGrailsPlugin: { String name -> true }] as GrailsPluginManager        
        mockDomains MetaAttachment, Invoice, Amount

        mockGrailsTagLib = mock()
        mock(controller).getG().returns(mockGrailsTagLib).stub()
    }

    def cleanup() {
        PluginManagerHolder.pluginManager = null        
    }

    def "Index action uses the correct view"() {
        when:
        controller.index()

        then:
        renderArgs.view == 'index'
    }

    def "List action uses the correct template"() {
        when:
        controller.list()

        then:
        renderArgs.template == 'tableData'
    }

    def "List action adds all attachments to the model"() {
        given:
        MetaAttachment attachment1 = new MetaAttachmentBuilder().buildAndSave()
        MetaAttachment attachment2 = new MetaAttachmentBuilder().buildAndSave()
        MetaAttachment attachment3 = new MetaAttachmentBuilder().buildAndSave()

        when:
        controller.list()

        then:
        modelAttachments.size() == 3
        modelAttachments.containsAll([attachment1, attachment2, attachment3])
    }

    def "List action orders attachments by owner id and original filename"() {
        given:
        MetaAttachment attachment1 = new MetaAttachmentBuilder().ownerId('B').buildAndSave()
        MetaAttachment attachment2 = new MetaAttachmentBuilder().ownerId('A').originalFilename('b.jpg').buildAndSave()
        MetaAttachment attachment3 = new MetaAttachmentBuilder().ownerId('A').originalFilename('a.jpg').buildAndSave()        

        when:
        controller.list()

        then:

        modelAttachments[0] == attachment3
        modelAttachments[1] == attachment2
        modelAttachments[2] == attachment1
    }

    def "Upload an attachment"() {
        given:
        CommonsMultipartFile file = mock(CommonsMultipartFile)
        MetaAttachment metaAttachment = mock(MetaAttachment, constructor())

        when:
        mock(controller.request).getFile('file').returns(file)
        metaAttachment.setProperties(controller.params)
        metaAttachment.upload(file)
        
        play {
            controller.upload()
        }

        then:
        content == 'OK'
    }

    def "Download an attachment"() {
        given:
        ByteArrayInputStream data = new ByteArrayInputStream('foo'.getBytes())
        MetaAttachment metaAttachment = new MetaAttachmentBuilder().buildAndSave()

        when:
        mock(metaAttachment).exists.returns(true)
        mock(metaAttachment).download().returns(data)

        play {
            controller.params.id = metaAttachment.id
            controller.download()
        }

        then:
        response.status == 200
        response.getHeader('Content-Type') == metaAttachment.contentType
        response.getHeader('Content-Length') == '3'
        response.getHeader('Content-Disposition') == "attachment;filename=${metaAttachment.originalFilename}"
        response.contentAsString == 'foo'
    }

    def "Download serves 404 for non existent MetaAttachment"() {
        when:
        play {
            controller.params.id = 999
            controller.download()
        }

        then:
        status == 404
    }


    def "Download serves 404 for non existent underlying attachment"() {
        given:
        MetaAttachment metaAttachment = new MetaAttachmentBuilder().buildAndSave()

        when:
        mock(metaAttachment).exists.returns(false)

        play {
            controller.params.id = metaAttachment.id
            controller.download()
        }

        then:
        status == 404
    }

    def "Delete an attachment"() {
        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()
        MetaAttachment ma = new MetaAttachmentBuilder().owner(invoice).buildAndSave()

        when:
        controller.params.id = ma.id
        controller.delete()

        then:
        invoice.attachments.size() == 0
        content == 'OK'
    }

    private Collection getModelAttachments() {
        assert renderArgs.model
        renderArgs.model.attachments
    }

    private MetaAttachment getTheSavedAttachment() {
        assert MetaAttachment.count() == 1
        return MetaAttachment.list()[0]
    }
}
