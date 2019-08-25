package pointeuse

class BankHoliday {

	int month
	int year
	int day
	int week
	Calendar calendar
	User user
	Date loggingDate
	
	static belongsTo = [period:Period]
	
    static constraints = {
		calendar(unique:['day','month','year','week'])
		user nullable: true
    }
}
