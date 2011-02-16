package uk.co.acuminous.util

import liquibase.spring.SpringLiquibase
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.apache.log4j.Logger

class LiquibaseDropAll extends SpringLiquibase {

    static Logger log = Logger.getLogger(LiquibaseDropAll.class)

    void afterPropertiesSet() {
        log.info('Initialising database schema')        
        if (ConfigurationHolder.config.liquibase.dropAll) {
            log.warn('Dropping database schema')
            super.createLiquibase(dataSource.connection).dropAll()
        }
        super.afterPropertiesSet()
    }
}
