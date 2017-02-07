package pointeuse

import java.util.Date;

class Mileage {
	Employee employee
	Date loggingTime
	User user
	Period period
	int month
	int year
	long value
	int day

	static belongsTo = [employee:Employee]
	
	String toString(){
		return this.year + '/' + this.month +  '/' +this.day + '-' + +this.employee.lastName + '-' + this.value
	}
	
	static constraints = {
		month (unique:['employee','period','month','year','day'])
	}
	
	Mileage(Employee employee,Period period,def day,def month,def year, def value,User user){
		this.employee = employee
		this.period = period
		this.day = day
		this.month = month
		this.year = year
		this.value = value
		this.user = user
		this.loggingTime = new Date()
	}
}
