package pointeuse



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


class UtilService {

	def springSecurityService
	
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
	
	def getWeeksfMonth(int month, int year){
		def weekList = []
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
		return [calendar.get(Calendar.WEEK_OF_YEAR),isSunday,weekList]
		
	}
	
	
	def getSundaysInYear(int year,int month){
		def calendar = Calendar.instance
		def endPeriodCalendar = Calendar.instance
		boolean twoLoops = month < 6 ? true : false
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
			log.debug("getting opened days from: "+calendar.time + " until end of year "+year-1)
			
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
			
			log.debug("getting opened days from: "+calendar.time + " until "+endPeriodCalendar.time)
			
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
			log.debug("getting opened days from: "+calendar.time + " until "+endPeriodCalendar.time)
			while(calendar.get(Calendar.DAY_OF_YEAR) <= endPeriodCalendar.get(Calendar.DAY_OF_YEAR)){
				//log.warn("date: "+calendar.time)
				if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
					yearlyCounter ++
				}
			
				if (calendar.get(Calendar.DAY_OF_YEAR) == endPeriodCalendar.get(Calendar.DAY_OF_YEAR)){
					break
				}
				calendar.roll(Calendar.DAY_OF_YEAR, 1)
			}		
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
	
	def initiateVacations(Period year){
		
		def user = springSecurityService.currentUser

		for (Employee employee:Employee.findAll("from Employee")){
			def vacationCA = new Vacation()
			vacationCA.employee = employee
			vacationCA.loggingTime = new Date()
			if (user){
				vacationCA.user=user
			}
			vacationCA.counter=32
			vacationCA.type=VacationType.CA
			vacationCA.period=year
			vacationCA.save()
			
			def vacationRTT = new Vacation()		
			vacationRTT.employee = employee
			vacationRTT.loggingTime = new Date()
			if (user){
				vacationRTT.user=user
			}
			vacationRTT.counter = employee.weeklyContractTime==35 ? 4 : 0
			vacationRTT.type=VacationType.RTT
			vacationRTT.period=year
			vacationRTT.save()
		}
	}
	
	def initiateVacations(Employee employee){
		
		def user = springSecurityService.currentUser

		for (Period period:Period.findAll("from Period")){
			def vacationCA = new Vacation()
			vacationCA.employee = employee
			vacationCA.loggingTime = new Date()
			if (user){
				vacationCA.user=user
			}
			vacationCA.counter=32
			vacationCA.type=VacationType.CA
			vacationCA.period=period
			vacationCA.save()
			
			def vacationRTT = new Vacation()
			vacationRTT.employee = employee
			vacationRTT.loggingTime = new Date()
			if (user){
				vacationRTT.user=user
			}
			vacationRTT.counter = employee.weeklyContractTime==35 ? 4 : 0
			vacationRTT.type=VacationType.RTT
			vacationRTT.period=period
			vacationRTT.save()
		}
	}
	
	def getOpenDays(Period period){
		
		def bankHolidayCounter=0
		def bankHolidayList
		def criteria
		def openDays=0
		def startCalendar = Calendar.instance
		def endCalendar   = Calendar.instance
		
		startCalendar.set(Calendar.YEAR,period.year)
		startCalendar.set(Calendar.MONTH,5)
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.clearTime()
		
		endCalendar.set(Calendar.YEAR,period.year+1)
		endCalendar.set(Calendar.MONTH,6)

		log.warn("getting opened days from: "+startCalendar.time + " until end of year "+period.year)
		
		while(startCalendar.get(Calendar.DAY_OF_YEAR) <= startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
			if (startCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				openDays ++
			}
			if (startCalendar.get(Calendar.DAY_OF_YEAR) == startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
				break
			}
			startCalendar.roll(Calendar.DAY_OF_YEAR, 1)
		}
		startCalendar.set(Calendar.YEAR,period.year+1)
		startCalendar.set(Calendar.MONTH,0)
		startCalendar.set(Calendar.DAY_OF_YEAR,1)
		
		log.warn("getting opened days from: "+startCalendar.time + " until "+endCalendar.time)
		
		while(startCalendar.get(Calendar.DAY_OF_YEAR) <= endCalendar.get(Calendar.DAY_OF_YEAR)){
			if (startCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				openDays ++
			}
			if (startCalendar.get(Calendar.DAY_OF_YEAR) == endCalendar.get(Calendar.DAY_OF_YEAR)){
				break
			}
			startCalendar.roll(Calendar.DAY_OF_YEAR, 1)	
		}
		
		
		criteria = BankHoliday.createCriteria()
		bankHolidayList = criteria.list{
			
			or{
				and {
					ge('month',6)
					le('month',12)
					eq('year',period.year)
				}
				and{
					ge('month',1)
					le('month',5)
					eq('year',period.year+1)
				}
			}
		}
	
		for (BankHoliday bankHoliday:bankHolidayList){
			if (bankHoliday.calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				bankHolidayCounter ++
			}
		}
		
		openDays -= bankHolidayCounter
		return openDays
	}
	
}
