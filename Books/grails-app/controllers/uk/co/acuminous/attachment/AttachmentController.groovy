/*
 * Copyright 2010 Acuminous Ltd
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

import javax.servlet.http.HttpServletResponse

class AttachmentController {

    def index = {
        render(view: 'index')        
    }

    def list = {
        Map model = [:]
        model.attachments = MetaAttachment.list().sort { MetaAttachment ma1, MetaAttachment ma2 ->
            ma1.ownerId <=> ma2.ownerId ?: ma1.originalFilename <=> ma2.originalFilename
        }
        render(template: 'tableData', model: model)
    }

    def upload = {
        MetaAttachment metaAttachment = new MetaAttachment()
        metaAttachment.properties = params
        metaAttachment.upload(request.getFile('file'))
        render("OK")
    }

    def download = {
        MetaAttachment metaAttachment = MetaAttachment.get(params.id)
        if (metaAttachment?.exists) {
            serve metaAttachment
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND)
        }
        return null
    }

    def delete = {
        MetaAttachment.get(params.id)?.removeFromOwner() // Will delete the attachment via cascade to delete-orphan
        render("OK")
    }

    private void serve(MetaAttachment ma) {
        InputStream inputStream = ma.download()
        response.setHeader("Content-Disposition", "attachment;filename=${ma.originalFilename}")
        response.setHeader("Content-Type", "${ma.contentType}")
        response.setHeader("Content-Length", "${inputStream.available()}")
        response.outputStream << inputStream
    }
}