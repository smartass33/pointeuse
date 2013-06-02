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
	
	def getSundaysInYear(int year,int month){
		def calendar = Calendar.instance
		def endPeriodCalendar = Calendar.instance
		
		boolean twoLoops = false
		if (month<6){
		//	year=year-1
			twoLoops = true
		}
		
		def currentDate = calendar.time
		
		calendar.set(Calendar.YEAR,year)		
		calendar.set(Calendar.MONTH,5)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.clearTime()
		
		endPeriodCalendar.set(Calendar.YEAR,year)
		endPeriodCalendar.set(Calendar.MONTH,month-1)
		endPeriodCalendar.set(Calendar.DAY_OF_MONTH,endPeriodCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		
		
		
		def yearlyCounter = 0
		
		if (twoLoops){
			calendar.set(Calendar.YEAR,year-1)
			log.warn("getting opened days from: "+calendar.time + " until end of year "+year-1)
			
			while(calendar.get(Calendar.DAY_OF_YEAR) <= calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
				if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
					yearlyCounter ++
				}
				if (calendar.get(Calendar.DAY_OF_YEAR) == calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
					break
				}
				calendar.roll(Calendar.DAY_OF_YEAR, 1)
			}
			calendar.set(Calendar.YEAR,year)
			calendar.set(Calendar.MONTH,0)
			calendar.set(Calendar.DAY_OF_YEAR,1)
			
			log.warn("getting opened days from: "+calendar.time + " until "+endPeriodCalendar.time)
			
			while(calendar.get(Calendar.DAY_OF_YEAR) <= endPeriodCalendar.get(Calendar.DAY_OF_YEAR)){
				if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
					yearlyCounter ++
				}
			
				if (calendar.get(Calendar.DAY_OF_YEAR) == endPeriodCalendar.get(Calendar.DAY_OF_YEAR)){
					break
				}
				calendar.roll(Calendar.DAY_OF_YEAR, 1)
			}
		}else{
			log.warn("getting opened days from: "+calendar.time + " until "+endPeriodCalendar.time)
			while(calendar.get(Calendar.DAY_OF_YEAR) <= endPeriodCalendar.get(Calendar.DAY_OF_YEAR)){
				if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
					yearlyCounter ++
				}
			
				if (calendar.get(Calendar.DAY_OF_YEAR) == endPeriodCalendar.get(Calendar.DAY_OF_YEAR)){
					break
				}
				calendar.roll(Calendar.DAY_OF_YEAR, 1)
			}		
		}
		
		/*
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

		
		endPeriodCalendar.set(Calendar.MONTH,4)
		endPeriodCalendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
*/

		
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
