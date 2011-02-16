
grails.project.groupId = Books // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]
// The default codec used to encode data with ${}
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
grails.converters.encoding="UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder=false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable fo AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

grails.converters.json.default.deep=true

grails.app.context='/'

// set per-environment serverURL stem for creating absolute links
// required to be set here otherwise jawr links fail
grails.serverURL = "http://localhost:8080"
environments {
    production {
        grails.serverURL = "http://books.acuminous.meh"
    }
}
// log4j configuration
log4j = {
    appenders {
        console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
        'null' name:'stacktrace'
    }

    root {
        warn 'stdout' 
    }

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
	       'org.codehaus.groovy.grails.web.pages', //  GSP
	       'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
	       'org.codehaus.groovy.grails.web.mapping', // URL mapping
	       'org.codehaus.groovy.grails.commons', // core / classloading
	       'org.codehaus.groovy.grails.plugins', // plugins
	       'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
	       'org.springframework',
	       'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
}

jawr {
    debug.on = true
    gzip.ie6.on = true
    js {
        mapping = '/script/'
        bundle {
            names = 'application.js, jquery.js, jquery-ui.js, jquery-plugins.js'
            'application.js' {
                id = '/bundles/application.js'
                mappings = '/js/application/**'
            }
            'jquery.js' {
                id = '/bundles/jquery.js'
                mappings = '/js/jquery/**'
                bundlepostprocessors = 'none'                
            }
            'jquery-ui.js' {
                id = '/bundles/jquery-ui.js'
                mappings = '/js/jquery-ui/**'
                bundlepostprocessors = 'none'
            }
            'jquery-plugins.js' {
                id = '/bundles/jquery-plugins.js'
                mappings = '/js/jquery-plugins/**'
                bundlepostprocessors = 'none' 
            }
        }
    }
    css {
        mapping = '/style/'
        bundle {
            names = 'application.css, jquery-ui.css'
            'application.css' {
                id = '/bundles/application.css'
                mappings = '/css/application.css'
            }
            'jquery-ui.css' {
                id = '/bundles/jquery-ui.css'
                mappings = '/css/jquery-ui/**'
            }
        }
    }
}

jodatime.format.org.joda.time.LocalDate='dd-MMM-yy'
datapicker.format.org.joda.time.LocalDate='dd-M-y'

// Added by the Joda-Time plugin:
grails.gorm.default.mapping = {
	"user-type" type: org.joda.time.contrib.hibernate.PersistentDateTime, class: org.joda.time.DateTime
	"user-type" type: org.joda.time.contrib.hibernate.PersistentDuration, class: org.joda.time.Duration
	"user-type" type: org.joda.time.contrib.hibernate.PersistentInstant, class: org.joda.time.Instant
	"user-type" type: org.joda.time.contrib.hibernate.PersistentInterval, class: org.joda.time.Interval
	"user-type" type: org.joda.time.contrib.hibernate.PersistentLocalDate, class: org.joda.time.LocalDate
	"user-type" type: org.joda.time.contrib.hibernate.PersistentLocalTimeAsString, class: org.joda.time.LocalTime
	"user-type" type: org.joda.time.contrib.hibernate.PersistentLocalDateTime, class: org.joda.time.LocalDateTime
	"user-type" type: org.joda.time.contrib.hibernate.PersistentPeriod, class: org.joda.time.Period
}

attachment.basePath = '/var/BooksTest/attachments'
