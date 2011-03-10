/*
 * Copyright 2010 Stephen Mark Cresswell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

