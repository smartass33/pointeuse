package pointeuse

class Contract {

	Employee employee
	Period period
	Date date
	int year
	int month
	float weeklyLength
	static belongsTo = [employee:Employee]
	
    static constraints = {
    }
	
	String toString(){
		return 'contract: '+ this.employee.lastName+' '+this.date+' '+this.weeklyLength
	}
}
  