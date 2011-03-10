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
