package pointeuse

import java.util.Calendar
import org.apache.commons.logging.LogFactory


class SupTimeComputationJob {
	private static final log = LogFactory.getLog(this)
	def timeManagerService
	static triggers = {
		// fire trigger every day of the month at 2AM
		cron name: 'myTrigger', cronExpression: "0 0 2 * * ?"

	}
	def group = "MyGroup"


	def execute() {
		log.error "SupTimeComputationJob run!"
		def calendar = Calendar.instance
		
	//	computeYearSupTime(boolean isLastMonth)
		
		def isLastMonth = false
		
		log.error('computeYearSupTime called')
		def month
		def year
		//def calendar = Calendar.instance
		if (isLastMonth){
			month = calendar.get(Calendar.MONTH)
			year = calendar.get(Calendar.YEAR)
			if (month == 0){
				month = 1
				year = year - 1
			}
		} else{
			month = calendar.get(Calendar.MONTH)+1
			year = calendar.get(Calendar.YEAR)
		}
		
		def employees = Employee.findAll()
		log.error('there are '+employees.size()+' employees found')
		def counter = 1
		
		for (Employee employee: employees){
			Period period = (month>5)?Period.findByYear(year):Period.findByYear(year - 1)
			def data = timeManagerService.getYearSupTime(employee,year,month)
			def criteria = SupplementaryTime.createCriteria()
			def supTime = criteria.get {
				and {
					eq('employee',employee)
					eq('period',period)
					eq('month',month)
				}
				maxResults(1)
			}
			if (supTime == null){
				supTime = new SupplementaryTime( employee, period,  month, data.get('ajaxYearlySupTimeDecimal'))
			}else{
				supTime.value = data.get('ajaxYearlySupTimeDecimal')
			}
			supTime.save(flush: true)
		}
		
		
		log.error('computeYearSupTime ended')
		log.error('recreating job')
		
		
		
		
		
		if (calendar.get(Calendar.DAY_OF_YEAR) == calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
			calendar.roll(Calendar.YEAR,1)
			calendar.set(Calendar.DAY_OF_YEAR,1)
		}else{
			calendar.roll(Calendar.DAY_OF_YEAR,1)	
		}
		calendar.set(Calendar.HOUR_OF_DAY,2)
		calendar.set(Calendar.MINUTE,0)
		log.error 'registring SupTimeComputationJob at '+calendar.time
		
		this.schedule(calendar.time)
		
	}
	
}
