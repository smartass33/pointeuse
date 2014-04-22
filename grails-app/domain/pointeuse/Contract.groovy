package pointeuse

class Contract {

	Employee employee
	//Period period
	Date startDate
	Date endDate
	int year
	int month
	float weeklyLength
	//boolean currentContract
	Date loggingTime
	//Date lowerBoundDate
	//Date uperBoundDate
	static belongsTo = [employee:Employee]
	
    static constraints = {
		endDate(blank:true,nullable:true)
		
    }
	
	String toString(){
		return 'contract: '+ this.employee.lastName+' '+this.startDate+' '+this.endDate+' '+this.weeklyLength
	}
	
	
	public Contract(Date startDate,Employee employee) {
		this.startDate = startDate
		this.year=startDate.getAt(Calendar.YEAR)
		this.month=(startDate.getAt(Calendar.MONTH))+1
		this.weeklyLength = employee.weeklyContractTime
		this.employee = employee
		this.loggingTime = new Date()
   }
}
  