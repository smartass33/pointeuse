package pointeuse

class EmployeeService {

	
	
	public int getTakenCA(Employee employee,Period period){
		def criteria
		def takenCA
		def startCalendar = Calendar.instance
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.set(Calendar.MONTH,5)
		startCalendar.set(Calendar.HOUR_OF_DAY,00)
		startCalendar.set(Calendar.MINUTE,00)
		startCalendar.set(Calendar.SECOND,00)
		
		// ending calendar: 31 of May of the period
		def endCalendar   = Calendar.instance
		endCalendar.set(Calendar.DAY_OF_MONTH,31)
		endCalendar.set(Calendar.MONTH,6)
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
		
		startCalendar.set(Calendar.YEAR,period.year)
		endCalendar.set(Calendar.YEAR,period.year+1)
		
		
		//CA
		criteria = Absence.createCriteria()
		takenCA = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startCalendar.time)
				lt('date',endCalendar.time)
				eq('type',AbsenceType.VACANCE)
			}
		}
		return takenCA!=null?takenCA.size():0
	}
	
	
	public int getInitialCA(Employee employee,Period period){
		def criteria
		def initialCA
		criteria = Vacation.createCriteria()
		initialCA = criteria.get{
			and {
				eq('employee',employee)
				eq('period',period)
				eq('type',VacationType.CA)
			}
		}
		return initialCA!=null?initialCA.counter:0
	}
	
	public int getRemainingCA(Employee employee,Period period){
		def remainingCA = 0
		def criteria
		def takenCA
		def initialCA
		def startCalendar = Calendar.instance
		startCalendar.set(Calendar.DAY_OF_MONTH,1)
		startCalendar.set(Calendar.MONTH,5)
		startCalendar.set(Calendar.HOUR_OF_DAY,00)
		startCalendar.set(Calendar.MINUTE,00)
		startCalendar.set(Calendar.SECOND,00)
		
		// ending calendar: 31 of May of the period
		def endCalendar   = Calendar.instance
		endCalendar.set(Calendar.DAY_OF_MONTH,31)
		endCalendar.set(Calendar.MONTH,4)
		endCalendar.set(Calendar.HOUR_OF_DAY,23)
		endCalendar.set(Calendar.MINUTE,59)
		endCalendar.set(Calendar.SECOND,59)
		
		startCalendar.set(Calendar.YEAR,period.year)
		endCalendar.set(Calendar.YEAR,period.year+1)
		
		criteria = Vacation.createCriteria()
		initialCA = criteria.get{
			and {
				eq('employee',employee)
				eq('period',period)
				eq('type',VacationType.CA)
			}
		}		
		
		//CA
		criteria = Absence.createCriteria()
		takenCA = criteria.list {
			and {
				eq('employee',employee)
				ge('date',startCalendar.time)
				lt('date',endCalendar.time)
				eq('type',AbsenceType.VACANCE)
			}
		}		
		return remainingCA=takenCA!=null?(initialCA.counter - takenCA.size()):initialCA.counter
	}
	
}