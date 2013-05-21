package pointeuse

class BankHoliday {

	int month
	int year
	int day
	Calendar calendar
	User user
	Date loggingDate
    static constraints = {
		calendar(unique:['day','month','year'])
    }
}
