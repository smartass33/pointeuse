package pointeuse

import groovy.time.TimeDuration
import java.util.Calendar;
import java.util.Date;

class TimeManagerService {

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
	
}
