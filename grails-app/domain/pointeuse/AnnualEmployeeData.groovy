package pointeuse

import java.util.Date;


class AnnualEmployeeData {
	Employee employee
	Period period
	Map<String, Long> valueMap 
	def creationDate
	static belongsTo = [employee:Employee]
	
    static constraints = {
    }
	
	String toString(){
		return this.employee +'-'+this.period
	}
	
	AnnualEmployeeData(Employee employee,Period period){
		this.employee = employee
		this.period = period
		this.creationDate = new Date()
		this.valueMap = [:]	
	}
}
