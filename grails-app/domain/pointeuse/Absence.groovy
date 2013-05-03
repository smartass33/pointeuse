package pointeuse

import java.util.Calendar;

class Absence {

	Employee employee
	Date date
	int day
	int month
	int year
	AbsenceType type
	static belongsTo = [employee:Employee]
	
	
    static constraints = {
    }
}
