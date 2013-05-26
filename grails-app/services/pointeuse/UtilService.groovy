package pointeuse



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


class UtilService {

	def getLastWeekOfMonth(int month, int year){
		boolean isSunday=true
		Calendar calendar = Calendar.instance
		calendar.set(Calendar.MONTH,month-1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		def lastDay = calendar.get(Calendar.DAY_OF_WEEK)
		def lastWeek=calendar.get(Calendar.WEEK_OF_YEAR)		
		if (lastDay!=Calendar.SUNDAY){
			isSunday=false
		}
		return [calendar.get(Calendar.WEEK_OF_YEAR),isSunday]		
		
	}
	
	def getSundaysInYear(int year){
		def calendar = Calendar.instance
		calendar.set(Calendar.YEAR,year)		
		// set the date end of may
		calendar.set(Calendar.MONTH,4)
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		calendar.set(Calendar.YEAR,year-1)
		calendar.set(Calendar.MONTH,5)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.clearTime()
		def yearlyCounter = 0
		while(calendar.get(Calendar.DAY_OF_YEAR) <= calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
			if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				yearlyCounter ++
			}
			if (calendar.get(Calendar.DAY_OF_YEAR) == calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
				break
			}
			calendar.roll(Calendar.DAY_OF_YEAR, 1)
		}
		
		calendar.set(Calendar.DAY_OF_YEAR,1)
		calendar.set(Calendar.YEAR,year)

		def endPeriodCalendar = Calendar.instance
		
		endPeriodCalendar.set(Calendar.MONTH,4)
		endPeriodCalendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))

		while(calendar.get(Calendar.DAY_OF_YEAR) <= endPeriodCalendar.get(Calendar.DAY_OF_YEAR)){
			if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				yearlyCounter ++
			}
		
			if (calendar.get(Calendar.DAY_OF_YEAR) == endPeriodCalendar.get(Calendar.DAY_OF_YEAR)){
				break
			}
			calendar.roll(Calendar.DAY_OF_YEAR, 1)
		}
		
		return yearlyCounter
	}
	
	
	def removeAbsence(Employee employee,Calendar calendar){
		def criteria = Absence.createCriteria()
		def absence = criteria.get {
			and {
				eq('employee',employee)
				eq('day',calendar.get(Calendar.DATE))
				eq('month',calendar.get(Calendar.MONTH)+1)
				eq('year',calendar.get(Calendar.YEAR))
			}
		}
		if (absence!=null){
			absence.delete()
		}
	}
}
