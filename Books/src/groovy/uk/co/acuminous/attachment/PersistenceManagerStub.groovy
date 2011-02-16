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


