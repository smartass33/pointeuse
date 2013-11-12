grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
		excludes 'xercesImpl'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		compile 'log4j:apache-log4j-extras:1.0'
        runtime 'mysql:mysql-connector-java:5.1.21'
		compile 'com.itextpdf:itextpdf:5.4.1'
		

		
    }

    plugins {
		compile ":google-visualization:0.6.2"
		
		compile ":excel-export:0.1.5"
        runtime ":hibernate:$grailsVersion"
		compile ":jquery:1.10.0"
		compile ":jquery-ui:1.8.24"
		compile ":jquery-ui-timepicker:0.9.8.1"
		compile ":jquery-date-time-picker:0.1.0"
        runtime ":resources:1.0"
		compile ":tooltip:0.7"
		compile ":mail:1.0.1"
		runtime ":prototype:1.0"
		compile ":spring-security-core:1.2.7.3"
		compile ":joda-time:1.4"
		compile ":rendering:0.4.4"
		compile ":spring-security-core:1.2.7.3"
		compile ":quartz2:2.1.6.2"
		compile ":modalbox:0.4"
		compile ":richui:0.8"
		compile ":searchable:0.6.4"
        build ":tomcat:$grailsVersion"
        runtime ":database-migration:1.1"
        compile ':cache:1.0.0'
    }
}
