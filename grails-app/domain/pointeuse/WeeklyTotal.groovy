package pointeuse

class WeeklyTotal {

	static final float WeeklyLegalTime=35 //35H
	static final long maxWorkingTime=42*3600
	int month
	int year
	int week
	long elapsedSeconds
	long supplementarySeconds
	long complementarySeconds
	MonthlyTotal monthlyTotal
	Employee employee
	static hasMany = [dailyTotals:DailyTotal]
	static belongsTo = [MonthlyTotal]
	
	WeeklyTotal(Employee employee,Date currentDate){
		this.employee=employee
		this.year=currentDate.getAt(Calendar.YEAR)
		this.month=currentDate.getAt(Calendar.MONTH)+1
		this.week=currentDate.getAt(Calendar.WEEK_OF_YEAR)
		this.elapsedSeconds=0
		this.dailyTotals=[]
	}
	
    static constraints = {
    }
}
