package pointeuse

class Contract {

	Employee employee
	Period period
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
}
  