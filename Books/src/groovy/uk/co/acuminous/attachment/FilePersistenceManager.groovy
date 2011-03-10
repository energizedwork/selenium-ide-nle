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

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.web.multipart.commons.CommonsMultipartFile

class FilePersistenceManager implements PersistenceManager {

    String store(MetaAttachment ma, CommonsMultipartFile source) {
        String path = getPath(ma)
        source.transferTo(new File(path))
        return path
    }

    String getPath(MetaAttachment ma) {
        new PathGenerator(ma).path        
    }

    boolean exists(String path) {
        new File(path).exists()
    }    

    InputStream load(String path) {
        return new File(path).newInputStream()
    }

    void delete(String path) {
        new File(path).delete()
    }
}

class PathGenerator {

    static final Integer INDEX_LENGTH = 5    
    MetaAttachment ma

    PathGenerator(MetaAttachment ma) {
        this.ma = ma
    }

    String getPath() {
        "${basePath}/${fileName}"
    }    

    protected String getFileName() {
        "${fileNameBeforeExtension}-${index}.${extension}".toLowerCase()
    }

    protected String getIndex() {
        String.format("%0${INDEX_LENGTH}d", ma.id);
    }

    protected String getExtension() {
        List filenameParts = ma.originalFilename.split(/\./)
        filenameParts.remove(0)
        return filenameParts.join('.')
    }

    protected String getFileNameBeforeExtension() {
        ma.originalFilename.split(/\./)[0]
    }

    protected String getBasePath() {
        ConfigurationHolder?.config?.attachment?.basePath ?: "/var/${appName}/attachments"
    }

    protected String getAppName() {
        ApplicationHolder?.application?.metadata?.getAt('app.name')?.toLowerCase() ?: 'UnitTest'
    }

}
