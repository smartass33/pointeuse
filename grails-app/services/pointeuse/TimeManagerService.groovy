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
	
	def computeSupplementaryTime(DailyTotal dailyTotal){
		def weeklyTotal = dailyTotal.weeklyTotal
		if (weeklyTotal.elapsedSeconds > WeeklyTotal.maxWorkingTime){
			weeklyTotal.supplementarySeconds = weeklyTotal.elapsedSeconds-WeeklyTotal.maxWorkingTime		
		}else {
			weeklyTotal.supplementarySeconds = 0
		}
		//else 
		if (dailyTotal.elapsedSeconds > DailyTotal.maxWorkingTime){
			dailyTotal.supplementarySeconds = dailyTotal.elapsedSeconds-DailyTotal.maxWorkingTime
		}else {
			dailyTotal.supplementarySeconds =0
		}
	}
	
	def computeComplementaryTime(DailyTotal dailyTotal){
		def weeklyTotal = dailyTotal.weeklyTotal
		def sup = 0
		def HC = 0
		def HS = 0
		// calculer les HS et HC hebdo
		
		// ne peuvent pas exceder 1/3 du temps hebdo prévu au contrat:
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
	
		// liste les entrees de la journée et vérifie que cette valeur n'est pas supérieure à une valeur statique
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
		
			// l'employee effectue une sortie: il faut calculer le temps passé depuis la derniere entrée
			if (type.equals("S")){
				if (lastElement != null && lastElement.type.equals('E')){
					LIT = lastElement.time
					use (TimeCategory){deltaTime=inOrOut.time-LIT}
				}
			}
			// il y a eu une entrée: il faut vérifier par rapport au temps déjà décompté
			// c'est un cas spécial qui a lieu lors d'une régularisation
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
				if (employeeInstance.weeklyContractTime != 35){
					computeComplementaryTime(dailyTotal)
				}else{
					computeSupplementaryTime(dailyTotal)
				}
			}
			if (dailyTotal.weeklyTotal.monthlyTotal != null){
				dailyTotal.weeklyTotal.monthlyTotal.elapsedSeconds += deltaTime.seconds+deltaTime.minutes*60+deltaTime.hours*3600
				dailyTotal.weeklyTotal.monthlyTotal.weeklyTotals.add(weeklyTotal)
			}														
			employeeInstance.inAndOuts.add(inOrOut)
			//dailyTotal.inAndOuts.add(inOrOut)
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
		if (type.equals("S")){employeeInstance.status=false}
		else {employeeInstance.status=true}
		return deltaTime
	}
		
		
	def timeModification(def idList,def timeList,def dayList,def monthList, def yearList,Employee employee,def newTimeList,def fromRegularize){
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
		// l'idée: comparer time et newTime
			def inOrOut = InAndOut.get(idList[p])
			if (inOrOut==null){
				log.error("InAndOut with id= "+idList[p]+" cannot be found. exiting timeModification§")
				def retour = report(employee.id as long,monthList[p] as int,yearList[p] as int)
				render(view: "report", model: retour)
				return
			}
			
			SimpleDateFormat dateFormat = new SimpleDateFormat('d/M/yyyy H:m');
			//Date newDate = dateFormat.parse(dayList[p]+"/"+monthList[p]+"/"+yearList[p]+" "+newTimeList[p])
			def newCalendar = Calendar.instance
			newCalendar.time=dateFormat.parse(dayList[p]+"/"+monthList[p]+"/"+yearList[p]+" "+newTimeList[p])
			def oldCalendar = Calendar.instance
			oldCalendar.time=inOrOut.time
			newCalendar.set(Calendar.SECOND,oldCalendar.get(Calendar.SECOND))
			
			if (newCalendar.time!=oldCalendar.time){
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
						eq('day',calendar.getAt(Calendar.DAY_OF_MONTH))
						eq('month',calendar.getAt(Calendar.MONTH)+1)
						eq('year',calendar.getAt(Calendar.YEAR))
						lt('time',toCompare)
						lt('id',inOrOut.id)
						order('time','desc')
					}
					maxResults(1)
				}
			
				if (previousInOrOut != null){
					use (TimeCategory){deltaDown=newCalendar.time-previousInOrOut.time}
				}
				else {
					deltaDown=new Date()
					deltaDown.minutes=0
				}
				
				if (nextInOrOut != null){
					use (TimeCategory){deltaUp=nextInOrOut.time-newCalendar.time}
				}
				else {
					deltaUp=new Date()
					deltaUp.minutes=0
				}
				
				if (deltaUp.minutes < 0 || deltaDown.minutes < 0 || deltaDown.hours < 0 || deltaUp.hours < 0){
					throw new PointeuseException("entry cannot be positionned after exit")

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
				if (employee.weeklyContractTime != 35){
					computeComplementaryTime(inOrOut.dailyTotal)
				}else{
					computeSupplementaryTime(inOrOut.dailyTotal)
				}
				
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
	
}
