package pointeuse

import java.util.Calendar;

class Vacation {
	Employee employee
	Date loggingTime
	User user
	int counter
	VacationType type
	Period period
	
	//static belongsTo = [employee:Employee]
    static constraints = {
	//	period(unique:['type','employee','period'])
		
    }
}
