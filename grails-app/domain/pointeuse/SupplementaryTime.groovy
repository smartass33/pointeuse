package pointeuse

import java.util.Date;

class SupplementaryTime {
	Employee employee
	Date loggingTime
	double value
	Period period
	int month
	double amountPaid
	Date lastPayment
	static belongsTo = [employee: Employee]
	
    static constraints = {
		month (unique:['employee','period'])
		amountPaid (blank: true,nullable:true)
		lastPayment(blank: true,nullable:true)
    }
	
	String toString(){
		return (this.period).toString() +' - month - ' + month + ' : ' +this.value
	}
	
	
	SupplementaryTime(Employee employee,Period period, int month,double value){
		this.employee = employee
		this.period = period
		this.month = month
		this.value = value
		this.loggingTime = new Date()
	}
}
