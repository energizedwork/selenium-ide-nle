package uk.co.acuminous.attachment

import org.joda.time.DateTime
import org.springframework.web.multipart.commons.CommonsMultipartFile
import static uk.co.acuminous.util.Normalizer.normalize
import org.apache.commons.lang.StringUtils

class MetaAttachment {

    Serializable ownerId
    Class ownerClass
    String ownerField
    String internalUri
    String originalFilename
    String contentType
    DateTime created
    PersistenceManager attachmentPersistenceManager

    static transients = ['owner', 'attachmentPersistenceManager', 'exists']

    static constraints = {
        internalUri(nullable:true)
    }

    MetaAttachment upload(CommonsMultipartFile file) {
        if (!file.isEmpty()) {
            created = new DateTime()
            originalFilename = file.originalFilename
            contentType = file.contentType

            addToOwner()
            save(flush:true) // ensure the meta attachment gets an id before generating the internal uris

            internalUri = attachmentPersistenceManager.store(this, file)
            save()
        }        
        return this
    }

    def getOwner() {
        ownerClass.get(ownerId)
    }

    def addToOwner() {
        def cachedOwner = owner
        cachedOwner."addTo${StringUtils.capitalize(ownerField)}"(this)
        cachedOwner.save(flush:true)
    }

    def removeFromOwner() {
        def attachmentOwner = owner

        attachmentOwner."removeFrom${StringUtils.capitalize(ownerField)}"(this)
        attachmentOwner.save(flush:true)
    }

    protected String normalizeOriginalFilename() {
        List filenameParts = originalFilename?.split(/\./)
        return filenameParts.collect { String part -> normalize part }.join('.')
    }

    InputStream download() {
        attachmentPersistenceManager.load(internalUri)
    }

    boolean getExists() {
        internalUri ? attachmentPersistenceManager.exists(internalUri) : false
    }

    def beforeDelete = {
        if (exists) {
            attachmentPersistenceManager.delete(internalUri)
        }
    }

    String toString() {
        return "MetaAttachment[id:$id, ownerID:$ownerId, ownerClass:$ownerClass, ownerField:$ownerField, internalUri:$internalUri, originalFilename:$originalFilename]"
    }
}

