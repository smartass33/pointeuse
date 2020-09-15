package pointeuse

import java.util.Calendar
import org.apache.commons.logging.LogFactory


class SiteAnnualReportJob {
	private static final log = LogFactory.getLog(this)
	
	static triggers = {
		cron name: 'myTrigger', cronExpression: "0 0 5 1 * ?"
	}
	def group = "MyGroup"


	def execute() {
		log.error "SiteAnnualReportJob is running!"
		
		
		//execution of the report
		
		
		// programming the next one
		def calendar = Calendar.instance
	
		if (calendar.get(Calendar.DAY_OF_YEAR) == calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
			calendar.roll(Calendar.YEAR,1)
			calendar.set(Calendar.MONTH,0)
		}

		calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH) + 1)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.set(Calendar.HOUR_OF_DAY,3)
		calendar.set(Calendar.MINUTE,0)
		
		
		log.error 'registring SiteAnnualReportJob at '+calendar.time
		
		this.schedule(calendar.time)
		
	}
	
}
