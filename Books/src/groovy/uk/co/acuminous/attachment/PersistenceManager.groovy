package uk.co.acuminous.attachment

import org.springframework.web.multipart.commons.CommonsMultipartFile

public interface PersistenceManager {

    String store(MetaAttachment ma, CommonsMultipartFile file)
    boolean exists(String uri)
    InputStream load(String uri)
    void delete(String uri)

}