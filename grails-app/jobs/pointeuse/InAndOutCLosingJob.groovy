package pointeuse

import java.util.Date;
import pointeuse.Employee;
import java.util.Calendar
import org.apache.commons.logging.LogFactory


class InAndOutCLosingJob {
	private static final log = LogFactory.getLog(this)
	def timeManagerService
	static triggers = {
		//cron name: 'myTrigger', cronExpression: "0 0 21 * * ?"
		// 00:00 every day
	//	cron name: 'closingJobTrigger', cronExpression: "0 0 0 * * ?"
		cron name: 'closingJobTrigger', cronExpression: "0 45 5 * * ?"
		
	}
	def group = "MyGroup"


	def execute() {
		log.error("InAndOutCLosingJob run!")
		
		def inOrOut
		//trouver toutes les entrees du jour courant
		//trouver, pour chaque employe le plus recent
		//Si ce dernier est une Entree, rajouter une sortie
		// ajouter dans un nouveau champs de InAndOut que cette 'sortie' est generee automatiquement pour la resortir en erreur dans les rapprts.

		def employeeList = Employee.findAll("from Employee")
		def calendar = Calendar.instance
		calendar.clearTime()
		def lasDayCalendar = Calendar.instance
		
		if ( lasDayCalendar.get(Calendar.DAY_OF_YEAR) == lasDayCalendar.getActualMinimum(Calendar.DAY_OF_YEAR) ){
			lasDayCalendar.roll(Calendar.YEAR,-1)
			lasDayCalendar.set(Calendar.MONTH,11)
			lasDayCalendar.set(Calendar.DAY_OF_MONTH,lasDayCalendar.getActualMaximum(Calendar.DAY_OF_YEAR))
		}else{
			lasDayCalendar.roll(Calendar.DAY_OF_YEAR, -1)
		}
		
		lasDayCalendar.set(Calendar.HOUR_OF_DAY,23)
		lasDayCalendar.set(Calendar.MINUTE,59)
		lasDayCalendar.set(Calendar.SECOND,59)
		
		
		for (employee in employeeList){
			def lastIn = InAndOut.findByEmployee(employee,[max:1,sort:"time",order:"desc"])
			if (lastIn != null && lastIn.type == "E"){
				log.error("we have a problem: user "+employee.lastName +" did not log out")
				inOrOut = new InAndOut(employee, lasDayCalendar.time,"S",false)
				inOrOut.dailyTotal=lastIn.dailyTotal
				inOrOut.systemGenerated=true
				employee.inAndOuts.add(inOrOut)
				employee.hasError=true
				log.error("creating inOrOut: "+inOrOut)
				
				// cas special du travail de nuit
				// le salarié était entré et le systeme vient de lui creer une sortie à 23:59
				// il faut donc recreer une entrée à 00:00
				if (employee.hasNightJob){
					log.error "system creating entry for night job: user "+employee.lastName
					inOrOut = new InAndOut(employee, calendar.time,"E",false)
					def totals=initializeTotals(inOrOut.employee,calendar.time)
					def dailyTotal=totals.getAt(0)
					inOrOut.dailyTotal=dailyTotal					
					inOrOut.systemGenerated=true
					employee.inAndOuts.add(inOrOut)
					employee.hasError=true
					log.error ("creating inOrOut: "+inOrOut)
				}
			}
			
			def criteria = InAndOut.createCriteria()
			def inAndOutList = criteria.list {
				 or{
					 and {
						 eq('employee',employee)
						 eq('systemGenerated',true)
					 }
					and {
						eq('employee',employee)
						eq('regularizationType',InAndOut.INITIALE_SALARIE)						
					} 
				 }
			 }
			 if (inAndOutList != null && inAndOutList.size() > 0){
				 log.debug("there still "+inAndOutList.size() +" errors for employee "+employee.id + " : " +employee.lastName)
				 employee.hasError=true
			 }else {
			 	employee.hasError=false
			 }
			
		}
		if (calendar.get(Calendar.DAY_OF_YEAR) == calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
			calendar.roll(Calendar.YEAR,1)
			calendar.set(Calendar.DAY_OF_YEAR,1)
		}else{
			calendar.roll(Calendar.DAY_OF_YEAR,1)	
		}
		calendar.set(Calendar.HOUR_OF_DAY,23)
		calendar.set(Calendar.MINUTE,59)
		calendar.set(Calendar.SECOND,59)
		log.error('registring InAndOutCLosingJob at '+calendar.time)
		this.schedule(calendar.time)
	}
}
