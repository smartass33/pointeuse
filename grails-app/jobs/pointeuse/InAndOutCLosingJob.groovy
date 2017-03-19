package pointeuse

import java.util.Calendar
import org.apache.commons.logging.LogFactory


class InAndOutCLosingJob {
	private static final log = LogFactory.getLog(this)
	
	static triggers = {
		// fire trigger every day of the month at 22H
		cron name: 'myTrigger', cronExpression: "0 0 21 * * ?"
	//	cron name: 'myTrigger', cronExpression: "* 1 * * * ?"

	}
	def group = "MyGroup"


	def execute() {
		log.error "Job run!"
		
		def inOrOut
		//trouver toutes les entrees du jour courant
		//trouver, pour chaque employe le plus recent
		//Si ce dernier est une Entree, rajouter une sortie
		// ajouter dans un nouveau champs de InAndOut que cette 'sortie' est generee automatiquement pour la resortir en erreur dans les rapprts.

		def employeeList = Employee.findAll("from Employee")
		def calendar = Calendar.instance
		for (employee in employeeList){
			def lastIn = InAndOut.findByEmployee(employee,[max:1,sort:"time",order:"desc"])
			if (lastIn != null && lastIn.type == "E"){
				log.error "we have a problem: user "+employee.lastName +" did not log out"
				inOrOut = new InAndOut(employee, calendar.time,"S",false)
				inOrOut.dailyTotal=lastIn.dailyTotal
				inOrOut.systemGenerated=true
				employee.inAndOuts.add(inOrOut)
				//employee.status=false
				employee.hasError=true
				log.error "creating inOrOut: "+inOrOut
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
			 if (inAndOutList != null && inAndOutList.size()>0){
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
		calendar.set(Calendar.HOUR_OF_DAY,22)
		calendar.set(Calendar.MINUTE,0)
		log.error 'registring InAndOutCLosingJob at '+calendar.time
		
		this.schedule(calendar.time)
		
	}
	
}
