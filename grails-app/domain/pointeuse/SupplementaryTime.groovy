package pointeuse

import java.util.Date;

class SupplementaryTime {
	Employee employee
	Date loggingTime
	Double value
	Period period
	int month
	Double amountPaid
	Date lastPayment
	static belongsTo = [employee: Employee]
	
    static constraints = {
		month (unique:['employee','period','month'])
		amountPaid (nullable:true)
		lastPayment(nullable:true)
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
		this.amountPaid = 0
	}
}
