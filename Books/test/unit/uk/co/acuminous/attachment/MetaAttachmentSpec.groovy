package uk.co.acuminous.attachment

import uk.co.acuminous.test.TestUtils
import uk.co.acuminous.books.builder.MetaAttachmentBuilder
import org.springframework.web.multipart.commons.CommonsMultipartFile

import grails.plugin.spock.UnitSpec
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.codehaus.groovy.grails.plugins.GrailsPluginManager

import uk.co.acuminous.books.Expense
import uk.co.acuminous.books.Invoice
import org.joda.time.DateTime
import uk.co.acuminous.books.builder.InvoiceBuilder
import uk.co.acuminous.books.Amount

@Mixin(TestUtils)
class MetaAttachmentSpec extends UnitSpec {

    def setup() {
        PluginManagerHolder.pluginManager = [hasGrailsPlugin: { String name -> true }] as GrailsPluginManager
        mockDomains MetaAttachment, Invoice, Amount
    }

    def cleanup() {
        PluginManagerHolder.pluginManager = null
    }

    def "Uploading an attachment assigns the simple fields"() {

        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()
        PersistenceManager pm = Mock(PersistenceManager)
        MetaAttachment ma = new MetaAttachmentBuilder().owner(invoice).attachmentPersistenceManager(pm).build()
        CommonsMultipartFile file = Mock(CommonsMultipartFile)

        when:
        runAsIf testDate, {
            ma.upload(file)
        }

        then:
        1 * file.originalFilename >> originalFilename
        1 * file.contentType >> contentType

        ma.created == testDate
        ma.originalFilename == originalFilename
        ma.contentType == contentType

        where:
        originalFilename | contentType  | testDate
        'foo.bar'        | 'foo/bar'    | new DateTime().minusDays(3)
        'gc.log'         | 'text/plain' | new DateTime().minusYears(1)
    }

    def "Upload stores the underlying attachment"() {
        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()        
        PersistenceManager pm = Mock(PersistenceManager)
        MetaAttachment ma = new MetaAttachmentBuilder().owner(invoice).attachmentPersistenceManager(pm).build()
        CommonsMultipartFile file = Mock(CommonsMultipartFile)

        when:
        ma.upload(file)

        then:
        1 * pm.store(ma, file) >> internalUri                
        ma.internalUri == internalUri

        where:
        internalUri << ['/var/BooksTest/attachments/db-00001.pdf']
    }

    def "Upload persists the meta attachment"() {
        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()
        PersistenceManager pm = Mock(PersistenceManager)
        MetaAttachment ma = new MetaAttachmentBuilder().owner(invoice).attachmentPersistenceManager(pm).build()
        CommonsMultipartFile file = Mock(CommonsMultipartFile)

        when:
        ma.upload(file)

        then:
        1 * file.originalFilename >> 'foo.bar'
        1 * file.contentType >> 'foo/bar'
        1 * pm.store(ma, file)
        ma == thePersistedMetaAttachment
    }

    def "Upload adds the meta attachment to the owner"() {
        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()
        PersistenceManager pm = Mock(PersistenceManager)
        MetaAttachment ma = new MetaAttachmentBuilder().ownerId(invoice.id).ownerClass(invoice.class).ownerField('attachments').attachmentPersistenceManager(pm).build()
        CommonsMultipartFile file = Mock(CommonsMultipartFile)

        when:
        ma.upload(file)

        then:
        invoice.attachments.size() == 1
        invoice.attachments.iterator().next() == ma
    }

    def "Upload does nothing if the underlying file does not exist"() {
        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()
        PersistenceManager pm = Mock(PersistenceManager)
        MetaAttachment ma = new MetaAttachmentBuilder().owner(invoice).attachmentPersistenceManager(pm).build()
        CommonsMultipartFile file = Mock(CommonsMultipartFile)

        when:
        ma.upload(file)

        then:
        1 * file.isEmpty() >> true
    }

    def "Attachment is deleted on entity deletion"() {
        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()        
        PersistenceManager pm = Mock(PersistenceManager)
        MetaAttachment ma = new MetaAttachmentBuilder().owner(invoice).attachmentPersistenceManager(pm).internalUri('/some/file').build()

        when:
        ma.beforeDelete()

        then:
        1 * pm.exists(ma.internalUri) >> true
        1 * pm.delete(ma.internalUri)
    }

    def "Attachment is not deleted if it does not exist"() {
        given:
        Invoice invoice = new InvoiceBuilder().buildAndSave()       
        PersistenceManager pm = Mock(PersistenceManager)
        MetaAttachment ma = new MetaAttachmentBuilder().owner(invoice).attachmentPersistenceManager(pm).internalUri('/some/file').build()

        when:
        ma.beforeDelete()

        then:
        1 * pm.exists(ma.internalUri) >> false
    }

    def "MetaAttachment safely reports the attachment does not exist before an internal uri has been set"() {
        given:
        MetaAttachment ma = new MetaAttachmentBuilder().build()

        expect:
        ma.exists == false
    }

    def "MetaAttachment correctly reports whether the underlying attachment exists"() {
        given:
        PersistenceManager pm = Mock(PersistenceManager)
        MetaAttachment ma = new MetaAttachmentBuilder().attachmentPersistenceManager(pm).internalUri('/some/file').build()

        when:
        Boolean result = ma.exists

        then:
        1 * pm.exists('/some/file') >> fileExists
        result == fileExists

        where:
        fileExists << [true, false]
    }

    private MetaAttachment getThePersistedMetaAttachment() {
        assert MetaAttachment.count() == 1
        return MetaAttachment.list()[0]
    }
}

