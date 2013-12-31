package pointeuse

import java.util.Calendar;

class Contract {

	Employee employee
	Date date
	float weeklyLength
	static belongsTo = [employee:Employee]
	
    static constraints = {
    }
	
	String toString(){
		return 'contract: '+ this.employee.lastName+' '+this.date+' '+this.weeklyLength
	}
}
