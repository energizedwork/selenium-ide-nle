import uk.co.acuminous.books.editor.CustomEditorRegistrar
import uk.co.acuminous.attachment.FilePersistenceManager
import liquibase.spring.SpringLiquibase
import grails.util.GrailsUtil
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import uk.co.acuminous.util.LiquibaseDropAll

beans = {

    ConfigObject config = ConfigurationHolder.config


    dataSource(ComboPooledDataSource) { def bean ->
        driverClass = config.dataSource.driverClassName
        user = config.dataSource.username
        password = config.dataSource.password
        jdbcUrl = config.dataSource.url
        minPoolSize = 5
        maxPoolSize = 10
        acquireIncrement = 5
        testConnectionOnCheckout = true
        bean.destroyMethod = 'close'
    }

    myEditorRegistrar(CustomEditorRegistrar)
    
    attachmentPersistenceManager(FilePersistenceManager)
}
