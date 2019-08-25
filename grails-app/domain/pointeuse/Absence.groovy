package pointeuse

import java.util.Date;
import java.util.Calendar;

class Absence {
	Employee employee
	Date date
	int day
	int month
	int year
	AbsenceType type
	Period period

	static belongsTo = [employee:Employee]

	public Absence(){

	}

	public Absence(Employee employee, Date date, AbsenceType type) {
		this.employee = employee
		this.date = date
		this.day = date.getAt(Calendar.DAY_OF_MONTH)
		this.month = date.getAt(Calendar.MONTH) + 1
		this.year = date.getAt(Calendar.YEAR)
		this.type = type
		this.period = (this.month > 5) ? Period.findByYear(year) : Period.findByYear(year - 1)
	}

	String toString(){
		return 'employee: lastname:'+ this.employee.lastName+' firstname:'+this.employee.firstName + ' type:'+ this.type + ' day:'+this.day + ' month:'+this.month + ' year:'+this.year
	}
}
