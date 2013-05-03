package pointeuse

import java.util.Calendar


class InAndOutCLosingJob {
	static triggers = {
		// fire trigger every day of the month at 22H
		cron name: 'myTrigger', cronExpression: "0 0 21 * * ?"
	//	cron name: 'myTrigger', cronExpression: "* 1 * * * ?"

	}
	def group = "MyGroup"

	def sendEmail(Employee employee,InAndOut inOrOut){
		inOrOut
		int day
		int month
		
		sendMail{
		to "henri.martin@orange.com"
		from "henri.martin@gmail.com"
		subject "Erreur de badgage pour" +employee.firstName+" "+employee.lastName
		body "L'employé "+employee.firstName+" "+employee.lastName+" a oublié de badger sa sortie du "+inOrOut.day+" "+inOrOut.month""
		}
	}
	
	
	def execute() {
		log.error "Job run!"
		
		def inOrOut
		//trouver toutes les entrées du jour courant
		//trouver, pour chaque employé le plus récent
		//Si ce dernier est une Entrée, rajouter une sortie
		// ajouter dans un nouveau champs de InAndOut que cette 'sortie' est générée automatiquement pour la resortir en erreur dans les rapprts.

		def employeeList = Employee.findAll()
		def calendar = Calendar.instance
		def dailyTotal
		def criteria
		for (employee in employeeList){
			def lastIn = InAndOut.findByEmployee(employee,[max:1,sort:"time",order:"desc"])
			if (lastIn != null && lastIn.type == "E"){
				log.error "we have a problem: user "+employee.lastName +" did not log out"
				
				criteria = DailyTotal.createCriteria()
				dailyTotal = criteria.get {
					and {
						eq('employee',employee)
						eq('year',calendar.getAt(Calendar.YEAR))
						eq('month',calendar.getAt(Calendar.MONTH)+1)
						eq('day',calendar.getAt(Calendar.DAY_OF_MONTH))
					}
				}
				
				inOrOut = new InAndOut(employee, calendar.time,"S")
				inOrOut.dailyTotal=dailyTotal
				inOrOut.systemGenerated=true
				employee.inAndOuts.add(inOrOut)
				employee.status=false
				employee.hasError=true
				log.error "creating inOrOut: "+inOrOut
				
			}
		//	sendEmail(employee,inOrOut)
		}
		if (calendar.get(Calendar.DAY_OF_YEAR) == calendar.getActualMaximum(Calendar.DAY_OF_YEAR)){
			calendar.roll(Calendar.YEAR,1)
			calendar.set(Calendar.DAY_OF_YEAR,1)
		}else{
			calendar.roll(Calendar.DAY_OF_YEAR,1)	
			//calendar.roll(Calendar.MINUTE, 1)
		}
		calendar.set(Calendar.HOUR_OF_DAY,22)
		calendar.set(Calendar.MINUTE,0)
		log.error 'registring InAndOutCLosingJob at '+calendar.time
		
		this.schedule(calendar.time)
		
	}
	
}
