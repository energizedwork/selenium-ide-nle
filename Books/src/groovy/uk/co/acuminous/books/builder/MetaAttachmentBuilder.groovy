package uk.co.acuminous.books.builder

import uk.co.acuminous.attachment.MetaAttachment
import uk.co.acuminous.books.Invoice
import uk.co.acuminous.attachment.PersistenceManagerStub
import org.apache.commons.lang.StringUtils

class MetaAttachmentBuilder extends BooksEntityBuilder {

    MetaAttachmentBuilder() {
        factories.put 'MetaAttachment.ownerId', { def entity -> UUID.randomUUID().toString() }        
        factories.put 'MetaAttachment.ownerClass', { def entity -> Invoice }
        factories.put 'MetaAttachment.ownerField', { def entity -> 'attachments' }
        factories.put 'MetaAttachment.contentType', { def entity -> "${randomLowerCaseWord(4)}/${randomLowerCaseWord(3)}"}
        factories.put 'MetaAttachment.originalFilename', { def entity -> "${randomLowerCaseWord()}.${randomLowerCaseWord(3)}"}
        factories.put 'MetaAttachment.attachmentPersistenceManager', { def entity -> new PersistenceManagerStub() }
    }

    def afterAssignValues = {}

    MetaAttachmentBuilder owner(def owner, String field = 'attachments') {
        ownerId(owner.id)
        ownerClass(owner.class)
        ownerField(field)
        afterAssignValues = {
            owner."addTo${StringUtils.capitalize(field)}"(entity)
        }
        return this
    }

    Collection getAutoAssignedFieldNames() {
        ['ownerId', 'ownerClass', 'ownerField', 'attachmentPersistenceManager']
    }

    MetaAttachment build() {
        entity = new MetaAttachment()
        assignValues()
        afterAssignValues()
        return entity
    }

    MetaAttachment buildAndSave() {
        build()
        entity.created = entity.created ?: generateValue('created')
        entity.contentType = entity.contentType ?: generateValue('contentType')
        entity.originalFilename  = entity.originalFilename ?: generateValue('originalFilename')
        entity.save()
        return entity
    }
}
