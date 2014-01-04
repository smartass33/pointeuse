package pointeuse

import groovy.time.TimeDuration;
import groovy.time.TimeCategory


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

class TimeManagerService {

	def springSecurityService
	def utilService
	def employeeService
	def addExtraTime(InAndOut inOrOut,TimeDuration extraTime){
		if (inOrOut.dailyTotal==null){
			def totals=initializeTotals(inOrOut.employee,inOrOut.time)
			def dailyTotal=totals.getAt(0)
			inOrOut.dailyTotal=dailyTotal
		}
		inOrOut.dailyTotal.elapsedSeconds+=extraTime.hours*3600+extraTime.minutes*60+extraTime.seconds
		inOrOut.dailyTotal.weeklyTotal.elapsedSeconds+=extraTime.hours*3600+extraTime.minutes*60+extraTime.seconds
		inOrOut.dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds+=extraTime.hours*3600+extraTime.minutes*60+extraTime.seconds
		return inOrOut
	}
	
	def computeSupplementaryTime(Employee employee,int week, int year){
		def dailySupplementarySeconds = 0
		def weeklySupplementarySeconds = 0
		def dailyTotalSum=0
		def criteria
		def dailyTotals
		def bankHolidays
		def calendar = Calendar.instance
		def bankHolidayCounter = 0
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.WEEK_OF_YEAR,week)
		
		criteria = BankHoliday.createCriteria()
		bankHolidays = criteria.list {
			and {
				eq('year',year)
				eq('month',calendar.get(Calendar.MONTH)+1)
			}
		}
		
