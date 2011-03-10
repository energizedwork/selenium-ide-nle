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

import org.springframework.web.multipart.commons.CommonsMultipartFile
import org.apache.commons.lang.RandomStringUtils

class PersistenceManagerStub implements PersistenceManager {

    Map virtualFs

    String store(MetaAttachment ma, CommonsMultipartFile source) {
        String key = UUID.randomUUID().toString() 
        Byte[] data = RandomStringUtils.randomAlphabetic(100).getBytes()
        virtualFs.key = new ByteArrayInputStream(data)
        return key

    }

    boolean exists(String key) {
        virtualFs.contains(key)
    }    

    InputStream load(String key) {
        return virtualFs[key]
    }

    void delete(String key) {
        virtualFs.remove(key)
    }
}


