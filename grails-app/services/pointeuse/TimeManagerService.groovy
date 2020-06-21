package pointeuse

import groovy.time.TimeDuration;
import groovy.time.TimeCategory;

import java.text.SimpleDateFormat
import java.util.Date;
import java.util.concurrent.TimeUnit
import java.util.logging.Logger;

import org.springframework.transaction.annotation.Transactional

import groovyx.gpars.*


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
	
	def computePayableSupplementaryTime(Employee employee,long realise, long theorique, long supTime){
		def payableSupTime = 0
		def aboveTheoritical = (realise > theorique)?(realise - theorique):0
		/* 
		* si Realisé - Theorique < HS j ou hebdo
		* alors payableSUpTIme = HS j ou hebdo
		* sinon, payableSupTime = Réalisé - Theorique
		*
		*/
		if (aboveTheoritical > 0){
			if (aboveTheoritical < supTime){
				payableSupTime = supTime
			}else{
				payableSupTime = aboveTheoritical
			}
		}else{
			if (supTime > 0){
				payableSupTime = supTime
			}
		}
		return payableSupTime
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
		def holidayCal = Calendar.instance
		
		criteria = BankHoliday.createCriteria()
		bankHolidays = criteria.list {
			or{
				and {
					eq('year',year)
					eq('month',calendar.get(Calendar.MONTH) + 1)
				}
				
				and{
					eq('year',year)
					eq('week',calendar.get(Calendar.WEEK_OF_YEAR))
					
				}		
			}
		}
		
		
		for (BankHoliday bankHoliday:bankHolidays){
			holidayCal.set(Calendar.YEAR,bankHoliday.year)
			holidayCal.set(Calendar.WEEK_OF_YEAR,bankHoliday.week)
			holidayCal.set(Calendar.DAY_OF_MONTH,bankHoliday.day)
			holidayCal.set(Calendar.MONTH,bankHoliday.month - 1)

			if (bankHoliday.calendar.get(Calendar.WEEK_OF_YEAR) == week & holidayCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				
		//	if (bankHoliday.calendar.get(Calendar.WEEK_OF_YEAR) == week) {
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
			def tmpElapsed = (getDailyTotal(tmpDaily)).get("elapsedSeconds")
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
		

	

	
	def initializeTotals(Employee employee, Date currentDate,String type,def event, Boolean isOutside){
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
	
	def initializeTotals(Employee employee, Date currentDate,Site site){
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
			dailyTotal.site = site
			dailyTotal.weeklyTotal=weeklyTotal
			weeklyTotal.dailyTotals.add(dailyTotal)
			dailyTotal.save(flush:true)
			
		}
		return [dailyTotal,weeklyTotal,monthlyTotal]
	}
	
	def regularizeTime(String type,Long userId,InAndOut inOrOut,Calendar calendar){
		if (calendar == null){
			calendar = Calendar.instance
		}
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
		getDailyTotalWithMonth(inOrOut.dailyTotal)
		
		utilService.removeAbsence( employee, calendar)
		//computeWeeklyTotals( employee,  calendar.get(Calendar.MONTH) + 1,  calendar.get(Calendar.YEAR))
		
		//employee.status=type.equals("S")?false:true
	}
		
	def timeModification(def idList,def dayList,def monthList, def yearList,Employee employee,def newTimeList,def fromRegularize) throws PointeuseException{
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
		
		for( int p = 0 ; ( p < idList.size() ) ; p++ ) {
		// l'idée: comparer time et newTime
			def inOrOut = InAndOut.get(idList[p])
			if (inOrOut==null){
				log.debug("InAndOut with id= "+idList[p]+" cannot be found. exiting timeModification")
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
					log.debug('entry cannot be positionned after exit')
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
					log.debug("user "+user?.username+" modified "+inOrOut)
				}
				inOrOut.save(flush:true)				
				computeWeeklyTotals( employee, calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR),true)					
			}
		}	
	}
	
	def openDaysBetweenDates(Date startDate,Date endDate){
		log.debug('openDaysBetweenDates between: '+startDate+' and '+endDate)
		def openedDays = 0
		def criteria
		def calendarIter = Calendar.instance
		def startCalendar = Calendar.instance
		def endCalendar = Calendar.instance
		def bankHolidayList
		def holidayCounter = 0
		
		if (startDate > endDate){
			def tmpDate = startDate
			startDate = endDate
			endDate = tmpDate
		}
		calendarIter.setTime(startDate)		
		startCalendar.setTime(startDate)		
		startCalendar.clearTime()
		endCalendar.setTime(endDate)		
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
		
		
		//2 cases: start and end dates are contained in the same year. otherwise do it recursively year by year
		if (startDate.getAt(Calendar.YEAR) == endDate.getAt(Calendar.YEAR)){
			// retrieve bank holidays
			// count bank holiday
			//find all bank holiday over the period
			criteria = BankHoliday.createCriteria()
			bankHolidayList = criteria.list {
				and {
					ge('calendar',startCalendar)
					le('calendar',endCalendar)
				}		
			}
			for (BankHoliday holiday:bankHolidayList){
				if (holiday.calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
					holidayCounter++
				}
			}
			log.debug('holidayCounter: '+holidayCounter)		
			
			while(calendarIter.get(Calendar.DAY_OF_YEAR) <= endDate.getAt(Calendar.DAY_OF_YEAR)){
				log.debug('calendarIter.time: '+calendarIter.time)
				if (calendarIter.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
					openedDays += 1
				}
				if (calendarIter.get(Calendar.DAY_OF_YEAR) == endDate.getAt(Calendar.DAY_OF_YEAR)){
					openedDays -= holidayCounter
					break
				}
				calendarIter.roll(Calendar.DAY_OF_YEAR, 1)
			}
		}
		// in this case, the year of the startdate is < endDate
		else{
			calendarIter.set(Calendar.DAY_OF_YEAR,startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR))
			openedDays  += openDaysBetweenDates(startDate,calendarIter.time)
			startCalendar.set(Calendar.YEAR,startCalendar.get(Calendar.YEAR) + 1)
			startCalendar.set(Calendar.DAY_OF_YEAR,1)
			startCalendar.clearTime()
			openedDays += openDaysBetweenDates(startCalendar.time,endCalendar.time)
		}
		return openedDays
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
		boolean isNegative = false
		if (inputSeconds < 0){
			//inputSeconds = -inputSeconds
			isNegative = true
		}
		def diff=inputSeconds
		long hours=TimeUnit.SECONDS.toHours(diff);
		diff=diff-(hours*3600);
		long minutes=TimeUnit.SECONDS.toMinutes(diff);
		diff=diff-(minutes*60);
		long seconds=TimeUnit.SECONDS.toSeconds(diff);
		//return [hours,minutes,seconds]
		return [hours,minutes,seconds,isNegative]
	}
	
	
	def computeHumanTimeAsText(long inputSeconds){
		boolean isNegative = false
		if (inputSeconds < 0){
			//inputSeconds = -inputSeconds
			isNegative = true
		}
		def diff=inputSeconds
		long hours=TimeUnit.SECONDS.toHours(diff);
		diff=diff-(hours*3600);
		long minutes=TimeUnit.SECONDS.toMinutes(diff);
		diff=diff-(minutes*60);
		long seconds=TimeUnit.SECONDS.toSeconds(diff);
		//return [hours,minutes,seconds]
		def table = [hours,minutes,seconds,isNegative]
		def outputString = ''
		
		if (!table.get(3)){
			if (table.get(0)<10)
				outputString += '0'
			outputString += table.get(0)
			outputString += ':'		
			if (table.get(1)<10)
				outputString += '0'
			outputString += table.get(1)
		}else{
			outputString += '-'
			if (Math.abs(table.get(0))<10)
				outputString += '0'
			outputString += Math.abs(table.get(0))
			outputString += ':'
			if (Math.abs(table.get(1))<10)
				outputString += '0'
				outputString += Math.abs(table.get(1))
		}	
		return outputString
	}
	
	def getTimeFromText(String text, boolean hasSpace){
		def values
		if (hasSpace){
			values = text.split(' : ')
		}
		else{
			 values = text.split(':')	
		}
		def time = (values[0] as long)*3600 + (values[1] as long)*60
		return time;
	}
	
	def getTimeAsText(def table,hasSpace){
		def result
		def outputString = ''
		
		if (!table.get(3)){
			if (table.get(0)<10)
				outputString += '0'
			outputString += table.get(0)
			if (hasSpace){
				outputString += ' : '
			}
			else{
				outputString += ':'
			}
			if (table.get(1)<10)
				outputString += '0'
			outputString += table.get(1)
		}else{
			outputString += '-'
			if (Math.abs(table.get(0))<10)
				outputString += '0'
			outputString += Math.abs(table.get(0))
			if (hasSpace){
				outputString += ' : '
			}
			else{
				outputString += ':'
			}
			if (Math.abs(table.get(1))<10)
				outputString += '0'
				outputString += Math.abs(table.get(1))
		}
		
		log.debug('output String: '+outputString)
		return outputString
	}

	def computeHumanTimeAsString(long inputSeconds){
		def diff=inputSeconds
		long hours=TimeUnit.SECONDS.toHours(diff);
		def hoursAsString = hours > 9 ? hours.toString() :'0'+hours.toString()
		if (hours==0) hoursAsString ='00'
		diff=diff-(hours*3600);
		long minutes=TimeUnit.SECONDS.toMinutes(diff);
		def minutesAsString = minutes > 9 ? minutes.toString() :'0'+minutes.toString()
		if (minutes==0) minutesAsString ='00'
		diff=diff-(minutes*60);
		long seconds=TimeUnit.SECONDS.toSeconds(diff);
		def secondsAsString = seconds > 9 ? seconds.toString() :'0'+seconds.toString()
		if (seconds==0) secondsAsString ='00'
		
		return [hoursAsString,minutesAsString,secondsAsString]		
	}
	
	
	def computeSecondsFromHumanTime(def table){
		return table.get(0)*3600+table.get(1)*60+table.get(2)
	}
	
	def recomputeDailyTotals(){
		def tmpInOrOut
		def dailyDelta=0
		def timeDiff
		def criteria
		def employeeList = Employee.findAll("from Employee")
		
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
		def timeBefore7 = 0
		def timeAfter20 = 0
		def timeOffHours = 0
		def tmpInOrOut
		def timeDifference
		def currentInOrOut
		def previousInOrOut
		def calendarAtSeven = Calendar.instance
		def calendarAtNine = Calendar.instance
		calendarAtSeven.set(Calendar.YEAR,dailyTotal.year)
		calendarAtSeven.set(Calendar.MONTH,dailyTotal.month-1)
		calendarAtSeven.set(Calendar.DAY_OF_MONTH,dailyTotal.day)
		calendarAtSeven.set(Calendar.HOUR_OF_DAY,7)
		calendarAtSeven.set(Calendar.MINUTE,0)
		calendarAtSeven.set(Calendar.SECOND,0)	
		calendarAtNine.set(Calendar.YEAR,dailyTotal.year)
		calendarAtNine.set(Calendar.MONTH,dailyTotal.month-1)
		calendarAtNine.set(Calendar.DAY_OF_MONTH,dailyTotal.day)
		calendarAtNine.set(Calendar.HOUR_OF_DAY,20)
		calendarAtNine.set(Calendar.MINUTE,0)
		calendarAtNine.set(Calendar.SECOND,0)
		
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
			currentInOrOut = inOrOut
			if (previousInOrOut == null){
				// it is the first occurence
				previousInOrOut = inOrOut
			}else{
				if (previousInOrOut.type.equals("E") && currentInOrOut.type.equals("S")){
					use (TimeCategory){timeDifference = currentInOrOut.time - previousInOrOut.time}
					elapsedSeconds += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
					
					// managing time before 7
					if (currentInOrOut.time < calendarAtSeven.time){
						use (TimeCategory){timeDifference = currentInOrOut.time - previousInOrOut.time}
						timeBefore7 += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
					}
					if (currentInOrOut.time > calendarAtSeven.time && previousInOrOut.time < calendarAtSeven.time){
						use (TimeCategory){timeDifference = calendarAtSeven.time - previousInOrOut.time}
						timeBefore7 += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
					}
					
					// managing time after 21
					if (previousInOrOut.time > calendarAtNine.time){
						use (TimeCategory){timeDifference = currentInOrOut.time - previousInOrOut.time}
						timeAfter20 += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
					}
					if (currentInOrOut.time > calendarAtNine.time && previousInOrOut.time < calendarAtNine.time){
						use (TimeCategory){timeDifference = currentInOrOut.time - calendarAtNine.time}
						timeAfter20 += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
					}
				}
				previousInOrOut = inOrOut
			}
		}
		dailyTotal.elapsedSeconds=elapsedSeconds
		
		try{
			dailyTotal.save(flush:true)
		}
		catch(org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException e) {
			log.debug('concurrent access error: '+e.message)
		}
		
		return [
			elapsedSeconds:elapsedSeconds,
			timeBefore7:timeBefore7,
			timeAfter20:timeAfter20,
			timeOffHours:(timeBefore7 + timeAfter20)
		]
	}
	
	def getDailyTotalWithMonth(DailyTotal dailyTotal){
		def criteria = InAndOut.createCriteria()
		def elapsedSeconds = 0
		def timeBefore7 = 0
		def timeAfter20 = 0
		def timeOffHours = 0
		def tmpInOrOut
		def timeDifference
		def deltaTime
		def currentInOrOut
		def previousInOrOut
		def calendarAtSeven = Calendar.instance
		def calendarAtNine = Calendar.instance
		calendarAtSeven.set(Calendar.YEAR,dailyTotal.year)
		calendarAtSeven.set(Calendar.MONTH,dailyTotal.month-1)
		calendarAtSeven.set(Calendar.DAY_OF_MONTH,dailyTotal.day)
		calendarAtSeven.set(Calendar.HOUR_OF_DAY,7)
		calendarAtSeven.set(Calendar.MINUTE,0)
		calendarAtSeven.set(Calendar.SECOND,0)
		calendarAtNine.set(Calendar.YEAR,dailyTotal.year)
		calendarAtNine.set(Calendar.MONTH,dailyTotal.month-1)
		calendarAtNine.set(Calendar.DAY_OF_MONTH,dailyTotal.day)
		calendarAtNine.set(Calendar.HOUR_OF_DAY,20)
		calendarAtNine.set(Calendar.MINUTE,0)
		calendarAtNine.set(Calendar.SECOND,0)
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
			currentInOrOut = inOrOut
			if (previousInOrOut == null){
				// it is the first occurence
				previousInOrOut = inOrOut
			}else{
				if (previousInOrOut.type.equals("E") && currentInOrOut.type.equals("S")){
					use (TimeCategory){timeDifference = currentInOrOut.time - previousInOrOut.time}
					elapsedSeconds += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
				}
				// managing time before 7
				if (currentInOrOut.time < calendarAtSeven.time){
					use (TimeCategory){timeDifference = currentInOrOut.time - previousInOrOut.time}
					timeBefore7 += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
				}
				if (currentInOrOut.time > calendarAtSeven.time && previousInOrOut.time < calendarAtSeven.time){
					use (TimeCategory){timeDifference = calendarAtSeven.time - previousInOrOut.time}
					timeBefore7 += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
				}
				
				// managing time after 21
				if (previousInOrOut.time > calendarAtNine.time){
					use (TimeCategory){timeDifference = currentInOrOut.time - previousInOrOut.time}
					timeAfter20 += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
				}
				if (currentInOrOut.time > calendarAtNine.time && previousInOrOut.time < calendarAtNine.time){
					use (TimeCategory){timeDifference = currentInOrOut.time - calendarAtNine.time}
					timeAfter20 += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
				}
				
				previousInOrOut = inOrOut
			}
		}

		deltaTime = dailyTotal.elapsedSeconds - elapsedSeconds//old-new
		dailyTotal.elapsedSeconds = elapsedSeconds

		def deltaBefore7 = dailyTotal.weeklyTotal.monthlyTotal.timeBefore7 - timeBefore7
		def deltaAfter20 = dailyTotal.weeklyTotal.monthlyTotal.timeAfter20 - timeAfter20
	
		
		dailyTotal.weeklyTotal.elapsedSeconds -= deltaTime
		dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds -= deltaTime
		
		dailyTotal.weeklyTotal.monthlyTotal.timeBefore7 -= deltaBefore7
		dailyTotal.weeklyTotal.monthlyTotal.timeAfter20 -= deltaAfter20
		
		return elapsedSeconds
	}
	
	
	def getDailyTotal(def inOrOutList){
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
				elapsedSeconds += dailyTotal.elapsedSeconds
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
		def yearOpenDays = 0
		def yearTheoritical = 0
		def yearSupTime = 0
		def data
		def calendar = Calendar.instance
		calendar.set(Calendar.YEAR,year)
		def yearlyCounter = 0
		def totalTime = 0
		def bankHolidayCounter=0
		def bankHolidayList
		def criteria = Absence.createCriteria()
		def monthTheoritical
		def monthsAggregate
		if (month > 5){
			year = year + 1
			monthNumber = month - 6 + 1
		}else{
			monthNumber = month + 1 + 6
		}

		// set the date end of may
		calendar.set(Calendar.DAY_OF_MONTH,10)	
		calendar.set(Calendar.MONTH,month-1)
		log.debug("month: "+calendar.get(Calendar.MONTH))	
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		log.debug("last day of the month: "+calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		log.debug("calendar: "+calendar)
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		def maxDate = calendar.time
		log.debug("month: "+calendar.get(Calendar.MONTH))
		log.debug("maxDate: "+maxDate)
		calendar.set(Calendar.YEAR,year-1)
		calendar.set(Calendar.MONTH,5)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.clearTime()
		def minDate = calendar.time

		/* create a loop over months and invoke getMonthTheoritical method */
		// check if employee entered the company or left the company over the period.
		def arrivalDate = employee.arrivalDate
		def exitDate = employee.status == StatusType.TERMINE ? employee.status.date : null

		// the employee arrived after the period started: resetting the minDate
		if (arrivalDate > minDate){
			minDate = arrivalDate
		}

		// the employee left before period's end:
		if ((exitDate != null) && exitDate < maxDate){
			maxDate = exitDate
		}	
		yearOpenDays = openDaysBetweenDates(minDate,maxDate)
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
		def yearlyGarde_enfant = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.GARDE_ENFANT)
			}
		}
		criteria = Absence.createCriteria()
		def yearlyChomage = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.CHOMAGE)
			}
		}
		criteria = Absence.createCriteria()
		def yearlyExceptionnel = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.EXCEPTIONNEL)
			}
		}	
		criteria = Absence.createCriteria()
		def yearlyPaternite = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.PATERNITE)
			}
		}	
		criteria = Absence.createCriteria()
		def yearlyParental = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.PARENTAL)
			}
		}
		criteria = Absence.createCriteria()
		def yearlyDif = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.DIF)
			}
		}
		
		criteria = Absence.createCriteria()
		def yearlyDon = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.DON)
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
		def yearlyMaternite = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.MATERNITE)
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
		def yearlyPregnancyCredit=30*60*pregnancy.size()				
		criteria = Absence.createCriteria()
		def yearlySansSolde = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.CSS)
			}
		}
		
		criteria = Absence.createCriteria()
		def yearlyInjustifie = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.INJUSTIFIE)
			}
		}
		
		criteria = Absence.createCriteria()
		def yearlyMISE_A_PIED = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.MISE_A_PIED)
			}
		}
		
		criteria = Absence.createCriteria()
		def yearlyFormation = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.FORMATION)
			}
		}
		
						
		if (month >= 6){
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

		yearlyCounter = month > 5 ? utilService.getYearlyCounter(year-1,month,employee) : utilService.getYearlyCounter(year,month,employee)
		criteria = BankHoliday.createCriteria()
		
		if (month < 6){
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
			if ((bankHoliday.calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) && (bankHoliday.calendar.time > employee.arrivalDate) ){
				bankHolidayCounter ++
			}
		}
		
		def calendarIter = Calendar.instance
		calendarIter.time = minDate

		// 2 cases: either min date is greater than 1st of the year, then 1 loop. Otherwise, 2 loops.
		if (minDate.getAt(Calendar.YEAR) == maxDate.getAt(Calendar.YEAR)){
			while(calendarIter.get(Calendar.MONTH) <= maxDate.getAt(Calendar.MONTH)){
				log.debug('calendarIter: '+calendarIter.time)
				monthTheoritical = getMonthTheoritical(employee,  calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
				yearTheoritical += monthTheoritical
				if (calendarIter.get(Calendar.MONTH) == maxDate.getAt(Calendar.MONTH)){
					break
				}
				calendarIter.roll(Calendar.MONTH, 1)
			}
		}else{
			while(calendarIter.get(Calendar.MONTH) <= 11){
				log.debug('calendarIter: '+calendarIter.time)
				monthTheoritical = getMonthTheoritical(employee,  calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))		
				yearTheoritical += monthTheoritical
				if (calendarIter.get(Calendar.MONTH) == 11){
					break
				}
				calendarIter.roll(Calendar.MONTH, 1)
			}
			calendarIter.set(Calendar.MONTH,0)
			calendarIter.set(Calendar.YEAR,(calendarIter.get(Calendar.YEAR)+1))
			
			while(calendarIter.get(Calendar.MONTH) <= maxDate.getAt(Calendar.MONTH)){
				log.debug('calendarIter: '+calendarIter.time)
				monthTheoritical = getMonthTheoritical(employee,  calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
				yearTheoritical += monthTheoritical
				if (calendarIter.get(Calendar.MONTH) == maxDate.getAt(Calendar.MONTH)){
					break
				}
				calendarIter.roll(Calendar.MONTH, 1)
			}		
		}
		
		yearlyCounter = openDaysBetweenDates(minDate,maxDate)
		return [
			yearOpenDays:yearOpenDays,
			yearlyActualTotal:yearlyCounter,
			yearlyHolidays:yearlyHolidays.size(),
			yearlyGarde_enfant:yearlyGarde_enfant.size(),
			yearlyChomage:yearlyChomage.size(),
			yearlyExceptionnel:yearlyExceptionnel.size(),
			yearlyPaternite:yearlyPaternite.size(),		
			yearlyParental:yearlyParental.size(),
			yearlyDif:yearlyDif.size(),	
			yearlyDon:yearlyDon.size(),
			yearlyRtt:yearlyRtt.size(),
			yearlySickness:yearlySickness.size(),
			yearlyFormation:yearlyFormation.size(),
			yearlyMaternite:yearlyMaternite.size(),
			yearlyTheoritical:yearTheoritical,
			yearlyPregnancyCredit:yearlyPregnancyCredit,
			yearlyTotalTime:totalTime,
			yearlySansSolde:yearlySansSolde.size(),			
			yearlyMISE_A_PIED:yearlyMISE_A_PIED.size(),
			yearlyInjustifie:yearlyInjustifie.size(),
			yearSupTime:yearSupTime
		]
	}
	
	def getCartoucheData(Employee employeeInstance,int year,int month){
		def counter = 0
		def totalNumberOfDays = 0
		def monthTheoritical
		def criteria
		def calendar = Calendar.instance
		def startCalendar = Calendar.instance
		def endCalendar = Calendar.instance
		def isCurrentMonth = false
		
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.MONTH,month-1)
		calendar.clearTime()
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.set(Calendar.YEAR,year)
		startCalendar.set(Calendar.MONTH,month-1)
		startCalendar.clearTime()
		endCalendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		endCalendar.set(Calendar.YEAR,year)
		endCalendar.set(Calendar.MONTH,month-1)
		
		log.debug('current date: '+calendar.time)
		def currentCalendar = Calendar.instance

		// special case: the month is not over yet
		if (currentCalendar.get(Calendar.MONTH) == (month - 1) && currentCalendar.get(Calendar.YEAR) == year){
			log.debug('the month is not over yet')
			counter = openDaysBetweenDates(calendar.time,currentCalendar.time)
			totalNumberOfDays = counter
			isCurrentMonth = true
		}else{
			counter = openDaysBetweenDates(calendar.time,endCalendar.time)
			totalNumberOfDays = counter
		}
		// count sundays within given month

		// treat special case whereby employee enters the company, or leaves...
		// special case: arrival month
		if ((employeeInstance.arrivalDate.getAt(Calendar.MONTH) + 1) == month &&  (employeeInstance.arrivalDate.getAt(Calendar.YEAR)) == year){
			// we need to count OPEN DAYS between dates and not consecutive days
			counter = openDaysBetweenDates(employeeInstance.arrivalDate,endCalendar.time)
		}
		
		//special case: departure month
		def currentStatus = employeeInstance.status
		if (currentStatus.date != null && currentStatus.date <= endCalendar.time){
			if (currentStatus.type != StatusType.ACTIF){
				if (currentStatus.date.getAt(Calendar.MONTH) == endCalendar.get(Calendar.MONTH) && currentStatus.date.getAt(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) ){				
					log.error('departure month. recomputing days')
					Calendar exitCalendar = Calendar.instance
					exitCalendar.time = currentStatus.date
					exitCalendar.roll(Calendar.DAY_OF_YEAR, -1)
					counter = openDaysBetweenDates(calendar.time,exitCalendar.time)
				}else{
					counter = 0
				}
			}
		}	
			
		// special case: employee has not yet arrived in the company
		if (employeeInstance.arrivalDate > currentCalendar.time ){
			counter = 0
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
		
		criteria = Absence.createCriteria()
		def pregnancy = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.GROSSESSE)
			}
		}
		def pregnancyCredit=30*60*pregnancy.size()
		
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
		def maternite = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.MATERNITE)
			}
		}
		
		
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
		
		criteria = Absence.createCriteria()
		def garde_enfant = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.GARDE_ENFANT)
			}
		}
		
		criteria = Absence.createCriteria()
		def chomage = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.CHOMAGE)
			}
		}
		
		criteria = Absence.createCriteria()
		def exceptionnel = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.EXCEPTIONNEL)
			}
		}
		
		criteria = Absence.createCriteria()
		def formation = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.FORMATION)
			}
		}
		
		criteria = Absence.createCriteria()
		def paternite = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.PATERNITE)
			}
		}
		
		criteria = Absence.createCriteria()
		def parental = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.PARENTAL)
			}
		}
		
		criteria = Absence.createCriteria()
		def dif = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.DIF)
			}
		}
		
		criteria = Absence.createCriteria()
		def don = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.DON)
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
		
		criteria = Absence.createCriteria()
		def injustifie = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.INJUSTIFIE)
			}
		}
		
		criteria = Absence.createCriteria()
		def MISE_A_PIED = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.MISE_A_PIED)
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

		// determine monthly theoritical time:
		monthTheoritical=getMonthTheoritical(employeeInstance,  month, year)
		criteria = Contract.createCriteria()	
		def currentContract 
		def contracts = Contract.findAllByEmployee(employeeInstance)
		for (Contract contract : contracts){
			if (contract.endDate != null && contract.startDate < endCalendar.time && contract.endDate > endCalendar.time){
				currentContract = contract
			}			
			if (contract.endDate != null && contract.startDate < endCalendar.time && contract.endDate < endCalendar.time && contract.endDate.getAt(Calendar.MONTH)==endCalendar.time.getAt(Calendar.MONTH)  && contract.endDate.getAt(Calendar.YEAR)==endCalendar.time.getAt(Calendar.YEAR)){
				currentContract = contract			
			}
			if (currentContract == null && contract.endDate == null){
				currentContract = contract
			}
		}

		def cartoucheMap = [
			isCurrentMonth:isCurrentMonth,
			currentContract:currentContract,
			employeeInstance:employeeInstance,
			workingDays:counter,
			holidays:holidays.size(),
			exceptionnel:exceptionnel.size(),
			garde_enfant:garde_enfant.size(),
			chomage:chomage.size(),
			formation:formation.size(),
			paternite:paternite.size(),			
			parental:parental.size(),			
			dif:dif.size(),
			don:don.size(),
			rtt:rtt.size(),
			sickness:sickness.size(),
			maternite:maternite.size(),
			sansSolde:sansSolde.size(),
			injustifie:injustifie.size(),
			MISE_A_PIED:MISE_A_PIED.size(),
			monthTheoritical:monthTheoritical,
			pregnancyCredit:pregnancyCredit,
			monthTheoriticalHuman:getTimeAsText(computeHumanTime(monthTheoritical),false),
			calendar:calendar
		]
		def mergedMap = cartoucheMap << yearlyCartouche
		return mergedMap
	}

	
	def getReportData(String siteId,Employee employee, Date myDate,int monthPeriod,int yearPeriod,boolean entityUpdate){
		def calendar = Calendar.instance
		def monthlyTotalTimeByEmployee = [:]
		def criteria
		def monthlyPeriodValue = 0

		//get last day of the month
		if (myDate == null){
			if (yearPeriod!=0){
				calendar.set(Calendar.MONTH,monthPeriod-1)
				calendar.set(Calendar.YEAR,yearPeriod)
			}
			calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH))
			calendar.set(Calendar.DATE,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		}else{
			calendar.time = myDate
		}
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		calendar.set(Calendar.DATE,1)
		def calendarLoop = calendar
		int month = calendar.get(Calendar.MONTH)+1 // starts at 0
		int year = calendar.get(Calendar.YEAR)			
		def data = computeWeeklyTotals( employee,  month,  year, entityUpdate)
		def cartoucheTable = getCartoucheData(employee,year,month)
		def currentContract = cartoucheTable.get('currentContract')
		def workingDays = cartoucheTable.get('workingDays')
		def holiday = cartoucheTable.get('holidays')
		def garde_enfant = cartoucheTable.get('garde_enfant')
		def chomage = cartoucheTable.get('chomage')
		def dif = cartoucheTable.get('dif')	
		def don = cartoucheTable.get('don')
		def exceptionnel = cartoucheTable.get('exceptionnel')
		def formation = cartoucheTable.get('formation')
		def paternite = cartoucheTable.get('paternite')		
		def parental = cartoucheTable.get('parental')		
		def rtt = cartoucheTable.get('rtt')
		def isCurrentMonth = cartoucheTable.get('isCurrentMonth')	
		def sickness = cartoucheTable.get('sickness')
		def maternite = cartoucheTable.get('maternite')
		def sansSolde = cartoucheTable.get('sansSolde')
		def injustifie = cartoucheTable.get('injustifie')	
		def MISE_A_PIED = cartoucheTable.get('MISE_A_PIED')
		def monthTheoritical = cartoucheTable.get('monthTheoritical')
		def pregnancyCredit = computeHumanTimeAsString(cartoucheTable.get('pregnancyCredit'))
		def yearlyHoliday = cartoucheTable.get('yearlyHolidays')
		def yearlyGarde_enfant = cartoucheTable.get('yearlyGarde_enfant')	
		def yearlyChomage = cartoucheTable.get('yearlyChomage')	
		def yearlyDif = cartoucheTable.get('yearlyDif')
		def yearlyDon = cartoucheTable.get('yearlyDon')
		def yearlyExceptionnel = cartoucheTable.get('yearlyExceptionnel')	
		def yearlyPaternite = cartoucheTable.get('yearlyPaternite')
		def yearlyParental = cartoucheTable.get('yearlyParental')
		def yearlyFormation = cartoucheTable.get('yearlyFormation')	
		def yearlyRtt = cartoucheTable.get('yearlyRtt')
		def yearlySickness = cartoucheTable.get('yearlySickness')
		def yearlyMaternite = cartoucheTable.get('yearlyMaternite')
		def yearlyTheoritical = computeHumanTimeAsString(cartoucheTable.get('yearlyTheoritical'))
		def yearlyPregnancyCredit = computeHumanTimeAsString(cartoucheTable.get('yearlyPregnancyCredit'))
		def yearlyActualTotal = computeHumanTimeAsString(cartoucheTable.get('yearlyTotalTime'))
		def yearlySansSolde = cartoucheTable.get('yearlySansSolde')
		def yearlyInjustifie = cartoucheTable.get('yearlyInjustifie')
		def yearlyMISE_A_PIED = cartoucheTable.get('yearlyMISE_A_PIED')
		def yearlySupTime = cartoucheTable.get('yearSupTime')
		def yearOpenDays = cartoucheTable.get('yearOpenDays')
		
		// ADD a computing principle for payable sup time:
		/*
		 * si Realisé - Theorique < HS j ou hebdo
		 * alors payableSUpTIme = HS j ou hebdo
		 * sinon, payableSupTime = Réalisé - Theorique
		 *
		 */
		def payableSupTime = computeHumanTime(Math.round(computePayableSupplementaryTime(employee,data.get('monthlyTotalTime') as long,monthTheoritical as long,data.get('monthlySupTime') as long)))
		def payableCompTime = 0
		if (currentContract != null && currentContract.weeklyLength != Employee.legalWeekTime && data.get('monthlyTotalTime') > monthTheoritical){
			payableCompTime = Math.round(Math.max(data.get('monthlyTotalTime')-monthTheoritical-data.get('monthlySupTime'),0))
		}		
		monthlyTotalTimeByEmployee.put(employee, computeHumanTime(data.get('monthlyTotalTime')))
		def monthlyTotal=computeHumanTime(data.get('monthlyTotalTime'))
		monthTheoritical = computeHumanTime(cartoucheTable.get('monthTheoritical'))
		def monthlyTheoriticalAsString = computeHumanTimeAsString(cartoucheTable.get('monthTheoritical'))
		Period period = (month > 5)?Period.findByYear(year):Period.findByYear(year - 1)	
		def initialCA = employeeService.getInitialCA(employee,(month>5)?Period.findByYear(year):Period.findByYear(year - 1))
		def initialRTT = employeeService.getInitialRTT(employee,(month>5)?Period.findByYear(year):Period.findByYear(year - 1))
		def departureDate
		if (employee.status.date != null){
			if (employee.status.date != null && employee.status.date <= calendarLoop.time){
				departureDate = employee.status.date
			}
		}	
		
		
		criteria = Mileage.createCriteria()
		def mileageList
		def mileageMinDate = Calendar.instance
		def mileageMaxDate = Calendar.instance
		mileageMinDate.set(Calendar.YEAR,year)
		mileageMinDate.set(Calendar.MONTH,month - 1)
		mileageMinDate.set(Calendar.DAY_OF_MONTH,1)
		mileageMaxDate.set(Calendar.YEAR,year)
		mileageMaxDate.set(Calendar.MONTH,month - 1)
		mileageMaxDate.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		
		mileageList = criteria.list {
			or{
				and {
					eq('employee',employee)
					eq('month',month)
					eq('year',year)
					le('day',calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
				}
				and {
					eq('employee',employee)
					eq('month',month)
					eq('year',year)
					ge('day',1)
				}
			}
		}
		
	
		
		if (mileageList != null){
			for (Mileage mileageIter : mileageList){
				monthlyPeriodValue += mileageIter.value
			}
		}

		return [
			mileageMinDate:mileageMinDate,
			mileageMaxDate:mileageMaxDate,
			monthlyPeriodValue:monthlyPeriodValue as long,
			yearOpenDays:yearOpenDays,
			monthlySupTime:data.get('monthlySupTime') as long,
			timeBefore7:data.get('timeBefore7') as long,	
			timeAfter20:data.get('timeAfter20') as long,	
			timeOffHours:data.get('timeOffHours') as long,
			initialCA:initialCA,
			initialRTT:initialRTT,	
			isCurrentMonth:isCurrentMonth,
			departureDate:departureDate,
			currentContract:currentContract,
			period2:period,
			dailyBankHolidayMap:data.get('dailyBankHolidayMap'),
			payableSupTime:payableSupTime,
			payableCompTime:payableCompTime,
			monthlyTotalRecapAsString:computeHumanTimeAsString(data.get('monthlyTotalTime')),
			employee:employee,
			siteId:siteId,
			userId:employee.id,
			workingDays:workingDays,
			holiday:holiday,
			rtt:rtt,
			sickness:sickness,
			maternite:maternite,
			formation:formation,
			chomage:chomage,
			garde_enfant:garde_enfant,
			sansSolde:sansSolde,			
			injustifie:injustifie,
			MISE_A_PIED:MISE_A_PIED,
			yearlyActualTotal:yearlyActualTotal,
			monthTheoritical:cartoucheTable.get('monthTheoritical'),
			monthlyTheoriticalAsString:monthlyTheoriticalAsString,
			pregnancyCredit:pregnancyCredit,
			yearlyPregnancyCredit:yearlyPregnancyCredit,
			yearlyHoliday:yearlyHoliday,
			yearlyChomage:yearlyChomage,
			yearlyGarde_enfant:yearlyGarde_enfant,
			yearlyRtt:yearlyRtt,
			yearlySickness:yearlySickness,
			yearlyMaternite:yearlyMaternite,
			yearlySansSolde:yearlySansSolde,
			yearlyInjustifie:yearlyInjustifie,
			yearlyMISE_A_PIED:yearlyMISE_A_PIED,
			yearlySupTime:computeHumanTimeAsString(yearlySupTime as long),
			dif:dif,
			don:don,
			yearlyDif:yearlyDif,	
			yearlyDon:yearlyDon,
			exceptionnel:exceptionnel,
			parental:parental,
			yearlyParental:yearlyParental,
			yearlyExceptionnel:yearlyExceptionnel,
			paternite:paternite,
			yearlyPaternite:yearlyPaternite,
			yearlyFormation:yearlyFormation,
			yearlyTheoritical:yearlyTheoritical,
			monthlyTotal:monthlyTotalTimeByEmployee,
			weeklyTotal:data.get('weeklyTotalTimeByEmployee'),
			weeklySupTotal:data.get('weeklySupTotalTimeByEmployee'),
			dailySupTotalMap:data.get('dailySupTotalMap'),
			dailyTotalMap:data.get('dailyTotalMap'),
			dailySupTotalTextMap:data.get('dailySupTotalTextMap'),
			dailyTotalTextMap:data.get('dailyTotalTextMap'),
			holidayMap:data.get('holidayMap'),
			mileageMapByDay:data.get('mileageMapByDay'),
			month:month,
			year:year,
			period:calendarLoop.getTime(),		
			weeklyAggregate:data.get('weeklyAggregate')
		]
	}
	
	def getEcartData(Site site, def monthList, Period period){
		def tmpYear
		def criteria
		def referenceRTT
		def takenRTT
		def data
		Contract currentContract
		def monthlyTheoriticalMap = [:]
		def monthlyActualMap = [:]
		def monthlyTakenRTTMap = [:]
		def ecartMap = [:]
		def ecartMinusRTTAndHSMap = [:]
		def monthlyTheoriticalByEmployee = [:]
		def monthlyActualByEmployee = [:]
		def ecartMinusRTTMap = [:]
		def ecartMinusRTTByEmployee = [:]
		def ecartMinusRTTAndHSByEmployee = [:]
		def ecartByEmployee = [:]
		def rttByEmployee = [:]
		def monthlySupTimeMap = [:]
		def monthlySupTimeMapByEmployee =[:]	
		def employeeInstanceList = (site != null) ? Employee.findAllBySite(site) : Employee.findAll("from Employee")
		def totalMonthlyTheoritical = [:]
		def totalMonthlyActual = [:]
		def totalTakenRTT = [:]
		def totalSupTime = [:]
		def totalEcart = [:]
		def totalEcartMinusRTT = [:]
		def totalEcartMinusRTTAndHS = [:]
		
		////
		def totalPeriodTheoritical = 0
		def totalEmployees = 0
		def presentEmployeeByMonth = [:]
		def totalPeriodTheoriticalByMonth = [:]
		def totalPeriodEcartByMonth = [:]
		////

		for (Employee employee:employeeInstanceList){
			monthlySupTimeMap = [:]
			monthlyTheoriticalMap = [:]
			monthlyActualMap = [:]
			monthlyTakenRTTMap = [:]
			ecartMap = [:]
			ecartMinusRTTMap = [:]
			ecartMinusRTTAndHSMap = [:]
			
			for (month in monthList){
				tmpYear = (month < 6) ? period.year + 1 : period.year
	   
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
				
				criteria = SupplementaryTime.createCriteria()	
				def supTime = criteria.get {
					and {
						eq('employee',employee)
						eq('period',period)
						eq('month',month)
					}
					maxResults(1)
				}
				if (supTime == null){
					monthlySupTimeMap.put(month,0)
					
				}else{
					monthlySupTimeMap.put(month,supTime.value as long)
				}
				
				//special case for employees for which vacations were not properly instantiated
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
				
				def theoricalTime2add = 0
				def actualTime2add = 0
				def takenRTT2add
				def actualSupTime2add = 0
				// initialization month
				if (month == 6){
					theoricalTime2add = 0
					actualTime2add = 0
					takenRTT2add = referenceRTT.counter	
				}

				// special case for 1st month of year
				if (month == 1){
					theoricalTime2add = monthlyTheoriticalMap.get(12)
					actualTime2add = monthlyActualMap.get(12)
					takenRTT2add = monthlyTakenRTTMap.get(12)
					actualSupTime2add = monthlySupTimeMap.get(12)
				}
				
				if (month != 6 && month != 1){
					theoricalTime2add = monthlyTheoriticalMap.get(month-1)
					actualTime2add = monthlyActualMap.get(month-1)
					takenRTT2add = monthlyTakenRTTMap.get(month-1)
					actualSupTime2add = monthlySupTimeMap.get(month-1)
					
				}
				if (data.get('monthTheoritical') != null){
					monthlyTheoriticalMap.put(month, data.get('monthTheoritical') + theoricalTime2add)
				}else {
					monthlyTheoriticalMap.put(month, theoricalTime2add)	
				}
				if (totalMonthlyTheoritical.get(month) == null){
					totalMonthlyTheoritical.put(month, monthlyTheoriticalMap.get(month))
				}else{
					totalMonthlyTheoritical.put(month, totalMonthlyTheoritical.get(month) + monthlyTheoriticalMap.get(month))			
				}
				
				/////
				if (totalPeriodTheoriticalByMonth.get(month) != null){
					totalPeriodTheoriticalByMonth.put(month,totalPeriodTheoriticalByMonth.get(month) + totalMonthlyTheoritical.get(month) as long)
				}else{
					totalPeriodTheoriticalByMonth.put(month,totalMonthlyTheoritical.get(month) as long)
				}
				
				
				////
				
				if (monthlyTotalInstance != null){		
					
					////
					if (presentEmployeeByMonth.get(month) == null){
						presentEmployeeByMonth.put(month,1)
					}else{
						presentEmployeeByMonth.put(month,presentEmployeeByMonth.get(month) + 1)
					}
					////
					
							
					monthlyActualMap.put(month, monthlyTotalInstance.elapsedSeconds + actualTime2add)
				}else{
					monthlyActualMap.put(month, actualTime2add)
				}
				if (totalMonthlyActual.get(month) == null){
					totalMonthlyActual.put(month, monthlyActualMap.get(month))
				}else{
					totalMonthlyActual.put(month, totalMonthlyActual.get(month) + monthlyActualMap.get(month))
				}
				
				if (takenRTT!=null){
					monthlyTakenRTTMap.put(month,takenRTT2add - takenRTT.size())
				}else{
					monthlyTakenRTTMap.put(month,takenRTT2add)
				}
				if (totalTakenRTT.get(month) == null){
					totalTakenRTT.put(month, monthlyTakenRTTMap.get(month))
				}else{
					totalTakenRTT.put(month, totalTakenRTT.get(month) + monthlyTakenRTTMap.get(month))
				}							
				if (supTime != null){
					monthlySupTimeMap.put(month,(supTime.value as long) + actualSupTime2add)				
				}else{
					monthlySupTimeMap.put(month,actualSupTime2add)		
				}
				if (totalSupTime.get(month) == null){
					totalSupTime.put(month, monthlySupTimeMap.get(month))
				}else{
					totalSupTime.put(month, totalSupTime.get(month) + monthlySupTimeMap.get(month))
				}
						
				ecartMap.put(month, monthlyActualMap.get(month)-monthlyTheoriticalMap.get(month))
				if (totalEcart.get(month) == null){
					totalEcart.put(month, ecartMap.get(month))
				}else{
					totalEcart.put(month, totalEcart.get(month) + ecartMap.get(month))
				}
				
				/////
				
				if (totalPeriodEcartByMonth.get(month) != null){
					totalPeriodEcartByMonth.put(month,totalPeriodEcartByMonth.get(month) + totalEcart.get(month) as long)
				}else{
					totalPeriodEcartByMonth.put(month,totalEcart.get(month) as long)
				}
				/////
				currentContract = data.get('currentContract')	
				if (currentContract != null){
					ecartMinusRTTMap.put(month, ecartMap.get(month)-(3600*(monthlyTakenRTTMap.get(month))*(currentContract.weeklyLength/Employee.WeekOpenedDays)) as long)
					ecartMinusRTTAndHSMap.put(month, (ecartMap.get(month)-(3600*(monthlyTakenRTTMap.get(month))*(currentContract.weeklyLength/Employee.WeekOpenedDays)) - monthlySupTimeMap.get(month))as long)
					
				}else{
					log.debug('currentContract is null for employee  '+employee+ ' and month= '+month)
					ecartMinusRTTMap.put(month, ecartMap.get(month) as long)
					ecartMinusRTTAndHSMap.put(month, (ecartMap.get(month) - monthlySupTimeMap.get(month))as long)				
				}		
				
				if (totalEcartMinusRTT.get(month) == null){
					totalEcartMinusRTT.put(month, ecartMinusRTTMap.get(month))
				}else{
					totalEcartMinusRTT.put(month, totalEcartMinusRTT.get(month) + ecartMinusRTTMap.get(month))
				}
				if (totalEcartMinusRTTAndHS.get(month) == null){
					totalEcartMinusRTTAndHS.put(month, ecartMinusRTTAndHSMap.get(month))
				}else{
					totalEcartMinusRTTAndHS.put(month, totalEcartMinusRTTAndHS.get(month) + ecartMinusRTTAndHSMap.get(month))
				}
			}
			monthlySupTimeMapByEmployee.put(employee,monthlySupTimeMap)
			monthlyTheoriticalByEmployee.put(employee,monthlyTheoriticalMap)
			monthlyActualByEmployee.put(employee,monthlyActualMap)
			ecartByEmployee.put(employee, ecartMap)
			ecartMinusRTTByEmployee.put(employee,ecartMinusRTTMap)
			ecartMinusRTTAndHSByEmployee.put(employee,ecartMinusRTTAndHSMap)		
			rttByEmployee.put(employee, monthlyTakenRTTMap)
		}
		return [
			period:period,
			employeeInstanceList:employeeInstanceList,
			monthlyTheoriticalByEmployee:monthlyTheoriticalByEmployee,
			monthlyActualByEmployee:monthlyActualByEmployee,
			ecartByEmployee:ecartByEmployee,
			rttByEmployee:rttByEmployee,
			ecartMinusRTTByEmployee:ecartMinusRTTByEmployee,
			monthList:monthList,
			monthlySupTimeMapByEmployee:monthlySupTimeMapByEmployee,
			ecartMinusRTTAndHSByEmployee:ecartMinusRTTAndHSByEmployee,
			totalMonthlyTheoritical:totalMonthlyTheoritical,
			totalMonthlyActual:totalMonthlyActual,
			totalSupTime:totalSupTime,
			totalEcart:totalEcart,
			totalEcartMinusRTT:totalEcartMinusRTT,
			totalEcartMinusRTTAndHS:totalEcartMinusRTTAndHS,
			totalTakenRTT:totalTakenRTT,
			totalPeriodTheoriticalByMonth:totalPeriodTheoriticalByMonth,
			totalPeriodEcartByMonth:totalPeriodEcartByMonth,
			presentEmployeeByMonth:presentEmployeeByMonth		
		]
	}

	def getMonthlySupTime(Employee employee,int month, int year){
		log.error('getMonthlySupTime called with month: '+month+' year: '+year)
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
		}
		
		while(calendarLoop.get(Calendar.DAY_OF_YEAR)<=lastDayOfMonth.get(Calendar.DAY_OF_YEAR)){
			if ((calendarLoop.get(Calendar.DAY_OF_YEAR)+7)>lastDayOfMonth.get(Calendar.DAY_OF_YEAR)){
				break
			}
			supTime += computeSupplementaryTime(employee,calendarLoop.get(Calendar.WEEK_OF_YEAR), calendarLoop.get(Calendar.YEAR))
			calendarLoop.roll(Calendar.DAY_OF_YEAR,7)
		}
		return supTime
	}
	
	def getAnnualReportData(int year, Employee employee){
		def criteria
		def dailySeconds
		def monthlyTotalTime
		def monthlySupTotalTime
		def yearMonthMap = [:]
		def yearTotalMap = [:]
		def yearMonthlySupTime = [:]
		def yearMonthlyCompTime = [:]
		def yearMap = [:]
		def yearSundayMap = [:]
		def yearBankHolidayMap =[:]
		def cartoucheTable=[]
		def firstWeekOfMonth
		def lastWeekOfMonth
		def payableCompTime
		def annualMonthlySupTime = 0
		def annualTheoritical = 0
		def annualTotal = 0
		def annualHoliday = 0
		def annualGarde_enfant = 0
		def annualChomage = 0
		def annualRTT = 0
		def annualCSS = 0
		def annualINJUSTIFIE = 0
		def annualMISE_A_PIED = 0
		def annualDIF = 0
		def annualDON = 0
		def annualSickness = 0
		def annualMaternite = 0
		def annualExceptionnel = 0	
		def annualPaternite = 0	
		def annualParental = 0
		def annualFormation = 0
		def annualPayableSupTime = 0
		def annualPayableCompTime = 0
		def annualWorkingDays = 0
		def annualEmployeeWorkingDays = 0
		def annualTotalIncludingHS = 0
		def annualQuotaIncludingExtra = 0
		def annualTheoriticalIncludingExtra = 0
		def annualSupTimeAboveTheoritical = 0
		def annualGlobalSupTimeToPay = 0
		def annualSundayTime = 0
		def annualBankHolidayTime = 0	
		def annualPaidHS = 0 as long
		def calendar = Calendar.instance
		def currentYear=year
		def monthlyPresentDays = 0
		def monthlyWorkingDays = [:]
		def monthlyTakenHolidays = [:]
		def monthlyQuotaIncludingExtra = [:]		
		def period = Period.findByYear(year)
		def remainingCA = employeeService.getRemainingCA(employee,period)
		def takenCA = employeeService.getTakenCA(employee,period)
		def initialCA = employeeService.getInitialCA(employee,period)			
		def monthList = [6,7,8,9,10,11,12,1,2,3,4,5]
		
		for (int currentMonth in monthList){		
			def tmpSunDayTime = 0
			def tmpBankHolidayTime = 0
			monthlyPresentDays = 0
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
				log.debug('tmpCal: '+tmpCal.time)
				if (tmpCal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY && tmpCal.get(Calendar.MONTH) == currentMonth - 1){
					log.debug("sunday: "+tmpCal.time)
					criteria = DailyTotal.createCriteria()
					def sundayDaily = criteria.get {
						and {
							eq('employee',employee)
							eq('month',tmpCal.get(Calendar.MONTH)+1)
							eq('year',tmpCal.get(Calendar.YEAR))
							eq('day',tmpCal.get(Calendar.DAY_OF_MONTH))						
						}
					}
					if (sundayDaily != null){
						annualSundayTime += sundayDaily.elapsedSeconds
						tmpSunDayTime += sundayDaily.elapsedSeconds
					}
				}
				tmpCal.roll(Calendar.DAY_OF_YEAR, 1)
				if (tmpCal.get(Calendar.DAY_OF_MONTH) == tmpCal.getActualMaximum(Calendar.DAY_OF_MONTH))	{
					log.debug('adding sunday time: '+tmpCal.time)
					log.debug('time added: '+annualSundayTime)				
					yearSundayMap.put(currentMonth,getTimeAsText(computeHumanTime(tmpSunDayTime as long),false))
					break;
				}					
			}
			
			// finding time spent during bank holidays:
			criteria = BankHoliday.createCriteria()		
			def bankHolidayList = criteria.list {
				and {
					eq('month',currentMonth)
					eq('year',currentYear)
				}
			}
			
			for (BankHoliday bankHoliday : bankHolidayList){
				criteria = DailyTotal.createCriteria()
				def bankHolidayDaily = criteria.list {
					and {
						eq('employee',employee)
						eq('month',bankHoliday.month)
						eq('year',bankHoliday.year)
						eq('day',bankHoliday.day)
					}
				}
				if (bankHolidayDaily != null && bankHolidayDaily.size() > 0){
					annualBankHolidayTime += bankHolidayDaily.elapsedSeconds
					tmpBankHolidayTime += bankHolidayDaily.elapsedSeconds
				}
			}
			yearBankHolidayMap.put(currentMonth,getTimeAsText(computeHumanTime(tmpBankHolidayTime as long),false))
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
				dailySeconds = (getDailyTotal(dailyTotal)).get("elapsedSeconds")
				monthlyTotalTime += dailySeconds
				annualEmployeeWorkingDays += 1
				monthlyPresentDays += 1
			}
			monthlyWorkingDays.put(currentMonth,monthlyPresentDays)
			yearTotalMap.put(currentMonth, getTimeAsText(computeHumanTime(monthlyTotalTime),false))
			
			// computing totals.		
			annualSupTimeAboveTheoritical += monthlyTotalTime
			def currentPeriod = (currentMonth < 6) ? Period.findByYear(currentYear-1) : Period.findByYear(currentYear)			
			criteria = SupplementaryTime.createCriteria()
			try {				
				criteria = SupplementaryTime.createCriteria()
				def monthlySupTotalTimeList = criteria.list {
					and {
						eq('employee',employee)
						eq('month',currentMonth)
						eq('period',currentPeriod)
					}
				}
				
				// we have a problem, we should not have more than 1 
				if (monthlySupTotalTimeList.size() > 1){
					for (SupplementaryTime zombie:monthlySupTotalTimeList){
						log.error('zombie.value: '+zombie.value)
						if (zombie.value == 0){
							log.debug('zombie is 0: ')
							zombie.delete(flush:true)
						}else{
							monthlySupTotalTime = zombie
						}
					}
				}else{
					if (monthlySupTotalTimeList.size() > 0)
						monthlySupTotalTime = monthlySupTotalTimeList.get(0)
				}
				/*
				criteria = SupplementaryTime.createCriteria()
				monthlySupTotalTime = criteria.get {
					and {
						eq('employee',employee)
						eq('month',currentMonth)
						eq('period',currentPeriod)
					}
					maxResults(1)
				}
				*/
				//log.error('monthlySupTotalTime: '+monthlySupTotalTime)
			}catch(org.hibernate.NonUniqueResultException ex){
				log.error('error getting monthlySupTotalTime for: '+employee+' and month: '+currentMonth+' and period: '+currentPeriod)
			}
			//monthlySupTotalTime = weeklyTotals.get('monthlySupTime')//getMonthlySupTime(employee,currentMonth, currentYear)
			if (monthlySupTotalTime != null){
				annualMonthlySupTime += monthlySupTotalTime.value as long
				yearMonthlySupTime.put(currentMonth,Math.round(monthlySupTotalTime.value) as long)
			}else{
				annualMonthlySupTime += 0 as long
				yearMonthlySupTime.put(currentMonth,0 as long)
			}
			def monthTheoritical = cartoucheTable.getAt('monthTheoritical')
			
			// in order to compute complementary time, we are going to look for employees whose contract equal 35h over a sub-period of time during the month
			if (utilService.getActiveFullTimeContract( currentMonth,  currentYear, employee)){
				if (monthlyTotalTime > monthTheoritical){
					payableCompTime = (monthlySupTotalTime != null) ? Math.max(monthlyTotalTime-monthTheoritical-monthlySupTotalTime.value as long,0) : Math.max(monthlyTotalTime-monthTheoritical,0)
					yearMonthlyCompTime.put(currentMonth, getTimeAsText(computeHumanTime(payableCompTime as long),false))
				}else{
					payableCompTime = 0
					yearMonthlyCompTime.put(currentMonth, getTimeAsText(computeHumanTime(0),false))
				}
			}else{
				payableCompTime = 0	
			}
			def tmpQuota = (monthlySupTotalTime != null) ? monthlyTotalTime + (monthlySupTotalTime.value as long) + payableCompTime : monthlyTotalTime + payableCompTime
			monthlyQuotaIncludingExtra.put(currentMonth, computeHumanTime(Math.round(tmpQuota) as long))		
			annualTheoritical += cartoucheTable.getAt('monthTheoritical')
			annualHoliday += cartoucheTable.getAt('holidays')
			annualGarde_enfant += cartoucheTable.getAt('garde_enfant')
			annualChomage += cartoucheTable.getAt('chomage')
			monthlyTakenHolidays.put(currentMonth, initialCA - annualHoliday)
			annualRTT += cartoucheTable.getAt('rtt')
			annualDIF += cartoucheTable.getAt('dif')		
			annualDON += cartoucheTable.getAt('don')
			annualCSS += cartoucheTable.getAt('sansSolde')
			annualINJUSTIFIE += cartoucheTable.getAt('injustifie')
			annualMISE_A_PIED += cartoucheTable.getAt('MISE_A_PIED')
			annualSickness += cartoucheTable.getAt('sickness')
			annualMaternite += cartoucheTable.getAt('maternite')		
			annualExceptionnel += cartoucheTable.getAt('exceptionnel')
			annualPaternite += cartoucheTable.getAt('paternite')	
			annualParental += cartoucheTable.getAt('parental')		
			annualFormation += cartoucheTable.getAt('formation')	
			annualWorkingDays += cartoucheTable.getAt('workingDays')
			if (monthlySupTotalTime != null)
				annualPayableSupTime += monthlySupTotalTime.value as long
			annualPayableCompTime += payableCompTime
			annualTotal += monthlyTotalTime
			annualQuotaIncludingExtra += tmpQuota
		}
		
		annualTheoriticalIncludingExtra = annualTheoritical + annualPayableSupTime
		annualSupTimeAboveTheoritical = annualSupTimeAboveTheoritical - annualTheoriticalIncludingExtra	
		annualGlobalSupTimeToPay = (annualSupTimeAboveTheoritical > 0) ? (annualSupTimeAboveTheoritical + annualPayableSupTime) : annualPayableSupTime			
		annualTheoriticalIncludingExtra = Math.round(annualTheoriticalIncludingExtra) as long
		
		// if the total is less than 0, consider only above daily and weekly threshold HS
		annualGlobalSupTimeToPay = (annualGlobalSupTimeToPay > 0 )? Math.round(annualGlobalSupTimeToPay) as long : Math.round(annualPayableSupTime) as long
		// if the total is less than 0, set it to 0 as it makes no sens
		annualSupTimeAboveTheoritical = (annualSupTimeAboveTheoritical > 0 ? Math.round(annualSupTimeAboveTheoritical) as long : Math.round(0) as long)

		def paymentList = Payment.findAllByEmployeeAndPeriod(employee,period)
		
		for (Payment payment:paymentList){
			annualPaidHS += payment.amountPaid as long
		}
		return [
			yearOpenDays:cartoucheTable.getAt('yearOpenDays'),
			monthlyQuotaIncludingExtra:monthlyQuotaIncludingExtra,
			monthlyTakenHolidays:monthlyTakenHolidays,
			monthlyWorkingDays:monthlyWorkingDays,
			takenCA:takenCA,
			initialCA:initialCA,
			remainingCA:remainingCA,
			annualEmployeeWorkingDays:annualEmployeeWorkingDays,	
			annualMonthlySupTime:Math.round(annualMonthlySupTime) as long,
			annualTheoritical:Math.round(annualTheoritical) as long,			
			annualTheoriticalIncludingExtra:annualTheoriticalIncludingExtra,
			annualSupTimeAboveTheoritical:annualSupTimeAboveTheoritical,
			annualGlobalSupTimeToPay:annualGlobalSupTimeToPay,
			annualPaidHS:annualPaidHS,
			annualHoliday:annualHoliday,
			annualGarde_enfant:annualGarde_enfant,	
			annualRTT:annualRTT,
			annualDIF:annualDIF,			
			annualChomage:annualChomage,
			annualDON:annualDON,
			annualCSS:annualCSS,
			annualINJUSTIFIE:annualINJUSTIFIE,
			annualMISE_A_PIED:annualMISE_A_PIED,
			annualSickness:annualSickness,
			annualMaternite:annualMaternite,			
			annualExceptionnel:annualExceptionnel,
			annualParental:annualParental,
			annualFormation:annualFormation,
			annualPaternite:annualPaternite,
			annualWorkingDays:annualWorkingDays,
			annualPayableSupTime:Math.round(annualPayableSupTime) as long,
			annualPayableCompTime:getTimeAsText(computeHumanTime(Math.round(annualPayableCompTime) as long),false),
			annualTotal:Math.round(annualTotal) as long,
			annualSundayTime:Math.round(annualSundayTime) as long,
			annualBankHolidayTime:Math.round(annualBankHolidayTime) as long,
			lastYear:year,
			thisYear:year+1,
			yearMap:yearMap,
			yearMonthlyCompTime:yearMonthlyCompTime,
			yearMonthlySupTime:yearMonthlySupTime,
			yearTotalMap:yearTotalMap,
			yearMonthMap:yearMonthMap,
			yearSundayMap:yearSundayMap,
			yearBankHolidayMap:yearBankHolidayMap,
			userId:employee.id,
			employee:employee
		]
	}	
	

	def getAnnualReportDataNOHS(int year, Employee employee){
		def criteria
		def dailySeconds
		def monthlyTotalTime
		def monthlySupTotalTime
		def yearMonthMap = [:]
		def yearTotalMap = [:]
		def yearMonthlySupTime = [:]
		def yearMonthlyCompTime = [:]
		def yearMap = [:]
		def yearSundayMap = [:]
		def yearBankHolidayMap =[:]
		def cartoucheTable=[]
		def firstWeekOfMonth
		def lastWeekOfMonth
		def payableCompTime
		def annualMonthlySupTime = 0
		def annualTheoritical = 0
		def annualTotal = 0
		def annualHoliday = 0
		def annualGarde_enfant = 0
		def annualChomage = 0
		def annualRTT = 0
		def annualCSS = 0
		def annualINJUSTIFIE = 0
		def annualMISE_A_PIED = 0
		def annualDIF = 0
		def annualDON = 0
		def annualSickness = 0
		def annualMaternite = 0	
		def annualExceptionnel = 0
		def annualFormation = 0
		def annualPaternite = 0
		def annualParental = 0
		def annualPayableSupTime = 0
		def annualPayableCompTime = 0
		def annualWorkingDays = 0
		def annualEmployeeWorkingDays = 0
		def annualTotalIncludingHS = 0
		def annualQuotaIncludingExtra = 0
		def annualTheoriticalIncludingExtra = 0
		def annualSupTimeAboveTheoritical = 0
		def annualGlobalSupTimeToPay = 0
		def annualSundayTime = 0
		def annualBankHolidayTime = 0
		def annualPaidHS = 0 as long
		def calendar = Calendar.instance
		def currentYear=year
		def monthlyPresentDays = 0
		def monthlyWorkingDays = [:]
		def monthlyTakenHolidays = [:]
		def monthlyQuotaIncludingExtra = [:]
		def period = Period.findByYear(year)
		def remainingCA = employeeService.getRemainingCA(employee,period)
		def takenCA = employeeService.getTakenCA(employee,period)
		def initialCA = employeeService.getInitialCA(employee,period)
		def monthList = [6,7,8,9,10,11,12,1,2,3,4,5]
		
		for (int currentMonth in monthList){
			def tmpSunDayTime = 0
			def tmpBankHolidayTime = 0
			monthlyPresentDays = 0
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
				log.debug('tmpCal: '+tmpCal.time)
				if (tmpCal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY && tmpCal.get(Calendar.MONTH) == currentMonth - 1){
					log.debug("sunday: "+tmpCal.time)
					criteria = DailyTotal.createCriteria()
					def sundayDaily = criteria.get {
						and {
							eq('employee',employee)
							eq('month',tmpCal.get(Calendar.MONTH)+1)
							eq('year',tmpCal.get(Calendar.YEAR))
							eq('day',tmpCal.get(Calendar.DAY_OF_MONTH))
						}
					}
					if (sundayDaily != null){
						annualSundayTime += sundayDaily.elapsedSeconds
						tmpSunDayTime += sundayDaily.elapsedSeconds
					}
				}
				tmpCal.roll(Calendar.DAY_OF_YEAR, 1)
				if (tmpCal.get(Calendar.DAY_OF_MONTH) == tmpCal.getActualMaximum(Calendar.DAY_OF_MONTH))	{
					log.debug('adding sunday time: '+tmpCal.time)
					log.debug('time added: '+annualSundayTime)
					yearSundayMap.put(currentMonth,getTimeAsText(computeHumanTime(tmpSunDayTime as long),false))
					break;
				}
			}
			
			// finding time spent during bank holidays:
			criteria = BankHoliday.createCriteria()
			def bankHolidayList = criteria.list {
				and {
					eq('month',currentMonth)
					eq('year',currentYear)
				}
			}
			
			for (BankHoliday bankHoliday : bankHolidayList){
				criteria = DailyTotal.createCriteria()
				def bankHolidayDaily = criteria.list {
					and {
						eq('employee',employee)
						eq('month',bankHoliday.month)
						eq('year',bankHoliday.year)
						eq('day',bankHoliday.day)
					}
				}
				if (bankHolidayDaily != null && bankHolidayDaily.size() > 0){
					annualBankHolidayTime += bankHolidayDaily.elapsedSeconds
					tmpBankHolidayTime += bankHolidayDaily.elapsedSeconds
				}
			}
			yearBankHolidayMap.put(currentMonth,getTimeAsText(computeHumanTime(tmpBankHolidayTime as long),false))
			yearMap.put(currentMonth, currentYear)	
			cartoucheTable = getYearCartoucheData(employee,currentYear,currentMonth)

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
				dailySeconds = (getDailyTotal(dailyTotal)).get("elapsedSeconds")
				monthlyTotalTime += dailySeconds
				annualEmployeeWorkingDays += 1
				monthlyPresentDays += 1
			}
			monthlyWorkingDays.put(currentMonth,monthlyPresentDays)
			yearTotalMap.put(currentMonth, getTimeAsText(computeHumanTime(monthlyTotalTime),false))
			annualTheoritical += cartoucheTable.getAt('yearlyTheoritical')
			annualHoliday += cartoucheTable.getAt('yearlyHolidays')
			annualGarde_enfant += cartoucheTable.getAt('yearlyGarde_enfant')
			annualChomage += cartoucheTable.getAt('yearlyChomage')
			monthlyTakenHolidays.put(currentMonth, initialCA - annualHoliday)
			annualRTT += cartoucheTable.getAt('yearlyRtt')
			annualDIF += cartoucheTable.getAt('yearlyDif')
			annualDON += cartoucheTable.getAt('yearlyDon')
			annualCSS += cartoucheTable.getAt('yearlySansSolde')
			annualINJUSTIFIE += cartoucheTable.getAt('yearlyInjustifie')			
			annualMISE_A_PIED += cartoucheTable.getAt('yearlyMISE_A_PIED')			
			annualSickness += cartoucheTable.getAt('yearlySickness')
			annualMaternite += cartoucheTable.getAt('yearlyMaternite')		
			annualExceptionnel += cartoucheTable.getAt('yearlyExceptionnel')
			annualPaternite += cartoucheTable.getAt('yearlyPaternite')
			annualParental += cartoucheTable.getAt('yearlyParental')
			annualFormation += cartoucheTable.getAt('yearlyFormation')
			annualWorkingDays += cartoucheTable.getAt('yearOpenDays')
			annualTotal += monthlyTotalTime
		}
		
		//annualTheoriticalIncludingExtra = getTimeAsText(computeHumanTime(Math.round(annualTheoriticalIncludingExtra) as long),false)
		
		// if the total is less than 0, consider only above daily and weekly threshold HS
		// if the total is less than 0, set it to 0 as it makes no sens

		def paymentList = Payment.findAllByEmployeeAndPeriod(employee,period)
		
		for (Payment payment:paymentList){
			annualPaidHS += payment.amountPaid as long
		}

		return [
			monthlyQuotaIncludingExtra:monthlyQuotaIncludingExtra,
			monthlyTakenHolidays:monthlyTakenHolidays,
			monthlyWorkingDays:monthlyWorkingDays,
			takenCA:takenCA,
			initialCA:initialCA,
			remainingCA:remainingCA,
			yearOpenDays:cartoucheTable.getAt('yearOpenDays'),
			annualMonthlySupTime:Math.round(annualMonthlySupTime) as long,
			annualTheoritical:Math.round(annualTheoritical) as long,
			annualTheoriticalIncludingExtra:annualTheoriticalIncludingExtra,
			annualSupTimeAboveTheoritical:annualSupTimeAboveTheoritical,
			annualGlobalSupTimeToPay:annualGlobalSupTimeToPay,
			annualPaidHS:annualPaidHS,
			annualHoliday:annualHoliday,
			annualGarde_enfant:annualGarde_enfant,
			annualChomage:annualChomage,
			annualRTT:annualRTT,
			annualDIF:annualDIF,
			annualDON:annualDON,
			annualCSS:annualCSS,
			annualINJUSTIFIE:annualINJUSTIFIE,
			annualMISE_A_PIED:annualMISE_A_PIED,
			annualSickness:annualSickness,
			annualMaternite:annualMaternite,
			annualExceptionnel:annualExceptionnel,
			annualPaternite:annualPaternite,
			annualParental:annualParental,
			annualFormation:annualFormation,
			annualWorkingDays:annualWorkingDays,
			annualPayableSupTime:Math.round(annualPayableSupTime) as long,
			annualPayableCompTime:getTimeAsText(computeHumanTime(Math.round(annualPayableCompTime) as long),false),
			annualTotal:Math.round(annualTotal) as long,
			annualSundayTime:Math.round(annualSundayTime) as long,
			annualBankHolidayTime:Math.round(annualBankHolidayTime) as long,
			yearMap:yearMap,
			yearMonthlyCompTime:yearMonthlyCompTime,
			yearMonthlySupTime:yearMonthlySupTime,
			yearTotalMap:yearTotalMap,
			yearMonthMap:yearMonthMap,
			yearSundayMap:yearSundayMap,
			yearBankHolidayMap:yearBankHolidayMap,
			userId:employee.id,
			employee:employee
		]
	}
		
	def getOffHoursTime(Employee employee, def year){
		log.error('getOffHoursTime called with param: year= '+year+' and employee: '+employee)
		def annualBefore7Time = 0
		def annualAfter20Time = 0
		def data
		def monthList = [6,7,8,9,10,11,12,1,2,3,4,5]
		for (int currentMonth in monthList){
			data = computeWeeklyTotals( employee,  currentMonth,  (currentMonth < 6) ? year + 1 : year,true)
			def timeBefore7 = data.get('timeBefore7')
			def timeAfter20 = data.get('timeAfter20')
			annualBefore7Time += timeBefore7
			annualAfter20Time += timeAfter20
		}	
		def annualOffHoursTime = annualBefore7Time + annualAfter20Time
		def annualOffHoursTimeDecimal = computeHumanTime(annualOffHoursTime as long)
		annualOffHoursTimeDecimal=(annualOffHoursTimeDecimal.get(0)+annualOffHoursTimeDecimal.get(1)/60).setScale(2,2)		
		return [annualBefore7Time:annualBefore7Time,annualAfter20Time: annualAfter20Time,annualOffHoursTime:annualOffHoursTime,annualOffHoursTimeDecimal:annualOffHoursTimeDecimal]	
	}
	
	def getOffHoursTimeNoUpdate(Employee employee, def year){
		log.error('getOffHoursTimeNoUpdate called with param: year= '+year+' and employee: '+employee)
		def annualBefore7Time = 0
		def annualAfter20Time = 0
		def data
		def monthList = [6,7,8,9,10,11,12,1,2,3,4,5]
		for (int currentMonth in monthList){
			data = computeWeeklyTotals( employee,  currentMonth,  (currentMonth < 6) ? year + 1 : year,false)
			def timeBefore7 = data.get('timeBefore7')
			def timeAfter20 = data.get('timeAfter20')
			annualBefore7Time += timeBefore7
			annualAfter20Time += timeAfter20
		}
		def annualOffHoursTime = annualBefore7Time + annualAfter20Time
		def annualOffHoursTimeDecimal = computeHumanTime(annualOffHoursTime as long)
		annualOffHoursTimeDecimal=(annualOffHoursTimeDecimal.get(0)+annualOffHoursTimeDecimal.get(1)/60).setScale(2,2)
		return [annualBefore7Time:annualBefore7Time,annualAfter20Time: annualAfter20Time,annualOffHoursTime:annualOffHoursTime,annualOffHoursTimeDecimal:annualOffHoursTimeDecimal]
	}
	
	def getAbsencesBetweenDates(Employee employee,Date startDate,Date endDate){
		def absenceMap = [:]
		def	criteria = Absence.createCriteria()
		def patSundayCount = 0
		// get cumul sickness
		def sickness = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.MALADIE)
			}
		}
		absenceMap.put(AbsenceType.MALADIE, sickness.size())	
		
		criteria = Absence.createCriteria()
		def maternite = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.MATERNITE)
			}
		}
		absenceMap.put(AbsenceType.MATERNITE, maternite.size())
		
		// get cumul holidays
		criteria = Absence.createCriteria()
		def holidays = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.VACANCE)
			}
		}
		absenceMap.put(AbsenceType.VACANCE, holidays.size())	
		
		criteria = Absence.createCriteria()
		def garde_enfant = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.GARDE_ENFANT)
			}
		}
		absenceMap.put(AbsenceType.GARDE_ENFANT, garde_enfant.size())
		
		criteria = Absence.createCriteria()
		def chomage = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.CHOMAGE)
			}
		}
		absenceMap.put(AbsenceType.CHOMAGE, chomage.size())
			
		criteria = Absence.createCriteria()
		def exceptionnel = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.EXCEPTIONNEL)
			}
		}
		absenceMap.put(AbsenceType.EXCEPTIONNEL, exceptionnel.size())	
		
		criteria = Absence.createCriteria()
		def parental = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.PARENTAL)
			}
		}
		absenceMap.put(AbsenceType.PARENTAL, parental.size())
		
		criteria = Absence.createCriteria()
		def formation = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.FORMATION)
			}
		}
		absenceMap.put(AbsenceType.FORMATION, formation.size())
		
		criteria = Absence.createCriteria()
		def paternite = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.PATERNITE)
			}
		}
		
		for (Absence tmpPat : paternite){
			//log.error('tmpPat: '+tmpPat)
			if (tmpPat.date.getAt(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				//log.error('a sunday exists. ')
				patSundayCount += 1
			}
		}
		absenceMap.put("patSundayCount",patSundayCount)
		absenceMap.put(AbsenceType.PATERNITE, paternite.size())	
			
		criteria = Absence.createCriteria()
		def dif = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.DIF)
			}
		}	
		absenceMap.put(AbsenceType.DIF, dif.size())	
		
		criteria = Absence.createCriteria()
		def don = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.DON)
			}
		}
		absenceMap.put(AbsenceType.DON, don.size())
					
		criteria = Absence.createCriteria()
		def sansSolde = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.CSS)
			}
		}
		absenceMap.put(AbsenceType.CSS, sansSolde.size())
		
		criteria = Absence.createCriteria()
		def injustifie = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.INJUSTIFIE)
			}
		}
		absenceMap.put(AbsenceType.INJUSTIFIE, injustifie.size())
		
		criteria = Absence.createCriteria()
		def MISE_A_PIED = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.MISE_A_PIED)
			}
		}
		absenceMap.put(AbsenceType.MISE_A_PIED, MISE_A_PIED.size())
		
		criteria = Absence.createCriteria()
		def pregnancy = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startDate)
				le('date',endDate)
				eq('type',AbsenceType.GROSSESSE)
			}
		}
		def pregnancyCredit=30*60*pregnancy.size()
		absenceMap.put(AbsenceType.GROSSESSE, pregnancyCredit)		
		return absenceMap
	}
		
	def getAbsences(Employee employee,int month,int year){
		def absenceMap = [:]
		def	criteria = Contract.createCriteria()
		
		// get cumul sickness
		criteria = Absence.createCriteria()
		def sickness = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.MALADIE)
			}
		}
		absenceMap.put(AbsenceType.MALADIE, sickness.size())
		
		criteria = Absence.createCriteria()
		def maternite = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.MATERNITE)
			}
		}
		absenceMap.put(AbsenceType.MATERNITE, maternite.size())
		
		// get cumul holidays
		criteria = Absence.createCriteria()
		def holidays = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.VACANCE)
			}
		}
		absenceMap.put(AbsenceType.VACANCE, holidays.size())
		
		criteria = Absence.createCriteria()
		def garde_enfant = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.GARDE_ENFANT)
			}
		}
		absenceMap.put(AbsenceType.GARDE_ENFANT, garde_enfant.size())
		
		criteria = Absence.createCriteria()
		def chomage = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.CHOMAGE)
			}
		}
		absenceMap.put(AbsenceType.CHOMAGE, chomage.size())
		
		criteria = Absence.createCriteria()
		def exceptionnel = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.EXCEPTIONNEL)
			}
		}
		absenceMap.put(AbsenceType.EXCEPTIONNEL, exceptionnel.size())
		
		criteria = Absence.createCriteria()
		def parental = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.PARENTAL)
			}
		}
		absenceMap.put(AbsenceType.PARENTAL, parental.size())
		
		criteria = Absence.createCriteria()
		def formation = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.FORMATION)
			}
		}
		absenceMap.put(AbsenceType.FORMATION, formation.size())
				
		criteria = Absence.createCriteria()
		def sansSolde = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.CSS)
			}
		}
		absenceMap.put(AbsenceType.CSS, sansSolde.size())
		
		criteria = Absence.createCriteria()
		def injustifie = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.INJUSTIFIE)
			}
		}
		absenceMap.put(AbsenceType.INJUSTIFIE, injustifie.size())
		
		criteria = Absence.createCriteria()
		def MISE_A_PIED = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.MISE_A_PIED)
			}
		}
		absenceMap.put(AbsenceType.MISE_A_PIED, MISE_A_PIED.size())
		
		criteria = Absence.createCriteria()
		def paternite = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.PATERNITE)
			}
		}
		absenceMap.put(AbsenceType.PATERNITE, paternite.size())
		
				
		criteria = Absence.createCriteria()
		def pregnancy = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.GROSSESSE)
			}
		}
		def pregnancyCredit=30*60*pregnancy.size()
		absenceMap.put(AbsenceType.GROSSESSE, pregnancyCredit)
		
		return absenceMap
	}
	
	def getMonthTheoritical(Employee employee, int month,int year){
		def monthTheoritical = 0
		def counter = 0
		def isOut = false
		def totalNumberOfDays
		def weeklyContractTime
		def contract
		def criteria
		def previousContracts
		def remainingDays
		def currentStatus = employee.status
		def realOpenDays
		def departureDate
		def startDate
		def endDate
		def startCalendar = Calendar.instance
		def endCalendar = Calendar.instance		
		def calendarCompute = Calendar.instance
		
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.set(Calendar.YEAR,year)
		startCalendar.set(Calendar.MONTH,month-1)
		startCalendar.clearTime()
		log.debug('startCalendar: '+startCalendar.time)
		
		endCalendar.set(Calendar.YEAR,year)
		endCalendar.set(Calendar.MONTH,month-1)
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
		endCalendar.set(Calendar.DAY_OF_MONTH,startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		log.debug('endCalendar: '+endCalendar.time)
		
		criteria = Contract.createCriteria()		
		previousContracts = criteria.list {
			or{
				// all contracts that were started during the month and not over during the month
				and {
					isNotNull('endDate')
					le('startDate',startCalendar.time)
					ge('endDate',startCalendar.time)
					eq('employee',employee)
				}
				
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
				
				//contract started during the month
				and {
					isNotNull('endDate')
					ge('startDate',startCalendar.time)
					le('startDate',endCalendar.time)
					eq('employee',employee)
				}
			}
			order('startDate','desc')
		}
		
		monthTheoritical = 0
		
		def montlyTotalNumberOfDays = openDaysBetweenDates(startCalendar.time,endCalendar.time)
		
		
		for (Contract currentContract : previousContracts){
			log.debug("currentContract: "+currentContract)
			weeklyContractTime = currentContract.weeklyLength
			
			if (weeklyContractTime == 0){			
				monthTheoritical += 0
			}else{
				startDate = currentContract.startDate
				
				//the contract was not started during the current month: we will take first day of the month as starting point.
				if (startDate < startCalendar.time){
					startDate = startCalendar.time
				}
				
				//the contract did not end during the month: we will take the last day of the month as end point.
				endDate = currentContract.endDate
				if (endDate > endCalendar.time){
					endDate = endCalendar.time
				}
				
				
				// if the contract has no end date, set the end the end date to the end of the month.
				if (currentContract.endDate ==  null){
					def endContractCalendar = Calendar.instance
					endContractCalendar.time = startDate
					endContractCalendar.set(Calendar.HOUR_OF_DAY,23)
					endContractCalendar.set(Calendar.MINUTE,59)
					endContractCalendar.set(Calendar.SECOND,59)
					endContractCalendar.set(Calendar.DAY_OF_MONTH,endContractCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
					endDate = endContractCalendar.time				
				}
				log.debug('startDate: '+startDate)
				log.debug('endDate: '+endDate)
				
				def absenceMap = getAbsencesBetweenDates( employee, startDate, endDate)
				def paterniteSunday = absenceMap.get("patSundayCount")
				
				//initialize day counters:
				realOpenDays      = openDaysBetweenDates(startDate,endDate)
				totalNumberOfDays = openDaysBetweenDates(startDate,endDate)
				
				// special case: arrival month
				if ((employee.arrivalDate.getAt(Calendar.MONTH) + 1) == month &&  (employee.arrivalDate.getAt(Calendar.YEAR)) == year){
					// we need to count OPEN DAYS between dates and not consecutive days
					realOpenDays = openDaysBetweenDates(employee.arrivalDate,endDate)
				}
	
				// special case: employee has not yet arrived in the company
				if (employee.arrivalDate > endCalendar.time ){
					isOut = true
				}
				
				//special case: departure month
				
				log.debug('currentStatus.date: '+currentStatus.date)
				log.debug('endCalendar.time: '+endCalendar.time)
				log.debug('currentStatus.type: '+currentStatus.type)
				
				if (currentStatus.date != null && currentStatus.date <= endCalendar.time && currentStatus.type != StatusType.ACTIF){
					if (currentStatus.date.getAt(Calendar.MONTH) == endCalendar.get(Calendar.MONTH) && currentStatus.date.getAt(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) && currentStatus.date.getAt(Calendar.DAY_OF_MONTH) != 1){
					log.debug('departure month. recomputing open days')
						Calendar exitCalendar = Calendar.instance
						exitCalendar.time = currentStatus.date
						//exitCalendar.roll(Calendar.DAY_OF_YEAR, -1)
						realOpenDays = openDaysBetweenDates(startCalendar.time,exitCalendar.time)
						totalNumberOfDays = openDaysBetweenDates(startCalendar.time,exitCalendar.time)
					}else{
						realOpenDays = 0
						isOut = true
					}	
				}

				//special case: changing contract during the month
				
				
				log.debug('realOpenDays: '+realOpenDays)
				log.debug('totalNumberOfDays: '+totalNumberOfDays)
				
				log.debug('realOpenDays*weeklyContractTime/Employee.WeekOpenedDays: '+realOpenDays*weeklyContractTime/Employee.WeekOpenedDays)
				if (totalNumberOfDays != 0){
					log.debug('(Employee.Pentecote)*((realOpenDays - absenceMap.get(AbsenceType.CSS))/totalNumberOfDays)*(weeklyContractTime/Employee.legalWeekTime): '+(Employee.Pentecote)*((realOpenDays - absenceMap.get(AbsenceType.CSS))/totalNumberOfDays)*(weeklyContractTime/Employee.legalWeekTime))
				}
				log.debug('(weeklyContractTime/Employee.WeekOpenedDays)*(absenceMap.get(AbsenceType.MALADIE)+absenceMap.get(AbsenceType.MATERNITE)+absenceMap.get(AbsenceType.VACANCE)+absenceMap.get(AbsenceType.CHOMAGE)+absenceMap.get(AbsenceType.GARDE_ENFANT)+absenceMap.get(AbsenceType.CSS)+absenceMap.get(AbsenceType.EXCEPTIONNEL)+absenceMap.get(AbsenceType.DIF)+absenceMap.get(AbsenceType.DON)): '+(weeklyContractTime/Employee.WeekOpenedDays)*(absenceMap.get(AbsenceType.MALADIE)+absenceMap.get(AbsenceType.VACANCE)+absenceMap.get(AbsenceType.GARDE_ENFANT)+absenceMap.get(AbsenceType.CSS)+absenceMap.get(AbsenceType.EXCEPTIONNEL)+absenceMap.get(AbsenceType.DIF)+absenceMap.get(AbsenceType.DON)))
	  			log.debug('(35/7)*absenceMap.get(AbsenceType.PATERNITE): '+(35/7)*absenceMap.get(AbsenceType.PATERNITE))
				log.debug('(weeklyContractTime/Employee.WeekOpenedDays)*paterniteSunday: '+(weeklyContractTime/Employee.WeekOpenedDays)*paterniteSunday)
				log.debug('absenceMap.get(AbsenceType.GROSSESSE)): '+absenceMap.get(AbsenceType.GROSSESSE))
			
				if (isOut){
					monthTheoritical += 0
				}else{
					
					def tem = totalNumberOfDays!= 0 ? (Employee.Pentecote)*((realOpenDays - absenceMap.get(AbsenceType.CSS) - absenceMap.get(AbsenceType.INJUSTIFIE))/montlyTotalNumberOfDays)*(weeklyContractTime/Employee.legalWeekTime) : 0	
					
					def A = realOpenDays*weeklyContractTime/Employee.WeekOpenedDays
					def B = tem
					def C = -(weeklyContractTime/Employee.WeekOpenedDays)*(absenceMap.get(AbsenceType.MALADIE)+absenceMap.get(AbsenceType.MATERNITE)+absenceMap.get(AbsenceType.CHOMAGE)+absenceMap.get(AbsenceType.VACANCE)+absenceMap.get(AbsenceType.MISE_A_PIED)+absenceMap.get(AbsenceType.GARDE_ENFANT)+absenceMap.get(AbsenceType.CSS)+absenceMap.get(AbsenceType.INJUSTIFIE)+absenceMap.get(AbsenceType.EXCEPTIONNEL)+absenceMap.get(AbsenceType.PARENTAL)+absenceMap.get(AbsenceType.DIF)+absenceMap.get(AbsenceType.DON))
					def D = - (35/7)*absenceMap.get(AbsenceType.PATERNITE)		
					def E = (weeklyContractTime/Employee.WeekOpenedDays)*paterniteSunday	
					
					
					monthTheoritical += (
						3600*(
								realOpenDays*weeklyContractTime/Employee.WeekOpenedDays
								+ tem
								-(weeklyContractTime/Employee.WeekOpenedDays)*(absenceMap.get(AbsenceType.MALADIE)+absenceMap.get(AbsenceType.MATERNITE)+absenceMap.get(AbsenceType.VACANCE)+absenceMap.get(AbsenceType.CHOMAGE)+absenceMap.get(AbsenceType.GARDE_ENFANT)+absenceMap.get(AbsenceType.CSS)+absenceMap.get(AbsenceType.MISE_A_PIED)+absenceMap.get(AbsenceType.INJUSTIFIE)+absenceMap.get(AbsenceType.EXCEPTIONNEL)+absenceMap.get(AbsenceType.PARENTAL)+absenceMap.get(AbsenceType.DIF)+absenceMap.get(AbsenceType.DON))
								- (35/7)*absenceMap.get(AbsenceType.PATERNITE)
								+ (weeklyContractTime/Employee.WeekOpenedDays)*paterniteSunday			
							)
							- absenceMap.get(AbsenceType.GROSSESSE)) as int			
				}
			}
			log.debug('monthTheoritical with month: '+month+' and year: '+year+' is: '+monthTheoritical)		
		}
		return monthTheoritical
	}
	
	def getDailySickLeaveData(def siteId, Date currentDate,AbsenceType absenceType){
		log.error("getDailySickLeaveData called")
		def criteria
		def site
		def employeeInstanceList
		def calendar = Calendar.instance
		calendar.time = currentDate
		def localisationMap = [:]
		def functionList = Function.list([sort: "ranking", order: "asc"])
		def serviceList = Service.list([sort: "name", order: "asc"])
		def employeeSubList
		def sickEmployeeMap = [:]

		if (siteId != null && !siteId.equals("")){
			site = Site.get(siteId)
			employeeInstanceList = []
			for (Function function:functionList){
				for (Service service:serviceList){
					 criteria = Employee.createCriteria()
					 

						 employeeSubList = criteria.list{
							 or{
								 and {
									 eq('function',function)
									 eq('service',service)
									 eq('site',site)
								 }
								 order('lastName','asc')
							 }
						 }
						 
					 
					 employeeInstanceList.addAll(employeeSubList)
				}
			}
	   }else{
		   employeeInstanceList = []
		   for (Service service:serviceList){
			   for (Function function:functionList){
				   employeeInstanceList.addAll(Employee.findAllByFunctionAndService(function,service,[sort: "lastName", order: "asc"]))
			   }
		   }
	   }
	   
	   for (Employee employee:employeeInstanceList){
		   sickEmployeeMap.put(employee,false)
		   criteria = Absence.createCriteria()
		   def absence = criteria.get{
			   and {
				   eq('day',calendar.get(Calendar.DAY_OF_MONTH))
				   eq('month',calendar.get(Calendar.MONTH)+1)
				   eq('year',calendar.get(Calendar.YEAR))
				   eq('employee',employee) 
				   eq('type',absenceType)
			   }
		   }
		   if (absence != null){
			   sickEmployeeMap.put(employee,true)
		   }
	   }
	   
	   return [
		   sickEmployeeMap:sickEmployeeMap,
		   site:site,
		   currentDate:currentDate
		   ]
		
	}
	
	
	def getDailyInAndOutsData(def siteId, Date currentDate){
		def absenceType = AbsenceType.MALADIE
		def maxSize = 0
		def functionList = Function.list([sort: "ranking", order: "asc"])
		def serviceList = Service.list([sort: "name", order: "asc"])
		def employeeInstanceList
		def criteria
		def dailyTotal
		def inAndOutList
		def elapsedSeconds
		def site
		def dailyMap = [:]
		def dailySupMap = [:]
		def dailyInAndOutMap = [:]
		def inAndOutsForEmployee = []
		def inAndOutsForEmployeeMap = [:]
		def employeeSubList
		def localisationMap = [:]
		def sickEmployeeList = []
		def employeeSiteList = []
		def calendar = Calendar.instance
		calendar.time = currentDate
			
		if (siteId != null && !siteId.equals("")){
			 site = Site.get(siteId)
			 employeeSiteList = Employee.findAllBySite(site)
			 employeeInstanceList = []
			 def foreignEmployees = []
			 def foreignEmployeesIds = []
			 criteria = DailyTotal.createCriteria()
			 
			 def dailyTotals = criteria.list{
				 and {
					 eq('day',calendar.get(Calendar.DAY_OF_MONTH))
					 eq('month',calendar.get(Calendar.MONTH)+1)
					 eq('year',calendar.get(Calendar.YEAR))
					 eq('site',site)
				 }
			 }
			 for (DailyTotal dailyTotalIter : dailyTotals){
				 if (dailyTotalIter.site != null && dailyTotalIter.site != dailyTotalIter.employee.site){
					 foreignEmployees.add(dailyTotalIter.employee)
					 foreignEmployeesIds.add(dailyTotalIter.employee.id)
					 localisationMap.put(dailyTotalIter.employee,site)
				 }
			 }
			 
			 for (Function function:functionList){
				 for (Service service:serviceList){
					  criteria = Employee.createCriteria()
					  
					  if (foreignEmployeesIds != null && foreignEmployeesIds.size() > 0){
						  employeeSubList = criteria.list{
							  or{
								  
								  and {
									  eq('function',function)
									  eq('service',service)
									  eq('site',site)
								  }
								  
								  and {
									  eq('function',function)
									  eq('service',service)
									  'in'("id",foreignEmployeesIds)
								  }
								  order('lastName','asc')
							  }
						  }
					  }
					  else{
						  employeeSubList = criteria.list{
							  or{
								  and {
									  eq('function',function)
									  eq('service',service)
									  eq('site',site)
								  }
								  order('lastName','asc')
							  }
						  }
						  
					  }
					  employeeInstanceList.addAll(employeeSubList)
				 }
			 }
		}else{
			employeeSiteList = Employee.findAll()
			employeeInstanceList = []
			for (Service service:serviceList){
				for (Function function:functionList){
					employeeInstanceList.addAll(Employee.findAllByFunctionAndService(function,service,[sort: "lastName", order: "asc"]))
				}
			}
		}
		for (Employee employee:employeeInstanceList){
			inAndOutsForEmployee = []
			criteria = DailyTotal.createCriteria()
			dailyTotal = criteria.get{
				and {
					eq('employee',employee)
					eq('week',calendar.get(Calendar.WEEK_OF_YEAR))
					eq('year',calendar.get(Calendar.YEAR))
					eq('day',calendar.get(Calendar.DAY_OF_MONTH))
				}
			}
			
			if (dailyTotal != null && dailyTotal.site != null && dailyTotal.site != site){
				def tmpSite = Site.findByName(dailyTotal.site.name)
				localisationMap.put(employee,tmpSite)
			}else{
				localisationMap.put(employee,site)
			}
			
			
			criteria = InAndOut.createCriteria()
			inAndOutList = criteria.list{
				and {
						eq('employee',employee)
						eq('week',calendar.get(Calendar.WEEK_OF_YEAR))
						eq('day',calendar.get(Calendar.DAY_OF_MONTH))
						eq('month',calendar.get(Calendar.MONTH)+1)
						eq('year',calendar.get(Calendar.YEAR))
						order('time')
					}
			}
			
			maxSize = (inAndOutList != null && inAndOutList.size() > maxSize) ? inAndOutList.size() : maxSize
			
			for (InAndOut inOrOut : inAndOutList){
				inAndOutsForEmployee.add("'"+inOrOut.time.format('yyyy-MM-dd HH:mm:SS')+"'")
			}
		
			if (inAndOutList.size() < maxSize){
				def difference = maxSize - inAndOutList.size()
				for (int j = 0 ;j < difference ; j++){
					inAndOutsForEmployee.add(null)
				}
			}
			dailyInAndOutMap.put(employee, inAndOutList)
			elapsedSeconds = dailyTotal != null ? (getDailyTotal(dailyTotal)).get('elapsedSeconds') : 0
	
			if (elapsedSeconds > DailyTotal.maxWorkingTime){
				dailySupMap.put(employee,elapsedSeconds-DailyTotal.maxWorkingTime)
			}else{
				dailySupMap.put(employee,0)
			}
			if (dailyTotal != null && (dailyTotal.site == site || dailyTotal.site == null)){
				dailyMap.put(employee,elapsedSeconds)
			}
			
			inAndOutsForEmployeeMap.put(employee,inAndOutsForEmployee)
		}
		
		
		for (Employee employee:employeeSiteList){
			criteria = Absence.createCriteria()
			def absence = criteria.get{
				and {
					eq('day',calendar.get(Calendar.DAY_OF_MONTH))
					eq('month',calendar.get(Calendar.MONTH)+1)
					eq('year',calendar.get(Calendar.YEAR))
					eq('employee',employee)
					eq('type',absenceType)
				}
			}
			if (absence != null){
				sickEmployeeList.add(employee)
			}
		}
		return [
			dailyMap: dailyMap,
			sickEmployeeList:sickEmployeeList,
			site:site,
			dailySupMap:dailySupMap,
			dailyInAndOutMap:dailyInAndOutMap,
			currentDate:currentDate,
			localisationMap:localisationMap,
			maxSize:maxSize,
			inAndOutsForEmployeeMap:inAndOutsForEmployeeMap
			]
	}
	
	def getPointageData(Employee employee){
		def entranceStatus
		def mapByDay=[:]
		def totalByDay=[:]
		def dailyCriteria
		def elapsedSeconds=0
		def timing 
		def humanTime 
		def dailySupp
		def calendar = Calendar.instance
		def inAndOutsCriteria = InAndOut.createCriteria()
		def tmpCalendar = Calendar.instance
		def inAndOuts
		tmpCalendar.set(Calendar.DAY_OF_YEAR,tmpCalendar.get(Calendar.DAY_OF_YEAR)-4)
		
		inAndOuts = inAndOutsCriteria.list {
			and {
				eq('employee',employee)
				eq('year',calendar.get(Calendar.YEAR))
				eq('month',calendar.get(Calendar.MONTH)+1)
				eq('day',calendar.get(Calendar.DAY_OF_MONTH))
				order('time','asc')
			}
		}
		 
		// iterate over tmpCalendar
		while(tmpCalendar.get(Calendar.DAY_OF_YEAR) < (calendar.get(Calendar.DAY_OF_YEAR) -1)){
			tmpCalendar.roll(Calendar.DAY_OF_YEAR, 1)
			inAndOutsCriteria = InAndOut.createCriteria()
			def lastInAndOuts = inAndOutsCriteria.list {
				and {
					eq('employee',employee)
					eq('year',calendar.get(Calendar.YEAR))
					eq('month',tmpCalendar.get(Calendar.MONTH)+1)
					eq('day',tmpCalendar.get(Calendar.DAY_OF_MONTH))
					order('time','asc')
				}
			}
			mapByDay.put(tmpCalendar.time,lastInAndOuts)
			elapsedSeconds = getDailyTotal(lastInAndOuts)
			totalByDay.put(tmpCalendar.time, getTimeAsText(computeHumanTime(elapsedSeconds),false))
		}
		
		dailyCriteria = DailyTotal.createCriteria()
		DailyTotal dailyTotal = dailyCriteria.get {
			and {
				eq('employee',employee)
				eq('year',calendar.get(Calendar.YEAR))
				eq('month',calendar.get(Calendar.MONTH)+1)
				eq('day',calendar.get(Calendar.DAY_OF_MONTH))
			}
		}
		if (dailyTotal != null){
			timing = getDailyTotal(dailyTotal)
			humanTime = getTimeAsText(computeHumanTime(timing.get("elapsedSeconds")),false)
			dailySupp = getTimeAsText(computeHumanTime(Math.max(timing.get("elapsedSeconds")-DailyTotal.maxWorkingTime,0)),false)
		}else{
			humanTime = getTimeAsText(computeHumanTime(0),false)
			dailySupp = getTimeAsText(computeHumanTime(0),false)
		}
		if (inAndOuts!=null){
			def max = inAndOuts.size()
			if (max > 0){
			def  lastEvent = inAndOuts.get(max - 1)
			entranceStatus = lastEvent.type.equals("S") ? false : true
			}else{
				entranceStatus=false	
			}
		}else{
			entranceStatus=false
		}

		return [
			inAndOuts:inAndOuts,
			dailyTotal:dailyTotal,
			humanTime:humanTime,
			dailySupp:dailySupp,
			mapByDay:mapByDay,
			entranceStatus:entranceStatus,
			totalByDay:totalByDay,
			period:tmpCalendar.time
		]
	}
	
	
	def getSiteData(Site site,Period period){
		def annualReportMap = [:]
		def siteAnnualEmployeeWorkingDays = 0
		def siteAnnualTheoritical = 0
		def siteAnnualTotal = 0
		def siteAnnualHoliday = 0
		def siteAnnualGarde_enfant = 0
		def siteAnnualChomage = 0
		def siteRemainingCA = 0
		def siteAnnualRTT = 0
		def siteAnnualCSS = 0
		def siteAnnualINJUSTIFIE = 0
		def siteAnnualMISE_A_PIED = 0
		def siteAnnualSickness = 0
		def siteAnnualMaternite = 0
		def siteAnnualExceptionnel = 0
		def siteAnnualPaternite = 0	
		def siteAnnualParental = 0	
		def siteAnnualDIF = 0
		def siteAnnualDON = 0
		def siteAnnualPayableSupTime = 0
		def siteAnnualTheoriticalIncludingExtra = 0
		def siteAnnualSupTimeAboveTheoritical = 0
		def siteAnnualGlobalSupTimeToPay = 0
		def employeeList = (site != null) ? Employee.findAllBySite(site) :Employee.findAll("from Employee")

		for (Employee employee:employeeList){
			log.error("current employee: "+employee)
			annualReportMap.put(employee,getAnnualReportData(period.year, employee))		
			siteAnnualEmployeeWorkingDays += ((annualReportMap.get(employee)).get('annualEmployeeWorkingDays'))
			siteAnnualTheoritical += getTimeFromText((annualReportMap.get(employee)).get('annualTheoritical'),false)
			siteAnnualTotal += getTimeFromText((annualReportMap.get(employee)).get('annualTotal'),false)
			siteAnnualHoliday += ((annualReportMap.get(employee)).get('annualHoliday'))
			siteAnnualGarde_enfant += ((annualReportMap.get(employee)).get('annualGarde_enfant'))
			siteAnnualChomage += ((annualReportMap.get(employee)).get('annualChomage'))
			siteRemainingCA += ((annualReportMap.get(employee)).get('remainingCA'))
			siteAnnualRTT += ((annualReportMap.get(employee)).get('annualRTT'))
			siteAnnualCSS += ((annualReportMap.get(employee)).get('annualCSS'))
			siteAnnualINJUSTIFIE += ((annualReportMap.get(employee)).get('annualINJUSTIFIE'))
			siteAnnualMISE_A_PIED += ((annualReportMap.get(employee)).get('annualMISE_A_PIED'))
			siteAnnualSickness += ((annualReportMap.get(employee)).get('annualSickness'))
			siteAnnualMaternite += ((annualReportMap.get(employee)).get('annualMaternite'))		
			siteAnnualDIF += ((annualReportMap.get(employee)).get('annualDIF'))
			siteAnnualDON += ((annualReportMap.get(employee)).get('annualDON'))
			siteAnnualExceptionnel += ((annualReportMap.get(employee)).get('annualExceptionnel'))
			siteAnnualPaternite += ((annualReportMap.get(employee)).get('annualPaternite'))
			siteAnnualParental += ((annualReportMap.get(employee)).get('annualParental'))
			siteAnnualPayableSupTime += getTimeFromText((annualReportMap.get(employee)).get('annualPayableSupTime'),false)
			siteAnnualTheoriticalIncludingExtra += getTimeFromText((annualReportMap.get(employee)).get('annualTheoriticalIncludingExtra'),false)
			siteAnnualSupTimeAboveTheoritical += (annualReportMap.get(employee)).get('annualSupTimeAboveTheoritical')
			siteAnnualGlobalSupTimeToPay += (annualReportMap.get(employee)).get('annualGlobalSupTimeToPay')
		}
		
		return [
			annualReportMap:annualReportMap,
			siteAnnualEmployeeWorkingDays:siteAnnualEmployeeWorkingDays,
			siteAnnualTheoritical:getTimeAsText(computeHumanTime(siteAnnualTheoritical),false),
			siteAnnualTotal:getTimeAsText(computeHumanTime(siteAnnualTotal),false),
			siteAnnualHoliday:siteAnnualHoliday,
			siteAnnualGarde_enfant:siteAnnualGarde_enfant,
			siteAnnualChomage:siteAnnualChomage,
			siteRemainingCA:siteRemainingCA,
			siteAnnualRTT:siteAnnualRTT,
			siteAnnualCSS:siteAnnualCSS,
			siteAnnualINJUSTIFIE:siteAnnualINJUSTIFIE,
			siteAnnualMISE_A_PIED:siteAnnualMISE_A_PIED,
			siteAnnualSickness:siteAnnualSickness,
			siteAnnualMaternite:siteAnnualMaternite,
			siteAnnualDIF:siteAnnualDIF,
			siteAnnualDON:siteAnnualDON,
			siteAnnualExceptionnel:siteAnnualExceptionnel,
			siteAnnualPaternite:siteAnnualPaternite,	
			siteAnnualParental:siteAnnualParental,
			siteAnnualPayableSupTime:getTimeAsText(computeHumanTime(siteAnnualPayableSupTime),false),
			siteAnnualTheoriticalIncludingExtra:getTimeAsText(computeHumanTime(siteAnnualTheoriticalIncludingExtra),false),
			siteAnnualSupTimeAboveTheoritical:getTimeAsText(computeHumanTime(siteAnnualSupTimeAboveTheoritical),false),
			siteAnnualGlobalSupTimeToPay:getTimeAsText(computeHumanTime(siteAnnualGlobalSupTimeToPay),false),
			employeeList:employeeList		
			]
	}
	
	def computeWeeklyTotalsMultiThread(Employee employee, int month, int year){
		def startDate = new Date()
		def endDate
		def executionTime
		log.debug('computeWeeklyTotals called for employee: '+employee+', month: '+month+',year: '+year)
		//variables
		def weekName="semaine "
		def criteria
		def dailySeconds
		def timeBefore7 = 0
		def timeAfter20 = 0
		def timeOffHours = 0
		def yearlyBefore7 = 0
		def yearlyAfter21 = 0
		def yearlyTimeOffHours = 0
		def monthlyTotalTime = 0
		def monthlySupTime = 0
		def dailyTotalId = 0
		def weeklySupTime = 0
		def currentWeek = 0
		def weeklyTotal = 0

		//maps
		def mapByDay = [:]
		def weeklyTotalTime = [:]
		def weeklySuppTotalTime = [:]
		def weeklySupTotalTimeByEmployee = [:]
		def weeklyTotalTimeByEmployee = [:]
		def dailyTotalMap = [:]
		def dailySupTotalMap = [:]
		def dailyBankHolidayMap = [:]
		def holidayMap = [:]
		def weeklyAggregate = [:]
		
		//calendars
		def calendar = Calendar.instance
		calendar.set(Calendar.MONTH,month - 1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		calendar.set(Calendar.DATE,1)
		def calendarLoop = calendar

		calendarLoop.getTime().clearTime()
		
		def lastWeekParam = utilService.getLastWeekOfMonth(month, year)
		def isSunday = lastWeekParam.get(1)
		def dailyTotalCriteria = DailyTotal.createCriteria()
		def dailyTotalList = dailyTotalCriteria.list {
			and {
				eq('employee',employee)
				//eq('day',calendarLoop.get(Calendar.DAY_OF_MONTH))
				eq('month',month)
				eq('year',year)
			}
		}
		def montlyTotalCriteria = MonthlyTotal.createCriteria()
		def monthlyTotal = montlyTotalCriteria.get {
			and {
				eq('employee',employee)
				eq('year',calendarLoop.get(Calendar.YEAR))
				eq('month',calendarLoop.get(Calendar.MONTH)+1)
			}
		}
		if (monthlyTotal != null){
			monthlyTotal.elapsedSeconds=monthlyTotalTime
			monthlyTotal.timeBefore7=timeBefore7
			monthlyTotal.timeAfter20=timeAfter20
			monthlyTotal.supplementarySeconds=monthlySupTime
			monthlyTotal.save(flush:true,failOnError:true)
		}	
		
		//here, we can proceed to the creation, or update of the SupplementaryTime object
		criteria = SupplementaryTime.createCriteria()
		Period period = (month < 6) ? Period.findByYear(year-1) : Period.findByYear(year)

		def supTime = criteria.get {
			and {
				eq('employee',employee)
				eq('period',period)
				eq('month',month)
			}
			maxResults(1)
		}
		if (supTime == null){
			supTime = new SupplementaryTime( employee, period,  month,monthlySupTime)
		}else{
			supTime.value = monthlySupTime
		}
		supTime.save(flush: true)
		endDate = new Date()
		use (TimeCategory){executionTime=endDate-startDate}

		return [
			timeBefore7:timeBefore7,
			timeAfter20:timeAfter20,
			timeOffHours:timeOffHours,
			monthlyTotalTime:monthlyTotalTime,
			monthlySupTime:monthlySupTime,
			mapByDay:mapByDay,
			weeklyTotalTime:weeklyTotalTime,
			weeklySuppTotalTime:weeklySuppTotalTime,
			weeklySupTotalTimeByEmployee:weeklySupTotalTimeByEmployee,
			weeklyTotalTimeByEmployee:weeklyTotalTimeByEmployee,
			dailyTotalMap:dailyTotalMap,
			dailySupTotalMap:dailySupTotalMap,
			dailyBankHolidayMap:dailyBankHolidayMap,
			holidayMap:holidayMap,
			weeklyAggregate:weeklyAggregate,
			monthlySupTime:monthlySupTime
		]
	}
	
	def computeWeeklyTotals(Employee employee, int month, int year, boolean entityUpdate){
			def startDate = new Date()
			def endDate
			def executionTime
			log.debug('computeWeeklyTotals called for employee: '+employee+', month: '+month+',year: '+year+' and entityUpdate: '+entityUpdate)
			//variables
			def weekName="semaine "
			def criteria
			def dailySeconds
			def timeBefore7 = 0
			def timeAfter20 = 0
			def timeOffHours = 0
			def yearlyBefore7 = 0
			def yearlyAfter21 = 0
			def yearlyTimeOffHours = 0 
			def monthlyTotalTime = 0
			def monthlySupTime = 0
			def dailyTotalId = 0
			def weeklySupTime = 0
			def currentWeek = 0
			def weeklyTotal = 0
	
			//maps
			def mapByDay = [:]
			def mileageMapByDay = [:]
			def weeklyTotalTime = [:]
			def weeklySuppTotalTime = [:]
			def weeklySupTotalTimeByEmployee = [:]
			def weeklyTotalTimeByEmployee = [:]
			def dailyTotalMap = [:]
			def dailySupTotalMap = [:]
			def dailyTotalTextMap = [:]
			def dailySupTotalTextMap = [:]
			def dailyBankHolidayMap = [:]
			def holidayMap = [:]
			def weeklyAggregate = [:]
			
			//calendars
			def calendar = Calendar.instance
			calendar.set(Calendar.MONTH,month - 1)
			calendar.set(Calendar.YEAR,year)
			calendar.set(Calendar.HOUR_OF_DAY,23)
			calendar.set(Calendar.MINUTE,59)
			calendar.set(Calendar.SECOND,59)
			calendar.set(Calendar.DATE,1)
			def calendarLoop = calendar
	
			calendarLoop.getTime().clearTime()
			
			def lastWeekParam = utilService.getLastWeekOfMonth(month, year)
			def isSunday = lastWeekParam.get(1)
			
			while(calendarLoop.get(Calendar.DAY_OF_MONTH) <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				// élimine les dimanches du rapport
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
				
				criteria = Mileage.createCriteria()
				def dailyMileage = criteria.get {
					and {
						eq('employee',employee)
						eq('day',calendarLoop.get(Calendar.DAY_OF_MONTH))
						eq('month',month)
						eq('year',year)
					}
				}
			
				
				// permet de récupérer le total hebdo
				if (dailyTotal != null && dailyTotal != dailyTotalId){
					def timing = getDailyTotal(dailyTotal)
					dailySeconds = timing.get("elapsedSeconds")
					timeBefore7 += timing.get("timeBefore7")
					timeAfter20 +=  timing.get("timeAfter20")
					timeOffHours = timeOffHours + timing.get("timeBefore7") + timing.get("timeAfter20")
					monthlyTotalTime += dailySeconds
								
					if (currentWeek == calendarLoop.get(Calendar.WEEK_OF_YEAR)){
						weeklyTotal += dailySeconds
					}else{
						weeklyTotal = dailySeconds
					}
					
					weeklyTotalTime.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR), getTimeAsText(computeHumanTime(weeklyTotal),false))
					def weeklyTotalCriteria = WeeklyTotal.createCriteria()
					def weeklyTotalInstance = weeklyTotalCriteria.get {
						and {
							eq('employee',employee)
							eq('year',calendarLoop.get(Calendar.YEAR))
							eq('month',calendarLoop.get(Calendar.MONTH)+1)
							eq('week',calendarLoop.get(Calendar.WEEK_OF_YEAR))
							
						}
					}
					if (weeklyTotalInstance != null && entityUpdate){
						weeklyTotalInstance.elapsedSeconds = weeklyTotal
						try{
							weeklyTotalInstance.save(flush:true)
						}
						catch(org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException e) {
							log.debug('concurrent access error: '+e.message)
						}
					}
					
					if (!isSunday && calendarLoop.get(Calendar.WEEK_OF_YEAR) == lastWeekParam.get(0) ){
						weeklySupTime = 0
					}else{
						weeklySupTime = computeSupplementaryTime(employee,calendarLoop.get(Calendar.WEEK_OF_YEAR), calendarLoop.get(Calendar.YEAR))
					}
					weeklySuppTotalTime.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR),getTimeAsText(computeHumanTime(Math.round(weeklySupTime)),false))
					if (currentWeek != calendarLoop.get(Calendar.WEEK_OF_YEAR)){
						monthlySupTime += weeklySupTime
						currentWeek = calendarLoop.get(Calendar.WEEK_OF_YEAR)
					}
					weeklySupTotalTimeByEmployee.put(employee,weeklySuppTotalTime)
					weeklyTotalTimeByEmployee.put(employee,weeklyTotalTime)
					dailyTotalId=dailyTotal.id
				}
				// daily total is null. Still, we need to check if supplementary time exists within the week
				if (dailyTotal == null && calendarLoop.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
					if (calendarLoop.get(Calendar.WEEK_OF_YEAR) == lastWeekParam.get(0) ){
						weeklySupTime = 0
					}else{
						weeklySupTime = computeSupplementaryTime(employee,calendarLoop.get(Calendar.WEEK_OF_YEAR), calendarLoop.get(Calendar.YEAR))
					}
					weeklySuppTotalTime.put(weekName+calendarLoop.get(Calendar.WEEK_OF_YEAR),getTimeAsText(computeHumanTime(Math.round(weeklySupTime)),false))
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
				if 	(entriesByDay.size() > 0){
					if (dailyTotal != null){
						dailySeconds = getDailyTotal(dailyTotal).get("elapsedSeconds")
						dailyTotalMap.put(tmpDate, dailyTotal)
						dailySupTotalMap.put(tmpDate, Math.max(dailySeconds-DailyTotal.maxWorkingTime,0))
						dailyTotalTextMap.put(tmpDate, getTimeAsText(computeHumanTime(dailySeconds),false))
						dailySupTotalTextMap.put(tmpDate, getTimeAsText(computeHumanTime(Math.max(dailySeconds-DailyTotal.maxWorkingTime,0)),false))
	
					}else {
						dailyTotalMap.put(tmpDate,null)// 0)
						dailySupTotalMap.put(tmpDate, 0)
						dailyTotalTextMap.put(tmpDate, getTimeAsText(computeHumanTime(0),false))
						dailySupTotalTextMap.put(tmpDate, getTimeAsText(computeHumanTime(0),false))
	
					}
					mapByDay.put(tmpDate, entriesByDay)
				}else{
					if (dailyTotal != null){
						dailyTotalMap.put(tmpDate, dailyTotal)
					}else{
						dailyTotalMap.put(tmpDate, null)
					}
					dailySupTotalMap.put(tmpDate, 0)
					dailyTotalTextMap.put(tmpDate, getTimeAsText(computeHumanTime(0),false))
					dailySupTotalTextMap.put(tmpDate, getTimeAsText(computeHumanTime(0),false))
					mapByDay.put(tmpDate, null)
				}
				mileageMapByDay.put(tmpDate, dailyMileage)
				
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
			
			def montlyTotalCriteria = MonthlyTotal.createCriteria()
			def monthlyTotal = montlyTotalCriteria.get {
				and {
					eq('employee',employee)
					eq('year',calendarLoop.get(Calendar.YEAR))
					eq('month',calendarLoop.get(Calendar.MONTH)+1)
				}
			}
			if (monthlyTotal != null && entityUpdate){
				log.debug('new value: '+monthlyTotalTime)
				monthlyTotal.elapsedSeconds=monthlyTotalTime
				monthlyTotal.timeBefore7=timeBefore7
				monthlyTotal.timeAfter20=timeAfter20
				monthlyTotal.supplementarySeconds=monthlySupTime
				try {
					monthlyTotal.save(flush:true,failOnError:true)

				   }
				   catch(org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException e) {
				   	log.debug('error of concurrent acces: '+e.message)
				   }
			}
			
			//here, we can proceed to the creation, or update of the SupplementaryTime object
			criteria = SupplementaryTime.createCriteria()	
			Period period = (month < 6) ? Period.findByYear(year - 1) : Period.findByYear(year)
	
			def supTime = criteria.get {
				and {
					eq('employee',employee)
					eq('period',period)
					eq('month',month)
				}
				maxResults(1)
			}
			
			if (entityUpdate){
				if (supTime == null){
					supTime = new SupplementaryTime( employee, period,  month,monthlySupTime)
				}else{
					supTime.value = monthlySupTime
					if (supTime.amountPaid == null){
						supTime.amountPaid = 0
					}
				}			
				
				try {
					supTime.save(flush: true)
				   }
				   catch(org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException e) {
					   log.debug('error of concurrent acces: '+e.message)
				   }
			}
			endDate = new Date()
			use (TimeCategory){executionTime = endDate - startDate}
			log.debug('computeWeeklyTotals executed in '+executionTime+' for employee: '+employee.lastName)
	
			return [
				timeBefore7:timeBefore7,
				timeAfter20:timeAfter20,
				timeOffHours:timeOffHours,
				monthlyTotalTime:monthlyTotalTime,
				monthlySupTime:monthlySupTime,
				mapByDay:mapByDay,
				weeklyTotalTime:weeklyTotalTime,
				weeklySuppTotalTime:weeklySuppTotalTime,
				weeklySupTotalTimeByEmployee:weeklySupTotalTimeByEmployee,
				weeklyTotalTimeByEmployee:weeklyTotalTimeByEmployee,
				dailyTotalMap:dailyTotalMap,
				dailySupTotalMap:dailySupTotalMap,
				dailyTotalTextMap:dailyTotalTextMap,
				dailySupTotalTextMap:dailySupTotalTextMap,
				dailyBankHolidayMap:dailyBankHolidayMap,
				holidayMap:holidayMap,
				weeklyAggregate:weeklyAggregate,
				monthlySupTime:monthlySupTime,
				mileageMapByDay:mileageMapByDay
			]	
	}
	
	def computeOffTimeTotals(Employee employee, int month, int year){
		//variables
		def criteria
		def timeBefore7 = 0
		def timeAfter20 = 0
		def timeOffHours = 0
		def currentWeek = 0
		def dailyTotalId = 0
		
		//calendars
		def calendar = Calendar.instance
		calendar.set(Calendar.MONTH,month - 1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		calendar.set(Calendar.DATE,1)
		def calendarLoop = calendar

		calendarLoop.getTime().clearTime()
		
		def lastWeekParam = utilService.getLastWeekOfMonth(month, year)
		def isSunday = lastWeekParam.get(1)
		
		while(calendarLoop.get(Calendar.DAY_OF_MONTH) <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			// élimine les dimanches du rapport
		
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
			// permet de récupérer le total hebdo
			if (dailyTotal != null && dailyTotal != dailyTotalId){
				def timing = getDailyTotal(dailyTotal)
				timeBefore7 += timing.get("timeBefore7")
				timeAfter20 +=  timing.get("timeAfter20")
				timeOffHours = timeOffHours + timing.get("timeBefore7") + timing.get("timeAfter20")
							
				if (currentWeek != calendarLoop.get(Calendar.WEEK_OF_YEAR)){
					currentWeek = calendarLoop.get(Calendar.WEEK_OF_YEAR)
				}

				dailyTotalId=dailyTotal.id
			}
			if (calendarLoop.get(Calendar.DAY_OF_MONTH)==calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendarLoop.roll(Calendar.DAY_OF_MONTH, 1)
		}
						
			return [
			timeBefore7:timeBefore7,
			timeAfter20:timeAfter20,
			timeOffHours:timeOffHours

		]
	}
	
	
	
	def getYearSupTime(Employee employee,int year,int month,boolean entityUpdate){
		log.debug('getYearSupTime and year: '+year+' and month: '+month)
		def data
		def monthNumber=0
		def yearSupTime = 0
		def yearTimeBefore7 = 0
		def yearTimeAfter20 = 0
		def yearTimeOffHours = 0
		def yearlyCounter = 0
		def monthlySupTime = 0
		def timeBefore7 = 0
		def timeAfter20 = 0
		def timeOffHours= 0
		def yearTheoritical = 0
		def monthTheoritical = 0
		def calendar = Calendar.instance
		calendar.set(Calendar.YEAR,year)
		def bankHolidayList
		def criteria = Absence.createCriteria()

		if (month > 5){
			year = year + 1
			monthNumber = month - 6 + 1
		}else{
			monthNumber = month + 1 + 6
		}

		// set the date end of may
		calendar.set(Calendar.DAY_OF_MONTH,10)
		calendar.set(Calendar.MONTH,month-1)
		log.debug("month: "+calendar.get(Calendar.MONTH))
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		log.debug("last day of the month: "+calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		log.debug("calendar: "+calendar)
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		def maxDate = calendar.time
		log.debug("month: "+calendar.get(Calendar.MONTH))
		log.debug("maxDate: "+maxDate)
		calendar.set(Calendar.YEAR,year-1)
		calendar.set(Calendar.MONTH,5)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.clearTime()
		def minDate = calendar.time

		/* create a loop over months and invoke getMonthTheoritical method */
		// check if employee entered the company or left the company over the period.
		def arrivalDate = employee.arrivalDate
		def exitDate = employee.status == StatusType.TERMINE ? employee.status.date : null

		// the employee arrived after the period started: resetting the minDate
		if (arrivalDate > minDate){
			minDate = arrivalDate
		}

		// the employee left before period's end:
		if ((exitDate != null) && exitDate < maxDate){
			maxDate = exitDate
		}
		
		def calendarIter = Calendar.instance
		calendarIter.time = minDate
		
		// 2 cases: either min date is greater than 1st of the year, then 1 loop. Otherwise, 2 loops.
		if (minDate.getAt(Calendar.YEAR) == maxDate.getAt(Calendar.YEAR)){
			while(calendarIter.get(Calendar.MONTH) <= maxDate.getAt(Calendar.MONTH)){
				log.debug('calendarIter: '+calendarIter.time)
				data = computeWeeklyTotals( employee, calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR),entityUpdate)
				monthTheoritical = getMonthTheoritical(employee,  calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
				yearTheoritical += monthTheoritical
				yearSupTime +=  data.get('monthlySupTime')
				yearTimeBefore7 += data.get('timeBefore7')
				yearTimeAfter20 += data.get('timeAfter20')
				yearTimeOffHours += data.get('timeOffHours')
				if (calendarIter.get(Calendar.MONTH) == maxDate.getAt(Calendar.MONTH)){
					monthlySupTime = data.get('monthlySupTime')
					timeBefore7 = data.get('timeBefore7') != null ? data.get('timeBefore7') : 0
					timeAfter20 = data.get('timeAfter20') != null ? data.get('timeAfter20') : 0
					timeOffHours = data.get('timeOffHours') != null ? data.get('timeOffHours') : 0
					break
				}
				calendarIter.roll(Calendar.MONTH, 1)
			}
		}else{
			while(calendarIter.get(Calendar.MONTH) <= 11){
				log.debug('calendarIter: '+calendarIter.time)
				data = computeWeeklyTotals( employee, calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR),entityUpdate)
				monthTheoritical = getMonthTheoritical(employee,  calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
				yearTheoritical += monthTheoritical
				yearSupTime +=  data.get('monthlySupTime')
				yearTimeBefore7 += data.get('timeBefore7')
				yearTimeAfter20 += data.get('timeAfter20')
				yearTimeOffHours += data.get('timeOffHours')
				monthlySupTime = data.get('monthlySupTime')
				timeBefore7 = data.get('timeBefore7') != null ? data.get('timeBefore7') : 0
				timeAfter20 = data.get('timeAfter20') != null ? data.get('timeAfter20') : 0
				timeOffHours = data.get('timeOffHours') != null ? data.get('timeOffHours') : 0
				if (calendarIter.get(Calendar.MONTH) == 11){
					break
				}
				calendarIter.roll(Calendar.MONTH, 1)
			}
			calendarIter.set(Calendar.MONTH,0)
			calendarIter.set(Calendar.YEAR,(calendarIter.get(Calendar.YEAR)+1))
			
			while(calendarIter.get(Calendar.MONTH) <= maxDate.getAt(Calendar.MONTH)){
				log.debug('calendarIter: '+calendarIter.time)
				data = computeWeeklyTotals( employee, calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR),entityUpdate)
				monthTheoritical = getMonthTheoritical(employee,  calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
				yearTheoritical += monthTheoritical
				yearSupTime +=  data.get('monthlySupTime')
				yearTimeBefore7 += data.get('timeBefore7')
				yearTimeAfter20 += data.get('timeAfter20')
				yearTimeOffHours += data.get('timeOffHours')
				monthlySupTime = data.get('monthlySupTime')
				timeBefore7 = data.get('timeBefore7') != null ? data.get('timeBefore7') : 0
				timeAfter20 = data.get('timeAfter20') != null ? data.get('timeAfter20') : 0
				timeOffHours = data.get('timeOffHours') != null ? data.get('timeOffHours') : 0
				if (calendarIter.get(Calendar.MONTH) == maxDate.getAt(Calendar.MONTH)){
					break
				}
				calendarIter.roll(Calendar.MONTH, 1)
			}
		}

		return [
			monthTheoritical:monthTheoritical as long,
			yearTheoritical:yearTheoritical as long,
			monthlySupTime:monthlySupTime as long,
			timeBefore7:timeBefore7 as long,
			timeAfter20:timeAfter20 as long,
			timeOffHours:timeOffHours as long,
			ajaxYearlySupTime:yearSupTime as long,
			yearTimeBefore7:yearTimeBefore7 as long,
			yearTimeAfter20:yearTimeAfter20 as long,
			yearTimeOffHours:yearTimeOffHours as long
		]	
	}

	def retrieveOffHoursTime(Employee employee,int year,int month){
		def monthNumber=0
		def yearTimeBefore7 = 0
		def yearTimeAfter20 = 0
		def yearTimeOffHours = 0
		def yearlyCounter = 0
		def data
		def timeBefore7 = 0
		def timeAfter20 = 0
		def timeOffHours= 0
		def calendar = Calendar.instance
		calendar.set(Calendar.YEAR,year)
		def bankHolidayList
		def criteria = Absence.createCriteria()

		if (month > 5){
			year = year + 1
			monthNumber = month - 6 + 1
		}else{
			monthNumber = month + 1 + 6
		}

		// set the date end of may
		calendar.set(Calendar.DAY_OF_MONTH,10)
		calendar.set(Calendar.MONTH,month-1)
		log.debug("month: "+calendar.get(Calendar.MONTH))
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		log.debug("last day of the month: "+calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		log.debug("calendar: "+calendar)
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		def maxDate = calendar.time
		log.debug("month: "+calendar.get(Calendar.MONTH))
		log.debug("maxDate: "+maxDate)
		calendar.set(Calendar.YEAR,year-1)
		calendar.set(Calendar.MONTH,5)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.clearTime()
		def minDate = calendar.time
		def monthlyTotal

		/* create a loop over months and invoke getMonthTheoritical method */
		// check if employee entered the company or left the company over the period.
		def arrivalDate = employee.arrivalDate
		def exitDate = employee.status == StatusType.TERMINE ? employee.status.date : null

		// the employee arrived after the period started: resetting the minDate
		if (arrivalDate > minDate){
			minDate = arrivalDate
		}

		// the employee left before period's end:
		if ((exitDate != null) && exitDate < maxDate){
			maxDate = exitDate
		}
		
		def calendarIter = Calendar.instance
		calendarIter.time = minDate
		
		// 2 cases: either min date is greater than 1st of the year, then 1 loop. Otherwise, 2 loops.
		if (minDate.getAt(Calendar.YEAR) == maxDate.getAt(Calendar.YEAR)){
			while(calendarIter.get(Calendar.MONTH) <= maxDate.getAt(Calendar.MONTH)){
				log.debug('calendarIter: '+calendarIter.time)				
				criteria = MonthlyTotal.createCriteria()
				 monthlyTotal = criteria.get {
						and {
							eq('employee',employee)
							eq('year',calendarIter.get(Calendar.YEAR))
							eq('month',calendarIter.get(Calendar.MONTH)+1)
						}
					}
				
				if (monthlyTotal != null){
					yearTimeBefore7 += monthlyTotal.timeBefore7
					yearTimeOffHours += monthlyTotal.timeBefore7
					yearTimeAfter20 += monthlyTotal.timeAfter20
					yearTimeOffHours += monthlyTotal.timeAfter20
				}
				if (calendarIter.get(Calendar.MONTH) == maxDate.getAt(Calendar.MONTH)){
					timeBefore7 = monthlyTotal != null ? monthlyTotal.timeBefore7 : 0
					timeAfter20 = monthlyTotal != null ? monthlyTotal.timeAfter20 : 0
					timeOffHours = monthlyTotal != null ? (monthlyTotal.timeAfter20 + monthlyTotal.timeBefore7) : 0
					break
				}
				calendarIter.roll(Calendar.MONTH, 1)
			}
		}else{
			while(calendarIter.get(Calendar.MONTH) <= 11){
				log.debug('calendarIter: '+calendarIter.time)
				criteria = MonthlyTotal.createCriteria()
				monthlyTotal = criteria.get {
					   and {
						   eq('employee',employee)
						   eq('year',calendarIter.get(Calendar.YEAR))
						   eq('month',calendarIter.get(Calendar.MONTH)+1)
					   }
				   }			   
				if (monthlyTotal != null){
					yearTimeBefore7 += monthlyTotal.timeBefore7
					yearTimeOffHours += monthlyTotal.timeBefore7
					yearTimeAfter20 += monthlyTotal.timeAfter20
					yearTimeOffHours += monthlyTotal.timeAfter20
				}
				if (calendarIter.get(Calendar.MONTH) == 11){
					timeBefore7 = monthlyTotal != null ? monthlyTotal.timeBefore7 : 0
					timeAfter20 = monthlyTotal != null ? monthlyTotal.timeAfter20 : 0
					timeOffHours = monthlyTotal != null ? (monthlyTotal.timeAfter20 + monthlyTotal.timeBefore7) : 0
					break
				}
				calendarIter.roll(Calendar.MONTH, 1)
			}
			calendarIter.set(Calendar.MONTH,0)
			calendarIter.set(Calendar.YEAR,(calendarIter.get(Calendar.YEAR)+1))
			
			while(calendarIter.get(Calendar.MONTH) <= maxDate.getAt(Calendar.MONTH)){
				log.debug('calendarIter: '+calendarIter.time)
				criteria = MonthlyTotal.createCriteria()
				 monthlyTotal = criteria.get {
					and {
						eq('employee',employee)
						eq('year',calendarIter.get(Calendar.YEAR))
						eq('month',calendarIter.get(Calendar.MONTH)+1)
					}
				}				
				if (monthlyTotal != null){
					yearTimeBefore7 += monthlyTotal.timeBefore7
					yearTimeOffHours += monthlyTotal.timeBefore7
					yearTimeAfter20 += monthlyTotal.timeAfter20
					yearTimeOffHours += monthlyTotal.timeAfter20
				}
				if (calendarIter.get(Calendar.MONTH) == maxDate.getAt(Calendar.MONTH)){
					timeBefore7 = monthlyTotal != null ? monthlyTotal.timeBefore7 : 0
					timeAfter20 = monthlyTotal != null ? monthlyTotal.timeAfter20 : 0
					timeOffHours = monthlyTotal != null ? (monthlyTotal.timeAfter20 + monthlyTotal.timeBefore7) : 0
					break
				}
				calendarIter.roll(Calendar.MONTH, 1)
			}
		}	
		criteria = MonthlyTotal.createCriteria()
		monthlyTotal = criteria.get {
		   and {
			   eq('employee',employee)
			   eq('year',year)
			   eq('month',month)
		   }
		}
		if (monthlyTotal != null){
			timeBefore7 = monthlyTotal.timeBefore7
			timeAfter20 = monthlyTotal.timeAfter20
			timeOffHours = timeBefore7 + timeAfter20
		}
		
		def timeBefore7Decimal = computeHumanTime(timeBefore7 as long)
		timeBefore7Decimal=(timeBefore7Decimal.get(0)+timeBefore7Decimal.get(1)/60).setScale(2,2)
		
		def timeAfter20Decimal = computeHumanTime(timeAfter20 as long)
		timeAfter20Decimal=(timeAfter20Decimal.get(0)+timeAfter20Decimal.get(1)/60).setScale(2,2)
		
		def timeOffHoursDecimal = computeHumanTime(timeOffHours as long)
		timeOffHoursDecimal= (timeOffHoursDecimal.get(0)+timeOffHoursDecimal.get(1)/60).setScale(2,2)
				
		def yearTimeBefore7Decimal =computeHumanTime(yearTimeBefore7 as long)
		yearTimeBefore7Decimal= (yearTimeBefore7Decimal.get(0)+yearTimeBefore7Decimal.get(1)/60).setScale(2,2)
		
		def yearTimeAfter20Decimal = computeHumanTime(yearTimeAfter20 as long)
		yearTimeAfter20Decimal= (yearTimeAfter20Decimal.get(0)+yearTimeAfter20Decimal.get(1)/60).setScale(2,2)
		
		def yearTimeOffHoursDecimal = computeHumanTime(yearTimeOffHours as long)
		yearTimeOffHoursDecimal=(yearTimeOffHoursDecimal.get(0)+yearTimeOffHoursDecimal.get(1)/60).setScale(2,2)
			
		return [
			timeBefore7:timeBefore7,
			timeAfter20:timeAfter20 ,
			timeOffHours:timeOffHours,
			yearTimeBefore7:yearTimeBefore7,
			yearTimeAfter20:yearTimeAfter20,
			yearTimeOffHours:yearTimeOffHours,
			timeBefore7Decimal:timeBefore7Decimal,
			timeAfter20Decimal:timeAfter20Decimal,
			timeOffHoursDecimal:timeOffHoursDecimal,
			yearTimeBefore7Decimal:yearTimeBefore7Decimal,
			yearTimeAfter20Decimal:yearTimeAfter20Decimal,
			yearTimeOffHoursDecimal:yearTimeOffHoursDecimal
		]
	}
	
	def getCartoucheDataMultiThread(Employee employeeInstance,int year,int month){
		def holidays = []
		def garde_enfant = []
		def exceptionnel = []
		def paternite = []
		def chomage = []
		def parental = []
		def formation = []
		def dif = []
		def don = []
		def rtt = []
		def sickness = []
		def maternite = []
		def pregnancy = []
		def sansSolde = []
		def injustifie = []
		def MISE_A_PIED = []
		def pregnancyCredit = 0
		def counter = 0
		def totalNumberOfDays = 0
		def monthTheoritical
		def criteria
		def calendar = Calendar.instance
		def startCalendar = Calendar.instance
		def endCalendar = Calendar.instance
		def isCurrentMonth = false		
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.MONTH,month-1)
		calendar.clearTime()
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.set(Calendar.YEAR,year)
		startCalendar.set(Calendar.MONTH,month-1)
		startCalendar.clearTime()
		endCalendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		endCalendar.set(Calendar.YEAR,year)
		endCalendar.set(Calendar.MONTH,month-1)
		def currentCalendar = Calendar.instance

		// special case: the month is not over yet
		if (currentCalendar.get(Calendar.MONTH) == (month - 1) && currentCalendar.get(Calendar.YEAR) == year){
			log.debug('the month is not over yet')
			counter = openDaysBetweenDates(calendar.time,currentCalendar.time)
			totalNumberOfDays = counter
			isCurrentMonth = true
		}else{
			counter = openDaysBetweenDates(calendar.time,endCalendar.time)
			totalNumberOfDays = counter
		}
		// count sundays within given month

		// treat special case whereby employee enters the company, or leaves...
		// special case: arrival month
		if ((employeeInstance.arrivalDate.getAt(Calendar.MONTH) + 1) == month &&  (employeeInstance.arrivalDate.getAt(Calendar.YEAR)) == year){
			// we need to count OPEN DAYS between dates and not consecutive days
			counter = openDaysBetweenDates(employeeInstance.arrivalDate,endCalendar.time)
		}
		
		//special case: departure month
		def currentStatus = employeeInstance.status
		if (currentStatus.date != null && currentStatus.date <= endCalendar.time){
			if (currentStatus.type != StatusType.ACTIF){
				if (currentStatus.date.getAt(Calendar.MONTH) == endCalendar.get(Calendar.MONTH) && currentStatus.date.getAt(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) ){
					log.error('departure month. recomputing days')
					Calendar exitCalendar = Calendar.instance
					exitCalendar.time = currentStatus.date
					exitCalendar.roll(Calendar.DAY_OF_YEAR, -1)
					counter = openDaysBetweenDates(calendar.time,exitCalendar.time)
				}else{
					counter = 0
				}
			}
		}
			
		// special case: employee has not yet arrived in the company
		if (employeeInstance.arrivalDate > currentCalendar.time ){
			counter = 0
		}

		// get cumul RTT
		Absence.withTransaction{
			rtt = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.RTT])
		}
		
		Absence.withTransaction{
			pregnancy = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.GROSSESSE])
			pregnancyCredit=30*60*pregnancy.size()
		}
		
		Absence.withTransaction{
			sickness = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.MALADIE])
		}
		
		Absence.withTransaction{
			maternite = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.MATERNITE])
		}

		
		Absence.withTransaction{
			holidays = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.VACANCE])
		}
		Absence.withTransaction{
			garde_enfant = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.GARDE_ENFANT])
		}
		Absence.withTransaction{
			chomage = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.CHOMAGE])
		}
		Absence.withTransaction{
			exceptionnel = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.EXCEPTIONNEL])
		}

		Absence.withTransaction{
			paternite = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.PATERNITE])
		}
		
		Absence.withTransaction{
			parental = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.PARENTAL])
		}
		Absence.withTransaction{
			formation = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.FORMATION])
		}
		
		Absence.withTransaction{
			dif = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.DIF])
		}
		
		Absence.withTransaction{
			don = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.DON])
		}
		
		Absence.withTransaction{
			sansSolde = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.CSS])
		}
		
		Absence.withTransaction{
			injustifie = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.INJUSTIFIE])
		}
		Absence.withTransaction{
			MISE_A_PIED = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.MISE_A_PIED])
		}
		
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		calendar.set(Calendar.DATE,1)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.MONTH,month-1)
		def yearlyCartouche = getYearCartoucheDataMultiThread(employeeInstance,year,month)
		
		//def yearlyCartouche=getYearCartoucheData(employeeInstance,year,month)
		// determine monthly theoritical time:
		//	monthTheoritical=getMonthTheoritical(employeeInstance,  month, year)
	
		
		def currentContract
		Contract.withTransaction{
			def contracts = Contract.findAllByEmployee(employeeInstance)			
			for (Contract contract : contracts){
				if (contract.endDate != null && contract.startDate < endCalendar.time && contract.endDate > endCalendar.time){currentContract = contract}
				if (contract.endDate != null && contract.startDate < endCalendar.time && contract.endDate < endCalendar.time && contract.endDate.getAt(Calendar.MONTH)==endCalendar.time.getAt(Calendar.MONTH)){currentContract = contract}
				if (currentContract == null && contract.endDate == null){currentContract = contract}
			}
		}
		
		monthTheoritical=getMonthTheoritical(employeeInstance,  month, year)
		
		def cartoucheMap = [
			isCurrentMonth:isCurrentMonth,
			currentContract:currentContract,
			employeeInstance:employeeInstance,
			workingDays:counter,
			holidays:holidays.size(),
			exceptionnel:exceptionnel.size(),
			paternite:paternite.size(),
			parental:parental.size(),
			formation:formation.size(),
			dif:dif.size(),
			don:don.size(),
			rtt:rtt.size(),
			sickness:sickness.size(),
			maternite:maternite.size(),
			sansSolde:sansSolde.size(),
			injustifie:injustifie.size(),
			MISE_A_PIED:MISE_A_PIED.size(),
			monthTheoritical:monthTheoritical,
			pregnancyCredit:pregnancyCredit,
			calendar:calendar
		]
		return cartoucheMap
	}
	
	
	def getYearCartoucheDataMultiThread(Employee employee,int year,int month){
		def monthNumber=0
		def yearOpenDays = 0
		def yearTheoritical = 0
		def yearSupTime = 0
		def data
		def calendar = Calendar.instance
		calendar.set(Calendar.YEAR,year)
		def yearlyCounter = 0
		def totalTime = 0
		def bankHolidayCounter=0
		def bankHolidayList
		def monthTheoritical
		def monthsAggregate
		if (month > 5){
			year = year + 1
			monthNumber = month - 6 + 1
		}else{
			monthNumber = month + 1 + 6
		}

		// set the date end of may
		calendar.set(Calendar.DAY_OF_MONTH,10)
		calendar.set(Calendar.MONTH,month-1)
		log.debug("month: "+calendar.get(Calendar.MONTH))
		calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		log.debug("last day of the month: "+calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		log.debug("calendar: "+calendar)
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		def maxDate = calendar.time
		log.debug("month: "+calendar.get(Calendar.MONTH))
		log.debug("maxDate: "+maxDate)
		calendar.set(Calendar.YEAR,year-1)
		calendar.set(Calendar.MONTH,5)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.clearTime()
		def minDate = calendar.time
		def yearlyHolidays = []
		def yearlyGarde_enfant = []
		def yearlyChomage = []
		def yearlyExceptionnel = []
		def yearlyParental = []
		def yearlyPaternite = []
		def yearlyMaternite = []
		def yearlyFormation = []
		def yearlyDif = []
		def yearlyDon = []
		def yearlyRtt = []
		def yearlySickness = []
		def pregnancy = []
		def yearlySansSolde = []
		def yearlyInjustifie = []
		def yearlyMISE_A_PIED = []
		
		/* create a loop over months and invoke getMonthTheoritical method */
		// check if employee entered the company or left the company over the period.
		def arrivalDate = employee.arrivalDate
		def exitDate = employee.status == StatusType.TERMINE ? employee.status.date : null

		// the employee arrived after the period started: resetting the minDate
		if (arrivalDate > minDate){minDate = arrivalDate}

		// the employee left before period's end:
		if ((exitDate != null) && exitDate < maxDate){maxDate = exitDate}
		
		yearOpenDays = openDaysBetweenDates(minDate,maxDate)
		// get cumul holidays
		
		Absence.withTransaction{
			yearlyHolidays = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.VACANCE])
		}
		Absence.withTransaction{
			yearlyGarde_enfant = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.GARDE_ENFANT])
		}
		Absence.withTransaction{
			yearlyChomage = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.CHOMAGE])
		}
		Absence.withTransaction{
			yearlyExceptionnel = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.EXCEPTIONNEL])
		}

		Absence.withTransaction{
			yearlyPaternite = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.PATERNITE])
		}
		
		Absence.withTransaction{
			yearlyParental = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.PARENTAL])
		}
		Absence.withTransaction{
			yearlyFormation = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.FORMATION])
		}

		Absence.withTransaction{
			yearlyDif = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.DIF])
		}
		
		Absence.withTransaction{
			yearlyDon = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.DON])
		}
		
		Absence.withTransaction{
			yearlyRtt = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.RTT])
		}

		Absence.withTransaction{
			yearlySickness = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.MALADIE])
		}
		
		Absence.withTransaction{
			yearlyMaternite = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.MATERNITE])
		}
		
		Absence.withTransaction{
			pregnancy = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.GROSSESSE])
		}
		def yearlyPregnancyCredit=30*60*pregnancy.size()
		
		
		Absence.withTransaction{
			yearlySansSolde = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.CSS])
		}
		
		Absence.withTransaction{
			yearlyInjustifie = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.INJUSTIFIE])
		}
		Absence.withTransaction{
			yearlyMISE_A_PIED = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.MISE_A_PIED])
		}
						
		if (month >= 6){			
			MonthlyTotal.withTransaction{
				monthsAggregate = MonthlyTotal.findAll("from MonthlyTotal where employee = :employee and month >= :minMonth and month <= :month  and year = :year",[employee:employee,minMonth:6,month:month,year:year-1])
			}
			for (MonthlyTotal monthIter:monthsAggregate){totalTime += monthIter.elapsedSeconds}
		}else{
			MonthlyTotal.withTransaction{
				monthsAggregate = MonthlyTotal.findAll("from MonthlyTotal where employee = :employee and month >= :minMonth and month <= :month  and year = :year",[employee:employee,minMonth:6,month:12,year:year-1])
			}
		
			for (MonthlyTotal monthIter:monthsAggregate){totalTime += monthIter.elapsedSeconds}
			MonthlyTotal.withTransaction{
				monthsAggregate = MonthlyTotal.findAll("from MonthlyTotal where employee = :employee and month <= :month  and year = :year",[employee:employee,month:month,year:year])
			}
			for (MonthlyTotal monthIter:monthsAggregate){totalTime += monthIter.elapsedSeconds}	
		}

		yearlyCounter = month > 5 ? utilService.getYearlyCounter(year-1,month,employee) : utilService.getYearlyCounter(year,month,employee)
		
		if (month < 6){		
			BankHoliday.withTransaction{
				bankHolidayList = BankHoliday.findAll("from BankHoliday where month >= :minMonth and month <= :month  and year = :year",[minMonth:1,month:month,year:year])
			}
			for (BankHoliday bankHoliday:bankHolidayList){
				if ((bankHoliday.calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) && (bankHoliday.calendar.time > employee.arrivalDate) ){bankHolidayCounter ++}
			}
			
			BankHoliday.withTransaction{
				bankHolidayList = BankHoliday.findAll("from BankHoliday where month >= :minMonth and month <= :month  and year = :year",[minMonth:6,month:12,year:year-1])
			}
			for (BankHoliday bankHoliday:bankHolidayList){
				if ((bankHoliday.calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) && (bankHoliday.calendar.time > employee.arrivalDate) ){bankHolidayCounter ++}
			}
		}
		else{
			BankHoliday.withTransaction{
				bankHolidayList = BankHoliday.findAll("from BankHoliday where month >= :minMonth and month <= :month  and year = :year",[minMonth:6,month:month,year:year-1])
			}
			for (BankHoliday bankHoliday:bankHolidayList){
				if ((bankHoliday.calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) && (bankHoliday.calendar.time > employee.arrivalDate) ){bankHolidayCounter ++}
			}
		}
		def calendarIter = Calendar.instance
		calendarIter.time = minDate

		// 2 cases: either min date is greater than 1st of the year, then 1 loop. Otherwise, 2 loops.
		if (minDate.getAt(Calendar.YEAR) == maxDate.getAt(Calendar.YEAR)){
			while(calendarIter.get(Calendar.MONTH) <= maxDate.getAt(Calendar.MONTH)){
				log.debug('calendarIter: '+calendarIter.time)
				monthTheoritical = getMonthTheoriticalMultiThread(employee,  calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
				yearTheoritical += monthTheoritical
				if (calendarIter.get(Calendar.MONTH) == maxDate.getAt(Calendar.MONTH)){
					break
				}
				calendarIter.roll(Calendar.MONTH, 1)
			}
		}else{
			while(calendarIter.get(Calendar.MONTH) <= 11){
				log.debug('calendarIter: '+calendarIter.time)
				monthTheoritical = getMonthTheoriticalMultiThread(employee,  calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
				yearTheoritical += monthTheoritical
				if (calendarIter.get(Calendar.MONTH) == 11){
					break
				}
				calendarIter.roll(Calendar.MONTH, 1)
			}
			calendarIter.set(Calendar.MONTH,0)
			calendarIter.set(Calendar.YEAR,(calendarIter.get(Calendar.YEAR)+1))
			
			while(calendarIter.get(Calendar.MONTH) <= maxDate.getAt(Calendar.MONTH)){
				log.debug('calendarIter: '+calendarIter.time)
				monthTheoritical = getMonthTheoriticalMultiThread(employee,  calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
				yearTheoritical += monthTheoritical
				if (calendarIter.get(Calendar.MONTH) == maxDate.getAt(Calendar.MONTH)){
					break
				}
				calendarIter.roll(Calendar.MONTH, 1)
			}
		}
		
		yearlyCounter = openDaysBetweenDates(minDate,maxDate)
		return [
			yearOpenDays:yearOpenDays,
			yearlyActualTotal:yearlyCounter,
			yearlyHolidays:yearlyHolidays.size(),
			yearlyGarde_enfant:yearlyGarde_enfant.size(),
			yearlyChomage:yearlyChomage.size(),
			yearlyExceptionnel:yearlyExceptionnel.size(),
			yearlyPaternite:yearlyPaternite.size(),
			yearlyParental:yearlyParental.size(),
			yearlyFormation:yearlyFormation.size(),
			yearlyDif:yearlyDif.size(),
			yearlyDon:yearlyDon.size(),
			yearlyRtt:yearlyRtt.size(),
			yearlySickness:yearlySickness.size(),
			yearlyMaternite:yearlyMaternite.size(),
			yearlyTheoritical:yearTheoritical,
			yearlyPregnancyCredit:yearlyPregnancyCredit,
			yearlyTotalTime:totalTime,
			yearlySansSolde:yearlySansSolde.size(),
			yearSupTime:yearSupTime
		]
	}
	
	def getMonthTheoriticalMultiThread(Employee employee, int month,int year){
		def monthTheoritical = 0
		def counter = 0
		def isOut = false
		def totalNumberOfDays
		def weeklyContractTime
		def contract
		def criteria
		def previousContracts
		def remainingDays
		def currentStatus = employee.status
		def realOpenDays
		def departureDate
		def startDate
		def endDate
		def startCalendar = Calendar.instance
		def endCalendar = Calendar.instance
		def calendarCompute = Calendar.instance
		
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.set(Calendar.YEAR,year)
		startCalendar.set(Calendar.MONTH,month-1)
		startCalendar.clearTime()
		log.debug('startCalendar: '+startCalendar.time)
		
		endCalendar.set(Calendar.YEAR,year)
		endCalendar.set(Calendar.MONTH,month-1)
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
		endCalendar.set(Calendar.DAY_OF_MONTH,startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
		log.debug('endCalendar: '+endCalendar.time)
		
		previousContracts = []
		Contract.withTransaction{
			
			def tmp = Contract.findAll("from Contract where employee = :employee and startDate <= :startDate and endDate >= :endDate and endDate is not null order by startDate desc",[employee:employee,startDate:startCalendar.time,endDate:startCalendar.time])
			if (tmp != null && tmp.size() > 0)
				previousContracts.addAll(tmp)
			tmp = Contract.findAll("from Contract where employee = :employee and endDate >= :startDate and endDate <= :endDate and endDate is not null order by startDate desc",[employee:employee,startDate:startCalendar.time,endDate:endCalendar.time])
			if (tmp != null && tmp.size() > 0)
				previousContracts.addAll(tmp)
			tmp = Contract.findAll("from Contract where employee = :employee and startDate <= :endDate and endDate is null order by startDate desc",[employee:employee,endDate:startCalendar.time])
			if (tmp != null && tmp.size() > 0)
				previousContracts.addAll(tmp)
			tmp = Contract.findAll("from Contract where employee = :employee and startDate >= :startDate and startDate <= :endDate order by startDate desc",[employee:employee,startDate:startCalendar.time,endDate:endCalendar.time])	
			if (tmp != null && tmp.size() > 0)
				previousContracts.addAll(tmp)
				
			monthTheoritical = 0
			for (Contract currentContract : previousContracts){
				weeklyContractTime = currentContract.weeklyLength
				if (weeklyContractTime == 0){
					monthTheoritical += 0
				}else{
					startDate = currentContract.startDate
		
					//the contract was not started during the current month: we will take first day of the month as starting point.
					if (startDate < startCalendar.time){
						startDate = startCalendar.time
					}
					
					//the contract did not end during the month: we will take the last day of the month as end point.
					endDate = currentContract.endDate
					if (endDate > endCalendar.time){
						endDate = endCalendar.time
					}
									
					// if the contract has no end date, set the end the end date to the end of the month.
					if (currentContract.endDate ==  null){
						def endContractCalendar = Calendar.instance
						endContractCalendar.time = startDate
						endContractCalendar.set(Calendar.HOUR_OF_DAY,23)
						endContractCalendar.set(Calendar.MINUTE,59)
						endContractCalendar.set(Calendar.SECOND,59)
						endContractCalendar.set(Calendar.DAY_OF_MONTH,endContractCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
						endDate = endContractCalendar.time
					}
					
					def absenceMap = getAbsencesBetweenDatesMultiThread( employee, startDate, endDate)
					
					//initialize day counters:
					realOpenDays = openDaysBetweenDates(startDate,endDate)
					totalNumberOfDays = openDaysBetweenDates(startDate,endDate)
					
					// special case: arrival month
					if ((employee.arrivalDate.getAt(Calendar.MONTH) + 1) == month &&  (employee.arrivalDate.getAt(Calendar.YEAR)) == year){
						// we need to count OPEN DAYS between dates and not consecutive days
						realOpenDays = openDaysBetweenDates(employee.arrivalDate,endDate)
					}
		
					// special case: employee has not yet arrived in the company
					if (employee.arrivalDate > endCalendar.time ){
						isOut = true
					}
					
					//special case: departure month
					if (currentStatus.date != null && currentStatus.date <= endCalendar.time){
						if (currentStatus.type != StatusType.ACTIF){
							if (currentStatus.date.getAt(Calendar.MONTH) == endCalendar.get(Calendar.MONTH) && currentStatus.date.getAt(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) ){
							log.debug('departure month. recomputing open days')
								Calendar exitCalendar = Calendar.instance
								exitCalendar.time = currentStatus.date
								exitCalendar.roll(Calendar.DAY_OF_YEAR, -1)
								realOpenDays = openDaysBetweenDates(startCalendar.time,exitCalendar.time)
							}else{
								realOpenDays = 0
								isOut = true
							}
						}
					}
			
					log.debug('open days: '+realOpenDays)
					if (isOut){
						monthTheoritical += 0
					}else{
						log.debug('realOpenDays*weeklyContractTime/Employee.WeekOpenedDays: '+realOpenDays*weeklyContractTime/Employee.WeekOpenedDays)
						log.debug('realOpenDays: '+realOpenDays)
						log.debug('absenceMap.get(AbsenceType.CSS): '+absenceMap.get(AbsenceType.CSS))
						def tmpTheoritical = 0
						tmpTheoritical += realOpenDays*weeklyContractTime/Employee.WeekOpenedDays as long
						log.debug('1: tmpTheoritical: '+tmpTheoritical)
						
						tmpTheoritical += (Employee.Pentecote)*(((realOpenDays as long) - (absenceMap.get(AbsenceType.CSS) as long) - (absenceMap.get(AbsenceType.INJUSTIFIE) as long))/totalNumberOfDays)*((weeklyContractTime as long)/Employee.legalWeekTime)
						log.debug('2: tmpTheoritical: '+tmpTheoritical)
												
						def multiplier = 0
						if ( absenceMap.get(AbsenceType.MALADIE) != null )
							multiplier += absenceMap.get(AbsenceType.MALADIE) as long
						if ( absenceMap.get(AbsenceType.MATERNITE) != null )
							multiplier += absenceMap.get(AbsenceType.MATERNITE) as long							
						if ( absenceMap.get(AbsenceType.VACANCE) != null )
							multiplier += absenceMap.get(AbsenceType.VACANCE) as long
						if ( absenceMap.get(AbsenceType.GARDE_ENFANT) != null )
							multiplier += absenceMap.get(AbsenceType.GARDE_ENFANT) as long		
						if ( absenceMap.get(AbsenceType.CHOMAGE) != null )
							multiplier += absenceMap.get(AbsenceType.CHOMAGE) as long
						if ( absenceMap.get(AbsenceType.CSS) != null )
							multiplier += absenceMap.get(AbsenceType.CSS) as long					
						if ( absenceMap.get(AbsenceType.INJUSTIFIE) != null )
							multiplier += absenceMap.get(AbsenceType.INJUSTIFIE) as long
						if ( absenceMap.get(AbsenceType.MISE_A_PIED) != null )
							multiplier += absenceMap.get(AbsenceType.MISE_A_PIED) as long
						if ( absenceMap.get(AbsenceType.EXCEPTIONNEL) != null )
							multiplier += absenceMap.get(AbsenceType.EXCEPTIONNEL) as long							
						if ( absenceMap.get(AbsenceType.PARENTAL) != null )
							multiplier += absenceMap.get(AbsenceType.PARENTAL) as long							
						if ( absenceMap.get(AbsenceType.DIF) != null )
							multiplier += absenceMap.get(AbsenceType.DIF)							
						if ( absenceMap.get(AbsenceType.DON) != null )
							multiplier += absenceMap.get(AbsenceType.DON)

						tmpTheoritical -= (weeklyContractTime/Employee.WeekOpenedDays)*multiplier
						log.debug('3: tmpTheoritical: '+tmpTheoritical)
						
						tmpTheoritical -= (35/7)*(absenceMap.get(AbsenceType.PATERNITE) as long)
						log.debug('4: tmpTheoritical: '+tmpTheoritical)
						
						tmpTheoritical = 3600*tmpTheoritical
						log.debug('5: tmpTheoritical: '+tmpTheoritical)
						
						tmpTheoritical -= (absenceMap.get(AbsenceType.GROSSESSE) as int)
						log.debug('6: tmpTheoritical: '+tmpTheoritical)
						
						monthTheoritical = tmpTheoritical
						log.debug('monthTheoritical: '+monthTheoritical)						
					}
				}
				log.debug('monthTheoritical: '+monthTheoritical)
			}		
		}
		return monthTheoritical
	}
	
	def getAbsencesBetweenDatesMultiThread(Employee employee,Date startDate,Date endDate){
		def absenceMap = [:]
		def sickness = []
		def maternite = []
		def holidays = []
		def garde_enfant = []
		def chomage = []
		def exceptionnel = []
		def paternite = []
		def parental = []
		def dif = []
		def don = []
		def formation = []
		def sansSolde = []
		def injustifie = []
		def MISE_A_PIED = []
		def pregnancy = []
		def pregnancyCredit = 0
		def patSundayCount = 0
		
		Absence.withTransaction{
			sickness = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.MALADIE])
			absenceMap.put(AbsenceType.MALADIE, sickness.size())
		}
		
		Absence.withTransaction{
			maternite = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.MATERNITE])
			absenceMap.put(AbsenceType.MATERNITE, maternite.size())
		}
		
		Absence.withTransaction{
			holidays = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.VACANCE])
			absenceMap.put(AbsenceType.VACANCE, holidays.size())
		}
		Absence.withTransaction{
			garde_enfant = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.GARDE_ENFANT])
			absenceMap.put(AbsenceType.GARDE_ENFANT, garde_enfant.size())
		}
		Absence.withTransaction{
			chomage = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.CHOMAGE])
			absenceMap.put(AbsenceType.CHOMAGE, chomage.size())
		}

		Absence.withTransaction{
			exceptionnel = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.EXCEPTIONNEL])
			absenceMap.put(AbsenceType.EXCEPTIONNEL, exceptionnel.size())
		}
		Absence.withTransaction{
			parental = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.PARENTAL])
			absenceMap.put(AbsenceType.PARENTAL, parental.size())
		}
		
		Absence.withTransaction{
			formation = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.FORMATION])
			absenceMap.put(AbsenceType.FORMATION, formation.size())
		}
		
		Absence.withTransaction{
			paternite = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.PATERNITE])
			absenceMap.put(AbsenceType.PATERNITE, paternite.size())
			for (Absence tmpPat : paternite){
				//log.error('tmpPat: '+tmpPat)
				if (tmpPat.date.getAt(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
					//log.error('a sunday exists. ')
					patSundayCount += 1
				}
			}
			absenceMap.put("patSundayCount", patSundayCount)
		}

		Absence.withTransaction{
			dif = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.DIF])
			absenceMap.put(AbsenceType.DIF, dif.size())
		}
		
		Absence.withTransaction{
			don = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.DON])
			absenceMap.put(AbsenceType.DON, don.size())
		}

		Absence.withTransaction{
			sansSolde = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.CSS])
			absenceMap.put(AbsenceType.CSS, sansSolde.size())
		}
		
		Absence.withTransaction{
			injustifie = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.INJUSTIFIE])
			absenceMap.put(AbsenceType.INJUSTIFIE, injustifie.size())
		}
		Absence.withTransaction{
			MISE_A_PIED = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.MISE_A_PIED])
			absenceMap.put(AbsenceType.MISE_A_PIED, MISE_A_PIED.size())
		}
		
		Absence.withTransaction{
			pregnancy = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.GROSSESSE])
			pregnancyCredit=30*60*pregnancy.size()	
			absenceMap.put(AbsenceType.GROSSESSE, pregnancyCredit)		
		}
		return absenceMap
	}
		
	
	def getDailyTotalMultiThread(Employee employee,def year, def month, def day){
		def elapsedSeconds = 0
		def timeBefore7 = 0
		def timeAfter20 = 0
		def timeOffHours = 0
		def tmpInOrOut
		def timeDifference
		def currentInOrOut
		def previousInOrOut
		def inOrOutList = []
		def calendarAtSeven = Calendar.instance
		def calendarAtNine = Calendar.instance
		calendarAtSeven.set(Calendar.YEAR,year)
		calendarAtSeven.set(Calendar.MONTH,month-1)
		calendarAtSeven.set(Calendar.DAY_OF_MONTH,day)
		calendarAtSeven.set(Calendar.HOUR_OF_DAY,7)
		calendarAtSeven.set(Calendar.MINUTE,0)
		calendarAtSeven.set(Calendar.SECOND,0)
		calendarAtNine.set(Calendar.YEAR,year)
		calendarAtNine.set(Calendar.MONTH,month-1)
		calendarAtNine.set(Calendar.DAY_OF_MONTH,day)
		calendarAtNine.set(Calendar.HOUR_OF_DAY,20)
		calendarAtNine.set(Calendar.MINUTE,0)
		calendarAtNine.set(Calendar.SECOND,0)
		
		InAndOut.withTransaction{
			inOrOutList = InAndOut.findAll("from InAndOut as a where a.employee = :employee and year = :year and month = :month and day = :day order by time asc",[employee:employee,year:year,month:month,day:day])
			for (InAndOut inOrOut:inOrOutList){
				currentInOrOut = inOrOut
				if (previousInOrOut == null){
					// it is the first occurence
					previousInOrOut = inOrOut
				}else{
					if (previousInOrOut.type.equals("E") && currentInOrOut.type.equals("S")){
						use (TimeCategory){timeDifference = currentInOrOut.time - previousInOrOut.time}
						elapsedSeconds += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
						
						// managing time before 7
						if (currentInOrOut.time < calendarAtSeven.time){
							use (TimeCategory){timeDifference = currentInOrOut.time - previousInOrOut.time}
							timeBefore7 += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
						}
						if (currentInOrOut.time > calendarAtSeven.time && previousInOrOut.time < calendarAtSeven.time){
							use (TimeCategory){timeDifference = calendarAtSeven.time - previousInOrOut.time}
							timeBefore7 += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
						}
						
						// managing time after 21
						if (previousInOrOut.time > calendarAtNine.time){
							use (TimeCategory){timeDifference = currentInOrOut.time - previousInOrOut.time}
							timeAfter20 += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
						}
						if (currentInOrOut.time > calendarAtNine.time && previousInOrOut.time < calendarAtNine.time){
							use (TimeCategory){timeDifference = currentInOrOut.time - calendarAtNine.time}
							timeAfter20 += timeDifference.seconds + timeDifference.minutes*60 + timeDifference.hours*3600
						}
					}
					previousInOrOut = inOrOut
				}
			}		
		}
		//dailyTotal.elapsedSeconds=elapsedSeconds
		timeOffHours = (timeBefore7 as long) + (timeAfter20 as long)
		return [
			elapsedSeconds:elapsedSeconds,
			timeBefore7:timeBefore7,
			timeAfter20:timeAfter20,
			timeOffHours:timeOffHours
		]
	}
	
	def computeSupplementaryTimeMultiThread(Employee employee,int week, int year){
		def dailySupplementarySeconds = 0
		def weeklySupplementarySeconds = 0
		def dailyTotalSum=0
		def dailyTotals = []
		def bankHolidays = []
		def calendar = Calendar.instance
		def bankHolidayCounter = 0
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.WEEK_OF_YEAR,week)	
		
		BankHoliday.withTransaction{
			bankHolidays.addAll(BankHoliday.findAll("from BankHoliday where year = :year and month = :month",[year:year,month:calendar.get(Calendar.MONTH)+1]))
			bankHolidays.addAll(BankHoliday.findAll("from BankHoliday where year = :year and week = :week",[year:year,week:calendar.get(Calendar.WEEK_OF_YEAR)]))
			
			for (BankHoliday bankHoliday:bankHolidays){
				if (bankHoliday.calendar.get(Calendar.WEEK_OF_YEAR)==week){
					bankHolidayCounter+=1
				}
			}
		}

		DailyTotal.withTransaction{
			dailyTotals.addAll(DailyTotal.findAll("from DailyTotal where employee = :employee and week = :week and year = :year",[year:year,week:week,employee:employee]))		
			for (DailyTotal tmpDaily:dailyTotals){
				
				def tmpElapsed = getDailyTotalMultiThread(tmpDaily.employee,tmpDaily.year, tmpDaily.month, tmpDaily.day).get("elapsedSeconds")
				dailyTotalSum += tmpElapsed
				dailySupplementarySeconds += Math.max(tmpElapsed-DailyTotal.maxWorkingTime, 0)
			}
		}
		
		if (dailyTotalSum<=1.2*3600*(WeeklyTotal.WeeklyLegalTime-bankHolidayCounter*(Employee.legalWeekTime/Employee.WeekOpenedDays))){
			weeklySupplementarySeconds = dailySupplementarySeconds
		}else {
			weeklySupplementarySeconds = Math.max(dailySupplementarySeconds,dailyTotalSum-1.2*3600*(WeeklyTotal.WeeklyLegalTime-bankHolidayCounter*(Employee.legalWeekTime/Employee.WeekOpenedDays)))
		}
		return weeklySupplementarySeconds
	}
	
	
	def getAnnualSiteDataGroupByWeek(def currentYear,def site){
		log.error('entering getAnnualSiteDataGroupByWeek')
		def criteria
		def weeklyTotals 
		def weeklyTotalByEmployee = [:]
		def weeklyTotalByWeek = [:]
		def lastWeekOfYearByEmployee = [:]
		def lastWeekOfYearWeeklyTotals
		def employeeInstanceList
		def employeeSubList = []
		def weekList = []
		def rollingCal = Calendar.instance
		def monthList = [6,7,8,9,10,11,12,1,2,3,4,5]
		def currentWeek = 0
		def functionList = Function.list([sort: "ranking", order: "asc"])
		def serviceList = Service.list([sort: "name", order: "asc"])
		
		rollingCal.set(Calendar.MONTH,5)
		rollingCal.set(Calendar.DAY_OF_MONTH,1)
		rollingCal.set(Calendar.YEAR,currentYear)
		rollingCal.clearTime()

		if (site != null){
			employeeInstanceList = []
			for (Function function:functionList){
				for (Service service:serviceList){
					 criteria = Employee.createCriteria()
					 employeeSubList = criteria.list{
						 and {
							 eq('function',function)
							 eq('service',service)
							 eq('site',site)
						 }
						 order('lastName','asc')
					 }
					 employeeInstanceList.addAll(employeeSubList)
				 }
			 }
		}else{
			employeeInstanceList = []
			for (Service service:serviceList){
				for (Function function:functionList){
					employeeInstanceList.addAll(Employee.findAllByFunctionAndService(function,service,[sort: "lastName", order: "asc"]))
				}
			}
		}

		def lastWeekOfYear = rollingCal.getActualMaximum(Calendar.WEEK_OF_YEAR)
		def firstWeek = rollingCal.get(Calendar.WEEK_OF_YEAR)
		def weekNumber = []
		for (int i = firstWeek; i <= lastWeekOfYear;i++){
			weekNumber.add(i)
		}
		def endOfPeriodCal =  Calendar.instance
		endOfPeriodCal.set(Calendar.MONTH,4)
		endOfPeriodCal.set(Calendar.DAY_OF_MONTH,endOfPeriodCal.getActualMaximum(Calendar.DAY_OF_MONTH))
		endOfPeriodCal.set(Calendar.YEAR,currentYear + 1)
		endOfPeriodCal.clearTime()

		for (int j = 1; j <= endOfPeriodCal.get(Calendar.WEEK_OF_YEAR);j++){
			weekNumber.add(j)
		}

		def iteratorYear = currentYear
		for (week in weekNumber){
			if (week < firstWeek){
				iteratorYear = currentYear + 1
			}
			criteria = WeeklyTotal.createCriteria()
			weeklyTotals = criteria.list {
					and {
						eq('year',iteratorYear)
						eq('week',week)

						'in'('employee',employeeInstanceList)
					}
				order('employee', 'asc')
			}

			weeklyTotalByWeek.put(week,weeklyTotals)
			weekList.add(week)

			if (iteratorYear == currentYear && week == lastWeekOfYear){
				log.debug('we got the rest of the last week of year that is in the year + 1')
				criteria = WeeklyTotal.createCriteria()
				lastWeekOfYearWeeklyTotals = criteria.list {
						and {
							eq('year',currentYear + 1)
							eq('week',week)

							'in'('employee',employeeInstanceList)
						}
					order('employee', 'asc')
				}
			}
		}

		for (WeeklyTotal weeklyTotal in lastWeekOfYearWeeklyTotals){
			lastWeekOfYearByEmployee.put(weeklyTotal.employee,weeklyTotal)
		}


		def weeklyTotalsByWeek = [:]
		for (int weekIter in weekList){
			def weeklyTotalsByEmployee = [:]
			for (Employee currentEmployee in employeeInstanceList){
				for (WeeklyTotal weeklyTotal in weeklyTotalByWeek.get(weekIter)){
					if (weeklyTotal.employee == currentEmployee){
						if (weeklyTotalsByEmployee.get(currentEmployee) == null){
							weeklyTotalsByEmployee.put(currentEmployee,weeklyTotal.elapsedSeconds as long)
						}else{
							weeklyTotalsByEmployee.put(currentEmployee,weeklyTotalsByEmployee.get(currentEmployee) + weeklyTotal.elapsedSeconds as long)
						}
						if (weekIter == lastWeekOfYear && lastWeekOfYearWeeklyTotals!= null && lastWeekOfYearByEmployee.get(currentEmployee) != null){
							weeklyTotalsByEmployee.put(currentEmployee,weeklyTotalsByEmployee.get(currentEmployee) + lastWeekOfYearByEmployee.get(currentEmployee).elapsedSeconds as long)
						}
						weeklyTotalsByWeek.put(weekIter,weeklyTotalsByEmployee)
					}
				}

			}
		}
		return [
			weeklyTotalsByWeek:weeklyTotalsByWeek,
			weekList:weekList,
			employeeInstanceList:employeeInstanceList
		]
	}
	
	def getAnnualSiteData(Site site,int month, int year, Period period){
		log.error('entering getAjaxSiteData method')
		def executionTime
		def data
		def calendar = Calendar.instance
		def annualReportMap =[:]
		def model = [:]
		def siteAnnualEmployeeWorkingDays = 0
		def siteAnnualTheoritical = 0
		def siteAnnualTotal = 0
		def siteAnnualHoliday = 0
		def siteAnnualGarde_enfant = 0
		def siteRemainingCA = 0
		def siteAnnualChomage = 0
		def siteAnnualRTT = 0
		def siteAnnualCSS = 0
		def siteAnnualINJUSTIFIE = 0
		def siteAnnualMISE_A_PIED = 0
		def siteAnnualSickness = 0
		def siteAnnualMaternite = 0
		def siteAnnualExceptionnel = 0
		def siteAnnualPaternite = 0
		def siteAnnualParental = 0
		def siteAnnualDIF = 0
		def siteAnnualDON = 0
		def siteAnnualPayableSupTime = 0
		def siteAnnualTheoriticalIncludingExtra = 0
		def siteAnnualSupTimeAboveTheoritical = 0
		def siteAnnualGlobalSupTimeToPay = 0
		def startDate = new Date()
		def employeeList = Employee.findAllBySite(site)
		log.error('nb of employees: '+employeeList.size())
				
		GParsExecutorsPool.withPool {
			 employeeList.iterator().eachParallel {
				 log.error('executing query for employee: '+it)
				 data = getAnnualReportData(period.year, it)
				 annualReportMap.put(it,data)
				 siteAnnualEmployeeWorkingDays += data.get('annualEmployeeWorkingDays')
				 siteAnnualTheoritical += data.get('annualTheoritical')
				 siteAnnualTotal += data.get('annualTotal')
				 siteAnnualHoliday += data.get('annualHoliday')
				 siteAnnualChomage += data.get('annualChomage')
				 siteAnnualGarde_enfant += data.get('annualGarde_enfant') 
				 siteRemainingCA += data.get('remainingCA')
				 siteAnnualRTT += data.get('annualRTT')
				 siteAnnualCSS += data.get('annualCSS')
				 siteAnnualINJUSTIFIE += data.get('annualINJUSTIFIE')
				 siteAnnualMISE_A_PIED += data.get('annualMISE_A_PIED')
				 siteAnnualSickness += data.get('annualSickness')
				 siteAnnualMaternite += data.get('annualMaternite')	 
				 siteAnnualDIF += data.get('annualDIF')
				 siteAnnualDON += data.get('annualDON')
				 siteAnnualExceptionnel += data.get('annualExceptionnel')
				 siteAnnualParental += data.get('annualParental')
				 siteAnnualPaternite += data.get('annualPaternite')
				 siteAnnualPayableSupTime += data.get('annualPayableSupTime') as long
				 siteAnnualTheoriticalIncludingExtra += data.get('annualTheoriticalIncludingExtra') as long
				 siteAnnualSupTimeAboveTheoritical += data.get('annualSupTimeAboveTheoritical') as long
				 siteAnnualGlobalSupTimeToPay += data.get('annualGlobalSupTimeToPay')
			 }
		 }
		log.error('employee loop finished')
		def endDate = new Date()
		use (TimeCategory){executionTime=endDate-startDate}
		log.error('execution time: '+executionTime)
		model << [
			period2:period,
			site:site,
			siteId:site.id,
			employeeList:employeeList,
			annualReportMap:annualReportMap,
			siteAnnualEmployeeWorkingDays:siteAnnualEmployeeWorkingDays,
			siteAnnualTheoritical:siteAnnualTheoritical,
			siteAnnualTotal:siteAnnualTotal,
			siteAnnualHoliday:siteAnnualHoliday,
			siteAnnualChomage:siteAnnualChomage,
			siteAnnualGarde_enfant:siteAnnualGarde_enfant,
			siteRemainingCA:siteRemainingCA,
			siteAnnualRTT:siteAnnualRTT,
			siteAnnualCSS:siteAnnualCSS,
			siteAnnualINJUSTIFIE:siteAnnualINJUSTIFIE,
			siteAnnualMISE_A_PIED:siteAnnualMISE_A_PIED,
			siteAnnualSickness:siteAnnualSickness,
			siteAnnualMaternite:siteAnnualMaternite,
			siteAnnualDIF:siteAnnualDIF,
			siteAnnualDON:siteAnnualDON,
			siteAnnualExceptionnel:siteAnnualExceptionnel,
			siteAnnualPaternite:siteAnnualPaternite,
			siteAnnualParental:siteAnnualParental,
			siteAnnualPayableSupTime:siteAnnualPayableSupTime,
			siteAnnualTheoriticalIncludingExtra:siteAnnualTheoriticalIncludingExtra,
			siteAnnualSupTimeAboveTheoritical:siteAnnualSupTimeAboveTheoritical,
			siteAnnualGlobalSupTimeToPay:siteAnnualGlobalSupTimeToPay
		]
		
		return model
	}
	
	/*
	def getMileage(Date minDate, Date maxDate, Employee employee){
		def mileageMaxDay = 20
		Calendar maxCalendar = Calendar.instance
		maxCalendar.time = maxDate
		maxCalendar.set(Calendar.DAY_OF_MONTH,1)
		Calendar minCalendar = Calendar.instance
		minCalendar.time = minDate
		def dailyMileage
		def totalPeriodMileage = 0
		def year
		def month
		def day
		def model = [:]
		 
		
		def mileageMap = [:]
		while(minCalendar.get(Calendar.DAY_OF_MONTH) <= minCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			log.debug('minCalendar: '+minCalendar.time)
			def criteria = Mileage.createCriteria()
			dailyMileage = criteria.get {
				and {
					eq('employee',employee)
					eq('year',minCalendar.get(Calendar.YEAR))
					eq('month',minCalendar.get(Calendar.MONTH) + 1)
					eq('day',minCalendar.get(Calendar.DAY_OF_MONTH))
				}
			}
			if (dailyMileage != null){
				mileageMap.put(minCalendar.time,dailyMileage.value)
				totalPeriodMileage += dailyMileage.value
			}
			else {
				mileageMap.put(minCalendar.time,0)
			}
			if (minCalendar.get(Calendar.DAY_OF_MONTH) == minCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			minCalendar.roll(Calendar.DAY_OF_MONTH, 1)
		}
		while(maxCalendar.get(Calendar.DAY_OF_MONTH) <= mileageMaxDay){
			log.debug('maxCalendar: '+maxCalendar.time)
			def criteria = Mileage.createCriteria()
			dailyMileage = criteria.get {
				and {
					eq('employee',employee)
					eq('year',maxCalendar.get(Calendar.YEAR))
					eq('month',maxCalendar.get(Calendar.MONTH) + 1)
					eq('day',maxCalendar.get(Calendar.DAY_OF_MONTH))
				}
			}
			if (dailyMileage != null){
				mileageMap.put(maxCalendar.time,dailyMileage.value)
				totalPeriodMileage += dailyMileage.value
			}
			else {
				mileageMap.put(maxCalendar.time,0)
			}
			if (maxCalendar.get(Calendar.DAY_OF_MONTH) == mileageMaxDay){
				break
			}
			maxCalendar.roll(Calendar.DAY_OF_MONTH, 1)
		}
		

		
		model << [
				mileageMap:mileageMap,
				totalPeriodMileage:totalPeriodMileage,
				minDate:minDate,
				maxDate:maxDate,
				employee:employee
			]
		return model
				
		
	}
	
	*/
	
	def getMileage(Date minDate, Date maxDate, Employee employee){
		Calendar maxCalendar = Calendar.instance
		Calendar minCalendar = Calendar.instance
		minCalendar.time = minDate
		maxCalendar.time = maxDate

		def dailyMileage 
		def totalPeriodMileage = 0
		def year
		def month
		def day
		def model = [:]
		 
		
		def mileageMap = [:]
		while(minCalendar.get(Calendar.DAY_OF_MONTH) <= minCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			def tmpDate = minCalendar.time
			def criteria = Mileage.createCriteria()
			dailyMileage = criteria.get {
				and {
					eq('employee',employee)				
					eq('year',minCalendar.get(Calendar.YEAR))
					eq('month',minCalendar.get(Calendar.MONTH) + 1)
					eq('day',minCalendar.get(Calendar.DAY_OF_MONTH))
				}
			}
			if (dailyMileage != null){
				mileageMap.put(tmpDate,dailyMileage.value)
				totalPeriodMileage += dailyMileage.value
			}
			else {
				mileageMap.put(tmpDate,0)
			}
			if (minCalendar.get(Calendar.DAY_OF_MONTH) == minCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)){			
				break
			}
			minCalendar.roll(Calendar.DAY_OF_MONTH, 1)
		}
		
		model << [
				mileageMap:mileageMap,
				totalPeriodMileage:totalPeriodMileage,
				minDate:minDate,
				maxDate:maxDate,
				employee:employee			
			]
		return model
				
		
	}
	
	String writeHumanTime(long inputSeconds){
		boolean isNegative = (inputSeconds < 0) ? true : false
		def outputString = ''
		def diff = inputSeconds
		long hours = TimeUnit.SECONDS.toHours(diff);
		diff = diff - (hours*3600);
		long minutes=TimeUnit.SECONDS.toMinutes(diff);
		diff = diff - (minutes*60);
		long seconds = TimeUnit.SECONDS.toSeconds(diff);
		if (!isNegative){
			if (hours < 10)
				outputString += '0'
			outputString += hours
			outputString += ':'
			
			if (minutes < 10)
				outputString += '0'
			outputString += minutes
		}else{
			outputString += '-'
			if (Math.abs(hours)<10)
				outputString += '0'
			outputString += Math.abs(hours)
			outputString += ':'
			if (Math.abs(minutes)<10)
				outputString += '0'
				outputString += Math.abs(minutes)
		}
		return outputString
	}
	
	def convertStringToTime(def inputString){
		def splitTable = inputString.split(":")
		def hours = splitTable[0] as long
		def minutes = splitTable[1] as long
		return hours*3600+minutes*60
		
	}
	
	def getWeeklyReportData(def year, def site, def funtionCheckBoxesMap){
		log.error('entering getWeeklyReportData')
		def monthList = [6,7,8,9,10,11,12,1,2,3,4,5]
		def weekList = []
		def weeklyTotalByEmployee = [:]
		def weeklyTotalByWeek = [:]
		def lastWeekOfYearByEmployee = [:]
		def criteria
		def employeeSubList
		def employeeInstanceList
		def weeklyTotals
		def weeklyCase
		def lastWeekOfYearWeeklyTotals
		def currentYear = year
		def currentWeek = 0
		def functionList = Function.list([sort: "ranking", order: "asc"])
		def serviceList = Service.list([sort: "name", order: "asc"])
		def totalByFunction = [:]
		def weeklyFunctionTotalMap = [:]
		def siteFunctionMap = [:]
		def weeklyCasesMap = [:]
		def yearlyTotalsByFunction = [:]
		def yearlySubTotals = 0
		def yearlyCases = 0
		def yearlyParticularity1 = 0
		def yearlyParticularity2 = 0
		def yearlyHomeAssistance = 0
		def yearlySubTotalsMinutes = 0
		def yearlyTotalsByEmployee = [:]		
		def weeklyTotalsByWeek = [:]
		def weeklySubTotalsByWeek = [:]
		def yearlyRemainingCAByEmployee = [:]
		def yearlyRemainingRTTByEmployee =[:]
		
		
		///
		def calendar = Calendar.instance
		def month = calendar.get(Calendar.MONTH) + 1
		calendar.set(Calendar.DAY_OF_MONTH,10)
		calendar.set(Calendar.MONTH,month - 1)
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

		
		if (site != null){
			employeeInstanceList = []
			for (Function function:functionList){
				for (Service service:serviceList){
					 criteria = Employee.createCriteria()
					 employeeSubList = criteria.list{
						 and {
							 eq('function',function)
							 eq('service',service)
							 eq('site',site)
						 }
						 order('lastName','asc')
					 }
					 employeeInstanceList.addAll(employeeSubList)
				 }
			 }
		}else{
			employeeInstanceList = []
			for (Service service:serviceList){
				for (Function function:functionList){
					employeeInstanceList.addAll(Employee.findAllByFunctionAndService(function,service,[sort: "lastName", order: "asc"]))
				}
			}
		}
		def rollingCal = Calendar.instance
		rollingCal.set(Calendar.MONTH,5)
		rollingCal.set(Calendar.DAY_OF_MONTH,1)
		rollingCal.set(Calendar.YEAR,currentYear)
		rollingCal.clearTime()
		
		def lastWeekOfYear = rollingCal.getActualMaximum(Calendar.WEEK_OF_YEAR)
		def firstWeek = rollingCal.get(Calendar.WEEK_OF_YEAR)
		def weekNumber = []
		for (int i = firstWeek; i <= lastWeekOfYear;i++){
			weekNumber.add(i)
		}
		def endOfPeriodCal =  Calendar.instance
		endOfPeriodCal.set(Calendar.MONTH,4)
		endOfPeriodCal.set(Calendar.DAY_OF_MONTH,endOfPeriodCal.getActualMaximum(Calendar.DAY_OF_MONTH))
		endOfPeriodCal.set(Calendar.YEAR,currentYear + 1)
		endOfPeriodCal.clearTime()
		endOfPeriodCal.roll(Calendar.WEEK_OF_YEAR,-1)
		
		for (int j = 1; j <= endOfPeriodCal.get(Calendar.WEEK_OF_YEAR);j++){
			weekNumber.add(j)
		}
				
		def iteratorYear = currentYear
		for (week in weekNumber){
			if (week < firstWeek){
				iteratorYear = currentYear + 1
			}
			criteria = WeeklyTotal.createCriteria()
			weeklyTotals = criteria.list {
					and {
						eq('year',iteratorYear)
						eq('week',week)
						'in'('employee',employeeInstanceList)
					}
				order('employee', 'asc')
			}
			weeklyTotalByWeek.put(week,weeklyTotals)
			weekList.add(week)
			
			criteria = WeeklyCase.createCriteria()
			weeklyCase = criteria.get {
				and {
					eq('year',iteratorYear)
					eq('week',week)
					eq('site',site)
				}
			}
			
			log.debug('week: '+week)
			log.debug('weeklyCase: '+weeklyCase)
			if (weeklyCase != null){
				weeklyCasesMap.put(week,weeklyCase)
				yearlyCases += weeklyCase.cases
				yearlyHomeAssistance += weeklyCase.home_assistance
			}else{
				weeklyCasesMap.put(week,null)
			}
			
			if (iteratorYear == currentYear && week == lastWeekOfYear){
				log.debug('we got the rest of the last week of year that is in the year + 1')
				criteria = WeeklyTotal.createCriteria()
				lastWeekOfYearWeeklyTotals = criteria.list {
						and {
							eq('year',currentYear + 1)
							eq('week',week)						
							'in'('employee',employeeInstanceList)
						}
					order('employee', 'asc')
				}	
			}
		}

		for (WeeklyTotal weeklyTotal in lastWeekOfYearWeeklyTotals){
			lastWeekOfYearByEmployee.put(weeklyTotal.employee,weeklyTotal)
		}
				
		for (int weekIter in weekList){
			def weeklyTotalsByEmployee = [:]
			def weeklyTotalsByFunction = [:]
			for (Employee currentEmployee in employeeInstanceList){
				
				
				/* create a loop over months and invoke getMonthTheoritical method */
				// check if employee entered the company or left the company over the period.
				/*
				def arrivalDate = currentEmployee.arrivalDate
				def exitDate = currentEmployee.status == StatusType.TERMINE ? currentEmployee.status.date : null
		
				// the employee arrived after the period started: resetting the minDate
				if (arrivalDate > minDate){ minDate = arrivalDate }
		
				// the employee left before period's end:
				if ((exitDate != null) && exitDate < maxDate){ maxDate = exitDate }
				
				// CA pris depuis le début de la période
				criteria = Absence.createCriteria()
				def holidaysYearToDate = criteria.list {
					and {
						eq('employee',currentEmployee)
						ge('date',minDate)
						lt('date',maxDate)
						eq('type',AbsenceType.VACANCE)
					}
				}
				
				// rtt pris depuis le début de la période
				criteria = Absence.createCriteria()
				def rttYearToDate = criteria.list {
					and {
						eq('employee',currentEmployee)
						ge('date',minDate)
						lt('date',maxDate)
						eq('type',AbsenceType.RTT)
					}
				}
				
				// reste à récuperer initialCA et initialRTT
				def initialCA = employeeService.getInitialCA(currentEmployee,(month>5)?Period.findByYear(year):Period.findByYear(year - 1))
				def initialRTT = employeeService.getInitialRTT(currentEmployee,(month>5)?Period.findByYear(year):Period.findByYear(year - 1))
				yearlyRemainingCAByEmployee.put(currentEmployee,initialCA - holidaysYearToDate.size())
				yearlyRemainingRTTByEmployee.put(currentEmployee,initialRTT - rttYearToDate.size())
				
				*/
				
				
				
				for (WeeklyTotal weeklyTotal in weeklyTotalByWeek.get(weekIter)){
					if (weeklyTotal.employee == currentEmployee){
						if (weeklyTotalsByEmployee.get(currentEmployee) == null){
							weeklyTotalsByEmployee.put(currentEmployee,weeklyTotal.elapsedSeconds as long)
						}else{
							weeklyTotalsByEmployee.put(currentEmployee,weeklyTotalsByEmployee.get(currentEmployee) + weeklyTotal.elapsedSeconds as long)
						}
						if (weekIter == lastWeekOfYear && lastWeekOfYearWeeklyTotals!= null && lastWeekOfYearByEmployee.get(currentEmployee) != null){
							weeklyTotalsByEmployee.put(currentEmployee,weeklyTotalsByEmployee.get(currentEmployee) + lastWeekOfYearByEmployee.get(currentEmployee).elapsedSeconds as long)
						}
						weeklyTotalsByWeek.put(weekIter,weeklyTotalsByEmployee)
						if (funtionCheckBoxesMap.get(currentEmployee.function.name) != null && funtionCheckBoxesMap.get(currentEmployee.function.name) == true){
							if (weeklySubTotalsByWeek.get(weekIter) != null){
								weeklySubTotalsByWeek.put(weekIter,(weeklySubTotalsByWeek.get(weekIter) as long) + (weeklyTotal.elapsedSeconds as long))	
							}else{							
								weeklySubTotalsByWeek.put(weekIter,weeklyTotal.elapsedSeconds as long)
							}
						}
						if (weeklyTotalsByFunction.get(currentEmployee.function) == null){
							weeklyTotalsByFunction.put(currentEmployee.function,weeklyTotal.elapsedSeconds as long)
						}else{
							weeklyTotalsByFunction.put(currentEmployee.function,weeklyTotalsByFunction.get(currentEmployee.function) + weeklyTotal.elapsedSeconds as long)
						}
						weeklyFunctionTotalMap.put(weekIter,weeklyTotalsByFunction)
					}
				}
				siteFunctionMap.put(currentEmployee.function.name,currentEmployee.function )
				if (weeklyTotalsByWeek != null && weeklyTotalsByWeek.get(weekIter) != null && (weeklyTotalsByWeek.get(weekIter)).get(currentEmployee) != null){
					if (yearlyTotalsByEmployee != null && yearlyTotalsByEmployee.get(currentEmployee) != null){
						yearlyTotalsByEmployee.put(currentEmployee, yearlyTotalsByEmployee.get(currentEmployee) + (weeklyTotalsByWeek.get(weekIter)).get(currentEmployee))
					}else{
						yearlyTotalsByEmployee.put(currentEmployee, (weeklyTotalsByWeek.get(weekIter)).get(currentEmployee))
					}
					if (yearlyTotalsByFunction != null && yearlyTotalsByFunction.get(currentEmployee.function) != null){
						yearlyTotalsByFunction.put(currentEmployee.function, yearlyTotalsByFunction.get(currentEmployee.function) + (weeklyTotalsByWeek.get(weekIter)).get(currentEmployee))
					}else{
						yearlyTotalsByFunction.put(currentEmployee.function,(weeklyTotalsByWeek.get(weekIter)).get(currentEmployee))
					}
				}
				
			}
			if (weeklySubTotalsByWeek != null && weeklySubTotalsByWeek.get(weekIter) != null && weeklyCasesMap.get(weekIter) != null &&  weeklyCasesMap.get(weekIter).particularity1 != null){
				weeklySubTotalsByWeek.put(weekIter,weeklySubTotalsByWeek.get(weekIter) + weeklyCasesMap.get(weekIter).particularity1)
				yearlyParticularity1 += weeklyCasesMap.get(weekIter).particularity1
			}
			
			if (weeklySubTotalsByWeek != null && weeklySubTotalsByWeek.get(weekIter) != null && weeklyCasesMap.get(weekIter) != null &&  weeklyCasesMap.get(weekIter).particularity2 != null){
				weeklySubTotalsByWeek.put(weekIter,weeklySubTotalsByWeek.get(weekIter) - weeklyCasesMap.get(weekIter).particularity2)
				yearlyParticularity2 += weeklyCasesMap.get(weekIter).particularity2
			}
			if (weeklySubTotalsByWeek != null && weeklySubTotalsByWeek.get(weekIter) != null){
				yearlySubTotals += weeklySubTotalsByWeek.get(weekIter) as long
			}	
		}
		

		return [
			yearlyRemainingRTTByEmployee:yearlyRemainingRTTByEmployee,
			yearlyRemainingCAByEmployee:yearlyRemainingCAByEmployee,
			yearlyTotalsByEmployee:yearlyTotalsByEmployee,
			yearlyTotalsByFunction:yearlyTotalsByFunction,
			yearlySubTotals:yearlySubTotals,
			yearlyHomeAssistance:yearlyHomeAssistance,
			yearlyCases:yearlyCases,
			yearlyParticularity1:yearlyParticularity1,
			yearlyParticularity2:yearlyParticularity2,
			weeklyTotalsByWeek:weeklyTotalsByWeek,
			weekList:weekList,
			employeeInstanceList:employeeInstanceList,
			siteFunctionMap:siteFunctionMap,
			weeklyCasesMap:weeklyCasesMap,
			weeklySubTotalsByWeek:weeklySubTotalsByWeek,
			weeklyFunctionTotalMap:weeklyFunctionTotalMap
		]
	}
	
	def isEmployeeIn(Employee employee,Date eventDate){
		def criteria
		def isIn = false
		def upperBound
		def lowerBound
		def entriesByDay
		
		criteria = InAndOut.createCriteria()
		entriesByDay = criteria{
			and {
				eq('employee',employee)
				eq('day',eventDate.getAt(Calendar.DAY_OF_MONTH))
				eq('month',eventDate.getAt(Calendar.MONTH) + 1)
				eq('year',eventDate.getAt(Calendar.YEAR))
				order('time')
				}
		}
		
		// find upperbound
		entriesByDay.each{
			println " entriesByDay Item: $it"
			if (it > eventDate){
				upperBound = it
				return
			}
		}
		
		//find lowerbound		
		entriesByDay = entriesByDay.reverse()
		entriesByDay.each{
			println " entriesByDayReverse Item: $it"
			if (eventDate > it){
				lowerBound = it
				return
			}
		}
	
		isIn = (lowerBound.type.equals('S') && upperBound.type.equals('E')) ? true : false		
		return isIn
	}
	
	def isEmployeeIn2(Employee employee,Date eventDate,Date lowerBound,Date upperBound){
		def criteria
		def isIn = false
		def entriesByDay
		
		criteria = InAndOut.createCriteria()
		entriesByDay = criteria{
			and {
				eq('employee',employee)
				eq('day',eventDate.getAt(Calendar.DAY_OF_MONTH))
				eq('month',eventDate.getAt(Calendar.MONTH) + 1)
				eq('year',eventDate.getAt(Calendar.YEAR))
				order('time')
				}
		}	
	}
	
	def getDailyTotalWithIntervals(Date currentDate, siteId){
		def pas = 10 as long
		def minuteStart = 30
		def hourStart = 6
		def minuteEnd = 22
		def hourEnd = 30
		def initialMinute
		def calendar = Calendar.instance
		def startDate = calendar.time
		def startCalendar = Calendar.instance
		def endDate = calendar.time
		def endCalendar = Calendar.instance
		def rollingDate = calendar.time
		def rollingCalendar = Calendar.instance
		def dateList = []
		def dateList2 = []
		def employeeSiteList = []
		def criteria
		def statusMapByEmployee = [:]
		def statusMapByTime = [:]
		def mapByTime = [:]
		def listByEmployee = []
		def entriesByDay
		def eventIndexList
		def eventMinute
		def eventIndex
		def status
		def i
		def statusMap
		def model
		def site

		if (siteId != null && !siteId.equals("")){
			site = Site.get(siteId)
		}
		
		employeeSiteList = Employee.findAllBySite(site)
		currentDate.clearTime()
		startDate.putAt(Calendar.HOUR_OF_DAY,hourStart)
		startDate.putAt(Calendar.MINUTE,minuteStart)
		startCalendar.time = startDate
		startCalendar.time.clearTime()
		initialMinute = hourStart*60+minuteStart
		
		endDate.putAt(Calendar.HOUR_OF_DAY,19)
		endDate.putAt(Calendar.MINUTE,30)
		endCalendar.time = endDate
		endCalendar.time.clearTime()
		
		rollingDate.putAt(Calendar.HOUR_OF_DAY,hourStart)
		rollingDate.putAt(Calendar.MINUTE,minuteStart)
		rollingCalendar.time = rollingDate
		rollingCalendar.time.clearTime()
		
		while (rollingCalendar.time < endCalendar.time){
			log.debug('rollingDate: '+rollingCalendar.time.format('HH:mm'))
			dateList.add(rollingCalendar.time.format('HH:mm'))
			dateList2.add(rollingCalendar.time)
			if (rollingCalendar.time == endCalendar.time){
				//last occurence
				rollingCalendar.setTimeInMillis(rollingCalendar.getTimeInMillis() + pas*60*1000)
				dateList.add(rollingCalendar.time.format('HH:mm'))
				dateList2.add(rollingCalendar.time)
				break
			}
			rollingCalendar.setTimeInMillis(rollingCalendar.getTimeInMillis() + pas*60*1000)
		}
					
		for (Employee employee:employeeSiteList){
			criteria = InAndOut.createCriteria()
				entriesByDay = criteria{
				and {
					eq('employee',employee)
					eq('day',currentDate.getAt(Calendar.DAY_OF_MONTH))
					eq('month',currentDate.getAt(Calendar.MONTH) + 1)
					eq('year',currentDate.getAt(Calendar.YEAR))
					order('time')
					}
			}
			eventIndexList = []
			for (InAndOut eventIter in entriesByDay){
				Date eventDate = eventIter.time
				eventMinute = eventDate.getAt(Calendar.HOUR_OF_DAY)*60 + eventDate.getAt(Calendar.MINUTE) - initialMinute
				log.debug('eventDate: '+eventDate.format('HH:mm'))
				eventIndex = (int)(eventMinute / pas)
				log.debug("eventIndex: "+eventIndex)
				if (eventIndexList != null && eventIndexList.size() > 0 && eventIndexList.last() == eventIndex){
					eventIndexList.remove(eventIndexList.size() - 1)
				}else{
					eventIndexList.add(eventIndex)
				}
			}			
			status = false
			i = 0
			statusMap = [:]
			for (Date date in dateList2){
				if (eventIndexList.contains(i)){
					status = (status == false) ? true : false
				}
				statusMap.put(date,status)
				i++
				if (statusMapByTime != null && statusMapByTime.get(date) != null){
					def tmpList = statusMapByTime.get(date)
					tmpList.add(status)
					statusMapByTime.put(date,tmpList)
				}else{
					statusMapByTime.put(date,[status])
				}	
			}
			statusMapByEmployee.put(employee,statusMap)
		}
		
		model = getDailyInAndOutsData(site.id, currentDate)		
		model << [
			isWeekScheduleView:true,
			dateList:dateList,
			employeeSiteList:employeeSiteList,
			statusMapByEmployee:statusMapByEmployee,
			statusMapByTime:statusMapByTime,
			dateList2:dateList2
		]
		return model
	}
	
}
