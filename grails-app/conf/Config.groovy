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



// grails.config.locations = [ "classpath:${appName}-config.properties"]

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

// URL Mafolping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']
grails.resources.debug=true

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

//In Config.groovy
grails.plugins.remotepagination.max=20
//EnableBootstrap here when using twitter bootstrap, default is set to false.
grails.plugins.remotepagination.enableBootstrap=false

environments {
    development {
		ip.authorized = ['90.80.193.12','0:0:0:0:0:0:0:1']
		ip.authorization.on=false
		laboratory.logo='LABM.png'
		laboratory.name='LABM'
		def log4jFile=userHome.toString()+'/pointeuse.log'
		pdf.directory='/Users/henri/pointeuse'
		mysqldump.directory='/usr/local/bin/'
		grails.app.context=pointeuse
        grails.logging.jul.usebridge = true
		grails.resources.processing.enabled=false
		serverURL = "http://localhost:8080"
		context="/pointeuse"
		log4j = {
			appenders {
				rollingFile name:'rollingFileAppender',file:log4jFile, maxFileSize:1024,maxBackupIndex:10,layout:pattern(conversionPattern: '%d %c{2} %m%n')
			}
			warn 'org.codehaus.groovy.grails.orm.hibernate','org.hibernate','org.springframework', 'net.sf.ehcache.hibernate'
			warn   'org.codehaus.groovy.grails.web.sitemesh',       // layouts
				   'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
				   'org.codehaus.groovy.grails.web.mapping',        // URL mapping
				   'org.codehaus.groovy.grails.commons',            // core / classloading
				   'org.codehaus.groovy.grails.plugins'            // plugins
			warn 'org.springframework.security'
			root {
				warn 'rollingFileAppender','stdout'//,'eventLogAppender'
			}
		}
    }

	demo_aws {
		ip.authorized = ['90.80.193.12','90.120.222.39','0:0:0:0:0:0:0:1']
		ip.authorization.on=false
		pdf.directory='/opt/tomcat/pdf'
		laboratory.logo='LABM.png'
		laboratory.name='LABM'
		mysqldump.directory='/usr/bin'
		grails.app.context=''
		grails.logging.jul.usebridge = true
		grails.resources.processing.enabled=false
		serverURL = "http://pointeuse.ddns.net"
		//serverURL = "http://pointeuse.biolab33.com"

		context=''
		log4j = {
				'null' name:'stacktrace'
				appenders {
					rollingFile name:'myAppender',file:"/var/log/tomcat7/pointeuseDEMO.log", maxFileSize:1024000,maxBackupIndex:31,layout:pattern(conversionPattern: '%d %c{2} %m%n')
				}
			//	warn  myAppender:['pointeuse','pointeuse.ErrorsController','pointeuse.EmployeeController']     // controllers
				warn   'org.codehaus.groovy.grails.web.sitemesh',       // layouts
					   'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
					   'org.codehaus.groovy.grails.web.mapping',        // URL mapping
					   'org.codehaus.groovy.grails.commons',            // core / classloading
					   'org.codehaus.groovy.grails.plugins',            // plugins
					   'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
					   'org.springframework',
					   'org.hibernate',
					   'net.sf.ehcache.hibernate'
				warn 'org.springframework.security'
				root {
					warn 'myAppender'//,'rollingFile'
			}
		}
	}

	aws {
		ip.authorized = ['90.80.193.12','90.120.222.39','0:0:0:0:0:0:0:1']
		ip.authorization.on=true
		pdf.directory='/opt/tomcat/pdf'
		mysqldump.directory='/usr/bin'
		laboratory.logo='biolab3.png'
		laboratory.name='biolab'
		grails.app.context=''
		grails.logging.jul.usebridge = true
		grails.resources.processing.enabled=false
		//serverURL = "http://ec2-34-255-11-191.eu-west-1.compute.amazonaws.com"
		serverURL = "http://pointeuse.biolab33.com"

		context=''
		log4j = {
				'null' name:'stacktrace'
				appenders {
					rollingFile name:'myAppender',file:"/var/log/tomcat7/pointeuse.log", maxFileSize:1024000,maxBackupIndex:31,layout:pattern(conversionPattern: '%d %c{2} %m%n')
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
				warn 'org.springframework.security'
				root {
					warn 'myAppender'//,'rollingFile'
			}
		}
	}
	
	aws_isengard {
		ip.authorized = ['90.80.193.12','90.120.222.39','0:0:0:0:0:0:0:1']
		ip.authorization.on=true
		pdf.directory='/opt/tomcat/pdf'
		mysqldump.directory='/usr/bin'
		laboratory.logo='biolab3.png'
		laboratory.name='biolab'
		grails.app.context=''
		grails.logging.jul.usebridge = true
		grails.resources.processing.enabled=false
		//serverURL = "http://ec2-34-255-11-191.eu-west-1.compute.amazonaws.com"
		serverURL = "http://pointeuse.biolab33.com"

		context=''
		log4j = {
				'null' name:'stacktrace'
				appenders {
					rollingFile name:'myAppender',file:"/tmp/pointeuse.log", maxFileSize:1024000,maxBackupIndex:31,layout:pattern(conversionPattern: '%d %c{2} %m%n')
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
				warn 'org.springframework.security'
				root {
					warn 'myAppender'//,'rollingFile'
			}
		}
	}

}

grails {
   mail {
	   host = "smtp.msa.orange-business.com"
	   port = 587
	   username = "pointeuse2@biolab32.fr.fto"
	   password = "pasteur33!"
	   grails.mail.default.from="pointeuse@biolab33.com"
	   props = ["mail.smtp.starttls.enable":"true",
		   		"mail.smtp.auth":"true",
                "mail.smtp.port":"587"]
	 }
}

jquery {
	sources = 'jquery'
	version = '1.10.0'
}

prototype {
	sources = 'prototype'
	version = '1.0'
}


// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'pointeuse.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'pointeuse.UserRole'
grails.plugin.springsecurity.authority.className = 'pointeuse.Role'
grails.plugin.springsecurity.auth.loginFormUrl = '/login/auth'
grails.plugin.springsecurity.failureHandler.defaultFailureUrl = '/login/denied'
grails.plugin.springsecurity.password.algorithm = 'SHA-256'
grails.plugin.springsecurity.password.hash.iterations = 1
grails.plugin.springsecurity.userLookup.userDomainClassName = 'pointeuse.User'


// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	'/':                			['permitAll'],
	'/index':           			['permitAll'],
	'/redirection':           		['permitAll'],
	'/redirection.html':           	['permitAll'],
	'/employee/main.css':			['permitAll'],
	'/user/**':						['ROLE_SUPER_ADMIN'],
	'/role/**':						['ROLE_SUPER_ADMIN'],
	'/registrationCode/**':			['ROLE_SUPER_ADMIN'],
	'/securityInfo/**':				['ROLE_SUPER_ADMIN'],
	'/employee/pointage':			['permitAll'],
	'/index.gsp':       			['permitAll'],
	'/pointeuse/assets/**':			['permitAll'],
	'/assets/**':					['permitAll'],
	'**/assets/**':       			['permitAll'],
	'/**/js/**':        			['permitAll'],
	'/**/css/**':       			['permitAll'],
	'/**/images/**':    			['permitAll'],
	'/register/**':    				['permitAll'],
	'/**/favicon.ico':  			['permitAll'],
	'/plugins/**':					['permitAll'],
	'/jasper/**':					['permitAll'],
	'/range.html': ['permitAll']

	// special URL to be accessed via cron

]

grails.plugin.springsecurity.ui.register.defaultRoleNames = ['ROLE_ADMIN']
grails.plugin.springsecurity.ui.encodePassword = true
grails.plugin.springsecurity.ui.forgotPassword.emailFrom = 'pointeuse@biolab33.com'

//grails.plugin.springsecurity.ui.password.validationRegex='^.*(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}*$'
grails.plugin.springsecurity.ui.password.validationRegex='^.*[A-Za-z0-9].*$'
//grails.plugin.springsecurity.ui.password.validationRegex='^.*(?!^.*[A-Z]{2,}.*$)^[A-Za-z]*$'
//grails.plugin.springsecurity.ui.password.validationRegex='^.*(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&]).*$'
grails.plugin.springsecurity.ui.password.minLength = 6
grails.plugin.springsecurity.ui.password.maxLength = 64
grails.plugin.springsecurity.ui.register.postRegisterUrl = '/index.gsp'
grails.plugin.springsecurity.ui.register.emailFrom = 'pointeuse@biolab33.com'
grails.plugin.springsecurity.ui.forgotPassword.emailSubject = 'Réinitialisation du mot de passe de la pointeuse'
//grails.plugin.springsecurity.ui.register.emailBody = '...'
//grails.plugin.springsecurity.ui.register.emailSubject = '...'
grails.plugin.springsecurity.ui.register.postRegisterUrl = '/'
grails.plugin.springsecurity.ui.forgotPassword.emailBody = '''\
Bonjour $user.username,<br/>
<br/>
Vous (ou quelqu'un prétendant être vous) a demandé la réinitialisation de votre mot de passe.<br/>
<br/>
Si vous n'êtes pas à l'origine de cette demande, veuillez ignorer ce message.<br/>
<br/>
Si en revanche vous souhaitez réinitialiser votre mot passe, merci de cliquer <a href="$url">ici</a>.
'''
