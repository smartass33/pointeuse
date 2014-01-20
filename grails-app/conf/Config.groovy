import org.apache.log4j.*;
import org.apache.log4j.jdbc.JDBCAppender;
import pointeuse.EventLogAppender
// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]


//grails.config.locations = ["file:${userHome}/.grails/${appName}-config.groovy","file:/opt/tomcat/${appName}-config.groovy"]


 if (System.properties["${appName}.config.location"]) {
   grails.config.locations << "file:" + System.properties["${appName}.config.location"]
 }

grails.views.javascript.library = "jquery"
grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false


environments {
    development {
		pdf.directory='/Users/henri'
		grails.app.context=pointeuse
        grails.logging.jul.usebridge = true
		grails.resources.processing.enabled=false
		serverURL = "http://localhost:8080"
		context="pointeuse"
		log4j = {
			
				appenders {
					appender new EventLogAppender(source:'pointeuse', name: 'eventLogAppender', layout:new EnhancedPatternLayout(conversionPattern: '%d{DATE} %5p %c{1}:%L - %m%n %throwable{500}'), threshold: org.apache.log4j.Level.ERROR)
					
					rollingFile name:'myAppender',file:"/Users/henri/Documents/workspace/pointeuse/logs/pointeuse.log", maxFileSize:1024,layout:pattern(conversionPattern: '%d %c{2} %m%n')
				}
			
				warn  myAppender:['pointeuse','pointeuse.ErrorsController','pointeuse.EmployeeController']     // controllers
							   
				warn   'org.codehaus.groovy.grails.web.sitemesh',       // layouts
					   'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
					   'org.codehaus.groovy.grails.web.mapping',        // URL mapping
					   'org.codehaus.groovy.grails.commons',            // core / classloading
					   'org.codehaus.groovy.grails.plugins',            // plugins
					   'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
					   'org.springframework',
					   'org.hibernate',
					   'net.sf.ehcache.hibernate'
				root {
					//error 'eventLogAppender'
					
					warn 'rollingFile'//,'stdout'
				}
			}
			
		
    }
	
    production {
		pdf.directory='/opt/tomcat/pdf'
		grails.app.context=''
        grails.logging.jul.usebridge = false
        //serverURL = "http://192.168.1.17"
		serverURL = "http://alrikiki.darktech.org"
		context=''
		log4j = {
				'null' name:'stacktrace'
				appenders {
					//rollingFile name:'myAppender',file:"/var/log/tomcat6/pointeuse.log", maxFileSize:1024000,layout:pattern(conversionPattern: '%d %c{2} %m%n')
					rollingFile name:'myAppender',file:"/var/log/tomcat7/pointeuse.log", maxFileSize:1024000,layout:pattern(conversionPattern: '%d %c{2} %m%n')
					
				}
			
				warn  myAppender:['pointeuse','pointeuse.ErrorsController','pointeuse.EmployeeController']     // controllers
				 
				warn   'org.codehaus.groovy.grails.web.sitemesh',       // layouts
					   'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
					   'org.codehaus.groovy.grails.web.mapping',        // URL mapping
					   'org.codehaus.groovy.grails.commons',            // core / classloading
					   'org.codehaus.groovy.grails.plugins',            // plugins
					   'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
					   'org.springframework',
					   'org.hibernate',
					   'net.sf.ehcache.hibernate'
				
				root {
					warn 'rollingFile'//,'stdout'
				}
			}
    }

}

grails {
mail {
	host = "smtp.gmail.com"
	port = 465
	username = "pointeuse.biolab33@gmail.com"
	password = "Wichita3*"
	props = ["mail.smtp.auth":"true",
			 "mail.smtp.socketFactory.port":"465",
			 "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
			 "mail.smtp.socketFactory.fallback":"false"]
  }
}

// Added by the Spring Security Core plugin:
grails.plugins.springsecurity.userLookup.userDomainClassName = 'pointeuse.User'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'pointeuse.UserRole'
grails.plugins.springsecurity.authority.className = 'pointeuse.Role'
grails.plugins.springsecurity.auth.loginFormUrl = '/login/auth'
grails.plugins.springsecurity.failureHandler.defaultFailureUrl = '/login/denied'


jquery {
	sources = 'jquery'
	version = '1.10.0'
}

prototype {
	sources = 'prototype'
	version = '1.0'
}



// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
