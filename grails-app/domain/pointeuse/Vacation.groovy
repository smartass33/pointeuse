package pointeuse

import java.util.Calendar;

class Vacation {
	Employee employee
	Date loggingTime
	User user
	//int period // starting period: 2013 if 2013/2014 for instance
	int counter
	VacationType type
	Year year
	
	static belongsTo = [employee:Employee]
    static constraints = {
		year(unique:['type','employee'])
		
    }
}