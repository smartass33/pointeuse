package pointeuse

class AbsenceCounter {

	Date logging_time
	Employee employee
	User user
	int year
	float counter
	static belongsTo = [employee:Employee]
	
    static constraints = {
    }
}
