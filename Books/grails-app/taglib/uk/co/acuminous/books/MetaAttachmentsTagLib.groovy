package uk.co.acuminous.books

import groovy.xml.MarkupBuilder

import uk.co.acuminous.attachment.MetaAttachment

class MetaAttachmentsTagLib {

    static namespace = 'ma'

    def attachmentsIcon = { Map attrs, def body ->
        String targetId = attrs.target.id
        String iconId = "toggle-attachments-${targetId}"
        String imgSrc = g.resource(dir:'/images', file:'attachment.gif')

        MarkupBuilder builder = new MarkupBuilder(out)
        builder.img(id: iconId, src: imgSrc)
        builder.script(type:'text/javascript') {
            mkp.yield "\$('#${iconId}').bind('click', Books.attachments.toggle);"
        }
        out << '' // flush for unit tests        
    }

    def attachmentsTable = { Map attrs, def body ->
        out << renderTableHeading(attrs)
        out << renderDownloadSection(attrs)
        out << renderUploadSection(attrs)
    }

    def renderTableHeading = { Map attrs, def body ->
        String targetId = attrs.target.id
        String iconId = "toggle-attachments-${targetId}"
        Range columns = attrs.columns
        Integer colspan = columns.to - columns.from + 1

        new MarkupBuilder(out).tr(class: ['heading', iconId].join(' ')) {
            for (int i = 1; i < columns.from; i++) {
                td {
                    mkp.yieldUnescaped '&nbsp;'
                }
            }
            th(colspan: colspan) {
                mkp.yield g.message(code: 'metaAttachment.title')
            }
        }
        out << '' // flush for unit tests
    }

    def renderDownloadSection = { Map attrs, def body ->
        String targetId = attrs.target.id
        String iconId = "toggle-attachments-${targetId}"
        Range columns = attrs.columns
        Integer colspan = columns.to - columns.from

        MarkupBuilder builder = new MarkupBuilder(out)        
        attrs.attachments.each { MetaAttachment metaAttachment ->
            builder.tr(class: ['download', iconId].join(' ')) {
                for (int i = 1; i < columns.from; i++) {
                    td {
                        mkp.yieldUnescaped '&nbsp;'
                    }
                }
                td(colspan: colspan, class: 'filename') {
                    mkp.yieldUnescaped g.link(controller:'attachment', action:'download', id:metaAttachment.id) {
                        metaAttachment.originalFilename
                    }
                }
                td(class: 'icon') {
                    mkp.yieldUnescaped renderDeleteIcon(metaAttachment:metaAttachment)
                }
            }
        }
        out << ''
    }

    def renderDeleteIcon = { Map attrs, def body ->
        MetaAttachment ma = attrs.metaAttachment
        String iconId = "delete-attachment-${ma.id}"
        String url = g.createLink(controller: "attachment", action: "delete", id: ma.id)
        String imgSrc = g.resource(dir:'/images/skin', file:'database_delete.png')
        String prompt = g.message(code:"metaAttachment.delete.confirm", args:[ma.originalFilename])

        MarkupBuilder builder = new MarkupBuilder(out)
        builder.img(id: iconId, class:'selectable', src: imgSrc) {}
        builder.script(type:'text/javascript') {
            mkp.yield "\$('#${iconId}').bind('click', {url:'${url}', prompt:'${prompt}'}, Books.attachments.del);"
        }

        out << ''
        
    }    

    def renderUploadSection = { Map attrs, def body ->
        String targetId = attrs.target.id
        String iconId = "toggle-attachments-${targetId}"
        Range columns = attrs.columns
        Integer colspan = columns.to - columns.from + 1

        new MarkupBuilder(out).tr(class: ['upload', iconId].join(' ')) {
            for (int i = 1; i < columns.from; i++) {
                td {
                    mkp.yieldUnescaped '&nbsp;'
                }
            }
            td(colspan: colspan, class: 'control') {
                mkp.yieldUnescaped g.render(template: '/fragments/fileUpload', model:[attachmentOwner: attrs.target])                
            }
        }
        out << ''
    }
}
