package pointeuse

class WeeklyCase {
	//int month
	int year
	int week
	int value
	Date loggingDate
	Site site
	static belongsTo = [Site]
	WeeklyCase(int value,Date currentDate,def site){
		this.year=currentDate.getAt(Calendar.YEAR)
		//this.month=currentDate.getAt(Calendar.MONTH + 1)
		this.week=currentDate.getAt(Calendar.WEEK_OF_YEAR)
		this.value=value
		this.site=site
		this.loggingDate=new Date()
	}
}
