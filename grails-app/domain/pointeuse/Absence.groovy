package pointeuse


class Absence {
	Employee employee
	Date date
	int day
	int month
	int year
	AbsenceType type
	Period period
	
	static belongsTo = [employee:Employee]
	
	String toString(){
		return 'employee: lastname:'+ this.employee.lastName+' firstname:'+this.employee.firstName + ' type:'+ this.type+' period:'+ this.period + ' day:'+this.day + ' month:'+this.month + ' year:'+this.year
	}
}
