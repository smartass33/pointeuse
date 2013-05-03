package pointeuse

class MonthlyTotal {

	
	int month
	int year
	long elapsedSeconds
	long supplementarySeconds
	long complementarySeconds
	Employee employee
	static hasMany = [weeklyTotals:WeeklyTotal]
	static belongsTo = [employee:Employee]
	
	MonthlyTotal(Employee employee,Date currentDate){
		this.employee=employee
		this.year=currentDate.getAt(Calendar.YEAR)
		this.month=currentDate.getAt(Calendar.MONTH)+1
		this.elapsedSeconds=0
		this.weeklyTotals=[]
	}
	
    static constraints = {
    }
}
