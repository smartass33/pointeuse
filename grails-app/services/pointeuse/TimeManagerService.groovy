package pointeuse

import groovy.time.TimeDuration;
import groovy.time.TimeCategory


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

class TimeManagerService {

	def springSecurityService
	
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
		def criteria = DailyTotal.createCriteria()
		def dailyTotals = criteria.list {
			and {
				eq('employee',employee)
				eq('year',year)
				eq('week',week)
			}
		}
		criteria = WeeklyTotal.createCriteria()
		WeeklyTotal previousWeeklyTotal
		def dailyTotalSum=0
		for (DailyTotal tmpDaily:dailyTotals){
			def tmpElapsed = getDailyTotal(tmpDaily)
			dailyTotalSum += tmpElapsed
			dailySupplementarySeconds += Math.max(tmpElapsed-DailyTotal.maxWorkingTime, 0)
		}
		if (dailyTotalSum<=WeeklyTotal.maxWorkingTime){
			weeklySupplementarySeconds = dailySupplementarySeconds
		}else {
			weeklySupplementarySeconds = Math.max(dailySupplementarySeconds,dailyTotalSum-WeeklyTotal.maxWorkingTime)
		}
		return weeklySupplementarySeconds
	}


	
	def computeSupplementaryTime(DailyTotal dailyTotal){
		def criteria
		// compute dailySupTime
		if (dailyTotal.elapsedSeconds>DailyTotal.maxWorkingTime){
			dailyTotal.supplementarySeconds=dailyTotal.elapsedSeconds-DailyTotal.maxWorkingTime
		}else {
			dailyTotal.supplementarySeconds=0
		}

		// compute corresponding weeklySupTime
		def weeklyTotal = dailyTotal.weeklyTotal
		criteria = DailyTotal.createCriteria()
		def dailyTotals 

		if (dailyTotal.week==1){
			 dailyTotals = criteria.list {
				or{
					and {
						eq('employee',dailyTotal.employee)
						eq('year',dailyTotal.year)
						eq('week',dailyTotal.week)
					}
					and{
						eq('employee',dailyTotal.employee)
						eq('year',dailyTotal.year-1)
						eq('week',dailyTotal.week)
					}
				}
			}
		}else{
			dailyTotals = criteria.list {
				and {
					eq('employee',dailyTotal.employee)
					eq('year',dailyTotal.year)
					eq('week',dailyTotal.week)
				}
			}
		}


		criteria = WeeklyTotal.createCriteria()
		WeeklyTotal previousWeeklyTotal
		// add special case for first week of year:
		def nextWeek=false
		def dailyTotalSum=0
		def weeklyTotalmonth=dailyTotal.weeklyTotal.month
		for (DailyTotal tmpDaily:dailyTotals){
			def tmpMonth=tmpDaily.weeklyTotal.month
			dailyTotalSum += tmpDaily.supplementarySeconds
			if (dailyTotal.week==1){
				if (tmpMonth>weeklyTotalmonth){
					tmpDaily.weeklyTotal.supplementarySeconds=0
					previousWeeklyTotal=tmpDaily.weeklyTotal
				}
				if (tmpMonth<weeklyTotalmonth){
					weeklyTotal.supplementarySeconds=0
				}
			}else{
				if (tmpMonth<weeklyTotalmonth){
					tmpDaily.weeklyTotal.supplementarySeconds=0
					previousWeeklyTotal=tmpDaily.weeklyTotal
				}
				if (tmpMonth>weeklyTotalmonth){
					weeklyTotal.supplementarySeconds=0
					nextWeek=true
				}	
			}		
		}
		if (previousWeeklyTotal!=null){
			if ((weeklyTotal.elapsedSeconds+previousWeeklyTotal.elapsedSeconds) <= WeeklyTotal.maxWorkingTime){
				weeklyTotal.supplementarySeconds = dailyTotalSum
			}
			else {
				weeklyTotal.supplementarySeconds=Math.max(dailyTotalSum,((weeklyTotal.elapsedSeconds+previousWeeklyTotal.elapsedSeconds)-WeeklyTotal.maxWorkingTime))
			}
		}
		else{
			if (!nextWeek){
				if ((weeklyTotal.elapsedSeconds) <= WeeklyTotal.maxWorkingTime){
					weeklyTotal.supplementarySeconds = dailyTotalSum
				}
				else {
					weeklyTotal.supplementarySeconds=Math.max(dailyTotalSum,(weeklyTotal.elapsedSeconds-WeeklyTotal.maxWorkingTime))
				}
			}
		}
	}	

	/*
	def computeComplementaryTime(DailyTotal dailyTotal){
		def weeklyTotal = dailyTotal.weeklyTotal
		def sup = 0
		def HC = 0
		def HS = 0
		// calculer les HS et HC hebdo
		
		// ne peuvent pas exceder 1/3 du temps hebdo prŽvu au contrat:
		if (weeklyTotal.elapsedSeconds > 3600*dailyTotal.employee.weeklyContractTime){
			//on est en dessous du seuil des 10%: il n'y a que des HC
			if (weeklyTotal.elapsedSeconds < 4*3600*dailyTotal.employee.weeklyContractTime/3){
				HC = weeklyTotal.elapsedSeconds - 3600*dailyTotal.employee.weeklyContractTime
			}
			// on est au dessus: il faut comptabiliser HC et HS
			else{
				HC = 3600*dailyTotal.employee.weeklyContractTime/3
				HS = weeklyTotal.elapsedSeconds-4*3600*dailyTotal.employee.weeklyContractTime/3
			}
		}
		weeklyTotal.supplementarySeconds=HS
		weeklyTotal.complementarySeconds=HC
		
		// calculer les HS quotidiennes
		if (dailyTotal.elapsedSeconds > DailyTotal.maxWorkingTime){
			dailyTotal.supplementarySeconds = dailyTotal.elapsedSeconds-DailyTotal.maxWorkingTime
		}else {
			dailyTotal.supplementarySeconds = 0
		}
	}
	*/
	
	def initializeTotals(Employee employee, Date currentDate,String type,def event){
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
			def inOrOut = new InAndOut(employee, currentDate,type)
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
		def cal = Calendar.instance
		if (calendar != null){
			cal = calendar
		}
		def dailyTotal
		def currentDate = cal.time
		def employeeInstance = Employee.get(userId)
		def weeklyTotal
		def monthlyTotal
		def criteria
		def lastElement
		def nextElement
		def deltaTime
		def NIT
		def LIT
		def today = new GregorianCalendar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE)).getTime()
	
		// liste les entrees de la journŽe et vŽrifie que cette valeur n'est pas supŽrieure ˆ une valeur statique
		criteria = InAndOut.createCriteria()
		def todayEmployeeEntries = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',cal.get(Calendar.YEAR))
				eq('month',cal.get(Calendar.MONTH)+1)
				eq('day',cal.get(Calendar.DAY_OF_MONTH))
				eq('type','E')
			}
		}
		
		if (todayEmployeeEntries != null && todayEmployeeEntries.size() > Employee.entryPerDay){
			flash.message = "TROP D'ENTREES DANS LA JOURNEE. POINTAGE NON PRIS EN COMPTE"
			redirect(action: "show", id: employeeInstance.id)
			return
		}
		criteria = DailyTotal.createCriteria()
		dailyTotal = criteria.get {
			and {
				eq('employee',employeeInstance)
				eq('year',cal.get(Calendar.YEAR))
				eq('month',cal.get(Calendar.MONTH)+1)
				eq('day',cal.get(Calendar.DAY_OF_MONTH))
			}
		}

		// initialisation
		if (dailyTotal == null) {
			initializeTotals(employeeInstance , cal.time , type,null)
		}else{
			criteria = InAndOut.createCriteria()
			lastElement = criteria.get {
				and {
					eq('employee',employeeInstance)
					eq('day',today.getAt(Calendar.DATE))
					eq('month',today.getAt(Calendar.MONTH)+1)
					eq('year',today.getAt(Calendar.YEAR))
					lt('time',inOrOut.time)
					order('time','desc')
				}
				maxResults(1)
			}
			
			// check if there is a next out
			criteria = InAndOut.createCriteria()
			nextElement = criteria.get {
				and {
					eq('employee',employeeInstance)
					eq('day',today.getAt(Calendar.DATE))
					eq('month',today.getAt(Calendar.MONTH)+1)
					eq('year',today.getAt(Calendar.YEAR))
					gt('time',inOrOut.time)
					order('time','asc')
				}
				maxResults(1)
			}
			
			deltaTime=new TimeDuration( 0, 0, 0, 0) 
		
			// l'employee effectue une sortie: il faut calculer le temps passŽ depuis la derniere entrŽe
			if (type.equals("S")){
				if (lastElement != null && lastElement.type.equals('E')){
					LIT = lastElement.time
					use (TimeCategory){deltaTime=inOrOut.time-LIT}
				}
			}
			// il y a eu une entrŽe: il faut vŽrifier par rapport au temps dŽjˆ dŽcomptŽ
			// c'est un cas spŽcial qui a lieu lors d'une rŽgularisation
			else {
				if (nextElement != null && nextElement.type.equals('S')){
					if (lastElement!=null){
						LIT = lastElement.time
						use (TimeCategory){deltaTime=LIT - inOrOut.time}
					}else{
						use (TimeCategory){deltaTime=nextElement.time - inOrOut.time}
					
					}
				}
			}	

			dailyTotal.elapsedSeconds += deltaTime.seconds+deltaTime.minutes*60+deltaTime.hours*3600
			
			if (dailyTotal.weeklyTotal != null){
				dailyTotal.weeklyTotal.elapsedSeconds += deltaTime.seconds+deltaTime.minutes*60+deltaTime.hours*3600
				dailyTotal.weeklyTotal.dailyTotals.add(dailyTotal)
				//if (employeeInstance.weeklyContractTime != 35){
				//	computeComplementaryTime(dailyTotal)
				//}else{
					computeSupplementaryTime(dailyTotal)
				//}
			}
			if (dailyTotal.weeklyTotal.monthlyTotal != null){
				dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds += deltaTime.seconds+deltaTime.minutes*60+deltaTime.hours*3600
				dailyTotal.weeklyTotal.monthlyTotal.weeklyTotals.add(weeklyTotal)
			}														
			employeeInstance.inAndOuts.add(inOrOut)
			dailyTotal.exitCount=dailyTotal.exitCount+1
			// the new input is the last one. return the 
			if (lastElement != null && nextElement != null){
				use (TimeCategory){deltaTime=nextElement.time-lastElement.time}
			}else{
				if (lastElement != null){
					use (TimeCategory){deltaTime=inOrOut.time-lastElement.time}
					
				}
				if (nextElement != null){
					use (TimeCategory){deltaTime=nextElement.time-inOrOut.time}
				}
			}
		}
		criteria = Absence.createCriteria()
		def absence = criteria.get {
			and {
				eq('employee',employeeInstance)
				eq('day',today.getAt(Calendar.DATE))
				eq('month',today.getAt(Calendar.MONTH)+1)
				eq('year',today.getAt(Calendar.YEAR))
			}
		}
		if (absence!=null){
			absence.delete()
		}
		if (type.equals("S")){employeeInstance.status=false}
		else {employeeInstance.status=true}
		return deltaTime
	}
		
	
	
	def regularizeTimeNew(String type,String userId,InAndOut inOrOut,Calendar calendar){
		if (calendar == null){
			calendar = Calendar.instance
		}
		def dailyTotal
		def currentDate = calendar.time
		def employeeInstance = Employee.get(userId)
		def criteria
	
		criteria = InAndOut.createCriteria()
		def todayEmployeeEntries = criteria.list {
			and {
				eq('employee',employeeInstance)
				eq('year',calendar.get(Calendar.YEAR))
				eq('month',calendar.get(Calendar.MONTH)+1)
				eq('day',calendar.get(Calendar.DAY_OF_MONTH))
				eq('type','E')
			}
		}
		
		if (todayEmployeeEntries != null && todayEmployeeEntries.size() > Employee.entryPerDay){
			flash.message = "TROP D'ENTREES DANS LA JOURNEE. POINTAGE NON PRIS EN COMPTE"
			redirect(action: "show", id: employeeInstance.id)
			return
		}
		//dailyTotal = recomputeDailyTotals(employeeInstance.id as int,calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR))
		employeeInstance.inAndOuts.add(inOrOut)
		//dailyTotal.exitCount=dailyTotal.exitCount+1
		
		criteria = Absence.createCriteria()
		def absence = criteria.get {
			and {
				eq('employee',employeeInstance)
				eq('day',calendar.get(Calendar.DATE))
				eq('month',calendar.get(Calendar.MONTH)+1)
				eq('year',calendar.get(Calendar.YEAR))
			}
		}
		if (absence!=null){
			absence.delete()
		}
		employeeInstance.status=type.equals("S")?false:true
	}
		
	def timeModification(def idList,def timeList,def dayList,def monthList, def yearList,Employee employee,def newTimeList,def fromRegularize) throws PointeuseException{
		def deltaUp
		def deltaDown
		def timeDiff
		def criteria
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
		// l'idŽe: comparer time et newTime
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
				
				
				def toCompare
				if (newCalendar.time>oldCalendar.time){
					toCompare=newCalendar.time
				}else{
					toCompare=oldCalendar.time
				}
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
				
				
				if (newCalendar.time>oldCalendar.time){
					toCompare=oldCalendar.time
				}else{
					toCompare=newCalendar.time
				}
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
				
				if (previousInOrOut != null){
					use (TimeCategory){deltaDown=newCalendar.time-previousInOrOut.time}
				}

				
				if (nextInOrOut != null){
					use (TimeCategory){deltaUp=nextInOrOut.time-newCalendar.time}
				}

				inOrOut.time=newCalendar.time
				
				// if the time difference
				if (inOrOut.systemGenerated && previousInOrOut!=null ){
						use (TimeCategory){timeDiff=newCalendar.time-previousInOrOut.time}
				}
				if (inOrOut.regularization && nextInOrOut !=null && nextInOrOut.systemGenerated){
					use (TimeCategory){timeDiff=nextInOrOut.time - newCalendar.time}
				}
				def doNothing=false
				if (inOrOut.type.equals('E') && nextInOrOut !=null && nextInOrOut.type.equals('E')){
					doNothing=true
				}
				if (inOrOut.type.equals('S') && previousInOrOut !=null && previousInOrOut.type.equals('S')){
					doNothing=true
				}
				if(!doNothing){
					dailyTotalUpdate(timeDiff,newCalendar.time,oldCalendar.time,inOrOut,employee,dayList[p] as int,month as int)
				}
				//if (employee.weeklyContractTime != 35){
				//	computeComplementaryTime(inOrOut.dailyTotal)
				//}else{
					computeSupplementaryTime(inOrOut.dailyTotal)
				//}
				
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
	
	def dailyTotalUpdate(TimeDuration timeDiff,Date newDate ,Date oldDate,InAndOut inOrOut,Employee employee,int day, int month){
		if (inOrOut.dailyTotal.elapsedSeconds<0){
			log.error('the daily Total is negative ' + inOrOut.dailyTotal)
			//dailyTotal.elapsedSeconds=0
		}
		if (timeDiff == null){
			use (TimeCategory){timeDiff=newDate-oldDate}
			if (inOrOut.type == "E"){
				inOrOut.dailyTotal.elapsedSeconds-=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
				inOrOut.dailyTotal.weeklyTotal.elapsedSeconds-=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
				inOrOut.dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds-=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
			} else {
				inOrOut.dailyTotal.elapsedSeconds+=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
				inOrOut.dailyTotal.weeklyTotal.elapsedSeconds+=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
				inOrOut.dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds+=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
			}
		}else{
			inOrOut.dailyTotal.elapsedSeconds+=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
			inOrOut.dailyTotal.weeklyTotal.elapsedSeconds+=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
			inOrOut.dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds+=(timeDiff.hours*3600+timeDiff.minutes*60+timeDiff.seconds)
		}
		return inOrOut.dailyTotal
	}
	
	def computeMonthlyHours(int year,int month){
		def openedDays = 0
		def calendar = Calendar.instance
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.MONTH,month)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		
		// discard sundays from opened days
		while(calendar.getAt(Calendar.DAY_OF_MONTH) <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			if (calendar.getAt(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
				openedDays += 1
			}
			
			if (calendar.getAt(Calendar.DAY_OF_MONTH) == calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
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
/*	
	def computeComplementaryTime(int month,int year,Employee employee,int monthTheoritical){	
		Calendar calendar = Calendar.instance
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		calendar.set(Calendar.DATE,1)
		calendar.set(Calendar.DAY_OF_MONTH,1)
		calendar.set(Calendar.YEAR,year)
		calendar.set(Calendar.MONTH,month-1)
		def calendarLoop = calendar
		calendarLoop.getTime().clearTime()
		def monthlyCompTime = 0
		def monthlySupTime = 0
		def dailyTotalId = 0
		
		def criteria
		
		criteria = MonthlyTotal.createCriteria()
		def monthlyTotal = criteria.get{
			and {
					eq('employee',employee)
					eq('month',month)
					eq('year',year)
				}
		}
		while(calendarLoop.get(Calendar.DAY_OF_MONTH) <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			// Žlimine les dimanches du rapport

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
			// permet de rŽcupŽrer le total hebdo
			if (dailyTotal != null && dailyTotal != dailyTotalId && dailyTotal.weeklyTotal.elapsedSeconds > 0){
				if (dailyTotal.weeklyTotal.elapsedSeconds > WeeklyTotal.maxWorkingTime){
					monthlySupTime+=dailyTotal.weeklyTotal.supplementarySeconds
				}else {
					monthlySupTime+=dailyTotal.supplementarySeconds
				}
				dailyTotalId=dailyTotal.id
			}
			if (calendarLoop.getAt(Calendar.DAY_OF_MONTH)==calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendarLoop.roll(Calendar.DAY_OF_MONTH, 1)
		}	
		
		if (employee.weeklyContractTime != 35){
			
			if (monthlyTotal!=null && monthlyTotal.elapsedSeconds > monthTheoritical){
				monthlyCompTime = monthlyTotal.elapsedSeconds-monthTheoritical-monthlySupTime
			}
		}		
		return [monthlySupTime,monthlyCompTime]
	}
	
	
*/
	
	def calculateSupAndComp(Calendar calendar,Employee employee, int monthTheoritical){
		def criteria
		def calendarLoop = calendar
		calendarLoop.set(Calendar.HOUR_OF_DAY,23)
		calendarLoop.set(Calendar.MINUTE,59)
		calendarLoop.set(Calendar.SECOND,59)
		calendarLoop.set(Calendar.DATE,1)
		calendarLoop.getTime().clearTime()
		int month = calendarLoop.get(Calendar.MONTH)+1 
		int year = calendarLoop.get(Calendar.YEAR)
		
		def dailyTotalId=0
		def monthlySupTime=0
		def weeklySuppTotalTime=[:]
		def payableCompTime=0
		def payableSupTime=0
		def monthlyCompTime=0
		
		while(calendarLoop.get(Calendar.DAY_OF_MONTH) <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
			def currentDay=calendarLoop.time
			// Žlimine les dimanches du rapport

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
			// permet de rŽcupŽrer le total hebdo
			if (dailyTotal != null && dailyTotal != dailyTotalId && dailyTotal.weeklyTotal.elapsedSeconds > 0){
				
				
				////////////
				// compute corresponding weeklySupTime
				if (dailyTotal.elapsedSeconds>DailyTotal.maxWorkingTime){
					dailyTotal.supplementarySeconds=dailyTotal.elapsedSeconds-DailyTotal.maxWorkingTime
				}else {
					dailyTotal.supplementarySeconds=0
				}
				
				def weeklyTotal = dailyTotal.weeklyTotal
				criteria = DailyTotal.createCriteria()
				def dailyTotals
		
				if (dailyTotal.week==1){
					 dailyTotals = criteria.list {
						or{
							and {
								eq('employee',dailyTotal.employee)
								eq('year',dailyTotal.year)
								eq('week',dailyTotal.week)
							}
							and{
								eq('employee',dailyTotal.employee)
								eq('year',dailyTotal.year-1)
								eq('week',dailyTotal.week)
							}
						}
					}
				}else{
					dailyTotals = criteria.list {
						and {
							eq('employee',dailyTotal.employee)
							eq('year',dailyTotal.year)
							eq('week',dailyTotal.week)
						}
					}
				}
				criteria = WeeklyTotal.createCriteria()
				WeeklyTotal previousWeeklyTotal
				boolean nextMonth=false
				// add special case for first week of year:
		
				def dailyTotalSum=0
				def weeklyTotalmonth=dailyTotal.weeklyTotal.month
				for (DailyTotal tmpDaily:dailyTotals){
					def tmpMonth=tmpDaily.weeklyTotal.month
					dailyTotalSum += tmpDaily.supplementarySeconds
					if (dailyTotal.week==1){
						if (tmpMonth>weeklyTotalmonth){
							tmpDaily.weeklyTotal.supplementarySeconds=0
							previousWeeklyTotal=tmpDaily.weeklyTotal
						}
						if (tmpMonth<weeklyTotalmonth){
							weeklyTotal.supplementarySeconds=0
							nextMonth=true
						}
					}else{
						if (tmpMonth<weeklyTotalmonth){
							tmpDaily.weeklyTotal.supplementarySeconds=0
							previousWeeklyTotal=tmpDaily.weeklyTotal
						}
						if (tmpMonth>weeklyTotalmonth){
							weeklyTotal.supplementarySeconds=0
							nextMonth=true
							
						}
					}
				}
				if (previousWeeklyTotal!=null){
					if ((weeklyTotal.elapsedSeconds+previousWeeklyTotal.elapsedSeconds) <= WeeklyTotal.maxWorkingTime){
						weeklyTotal.supplementarySeconds = dailyTotalSum
					}
					else {
						weeklyTotal.supplementarySeconds=Math.max(dailyTotalSum,((weeklyTotal.elapsedSeconds+previousWeeklyTotal.elapsedSeconds)-WeeklyTotal.maxWorkingTime))
					}
				}
				else{
					if (!nextMonth){
						if ((weeklyTotal.elapsedSeconds) <= WeeklyTotal.maxWorkingTime){
							weeklyTotal.supplementarySeconds = dailyTotalSum
						}
						else {
							weeklyTotal.supplementarySeconds=Math.max(dailyTotalSum,(weeklyTotal.elapsedSeconds-WeeklyTotal.maxWorkingTime))
						}
					}
				}
						
				////////////
				//computeSupplementaryTime(dailyTotal)
				weeklySuppTotalTime.put(calendarLoop.get(Calendar.WEEK_OF_YEAR),weeklyTotal.supplementarySeconds)
				
				//monthlySupTime += weeklyTotal.supplementarySeconds
				dailyTotalId=dailyTotal.id
			}

			if (calendarLoop.get(Calendar.DAY_OF_MONTH)==calendar.getActualMaximum(Calendar.DAY_OF_MONTH)){
				break
			}
			calendarLoop.roll(Calendar.DAY_OF_MONTH, 1)
		}

	
		criteria = MonthlyTotal.createCriteria()
		def monthlyTotal = criteria.get{
			and {
					eq('employee',employee)
					eq('month',month)
					eq('year',year)
				}
		}
		
		weeklySuppTotalTime.each() { key, value ->
			monthlySupTime += value
		};
	
		if (employee.weeklyContractTime!=35){
			if (monthlyTotal!=null && monthlyTotal.elapsedSeconds > monthTheoritical){
				monthlyCompTime = Math.max(monthlyTotal.elapsedSeconds-monthTheoritical-monthlySupTime,0)
			}
		}

		return [monthlySupTime,monthlyCompTime]
		
	}
	
	

	
	
	def recomputeDailyTotals(int userId,int month,int year){
		Employee employee = Employee.get(userId)
		
		def tmpInOrOut
		def dailyDelta=0
		def timeDiff
		def criteria = DailyTotal.createCriteria()
		def dailyTotalList = criteria.list {
				and {
					eq('employee',employee)
					eq('year',year)
					eq('month',month)
				}
			}
		
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
	
}
