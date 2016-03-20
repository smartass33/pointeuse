package pointeuse

import groovy.time.TimeDuration;
import groovy.time.TimeCategory;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

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
		
		criteria = BankHoliday.createCriteria()
		bankHolidays = criteria.list {
			or{
				and {
					eq('year',year)
					eq('month',calendar.get(Calendar.MONTH)+1)
				}
				
				and{
					eq('year',year)
					eq('week',calendar.get(Calendar.WEEK_OF_YEAR))
					
				}		
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
	
	
	static test(long inputSeconds){
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
		
		//return [hours,minutes,seconds]
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
		def yearlyDif = criteria.list {
			and {
				eq('employee',employee)
				ge('date',minDate)
				lt('date',maxDate)
				eq('type',AbsenceType.DIF)
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
						
		if (month>=6){
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
			yearlyExceptionnel:yearlyExceptionnel.size(),
			yearlyPaternite:yearlyPaternite.size(),		
			yearlyDif:yearlyDif.size(),	
			yearlyRtt:yearlyRtt.size(),
			yearlySickness:yearlySickness.size(),
			yearlyTheoritical:yearTheoritical,
			yearlyPregnancyCredit:yearlyPregnancyCredit,
			yearlyTotalTime:totalTime,
			yearlySansSolde:yearlySansSolde.size(),
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
		def exceptionnel = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.EXCEPTIONNEL)
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
		def dif = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',year)
				eq('month',month)
				eq('type',AbsenceType.DIF)
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

		def cartoucheMap=[
			isCurrentMonth:isCurrentMonth,
			currentContract:currentContract,
			employeeInstance:employeeInstance,
			workingDays:counter,
			holidays:holidays.size(),
			exceptionnel:exceptionnel.size(),
			paternite:paternite.size(),			
			dif:dif.size(),
			rtt:rtt.size(),
			sickness:sickness.size(),
			sansSolde:sansSolde.size(),
			injustifie:injustifie.size(),
			monthTheoritical:monthTheoritical,
			pregnancyCredit:pregnancyCredit,
			monthTheoriticalHuman:getTimeAsText(computeHumanTime(monthTheoritical),false),
			calendar:calendar
		]
		def mergedMap = cartoucheMap << yearlyCartouche
		return mergedMap
	}

	
	def getReportData(String siteId,Employee employee, Date myDate,int monthPeriod,int yearPeriod){
		def calendar = Calendar.instance
		def monthlyTotalTimeByEmployee = [:]
		def criteria

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
		def data = computeWeeklyTotals( employee,  month,  year)
		def cartoucheTable = getCartoucheData(employee,year,month)
		def currentContract = cartoucheTable.get('currentContract')
		def workingDays=cartoucheTable.get('workingDays')
		def holiday=cartoucheTable.get('holidays')
		def dif=cartoucheTable.get('dif')	
		def exceptionnel=cartoucheTable.get('exceptionnel')
		def paternite=cartoucheTable.get('paternite')		
		def rtt=cartoucheTable.get('rtt')
		def isCurrentMonth=cartoucheTable.get('isCurrentMonth')	
		def sickness=cartoucheTable.get('sickness')
		def sansSolde=cartoucheTable.get('sansSolde')
		def injustifie=cartoucheTable.get('injustifie')	
		def monthTheoritical = cartoucheTable.get('monthTheoritical')
		def pregnancyCredit = computeHumanTimeAsString(cartoucheTable.get('pregnancyCredit'))
		def yearlyHoliday=cartoucheTable.get('yearlyHolidays')
		def yearlyDif=cartoucheTable.get('yearlyDif')
		def yearlyExceptionnel=cartoucheTable.get('yearlyExceptionnel')	
		def yearlyPaternite=cartoucheTable.get('yearlyPaternite')
		def yearlyRtt=cartoucheTable.get('yearlyRtt')
		def yearlySickness=cartoucheTable.get('yearlySickness')
		def yearlyTheoritical = computeHumanTimeAsString(cartoucheTable.get('yearlyTheoritical'))
		def yearlyPregnancyCredit = computeHumanTimeAsString(cartoucheTable.get('yearlyPregnancyCredit'))
		def yearlyActualTotal = computeHumanTimeAsString(cartoucheTable.get('yearlyTotalTime'))
		def yearlySansSolde=cartoucheTable.get('yearlySansSolde')
		def yearlyInjustifie=cartoucheTable.get('yearlyInjustifie')
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
		def payableCompTime = computeHumanTime(0)
		if (currentContract != null && currentContract.weeklyLength != Employee.legalWeekTime && data.get('monthlyTotalTime') > monthTheoritical){
			payableCompTime = computeHumanTime(Math.round(Math.max(data.get('monthlyTotalTime')-monthTheoritical-data.get('monthlySupTime'),0)))
		}
		def monthlySupTime = computeHumanTime(data.get('monthlySupTime') as long)
		
		monthlyTotalTimeByEmployee.put(employee, computeHumanTime(data.get('monthlyTotalTime')))
		def monthlyTotal=computeHumanTime(data.get('monthlyTotalTime'))
		monthTheoritical = computeHumanTime(cartoucheTable.get('monthTheoritical'))
		def monthlyTheoriticalAsString = computeHumanTimeAsString(cartoucheTable.get('monthTheoritical'))
		Period period = (month>5)?Period.findByYear(year):Period.findByYear(year - 1)	
		def initialCA = employeeService.getInitialCA(employee,(month>5)?Period.findByYear(year):Period.findByYear(year - 1))
		def initialRTT = employeeService.getInitialRTT(employee,(month>5)?Period.findByYear(year):Period.findByYear(year - 1))
		def departureDate
		if (employee.status.date != null){
			if (employee.status.date != null && employee.status.date <= calendarLoop.time){
				departureDate = employee.status.date
			}
		}	
		
		def monthlySupTimeDecimal=(monthlySupTime.get(0)+monthlySupTime.get(1)/60).setScale(2,2)
				
		def timeBefore7Decimal=computeHumanTime(data.get('timeBefore7') as long)
		timeBefore7Decimal=(timeBefore7Decimal.get(0)+timeBefore7Decimal.get(1)/60).setScale(2,2)
		
		def timeAfter20Decimal=computeHumanTime(data.get('timeAfter20') as long)
		timeAfter20Decimal=(timeAfter20Decimal.get(0)+timeAfter20Decimal.get(1)/60).setScale(2,2)
		
		def timeOffHoursDecimal=computeHumanTime(data.get('timeOffHours') as long)
		timeOffHoursDecimal= (timeOffHoursDecimal.get(0)+timeOffHoursDecimal.get(1)/60).setScale(2,2)
		
		return [
			yearOpenDays:yearOpenDays,
			monthlySupTime:getTimeAsText(monthlySupTime,false),
			timeBefore7:getTimeAsText(computeHumanTime(data.get('timeBefore7')),false),
			timeAfter20:getTimeAsText(computeHumanTime(data.get('timeAfter20')),false),	
			timeOffHours:getTimeAsText(computeHumanTime(data.get('timeOffHours')),false),
			monthlySupTimeDecimal:monthlySupTimeDecimal,
			timeBefore7Decimal:timeBefore7Decimal,
			timeAfter20Decimal:timeAfter20Decimal,
			timeOffHoursDecimal:timeOffHoursDecimal,
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
			sansSolde:sansSolde,			
			injustifie:injustifie,
			yearlyActualTotal:yearlyActualTotal,
			monthTheoritical:monthTheoritical,
			monthlyTheoriticalAsString:monthlyTheoriticalAsString,
			pregnancyCredit:pregnancyCredit,
			yearlyPregnancyCredit:yearlyPregnancyCredit,
			yearlyHoliday:yearlyHoliday,
			yearlyRtt:yearlyRtt,
			yearlySickness:yearlySickness,
			yearlySansSolde:yearlySansSolde,
			yearlyInjustifie:yearlyInjustifie,
			yearlySupTime:computeHumanTimeAsString(yearlySupTime as long),
			dif:dif,
			yearlyDif:yearlyDif,	
			exceptionnel:exceptionnel,
			yearlyExceptionnel:yearlyExceptionnel,
			paternite:paternite,
			yearlyPaternite:yearlyPaternite,
			yearlyTheoritical:yearlyTheoritical,
			monthlyTotal:monthlyTotalTimeByEmployee,
			weeklyTotal:data.get('weeklyTotalTimeByEmployee'),
			weeklySupTotal:data.get('weeklySupTotalTimeByEmployee'),
			dailySupTotalMap:data.get('dailySupTotalMap'),
			dailyTotalMap:data.get('dailyTotalMap'),
			holidayMap:data.get('holidayMap'),
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
				
				monthlyTheoriticalMap.put(month, data.get('monthTheoritical') + theoricalTime2add)
				if (totalMonthlyTheoritical.get(month) == null){
					totalMonthlyTheoritical.put(month, monthlyTheoriticalMap.get(month))
				}else{
					totalMonthlyTheoritical.put(month, totalMonthlyTheoritical.get(month) + monthlyTheoriticalMap.get(month))			
				}
				if (monthlyTotalInstance!=null){				
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
				currentContract = data.get('currentContract')	
				if (currentContract != null){
					ecartMinusRTTMap.put(month, ecartMap.get(month)-(3600*(monthlyTakenRTTMap.get(month))*(currentContract.weeklyLength/Employee.WeekOpenedDays)) as long)
					ecartMinusRTTAndHSMap.put(month, (ecartMap.get(month)-(3600*(monthlyTakenRTTMap.get(month))*(currentContract.weeklyLength/Employee.WeekOpenedDays)) - monthlySupTimeMap.get(month))as long)
					
				}else{
					log.error('currentContract is null for employee  '+employee+ ' and month= '+month)
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
			
			
		/*
			monthlyTheoriticalMap.each() {
				it.value=getTimeAsText(computeHumanTime(it.value),false)
			}
			monthlyActualMap.each() {
				it.value=getTimeAsText(computeHumanTime(it.value),false)
		   }
			ecartMap.each() {
				it.value=getTimeAsText(computeHumanTime(it.value),false)
		   }
			ecartMinusRTTMap.each() {
				it.value=getTimeAsText(computeHumanTime(it.value),false)
		   }
			
			ecartMinusRTTAndHSMap.each() {
				it.value=getTimeAsText(computeHumanTime(it.value),false)
		   }
			monthlySupTimeMap.each(){
				it.value=getTimeAsText(computeHumanTime(it.value),false)
			}
			
			*/
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
			totalTakenRTT:totalTakenRTT
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
		def annualRTT = 0
		def annualCSS = 0
		def annualINJUSTIFIE = 0
		def annualDIF = 0
		def annualSickness = 0
		def annualExceptionnel = 0	
		def annualPaternite = 0	
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
			//def  weeklyTotals =  computeWeeklyTotals( employee, currentMonth, currentYear)
			def currentPeriod = (currentMonth < 6) ? Period.findByYear(currentYear-1) : Period.findByYear(currentYear)
			
			criteria = SupplementaryTime.createCriteria()
			log.debug('getting monthlySupTotalTime for: '+employee+' and month: '+currentMonth+' and period: '+currentPeriod)
			monthlySupTotalTime = criteria.get {
				and {
					eq('employee',employee)
					eq('month',currentMonth)
					eq('period',currentPeriod)
				}
			}
			log.debug('monthlySupTotalTime: '+monthlySupTotalTime)
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
			monthlyTakenHolidays.put(currentMonth, initialCA - annualHoliday)
			annualRTT += cartoucheTable.getAt('rtt')
			annualDIF += cartoucheTable.getAt('dif')			
			annualCSS += cartoucheTable.getAt('sansSolde')
			annualINJUSTIFIE += cartoucheTable.getAt('injustifie')
			annualSickness += cartoucheTable.getAt('sickness')
			annualExceptionnel += cartoucheTable.getAt('exceptionnel')
			annualPaternite += cartoucheTable.getAt('paternite')			
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
			annualRTT:annualRTT,
			annualDIF:annualDIF,
			annualCSS:annualCSS,
			annualINJUSTIFIE:annualINJUSTIFIE,
			annualSickness:annualSickness,
			annualExceptionnel:annualExceptionnel,
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
		def annualRTT = 0
		def annualCSS = 0
		def annualINJUSTIFIE = 0
		def annualDIF = 0
		def annualSickness = 0
		def annualExceptionnel = 0
		def annualPaternite = 0
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
			monthlyTakenHolidays.put(currentMonth, initialCA - annualHoliday)
			annualRTT += cartoucheTable.getAt('yearlyRtt')
			annualDIF += cartoucheTable.getAt('yearlyDif')
			annualCSS += cartoucheTable.getAt('yearlySansSolde')
			annualINJUSTIFIE += cartoucheTable.getAt('yearlyInjustifie')			
			annualSickness += cartoucheTable.getAt('yearlySickness')
			annualExceptionnel += cartoucheTable.getAt('yearlyExceptionnel')
			annualPaternite += cartoucheTable.getAt('yearlyPaternite')
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
			annualRTT:annualRTT,
			annualDIF:annualDIF,
			annualCSS:annualCSS,
			annualINJUSTIFIE:annualINJUSTIFIE,
			annualSickness:annualSickness,
			annualExceptionnel:annualExceptionnel,
			annualPaternite:annualPaternite,
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
			
			data = computeWeeklyTotals( employee,  currentMonth,  (currentMonth < 6) ? year + 1 : year)
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
				log.debug('realOpenDays*weeklyContractTime/Employee.WeekOpenedDays: '+realOpenDays*weeklyContractTime/Employee.WeekOpenedDays)
				log.debug('(Employee.Pentecote)*((realOpenDays - absenceMap.get(AbsenceType.CSS))/totalNumberOfDays)*(weeklyContractTime/Employee.legalWeekTime): '+(Employee.Pentecote)*((realOpenDays - absenceMap.get(AbsenceType.CSS))/totalNumberOfDays)*(weeklyContractTime/Employee.legalWeekTime))
				log.debug('(weeklyContractTime/Employee.WeekOpenedDays)*(absenceMap.get(AbsenceType.MALADIE)+absenceMap.get(AbsenceType.VACANCE)+absenceMap.get(AbsenceType.CSS)+absenceMap.get(AbsenceType.EXCEPTIONNEL)+absenceMap.get(AbsenceType.DIF)): '+(weeklyContractTime/Employee.WeekOpenedDays)*(absenceMap.get(AbsenceType.MALADIE)+absenceMap.get(AbsenceType.VACANCE)+absenceMap.get(AbsenceType.CSS)+absenceMap.get(AbsenceType.EXCEPTIONNEL)+absenceMap.get(AbsenceType.DIF)))
				log.debug('(35/7)*absenceMap.get(AbsenceType.PATERNITE): '+(35/7)*absenceMap.get(AbsenceType.PATERNITE))
				log.debug('(weeklyContractTime/Employee.WeekOpenedDays)*paterniteSunday: '+(weeklyContractTime/Employee.WeekOpenedDays)*paterniteSunday)
				log.debug('absenceMap.get(AbsenceType.GROSSESSE)): '+absenceMap.get(AbsenceType.GROSSESSE))
			
				if (isOut){
					monthTheoritical += 0
				}else{
					monthTheoritical += (
						3600*(
								realOpenDays*weeklyContractTime/Employee.WeekOpenedDays
								+(Employee.Pentecote)*((realOpenDays - absenceMap.get(AbsenceType.CSS) - absenceMap.get(AbsenceType.INJUSTIFIE))/totalNumberOfDays)*(weeklyContractTime/Employee.legalWeekTime)
								-(weeklyContractTime/Employee.WeekOpenedDays)*(absenceMap.get(AbsenceType.MALADIE)+absenceMap.get(AbsenceType.VACANCE)+absenceMap.get(AbsenceType.CSS)+absenceMap.get(AbsenceType.INJUSTIFIE)+absenceMap.get(AbsenceType.EXCEPTIONNEL)+absenceMap.get(AbsenceType.DIF))
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
	
	def getDailyInAndOutsData(Site site,Date currentDate){
		def dailyMap = [:]
		def dailySupMap = [:]
		def dailyInAndOutMap = [:]
		def dailyTotal
		def inAndOutList
		def criteria
		def elapsedSeconds
		//def employeeInstanceList
		def calendar = Calendar.instance
		calendar.time=currentDate
		def employeeInstanceList = []		
		def functionList = Function.list([sort: "ranking", order: "asc"])
		 for (Function function:functionList){
			 employeeInstanceList.addAll(Employee.findAllBySiteAndFunction(site,function))
		 }
		
		
		//employeeInstanceList = Employee.findAllBySite(site)
		for (Employee employee:employeeInstanceList){
			criteria = DailyTotal.createCriteria()
			dailyTotal= criteria.get{
				and {
					eq('employee',employee)
					eq('week',calendar.get(Calendar.WEEK_OF_YEAR))
					eq('year',calendar.get(Calendar.YEAR))
					eq('day',calendar.get(Calendar.DAY_OF_MONTH))
				}
			}
			criteria = InAndOut.createCriteria()
			inAndOutList= criteria.list{
				and {
						eq('employee',employee)
						eq('week',calendar.get(Calendar.WEEK_OF_YEAR))
						eq('day',calendar.get(Calendar.DAY_OF_MONTH))
						eq('month',calendar.get(Calendar.MONTH)+1)
						eq('year',calendar.get(Calendar.YEAR))
						order('time')
					}
			}		
			dailyInAndOutMap.put(employee, inAndOutList)

			elapsedSeconds = dailyTotal!=null ? (getDailyTotal(dailyTotal)).get("elapsedSeconds") : 0
			if (elapsedSeconds > DailyTotal.maxWorkingTime){
				dailySupMap.put(employee,getTimeAsText(computeHumanTime(elapsedSeconds-DailyTotal.maxWorkingTime),false))
			}else{
				dailySupMap.put(employee,getTimeAsText(computeHumanTime(0),false))
			}
			dailyMap.put(employee,getTimeAsText(computeHumanTime(elapsedSeconds),false))
		}	
		return [dailyMap: dailyMap,site:site,dailySupMap:dailySupMap,dailyInAndOutMap:dailyInAndOutMap,currentDate:currentDate]
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
		tmpCalendar.set(Calendar.DAY_OF_YEAR,tmpCalendar.get(Calendar.DAY_OF_YEAR)-4)
		
		def inAndOuts = inAndOutsCriteria.list {
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
			if (max>0){
			def  lastEvent = inAndOuts.get(max-1)
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
		def siteRemainingCA = 0
		def siteAnnualRTT = 0
		def siteAnnualCSS = 0
		def siteAnnualINJUSTIFIE = 0
		def siteAnnualSickness = 0
		def siteAnnualExceptionnel = 0
		def siteAnnualPaternite = 0	
		def siteAnnualDIF = 0
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
			siteRemainingCA += ((annualReportMap.get(employee)).get('remainingCA'))
			siteAnnualRTT += ((annualReportMap.get(employee)).get('annualRTT'))
			siteAnnualCSS += ((annualReportMap.get(employee)).get('annualCSS'))
			siteAnnualINJUSTIFIE += ((annualReportMap.get(employee)).get('annualINJUSTIFIE'))
			siteAnnualSickness += ((annualReportMap.get(employee)).get('annualSickness'))
			siteAnnualDIF += ((annualReportMap.get(employee)).get('annualDIF'))
			siteAnnualExceptionnel += ((annualReportMap.get(employee)).get('annualExceptionnel'))
			siteAnnualPaternite += ((annualReportMap.get(employee)).get('annualPaternite'))
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
			siteRemainingCA:siteRemainingCA,
			siteAnnualRTT:siteAnnualRTT,
			siteAnnualCSS:siteAnnualCSS,
			siteAnnualINJUSTIFIE:siteAnnualINJUSTIFIE,
			siteAnnualSickness:siteAnnualSickness,
			siteAnnualDIF:siteAnnualDIF,
			siteAnnualExceptionnel:siteAnnualExceptionnel,
			siteAnnualPaternite:siteAnnualPaternite,	
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
	//	log.error('computeWeeklyTotals executed in '+executionTime+' for employee: '+employee.lastName)
		
		//log.error('timeBefore7 for month and year: '+timeBefore7+', month: '+month+',year: '+year)
		//log.error('timeAfter20 for month and year: '+timeAfter20+', month: '+month+',year: '+year)
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
	
	def computeWeeklyTotals(Employee employee, int month, int year){
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
			
				if (!isSunday && calendarLoop.get(Calendar.WEEK_OF_YEAR)==lastWeekParam.get(0) ){
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
			if (dailyTotal==null && calendarLoop.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
				if (calendarLoop.get(Calendar.WEEK_OF_YEAR)==lastWeekParam.get(0) ){
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
			if 	(entriesByDay.size()>0){
				if (dailyTotal!=null){
					dailyTotalMap.put(tmpDate, getTimeAsText(computeHumanTime(dailySeconds),false))
					dailySupTotalMap.put(tmpDate, getTimeAsText(computeHumanTime(Math.max(dailySeconds-DailyTotal.maxWorkingTime,0)),false))
				}else {
					dailyTotalMap.put(tmpDate, getTimeAsText(computeHumanTime(0),false))
					dailySupTotalMap.put(tmpDate, getTimeAsText(computeHumanTime(Math.max(dailySeconds-DailyTotal.maxWorkingTime,0)),false))
				}
				mapByDay.put(tmpDate, entriesByDay)
			}
			else{
				dailyTotalMap.put(tmpDate, getTimeAsText(computeHumanTime(0),false))
				dailySupTotalMap.put(tmpDate, getTimeAsText(computeHumanTime(0),false))
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
			if (supTime.amountPaid == null){
				supTime.amountPaid = 0
			}
		}

		supTime.save(flush: true)
		endDate = new Date()
		use (TimeCategory){executionTime=endDate-startDate}
		log.debug('computeWeeklyTotals executed in '+executionTime+' for employee: '+employee.lastName)
		
		//log.error('timeBefore7 for month and year: '+timeBefore7+', month: '+month+',year: '+year)
		//log.error('timeAfter20 for month and year: '+timeAfter20+', month: '+month+',year: '+year)
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
	
	
	
	def getYearSupTime(Employee employee,int year,int month){
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
				data = computeWeeklyTotals( employee, calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
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
				data = computeWeeklyTotals( employee, calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
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
				data = computeWeeklyTotals( employee, calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
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
		//monthlySupTime=getTimeAsText(computeHumanTime(monthlySupTime as long),false)
		
		def monthlySupTimeDecimal=computeHumanTime(monthlySupTime as long)
		monthlySupTimeDecimal=(monthlySupTimeDecimal.get(0)+monthlySupTimeDecimal.get(1)/60).setScale(2,2)
			
		def timeBefore7Decimal = computeHumanTime(timeBefore7 as long) 
		timeBefore7Decimal=(timeBefore7Decimal.get(0)+timeBefore7Decimal.get(1)/60).setScale(2,2)
		
		def timeAfter20Decimal = computeHumanTime(timeAfter20 as long) 
		timeAfter20Decimal=(timeAfter20Decimal.get(0)+timeAfter20Decimal.get(1)/60).setScale(2,2)
		
		def timeOffHoursDecimal = computeHumanTime(timeOffHours as long)
		timeOffHoursDecimal= (timeOffHoursDecimal.get(0)+timeOffHoursDecimal.get(1)/60).setScale(2,2)
		
		def ajaxYearlySupTimeDecimal = computeHumanTime(yearSupTime as long)
		ajaxYearlySupTimeDecimal= (ajaxYearlySupTimeDecimal.get(0)+ajaxYearlySupTimeDecimal.get(1)/60).setScale(2,2)
		
		def yearTimeBefore7Decimal =computeHumanTime(yearTimeBefore7 as long)
		yearTimeBefore7Decimal= (yearTimeBefore7Decimal.get(0)+yearTimeBefore7Decimal.get(1)/60).setScale(2,2)
		
		def yearTimeAfter20Decimal = computeHumanTime(yearTimeAfter20 as long) 
		yearTimeAfter20Decimal= (yearTimeAfter20Decimal.get(0)+yearTimeAfter20Decimal.get(1)/60).setScale(2,2)
		
		def yearTimeOffHoursDecimal = computeHumanTime(yearTimeOffHours as long)
		yearTimeOffHoursDecimal=(yearTimeOffHoursDecimal.get(0)+yearTimeOffHoursDecimal.get(1)/60).setScale(2,2)
			
		return [
			monthlySupTime:getTimeAsText(computeHumanTime(monthlySupTime as long),false),
			timeBefore7:getTimeAsText(computeHumanTime(timeBefore7 as long),false),
			timeAfter20:getTimeAsText(computeHumanTime(timeAfter20 as long),false),
			timeOffHours:getTimeAsText(computeHumanTime(timeOffHours as long),false),
			ajaxYearlySupTime:getTimeAsText(computeHumanTime(yearSupTime as long),false),
			yearTimeBefore7:getTimeAsText(computeHumanTime(yearTimeBefore7 as long),false),
			yearTimeAfter20:getTimeAsText(computeHumanTime(yearTimeAfter20 as long),false),
			yearTimeOffHours:getTimeAsText(computeHumanTime(yearTimeOffHours as long),false),			
			monthlySupTimeDecimal:monthlySupTimeDecimal,
			timeBefore7Decimal:timeBefore7Decimal,
			timeAfter20Decimal:timeAfter20Decimal,
			timeOffHoursDecimal:timeOffHoursDecimal,
			ajaxYearlySupTimeDecimal:ajaxYearlySupTimeDecimal,
			yearTimeBefore7Decimal:yearTimeBefore7Decimal,
			yearTimeAfter20Decimal:yearTimeAfter20Decimal,
			yearTimeOffHoursDecimal:yearTimeOffHoursDecimal
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
				//data = computeOffTimeTotals( employee, calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
				
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
				//data = computeOffTimeTotals( employee, calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
				
				
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
				//data = computeOffTimeTotals( employee, calendarIter.get(Calendar.MONTH)+1, calendarIter.get(Calendar.YEAR))
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
			timeBefore7:getTimeAsText(computeHumanTime(timeBefore7 as long),false),
			timeAfter20:getTimeAsText(computeHumanTime(timeAfter20 as long),false),
			timeOffHours:getTimeAsText(computeHumanTime(timeOffHours as long),false),
			yearTimeBefore7:getTimeAsText(computeHumanTime(yearTimeBefore7 as long),false),
			yearTimeAfter20:getTimeAsText(computeHumanTime(yearTimeAfter20 as long),false),
			yearTimeOffHours:getTimeAsText(computeHumanTime(yearTimeOffHours as long),false),
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
		def exceptionnel = []
		def paternite = []
		def dif = []
		def rtt = []
		def sickness = []
		def pregnancy = []
		def sansSolde = []
		def injustifie = []
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
			holidays = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.VACANCE])
		}

		Absence.withTransaction{
			exceptionnel = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.EXCEPTIONNEL])
		}

		Absence.withTransaction{
			paternite = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.PATERNITE])
		}
		
		Absence.withTransaction{
			dif = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.DIF])
		}
		
		Absence.withTransaction{
			sansSolde = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.CSS])
		}
		
		Absence.withTransaction{
			injustifie = Absence.findAll("from Absence as a where a.employee = :employee and a.year = :year and month = :month  and type = :type",[employee:employeeInstance,year:year,month:month,type:AbsenceType.INJUSTIFIE])
		}
		
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		calendar.set(Calendar.DATE,1)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.MONTH,month-1)
		def yearlyCartouche=getYearCartoucheDataMultiThread(employeeInstance,year,month)
		
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
		

		def cartoucheMap=[
			isCurrentMonth:isCurrentMonth,
			currentContract:currentContract,
			employeeInstance:employeeInstance,
			workingDays:counter,
			holidays:holidays.size(),
			exceptionnel:exceptionnel.size(),
			paternite:paternite.size(),
			dif:dif.size(),
			rtt:rtt.size(),
			sickness:sickness.size(),
			sansSolde:sansSolde.size(),
			injustifie:injustifie.size(),
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
		def yearlyExceptionnel = []
		def yearlyPaternite = []
		def yearlyDif = []
		def yearlyRtt = []
		def yearlySickness = []
		def pregnancy = []
		def yearlySansSolde = []
		def yearlyInjustifie = []
		
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
			yearlyExceptionnel = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.EXCEPTIONNEL])
		}

		Absence.withTransaction{
			yearlyPaternite = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.PATERNITE])
		}

		Absence.withTransaction{
			yearlyDif = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.DIF])
		}
		
		Absence.withTransaction{
			yearlyRtt = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.RTT])
		}

		Absence.withTransaction{
			yearlySickness = Absence.findAll("from Absence as a where a.employee = :employee and date >= :minDate and date < :maxDate  and type = :type",[employee:employee,minDate:minDate,maxDate:maxDate,type:AbsenceType.MALADIE])
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
						
		if (month>=6){			
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
			yearlyExceptionnel:yearlyExceptionnel.size(),
			yearlyPaternite:yearlyPaternite.size(),
			yearlyDif:yearlyDif.size(),
			yearlyRtt:yearlyRtt.size(),
			yearlySickness:yearlySickness.size(),
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
				
			//previousContracts.addAll(Contract.findAll("from Contract where employee = :employee and endDate >= :startDate and endDate <= :endDate and endDate is not null order by startDate desc",[employee:employee,startDate:startCalendar.time,endDate:endCalendar.time]))
			//previousContracts .addAll(Contract.findAll("from Contract where employee = :employee and startDate <= :endDate and endDate is null order by startDate desc",[employee:employee,endDate:startCalendar.time]))
			//previousContracts.addAll(Contract.findAll("from Contract where employee = :employee and startDate >= :startDate and startDate <= :endDate order by startDate desc",[employee:employee,startDate:startCalendar.time,endDate:endCalendar.time]))
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

							
						if ( absenceMap.get(AbsenceType.VACANCE) != null )
							multiplier += absenceMap.get(AbsenceType.VACANCE) as long
							
						if ( absenceMap.get(AbsenceType.CSS) != null )
							multiplier += absenceMap.get(AbsenceType.CSS) as long
							
						if ( absenceMap.get(AbsenceType.INJUSTIFIE) != null )
							multiplier += absenceMap.get(AbsenceType.INJUSTIFIE) as long

						if ( absenceMap.get(AbsenceType.EXCEPTIONNEL) != null )
							multiplier += absenceMap.get(AbsenceType.EXCEPTIONNEL) as long
							
						if ( absenceMap.get(AbsenceType.DIF) != null )
							multiplier += absenceMap.get(AbsenceType.DIF)

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
						
						/*
						monthTheoritical += (
							3600*(
									realOpenDays*weeklyContractTime/Employee.WeekOpenedDays
									+(Employee.Pentecote)*((realOpenDays - absenceMap.get(AbsenceType.CSS))/totalNumberOfDays)*(weeklyContractTime/Employee.legalWeekTime)
									-(weeklyContractTime/Employee.WeekOpenedDays)*(absenceMap.get(AbsenceType.MALADIE)+absenceMap.get(AbsenceType.VACANCE)+absenceMap.get(AbsenceType.CSS)+absenceMap.get(AbsenceType.EXCEPTIONNEL)+absenceMap.get(AbsenceType.DIF))
									- (35/7)*absenceMap.get(AbsenceType.PATERNITE)
								)
								- absenceMap.get(AbsenceType.GROSSESSE)) as int
							*/
					}
				}
				log.debug('monthTheoritical: '+monthTheoritical)
			}
			
			}
		/*
		Contract.withTransaction{
			previousContracts.add(Contract.findAll("from Contract where employee = :employee and endDate >= :startDate and endDate <= :endDate and endDate is not null order by startDate desc",[employee:employee,startDate:startCalendar.time,endDate:endCalendar.time]))
		}

		Contract.withTransaction{
			previousContracts.add(Contract.findAll("from Contract where employee = :employee and startDate <= :endDate and endDate is null order by startDate desc",[employee:employee,endDate:startCalendar.time]))
		}
		
		Contract.withTransaction{
			previousContracts.add(Contract.findAll("from Contract where employee = :employee and startDate >= :startDate and startDate <= :endDate order by startDate desc",[employee:employee,startDate:startCalendar.time,endDate:endCalendar.time]))
		}
			*/	


		return monthTheoritical
		
	}
	
	def getAbsencesBetweenDatesMultiThread(Employee employee,Date startDate,Date endDate){
		def absenceMap = [:]
		def sickness = []
		def holidays = []
		def exceptionnel = []
		def paternite = []
		def dif = []
		def sansSolde = []
		def injustifie = []
		def pregnancy = []
		def pregnancyCredit = 0
		def patSundayCount = 0
		
		Absence.withTransaction{
			sickness = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.MALADIE])
			absenceMap.put(AbsenceType.MALADIE, sickness.size())
		}

		Absence.withTransaction{
			holidays = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.VACANCE])
			absenceMap.put(AbsenceType.MALADIE, holidays.size())
		}

		Absence.withTransaction{
			exceptionnel = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.EXCEPTIONNEL])
			absenceMap.put(AbsenceType.EXCEPTIONNEL, exceptionnel.size())
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
			sansSolde = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.CSS])
			absenceMap.put(AbsenceType.CSS, sansSolde.size())
		}
		
		Absence.withTransaction{
			injustifie = Absence.findAll("from Absence as a where a.employee = :employee and date >= :startDate and date <= :endDate  and type = :type",[employee:employee,startDate:startDate,endDate:endDate,type:AbsenceType.INJUSTIFIE])
			absenceMap.put(AbsenceType.INJUSTIFIE, injustifie.size())
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
		def siteRemainingCA = 0
		def siteAnnualRTT = 0
		def siteAnnualCSS = 0
		def siteAnnualINJUSTIFIE = 0
		def siteAnnualSickness = 0
		def siteAnnualExceptionnel = 0
		def siteAnnualPaternite = 0
		def siteAnnualDIF = 0
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
				 siteRemainingCA += data.get('remainingCA')
				 siteAnnualRTT += data.get('annualRTT')
				 siteAnnualCSS += data.get('annualCSS')
				 siteAnnualINJUSTIFIE += data.get('annualINJUSTIFIE')
				 siteAnnualSickness += data.get('annualSickness')
				 siteAnnualDIF += data.get('annualDIF')
				 siteAnnualExceptionnel += data.get('annualExceptionnel')
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
			siteRemainingCA:siteRemainingCA,
			siteAnnualRTT:siteAnnualRTT,
			siteAnnualCSS:siteAnnualCSS,
			siteAnnualINJUSTIFIE:siteAnnualINJUSTIFIE,
			siteAnnualSickness:siteAnnualSickness,
			siteAnnualDIF:siteAnnualDIF,
			siteAnnualExceptionnel:siteAnnualExceptionnel,
			siteAnnualPaternite:siteAnnualPaternite,
			siteAnnualPayableSupTime:siteAnnualPayableSupTime,
			siteAnnualTheoriticalIncludingExtra:siteAnnualTheoriticalIncludingExtra,
			siteAnnualSupTimeAboveTheoritical:siteAnnualSupTimeAboveTheoritical,
			siteAnnualGlobalSupTimeToPay:siteAnnualGlobalSupTimeToPay
		]
		
		return model
	}
}
