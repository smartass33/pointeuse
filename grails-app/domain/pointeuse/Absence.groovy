package pointeuse


class Absence {

	Employee employee
	Date date
	int day
	int month
	int year
	AbsenceType type
	static belongsTo = [employee:Employee]
	Period period
}
