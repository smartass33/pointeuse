package pointeuse

class AbsenceCounter {
	Date logging_time
	Employee employee
	User user
	int year
	float counter
	static belongsTo = [employee:Employee]
	
	String toString(){
		return 'employee: lastname:'+ this.employee.lastName+' firstname:'+this.employee.firstName + ' counter:'+ this.counter + ' year:'+this.year
	}
}
