package pointeuse

class Contract {

	Employee employee
	Date startDate
	Date endDate
	int year
	int month
	float weeklyLength
	Date loggingTime

	static belongsTo = [employee:Employee]
	
    static constraints = {
		endDate(nullable:true)
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
  