		for (BankHoliday bankHoliday:bankHolidays){
			if (bankHoliday.calendar.get(Calendar.WEEK_OF_YEAR)==week){
				bankHolidayCounter+=1
			}
		}
		
		
		criteria = DailyTotal.createCriteria()
		dailyTotals = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('week',week)
			}
		}
		criteria = WeeklyTotal.createCriteria()
		for (DailyTotal tmpDaily:dailyTotals){
			def tmpElapsed = getDailyTotal(tmpDaily)
			dailyTotalSum += tmpElapsed
			dailySupplementarySeconds += Math.max(tmpElapsed-DailyTotal.maxWorkingTime, 0)
		}
		if (dailyTotalSum<=1.2*3600*(WeeklyTotal.WeeklyLegalTime-bankHolidayCounter*(Employee.legalWeekTime/Employee.WeekOpenedDays))){
			weeklySupplementarySeconds = dailySupplementarySeconds
		}else {
			weeklySupplementarySeconds = Math.max(dailySupplementarySeconds,dailyTotalSum-1.2*3600*(WeeklyTotal.WeeklyLegalTime-bankHolidayCounter*(Employee.legalWeekTime/Employee.WeekOpenedDays)))
		}
		return weeklySupplementarySeconds
	}
		

	

	
	def initializeTotals(Employee employee, Date currentDate,String type,def event, Boolean isOutside ){
		def criteria = MonthlyTotal.createCriteria()
		def monthlyTotal = criteria.get {
				and {
					eq('employee',employee)
					eq('year',currentDate.getAt(Calendar.YEAR))
					eq('month',currentDate.getAt(Calendar.MONTH)+1)
				}
			}
		
		if (monthlyTotal==null){
			monthlyTotal = new MonthlyTotal(employee,currentDate)
			employee.monthlyTotals.add(monthlyTotal)
			monthlyTotal.save(flush:true)
			
		}
		
		criteria = WeeklyTotal.createCriteria()
		def weeklyTotal = criteria.get {
			and {
				eq('employee',employee)
				eq('year',currentDate.getAt(Calendar.YEAR))
				eq('month',currentDate.getAt(Calendar.MONTH)+1)
				eq('week',currentDate.getAt(Calendar.WEEK_OF_YEAR))
			}
		}
		
		if (weeklyTotal == null){
			weeklyTotal = new WeeklyTotal(employee,currentDate)
			monthlyTotal.weeklyTotals.add(weeklyTotal)
			weeklyTotal.monthlyTotal=monthlyTotal
			weeklyTotal.save(flush:true)
			
		}

		criteria = DailyTotal.createCriteria()
		def dailyTotal = criteria.get {
			and {
				eq('employee',employee)
				eq('year',currentDate.getAt(Calendar.YEAR))
				eq('month',currentDate.getAt(Calendar.MONTH)+1)
				eq('day',currentDate.getAt(Calendar.DAY_OF_MONTH))
			}
		}
		if (dailyTotal==null){
			dailyTotal = new DailyTotal(employee,currentDate)
			dailyTotal.weeklyTotal=weeklyTotal
			weeklyTotal.dailyTotals.add(dailyTotal)
			dailyTotal.save(flush:true)
			
		}
		 
		if (event==null){
			def inOrOut = new InAndOut(employee, currentDate,type,isOutside)
			inOrOut.dailyTotal=dailyTotal
			dailyTotal.inAndOuts.add(inOrOut)
			employee.inAndOuts.add(inOrOut)
			dailyTotal.exitCount=dailyTotal.exitCount+1
			return inOrOut
			
		}else{
			event.dailyTotal=dailyTotal
			return event
		}
	}
	
	
	def initializeTotals(Employee employee, Date currentDate){
		def criteria = MonthlyTotal.createCriteria()
		def monthlyTotal = criteria.get {
				and {
					eq('employee',employee)
					eq('year',currentDate.getAt(Calendar.YEAR))
					eq('month',currentDate.getAt(Calendar.MONTH)+1)
				}
			}
		
		if (monthlyTotal==null){
			monthlyTotal = new MonthlyTotal(employee,currentDate)
			employee.monthlyTotals.add(monthlyTotal)
			monthlyTotal.save(flush:true)
			
		}
		
		criteria = WeeklyTotal.createCriteria()
		def weeklyTotal = criteria.get {
			and {
				eq('employee',employee)
				eq('year',currentDate.getAt(Calendar.YEAR))
				eq('month',currentDate.getAt(Calendar.MONTH)+1)
				eq('week',currentDate.getAt(Calendar.WEEK_OF_YEAR))
			}
		}
		
		if (weeklyTotal == null){
			weeklyTotal = new WeeklyTotal(employee,currentDate)
			monthlyTotal.weeklyTotals.add(weeklyTotal)
			weeklyTotal.monthlyTotal=monthlyTotal
			weeklyTotal.save(flush:true)
			
		}

		criteria = DailyTotal.createCriteria()
		def dailyTotal = criteria.get {
			and {
				eq('employee',employee)
				eq('year',currentDate.getAt(Calendar.YEAR))
				eq('month',currentDate.getAt(Calendar.MONTH)+1)
				eq('day',currentDate.getAt(Calendar.DAY_OF_MONTH))
			}
		}
		if (dailyTotal==null){
			dailyTotal = new DailyTotal(employee,currentDate)
			dailyTotal.weeklyTotal=weeklyTotal
			weeklyTotal.dailyTotals.add(dailyTotal)
			dailyTotal.save(flush:true)
			
		}
		return [dailyTotal,weeklyTotal,monthlyTotal]
	}
	
	
	def regularizeTime(String type,String userId,InAndOut inOrOut,Calendar calendar){
		if (calendar == null){
			calendar = Calendar.instance
		}
		def dailyTotal
		def currentDate = calendar.time
		def employee = Employee.get(userId)
		def criteria
	
		criteria = InAndOut.createCriteria()
		def todayEmployeeEntries = criteria.list {
			and {
				eq('employee',employee)
				eq('year',calendar.get(Calendar.YEAR))
				eq('month',calendar.get(Calendar.MONTH)+1)
				eq('day',calendar.get(Calendar.DAY_OF_MONTH))
				eq('type','E')
			}
		}
		
		if (todayEmployeeEntries != null && todayEmployeeEntries.size() > Employee.entryPerDay){
			flash.message = "TROP D'ENTREES DANS LA JOURNEE. POINTAGE NON PRIS EN COMPTE"
			redirect(action: "show", id: employee.id)
			return
		}
		employee.inAndOuts.add(inOrOut)
		
		
		//getDailyTotal(inOrOut.dailyTotal)
		getDailyTotalWithMonth(inOrOut.dailyTotal)
		
		utilService.removeAbsence( employee, calendar)
		employee.status=type.equals("S")?false:true
	}
		
	def timeModification(def idList,def timeList,def dayList,def monthList, def yearList,Employee employee,def newTimeList,def fromRegularize) throws PointeuseException{
		def deltaUp
		def deltaDown
		def timeDiff
		def criteria
		def toCompare
		def calendar = Calendar.instance
		def user = springSecurityService.currentUser

		if (idList instanceof String){
			def tmpidList=idList
			def tmpdayList=dayList
			def tmpmonthList=monthList
			def tmpyearList=yearList
			def tmpnewTimeList=newTimeList
			idList=[]
			dayList=[]
			monthList=[]
			yearList=[]
			newTimeList=[]
			idList.add(tmpidList)
			dayList.add(tmpdayList)
			monthList.add(tmpmonthList)
			yearList.add(tmpyearList)
			newTimeList.add(tmpnewTimeList)
		}
		def month=monthList[0]
		def year=yearList[0]
		
		
		for( int p = 0 ; ( p < idList.size() ) ; p++ ) {
		// l'id�e: comparer time et newTime
			def inOrOut = InAndOut.get(idList[p])
			if (inOrOut==null){
				log.error("InAndOut with id= "+idList[p]+" cannot be found. exiting timeModification")
				throw new PointeuseException("inAndOut.not.found")
				return
			}
			
			SimpleDateFormat dateFormat = new SimpleDateFormat('d/M/yyyy H:m');
			def newCalendar = Calendar.instance
			newCalendar.time=dateFormat.parse(dayList[p]+"/"+monthList[p]+"/"+yearList[p]+" "+newTimeList[p])
			def oldCalendar = Calendar.instance
			oldCalendar.time=inOrOut.time
			newCalendar.set(Calendar.SECOND,oldCalendar.get(Calendar.SECOND))
			
			if (newCalendar.time!=oldCalendar.time){
				// compare initial states:
				criteria = InAndOut.createCriteria()
				def inititalPrevious = criteria.get {
					and {
						eq('employee',employee)
						eq('day',inOrOut.day)
						eq('month',inOrOut.month)
						eq('year',inOrOut.year)
						lt('time',inOrOut.time)
						lt('id',inOrOut.id)
						order('time','desc')
					}
					maxResults(1)
				}
				
				criteria = InAndOut.createCriteria()
				def initialNext = criteria.get {
					and {
						eq('employee',employee)
						eq('day',inOrOut.day)
						eq('month',inOrOut.month)
						eq('year',inOrOut.year)
						gt('time',inOrOut.time)
						gt('id',inOrOut.id)
						order('time','asc')
					}
					maxResults(1)
				}
				

				toCompare=(newCalendar.time > oldCalendar.time) ? newCalendar.time : oldCalendar.time
				toCompare.set(minutes:toCompare.minutes+1)
					
				// get next inOrOut for this user
				calendar.time=oldCalendar.time
				criteria = InAndOut.createCriteria()
				def nextInOrOut = criteria.get {
					and {
						eq('employee',employee)
						eq('day',calendar.getAt(Calendar.DAY_OF_MONTH))
						eq('month',calendar.getAt(Calendar.MONTH)+1)
						eq('year',calendar.getAt(Calendar.YEAR))
						gt('id',inOrOut.id)
						gt('time',toCompare)
						order('time','asc')
					}
					maxResults(1)
				}
				
				if (nextInOrOut!=null && initialNext != null && nextInOrOut!=initialNext){
					throw new PointeuseException('inAndOut.updateTime.error')
					return
				}
				
				toCompare=(newCalendar.time > oldCalendar.time) ? oldCalendar.time : newCalendar.time
				toCompare.set(minutes:toCompare.minutes+1)
				
				criteria = InAndOut.createCriteria()
				def previousInOrOut = criteria.get {
					and {
						eq('employee',employee)
						eq('day',calendar.get(Calendar.DAY_OF_MONTH))
						eq('month',calendar.get(Calendar.MONTH)+1)
						eq('year',calendar.get(Calendar.YEAR))
						lt('time',toCompare)
						lt('id',inOrOut.id)
						order('time','desc')
					}
					maxResults(1)
				}
			
				if (previousInOrOut!=null && inititalPrevious != null && previousInOrOut!=inititalPrevious){
					log.error('entry cannot be positionned after exit')
					throw new PointeuseException('inAndOut.updateTime.error')
					return
				}
				inOrOut.time=newCalendar.time
				getDailyTotalWithMonth(inOrOut.dailyTotal)
				
				inOrOut.regularizationType=fromRegularize ? InAndOut.MODIFIEE_SALARIE : InAndOut.MODIFIEE_ADMIN
				inOrOut.systemGenerated=false
				
				if (user!=null){
					inOrOut.modifyingUser=user
					inOrOut.modifyingTime=new Date()
					log.error("user "+user?.username+" modified "+inOrOut)
				}
			}
		}
	}
	

	def computeMonthlyHours(int year,int month){
		def openedDays = 0
		def calendar = Calendar.instance
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.MONTH,month)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		
		// discard sundays from opened days
		while(calendar.get(Calendar.DAY_OF_MONTH) <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			if (calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
				openedDays += 1
			}
			
			if (calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendar.roll(Calendar.DAY_OF_MONTH, 1)
		}
		def bankHolidays = BankHoliday.findAllByYearAndMonth(year,month+1)
		// discard bank holidays
		if (bankHolidays!= null){
			openedDays -= bankHolidays.size()
		}
		return openedDays
	}
	
	def computeHumanTime(long inputSeconds){
		def diff=inputSeconds
		long hours=TimeUnit.SECONDS.toHours(diff);
		diff=diff-(hours*3600);
		long minutes=TimeUnit.SECONDS.toMinutes(diff);
		diff=diff-(minutes*60);
		long seconds=TimeUnit.SECONDS.toSeconds(diff);
		return [hours,minutes,seconds]
	}

	
	def recomputeDailyTotals(long id){
		def tmpInOrOut
		def dailyDelta=0
		def timeDiff
		def criteria
		def employeeList = Employee.findAll()
		
		for (Employee employee:employeeList){
			criteria = DailyTotal.createCriteria()
			def dailyTotalList = DailyTotal.findByEmployee(employee)
			
			for (DailyTotal dailyTotal: dailyTotalList){
				tmpInOrOut=null
				dailyDelta=0
				criteria=InAndOut.createCriteria()
				def inOrOutList = criteria.list {
					and {
						eq('employee',employee)
						eq('year',year)
						eq('month',month)
						eq('day',dailyTotal.day)
					}
					order('time','asc')
				}
				
				for (InAndOut inOrOut:inOrOutList){
					if (inOrOut.type.equals("E")){
						tmpInOrOut=inOrOut
					}else{
						if (tmpInOrOut!=null){
							use (TimeCategory){timeDiff=inOrOut.time-tmpInOrOut.time}
							dailyDelta+=timeDiff.seconds + timeDiff.minutes*60+timeDiff.hours*3600
						}
					}
				}
				dailyTotal.elapsedSeconds=dailyDelta
				dailyTotal.weeklyTotal.elapsedSeconds+=dailyDelta
				dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds+=dailyDelta
			}
		}
		
	}
	
	def recomputeDailyTotals(int userId,int day,int month,int year){
		Employee employee = Employee.get(userId)
		
		def tmpInOrOut
		def dailyDelta=0
		def timeDiff
		def criteria = DailyTotal.createCriteria()
		def dailyTotal = criteria.get {
				and {
					eq('employee',employee)
					eq('year',year)
					eq('month',month)
					eq('day',day)
				}
			}
		
	
		criteria=InAndOut.createCriteria()
		def inOrOutList = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('day',dailyTotal.day)
			}
			order('time','asc')
		}
		
		for (InAndOut inOrOut:inOrOutList){
			if (inOrOut.type.equals("E")){
				tmpInOrOut=inOrOut
			}else{
				if (tmpInOrOut!=null){
					use (TimeCategory){timeDiff=inOrOut.time-tmpInOrOut.time}
					dailyDelta+=timeDiff.seconds + timeDiff.minutes*60+timeDiff.hours*3600
				}
			}
		}
		dailyTotal.elapsedSeconds=dailyDelta
		return dailyTotal
	}
	def getDailyTotal(DailyTotal dailyTotal){
		def criteria = InAndOut.createCriteria()
		def elapsedSeconds = 0
		def tmpInOrOut
		def timeDifference
		def inOrOutList = criteria.list {
			and {
				eq('employee',dailyTotal.employee)
				eq('year',dailyTotal.year)
				eq('month',dailyTotal.month)
				eq('day',dailyTotal.day)
			}
			order('time','asc')
		}
		
		for (InAndOut inOrOut:inOrOutList){
			if (inOrOut.type.equals("E")){
				tmpInOrOut=inOrOut
			}else{
				if (tmpInOrOut!=null){
					use (TimeCategory){timeDifference=inOrOut.time-tmpInOrOut.time}
					elapsedSeconds+=timeDifference.seconds + timeDifference.minutes*60+timeDifference.hours*3600
				}
			}
		}
		dailyTotal.elapsedSeconds=elapsedSeconds
		return elapsedSeconds
	}
	
	def getDailyTotalWithMonth(DailyTotal dailyTotal){
		def criteria = InAndOut.createCriteria()
		def elapsedSeconds = 0
		def tmpInOrOut
		def timeDifference
		def deltaTime
		def inOrOutList = criteria.list {
			and {
				eq('employee',dailyTotal.employee)
				eq('year',dailyTotal.year)
				eq('month',dailyTotal.month)
				eq('day',dailyTotal.day)
			}
			order('time','asc')
		}
		
		for (InAndOut inOrOut:inOrOutList){
			if (inOrOut.type.equals("E")){
				tmpInOrOut=inOrOut
			}else{
				if (tmpInOrOut!=null){
					use (TimeCategory){timeDifference=inOrOut.time-tmpInOrOut.time}
					elapsedSeconds+=timeDifference.seconds + timeDifference.minutes*60+timeDifference.hours*3600
				}
			}
		}
		deltaTime=dailyTotal.elapsedSeconds - elapsedSeconds//old-new
		dailyTotal.elapsedSeconds=elapsedSeconds
		dailyTotal.weeklyTotal.elapsedSeconds -= deltaTime
		dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds -= deltaTime

		return elapsedSeconds
	}
	
	def updateWeekAndMonth(DailyTotal dailyTotal, int delta){
		def criteria
		def previousValue
		def monthlyTotal = criteria.get {
			and {
				eq('employee',dailyTotal.employee)
				eq('year',dailyTotal.year)
				eq('month',dailyTotal.month)
			}
		}
		previousValue=monthlyTotal.elapsedSeconds
	}
	
	
	def getDailyTotal(def inOrOutList){
		def criteria = InAndOut.createCriteria()
		def elapsedSeconds = 0
		def tmpInOrOut
		def timeDifference
	
		for (InAndOut inOrOut:inOrOutList){
			if (inOrOut.type.equals("E")){
				tmpInOrOut=inOrOut
			}else{
				if (tmpInOrOut!=null){
					use (TimeCategory){timeDifference=inOrOut.time-tmpInOrOut.time}
					elapsedSeconds+=timeDifference.seconds + timeDifference.minutes*60+timeDifference.hours*3600
				}
			}
		}
		return elapsedSeconds
	}

	
	def getYearlyTotalTime(Employee employee, int year){
		def criteria
		def dailyTotal
		def elapsedSeconds = 0
		Calendar calendar = Calendar.instance
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
			criteria = DailyTotal.createCriteria()
			dailyTotal = criteria.get {
					and {
						eq('employee',employee)
						eq('year',calendar.get(Calendar.YEAR))
						eq('month',calendar.get(Calendar.MONTH)+1)
						eq('day',calendar.get(Calendar.DAY_OF_MONTH))
					}
				}
			if (dailyTotal!=null){
				elapsedSeconds += dailyTotal.elapsedSeconds//getDailyTotal(dailyTotal)
				//elapsedSeconds += getDailyTotal(dailyTotal)
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
			criteria = DailyTotal.createCriteria()
			dailyTotal = criteria.get {
					and {
						eq('employee',employee)
						eq('year',calendar.get(Calendar.YEAR))
						eq('month',calendar.get(Calendar.MONTH)+1)
						eq('day',calendar.get(Calendar.DAY_OF_MONTH))
					}
				}
			if (dailyTotal!=null){
				elapsedSeconds += dailyTotal.elapsedSeconds
				//elapsedSeconds += getDailyTotal(dailyTotal)
			}
			if (calendar.get(Calendar.DAY_OF_YEAR) == endPeriodCalendar.get(Calendar.DAY_OF_YEAR)){
				break
			}
			calendar.roll(Calendar.DAY_OF_YEAR, 1)
		}
		
		
	return elapsedSeconds
	}
	
	
	def getYearCartoucheData(Employee employee,int year,int month){
		def monthNumber=0
		def yearTheoritical
		def calendar = Calendar.instance
		calendar.set(Calendar.YEAR,year)
		def yearlyCounter = 0
		def totalTime = 0
		def bankHolidayCounter=0
		def bankHolidayList
		def criteria = Absence.createCriteria()
		
		if (month>5){
			year=year+1
			monthNumber=month-6+1
		}else{
			monthNumber=month+1+6
		}
		
		// set the date end of may
		calendar.set(Calendar.MONTH,month-1)
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		def maxDate = calendar.time
		calendar.set(Calendar.YEAR,year-1)
		calendar.set(Calendar.MONTH,5)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.clearTime()
		def minDate = calendar.time
		
		// get cumul holidays
		def yearlyHolidays = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.VACANCE)
			}
		}
		
		criteria = Absence.createCriteria()
		def yearlyRtt = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.RTT)
			}
		}
		
		criteria = Absence.createCriteria()
		def yearlySickness = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.MALADIE)
			}
		}
		
		criteria = Absence.createCriteria()
		def pregnancy = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.GROSSESSE)
			}
		}
		
		criteria = Absence.createCriteria()
		def yearlySansSolde = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.CSS)
			}
		}
				
		def monthsAggregate
		if (month>6){
			criteria = MonthlyTotal.createCriteria()
			
			monthsAggregate = criteria.list{
				and {
					eq('employee',employee)
					ge('month',6)
					le('month',month)
					eq('year',year-1)
				}
			}	
		}else{
			criteria = MonthlyTotal.createCriteria()	
			monthsAggregate = criteria.list{
				// year -1
				or{
					and {
						eq('employee',employee)
						ge('month',6)
						le('month',12)
						eq('year',year-1)
					}
					and {
						eq('employee',employee)
						le('month',month)
						eq('year',year)
					}
				}
			}
		}

		for (MonthlyTotal monthIter:monthsAggregate){			
			totalTime += monthIter.elapsedSeconds
		}

		
		yearlyCounter=month>5 ? utilService.getSundaysInYear(year-1,month) : utilService.getSundaysInYear(year,month)

		criteria = BankHoliday.createCriteria()
		
		if (month<6){
			bankHolidayList = criteria.list{
				or{
					and {
						ge('month',1)
						le('month',month)
						eq('year',year)
					}
					and{
						ge('month',6)
						le('month',12)
						eq('year',year-1)
					}
				}
			}
		}
		else{
			bankHolidayList = criteria.list{
				and {
					ge('month',6)
					le('month',month)
					eq('year',year-1)
				}
			}
		}
		
		for (BankHoliday bankHoliday:bankHolidayList){
			if (bankHoliday.calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				bankHolidayCounter ++
			}
		}
		
		yearlyCounter -= bankHolidayCounter
		def yearlyPregnancyCredit=30*60*pregnancy.size()
		yearTheoritical=3600*(
					yearlyCounter*employee.weeklyContractTime/Employee.WeekOpenedDays
					+ (Employee.Pentecote*monthNumber)*(employee.weeklyContractTime/Employee.legalWeekTime)
					-(employee.weeklyContractTime/Employee.WeekOpenedDays)*(yearlySickness.size()+yearlyHolidays.size()+yearlySansSolde.size())
					-pregnancy.size()
					) as int
		
		return [yearlyActualTotal:yearlyCounter ,yearlyHolidays:yearlyHolidays.size(),yearlyRtt:yearlyRtt.size(),yearlySickness:yearlySickness.size(),yearlyTheoritical:yearTheoritical,yearlyPregnancyCredit:yearlyPregnancyCredit,yearlyTotalTime:totalTime,yearlySansSolde:yearlySansSolde.size()]
	}
	
	def getCartoucheData(Employee employeeInstance,int year,int month){
		def counter = 0
		def holidayList
		def holidayCounter = 0
		def monthTheoritical
		def criteria
		def calendar = Calendar.instance
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.MONTH,month-1)
		
		// count sundays within given month
		while(calendar.get(Calendar.DAY_OF_YEAR) <= calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
			if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				counter ++
			}
			if (calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendar.roll(Calendar.DAY_OF_MONTH, 1)
		}
		
		// count bank holiday
		holidayList = BankHoliday.findAllByMonthAndYear(month,calendar.get(Calendar.YEAR))

		for (BankHoliday holiday:holidayList){
			if (holiday.calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
				holidayCounter++
			}
		}
				
		counter -= holidayCounter

		// get cumul holidays
		criteria = Absence.createCriteria()
		def holidays = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.VACANCE)
			}
		}
		
		// get cumul RTT
		criteria = Absence.createCriteria()
		def rtt = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.RTT)
			}
		}
		// get cumul sickness
		criteria = Absence.createCriteria()
		def sickness = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.MALADIE)
			}
		}
		
		criteria = Absence.createCriteria()
		def pregnancy = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.GROSSESSE)
			}
		}
		
		criteria = Absence.createCriteria()
		def sansSolde = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.CSS)
			}
		}
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		calendar.set(Calendar.DATE,1)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.MONTH,month-1)
	
		def yearlyCartouche=getYearCartoucheData(employeeInstance,year,month)
		def pregnancyCredit=30*60*pregnancy.size()
		// determine monthly theoritical time:
		
		monthTheoritical=getMonthTheoritical(employeeInstance,  month, year, counter,  sickness.size(), holidays.size(), sansSolde.size(),  pregnancyCredit)
		/*monthTheoritical=(
		3600*(counter*employeeInstance.weeklyContractTime/Employee.WeekOpenedDays
			+(Employee.Pentecote)*(employeeInstance.weeklyContractTime/Employee.legalWeekTime)
			-(employeeInstance.weeklyContractTime/Employee.WeekOpenedDays)*(sickness.size()+holidays.size()+sansSolde.size()))
			- pregnancyCredit)as int
		*/
		def monthTheoriticalHuman=computeHumanTime(monthTheoritical)
		def cartoucheMap=[employeeInstance:employeeInstance,workingDays:counter ,holidays:holidays.size(),rtt:rtt.size(),sickness:sickness.size(),sansSolde:sansSolde.size(),monthTheoritical:monthTheoritical,pregnancyCredit:pregnancyCredit,monthTheoriticalHuman:monthTheoriticalHuman,calendar:calendar]
		def mergedMap = cartoucheMap << yearlyCartouche
		return mergedMap
	}
	
	
	def getReportData(String siteId,Employee employee, Date myDate,int monthPeriod,int yearPeriod){
		def calendar = Calendar.instance
		def weekName="semaine "
		def weeklyTotalTime = [:]
		def weeklySuppTotalTime = [:]
		def weeklyCompTotalTime = [:]
		def weeklyTotalTimeByEmployee = [:]
		def weeklySupTotalTimeByEmployee = [:]
		def monthlyTotalTimeByEmployee = [:]
		def weeklyAggregate = [:]
		def dailyTotalMap = [:]
		def dailyBankHolidayMap = [:]
		def dailySupTotalMap = [:]
		def holidayMap = [:]
		def previousDay
		def mapByDay = [:]
		def dailyTotalId=0
		def monthlySupTime = 0
		def monthlyTotalTime = 0
		def monthlyCompTime = 0
		def criteria
		def dailySeconds = 0
		def weeklySupTime
		def currentWeek=0
		def yearInf
		def yearSup

		//get last day of the month
		if (myDate==null){
			if (yearPeriod!=0){
				calendar.set(Calendar.MONTH,monthPeriod-1)
				calendar.set(Calendar.YEAR,yearPeriod)
			}
			calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH))
			calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		}else{
			calendar.time=myDate
		}
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		calendar.set(Calendar.DATE,1)
		
		
		def calendarLoop = calendar
		int month = calendar.get(Calendar.MONTH)+1 // starts at 0
		int year = calendar.get(Calendar.YEAR)
		calendarLoop.getTime().clearTime()
		def lastWeekParam = utilService.getLastWeekOfMonth(month, year)
		def isSunday=lastWeekParam.get(1)
		
		
		while(calendarLoop.get(Calendar.DAY_OF_MONTH) <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			def currentDay=calendarLoop.time
			// �limine les dimanches du rapport
			if (calendarLoop.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY){
				mapByDay = [:]
			}
			//print calendarLoop.time
			criteria = DailyTotal.createCriteria()
			def dailyTotal = criteria.get {
				and {
					eq('employee',employee)
					eq('day',calendarLoop.get(Calendar.DAY_OF_MONTH))
					eq('month',month)
					eq('year',year)
				}
			}
			// permet de r�cup�rer le total hebdo
			if (dailyTotal != null && dailyTotal != dailyTotalId){
				dailySeconds = getDailyTotal(dailyTotal)
				monthlyTotalTime += dailySeconds
				def previousValue=weeklyTotalTime.get(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR))
				if (previousValue!=null){
					def newValue=previousValue.get(0)*3600+previousValue.get(1)*60+previousValue.get(2)
					weeklyTotalTime.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR), computeHumanTime(dailySeconds+newValue))
				}else{
					weeklyTotalTime.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR), computeHumanTime(dailySeconds))
				}
				
				if (!isSunday && calendarLoop.get(Calendar.WEEK_OF_YEAR)==lastWeekParam.get(0) ){
					weeklySupTime = 0
				}else{
					weeklySupTime = computeSupplementaryTime(employee,calendarLoop.get(Calendar.WEEK_OF_YEAR), calendarLoop.get(Calendar.YEAR))
				}
				weeklySuppTotalTime.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR),computeHumanTime(Math.round(weeklySupTime)))
				if (currentWeek != calendarLoop.get(Calendar.WEEK_OF_YEAR)){
					monthlySupTime += weeklySupTime
					currentWeek = calendarLoop.get(Calendar.WEEK_OF_YEAR)
				}
				weeklySupTotalTimeByEmployee.put(employee,weeklySuppTotalTime)
				weeklyTotalTimeByEmployee.put(employee,weeklyTotalTime)
				dailyTotalId=dailyTotal.id
			}
			// daily total is null. Still, we need to check if supplementary time exists within the week
			if (dailyTotal==null && calendarLoop.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
				if (calendarLoop.get(Calendar.WEEK_OF_YEAR)==lastWeekParam.get(0) ){
					weeklySupTime = 0
				}else{
					weeklySupTime = computeSupplementaryTime(employee,calendarLoop.get(Calendar.WEEK_OF_YEAR), calendarLoop.get(Calendar.YEAR))
				}
					weeklySuppTotalTime.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR),computeHumanTime(Math.round(weeklySupTime)))
				if (currentWeek != calendarLoop.get(Calendar.WEEK_OF_YEAR)){
					monthlySupTime += weeklySupTime
					currentWeek = calendarLoop.get(Calendar.WEEK_OF_YEAR)
				}
				weeklySupTotalTimeByEmployee.put(employee,weeklySuppTotalTime)
			
			}
						
			criteria = InAndOut.createCriteria()
			def entriesByDay = criteria{
				and {
					eq('employee',employee)
					eq('day',calendarLoop.getAt(Calendar.DATE))
					eq('month',month)
					eq('year',year)
					order('time')
					}
			}
			// put in a map in and outs
			def tmpDate = calendarLoop.time
			if 	(entriesByDay.size()>0){
				if (dailyTotal!=null){
					dailyTotalMap.put(tmpDate, computeHumanTime(dailySeconds))
					dailySupTotalMap.put(tmpDate, computeHumanTime(Math.max(dailySeconds-DailyTotal.maxWorkingTime,0)))
				}else {
					dailyTotalMap.put(tmpDate, computeHumanTime(0))
					dailySupTotalMap.put(tmpDate, computeHumanTime(0))
				}
				mapByDay.put(tmpDate, entriesByDay)
			}
			else{
				dailyTotalMap.put(tmpDate, computeHumanTime(0))
				mapByDay.put(tmpDate, null)
			}
			
			// find out if day is a bank holiday:
			criteria = BankHoliday.createCriteria()
			def bankHoliday = criteria.get{
				and {
					eq('year',calendarLoop.get(Calendar.YEAR))
					eq('month',calendarLoop.get(Calendar.MONTH)+1)
					eq('day',calendarLoop.get(Calendar.DAY_OF_MONTH))
				}
			}
			if (bankHoliday!=null){
				dailyBankHolidayMap.put(tmpDate, true)
			}else{
				dailyBankHolidayMap.put(tmpDate, false)
			}
			
			def absenceCriteria = Absence.createCriteria()
			def dailyAbsence = absenceCriteria.get {
				and {
					eq('employee',employee)
					eq('year',calendarLoop.get(Calendar.YEAR))
					eq('month',calendarLoop.get(Calendar.MONTH)+1)
					eq('day',calendarLoop.get(Calendar.DAY_OF_MONTH))
				}
			}
			holidayMap.put(tmpDate, dailyAbsence)
			weeklyAggregate.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR), mapByDay)
			if (calendarLoop.get(Calendar.DAY_OF_MONTH)==calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendarLoop.roll(Calendar.DAY_OF_MONTH, 1)
		}

		//update monthlyTotal
		
		criteria = MonthlyTotal.createCriteria()
		def monthlyTotalInstance = criteria.get {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
			}
		}
		if (monthlyTotalInstance!=null){
			monthlyTotalInstance.elapsedSeconds=monthlyTotalTime
		}
		def cartoucheTable = getCartoucheData(employee,year,month)
		def openedDays = computeMonthlyHours(year,month)
		def workingDays=cartoucheTable.get('workingDays')
		def holiday=cartoucheTable.get('holidays')
		def rtt=cartoucheTable.get('rtt')
		def sickness=cartoucheTable.get('sickness')
		def sansSolde=cartoucheTable.get('sansSolde')
		def monthTheoritical = cartoucheTable.get('monthTheoritical')
		def pregnancyCredit = computeHumanTime(cartoucheTable.get('pregnancyCredit'))
		def yearlyHoliday=cartoucheTable.get('yearlyHolidays')
		def yearlyRtt=cartoucheTable.get('yearlyRtt')
		def yearlySickness=cartoucheTable.get('yearlySickness')
		def yearlyTheoritical = computeHumanTime(cartoucheTable.get('yearlyTheoritical'))
		def yearlyPregnancyCredit = computeHumanTime(cartoucheTable.get('yearlyPregnancyCredit'))
		def yearlyActualTotal = computeHumanTime(cartoucheTable.get('yearlyTotalTime'))
		def yearlySansSolde=cartoucheTable.get('yearlySansSolde')
		def payableSupTime = computeHumanTime(Math.round(monthlySupTime))
		def payableCompTime = computeHumanTime(0)
		if (employee.weeklyContractTime!=Employee.legalWeekTime && (monthlyTotalTime > monthTheoritical)){
			payableCompTime = computeHumanTime(Math.round(Math.max(monthlyTotalTime-monthTheoritical-monthlySupTime,0)))
		}
		
		monthlyTotalTimeByEmployee.put(employee, computeHumanTime(monthlyTotalTime))
		def monthlyTotal=computeHumanTime(monthlyTotalTime)
		monthTheoritical = computeHumanTime(cartoucheTable.get('monthTheoritical'))
		
		if (month>5){
			yearInf=year
			yearSup=year+1
		}else{
			yearInf=year-1
			yearSup=year
		}

		def bankList = BankHoliday.findByCalendar(calendar)
		return [dailyBankHolidayMap:dailyBankHolidayMap,employeeId:employee.id,weeklyContractTime:employee.weeklyContractTime,matricule:employee.matricule,firstName:employee.firstName,lastName:employee.lastName,monthlyTotalRecap:monthlyTotal,payableSupTime:payableSupTime,payableCompTime:payableCompTime,employee:employee,siteId:siteId,yearInf:yearInf,yearSup:yearSup,userId:employee.id,workingDays:workingDays,holiday:holiday,rtt:rtt,sickness:sickness,sansSolde:sansSolde,yearlyActualTotal:yearlyActualTotal,monthTheoritical:monthTheoritical,pregnancyCredit:pregnancyCredit,yearlyPregnancyCredit:yearlyPregnancyCredit,yearlyTheoritical:yearlyTheoritical,yearlyHoliday:yearlyHoliday,yearlyRtt:yearlyRtt,yearlySickness:yearlySickness,yearlySansSolde:yearlySansSolde,yearlyTheoritical:yearlyTheoritical,period:calendar,monthlyTotal:monthlyTotalTimeByEmployee,weeklyTotal:weeklyTotalTimeByEmployee,weeklySupTotal:weeklySupTotalTimeByEmployee,dailySupTotalMap:dailySupTotalMap,dailyTotalMap:dailyTotalMap,month:month,year:year,period:calendarLoop.getTime(),dailyTotalMap:dailyTotalMap,holidayMap:holidayMap,weeklyAggregate:weeklyAggregate,employee:employee,payableSupTime:payableSupTime,payableCompTime:payableCompTime]

	}
	
	def getEcartData(Site site, def monthList, Period period){
		def tmpYear
		def criteria
		def referenceRTT
		def takenRTT
		def data
		def monthlyTheoriticalMap = [:]
		def monthlyActualMap = [:]
		def monthlyTakenRTTMap = [:]
		def ecartMap = [:]
		def ecartMinusRTTMap = [:]
		def monthlyTheoriticalByEmployee = [:]
		def monthlyActualByEmployee = [:]
		def ecartMinusRTTByEmployee = [:]
		def ecartByEmployee = [:]
		def rttByEmployee = [:]
		def employeeInstanceList
		
		if (site){
			employeeInstanceList = Employee.findAllBySite(site)
		}else{
			employeeInstanceList=Employee.findAll()
		}
		
		for (Employee employee:employeeInstanceList){
			monthlyTheoriticalMap = [:]
			monthlyActualMap = [:]
			monthlyTakenRTTMap = [:]
			ecartMap = [:]
			ecartMinusRTTMap = [:]
			for (month in monthList){
				tmpYear=(month<6)?period.year+1:period.year
		   
				criteria = MonthlyTotal.createCriteria()
				def monthlyTotalInstance = criteria.get {
					and {
						eq('employee',employee)
						eq('year',tmpYear)
						eq('month',month)
					}
				}
				
				criteria = Vacation.createCriteria()
				referenceRTT = criteria.get{
					and {
						eq('employee',employee)
						eq('period',period)
						eq('type',VacationType.RTT)
					}
				}
				//special case for employees for which vacations were not properly instanciated
				if (referenceRTT == null && employee.weeklyContractTime == Employee.legalWeekTime){
						utilService.initiateVacations(employee)
						criteria = Vacation.createCriteria()
						referenceRTT = criteria.get{
							and {
								eq('employee',employee)
								eq('period',period)
								eq('type',VacationType.RTT)
							}
						}
					}
					
				criteria = Absence.createCriteria()
				takenRTT = criteria.list{
					and {
						eq('employee',employee)
						eq('year',tmpYear)
						eq('month',month)
						eq('type',VacationType.RTT)
					}
				}
				data = getCartoucheData(employee,tmpYear,month)
				if (month>6 || (month>1 && month<6)){
					monthlyTheoriticalMap.put(month, data.get('monthTheoritical')+monthlyTheoriticalMap.get(month-1))
					if (monthlyTotalInstance!=null){
						monthlyActualMap.put(month, monthlyTotalInstance.elapsedSeconds+monthlyActualMap.get(month-1))
					}else{
						monthlyActualMap.put(month, monthlyActualMap.get(month-1))
					}
					if (employee.weeklyContractTime == Employee.legalWeekTime){
						if (takenRTT!=null){
							monthlyTakenRTTMap.put(month,monthlyTakenRTTMap.get(month-1) - takenRTT.size())
						}else{
							monthlyTakenRTTMap.put(month,monthlyTakenRTTMap.get(month-1))
						}
					}
				}else{
					if (month==6){
						monthlyTheoriticalMap.put(month, data.get('monthTheoritical'))
						if (monthlyTotalInstance!=null){
							monthlyActualMap.put(month, monthlyTotalInstance.elapsedSeconds)
						}else{
							monthlyActualMap.put(month, 0)
						}
						if (employee.weeklyContractTime == Employee.legalWeekTime){
							if (takenRTT!=null){
								monthlyTakenRTTMap.put(month,referenceRTT.counter - takenRTT.size())
							}else{
								monthlyTakenRTTMap.put(month,referenceRTT.counter)
							}
						}
					}else{
						if (month==1){
							monthlyTheoriticalMap.put(month, data.get('monthTheoritical')+monthlyTheoriticalMap.get(12))
							if (monthlyTotalInstance!=null){
								monthlyActualMap.put(month, monthlyTotalInstance.elapsedSeconds+monthlyActualMap.get(12))
							}else{
								monthlyActualMap.put(month, monthlyActualMap.get(12))
							}
						}
						if (employee.weeklyContractTime == Employee.legalWeekTime){	
							if (takenRTT!=null){
								monthlyTakenRTTMap.put(month,monthlyTakenRTTMap.get(12) - takenRTT.size())
							}else{
								monthlyTakenRTTMap.put(month,monthlyTakenRTTMap.get(12))
							}
						}
					}
				}
				ecartMap.put(month, monthlyActualMap.get(month)-monthlyTheoriticalMap.get(month))
				if (employee.weeklyContractTime == Employee.legalWeekTime){	
					def ecart = ecartMap.get(month)
					def remainingRTT = (3600*(monthlyTakenRTTMap.get(month))*(employee.weeklyContractTime/Employee.WeekOpenedDays)) as long
					def minusRtt = ecart - remainingRTT
					//ecartMinusRTTMap.put(month, ecart)
					
					ecartMinusRTTMap.put(month, ecartMap.get(month)-(3600*(monthlyTakenRTTMap.get(month))*(employee.weeklyContractTime/Employee.WeekOpenedDays)) as long)
				}
			}
		
			monthlyTheoriticalMap.each() {
				def serviceData=computeHumanTime(it.value)
				def hours=serviceData.get(0)
				def minutes=serviceData.get(1)==0?'00':serviceData.get(1)
				it.value=hours+'H'+minutes
				}
			monthlyActualMap.each() {
				def serviceData=computeHumanTime(it.value)
				def hours=serviceData.get(0)
				def minutes=serviceData.get(1)==0?'00':serviceData.get(1)
				it.value=hours+'H'+minutes
		   }
			ecartMap.each() {
				def serviceData=computeHumanTime(it.value)
				def hours=serviceData.get(0)
				def minutes=serviceData.get(1)==0?'00':serviceData.get(1)
				it.value=hours+'H'+minutes
		   }
			ecartMinusRTTMap.each() {
				/*
				if (it.value<0){
					def serviceData=computeHumanTime(-(it.value) as long)
					def hours=serviceData.get(0)
					def minutes=serviceData.get(1)==0?'00':serviceData.get(1)
					it.value='-'+hours+'H'+minutes
				}else{
					def serviceData=computeHumanTime(it.value as long)
					def hours=serviceData.get(0)
					def minutes=serviceData.get(1)==0?'00':serviceData.get(1)
					it.value=hours+'H'+minutes
				}*/
				def serviceData=computeHumanTime(it.value as long)
				def hours=serviceData.get(0)
				def minutes=serviceData.get(1)==0?'00':serviceData.get(1)
				it.value=hours+'H'+minutes
		   }
		   
			
			monthlyTheoriticalByEmployee.put(employee,monthlyTheoriticalMap)
			monthlyActualByEmployee.put(employee,monthlyActualMap)
			ecartByEmployee.put(employee, ecartMap)
			if (employee.weeklyContractTime == Employee.legalWeekTime)
				ecartMinusRTTByEmployee.put(employee,ecartMinusRTTMap)
			rttByEmployee.put(employee, monthlyTakenRTTMap)
		}
		return [period:period,employeeInstanceList:employeeInstanceList,monthlyTheoriticalByEmployee:monthlyTheoriticalByEmployee,monthlyActualByEmployee:monthlyActualByEmployee,ecartByEmployee:ecartByEmployee,rttByEmployee:rttByEmployee,ecartMinusRTTByEmployee:ecartMinusRTTByEmployee]
	
	}
		
	def getMonthlySupTime(Employee employee,int month, int year){
		int daysToWithdraw
		def supTime = 0
		Calendar calendar = Calendar.instance
		calendar.set(Calendar.MONTH,month-1)
		calendar.set(Calendar.YEAR,year)
		Calendar firstDayOfMonth = calendar.clone()
		Calendar lastDayOfMonth = calendar.clone()
		lastDayOfMonth.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		firstDayOfMonth.set(Calendar.DAY_OF_MONTH,1)
		
		if (firstDayOfMonth.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
			int currentDayInWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)
			int currentDayInYear = firstDayOfMonth.get(Calendar.DAY_OF_YEAR)
			if (firstDayOfMonth.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				daysToWithdraw = 6
			}else{
				daysToWithdraw = currentDayInWeek - Calendar.MONDAY
			}
			firstDayOfMonth.set(Calendar.DAY_OF_YEAR, currentDayInYear - daysToWithdraw)
		}
		Calendar calendarLoop = firstDayOfMonth.clone()
		// now that we have the first day of the period, iterate over each week of the period to retrieve supplementary time
		
		log.debug('calendarLoop before special loop '+calendarLoop.time)
		
		// special case if hover 2 years
		if (calendarLoop.get(Calendar.DAY_OF_YEAR)>lastDayOfMonth.get(Calendar.DAY_OF_YEAR)){
			supTime += computeSupplementaryTime(employee,calendarLoop.get(Calendar.WEEK_OF_YEAR), calendarLoop.get(Calendar.YEAR))
			calendarLoop.roll(Calendar.DAY_OF_YEAR,7)
			calendarLoop.set(Calendar.YEAR,year)
		  //  log.error('calendarLoop after special loop '+calendarLoop.time)
		}
		
		while(calendarLoop.get(Calendar.DAY_OF_YEAR)<=lastDayOfMonth.get(Calendar.DAY_OF_YEAR)){
		//	log.error('calendarLoop : '+calendarLoop.time)
			
			if ((calendarLoop.get(Calendar.DAY_OF_YEAR)+7)>lastDayOfMonth.get(Calendar.DAY_OF_YEAR)){
				break
			}
			supTime += computeSupplementaryTime(employee,calendarLoop.get(Calendar.WEEK_OF_YEAR), calendarLoop.get(Calendar.YEAR))
			calendarLoop.roll(Calendar.DAY_OF_YEAR,7)
		}
		return supTime
	}
	
	def getAnnualReportData(int year,int month, Employee employee){
		def criteria
		def dailySeconds
		def monthlyTotalTime
		def monthlySupTotalTime
		def yearMonthMap = [:]
		def yearTotalMap = [:]
		def yearSupMap = [:]
		def yearMonthlySupTime = [:]
		def yearMonthlyCompTime = [:]
		def yearMap = [:]
		def cartoucheTable=[]
		def firstWeekOfMonth
		def lastWeekOfMonth
		def weeklySupTime
		def payableCompTime
		def payableSupTime
		def annualTheoritical = 0
		def annualTotal = 0
		def annualHoliday = 0
		def annualRTT = 0
		def annualCSS = 0
		def annualSickness = 0
		def annualPayableSupTime = 0
		def annualPayableCompTime = 0
		def annualWorkingDays = 0
		def annualEmployeeWorkingDays = 0
		def annualTotalIncludingHS = 0
		def calendar = Calendar.instance
		def currentMonth
		def currentYear=year
		
		for (int monthLoop = 6 ;monthLoop <18 ; monthLoop++){
			if (monthLoop>12){
				currentYear=year+1
				currentMonth=monthLoop-12
			}else{
				currentMonth=monthLoop
			}
			
			log.debug('monthLoop: '+monthLoop)
			yearMap.put(currentMonth, currentYear)
			cartoucheTable=getCartoucheData(employee,currentYear,currentMonth)
			yearMonthMap.put(currentMonth, cartoucheTable)
			monthlyTotalTime = 0
			monthlySupTotalTime = 0
			calendar.set(Calendar.MONTH,currentMonth-1)
			calendar.set(Calendar.YEAR,year)
			calendar.set(Calendar.DAY_OF_MONTH,1)
			firstWeekOfMonth = calendar.get(Calendar.WEEK_OF_YEAR)
			calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
			lastWeekOfMonth = calendar.get(Calendar.WEEK_OF_YEAR)
			
			criteria = DailyTotal.createCriteria()
			def dailyTotalList = criteria.list {
				and {
					eq('employee',employee)
					eq('month',currentMonth)
					eq('year',currentYear)
				}
			}
			dailySeconds = 0
			for (DailyTotal dailyTotal:dailyTotalList){
				dailySeconds = getDailyTotal(dailyTotal)
				monthlyTotalTime += dailySeconds
				annualEmployeeWorkingDays += 1
			}
			yearTotalMap.put(currentMonth, computeHumanTime(monthlyTotalTime))
			monthlySupTotalTime = getMonthlySupTime(employee,currentMonth, currentYear)
			yearMonthlySupTime.put(currentMonth,computeHumanTime(Math.round(monthlySupTotalTime)))
			def monthTheoritical = cartoucheTable.getAt('monthTheoritical')
			if (employee.weeklyContractTime!=35){
				if (monthlyTotalTime > monthTheoritical){
					payableCompTime = Math.max(monthlyTotalTime-monthTheoritical-monthlySupTotalTime,0)
					yearMonthlyCompTime.put(currentMonth, computeHumanTime(payableCompTime))
				}else{
					payableCompTime = 0
					yearMonthlyCompTime.put(currentMonth, computeHumanTime(0))
				}
			}else{
				payableCompTime = 0	
			}
			
			annualTheoritical += cartoucheTable.getAt('monthTheoritical')
			annualHoliday += cartoucheTable.getAt('holidays')
			annualRTT += cartoucheTable.getAt('rtt')
			annualCSS += cartoucheTable.getAt('sansSolde')
			annualSickness += cartoucheTable.getAt('sickness')
			annualWorkingDays += cartoucheTable.getAt('workingDays')
			annualPayableSupTime += monthlySupTotalTime
			annualPayableCompTime += payableCompTime
			annualTotal += monthlyTotalTime
		}

		def period = Period.findByYear(year)
		def remainingCA = employeeService.getRemainingCA(employee,period)
		
		return [remainingCA:remainingCA,annualTotalIncludingHS:computeHumanTime(Math.round(annualTotalIncludingHS)),annualEmployeeWorkingDays:annualEmployeeWorkingDays,	annualTheoritical:computeHumanTime(Math.round(annualTheoritical)),annualHoliday:annualHoliday,annualRTT:annualRTT,annualCSS:annualCSS,annualSickness:annualSickness,annualWorkingDays:annualWorkingDays,annualPayableSupTime:computeHumanTime(Math.round(annualPayableSupTime)),annualPayableCompTime:computeHumanTime(Math.round(annualPayableCompTime)),annualTotal:computeHumanTime(Math.round(annualTotal)),
lastYear:year,thisYear:year+1,yearMap:yearMap,yearMonthlyCompTime:yearMonthlyCompTime,yearMonthlySupTime:yearMonthlySupTime,yearTotalMap:yearTotalMap,yearMonthMap:yearMonthMap,userId:employee.id,employee:employee]

	}
	
	
	def getMonthTheoritical(Employee employee, int month,int year, int openDays, int sickness, int holidays, int sansSolde, int pregnancy){
		def monthTheoritical = 0
		def calendar = Calendar.instance
		def weeklyContractTime
		def contract
		def criteria
		def previousContracts
		criteria = Contract.createCriteria()
		
		previousContracts = criteria.list {
			and {
				eq('year',year)
				eq('month',month)
				eq('employee',employee)
			}
		}
		
		if (previousContracts!=null){
		//	log.error('previsouContract not null')
			criteria = Contract.createCriteria()
			contract = criteria.get {
				and {
					eq('year',year)
					eq('month',month)
					eq('employee',employee)
				}
			}
			if (contract != null){
				weeklyContractTime = contract.weeklyLength
			}else{
				weeklyContractTime =employee.weeklyContractTime
			}
		}else{
				weeklyContractTime =employee.weeklyContractTime
		}
				
		monthTheoritical=(
			3600*(
					openDays*weeklyContractTime/Employee.WeekOpenedDays
					+(Employee.Pentecote)*(weeklyContractTime/Employee.legalWeekTime)
					-(weeklyContractTime/Employee.WeekOpenedDays)*(sickness+holidays+sansSolde))
				- pregnancy) as int
			
		return monthTheoritical
	}
	
	
	
}
