grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
  
grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
    // configure settings for the run-app JVM
    run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the run-war JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
		mavenRepo "http://repo.grails.org/grails/core" // needed for compass, etc.
		mavenRepo 'http://repository.jboss.org/maven2/'
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
		compile 'log4j:apache-log4j-extras:1.0'
		runtime 'mysql:mysql-connector-java:5.1.21'
		compile 'com.itextpdf:itextpdf:5.4.1'
		build 'org.codehaus.gpars:gpars:1.2.1'
		//compile ':spring-security-ui:1.0-RC3' //) { excludes ":asset-pipeline:2.6.10"}
		
    }

    plugins {
        // plugins for the build system only
        build ":tomcat:7.0.47"		
        // plugins for the compile step
        compile ":scaffolding:2.0.1"
        compile ':cache:1.1.1'
		compile ":navigation:1.3.2"
        // plugins needed at runtime but not for compilation
		compile ":hibernate:3.6.10.6" 
        runtime ":database-migration:1.3.8"
        runtime ":jquery:1.10.2.2"
        runtime ":resources:1.2.1"	
		compile ":google-visualization:0.6.2"
		compile ":jquery-ui:1.10.3"
		compile ":jquery-ui-timepicker:0.9.8.1"
		compile ":jquery-date-time-picker:0.1.1"
		compile ":tooltip:0.8"		
		runtime ":prototype:1.0"
		//compile ":spring-security-core:1.2.7.3"
		compile ":spring-security-core:2.0.0"
		//compile "org.grails.plugins:spring-security-ui:1.0-RC3"
		compile "org.grails.plugins:asset-pipeline:2.12.4"
		compile ":rendering:0.4.4"
		compile ":quartz2:2.1.6.2"
		compile ":richui:0.8"
		runtime ":searchable:0.6.6"	
		compile ":codenarc:0.20"
		compile ":mail:1.0.4"		
		compile ":excel-export:0.2.1"
		compile ":csv:0.3.1"
		compile ":ajax-uploader:1.1"
		compile "org.grails.plugins:remote-pagination:0.4.8"
		
		/*
		compile ("org.grails.plugins:email-confirmation:2.0.8"){
			excludes "quartz:1.0-RC2"
        }	
        */
    }
}
