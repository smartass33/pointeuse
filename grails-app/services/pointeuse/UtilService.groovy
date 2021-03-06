package pointeuse

class UtilService  {

	def springSecurityService
	
	def getLastWeekOfMonth(int month, int year){
		boolean isSunday=true
		Calendar calendar = Calendar.instance
		calendar.set(Calendar.MONTH,month-1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		def lastDay = calendar.get(Calendar.DAY_OF_WEEK)
		if (lastDay!=Calendar.SUNDAY){
			isSunday=false
		}
		return [calendar.get(Calendar.WEEK_OF_YEAR),isSunday]		
		
	}
	
	def getSaturdayOfMonth(Date currentDate){
		// retrieve all saturdays of a given month
		def saturdayList = []
		def calendar = Calendar.instance
		calendar.time = currentDate
		calendar.set(Calendar.DAY_OF_MONTH,1)
		
		while(calendar.get(Calendar.DAY_OF_MONTH) <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
				saturdayList.add(calendar.time)
			}
			if (calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendar.roll(Calendar.DAY_OF_MONTH,1)
		}
		return saturdayList
	}

	def getSaturdayBetweenDates(Date fromDate,Date toDate){
		// retrieve all saturdays between dates
		def saturdayList = []
		def calendar     = Calendar.instance
		def toCalendar   = Calendar.instance
		calendar.time    = fromDate
		toCalendar.time  = toDate
		
		
		
		
		// if calendar and toCalendar are in the same year
		while(calendar.time <= toCalendar.time){
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
				saturdayList.add(calendar.time)
			}
			if (calendar.time >= toCalendar.time){
				break
			}
			if (calendar.get(Calendar.DAY_OF_YEAR) == calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
				calendar.roll(Calendar.YEAR, 1)
				calendar.set(Calendar.DAY_OF_YEAR,calendar.getActualMinimum(Calendar.DAY_OF_YEAR))
				
			}else{
				calendar.roll(Calendar.DAY_OF_YEAR,1)
			}
		}
		return saturdayList
	}

	
	def getWeeksfMonth(int month, int year){
		def weekList = []		
		Calendar calendar = Calendar.instance
		calendar.set(Calendar.MONTH,month-1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))		
		boolean isSunday=(calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY)?false:true
		return [calendar.get(Calendar.WEEK_OF_YEAR),isSunday,weekList]		
	}
	
	
	def getYearlyCounter(int year,int month,Employee employee){
		def calendar = Calendar.instance
		def endPeriodCalendar = Calendar.instance
		boolean twoLoops = month < 6 ? true : false
		calendar.set(Calendar.YEAR,year)		
		calendar.set(Calendar.MONTH,5)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.clearTime()
		log.debug('initial calendar date: '+calendar.time )
		

		endPeriodCalendar.set(Calendar.YEAR,year)
		endPeriodCalendar.set(Calendar.MONTH,month-1)
		endPeriodCalendar.set(Calendar.DAY_OF_MONTH,endPeriodCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
	
		def yearlyCounter = 0
		
		if (twoLoops){
			calendar.set(Calendar.YEAR,year-1)
			// special case: employee entered the company within the period: we must start at entry date:
			if ((calendar.time < employee.arrivalDate) && (employee.arrivalDate.getAt(Calendar.YEAR) == calendar.get(Calendar.YEAR))){
				calendar.setTime(employee.arrivalDate)
				calendar.clearTime()
				log.debug('resetting calendar: '+calendar.time )
			}
			log.debug("getting opened days from: "+calendar.time + " until end of year "+calendar.get(Calendar.YEAR))
			
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
			// special case: employee entered the company within the period: we must start at entry date:
			if ((calendar.time < employee.arrivalDate) && (employee.arrivalDate.getAt(Calendar.YEAR) == calendar.get(Calendar.YEAR))){
				calendar.setTime(employee.arrivalDate)
				calendar.clearTime()
				log.debug('resetting calendar: '+calendar.time )
			}
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
		for (Employee employee:Employee.findAll()){
			def vacationCA = new Vacation()
			vacationCA.employee = employee
			vacationCA.loggingTime = new Date()
			if (user){
				vacationCA.user=user
			}			
			vacationCA.counter = (employee.status.type == StatusType.ACTIF) ? 32 : 0
			vacationCA.type=VacationType.CA
			vacationCA.period=year
			vacationCA.save()
			
			def vacationRTT = new Vacation()		
			vacationRTT.employee = employee
			vacationRTT.loggingTime = new Date()
			if (user){
				vacationRTT.user=user
			}
			vacationRTT.counter = (employee.weeklyContractTime == 35 && employee.status.type == StatusType.ACTIF) ? 4 : 0
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
	
	
	def getAllOpenDays(int month,int year){
		
		def bankHolidayCounter=0
		def bankHolidayList
		def criteria
		def openDays=0
		def startCalendar = Calendar.instance
		def endCalendar   = Calendar.instance
		
		startCalendar.set(Calendar.YEAR,year)
		startCalendar.set(Calendar.MONTH,month-1)
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.clearTime()
		log.warn("getting all opened days of: "+startCalendar.time )
		
		while(startCalendar.get(Calendar.DAY_OF_MONTH) <= startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			if (startCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				openDays ++
			}
			if (startCalendar.get(Calendar.DAY_OF_MONTH) == startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			startCalendar.roll(Calendar.DAY_OF_MONTH, 1)
		}
		return openDays
	}
	
	
	
	def getOpenDays(Period period){
		def openDays=0
		def startCalendar = Calendar.instance
		def criteria
		startCalendar.set(Calendar.YEAR,period.year)
		startCalendar.set(Calendar.MONTH,5)
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.clearTime()
		def bankHolidayCounter = 0
		
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
		def bankHolidayList = criteria.list{
			
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
	
	def detectCollidingContract(Date startDate,Date endDate,Employee employee){
		def criteria = Contract.createCriteria()
		def contractList
		def retour = false
		contractList = criteria.list{
			or{
				and{
					le('startDate',startDate)
					ge('endDate',startDate)
					eq('employee',employee)
				}
				and{
					le('startDate',endDate)
					ge('endDate',endDate)
					eq('employee',employee)				
				}
			}
		}
		
		if (contractList != null && contractList.size() > 0)
			retour = true
		return retour
	}
	
	boolean getActiveFullTimeContract(int month, int year,Employee employee){
		
		def criteria = Contract.createCriteria()
		
		def startCalendar = Calendar.instance
		def endCalendar = Calendar.instance
		
		startCalendar.set(Calendar.YEAR,year)
		startCalendar.set(Calendar.MONTH,month - 1)
		startCalendar.clearTime()
		
		endCalendar.set(Calendar.DAY_OF_MONTH,startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
		
		def montlyContracts = criteria.list {
			or{
				// all contracts that close during the month
				and {
					ge('endDate',startCalendar.time)
					le('endDate',endCalendar.time)
					eq('employee',employee)
					eq('weeklyLength',35 as float)
				}
				
				// add the contract that is not over, unless its startdate is ulterior to the end of the current month
				and {
					le('startDate',endCalendar.time)
					isNull('endDate')
					eq('employee',employee)
					eq('weeklyLength',35 as float)
					
				}
				
			}
			order('startDate','desc')
		}
		
		if (montlyContracts != null && montlyContracts.size() > 0){
			return true
		}else {
			return false
		}
	}
	
	
	def getCurrentActiveContract(Employee employee,int month,int year){
		def previousContracts
		def startCalendar = Calendar.instance
		def endCalendar = Calendar.instance
		def criteria = Contract.createCriteria()
		startCalendar.set(Calendar.MONTH,month - 1)
		startCalendar.set(Calendar.YEAR,year)
		startCalendar.clearTime()
		
		endCalendar.set(Calendar.MONTH,month - 1)
		endCalendar.set(Calendar.YEAR,year)
		endCalendar.set(Calendar.HOUR,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
				
		previousContracts = criteria.list {
			or{
				// all contracts that close during the month
				and {
					ge('endDate',startCalendar.time)
					le('endDate',endCalendar.time)
					eq('employee',employee)
				}
				
				// add the contract that is not over, unless its startdate is ulterior to the end of the current month
				and {
					le('startDate',endCalendar.time)
					isNull('endDate')
					eq('employee',employee)
				}
				
			}
			order('startDate','desc')
		}
	}
	
	def rollAllYear(Period period){
		def monthList = [6,7,8,9,10,11,12,1,2,3,4,5]
		def year = period.year
		def currentYear = year
		def currentWeek = 0
		def criteria
		for (int currentMonth in monthList){
	
			if (currentMonth == 1){
				currentYear = year + 1
			}
		
			//finding time done during sundays
			def tmpCal = Calendar.instance
			tmpCal.set(Calendar.MONTH,currentMonth - 1)
			tmpCal.set(Calendar.YEAR,currentYear)
			tmpCal.clearTime()
			tmpCal.set(Calendar.DATE,1)
			
			log.debug('current month: '+currentMonth)
			log.debug('current year: ' +currentYear)
			log.debug('tmpCal: '+tmpCal.time)
			log.debug('last day of month: '+tmpCal.getActualMaximum(Calendar.DAY_OF_MONTH))
					
			while(tmpCal.get(Calendar.DAY_OF_MONTH) <= tmpCal.getActualMaximum(Calendar.DAY_OF_MONTH)){
				if (currentWeek != tmpCal.get(Calendar.WEEK_OF_YEAR)){
					log.error('tmpCal.time: '+tmpCal.time)
					currentWeek = tmpCal.get(Calendar.WEEK_OF_YEAR)
					
					criteria = WeeklyTotal.createCriteria()
					def weeklyTotals = criteria.get {
						and {
							eq('month',tmpCal.get(Calendar.MONTH) + 1)
							eq('year',tmpCal.get(Calendar.YEAR))
							eq('week',tmpCal.get(tmpCal.get(Calendar.WEEK_OF_YEAR)))
						}
					}
				}
				
				log.debug('tmpCal: '+tmpCal.time)
				if (tmpCal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY && tmpCal.get(Calendar.MONTH) == currentMonth - 1){
					log.debug("sunday: "+tmpCal.time)
				}
				tmpCal.roll(Calendar.DAY_OF_YEAR, 1)
				if (tmpCal.get(Calendar.DAY_OF_MONTH) == tmpCal.getActualMaximum(Calendar.DAY_OF_MONTH))	{
					log.debug('adding sunday time: '+tmpCal.time)
					break;
				}
			}

		}
	}
}
