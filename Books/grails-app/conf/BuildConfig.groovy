import org.apache.ivy.core.settings.IvySettings
import org.apache.ivy.plugins.resolver.IvyRepResolver

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits( "global" ) {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {        
        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()
        mavenRepo "http://snapshots.repository.codehaus.org"
        mavenRepo "http://repository.codehaus.org"
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "http://wicketstuff.org/maven/repository"
        mavenRepo "http://download.csssprites.org/maven2"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.5'
//        test 'org.spockframework:spock-core:0.5-groovy-1.7-SNAPSHOT'

//        test ('net.jawr:jawr:3.3.2') {
//    		excludes "mail", "activation", "ejb","jms","jmxri","jmxremote", "junit"
//        }

        test 'org.objenesis:objenesis:1.2'
        compile 'org.apache.poi:poi:3.7'
        compile 'org.apache.poi:poi-scratchpad:3.7'
        compile 'org.apache.poi:poi-ooxml:3.7'
        compile 'org.jbehave:jbehave-core:3.1.2'
    }

}
