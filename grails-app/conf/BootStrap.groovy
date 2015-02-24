import org.apache.log4j.Logger
import org.apache.log4j.jdbc.JDBCAppender
import org.apache.log4j.net.SMTPAppender
import org.apache.log4j.DailyRollingFileAppender
import java.util.Calendar
import pointeuse.InAndOutCLosingJob
import pointeuse.SiteAnnualReportJob
import pointeuse.EventLogAppender

class BootStrap {

	def grailsApplication
	//def springSecurityService

	
	
    def init = { servletContext ->
		log.error 'executing bootstrap'
		EventLogAppender.appInitialized = true
		def calendar = Calendar.instance		
		calendar.set(Calendar.HOUR_OF_DAY,22)
		calendar.set(Calendar.MINUTE,0)
		log.error 'registring InAndOutCLosingJob at '+calendar.time
		InAndOutCLosingJob.schedule(calendar.time)
		
		
		calendar.set(Calendar.HOUR_OF_DAY,03)
		calendar.set(Calendar.MINUTE,30)
		//SupTimeComputationJob.schedule(calendar.time)
		//log.error 'registring SupTimeComputationJob at '+calendar.time
		

		
		/*
		calendar.set(Calendar.HOUR_OF_DAY,3)
		calendar.set(Calendar.MINUTE,0)
		log.error 'registring SiteMonthlyPDFJob at '+calendar.time
		SiteMonthlyPDFJob.schedule(calendar.time,[folder:grailsApplication.config.pdf.directory])
		*/
		
		//create a calendar to schedule next JOB first day of month
		def firstDayCalendar = Calendar.instance
		firstDayCalendar.set(Calendar.MONTH,firstDayCalendar.get(Calendar.MONTH) + 1)
		firstDayCalendar.set(Calendar.DAY_OF_MONTH,1)
		firstDayCalendar.set(Calendar.HOUR_OF_DAY,3)
		firstDayCalendar.set(Calendar.MINUTE,0)
		SiteAnnualReportJob.schedule(firstDayCalendar.time)
		
		
		
		
		
    }
    def destroy = {
    }
}
