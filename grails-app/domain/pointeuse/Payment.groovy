package pointeuse

import java.util.Date;

class Payment {
	Employee employee
	Date loggingTime
	Period period
	int month
	Double amountPaid
	
	static belongsTo = [employee: Employee]
    static constraints = {
		amountPaid (unique:['employee','period','month'])
		amountPaid (nullable:true)
    }
	
	
	Payment(Employee employee,Period period, int month,double amountPaid){
		this.employee = employee
		this.period = period
		this.month = month
		this.amountPaid = amountPaid
		this.loggingTime = new Date()
	}
}
